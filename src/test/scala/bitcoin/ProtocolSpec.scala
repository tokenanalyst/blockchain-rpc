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
package io.tokenanalyst.bitcoinrpc.test.bitcoin

import org.scalatest.{FlatSpec, Matchers}

import io.tokenanalyst.bitcoinrpc.bitcoin.Protocol._
import io.tokenanalyst.bitcoinrpc.Codecs._
import io.circe.generic.auto._
import io.circe.parser.decode

class ProtocolSpec extends FlatSpec with Matchers {

  behavior of "Bitcoin protocol"

  it should "decode BlockResponse" in {
    val response =
      """
        {"result":{"hash":"00000000000000000009db93b0bd627158b665cd954cc274c056ad8b257c3f35","confirmations":1,"strippedsize":779713,"size":1654295,"weight":3993434,"height":607523,"version":536928256,"versionHex":"2000e000","merkleroot":"e1c20883d44eead043c4d8bf118520e61c72183145067d212af8fba319d67ba5","tx":["26de2820d5e15884a18c24426c6fafa6f527cca0f67a0266aa9737690b0bf3bc"],"time":1575992821,"mediantime":1575988911,"nonce":3271658646,"bits":"1715dbd2","difficulty":12876842089682.48,"chainwork":"00000000000000000000000000000000000000000ac014e49df992a6caa4cd1c","nTx":2273,"previousblockhash":"0000000000000000000280301806aa6a1e74ff64139f2dd03c3ac30802739c7a"},"error":null,"id":"curltest"}
      """
    val decoded = decode[BlockResponse](response)
    decoded.isRight shouldEqual true
    decoded.right.get shouldEqual BlockResponse(
      607523,
      "00000000000000000009db93b0bd627158b665cd954cc274c056ad8b257c3f35",
      Some("0000000000000000000280301806aa6a1e74ff64139f2dd03c3ac30802739c7a"),
      3271658646L,
      779713,
      "e1c20883d44eead043c4d8bf118520e61c72183145067d212af8fba319d67ba5",
      536928256,
      3993434,
      12876842089682.48,
      "00000000000000000000000000000000000000000ac014e49df992a6caa4cd1c",
      "1715dbd2",
      1654295,
      1575988911,
      1575992821,
      2273,
      List("26de2820d5e15884a18c24426c6fafa6f527cca0f67a0266aa9737690b0bf3bc")
    )
  }
}
