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
package io.tokenanalyst.bitcoinrpc.ethereum

import cats.effect.IO
import io.tokenanalyst.bitcoinrpc.Ethereum
import io.tokenanalyst.bitcoinrpc.{RPCRequest, RPCResponse}
import scala.io.Source

object Methods {
  trait GetBestBlockHeightRLP[A <: Ethereum] {
    def getBestBlockHeight(a: A): IO[String]
  }

  trait GetBlockWithTransactionsByHash[A <: Ethereum, B] {
    def getBlockWithTransactionsByHash(a: A, hash: String): IO[B]
  }

  trait GetBlockWithTransactionsByHeight[A <: Ethereum, B] {
    def getBlockWithTransactionsByHeight(a: A, height: Long): IO[B]
  }

  trait GetReceipt[A <: Ethereum, B] { 
    def getReceipt(a: A, hash: String): IO[B]
  }
}

object Protocol {
  type BlockWithTransactionsRLPResponse =
    GenericBlockRLPResponse[TransactionRLPResponse]
  type BlockRLPResponse = GenericBlockRLPResponse[String]

  case class GenericBlockRLPResponse[A](
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

  case class TransactionRLPResponse(
      blockHash: String,
      blockNumber: String,
      chainId: String,
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
      to: String,
      transactionIndex: String,
      value: String
  ) extends RPCResponse

  case class ReceiptRLPResponse(
      blockHash: String,
      blockNumber: String,
      contractAddress: Option[String],
      from: String,
      to: Option[String],
      cumulativeGasUsed: String,
      gasUsed: String,
      logs: List[LogResponse],
      logsBloom: String,
      status: Option[String],
      transactionHash: String,
      transactionIndex: String
  ) extends RPCResponse

  case class LogResponse()

  case class BlockByHashRequest(hash: String, withTransactions: Boolean)
      extends RPCRequest
  case class BlockByHeightRequest(height: Long, withTransactions: Boolean)
      extends RPCRequest
  case class ReceiptRequest(hash: String) extends RPCRequest
  case class TransactionRequest(hash: String) extends RPCRequest
  case class BestBlockHeightRequest() extends RPCRequest
}

object Transactions {
  import io.circe.generic.auto._
  import io.circe.parser._
  import Protocol._

  lazy val GenesisTransactionHash =
    "4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b"

  lazy val GenesisTransaction =
    parse(Source.fromResource("bitcoinGenesisTransaction.json").mkString)
      .flatMap { json =>
        json.as[TransactionRLPResponse]
      }
      .getOrElse(throw new Exception("Could not parse genesis"))
}
