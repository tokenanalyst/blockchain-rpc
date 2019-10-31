package io.tokenanalyst.bitcoinrpc

import java.io.Closeable
import java.nio.ByteBuffer

import cats.effect.{IO, Resource}
import org.zeromq._

object ZeroMQ {

  val HASH_BLOCK = "hashblock"
  val HASH_TX = "hashtx"
  val RAW_BLOCK = "rawblock"
  val RAW_TX = "rawtx"

  case class message(topic: String, body: String, sequence: Int)

  def messageFromZMsg(zMsg: ZMsg) = {
    val topic = zMsg.popString()
    val body = zMsg.popString()
    val seq = ByteBuffer.wrap(zMsg.pop().getData.reverse).getInt

    message(topic, body, seq)
  }

  class Socket(host: String, port: Int)
      extends Closeable {
    val context = new ZContext()
    val socket: ZMQ.Socket = context.createSocket(SocketType.SUB)
    //http://api.zeromq.org/2-1:zmq-setsockopt
    socket.setHWM(0)
    socket.subscribe(HASH_BLOCK)
    socket.connect(f"tcp://$host:$port")

    def nextBlock(): String = {
      val msg = ZMsg.recvMsg(socket)
      messageFromZMsg(msg).body
    }

    override def close() = {
      println("closing...")
      context.close()
    }
  }

  def socket(host: String, port: Int): Resource[IO, Socket] =
    Resource.fromAutoCloseable(IO(new Socket(host, port)))
}
