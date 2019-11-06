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
package io.tokenanalyst.bitcoinrpc.omni

import io.tokenanalyst.bitcoinrpc._

object Protocol {
  case class TransactionResponse(
      txid: String,
      block: Long,
      blockhash: String,
      positioninblock: Option[Int],
      version: Option[Int],
      blocktime: Long,
      valid: Option[Boolean],
      type_int: Option[Int],
      `type`: Option[String],
      propertyid: Option[Int],
      amount: Option[Double],
      fee: Option[Double],
      sendingaddress: String,
      referenceaddress: Option[String]
  ) extends RPCResponse

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
      tx: List[String]
  ) extends RPCResponse

  case class BlockTransactionsRequest(height: Long) extends RPCRequest

  case class TransactionRequest(hash: String) extends RPCRequest

  case class BestBlockHashRequest() extends RPCRequest

  case class BlockRequest(hash: String) extends RPCRequest

}
