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

import cats.effect.{ExitCode, IO, IOApp}
import scala.concurrent.ExecutionContext.global
import scala.util.{Failure, Success}

object TestApp extends IOApp {

  def getConfig = {
    (sys.env.get("PASSWORD"), sys.env.get("USER"), sys.env.get("HOST")) match {
      case (Some(pass), Some(user), Some(host)) =>
        Success(Config(host, user, pass))
      case _ => Failure(new Exception("Pass HOST, USER, PASSWORD."))
    }
  }

  def loop(socket: ZeroMQ.Socket): IO[Unit] =
    for {
      msg <- IO(socket.nextBlock())
      _ <- IO(println(msg))
      next <- loop(socket)
    } yield next

  def run(args: List[String]): IO[ExitCode] = {
    implicit val config = getConfig.get
    implicit val ec = global
    BitcoinRPC.openAll().use {
      case (client,_) =>
        for {
          block <- BitcoinRPC.getTransaction(client, "787e26b4886a7e67062bec2b6a1934639ad09f5e8060ed950b402044722af8fa")
          _ <- IO { println(block) }
        } yield ExitCode(0)
    }
  }
}
