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

import org.http4s.circe.CirceEntityDecoder._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.dsl.io._
import org.http4s.headers.{Authorization, _}
import org.http4s.Uri
import org.http4s.{BasicCredentials, MediaType, Request}

import io.circe.{Decoder, Encoder, Json}
import io.circe.syntax._
import io.circe.generic.auto._

import cats.effect.{IO, ContextShift, Resource}
import cats.effect.ContextShift

import Protocol._

import scala.concurrent.ExecutionContext
import org.http4s.client.blaze.BlazeClientBuilder

case class Config(host: String,
                  user: String,
                  password: String,
                  port: Option[Int] = None,
                  zmqPort: Option[Int] = None)

object BitcoinRPC extends Http4sClientDsl[IO] with Calls {

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
      _ <- Right(println(request.asJson))
    } yield p)
      .getOrElse(throw new Exception("No proper exception handling yet"))
  }
}

trait Calls {
  import RPCEncoders._
  import RPCDecoders._

  def getBlock(client: Client[IO],
               hash: String)(implicit config: Config): IO[BlockResponse] = {
    BitcoinRPC.request[BlockRequest, BlockResponse](client, BlockRequest(hash))
  }
  def getTransaction(client: Client[IO], hash: String)(
    implicit config: Config
  ): IO[TransactionResponse] = {
    BitcoinRPC.request[TransactionRequest, TransactionResponse](
      client,
      TransactionRequest(hash)
    )
  }

  def getBestBlockHash(
    client: Client[IO]
  )(implicit config: Config): IO[Json] = {
    BitcoinRPC
      .requestJson[BestBlockHashRequest](client, new BestBlockHashRequest)
  }

  def getBestBlockHeight(
    client: Client[IO]
  )(implicit config: Config): IO[Long] =
    for {
      hash <- getBestBlockHash(client)
      block <- getBlock(client, hash.asObject.get("result").get.asString.get)
    } yield block.height

  def getBlockHashByHeight(client: Client[IO],
                           height: Long)(implicit config: Config): IO[Json] =
    BitcoinRPC.requestJson[BlockHashByHeightRequest](
      client,
      BlockHashByHeightRequest(height)
    )

  def getBlockDataByHeight(client: Client[IO], height: Long)(
    implicit config: Config
  ): IO[BlockResponse] =
    for {
      hashResponse <- getBlockHashByHeight(client, height)
      hash <- IO(hashResponse.asObject.get("result").get.asString.get)
      block <- getBlock(client, hash)
    } yield block
}
