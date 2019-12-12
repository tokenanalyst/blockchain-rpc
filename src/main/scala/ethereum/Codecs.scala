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

import io.circe.Json
import io.tokenanalyst.bitcoinrpc.Codecs._
import io.tokenanalyst.bitcoinrpc.RPCEncoder
import io.tokenanalyst.bitcoinrpc.ethereum.Protocol._
import io.tokenanalyst.bitcoinrpc.ethereum.rlp.RLPImplicits._

object Codecs {

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

  implicit val blockByNumberRequest = new RPCEncoder[BlockByHeightRequest] {
    final def apply(a: BlockByHeightRequest): Json =
      Json.obj(
        requestFields(
          "eth_getBlockByNumber",
          Array[Json](
            Json.fromString(
              bigIntEncDec.encode(BigInt(a.height)).hexEncoding
            ),
            Json.fromBoolean(false)
          )
        ): _*
      )
  }

  implicit val blockByHashRequest = new RPCEncoder[BlockByHashRequest] {
    final def apply(a: BlockByHashRequest): Json =
      Json.obj(
        requestFields(
          "eth_getBlockByHash",
          Array(Json.fromString(a.hash), Json.fromBoolean(false))
        ): _*
      )
  }
}
