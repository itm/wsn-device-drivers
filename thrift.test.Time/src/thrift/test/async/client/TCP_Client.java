package thrift.test.async.client;

import java.sql.Date;

import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TTransportException;

import tserver.gen.TimeServer;
import tserver.gen.TimeServer.AsyncClient.time_call;

public class TCP_Client {

	
	public static void main (String[] args) throws Exception{
		
		// Erstellen des Clients-Sockets mit IP und port
		final TNonblockingSocket socket = new TNonblockingSocket("localhost", 12345);

		// Erstellen eines Client-Manager
        final TAsyncClientManager acm = new TAsyncClientManager();
        
        // Instantieren und Initieren des Clients
        final TimeServer.AsyncClient client = new TimeServer.AsyncClient(new TBinaryProtocol.Factory(),acm,socket);
        
        // Synchro-Objekt
        final Object o = new Object();
        
        try {
			// Ein wenig Kommunikation
			System.out.println("Ich bin der Client!");
        
			// Ausfuehren der time-Methode
			// Uebergabe eines Callback-Objekts
			client.time(new AsyncMethodCallback<TimeServer.AsyncClient.time_call>() {
	            
				// Bei erfolgreichem Ausfueren wird diese Methode
				// des Callback Objektes ausgefuehrt
				@Override
	            public void onComplete(time_call response) {
					try {
						long now = response.getResult();
						System.out.println("Zeit: "+new Date(now));
						
					} catch (TException e) {
						e.printStackTrace();
					}
					/* benachrichtigen des synchro-objekts
					 */
					synchronized(o) {
						o.notifyAll();
			        }
	            }
	
				// Bei Callback Fehlern wird diese Methode ausgefuehrt
				@Override
				public void onError(Throwable arg0) {
				}
			});
			/* Thread ein wenig warten lassen, damit server die Moeglichkeit hat 
			   seine Arbeit zu tun, bevor die naechste Methode des Clients aufgerufen wird*/
			synchronized(o) {
		        o.wait(100000);
		      }

			
		} catch (TTransportException e) {
			e.printStackTrace();
		} catch (TException e) {
			e.printStackTrace();
		}
		
	}
}
