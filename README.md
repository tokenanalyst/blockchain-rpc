# bitcoin-rpc
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/202ed1ef51524b749560c0ffd78400f7)](https://www.codacy.com/manual/tokenanalyst/bitcoin-rpc?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=tokenanalyst/bitcoin-rpc&amp;utm_campaign=Badge_Grade)
<img src="https://typelevel.org/cats/img/cats-badge.svg" height="40px" align="right" alt="Cats friendly" /></a>


bitcoin-rpc is a typesafe bitcoind RPC client written in and to be used with Scala. Under the hood, it's using http4s, circe and cats-effect. It's in active development, but no official public release has been scheduled yet. We appreciate external contributions, please check issues for inspiration. 

For all examples, check: [src/main/scala/examples](https://github.com/tokenanalyst/bitcoin-rpc/tree/master/src/main/scala/examples).

## Example: Simple using hardcoded config

This is a simple example of how the RPCClient is generally used. We're using Cats Resources here which automatically deallocate any opened resources after use.

```scala
import cats.effect.{ExitCode, IO, IOApp}
import scala.concurrent.ExecutionContext.global

import io.tokenanalyst.bitcoinrpc.RPCClient
import io.tokenanalyst.bitcoinrpc.bitcoin.Syntax._

object GetBlockHash extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    implicit val ec = global
    RPCClient
      .bitcoin(
        hosts = Seq("127.0.0.1"),
        username = Some("user"),
        password = Some("password")
      )
      .use { bitcoin =>
        for {
          block <- bitcoin.getBlockByHash(
            "0000000000000000000...a581761ddc1d50a57358d"
          )
          _ <- IO { println(block) }
        } yield ExitCode(0)
      }
  }
}
```

## Example: Catch up from block zero

This example makes use of the EnvConfig import, which automatically configures RPC via ENV flags exported in the shell. The environment flags for it are BITCOIN_RPC_HOSTS, BITCOIN_RPC_USERNAME, BITCOIN_RPC_PASSWORD.

```scala
  import cats.effect.{ExitCode, IO, IOApp}
  import scala.concurrent.ExecutionContext.global
 
  import io.tokenanalyst.bitcoinrpc.Bitcoin
  import io.tokenanalyst.bitcoinrpc.RPCClient
  import io.tokenanalyst.bitcoinrpc.bitcoin.Syntax._
  import io.tokenanalyst.bitcoinrpc.EnvConfig._
  
  object CatchupFromZero extends IOApp {

    def loop(rpc: Bitcoin, current: Long = 0L, until: Long = 10L): IO[Unit] = 
    for {
        block <- rpc.getBlockByHeight(current)
        _ <- IO { println(block) }  
        l <- if(current + 1 < until) loop(rpc, current + 1, until) else IO.unit
    } yield l

    def run(args: List[String]): IO[ExitCode] = {
      implicit val ec = global
      RPCClient.bitcoin.use { rpc =>
        for {
            _ <- loop(rpc)
        } yield ExitCode(0)
      }
    }
  }
```

## Environment Variables

| variable  | description  | type |
|---|---|---|
| BITCOIN_RPC_HOSTS  | Comma-seperated IP list or hostname of full nodes | String |
| BITCOIN_RPC_USERNAME  | RPC username | Optional String |
| BITCOIN_RPC_PASSWORD  | RPC password | Optional String |
| BITCOIN_RPC_PORT  | RPC port when not default | Optional Int |
| BITCOIN_RPC_ZEROMQ_PORT  | ZeroMQ port when not default | Optional Int |

## Supported Bitcoin methods

| Bitcoind RPC methods  | description  |  bitcoin-rpc method |
|---|---|---|
| getblockhash  | Gets the block hash at a specific height  |  getBlockHash(height: Long) |
| getbestblockhash  |  Gets the block tip hash | getBestBlockHash()  |
| getblock  | Gets the block with transaction ids  | getBlockByHash(hash: String) |
| getblockhash, getblock  | Gets the block with transaction ids  |  getBlockByHeight(height: Long) |
| getrawtransaction | Gets raw transaction data | getTransaction(hash: String) |
| batch of getrawtransaction | Gets raw transaction data | getTransactions(hashes: Seq[String]) |
| estimatesmartfee | Estimates fee for include in block n | estimateSmartFee(height: Long) |
| usage of ZeroMQ | Gets next block hash subscription | getNextBlockHash() |
