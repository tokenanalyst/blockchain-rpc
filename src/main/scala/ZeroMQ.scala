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

import java.nio.ByteBuffer

import cats.effect.{IO, Resource}
import org.zeromq.{SocketType, ZContext, ZMQ, ZMsg}

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

  class Socket(host: String, port: Int) extends AutoCloseable {
    val context = new ZContext()
    val socket: ZMQ.Socket = context.createSocket(SocketType.SUB)
    //http://api.zeromq.org/2-1:zmq-setsockopt
    socket.setHWM(0)
    socket.subscribe(HASH_BLOCK)
    socket.connect(f"tcp://$host:$port")

    def nextBlock() = {
      val msg = ZMsg.recvMsg(socket)
      messageFromZMsg(msg).body
    }

    override def close() = context.close()
  }

  def socket(host: String, port: Int): Resource[IO, Socket] =
    Resource.make {
      IO(new Socket(host, port))
    } { socket =>
      IO(socket.close()).handleErrorWith(_ => IO.unit)
    }
}
