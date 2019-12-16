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
package io.tokenanalyst.blockchainrpc.examples.ethereum

import cats.effect.{ExitCode, IO, IOApp}
import io.tokenanalyst.blockchainrpc.ethereum.Syntax._
import io.tokenanalyst.blockchainrpc.{Config, RPCClient}

import scala.concurrent.ExecutionContext.global

object GetReceiptByHash extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    implicit val ec = global
    implicit val config = Config.fromEnv
    RPCClient
      .ethereum(
        config.hosts,
        config.port,
        config.username,
        config.password,
        onErrorRetry = { (_, e: Throwable) =>
          IO(println(e))
        }
      )
      .use { ethereum =>
        for {
          tx <- ethereum.getReceiptByHash(
            "0x218b632d932371478d1ae5a01620ebab1a2030f9dad6f8fba4a044ea6335a57e"
          )
          _ <- IO { println(tx) }
        } yield ExitCode(0)
      }
  }
}
