package io.tokenanalyst.jsonrpc

import org.http4s.circe.CirceEntityDecoder._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.dsl.io._
import org.http4s.headers.{Authorization, _}
import org.http4s.{BasicCredentials, MediaType, Request}
import io.circe.{Decoder, Encoder, Json}
import cats.effect.{IO, ContextShift}
import io.circe.syntax._
import Protocol._
import org.http4s.Uri
import org.http4s.client.blaze.BlazeClientBuilder

trait Connection {
  def request[A <: RPCRequest: Encoder, B <: RPCResponse: Decoder](
      client: Client[IO],
      request: A
  ): IO[B]

  def requestJson[A <: RPCRequest: Encoder](
      client: Client[IO],
      request: A
  ): IO[Json]
}

case class Config(url: String, user: String, password: String)

object Simple {
  import RPCEncoders._
  import RPCDecoders._
  import io.circe.generic.auto._

  def getBlock(config: Config, hash: String)(
      implicit cs: ContextShift[IO]
  ): IO[BlockResponse] = withClient { client =>
    RPC.request[BlockRequest, BlockResponse](client, config, BlockRequest(hash))
  }
  def getTransaction(config: Config, hash: String)(
      implicit cs: ContextShift[IO]
  ): IO[TransactionResponse] = withClient { client =>
    RPC.request[TransactionRequest, TransactionResponse](
      client,
      config,
      TransactionRequest(hash)
    )
  }
  def withClient[A](f: Client[IO] => IO[A])(implicit cs: ContextShift[IO]) = {
    import scala.concurrent.ExecutionContext.global
    BlazeClientBuilder[IO](global).resource.use { client =>
      f(client)
    }
  }
}

object RPC extends Http4sClientDsl[IO] {

  def post[A <: RPCRequest: Encoder](
      config: Config,
      request: A
  ): IO[Request[IO]] = {
    (for {
      url <- Uri.fromString(config.url)
      p <- Right(
        POST(
          request,
          url,
          Authorization(
            BasicCredentials
              .apply(config.user, config.password)
          ),
          Accept(MediaType.application.json)
        )
      )
      _ <- Right(println(request.asJson))
    } yield p)
      .getOrElse(throw new Exception("No proper exception handling yet"))
  }

  def request[A <: RPCRequest: Encoder, B <: RPCResponse: Decoder](
      client: Client[IO],
      config: Config,
      request: A
  ): IO[B] =
    for {
      req <- post(config, request)
      res <- client.expect[B](req)
    } yield res

  def requestJson[A <: RPCRequest: Encoder, B <: RPCResponse: Decoder](
      client: Client[IO],
      config: Config,
      request: A
  ): IO[Json] =
    for {
      req <- post(config, request)
      res <- client.expect[Json](req)
    } yield res

}
