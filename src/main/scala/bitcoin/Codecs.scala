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

import io.circe.{Encoder, Json}
import io.tokenanalyst.bitcoinrpc.GenericRPCEncoders._
import io.tokenanalyst.bitcoinrpc.bitcoin.Protocol._
import io.tokenanalyst.bitcoinrpc.{RPCEncoder, RPCRequest}

object Codecs {

  implicit val transactionRequest = new RPCEncoder[TransactionRequest] {
    final def apply(a: TransactionRequest): Json =
      Json.obj(
        requestFields(
          "getrawtransaction",
          Array(Json.fromString(a.hash), Json.fromInt(1))
        ): _*
      )
  }

  implicit val feeRequest = new RPCEncoder[FeeRequest] {
    final def apply(a: FeeRequest): Json =
      Json.obj(
        requestFields("estimatesmartfee", Array(Json.fromLong(a.block))): _*
      )
  }

  implicit val blockHashByHeightRequest =
    new RPCEncoder[BlockHashByHeightRequest] {
      override def apply(a: BlockHashByHeightRequest): Json =
        Json.obj(
          requestFields("getblockhash", Array(Json.fromLong(a.height))): _*
        )
    }

  implicit val bestBlockHashRequest = new RPCEncoder[BestBlockHashRequest] {
    final def apply(a: BestBlockHashRequest): Json =
      Json.obj(requestFields("getbestblockhash", Array[Json]()): _*)
  }

  implicit val blockHashRequest = new RPCEncoder[BlockHashRequest] {
    final def apply(a: BlockHashRequest): Json =
      Json.obj(
        requestFields("getblockhash", Array[Json](Json.fromLong(a.height))): _*
      )
  }

  implicit val blockRequest = new RPCEncoder[BlockRequest] {
    final def apply(a: BlockRequest): Json =
      Json.obj(requestFields("getblock", Array(Json.fromString(a.hash))): _*)
  }

  implicit def deriveCirceEncoder[A <: RPCRequest](
      implicit e: RPCEncoder[A]
  ) = new Encoder[A] {
    def apply(a: A): Json = e.apply(a)
  }
}
