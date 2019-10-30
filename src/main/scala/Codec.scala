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

import io.circe.{Encoder, Decoder}
import io.circe.Json
import io.circe.HCursor
import Protocol._

trait RPCEncoder[A] {
  def apply(a: A): Json
}

trait RPCDecoder[A] {
  def apply(a: A): Json
}

object RPCDecoders {
  implicit def batch[A <: RPCResponse: Decoder] = new Decoder[BatchResponse[A]] {
    def apply(a: HCursor): Decoder.Result[BatchResponse[A]] = 
      a.as[Seq[A]].map(s => BatchResponse(s))
  }

  implicit def deriveCirceDecoder[A <: RPCResponse: Decoder] = new Decoder[A] {
    def apply(a: HCursor): Decoder.Result[A] = a.downField("result").as[A]
  }
}

object RPCEncoders {
  def requestFields(
      method: String,
      params: Iterable[Json]
  ): List[(String, Json)] = List(
    ("jsonrpc", Json.fromString("2.0")),
    ("id", Json.fromString("0")),
    ("method", Json.fromString(method)),
    ("params", Json.fromValues(params))
  )

  implicit def batchRequest[A <: RPCRequest](implicit encoder: RPCEncoder[A]) = 
    new RPCEncoder[BatchRequest[A]] {
      final def apply(req: BatchRequest[A]): Json = {  
        val jsons = req.seq.map { i => encoder.apply(i)} 
        Json.arr(jsons:_*)
      }
  }

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
        requestFields("estimatesmartfee", Array(Json.fromInt(a.blocks))): _*
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
