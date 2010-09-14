package thrift.test.client;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.async.TAsyncMethodCall;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TTransportException;

import de.uniluebeck.itm.devicedriver.BinFileDataBlock;
import de.uniluebeck.itm.devicedriver.DeviceBinFile;
import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.MessagePacketListener;
import de.uniluebeck.itm.devicedriver.PacketType;
import de.uniluebeck.itm.devicedriver.State;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.async.DeviceAsync;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;

import thrift.test.files.AsyncDevice;
import thrift.test.files.AsyncDevice.AsyncClient.connect_call;
import thrift.test.files.AsyncDevice.AsyncClient.getMessage_call;
import thrift.test.files.AsyncDevice.AsyncClient.setMessage_call;
import thrift.test.files.AsyncDevice.AsyncClient.program_call;


public class TCP_Stub implements DeviceAsync{

	final TNonblockingSocket socket;
	final TAsyncClientManager acm;
	final AsyncDevice.AsyncClient client;
	private String id = "-1";
	private String message;
	
    // Synchro-Objekt
    final Object o = new Object();
	
	// Fehlerloses Callback, spart die Implementierung der onError-Methode fuer jede Methode
	private static abstract class FailureLessCallback<T extends TAsyncMethodCall> implements AsyncMethodCallback<T> {
	    @Override
	    public void onError(Throwable throwable) {
	      throwable.printStackTrace();
	    }
	  }
	
	TCP_Stub (String uri, int port, TAsyncClientManager acm) throws Exception{
		
		// Erstellen des Clients-Sockets mit IP und port
		socket = new TNonblockingSocket(uri, port);

		// Erstellen eines Client-Manager
        this.acm = acm;//new TAsyncClientManager();
        
        // Instanzieren und Initieren eines Cleints
        client = new AsyncDevice.AsyncClient(new TBinaryProtocol.Factory(),acm,socket);
        
        // einleiten der initialen Verbindung
        connect();

	}
    
	TCP_Stub (String uri, int port) throws Exception{
		
		// Erstellen des Clients-Sockets mit IP und port
		socket = new TNonblockingSocket(uri, port);

		// Erstellen eines Client-Manager
        this.acm = new TAsyncClientManager();
        
        // Instanzieren und Initieren eines Cleints
        client = new AsyncDevice.AsyncClient(new TBinaryProtocol.Factory(),acm,socket);
        
        // einleiten der initialen Verbindung
        connect();

	}
	
	/*
	 * Erstellt die initiale Verbindung zwischen Client und Server
	 * @throws InterruptedException
	 */
	private void connect() throws InterruptedException{
		try {
			// erstellt ein ClientID-Objekt mit id als Key, mit Hilfe dieses
			client.connect(new FailureLessCallback<AsyncDevice.AsyncClient.connect_call>(){
				@Override
				public void onComplete(connect_call response) {
					try {
						id = response.getResult();
					} catch (TException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}synchronized(o) {
						o.notifyAll();
			        }
				}			
			});
			synchronized(o) {
		        o.wait(100000);
		      }	
		} catch (TException e) {
			e.printStackTrace();
		}
	}
	
	public String getID(){
		return id;
	}
	
	/**
	 * Testen der RPC-Verbindung durch setzen einer Nachricht auf dem Server
	 * @param setMessage
	 * @return
	 * @throws InterruptedException
	 */
	public String setMessage(String setMessage) throws InterruptedException{

		try {
			// Entfernter Methodenaufruf mit De-Serialisierung eines einfachen Datentyps
			client.setMessage(id, setMessage, new FailureLessCallback<AsyncDevice.AsyncClient.setMessage_call>() {
	            
				// Bei erfolgreicher Uebertragung
				@Override
				public void onComplete(setMessage_call response) {
					try {
						response.getResult();
						//System.out.println(test);
						
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
		
		return message;
	}
	
	/**
	 * Testen der RPC-Verbindung durch abrufen einer Nachricht vom Server
	 * @return
	 * @throws InterruptedException
	 */
	public String getMessage() throws InterruptedException{

		try {
			// Entfernter Methodenaufruf mit De-Serialisierung eines einfachen Datentyps
			client.getMessage(id, new FailureLessCallback<AsyncDevice.AsyncClient.getMessage_call>() {
	            
				// Bei erfolgreicher Uebertragung
				@Override
				public void onComplete(getMessage_call response) {
					try {
						message = response.getResult();
						//System.out.println(test);
						
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
		
		return message;
	}

	@Override
	public OperationHandle<Void> program(DeviceBinFile binaryImage,
			long timeout, final AsyncCallback<Void> callback) {
		
		// TODO Klaeren wozu der Client den OperationHandle braucht
		// TODO Wer soll Timeout regeln?
		
		/*
		binaryImage.resetBlockIterator();
		
		List<ByteBuffer> blocks = new ArrayList<ByteBuffer>();
		List<Integer> addresses = new ArrayList<Integer>();
		
		/* aufbereiten der DeviceBinFile fuer den Transport *//*
		while(binaryImage.hasNextBlock()){
			BinFileDataBlock block = binaryImage.getNextBlock();
			blocks.add(ByteBuffer.wrap(block.data));
			addresses.add(block.address); 
		}*/
		
		List<ByteBuffer> blocks = null;
		List<Integer> addresses = null;
		
		try {
			client.program(id, blocks, addresses, timeout, new FailureLessCallback<AsyncDevice.AsyncClient.program_call>() {

				@Override
				public void onComplete(program_call response) {
					try {
						response.getResult();
						callback.onSuccess(null);
					} catch (TException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					synchronized(o) {
						o.notifyAll();
			        }
				}
			});
			synchronized(o) {
		        o.wait(100000);
		      }
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
        return new OperationHandle<Void>(){

			@Override
			public void cancel() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Void get() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public State getState() {
				// TODO Auto-generated method stub
				return null;
			}};
	}
	
	@Override
	public void addMessagePacketListener(MessagePacketListener listener,
			PacketType... types) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addMessagePacketListener(MessagePacketListener listener,
			int... types) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public OperationHandle<Void> eraseFlash(long timeout,
			AsyncCallback<Void> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OperationHandle<byte[]> readFlash(int address, int length,
			long timeout, AsyncCallback<byte[]> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OperationHandle<MacAddress> readMac(long timeout,
			AsyncCallback<MacAddress> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeMessagePacketListener(MessagePacketListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public OperationHandle<Void> reset(long timeout,
			AsyncCallback<Void> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OperationHandle<Void> send(MessagePacket packet, long timeout,
			AsyncCallback<Void> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OperationHandle<Void> writeFlash(int address, byte[] data,
			int length, long timeout, AsyncCallback<Void> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OperationHandle<Void> writeMac(MacAddress macAddress, long timeout,
			AsyncCallback<Void> callback) {
		// TODO Auto-generated method stub
		return null;
	}
}

