package io.tokenanalyst.blockchainrpc.ethereum

import org.apache.commons.codec.binary.Hex

object HexTools {

  def parseQuantity(in: String): BigInt = {
    val cleaned = in.replaceFirst("^0x", in.length % 2 match {
      case 0 => ""
      case 1 => "0"
    })
    UInt256(Hex.decodeHex(cleaned)).toBigInt
  }

  def parseData(in: String): Array[Byte] = {
    Hex.decodeHex(in.replaceFirst("^0x", ""))
  }

  def toHexString(in: Long) = UInt256(in).toHexString

}
