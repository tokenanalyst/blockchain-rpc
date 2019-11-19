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
import scala.concurrent.ExecutionContext.global

import io.tokenanalyst.bitcoinrpc.RPCClient
import io.tokenanalyst.bitcoinrpc.bitcoin.Syntax._

object SubscribeToBlockUpdates extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    implicit val ec = global
    RPCClient
      .bitcoin(
        hosts = Seq("127.0.0.1"),
        username = Some("user"),
        password = Some("password")
      )
      .use { bitcoin =>
        for {
          hash <- bitcoin.getNextBlockHash()
          block <- bitcoin.getBlockByHash(hash)
          _ <- IO { println(block) }
        } yield ExitCode(0)
      }
  }
}
