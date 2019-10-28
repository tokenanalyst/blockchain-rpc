package io.tokenanalyst.jsonrpc

import cats.effect.{ExitCode, IO, IOApp}

import scala.util.{Failure, Success}

object TestApp extends IOApp {

  def getConfig = {
    (sys.env.get("PASSWORD"), sys.env.get("USER"), sys.env.get("RPC_URL")) match {
      case (Some(pass), Some(user), Some(url)) =>
        Success(Config(url, user, pass))
      case _ => Failure(new Exception("Pass RPC_URL, USER, PASSWORD."))
    }
  }

  def loop(socket: ZeroMQ.Socket): IO[Unit] =
    for {
      msg <- IO(socket.nextBlock())
      _ <- IO(println(msg))
      next <- loop(socket)
    } yield next

  def run(args: List[String]): IO[ExitCode] =
    ZeroMQ.socket("172.31.32.41", 28332).use { socket =>
      for {
        _ <- loop(socket)
      } yield ExitCode(0)
    }
}
