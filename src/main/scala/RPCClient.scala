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

import cats.effect.{ContextShift, IO, Resource}

import io.circe.{Decoder, Encoder, Json}
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.dsl.io._
import org.http4s.headers.{Authorization, _}
import org.http4s.{BasicCredentials, MediaType, Request, Uri}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object RPCClient {

  def bitcoin(
      host: String,
      port: Option[Int] = None,
      username: Option[String] = None,
      password: Option[String] = None,
      zmqPort: Option[Int] = None
  )(
      implicit ec: ExecutionContext,
      cs: ContextShift[IO]
  ): Resource[IO, Bitcoin] = {
    val config = Config(host, port, username, password, zmqPort)
    for (client <- make(config)) yield Bitcoin(client)
  }

  def bitcoin(
      implicit ec: ExecutionContext,
      config: Config,
      cs: ContextShift[IO]
  ): Resource[IO, Bitcoin] = {
    for (client <- make(config)) yield Bitcoin(client)
  }

  def make(config: Config)(
      implicit ec: ExecutionContext,
      cs: ContextShift[IO]
  ): Resource[IO, RPCClient] = {
    for {
      client <- BlazeClientBuilder[IO](ec)
        .withConnectTimeout(2.minutes)
        .resource
      socket <- ZeroMQ.socket(config.host, config.zmqPort.getOrElse(28332))
    } yield new RPCClient(client, socket, config)
  }
}

class RPCClient(client: Client[IO], zmq: ZeroMQ.Socket, config: Config)
    extends Http4sClientDsl[IO] {
  val uri = Uri
    .fromString(s"http://${config.host}:${config.port.getOrElse(8332)}")
    .getOrElse(throw new Exception("Could not parse URL"))

  // is blocking
  def nextBlockHash(): IO[String] = zmq.nextBlock()

  def request[A <: RPCRequest: Encoder, B <: RPCResponse: Decoder](
      request: A
  ): IO[B] =
    for {
      req <- post(request)
      res <- client.expect[B](req)
    } yield res

  def requestJson[A <: RPCRequest: Encoder](request: A): IO[Json] =
    for {
      req <- post(request)
      res <- client.expect[Json](req)
    } yield res

  private def post[A <: RPCRequest: Encoder](
      request: A
  ): IO[Request[IO]] = (config.username, config.password) match {
    case (Some(user), Some(pass)) =>
      POST(
        request,
        uri,
        Authorization(BasicCredentials(user, pass)),
        Accept(MediaType.application.json)
      )
    case _ =>
      POST(
        request,
        uri,
        Accept(MediaType.application.json)
      )
  }
}
