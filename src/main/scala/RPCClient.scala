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

import java.net.{ConnectException, SocketTimeoutException}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object RPCClient {

  def bitcoin(
      hosts: Seq[String],
      port: Option[Int] = None,
      username: Option[String] = None,
      password: Option[String] = None,
      zmqPort: Option[Int] = None,
      onErrorRetry: (Int, Throwable) => IO[Unit] = (_,_) => IO.unit 
  )(
      implicit ec: ExecutionContext,
      cs: ContextShift[IO]
  ): Resource[IO, Bitcoin] = {
    val config = Config(hosts, port, username, password, zmqPort)
    for (client <- make(config, onErrorRetry)) yield Bitcoin(client)
  }

  def ethereum(
      hosts: Seq[String],
      port: Option[Int] = None,
      username: Option[String] = None,
      password: Option[String] = None,
      zmqPort: Option[Int] = None,
      onErrorRetry: (Int, Throwable) => IO[Unit] = (_,_) => IO.unit 
  )(
      implicit ec: ExecutionContext,
      cs: ContextShift[IO]
  ): Resource[IO, Ethereum] = {
    val config = Config(hosts, port, username, password, zmqPort)
    for (client <- make(config, onErrorRetry)) yield Ethereum(client)
  }

  def omni(
      hosts: Seq[String],
      port: Option[Int] = None,
      username: Option[String] = None,
      password: Option[String] = None,
      zmqPort: Option[Int] = None,
      onErrorRetry: (Int, Throwable) => IO[Unit] = (_,_) => IO.unit 
  )(
      implicit ec: ExecutionContext,
      cs: ContextShift[IO]
  ): Resource[IO, Omni] = {
    val config = Config(hosts, port, username, password, zmqPort)
    for (client <- make(config, onErrorRetry)) yield Omni(client)
  }

  def make(config: Config, onErrorRetry: (Int, Throwable) => IO[Unit])(
      implicit ec: ExecutionContext,
      cs: ContextShift[IO]
  ): Resource[IO, RPCClient] = {
    for {
      client <- BlazeClientBuilder[IO](ec)
        .withConnectTimeout(5.seconds)
        .withRequestTimeout(2.minutes)
        .resource
      socket <- ZeroMQ.socket(
        config.hosts.head,
        config.zmqPort.getOrElse(28332)
      )
    } yield new RPCClient(client, socket, config, onErrorRetry)
  }
}

class RPCClient(
    client: Client[IO],
    zmq: ZeroMQ.Socket,
    config: Config,
    onErrorRetry: (Int, Throwable) => IO[Unit]
) extends Http4sClientDsl[IO] {

  // is blocking
  def nextBlockHash(): IO[String] = zmq.nextBlock()

  def request[A <: RPCRequest: Encoder, B <: RPCResponse: Decoder](
      request: A
  ): IO[B] = retry(config.hosts) { host =>
    for {
      req <- post(host, request)
      res <- client.expect[B](req)
    } yield res
  }

  def requestJson[A <: RPCRequest: Encoder](request: A): IO[Json] =
    retry(config.hosts) { host =>
      for {
        req <- post(host, request)
        res <- client.expect[Json](req)
      } yield res
    }

  private def post[A <: RPCRequest: Encoder](
      host: String,
      request: A
  ): IO[Request[IO]] = {
    val uri = Uri
      .fromString(s"http://${host}:${config.port.getOrElse(8332)}")
      .getOrElse(throw new Exception("Could not parse URL"))
    (config.username, config.password) match {
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

  def retry[A](fallbacks: Seq[String], current: Int = 0, max: Int = 10)(
      f: String => IO[A]
  ): IO[A] = {
    val hostId = current % fallbacks.size
    val handle = (e: Exception) => {
      if (current <= max) for {
        _ <- onErrorRetry(hostId, e) 
        r <- retry(fallbacks, current + 1, max)(f)
      } yield r
      else
        IO.raiseError(new Exception(s"Running out of retries for: ${e}"))
    }
    f(fallbacks(hostId)).handleErrorWith {
      case e: org.http4s.client.UnexpectedStatus => handle(e)
      case e: ConnectException                   => handle(e)
      case e: SocketTimeoutException             => handle(e)
      case e                                     => IO.raiseError(e)
    }
  }
}
