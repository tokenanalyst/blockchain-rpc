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
package io.tokenanalyst.blockchainrpc.ethereum

import cats.effect.IO
import io.tokenanalyst.blockchainrpc.{Ethereum, RPCRequest, RPCResponse}

object Methods {

  trait GetBlockWithTransactionsByHash[A <: Ethereum, B] {
    def getBlockWithTransactionsByHash(a: A, hash: String): IO[B]
  }

  trait GetBlockWithTransactionsByHeight[A <: Ethereum, B] {
    def getBlockWithTransactionsByHeight(a: A, height: Long): IO[B]
  }

  trait GetReceipt[A <: Ethereum, B] {
    def getReceipt(a: A, hash: String): IO[B]
  }

  trait GetReceipts[A <: Ethereum, B] {
    def getReceipts(a: A, hashes: Seq[String]): IO[B]
  }
}

object Protocol {

  type BlockWithTransactionsResponse =
    GenericBlockResponse[TransactionResponse]

  type BlockResponse = GenericBlockResponse[String]

  case class GenericBlockResponse[A](
      author: String,
      difficulty: String,
      extraData: String,
      gasLimit: String,
      gasUsed: String,
      hash: String,
      logsBloom: String,
      miner: String,
      mixHash: String,
      nonce: String,
      number: String,
      parentHash: String,
      receiptsRoot: String,
      sealFields: List[String],
      sha3Uncles: String,
      size: String,
      stateRoot: String,
      timestamp: String,
      totalDifficulty: String,
      transactions: List[A],
      transactionsRoot: String,
      uncles: List[String]
  ) extends RPCResponse

  case class TransactionResponse(
      blockHash: String,
      blockNumber: String,
      chainId: Option[String],
      from: String,
      gas: String,
      gasPrice: String,
      hash: String,
      input: String,
      nonce: String,
      publicKey: String,
      r: String,
      raw: String,
      s: String,
      v: String,
      standardV: String,
      to: Option[String],
      transactionIndex: String,
      value: String,
      condition: Option[String],
      creates: Option[String]
  ) extends RPCResponse

  case class ReceiptResponse(
      blockHash: String,
      blockNumber: String,
      contractAddress: Option[String],
      from: Option[String],
      to: Option[String],
      cumulativeGasUsed: String,
      gasUsed: Option[String],
      logs: List[LogResponse],
      logsBloom: String,
      status: Option[String],
      transactionHash: String,
      transactionIndex: String
  ) extends RPCResponse

  case class LogResponse(
      removed: Boolean,
      logIndex: Option[String],
      transactionIndex: Option[String],
      transactionHash: Option[String],
      blockHash: Option[String],
      blockNumber: Option[String],
      address: String,
      data: String,
      topics: List[String],
      transactionLogIndex: Option[String],
      `type`: String
  )

  case class BlockByHashRequest(hash: String, withTransactions: Boolean)
      extends RPCRequest
  case class BlockByHeightRequest(height: Long, withTransactions: Boolean)
      extends RPCRequest
  case class ReceiptRequest(hash: String) extends RPCRequest
  case class TransactionRequest(hash: String) extends RPCRequest
  case class BestBlockHeightRequest() extends RPCRequest
}
