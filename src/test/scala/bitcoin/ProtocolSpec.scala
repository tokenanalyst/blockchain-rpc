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

import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import com.softwaremill.diffx.scalatest.DiffMatcher
import io.tokenanalyst.bitcoinrpc.bitcoin.Protocol._
import io.tokenanalyst.bitcoinrpc.Codecs._
import io.circe.generic.auto._
import io.circe.parser.decode

class ProtocolSpec extends AnyFlatSpec with Matchers with DiffMatcher {

  behavior of "Bitcoin protocol"

  it should "decode BlockResponse" in {
    val response =
      """
        {"result":{"hash":"00000000000000000009db93b0bd627158b665cd954cc274c056ad8b257c3f35","confirmations":1,"strippedsize":779713,"size":1654295,"weight":3993434,"height":607523,"version":536928256,"versionHex":"2000e000","merkleroot":"e1c20883d44eead043c4d8bf118520e61c72183145067d212af8fba319d67ba5","tx":["26de2820d5e15884a18c24426c6fafa6f527cca0f67a0266aa9737690b0bf3bc"],"time":1575992821,"mediantime":1575988911,"nonce":3271658646,"bits":"1715dbd2","difficulty":12876842089682.48,"chainwork":"00000000000000000000000000000000000000000ac014e49df992a6caa4cd1c","nTx":2273,"previousblockhash":"0000000000000000000280301806aa6a1e74ff64139f2dd03c3ac30802739c7a"},"error":null,"id":"curltest"}
      """
    val decoded = decode[BlockResponse](response)
    decoded.isRight shouldEqual true
    decoded.right.get should matchTo(BlockResponse(
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
    ))
  }

  it should "decode TransactionResponse" in { 
    val response = 
    """
    {"result":{"txid":"3246fa6d081b32223a4097052b15594acc00372a9ce293b29f92086e19655888","hash":"20ed27407730078e01478e375910cd92ec4c99c0e1763a893df877e36a63f24f","version":1,"size":193,"vsize":111,"weight":442,"locktime":0,"vin":[{"txid":"eee52abb8ef949f0eaba5b331b4aa6de13601b00a93d99ed0cc75189be3d2a24","vout":1,"scriptSig":{"asm":"","hex":""},"txinwitness":["3045022100dc5b8d3643750a3b96defa6e452b268d25bdcaba9d75e3614aaa8f59c285bd3902206b44833a75111ce4bd487be86972def80db4ef8c2865e8b1c19cda5f9f7ee09101","035d6e3719f26bcc5381eca4e5c33ff3f9fd5b4e84450f863abbd83a584ccdc204"],"sequence":4294967295}],"vout":[{"value":0.00803580,"n":0,"scriptPubKey":{"asm":"OP_HASH160 ab974139080e159ac8af9fc0d9e2ef4885a1339d OP_EQUAL","hex":"a914ab974139080e159ac8af9fc0d9e2ef4885a1339d87","reqSigs":1,"type":"scripthash","addresses":["3HLJfiJLa5CnKQoAF3YeteidRjDMvY5upH"]}}],"hex":"01000000000101242a3dbe8951c70ced993da9001b6013dea64a1b335bbaeaf049f98ebb2ae5ee0100000000ffffffff01fc420c000000000017a914ab974139080e159ac8af9fc0d9e2ef4885a1339d8702483045022100dc5b8d3643750a3b96defa6e452b268d25bdcaba9d75e3614aaa8f59c285bd3902206b44833a75111ce4bd487be86972def80db4ef8c2865e8b1c19cda5f9f7ee0910121035d6e3719f26bcc5381eca4e5c33ff3f9fd5b4e84450f863abbd83a584ccdc20400000000","blockhash":"0000000000000000000e924f2fc7105362ce640d0865d10d314086c795d2cfde","confirmations":1,"time":1575996190,"blocktime":1575996190},"error":null,"id":"curltest"}
    """
    val decoded = decode[TransactionResponse](response) 
    decoded.isRight shouldEqual true
    decoded.right.get should matchTo(TransactionResponse(
     Some(1),
      "0000000000000000000e924f2fc7105362ce640d0865d10d314086c795d2cfde",
      1575996190L,
      "20ed27407730078e01478e375910cd92ec4c99c0e1763a893df877e36a63f24f",
      "01000000000101242a3dbe8951c70ced993da9001b6013dea64a1b335bbaeaf049f98ebb2ae5ee0100000000ffffffff01fc420c000000000017a914ab974139080e159ac8af9fc0d9e2ef4885a1339d8702483045022100dc5b8d3643750a3b96defa6e452b268d25bdcaba9d75e3614aaa8f59c285bd3902206b44833a75111ce4bd487be86972def80db4ef8c2865e8b1c19cda5f9f7ee0910121035d6e3719f26bcc5381eca4e5c33ff3f9fd5b4e84450f863abbd83a584ccdc20400000000",
      "3246fa6d081b32223a4097052b15594acc00372a9ce293b29f92086e19655888",
      1575996190L,
      111,
      193,
      442,
      1,
      List(TransactionResponseVin(
        Some("eee52abb8ef949f0eaba5b331b4aa6de13601b00a93d99ed0cc75189be3d2a24"),
        Some(1),
        Some(TransactionResponseScriptSig("", "")),
        None,
        4294967295L)),
      List(TransactionResponseVout(0.00803580, 0, TransactionResponseScript("OP_HASH160 ab974139080e159ac8af9fc0d9e2ef4885a1339d OP_EQUAL",
      "a914ab974139080e159ac8af9fc0d9e2ef4885a1339d87", Some(1), "scripthash", Some(List("3HLJfiJLa5CnKQoAF3YeteidRjDMvY5upH"))))),
      0
    ))
  }
}
