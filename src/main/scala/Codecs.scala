package io.tokenanalyst.bitcoinrpc

import io.circe.{Decoder, Encoder, HCursor, Json}

object Codecs {
  implicit def batchResponse[A <: RPCResponse: Decoder] =
    new Decoder[BatchResponse[A]] {
      def apply(a: HCursor): Decoder.Result[BatchResponse[A]] =
        a.as[Seq[A]].map(s => BatchResponse(s))
    }

  implicit def deriveCirceDecoder[A <: RPCResponse: Decoder] = new Decoder[A] {
    def apply(a: HCursor): Decoder.Result[A] = a.downField("result").as[A]
  }

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
        val jsons = req.seq.map { i =>
          encoder.apply(i)
        }
        Json.arr(jsons: _*)
      }
    }

  implicit def deriveCirceEncoder[A <: RPCRequest](
      implicit e: RPCEncoder[A]
  ) = new Encoder[A] {
    def apply(a: A): Json = e.apply(a)
  }
}
