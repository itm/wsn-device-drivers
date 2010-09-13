package thrift.test.sync.client;

import java.sql.Date;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import tserver.gen.TimeServer;

public class TCP_Client {

	
	public static void main (String[] args) throws Exception{
		
		// oeffnen eines Sockets mit IP und Port
		final TSocket socket = new TSocket("localhost", 50000);
		// festlegen eines Transport-Protokolls
        final TTransport transport = new TFramedTransport(socket);
        // festlegen eines Serialisierungs-Formats
        final TProtocol protocol = new TCompactProtocol(transport);
        
        // erstellen des synchronen Clients
        final TimeServer.Client client = new TimeServer.Client(protocol);
        
        try {
        	// oeffnen der Verbindung
            transport.open();
            
            // ausfuehren der blockierenden Methode
            long now = client.time();
            System.out.println("Zeit: "+new Date(now));
            
            
        } catch (TException e) {
            e.printStackTrace(System.err);
        } catch (RuntimeException e) {
            e.printStackTrace(System.err);
        } finally {
        	// schliessen der Verbindung
            transport.close();
        }
	}
}
