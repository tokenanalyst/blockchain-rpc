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

import io.circe.Json
import io.tokenanalyst.bitcoinrpc.GenericRPCEncoders.requestFields
import io.tokenanalyst.bitcoinrpc.omni.Protocol.{BlockTransactionsRequest, TransactionRequest}
import io.tokenanalyst.bitcoinrpc.RPCEncoder

object Codecs {

  implicit val listTransactionsRequest = new RPCEncoder[BlockTransactionsRequest] {
    final def apply(a: BlockTransactionsRequest): Json =
      Json.obj(
        requestFields(
          "omni_listblocktransactions",
          Array(Json.fromLong(a.height))
        ): _*
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
}
