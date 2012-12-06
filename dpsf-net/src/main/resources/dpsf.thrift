namespace java com.dianping.dpsf.protocol.thrift

struct Request{

	1: required i32 messageType
	2: i32 callType
	3: required i64  transferId
	4: string rpcVersion
	5: i32  timeout
	
	6: string serviceName
	7: string methodName
	8: binary  parameters
	9: string argsClassName
}

struct Response{

	1: required i32 messageType
	2: required i64  transferId
	3: string rpcVersion
	
	4: binary  returnVal
	5: string cause

}