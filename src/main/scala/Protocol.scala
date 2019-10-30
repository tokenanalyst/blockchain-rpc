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

object Protocol {
  sealed trait RPCResponse

  case class BatchResponse[A](seq: Seq[A]) extends RPCResponse
  case class FeeResponse(feerate: Double, blocks: Int) extends RPCResponse
  case class BlockHashResponse(hash: String) extends RPCResponse
  case class BlockResponse(
      height: Long,
      hash: String,
      previousblockhash: String,
      nonce: Long,
      strippedsize: Long,
      merkleroot: String,
      version: Int,
      weight: Int,
      difficulty: Double,
      chainwork: String,
      bits: String,
      size: Long,
      mediantime: Long,
      time: Long,
      nTx: Int,
      tx: List[String]
  ) extends RPCResponse

  case class TransactionResponseVin(
      txid: Option[String],
      vout: Option[Int],
      scriptSig: Option[TransactionResponseScriptSig],
      coinbase: Option[String],
      sequence: Long
  )

  case class TransactionResponseScriptSig(
      asm: String,
      hex: String
  )

  case class TransactionResponseScript(
      asm: String,
      hex: String,
      reqSigs: Option[Int],
      `type`: String,
      addresses: Option[List[String]]
  )

  case class TransactionResponseVout(
      value: Double,
      n: Int,
      scriptPubKey: TransactionResponseScript
  )

  case class TransactionResponse(
      confirmations: Int,
      blockhash: String,
      blocktime: Long,
      hash: String,
      hex: String,
      txid: String,
      time: Long,
      vsize: Int,
      size: Int,
      weight: Int,
      version: Int,
      vin: List[TransactionResponseVin],
      vout: List[TransactionResponseVout],
      locktime: Long
  ) extends RPCResponse

  sealed trait RPCRequest
  case class BatchRequest[A](seq: Seq[A]) extends RPCRequest
  case class FeeRequest(blocks: Int) extends RPCRequest
  case class BlockRequest(hash: String) extends RPCRequest
  case class BlockHashRequest(height: Long) extends RPCRequest
  case class TransactionRequest(hash: String) extends RPCRequest
  case class BestBlockHashRequest() extends RPCRequest
  case class BlockHashByHeightRequest(height: Long) extends RPCRequest
}

object Blocks {
  import Protocol.BlockResponse
  import io.circe.parser._
  import io.circe.generic.auto._

  val Genesis = parse(
    """
    {
      "txid": "4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b",
      "hash": "4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b",
      "version": 1,
      "locktime": 0,
      "size": 204,
      "height": 0,
      "vsize": 204,
      "weight": 816,
      "time": 1231006505,
      "blocktime": 1231006505,
      "vin": [
        {
          "coinbase": "04ffff001d0104455468652054696d65732030332f4a616e2f32303039204368616e63656c6c6f72206f6e206272696e6b206f66207365636f6e64206261696c6f757420666f722062616e6b73",
          "sequence": 4294967295
        }
      ],
      "vout": [
        {
          "value": 50.00000000,
          "n": 0,
          "scriptPubKey": {
            "asm": "04678afdb0fe5548271967f1a67130b7105cd6a828e03909a67962e0ea1f61deb649f6bc3f4cef38c4f35504e51ec112de5c384df7ba0b8d578a4c702b6bf11d5f OP_CHECKSIG",
            "hex": "4104678afdb0fe5548271967f1a67130b7105cd6a828e03909a67962e0ea1f61deb649f6bc3f4cef38c4f35504e51ec112de5c384df7ba0b8d578a4c702b6bf11d5fac",
            "reqSigs": 1,
            "type": "pubkey",
            "addresses": [
              "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa"
            ]
          }
        }
      ]
    }
    """
  ).flatMap { json =>
      json.as[BlockResponse]
    }
    .getOrElse(throw new Exception("Could not parse genesis"))
}
