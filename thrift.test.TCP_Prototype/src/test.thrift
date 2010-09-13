namespace java thrift.test.files

struct Person{
1: string Name,
2: i32 alter

}

service TestService {
void sayHello(),
i32 getInt(),
Person getString() 
}