# 2.5.2

* changed Omni schema, propertyid is now Long

# 2.5.1 

* changed Ethereum syntax got rid of getNextBlockHash (not implemented yet)

# 2.5.0

* added Ethereum 
* added Ethereum block and transactions codec and syntax

# 2.4.0

* added Omni field confirmations
* added Omni field invalidreason

# 2.3.0

* removed implicit config
* add optional onRetryError: (hostId, exception) => IO.unit callback

# 2.2.0

* handle ConnectException with failover

# 2.1.0

* changed env flag for hosts BITCOIN_RPC_HOST -> BITCOIN_RPC_HOSTS

# 2.0.0

* added feature to add multiple hosts as fallback (not getNextBlockHash yet)
* interface changed for instantiating Bitcoin etc. provide sequence of fallbacks

# 1.22

* added getNextBlockHash for Omni
* added getBlockByHeight for Omni

# 1.21

* removed logback.xml from resources

# 1.20

* added omni methods
* added new API interface, resources etc.
* added EnvConfig for fast configuration 
* added optional parameters to make function
* added various examples for usage
* added more details in README.md

# 1.7

* added batching of requests
* added getTransactions method

# 1.6

* added logging subsystem, log to DEBUG

# 1.5

* added input fields txid, vout, scriptSig for TransactionResponse inputs  
* changed coinbase fields to be optional

# 1.4

* added version and hash to TransactionResponse

# 1.3

* added getBlock by height
