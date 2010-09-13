package thrift.test.async.server;


import org.apache.thrift.TException;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;

import tserver.gen.TimeServer;


public class TCP_Server {
	
	public static void main(String[] args) throws TTransportException{
        
		   // Erstellen eines nicht blockierenden Servers
	    final TNonblockingServer s = new TNonblockingServer(new TimeServer.Processor(new Handler()), new TNonblockingServerSocket(12345));
	    // erstellen eines neuen Reaktions-Threads
	    new Thread(new Runnable() {
	      @Override
	      public void run() {
	    	// Ein wenig Kommunikation
			System.out.println("Ich bin der Server!");
			// starten des Servers
	        s.serve();
	      }
	    }).start(); // starten des Threads
	}
	
	//Implementierung der im Thrift-IDL festgelegten Services
	//TimeServer.Iface ist das dazugehoerige Interface
	private static class Handler implements TimeServer.Iface{

		@Override
		public long time() throws TException {
			
			return System.currentTimeMillis();
		}
	}
}

