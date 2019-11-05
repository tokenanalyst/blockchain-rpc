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
package io.tokenanalyst.bitcoinrpc.bitcoin

import io.tokenanalyst.bitcoinrpc.{RPCRequest, RPCResponse}
import scala.io.Source

object Protocol {
  case class BatchResponse[A](seq: Seq[A]) extends RPCResponse
  case class FeeResponse(feerate: Double, blocks: Int) extends RPCResponse
  case class BlockHashResponse(hash: String) extends RPCResponse
  case class BlockResponse(
      height: Long,
      hash: String,
      previousblockhash: Option[String],
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

  case class TransactionResponseScriptSig(asm: String, hex: String)

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
      confirmations: Option[Int],
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

  case class BatchRequest[A](seq: Seq[A]) extends RPCRequest
  case class FeeRequest(blocks: Int) extends RPCRequest
  case class BlockRequest(hash: String) extends RPCRequest
  case class BlockHashRequest(height: Long) extends RPCRequest
  case class TransactionRequest(hash: String) extends RPCRequest
  case class BestBlockHashRequest() extends RPCRequest
  case class BlockHashByHeightRequest(height: Long) extends RPCRequest
}

object Transactions {
  import io.circe.generic.auto._
  import io.circe.parser._
  import Protocol._

  val GenesisTransactionHash =
    "4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b"

  val GenesisTransaction =
    parse(Source.fromResource("bitcoinGenesisTransaction.json").mkString)
      .flatMap { json =>
        json.as[TransactionResponse]
      }
      .getOrElse(throw new Exception("Could not parse genesis"))
}
