import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;


public class TCP_Client {

	public static void main (String[] args){
		
		// Erstellen des Clients mit IP und port
		final TSocket socket = new TSocket("localhost", 50000);
		
		// // Festlegen des Transport-Formats (Bufferformat und Groese)
		final TTransport transport = new TFramedTransport(socket);
		
		// Festlegen des Serialisierungsprotokolls
        final TProtocol protocol = new TCompactProtocol(transport);
        
        // Uebergeben an Client
        final TestService.Client client = new TestService.Client(protocol);
        
        try {
        	// Oeffnen der Verbindung, unbedingt notwendig
			transport.open();
			
			// Ein wenig Kommunikation
			System.out.println("Ich bin der Client!");
			
			// Entfernter Methodenaufruf
			client.sayHello();
			
			// Entfernter Methodenaufruf mit Rueckgabewert
			// Wahrscheinlich blockierend
			System.out.println(client.getString());
			
		} catch (TTransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	
}
