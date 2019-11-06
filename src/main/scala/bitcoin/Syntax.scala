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

import io.tokenanalyst.bitcoinrpc.{Bitcoin, BasicMethods}

import Instances._
import BasicMethods._
import Protocol._

object Syntax {
  implicit class BitcoinOps(b: Bitcoin) {

    def getNextBlockHash() = 
      implicitly[GetNextBlockHash[Bitcoin]].getNextBlockHash(b)

    def getBlockByHash(hash: String) =
      implicitly[GetBlockByHash[Bitcoin, BlockResponse]].getBlockByHash(b, hash)

    def getBlockByHeight(height: Long) =
      implicitly[GetBlockByHeight[Bitcoin, BlockResponse]]
        .getBlockByHeight(b, height)

    def getBlockHash(height: Long) =
      implicitly[GetBlockHash[Bitcoin]].getBlockHash(b, height)

    def getBestBlockHash() =
      implicitly[GetBestBlockHash[Bitcoin]].getBestBlockHash(b)

    def getBestBlockHeight() =
      implicitly[GetBestBlockHeight[Bitcoin]].getBestBlockHeight(b)

    def getTransactions(hashes: Seq[String]) =
      implicitly[GetTransactions[Bitcoin, BatchResponse[TransactionResponse]]]
        .getTransactions(b, hashes)

    def getTransaction(hash: String) =
      implicitly[GetTransaction[Bitcoin, TransactionResponse]]
        .getTransaction(b, hash)

    def estimateSmartFee(height: Long) =
      implicitly[EstimateSmartFee[Bitcoin, FeeResponse]]
        .estimateSmartFee(b, height)
  }
}
