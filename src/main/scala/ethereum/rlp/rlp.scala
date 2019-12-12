package io.tokenanalyst.bitcoinrpc.ethereum.rlp

import akka.util.ByteString
import org.apache.commons.codec.binary.Hex

case class RLPException(message: String) extends RuntimeException(message)

sealed trait RLPEncodeable

case class RLPList(items: RLPEncodeable*) extends RLPEncodeable

case class RLPValue(bytes: Array[Byte]) extends RLPEncodeable {
  override def toString: String = s"RLPValue(${Hex.encodeHex(bytes).mkString})"
}

trait RLPEncoder[T] {
  def encode(obj: T): RLPEncodeable
}

trait RLPDecoder[T] {
  def decode(rlp: RLPEncodeable): T
}

trait RLPSerializable {
  def toRLPEncodable: RLPEncodeable
  def toBytes(implicit di: DummyImplicit): ByteString = ByteString(toBytes: Array[Byte])
  def toBytes: Array[Byte] = encode(this.toRLPEncodable)
}
