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

	private static abstract class FailureLessCallback<T extends TAsyncMethodCall> implements AsyncMethodCallback<T> {
	    @Override
	    public void onError(Throwable throwable) {
	      throwable.printStackTrace();
	    }
	  }
	
	public static void main (String[] args) throws Exception{
		
		// Erstellen des Clients-Sockets mit IP und port
		final TNonblockingSocket socket = new TNonblockingSocket("localhost", 50000);

        final TAsyncClientManager acm = new TAsyncClientManager();
        
        // Uebergeben an Client
        final TestService.AsyncClient client = new TestService.AsyncClient(new TBinaryProtocol.Factory(),acm,socket);
        
        final Object o = new Object();
        
        try {
			// Ein wenig Kommunikation
			System.out.println("Ich bin der Client!");
			
			// Entfernter Methodenaufruf
			client.sayHello(new FailureLessCallback<TestService.AsyncClient.sayHello_call>() {

				@Override
				public void onComplete(sayHello_call response) {
					try {
						response.getResult();
					} catch (TException e) {
						e.printStackTrace();
					}synchronized(o) {
				          o.notifyAll();
			        }
					
				}});
		    synchronized(o) {
		        o.wait(100000);
		      }
			
			
			
			client.getString(new AsyncMethodCallback<TestService.AsyncClient.getString_call>() {
		            
				@Override
	            public void onComplete(getString_call response) {
					try {
						Person person = response.getResult();
						System.out.println("Name: "+person.Name+" ,alter: "+person.alter);
						System.out.println("Test");
						
					} catch (TException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}synchronized(o) {
						o.notifyAll();
			        }
	            }
	
				@Override
				public void onError(Throwable arg0) {
					System.out.println("Fehler");
					synchronized(o) {
						o.notifyAll();
			        }
				}
			});
			synchronized(o) {
		        o.wait(100000);
		      }

			
			client.getInt(new AsyncMethodCallback<TestService.AsyncClient.getInt_call>() {
	            
				@Override
	            public void onComplete(getInt_call response) {
					try {
						int test = response.getResult();
						System.out.println(test);
						
					} catch (TException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}synchronized(o) {
						o.notifyAll();
			        }
	            }
	
				@Override
				public void onError(Throwable arg0) {
					synchronized(o) {
				          o.notifyAll();
			        }
				}
			});
			synchronized(o) {
		        o.wait(100000);
		      }

			
		} catch (TTransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	
}
