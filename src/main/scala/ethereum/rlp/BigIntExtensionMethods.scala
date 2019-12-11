package io.tokenanalyst.bitcoinrpc.ethereum.rlp

object BigIntExtensionMethods {
  implicit class BigIntAsUnsigned(val srcBigInteger: BigInt) extends AnyVal {
    def toUnsignedByteArray: Array[Byte] = {
      val asByteArray = srcBigInteger.toByteArray
      if (asByteArray.head == 0) asByteArray.tail
      else asByteArray
    }

    def u256: UInt256 = UInt256(srcBigInteger)
  }
}
