namespace java thrift.test.files


service AsyncDevice {
string connect(),
oneway void disconnect(1: string key),
void setMessage(1: string key, 2: string message),
string getMessage(1: string key),
string program(1: string key, 2: list<binary> File, 3: list<i32> address 4: i64 timeout)

}