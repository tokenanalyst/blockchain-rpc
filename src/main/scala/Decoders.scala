package io.tokenanalyst.jsonrpc

import io.circe.Decoder
import io.circe.Json
import io.circe.HCursor
import Protocol._

trait RPCDecoder[A] {
  def apply(a: A): Json
}

object RPCDecoders {

  implicit def deriveCirceDecoder[A <: RPCResponse: Decoder] =
    new Decoder[A] {
      def apply(a: HCursor): Decoder.Result[A] = a.downField("result").as[A]
    }
}
