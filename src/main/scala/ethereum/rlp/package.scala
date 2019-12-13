package io.tokenanalyst.bitcoinrpc.ethereum

import org.apache.commons.codec.binary.Hex

package object rlp {

  def encode[T](input: T)(implicit enc: RLPEncoder[T]): Array[Byte] = RLP.encode(enc.encode(input))

  def encode(input: RLPEncodeable): Array[Byte] = RLP.encode(input)

  def decode[T](data: Array[Byte])(implicit dec: RLPDecoder[T]): T = dec.decode(RLP.rawDecode(data))

  def decode[T](data: RLPEncodeable)(implicit dec: RLPDecoder[T]): T = dec.decode(data)

  def rawDecode(input: Array[Byte]): RLPEncodeable = RLP.rawDecode(input)

  def rlpStringDecode[T](input: String)(implicit dec: RLPDecoder[T]): T =
    decode(Hex.decodeHex(input.replaceFirst("^0x", "")))

  /**
    * This function calculates the next element item based on a previous element starting position. It's meant to be
    * used while decoding a stream of RLPEncoded Items.
    *
    * @param data Data with encoded items
    * @param pos  Where to start. This value should be a valid start element position in order to be able to calculate
    *             next one
    * @return Next item position
    * @throws RLPException if there is any error
    */
  def nextElementIndex(data: Array[Byte], pos: Int): Int = RLP.getItemBounds(data, pos).end + 1
}
