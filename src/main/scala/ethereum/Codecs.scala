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
package io.tokenanalyst.blockchainrpc.ethereum

import io.circe.Json
import io.tokenanalyst.blockchainrpc.Codecs._
import io.tokenanalyst.blockchainrpc.RPCEncoder
import io.tokenanalyst.blockchainrpc.ethereum.Protocol._

object Codecs {

  implicit val receiptRequest = new RPCEncoder[ReceiptRequest] {
    final def apply(a: ReceiptRequest): Json = Json.obj(
      requestFields(
        "eth_getTransactionReceipt",
        Array(Json.fromString(a.hash))
      ): _*
    )
  }

  implicit val transactionRequest = new RPCEncoder[TransactionRequest] {
    final def apply(a: TransactionRequest): Json = Json.obj(
      requestFields(
        "eth_getTransactionByHash",
        Array(Json.fromString(a.hash))
      ): _*
    )
  }

  implicit val bestBlockHeightRequest = new RPCEncoder[BestBlockHeightRequest] {
    final def apply(a: BestBlockHeightRequest): Json =
      Json.obj(requestFields("eth_blockNumber", Array[Json]()): _*)
  }

  implicit val blockByHeightRequest = new RPCEncoder[BlockByHeightRequest] {
    final def apply(a: BlockByHeightRequest): Json =
      Json.obj(
        requestFields(
          "eth_getBlockByNumber",
          Array[Json](
            Json.fromString(
              HexTools.toHexString(a.height)
            ),
            Json.fromBoolean(a.withTransactions)
          )
        ): _*
      )
  }

  implicit val blockByHashRequest = new RPCEncoder[BlockByHashRequest] {
    final def apply(a: BlockByHashRequest): Json =
      Json.obj(
        requestFields(
          "eth_getBlockByHash",
          Array(Json.fromString(a.hash), Json.fromBoolean(a.withTransactions))
        ): _*
      )
  }
}
