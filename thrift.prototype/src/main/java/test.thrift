namespace java thrift.prototype.files

struct MessagePacket{
	1: i32 type
	2: binary content
}

exception LoginFailed {
  1: string message
}

service AsyncDevice {
string connect(1: string userName, 2: string passWord) throws (1: LoginFailed lf),
oneway void disconnect(1: string key),

void setMessage(1: string key, 2:string OperationHandleKey, 3: string message),
string getMessage(1: string key, 2: string OperationHandleKey),

void program(1: string key, 2: string OperationHandleKey, 3: list<binary> bytes, 4: string description 5: i64 timeout),

void eraseFlash(1: string key, 2:string OperationHandleKey, 3: i64 timeout),
list<binary> readFlash(1: string key, 2:string OperationHandleKey, 3:i32 address, 4:i32 length, 5:i64 timeout),
binary readMac(1: string key, 2:string OperationHandleKey, 3:i64 timeout),
void reset(1: string key, 2:string OperationHandleKey, 3:i64 timeout),
void send(1: string key, 2:string OperationHandleKey, 3:MessagePacket packet, 4:i64 timeout),		
void writeFlash(1: string key, 2:string OperationHandleKey, 3:i32 address, 4:binary data, 5:i32 length, 6:i64 timeout),
void writeMac(1: string key, 2:string OperationHandleKey, 3:binary macAddress, 4:i64 timeout),

oneway void HandleCancel(1: string key, 2: string OperationHandleKey),
void HandleGet(1: string key, 2: string OperationHandleKey),
string HandleGetState(1: string key, 2: string OperationHandleKey),

list<binary> HandleGetReadFlash(1: string key, 2: string OperationHandleKey),
binary HandleGetReadMac(1: string key, 2: string OperationHandleKey)
}

// void addMessagePacketListener(MessagePacketListener listener, PacketType... types)
// void addMessagePacketListener(MessagePacketListener listener, int... types)
// void removeMessagePacketListener(MessagePacketListener listener)

// Methoden zur Administration des TCP-Servers (2-3 Stueck)
