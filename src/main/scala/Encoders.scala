package io.tokenanalyst.jsonrpc

import io.circe.Encoder
import io.circe.Json
import Protocol._

trait RPCEncoder[A] {
  def apply(a: A): Json
}

object RPCEncoders {
    def requestFields(method: String,
                      params: Iterable[Json]): List[(String, Json)] = List(
      ("jsonrpc", Json.fromString("1.1")),
      ("id", Json.fromString("0")),
      ("method", Json.fromString(method)),
      ("params", Json.fromValues(params))
    )
 
    implicit val transactionRequest = new RPCEncoder[TransactionRequest] {
      final def apply(a: TransactionRequest): Json =
        Json.obj(requestFields("getrawtransaction", Array(Json.fromString(a.hash), Json.fromInt(1))): _*)
    }    

    implicit val feeRequest = new RPCEncoder[FeeRequest] {
      final def apply(a: FeeRequest): Json =
        Json.obj(
          requestFields("estimatesmartfee", Array(Json.fromInt(a.blocks))): _*
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
  
    implicit def deriveCirceEncoder[A <: RPCRequest](
      implicit e: RPCEncoder[A]
    ) = new Encoder[A] {
      def apply(a: A): Json = e.apply(a)
    }
  }