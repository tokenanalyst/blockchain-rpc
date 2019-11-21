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

package io.tokenanalyst.bitcoinrpc.examples

import cats.effect.{ExitCode, IO, IOApp}
import io.tokenanalyst.bitcoinrpc.{Config, RPCClient}
import io.tokenanalyst.bitcoinrpc.omni.Syntax._

import scala.concurrent.ExecutionContext.global

object OmniGetBlockTransactions extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    implicit val ec = global
    val config = Config.fromEnv
    RPCClient.omni(
      config.hosts,
      config.port,
      config.username,
      config.password, 
    ).use { omni =>
      for {
        bestBlockHeight <- omni.getBestBlockHeight()
        _ <- IO(println(s"best block: $bestBlockHeight"))
        txs <- omni.listBlockTransactions(bestBlockHeight)
        values <- omni.getTransactions(txs)
        _ <- IO {println(s"sending addresses: ${values.seq.map(_.sendingaddress)}")}
      } yield ExitCode.Success
    }
  }
}
