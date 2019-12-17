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
package io.tokenanalyst.blockchainrpc.omni

import io.circe.Json
import io.tokenanalyst.blockchainrpc.Codecs._
import io.tokenanalyst.blockchainrpc.RPCEncoder
import io.tokenanalyst.blockchainrpc.omni.Protocol.BlockRequest
import io.tokenanalyst.blockchainrpc.omni.Protocol.{
  BestBlockHashRequest,
  BlockTransactionsRequest,
  TransactionRequest,
  BlockHashRequest
}

object Codecs {

  implicit val listBlockTransactionsRequest =
    new RPCEncoder[BlockTransactionsRequest] {
      final def apply(a: BlockTransactionsRequest): Json = {
        Json.obj(
          requestFields(
            "omni_listblocktransactions",
            Array(Json.fromLong(a.height))
          ): _*
        )
      }
    }

  implicit val blockHashRequest = new RPCEncoder[BlockHashRequest] {
    final def apply(a: BlockHashRequest): Json =
      Json.obj(
        requestFields("getblockhash", Array[Json](Json.fromLong(a.height))): _*
      )
  }

  implicit val getTransactionRequest = new RPCEncoder[TransactionRequest] {
    final def apply(a: TransactionRequest): Json =
      Json.obj(
        requestFields(
          "omni_gettransaction",
          Array(Json.fromString(a.hash))
        ): _*
      )
  }

  implicit val bestBlockHashRequest = new RPCEncoder[BestBlockHashRequest] {
    final def apply(a: BestBlockHashRequest): Json =
      Json.obj(requestFields("getbestblockhash", Array[Json]()): _*)
  }

  implicit val blockRequest = new RPCEncoder[BlockRequest] {
    final def apply(a: BlockRequest): Json =
      Json.obj(requestFields("getblock", Array(Json.fromString(a.hash))): _*)
  }
}
