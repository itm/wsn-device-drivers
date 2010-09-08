
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;


public class TCP_Server {
	
	public static void main(String[] args) throws TTransportException{
		
		try{
			// Erstellen des nicht blockierenden Sockets
			final TNonblockingServerSocket socket = new TNonblockingServerSocket(50000);
			
			// Festlegen des Serialisierungsprotokolls
			final TProtocolFactory protocolFactory = new TCompactProtocol.Factory();
			
			// Uebergeben der implementierten Methoden an den Thrift-Service
			final TestService.Processor processor = new TestService.Processor(new Handler());
			
			// Erstellen des Servers
			final TServer server = new THsHaServer(processor, socket,
                    new TFramedTransport.Factory(), protocolFactory);
			
			// Ein wenig Kommunikation
			System.out.println("Ich bin der Server!");
			
			//starten des Servers und warten auf Clients
			server.serve();
			
		} catch (TTransportException e) {
			e.printStackTrace();
		}
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
		public String getString() throws TException {
			//Ausgabe einer Nachricht auf Client-Seite
			//String wird zum Client uebertragen
			
			//wahrscheinlich blockierender Aufruf!!
			
			return "Ich bin eine Nachricht vom Server";
		}
		
	}
}

