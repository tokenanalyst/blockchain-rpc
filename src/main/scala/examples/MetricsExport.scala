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

import cats.effect._
import cats.effect.{ExitCode, IO, IOApp}
import scala.concurrent.ExecutionContext.global

import io.tokenanalyst.bitcoinrpc.Bitcoin
import io.tokenanalyst.bitcoinrpc.{RPCClient, Config}
import io.tokenanalyst.bitcoinrpc.bitcoin.Syntax._
import org.http4s.implicits._

import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.{Router, Server}
import org.http4s.metrics.prometheus.PrometheusExportService

object MetricsExport extends IOApp {

  def loop(rpc: Bitcoin, current: Long = 0L, until: Long = 10L): IO[Unit] =
    for {
      block <- rpc.getBlockByHeight(current)
      _ <- IO { println(block) }
      l <- if (current + 1 < until) loop(rpc, current + 1, until) else IO.unit
    } yield l

  def run(args: List[String]): IO[ExitCode] = {
    implicit val ec = global
    implicit val config = Config.fromEnv
    val res = for {
      rpc <- RPCClient
        .bitcoin(config.hosts, config.port, config.username, config.password)
      server <- BlazeServerBuilder[IO]
        .bindHttp(8080)
        .withHttpApp(
          Router(
            "/" -> PrometheusExportService[IO](rpc.client.metrics).routes
          ).orNotFound
        )
        .resource
    } yield (rpc, server)
    res.use {
      case (bitcoin: Bitcoin, _: Server[IO]) =>
        for {
          _ <- loop(bitcoin)
        } yield ExitCode(0)
    }
  }
}
