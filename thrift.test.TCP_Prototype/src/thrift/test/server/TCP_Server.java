package thrift.test.server;


import org.apache.thrift.TException;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;


import thrift.test.files.Person;
import thrift.test.files.TestService;



public class TCP_Server {
	
	public static void main(String[] args) throws TTransportException, InterruptedException{
		
		
		   // put up a server
	    final TNonblockingServer s = new TNonblockingServer(new TestService.Processor(new Handler()), new TNonblockingServerSocket(50000));
	    new Thread(new Runnable() {
	      @Override
	      public void run() {
	    	// Ein wenig Kommunikation
			System.out.println("Ich bin der Server!");
	        s.serve();
	      }
	    }).start();
	    Thread.sleep(1000);
	}
	
	//Implementierung der im Thrift-IDL festgelegten Services
	//TestService.Iface ist das dazugehoerige Interface
	private static class Handler implements TestService.Iface{
		
		// Implementierung des sayHello Services
		@Override
		public void sayHello() throws TException {
			//Ausgabe einer Nachricht auf Server-Seite
			//reiner Methodenaufruf, keine Nachricht an den Client
			System.out.println("Hello World");
		}

		// Implementierung des getString Services
		@Override
		public Person getString() throws TException {
			//Ausgabe einer Nachricht auf Client-Seite
			//String wird zum Client uebertragen
			
			System.out.println("Ich bin in getString");
			
			Person test = new Person();
			test.alter = 12;
			test.Name = "Test";
			
			return test;
			//return "Ich bin eine Nachricht vom Server";
		}

		@Override
		public int getInt() throws TException {
			return 3;
		}
		
	}
}

