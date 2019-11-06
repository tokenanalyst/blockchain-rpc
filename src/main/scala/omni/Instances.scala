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
import io.tokenanalyst.bitcoinrpc.BasicMethods.{GetBlockByHeight, GetTransactions}
import io.tokenanalyst.bitcoinrpc.omni.Protocol.{BlockTransactionsRequest, TransactionRequest, TransactionResponse}
import io.tokenanalyst.bitcoinrpc.{BatchRequest, BatchResponse, Omni}

object Instances {

  implicit val listBlockTransactions =
    new GetBlockByHeight[Omni, Seq[String]] {
      override def getBlockByHeight(a: Omni, height: Long): IO[Seq[String]] =
        for {
          json <- a.client.requestJson[BlockTransactionsRequest](
            BlockTransactionsRequest(height)
          )
        } yield json.asObject.get("result").get.asArray.get.map(_.asString.get)
    }

  implicit val getTransactions =
    new GetTransactions[Omni, BatchResponse[TransactionResponse]] {
      override def getTransactions(
          a: Omni,
          hashes: Seq[String]
      ): IO[BatchResponse[TransactionResponse]] =
        for {
          res <- a.client
            .request[BatchRequest[TransactionRequest], BatchResponse[
              TransactionResponse
            ]](
              BatchRequest[TransactionRequest](
                hashes.map(TransactionRequest.apply)
              )
            )
        } yield res
    }
}
