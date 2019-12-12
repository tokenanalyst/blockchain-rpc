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
import io.tokenanalyst.bitcoinrpc.BasicMethods._
import io.tokenanalyst.bitcoinrpc.Ethereum
import io.tokenanalyst.bitcoinrpc.ethereum.Instances._
import io.tokenanalyst.bitcoinrpc.ethereum.Methods._
import io.tokenanalyst.bitcoinrpc.ethereum.Protocol.{BlockRLPResponse, _}
import io.tokenanalyst.bitcoinrpc.ethereum.rlp.RLPImplicits._
import io.tokenanalyst.bitcoinrpc.ethereum.rlp.RLPImplicitConversions._
import io.tokenanalyst.bitcoinrpc.ethereum.rlp._

object Syntax {
  implicit class EthereumOps(b: Ethereum) {

    def getNextBlockHash() =
      implicitly[GetNextBlockHash[Ethereum]].getNextBlockHash(b)

    def getBlockWithTransactionsByHeightRLP(height: Long) =
      implicitly[GetBlockByHeight[Ethereum, BlockWithTransactionsRLPResponse]]
        .getBlockByHeight(b, height)

    def getBlockWithTransactionsByHashRLP(hash: String) =
      implicitly[GetBlockByHash[Ethereum, BlockWithTransactionsRLPResponse]]
        .getBlockByHash(b, hash)

    def getReceiptByHash(hash: String) =
      implicitly[GetReceipt[Ethereum, ReceiptRLPResponse]].getReceipt(b, hash)

    def getBlockByHeightRLP(height: Long) =
      implicitly[GetBlockByHeight[Ethereum, BlockRLPResponse]]
        .getBlockByHeight(b, height)

    def getBlockByHeight(height: Long) =
      for {
        blockRLP <- implicitly[GetBlockByHeight[Ethereum, BlockRLPResponse]]
          .getBlockByHeight(b, height)
        _ <- IO(println(blockRLP.number))
        block <- IO.pure(
          GenericBlockResponse[Array[Byte]](
            author = decode[Array[Byte]](blockRLP.author),
            difficulty = decode[BigInt](blockRLP.difficulty),
            extraData = decode[Array[Byte]](blockRLP.extraData),
            gasLimit = decode[Long](blockRLP.gasLimit),
            gasUsed = decode[Long](blockRLP.gasUsed),
            hash = decode[Array[Byte]](blockRLP.hash),
            logsBloom = decode[Array[Byte]](blockRLP.logsBloom),
            miner = decode[Array[Byte]](blockRLP.miner),
            mixHash = decode[Array[Byte]](blockRLP.mixHash),
            nonce = decode[Array[Byte]](blockRLP.nonce),
            number = decode[Long](blockRLP.number),
            parentHash = decode[Array[Byte]](blockRLP.parentHash),
            receiptsRoot = decode[Array[Byte]](blockRLP.receiptsRoot),
            sealFields = decode[Seq[Array[Byte]]](blockRLP.sealFields)(seqEncDec()),
            sha3Uncles = decode[Array[Byte]](blockRLP.sha3Uncles),
            size = decode[Long](blockRLP.size),
            stateRoot = decode[Array[Byte]](blockRLP.stateRoot),
            timestamp = decode[BigInt](blockRLP.timestamp),
            totalDifficulty = decode[BigInt](blockRLP.totalDifficulty),
            transactions = decode[Seq[Array[Byte]]](blockRLP.transactions)(seqEncDec()),
            transactionsRoot = decode[Array[Byte]](blockRLP.transactionsRoot),
            uncles = decode[Seq[Array[Byte]]](blockRLP.uncles)(seqEncDec())
          )
        )
      } yield block

    def getBlockByHashRLP(hash: String) =
      implicitly[GetBlockByHash[Ethereum, BlockRLPResponse]]
        .getBlockByHash(b, hash)

    def getBestBlockHeight() =
      implicitly[GetBestBlockHeightRLP[Ethereum]].getBestBlockHeight(b)

    def getTransactionRLP(hash: String) =
      implicitly[GetTransaction[Ethereum, TransactionRLPResponse]]
        .getTransaction(b, hash)
  }
}
