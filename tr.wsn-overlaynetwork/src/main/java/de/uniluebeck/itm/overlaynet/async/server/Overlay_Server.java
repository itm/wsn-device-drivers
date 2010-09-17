package de.uniluebeck.itm.overlaynet.async.server;



import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;

import de.uniluebeck.itm.overlaynet.OverlayServer;
import de.uniluebeck.itm.overlaynet.OverlayServerImpl;





public class Overlay_Server {
	
	public static void main(String[] args) throws TTransportException{
        
		   // Erstellen eines nicht blockierenden Servers
	    final TNonblockingServer s = new TNonblockingServer(new OverlayServer.Processor(new OverlayServerImpl()), new TNonblockingServerSocket(7911));
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
}

