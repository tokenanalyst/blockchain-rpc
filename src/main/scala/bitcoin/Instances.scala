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
package io.tokenanalyst.bitcoinrpc.bitcoin

import io.circe.generic.auto._
import io.tokenanalyst.bitcoinrpc.{BasicMethods, Bitcoin}
import cats.effect.IO

import RPCEncoders._
import RPCDecoders._
import Protocol._

object Instances {
  implicit val getBlockInstance = new BasicMethods.GetBlock[Bitcoin, BlockResponse] {
    def getBlock(a: Bitcoin, hash: String): IO[BlockResponse] = {
      a.client.request[BlockRequest, BlockResponse](BlockRequest(hash))
    }
  }
}

/*
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

  def estimateSmartFee(client: Client[IO], height: Int)(
    implicit config: Config
  ): IO[FeeResponse] = {
    BitcoinRPC.request[FeeRequest, FeeResponse](client, FeeRequest(height))
  }
}
*/