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
package io.tokenanalyst.blockchainrpc.omni

import io.tokenanalyst.blockchainrpc.BatchResponse
import io.tokenanalyst.blockchainrpc.Omni
import io.tokenanalyst.blockchainrpc.BasicMethods._
import io.tokenanalyst.blockchainrpc.OmniMethods._
import io.tokenanalyst.blockchainrpc.omni.Instances._

import Protocol._

object Syntax {
  implicit class OmniOps(omni: Omni) {
    def listBlockTransactions(height: Long) =
      implicitly[ListBlockTransactions].listBlockTransactions(omni, height)

    def getTransaction(hash: String) =
      implicitly[GetTransaction[Omni, TransactionResponse]].getTransaction(omni, hash)

    def getTransactions(hashes: Seq[String]) =
      implicitly[GetTransactions[Omni, BatchResponse[TransactionResponse]]]
      .getTransactions(omni, hashes)

    def getNextBlockHash() =
      implicitly[GetNextBlockHash[Omni]].getNextBlockHash(omni)

    def getBlockByHeight(height: Long) =
      implicitly[GetBlockByHeight[Omni, BlockResponse]]
        .getBlockByHeight(omni, height)

    def getBestBlockHash() =
      implicitly[GetBestBlockHash[Omni]].getBestBlockHash(omni)

    def getBestBlockHeight() =
      implicitly[GetBestBlockHeight[Omni]].getBestBlockHeight(omni)

    def getBlockByHash(hash: String) =
      implicitly[GetBlockByHash[Omni, BlockResponse]].getBlockByHash(omni, hash)
  }
}
