package thrift.prototype.client;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TTransportException;

import de.uniluebeck.itm.devicedriver.DeviceBinFile;
import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.MessagePacketListener;
import de.uniluebeck.itm.devicedriver.PacketType;
import de.uniluebeck.itm.devicedriver.State;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.async.DeviceAsync;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;

import thrift.prototype.files.AsyncDevice;
import thrift.prototype.files.AsyncDevice.AsyncClient.connect_call;
import thrift.prototype.files.AsyncDevice.AsyncClient.getMessage_call;
import thrift.prototype.files.AsyncDevice.AsyncClient.setMessage_call;
import thrift.prototype.files.AsyncDevice.AsyncClient.program_call;
import thrift.prototype.files.LoginFailed;


public class TCP_Stub implements DeviceAsync{

	TNonblockingSocket socket;
	TAsyncClientManager acm = new TAsyncClientManager();
	AsyncDevice.AsyncClient client;
	private String id = "-1";
	State state;
	String uri;
	int port;
	
	
	AtomicBoolean Messageblocked = new AtomicBoolean(false);
	
	List<Thread> threads = new ArrayList<Thread>();
	
    // Synchro-Objekt
    final Object o = new Object();
	
    TCP_Stub (String userName, String passWord, String uri, int port) throws Exception{
   
        this.uri = uri;
        
        this.port = port;
        
        // einleiten der initialen Verbindung
        connect(userName, passWord, new AsyncCallback<String>(){

			@Override
			public void onCancel() {
			}

			@Override
			public void onFailure(Throwable throwable) {
				System.out.println("Authentifizierung felgeschlagen, "+throwable.getMessage());
			}

			@Override
			public void onSuccess(String result) {
				System.out.println("Authentifizierung erfolgreich");
				id = result;
			}

			@Override
			public void onProgressChange(float fraction) {	
			}
        	
        });
    }
    
	private TCP_Stub (String uri, int port, TAsyncClientManager acm, String id) throws Exception{
		
		// Erstellen des Clients-Sockets mit IP und port
		socket = new TNonblockingSocket(uri, port);
        
        // Instanzieren und Initieren eines Cleints
        client = new AsyncDevice.AsyncClient(new TBinaryProtocol.Factory(),acm,socket);

	}
	
	/*
	 * Erstellt die initiale Verbindung zwischen Client und Server
	 * @throws InterruptedException
	 */
	private void connect(String userName, String passWord, final AsyncCallback<String> callback) throws InterruptedException{
		try {
			// erstellt ein ClientID-Objekt mit id als Key, mit Hilfe dieses
			new TCP_Stub(uri, port, new TAsyncClientManager(), "-1").client.connect(userName, passWord, new AsyncMethodCallback<AsyncDevice.AsyncClient.connect_call>(){
				@Override
				public void onComplete(connect_call response) {
					try {
						callback.onSuccess(response.getResult());
					} catch (TException e) {
						System.out.println("Point 1");
						e.printStackTrace();
					} catch (LoginFailed lf) {
						//lf.printStackTrace();//TODO Fehlgeschlagenen Login besser behandeln!
						callback.onFailure(lf);
					}synchronized(o) {
						o.notifyAll();
			        }
				}
				@Override
				public void onError(Throwable throwable) {
					callback.onFailure(throwable);
				}			
			});
			synchronized(o) {
		        o.wait(10000);
		      }	
		} catch (TException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gibt eindeutige id des Stub-Objektes zurueck
	 * @return
	 */
	public String getID(){
		return id;
	}

	/**
	 * Testen der RPC-Verbindung durch setzen einer Nachricht auf dem Server
	 * @param setMessage
	 * @return
	 */
	public OperationHandle<Void> setMessage(String setMessage, final AsyncCallback<Void> callback) {

		// Abbruch wenn keine id vergeben wurde -> nicht authentifiziert
		if(id.equalsIgnoreCase("-1")){
			state = State.EXCEPTED;
			return null;
		}
		
		state = State.RUNNING;
		
		try {
			// Entfernter Methodenaufruf
			new TCP_Stub(uri, port, acm, id).client.setMessage(id, setMessage, new AsyncMethodCallback<AsyncDevice.AsyncClient.setMessage_call>() {
	            
				// Bei erfolgreicher Uebertragung
				@Override
				public void onComplete(setMessage_call response) {
					try {
						response.getResult();
						state = State.DONE;
						callback.onSuccess(null);
					} catch (TException e) {
						state = State.EXCEPTED;
						e.printStackTrace();
					}// benachrichtigen des synchro-objekts
					synchronized(o) {
						o.notifyAll();
						Messageblocked.set(false);
			        }
	            }

				// Bei fehlerhafter Uebertragung
				@Override
				public void onError(Throwable throwable) {
					synchronized(o) {
						o.notifyAll();
						Messageblocked.set(false);
						state = State.EXCEPTED;
						callback.onFailure(throwable);
					}
				}
			});
		} catch (TTransportException e) {
			e.printStackTrace();
		} catch (TException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new OperationHandle<Void>(){

			@Override
			public void cancel() {
				//TODO keine Ahnung wie ich eine laufende operation abbrechen soll
				//TODO oder doch nur aus warteschlange loeschen?
				state = State.CANCELED;
			}
			@Override
			public Void get() {
				// TODO warten auch auf warteschlange?
				synchronized(o) {
			        try {
			        	state = State.WAITING;
			        	o.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			    }
			return null;
			}
			@Override
			public State getState() {
				return state;
			}};
	}
	
	/**
	 * Testen der RPC-Verbindung durch abrufen einer Nachricht vom Server
	 * @return
	 * @throws InterruptedException
	 */
	public OperationHandle<Void>  getMessage(final AsyncCallback<String> callback) throws InterruptedException{
		
		// Abbruch wenn keine id vergeben wurde -> nicht authentifiziert
		if(id.equalsIgnoreCase("-1")){
			state = State.EXCEPTED;
			return null;
		}
		
		state = State.RUNNING;
		
		try {
			// Entfernter Methodenaufruf mit De-Serialisierung eines einfachen Datentyps
			new TCP_Stub(uri, port, acm, id).client.getMessage(id, new AsyncMethodCallback<AsyncDevice.AsyncClient.getMessage_call>() {
	            
				// Bei erfolgreicher Uebertragung
				@Override
				public void onComplete(getMessage_call response) {
					try {
						String message = response.getResult();
						state = State.DONE;
						callback.onSuccess(message);
					} catch (TException e) {
						state = State.EXCEPTED;
						e.printStackTrace();
					}// benachrichtigen des synchro-objekts
					synchronized(o) {
						o.notifyAll();
						Messageblocked.set(false);
			        }
	            }
				@Override
				public void onError(Throwable throwable) {
					synchronized(o) {
						o.notifyAll();
						Messageblocked.set(false);
						state = State.EXCEPTED;
						callback.onFailure(throwable);
					}
				}
			});
		} catch (TTransportException e) {
			e.printStackTrace();
		} catch (TException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new OperationHandle<Void>(){

			@Override
			public void cancel() {
				state = State.CANCELED;
			}
			@Override
			public Void get() {
				synchronized(o) {
			        try {
			        	state = State.WAITING;
			        	o.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			    }
			return null;
			}
			@Override
			public State getState() {
				return state;
			}};
	}

	@Override
	public OperationHandle<Void> program(DeviceBinFile binaryImage,
			long timeout, final AsyncCallback<Void> callback) {
		
		// TODO Klaeren wozu der Client den OperationHandle braucht
		// TODO Wer soll Timeout regeln?
		
		// Abbruch wenn keine id vergeben wurde -> nicht authentifiziert
		if(id.equalsIgnoreCase("-1")){
			state = State.EXCEPTED;
			return null;
		}
		
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
			new TCP_Stub(uri, port, acm, id).client.program(id, blocks, addresses, timeout, new AsyncMethodCallback<AsyncDevice.AsyncClient.program_call>() {

				@Override
				public void onComplete(program_call response) {
					try {
						System.out.println("client.programm: on Complete");
						callback.onSuccess(null);
						response.getResult();
					} catch (TException e) {
						e.printStackTrace();
					}
					synchronized(o) {
						o.notifyAll();
			        }
				}
				@Override
				public void onError(Throwable throwable) {
					callback.onFailure(throwable);
				}
			});
		} catch (TException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return new OperationHandle<Void>(){

			@Override
			public void cancel() {
				state = State.CANCELED;
			}
			@Override
			public Void get() {
				synchronized(o) {
			        try {
			        	state = State.WAITING;
			        	o.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			    }
			return null;
			}
			@Override
			public State getState() {
				return state;
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

