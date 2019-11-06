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

import cats.effect.IO
import io.circe.Json

trait RPCResponse
trait RPCRequest

case class BatchResponse[A](seq: Seq[A]) extends RPCResponse
case class BatchRequest[A](seq: Seq[A]) extends RPCRequest

trait RPCEncoder[A] {
  def apply(a: A): Json
}

trait RPCDecoder[A] {
  def apply(a: A): Json
}

case class Config(
    host: String,
    user: String,
    password: String,
    port: Option[Int] = None,
    zmqPort: Option[Int] = None
)

object EnvConfig { 
  implicit val config: Config = 
    (sys.env.get("PASSWORD"), sys.env.get("USER"), sys.env.get("HOST")) match {
      case (Some(pass), Some(user), Some(host)) =>
        Config(host, user, pass)
      case _ => throw new Exception("Pass HOST, USER, PASSWORD.")
    }
}

sealed trait Blockchain
case class Bitcoin(client: RPCClient) extends Blockchain
case class Omni(client: RPCClient) extends Blockchain

object BasicMethods {
  trait GetNextBlockHash[A <: Blockchain] { 
    def getNextBlockHash(a: A): IO[String]
  }

  trait GetBlockByHash[A <: Blockchain, B] {
    def getBlockByHash(a: A, hash: String): IO[B]
  }

  trait GetBlockByHeight[A <: Blockchain, B] {
    def getBlockByHeight(a: A, height: Long): IO[B]
  }

  trait GetBlockHash[A <: Blockchain] {
    def getBlockHash(a: A, height: Long): IO[String]
  }

  trait GetBestBlockHash[A <: Blockchain] {
    def getBestBlockHash(a: A): IO[String]
  }

  trait GetBestBlockHeight[A <: Blockchain] {
    def getBestBlockHeight(a: A): IO[Long]
  }

  trait GetTransactions[A <: Blockchain, B] {
    def getTransactions(a: A, hashes: Seq[String]): IO[B]
  }

  trait GetTransaction[A <: Blockchain, B] {
    def getTransaction(a: A, hash: String): IO[B]
  }

  trait EstimateSmartFee[A <: Blockchain, B] {
    def estimateSmartFee(a: A, height: Long): IO[B]
  }
}
