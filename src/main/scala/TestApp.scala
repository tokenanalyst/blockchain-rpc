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
          block <- BitcoinRPC.getBestBlockHeight(client)
          _ <- IO { println(block) }
        } yield ExitCode(0)
    }
  }
}