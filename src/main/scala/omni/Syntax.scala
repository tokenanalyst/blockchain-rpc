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

import io.tokenanalyst.bitcoinrpc.Omni
import io.tokenanalyst.bitcoinrpc.OmniMethods._
import io.tokenanalyst.bitcoinrpc.omni.Instances._

object Syntax {
  implicit class OmniOps(omni: Omni) {
    def listBlockTransactions(height: Long) = {
      implicitly[ListBlockTransactions].listBlockTransactions(omni, height)
    }

    def getTransaction(hash: String) = {
      implicitly[GetTransaction].getTransaction(omni, hash)
    }

    def getTransactions(hashes: Seq[String]) = {
      implicitly[GetTransactions].getTransactions(omni, hashes)
    }

    def getBestBlockHash() =
      implicitly[GetBestBlockHash].getBestBlockHash(omni)

    def getBestBlockHeight() =
      implicitly[GetBestBlockHeight].getBestBlockHeight(omni)

    def getBlockByHash(hash: String) =
      implicitly[GetBlockByHash].getBlockByHash(omni, hash)
  }
}
