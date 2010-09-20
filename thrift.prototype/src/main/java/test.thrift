namespace java thrift.prototype.files

exception LoginFailed {
  1: string message
}

service AsyncDevice {
string connect(1: string userName, 2: string passWord) throws (1: LoginFailed lf),
oneway void disconnect(1: string key),
void setMessage(1: string key, 2:string OperationHandleKey, 3: string message),
string getMessage(1: string key, 2: string OperationHandleKey),
void program(1: string key, 2: string OperationHandleKey, 3: list<binary> bytes, 4: string description 5: i64 timeout),
oneway void HandleCancel(1: string key, 2: string OperationHandleKey),
void HandleGet(1: string key, 2: string OperationHandleKey),
string HandleGetState(1: string key, 2: string OperationHandleKey)
}

