package thrift.test.client;

import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.async.TAsyncMethodCall;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TTransportException;

import thrift.test.files.Person;
import thrift.test.files.TestService;
import thrift.test.files.TestService.AsyncClient.getInt_call;
import thrift.test.files.TestService.AsyncClient.getString_call;
import thrift.test.files.TestService.AsyncClient.sayHello_call;


public class TCP_Client {

	// Fehlerloses Callback, spart die Implementierung der onError-Methode fuer jede Methode
	private static abstract class FailureLessCallback<T extends TAsyncMethodCall> implements AsyncMethodCallback<T> {
	    @Override
	    public void onError(Throwable throwable) {
	      throwable.printStackTrace();
	    }
	  }
	
	public static void main (String[] args) throws Exception{
		
		// Erstellen des Clients-Sockets mit IP und port
		final TNonblockingSocket socket = new TNonblockingSocket("localhost", 50000);

		// Erstellen eines Client-Manager
        final TAsyncClientManager acm = new TAsyncClientManager();
        
        // Instanzieren und Initieren eines Cleints
        final TestService.AsyncClient client = new TestService.AsyncClient(new TBinaryProtocol.Factory(),acm,socket);
        
        // Synchro-Objekt
        final Object o = new Object();
        
        try {
			// Ein wenig Kommunikation
			System.out.println("Ich bin der Client!");
			
			// Entfernter Methodenaufruf ohne De-Serialisierung
			client.sayHello(new FailureLessCallback<TestService.AsyncClient.sayHello_call>() {

				@Override
				public void onComplete(sayHello_call response) {
					try {
						response.getResult();
					} catch (TException e) {
						e.printStackTrace();
					}// benachrichtigen des synchro-objekts
					synchronized(o) {
				          o.notifyAll();
			        }
				}});
			/* Thread ein wenig warten lassen, damit server die Moeglichkeit hat 
			   seine Arbeit zu tun, bevor die naechste Methode des Clients aufgerufen wird*/
		    synchronized(o) {
		        o.wait(100000);
		      }
			
			
			// Entfernter Methodenaufruf mit De-Serialisierung eines komplexen Objekts
			client.getString(new FailureLessCallback<TestService.AsyncClient.getString_call>() {
		        
				// Bei erfolgreicher Uebertragung
				@Override
	            public void onComplete(getString_call response) {
					try {
						Person person = response.getResult();
						System.out.println("Name: "+person.Name+" ,alter: "+person.alter);
						System.out.println("Test");
						
					} catch (TException e) {
						e.printStackTrace();
					} // benachrichtigen des synchro-objekts
					synchronized(o) {
						o.notifyAll();
			        }
	            }
			});
			synchronized(o) {
		        o.wait(100000);
		      }

			// Entfernter Methodenaufruf mit De-Serialisierung eines einfachen Datentyps
			client.getInt(new FailureLessCallback<TestService.AsyncClient.getInt_call>() {
	            
				// Bei erfolgreicher Uebertragung
				@Override
	            public void onComplete(getInt_call response) {
					try {
						int test = response.getResult();
						System.out.println(test);
						
					} catch (TException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}// benachrichtigen des synchro-objekts
					synchronized(o) {
						o.notifyAll();
			        }
	            }
			});
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
