/**
  * Licensed to the Apache Software Foundation (ASF) under one or more
  * contributor license agreements.  See the NOTICE file distributed with
  * this work for additional information regarding copyright ownership.
  * The ASF licenses this file to You under the Apache License, Version 2.0
  * (the "License"); you may not use this file except in compliance with
  * the License.  You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
package io.tokenanalyst.bitcoinrpc

import cats.effect.{ContextShift, IO, Resource}
import com.typesafe.scalalogging.LazyLogging
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.{Decoder, Encoder, Json}
import io.tokenanalyst.bitcoinrpc.Protocol._
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.dsl.io._
import org.http4s.headers.{Authorization, _}
import org.http4s.{BasicCredentials, MediaType, Request, Uri}

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext

case class Config(host: String,
                  user: String,
                  password: String,
                  port: Option[Int] = None,
                  zmqPort: Option[Int] = None)

object BitcoinRPC extends Http4sClientDsl[IO] with Calls with LazyLogging {

  def openAll()(
    implicit config: Config,
    ec: ExecutionContext,
    cs: ContextShift[IO]
  ): Resource[IO, (Client[IO], ZeroMQ.Socket)] =
    for {
      client <- BlazeClientBuilder[IO](ec).resource
      socket <- ZeroMQ.socket(config.host, config.zmqPort.getOrElse(28332))
    } yield (client, socket)

  def request[A <: RPCRequest: Encoder, B <: RPCResponse: Decoder](
    client: Client[IO],
    request: A
  )(implicit config: Config): IO[B] =
    for {
      req <- post(request)
      res <- client.expect[B](req)
    } yield res

  def requestJson[A <: RPCRequest: Encoder](client: Client[IO], request: A)(
    implicit config: Config
  ): IO[Json] =
    for {
      req <- post(request)
      res <- client.expect[Json](req)
    } yield res

  private def post[A <: RPCRequest: Encoder](
    request: A
  )(implicit config: Config): IO[Request[IO]] = {
    (for {
      url <- Uri.fromString(s"http://${config.host}:8332")
      p <- Right(
        POST(
          request,
          url,
          Authorization(
            BasicCredentials
              .apply(config.user, config.password)
          ),
          Accept(MediaType.application.json)
        )
      )
      _ <- Right(logger.debug(request.asJson.toString()))
    } yield p)
      .getOrElse(throw new Exception("No proper exception handling yet"))
  }
}

trait Calls {
  import RPCDecoders._
  import RPCEncoders._

  def getBlock(client: Client[IO],
               hash: String)(implicit config: Config): IO[BlockResponse] = {
    BitcoinRPC.request[BlockRequest, BlockResponse](client, BlockRequest(hash))
  }

  def getBlock(client: Client[IO],
               height: Long)(implicit config: Config): IO[BlockResponse] =
    for {
      hash <- getBlockHash(client, height)
      data <- getBlock(client, hash)
    } yield data

  def getBlockHash(client: Client[IO],
                   height: Long)(implicit config: Config): IO[String] =
    for {
      json <- BitcoinRPC
        .requestJson[BlockHashRequest](client, BlockHashRequest(height))
    } yield json.asObject.get("result").get.asString.get

  def getBestBlockHash(
    client: Client[IO]
  )(implicit config: Config): IO[String] =
    for {
      json <- BitcoinRPC
        .requestJson[BestBlockHashRequest](client, new BestBlockHashRequest)
    } yield json.asObject.get("result").get.asString.get

  def getBestBlockHeight(
    client: Client[IO]
  )(implicit config: Config): IO[Long] =
    for {
      hash <- getBestBlockHash(client)
      block <- getBlock(client, hash)
    } yield block.height

  def getTransactions(client: Client[IO], hashes: Seq[String])(
    implicit config: Config
  ): IO[BatchResponse[TransactionResponse]] = {
    val list = ListBuffer(hashes: _*)
    val genesisTransactionIndex =
      list.indexOf(Transactions.GenesisTransactionHash)

    if (genesisTransactionIndex >= 0) {
      list.remove(genesisTransactionIndex)
    }

    val result =
      BitcoinRPC.request[BatchRequest[TransactionRequest], BatchResponse[
        TransactionResponse
      ]](
        client,
        BatchRequest[TransactionRequest](list.map(TransactionRequest.apply))
      )

    if (genesisTransactionIndex >= 0) {
      for {
        batcResponse <- result
        listResult <- IO(ListBuffer(batcResponse.seq: _*))
        _ <- IO(
          listResult
            .insert(genesisTransactionIndex, Transactions.GenesisTransaction)
        )
      } yield BatchResponse(listResult)
    } else {
      result
    }
  }

  def getTransaction(client: Client[IO], hash: String)(
    implicit config: Config
  ): IO[TransactionResponse] =
    if (hash != Transactions.GenesisTransactionHash) {
      BitcoinRPC.request[TransactionRequest, TransactionResponse](
        client,
        TransactionRequest(hash)
      )
    } else {
      IO(Transactions.GenesisTransaction)
    }
}
