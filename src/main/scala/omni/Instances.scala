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
package io.tokenanalyst.bitcoinrpc.omni

import cats.effect.IO
import io.circe.generic.auto._
import io.tokenanalyst.bitcoinrpc.Codecs._
import io.tokenanalyst.bitcoinrpc.OmniMethods._
import io.tokenanalyst.bitcoinrpc.omni.Codecs._
import io.tokenanalyst.bitcoinrpc.omni.Protocol._
import io.tokenanalyst.bitcoinrpc.{BatchRequest, BatchResponse, Omni}

object Instances {

  implicit val listBlockTransactionsInstance =
    new ListBlockTransactions {
      override def listBlockTransactions(
          omni: Omni,
          height: Long
      ): IO[Seq[String]] =
        for {
          json <- omni.client.requestJson[BlockTransactionsRequest](
            BlockTransactionsRequest(height)
          )
        } yield json.asObject.get("result").get.asArray.get.map(_.asString.get)
    }

  implicit val getTransactionInstance = new GetTransaction {
    override def getTransaction(
        omni: Omni,
        hash: String
    ): IO[TransactionResponse] = {
      for {
        res <- omni.client.request[TransactionRequest, TransactionResponse](
          TransactionRequest(hash)
        )
      } yield res
    }
  }

  implicit val getTransactionsInstance =
    new GetTransactions {
      override def getTransactions(
          omni: Omni,
          hashes: Seq[String]
      ): IO[BatchResponse[TransactionResponse]] =
        for {
          res <- omni.client
            .request[BatchRequest[TransactionRequest], BatchResponse[
              TransactionResponse
            ]](
              BatchRequest[TransactionRequest](
                hashes.map(TransactionRequest.apply)
              )
            )
        } yield res
    }

  implicit val getBestBlockHashInstance = new GetBestBlockHash {
    override def getBestBlockHash(omni: Omni): IO[String] =
      for {
        json <- omni.client
          .requestJson[BestBlockHashRequest](new BestBlockHashRequest)
      } yield json.asObject.get("result").get.asString.get
  }

  implicit val getBlockByHashInstance =
    new GetBlockByHash {
      override def getBlockByHash(
          a: Omni,
          hash: String
      ): IO[BlockResponse] = {
        a.client.request[BlockRequest, BlockResponse](BlockRequest(hash))
      }
    }

  implicit val getBestBlockHeightInstance =
    new GetBestBlockHeight {
      override def getBestBlockHeight(a: Omni): IO[Long] =
        for {
          hash <- getBestBlockHashInstance.getBestBlockHash(a)
          block <- getBlockByHashInstance.getBlockByHash(a, hash)
        } yield block.height
    }
}
