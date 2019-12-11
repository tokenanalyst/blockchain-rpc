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
package io.tokenanalyst.bitcoinrpc.ethereum

import cats.effect.IO
import io.circe.generic.auto._
import io.tokenanalyst.bitcoinrpc.BasicMethods._
import io.tokenanalyst.bitcoinrpc.Codecs._
import io.tokenanalyst.bitcoinrpc.ethereum.Protocol._
import io.tokenanalyst.bitcoinrpc.ethereum.Codecs._
import io.tokenanalyst.bitcoinrpc.{BatchRequest, BatchResponse, Ethereum}

import scala.collection.mutable.ListBuffer

import Methods._

object Instances {

  implicit val getNextBlockHashInstance =
    new GetNextBlockHash[Ethereum] {
      override def getNextBlockHash(a: Ethereum): IO[String] =
        a.client.nextBlockHash()
    }

  implicit val getBlockByHashInstance =
    new GetBlockByHash[Ethereum, BlockResponse] {
      override def getBlockByHash(
          a: Ethereum,
          hash: String
      ): IO[BlockResponse] = {
        a.client.request[BlockByHashRequest, BlockResponse](BlockByHashRequest(hash))
      }
    }

  implicit val getBlockByHeightInstance =
    new GetBlockByHeight[Ethereum, BlockResponse] {
      override def getBlockByHeight(
          a: Ethereum,
          height: Long
      ): IO[BlockResponse] =
      a.client.request[BlockByHeightRequest, BlockResponse](BlockByHeightRequest(height))
    }

  implicit val getBestBlockHeightInstance =
    new GetBestBlockHeightRLP[Ethereum] {
      override def getBestBlockHeight(a: Ethereum): IO[String] = 
        for {
          json <- a.client
            .requestJson[BestBlockHeightRequest](new BestBlockHeightRequest)
        } yield json.asObject.get("result").get.asString.get
    }

  implicit val getTransactionsInstance =
    new GetTransactions[Ethereum, BatchResponse[
      TransactionResponse
    ]] {
      override def getTransactions(
          a: Ethereum,
          hashes: Seq[String]
      ): IO[BatchResponse[TransactionResponse]] = {
        val list = ListBuffer(hashes: _*)
        val genesisTransactionIndex =
          list.indexOf(Transactions.GenesisTransactionHash)

        if (genesisTransactionIndex >= 0) {
          list.remove(genesisTransactionIndex)
        }

        val result =
          a.client.request[BatchRequest[TransactionRequest], BatchResponse[
            TransactionResponse
          ]](
            BatchRequest[TransactionRequest](list.map(TransactionRequest.apply).toSeq)
          )

        if (genesisTransactionIndex >= 0) {
          for {
            batcResponse <- result
            listResult <- IO(ListBuffer(batcResponse.seq: _*))
            _ <- IO(
              listResult
                .insert(
                  genesisTransactionIndex,
                  Transactions.GenesisTransaction
                )
            )
          } yield BatchResponse(listResult.toSeq)
        } else {
          result
        }
      }
    }

  implicit val getTransactionInstance =
    new GetTransaction[Ethereum, TransactionResponse] {
      override def getTransaction(
          a: Ethereum,
          hash: String
      ): IO[TransactionResponse] =
        if (hash != Transactions.GenesisTransactionHash) {
          a.client.request[TransactionRequest, TransactionResponse](
            TransactionRequest(hash)
          )
        } else {
          IO(Transactions.GenesisTransaction)
        }
    }
}
