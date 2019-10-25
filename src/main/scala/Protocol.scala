package io.tokenanalyst.jsonrpc

object Protocol {
    sealed trait RPCResponse
    case class FeeResponse(feerate: Double, blocks: Int) extends RPCResponse
    case class BestBlockHashResponse(hash: String) extends RPCResponse
    case class BlockResponse(
      height: Long, 
      hash: String,
      previousblockhash: String,
      nonce: Long,
      strippedsize: Long,
      merkleroot: String,
      version: Int,
      weight: Int,
      difficulty: Double,
      chainwork: String,
      bits: String,
      size: Long,
      mediantime: Long,
      time: Long,
      nTx: Int, 
      tx: List[String]
    ) extends RPCResponse

    case class TransactionResponseVin(
      coinbase: String,
      sequence: Long
    )
    
    case class TransactionResponseScript(
      asm: String,
      hex: String, 
      reqSigs: Option[Int],
      `type`: String,
      addresses: Option[List[String]]
    )

    case class TransactionResponseVout(
      value: Double,
      n: Int,
      scriptPubKey: TransactionResponseScript
    )

    case class TransactionResponse(
      confirmations: Int,
      blockhash: String,
      blocktime: Long,
      hex: String,
      txid: String,
      time: Long,
      vsize: Int,
      size: Int,
      weight:Int,
      vin: List[TransactionResponseVin],
      vout: List[TransactionResponseVout],
      locktime: Long
    ) extends RPCResponse
  
    sealed trait RPCRequest
    case class FeeRequest(blocks: Int) extends RPCRequest
    case class BlockRequest(hash: String) extends RPCRequest
    case class TransactionRequest(hash: String) extends RPCRequest
    case class BestBlockHashRequest() extends RPCRequest
  }
  