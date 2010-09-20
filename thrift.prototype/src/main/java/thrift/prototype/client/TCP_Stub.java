package thrift.prototype.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TNonblockingSocket;

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
import thrift.prototype.files.LoginFailed;
import thrift.prototype.operation.OperationKeys;
import thrift.prototype.operation.getMessageOp;
import thrift.prototype.operation.programm;
import thrift.prototype.operation.setMessageOp;


public class TCP_Stub implements DeviceAsync{

	TNonblockingSocket socket;
	TAsyncClientManager acm = new TAsyncClientManager();
	private AsyncDevice.AsyncClient client;
	private String id = "-1";
	State state;
	String uri;
	int port;
	
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
    
	public TCP_Stub (String uri, int port, TAsyncClientManager acm, String id) throws Exception{
		
		// Erstellen des Clients-Sockets mit IP und port
		socket = new TNonblockingSocket(uri, port);
        
        // Instanzieren und Initieren eines Cleints
        setClient(new AsyncDevice.AsyncClient(new TBinaryProtocol.Factory(),acm,socket));

	}
	
	/*
	 * Erstellt die initiale Verbindung zwischen Client und Server
	 * @throws InterruptedException
	 */
	private void connect(String userName, String passWord, final AsyncCallback<String> callback) throws InterruptedException{
		try {
			// erstellt ein ClientID-Objekt mit id als Key, mit Hilfe dieses
			new TCP_Stub(uri, port, new TAsyncClientManager(), "-1").getClient().connect(userName, passWord, new AsyncMethodCallback<AsyncDevice.AsyncClient.connect_call>(){
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

	public void setClient(AsyncDevice.AsyncClient client) {
		this.client = client;
	}

	public AsyncDevice.AsyncClient getClient() {
		return client;
	}

	/**
	 * Testen der RPC-Verbindung durch setzen einer Nachricht auf dem Server
	 * @param setMessage
	 * @return
	 */
	public OperationHandle<Void> setMessage(String setMessage, final AsyncCallback<Void> callback) {

		// Abbruch wenn keine id vergeben wurde -> nicht authentifiziert
		if(id.equalsIgnoreCase("-1")){
			callback.onFailure(null);
		}
		
		final String OperationHandleKey = OperationKeys.getInstance().getKey();
		
		setMessageOp setMessageop = new setMessageOp(id, OperationHandleKey, uri, port, acm);
		
		OperationHandle<Void> handle = setMessageop.operate(setMessage, callback);
		
		return handle;
		
	}
	
	/**
	 * Testen der RPC-Verbindung durch abrufen einer Nachricht vom Server
	 * @return
	 * @throws InterruptedException
	 */
	public OperationHandle<Void>  getMessage(final AsyncCallback<String> callback) throws InterruptedException{
		
		// Abbruch wenn keine id vergeben wurde -> nicht authentifiziert
		if(id.equalsIgnoreCase("-1")){
			callback.onFailure(null);
		}
		
		String OperationHandleKey = OperationKeys.getInstance().getKey();
		
		getMessageOp getMessageop = new getMessageOp(id, OperationHandleKey, uri, port, acm);
		
		OperationHandle<Void> handle = getMessageop.operate(callback);
		
		return handle;
	}

	@Override
	public OperationHandle<Void> program(DeviceBinFile binaryImage,
			long timeout, final AsyncCallback<Void> callback) {
		
		// Abbruch wenn keine id vergeben wurde -> nicht authentifiziert
		if(id.equalsIgnoreCase("-1")){
			callback.onFailure(null);
		}

		String OperationHandleKey = OperationKeys.getInstance().getKey();
		
		programm prog = new programm(id, OperationHandleKey, uri, port, acm);
		
		OperationHandle<Void> handle = prog.operate(binaryImage, timeout, callback);
		
		return handle;
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

