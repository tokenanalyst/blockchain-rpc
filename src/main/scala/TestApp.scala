package io.tokenanalyst.jsonrpc

import cats.effect.{IO, IOApp, ExitCode}

object TestApp extends IOApp {
  val config = Config("http://172.31.32.41:8332", "tokenanalyst","")

  def run(args: List[String]): IO[ExitCode] = for { 
    block <- Simple.getBlock(config, "000000000000000000100ed429cdd0f596a35317d0c7f754cf6f4e5110ce6307")
    tx <- Simple.getTransaction(config, block.tx.head)
    _ <- IO { println(tx) }
  } yield ExitCode(0)
}