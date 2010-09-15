package thrift.test.server;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.thrift.TException;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;

import de.uniluebeck.itm.Impl.Main;
import de.uniluebeck.itm.devicedriver.DeviceBinFile;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;


import thrift.test.files.AsyncDevice;

//TODO entweder Rohdaten direkt per program (list aus binaerdaten) uebertragen oder 
//TODO die Rohbloecke einzeln per transfer

public class TCP_Server {

	public static void main(String[] args) throws TTransportException {
	
		   // put up a server
	    final TNonblockingServer s = new TNonblockingServer(new AsyncDevice.Processor(new Handler()), new TNonblockingServerSocket(50000));
	    new Thread(new Runnable() {
	      @Override
	      public void run() {
	    	// Ein wenig Kommunikation
			System.out.println("Ich bin der Server!");
	        s.serve();
	      }
	    }).start();
	}
	
	//Implementierung der im Thrift-IDL festgelegten Services
	//TestService.Iface ist das dazugehoerige Interface
	private static class Handler implements AsyncDevice.Iface{

		private static Set<String> keys= new HashSet<String>();
		private static HashMap<String,ClientID> clientIDList = new  HashMap<String, ClientID>();
		
		@Override
		public String connect() throws TException {
			
			boolean doublekey = false;
			String key;
			
			/* erstellen eines zufaelligen key, mit dem ein Client identifiziert werden kann */
			do{
				double zwkey = Math.random();
				key = String.valueOf(zwkey);
				doublekey = keys.contains(key);
			}while(true == doublekey);
			
			/* erstellen einer Client-Repraesantition und speichern in einer Liste */
			ClientID clientId = new ClientID();
			clientIDList.put(key, clientId);
			
			return key;
		}
		
		@Override
		public void disconnect(String key) throws TException {
			clientIDList.remove(key);
		}
		
		@Override
		public void setMessage(String key,String message) throws TException {
			
			ClientID clientID = clientIDList.get(key);
			clientID.setMessage(message);
		}

		@Override
		public String getMessage(String key) throws TException {

			ClientID clientID = clientIDList.get(key);
			return clientID.getMessage();
		}
		
		@Override
		public String program(String key, List<ByteBuffer> BinFile,
				List<Integer> addresses, long timeout) throws TException {
			Main test = new Main();
			
			/* erstmal auskommentiert da ich keine 
			   Moeglichkeit habe eine DeviceBinFile zu erstellen */
			//ClientID clientID = clientIDList.get(key);

			//clientID.saveBinFile(addresses, BinFile);
			
			//DeviceBinFile binaryImage=clientID.getBinFile();
			
			// Provisorisch null
			DeviceBinFile binaryImage = null;
			
			test.program(binaryImage, timeout, new AsyncCallback<Void>(){

				@Override
				public void onCancel() {
					System.out.println("Abbruch im TCP-Server");
				}

				@Override
				public void onFailure(Throwable throwable) {
					System.out.println("Fehler im TCP-Server");
				}

				@Override
				public void onProgressChange(float fraction) {
					System.out.println("change im TCP-Server");
				}

				@Override
				public void onSuccess(Void result) {
					System.out.println("jup es geht im TCP-Server");
				}});
			
			System.out.println("fertig mit program");
			
			return "wieder im Client";
			
		}
		
	}
}

