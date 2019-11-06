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
import io.tokenanalyst.bitcoinrpc.omni.Protocol.{BlockResponse, TransactionResponse}

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
    port: Option[Int],
    username: Option[String],
    password: Option[String],
    zmqPort: Option[Int]
)

object EnvConfig {
  val PasswordEnv = "BITCOIN_RPC_PASSWORD"
  val UsernameEnv = "BITCOIN_RPC_USERNAME"
  val HostEnv = "BITCOIN_RPC_HOST"
  val PortEnv = "BITCOIN_RPC_PORT"
  val ZMQPortEnv = "BITCOIN_RPC_ZEROMQ_PORT"

  implicit val config: Config = {
    Seq(HostEnv, PortEnv, UsernameEnv, PasswordEnv, ZMQPortEnv)
      .map(sys.env.get(_)) match {
      case Seq(None, _, _, _, _) =>
        throw new Exception("Pass at least BITCOIN_RPC_HOST.")
      case Seq(Some(h), port, user, pass, zmqPort) =>
        Config(h, port.map(_.toInt), user, pass, zmqPort.map(_.toInt))
    }
  }
}

sealed trait Blockchain
case class Bitcoin(client: RPCClient) extends Blockchain
case class Omni(client: RPCClient) extends Blockchain

object OmniMethods {
  trait ListBlockTransactions {
    def listBlockTransactions(omni: Omni, height: Long): IO[Seq[String]]
  }

  trait GetTransaction {
    def getTransaction(omni: Omni, hash: String): IO[TransactionResponse]
  }

  trait GetTransactions {
    def getTransactions(
        omni: Omni,
        hashes: Seq[String]
    ): IO[BatchResponse[TransactionResponse]]
  }

  trait GetBestBlockHash {
    def getBestBlockHash(omni: Omni): IO[String]
  }

  trait GetBlockByHash {
    def getBlockByHash(omni: Omni, hash: String): IO[BlockResponse]
  }

  trait GetBestBlockHeight {
    def getBestBlockHeight(omni: Omni): IO[Long]
  }
}

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
