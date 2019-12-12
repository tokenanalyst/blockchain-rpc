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

import Methods._
import io.tokenanalyst.bitcoinrpc.BasicMethods._
import io.tokenanalyst.bitcoinrpc.ethereum.Instances._
import io.tokenanalyst.bitcoinrpc.ethereum.Protocol._
import io.tokenanalyst.bitcoinrpc.{Ethereum}

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

    def getBlockByHeightRLP(height: Long) =
      implicitly[GetBlockByHeight[Ethereum, BlockRLPResponse]]
        .getBlockByHeight(b, height)

    def getBlockByHashRLP(hash: String) =
      implicitly[GetBlockByHash[Ethereum, BlockRLPResponse]]
        .getBlockByHash(b, hash)

    def getBestBlockHeightRLP() =
      implicitly[GetBestBlockHeightRLP[Ethereum]].getBestBlockHeight(b)

    def getTransactionRLP(hash: String) =
      implicitly[GetTransaction[Ethereum, TransactionResponse]]
        .getTransaction(b, hash)
  }
}
