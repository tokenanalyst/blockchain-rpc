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
package io.tokenanalyst.blockchainrpc

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
    hosts: Seq[String],
    port: Option[Int],
    username: Option[String],
    password: Option[String],
    zmqPort: Option[Int]
)

object Config {
  val PasswordEnv = "BLOCKCHAIN_RPC_PASSWORD"
  val UsernameEnv = "BLOCKCHAIN_RPC_USERNAME"
  val HostEnv = "BLOCKCHAIN_RPC_HOSTS"
  val PortEnv = "BLOCKCHAIN_RPC_PORT"
  val ZMQPortEnv = "BLOCKCHAIN_RPC_ZEROMQ_PORT"

  val fromEnv: Config = {
    Seq(HostEnv, PortEnv, UsernameEnv, PasswordEnv, ZMQPortEnv)
      .map(sys.env.get(_)) match {
      case Seq(None, _, _, _, _) =>
        throw new Exception("Pass at least BLOCKCHAIN_RPC_HOSTS.")
      case Seq(Some(h), port, user, pass, zmqPort) =>
        Config(
          h.split(",").toIndexedSeq,
          port.map(_.toInt),
          user,
          pass,
          zmqPort.map(_.toInt)
        )
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
