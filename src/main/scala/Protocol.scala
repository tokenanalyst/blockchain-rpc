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
      coinbase: String,
      sequence: Long
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
      hex: String,
      txid: String,
      time: Long,
      vsize: Int,
      size: Int,
      weight:Int,
      vin: List[TransactionResponseVin],
      vout: List[TransactionResponseVout],
      locktime: Long
    ) extends RPCResponse

    sealed trait RPCRequest
    case class FeeRequest(blocks: Int) extends RPCRequest
    case class BlockRequest(hash: String) extends RPCRequest
    case class BlockHashRequest(height: Long) extends RPCRequest
    case class TransactionRequest(hash: String) extends RPCRequest
    case class BestBlockHashRequest() extends RPCRequest
  }
