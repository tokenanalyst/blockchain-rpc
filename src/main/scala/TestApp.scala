package io.tokenanalyst.jsonrpc

import scala.util.{Success, Failure}
import cats.effect.{IO, IOApp, ExitCode}

object TestApp extends IOApp {

  def getConfig = {
    (sys.env.get("PASSWORD"), sys.env.get("USER"), sys.env.get("RPC_URL")) match {
      case (Some(pass), Some(user), Some(url)) =>
        Success(Config(url, user, pass))
      case _ => Failure(new Exception("Pass RPC_URL, USER, PASSWORD."))
    }
  }

  def run(args: List[String]): IO[ExitCode] = 
    for {
      config <- IO.fromTry(getConfig)
      block <- Simple.getBlock(
        config,
        "000000000000000000100ed429cdd0f596a35317d0c7f754cf6f4e5110ce6307"
      )
      tx <- Simple.getTransaction(config, block.tx.head)
      _ <- IO { println(tx) }
    } yield ExitCode(0)
}
