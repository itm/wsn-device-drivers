package thrift.prototype.client;

import org.apache.thrift.async.TAsyncClientManager;

import de.uniluebeck.itm.devicedriver.DeviceBinFile;
import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.MessagePacketListener;
import de.uniluebeck.itm.devicedriver.PacketType;
import de.uniluebeck.itm.devicedriver.State;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.async.DeviceAsync;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;

import thrift.prototype.operation.Connect;
import thrift.prototype.operation.OperationKeys;
import thrift.prototype.operation.getMessageOp;
import thrift.prototype.operation.programm;
import thrift.prototype.operation.readMacOp;
import thrift.prototype.operation.setMessageOp;

/**
 * Stub fuer die entfernte Kommunikation mit einem Thrift-Server
 * @author Andreas Maier
 * @author Bjoern Schuett
 *
 */
public class TCP_Stub implements DeviceAsync{

	private TAsyncClientManager acm = new TAsyncClientManager();
	private String userID = "-1";
	
	@SuppressWarnings("unused")
	private State state;
	String uri;
	int port;
	
	/**
	 * Konstruktor zum erstellen einer entfernten Verbindung
	 * @param userName
	 * @param passWord
	 * @param uri
	 * @param port
	 * @throws Exception
	 */
    TCP_Stub (String userName, String passWord, String uri, int port) throws Exception{
   
        this.uri = uri;
        this.port = port;
        
        // einleiten der initialen Verbindung
        new Connect(userName, passWord, uri, port).operate(new AsyncCallback<String>(){

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
				userID = result;
			}

			@Override
			public void onProgressChange(float fraction) {	
			}
        	
        });
    }
	
	/**
	 * Gibt eindeutige id des Stub-Objektes zurueck
	 * @return
	 */
	public String getID(){
		return userID;
	}

	/**
	 * Testen der RPC-Verbindung durch senden einer Nachricht an den Server
	 * @param setMessage
	 * @return
	 */
	public OperationHandle<Void> setMessage(String setMessage, final AsyncCallback<Void> callback) {

		// Abbruch wenn keine id vergeben wurde -> nicht authentifiziert
		if(userID.equalsIgnoreCase("-1")){
			callback.onFailure(null);
		}
		
		// Generieren eines einzigartigen OperationHandle-Keys
		final String OperationHandleKey = OperationKeys.getInstance().getKey();

		// ausfuehren des Thrift-Befehls und Rueckgabe des OperationHandle
		return new setMessageOp(userID, OperationHandleKey, uri, port, acm).operate(setMessage, callback);
		
	}
	
	/**
	 * Testen der RPC-Verbindung durch abrufen einer Nachricht vom Server
	 * @return
	 * @throws InterruptedException
	 */
	public OperationHandle<Void>  getMessage(final AsyncCallback<String> callback) throws InterruptedException{
		
		// Abbruch wenn keine id vergeben wurde -> nicht authentifiziert
		if(userID.equalsIgnoreCase("-1")){
			callback.onFailure(null);
		}
		
		String OperationHandleKey = OperationKeys.getInstance().getKey();

		return new getMessageOp(userID, OperationHandleKey, uri, port, acm).operate(callback);
	}

	/**
	 * Methode um ein entferntes Device zu flashen
	 */
	@Override
	public OperationHandle<Void> program(DeviceBinFile binaryImage,
			long timeout, final AsyncCallback<Void> callback) {
		
		// Abbruch wenn keine id vergeben wurde -> nicht authentifiziert
		if(userID.equalsIgnoreCase("-1")){
			callback.onFailure(null);
		}

		String OperationHandleKey = OperationKeys.getInstance().getKey();

		return new programm(userID, OperationHandleKey, uri, port, acm).operate(binaryImage, timeout, callback);
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
	public void removeMessagePacketListener(MessagePacketListener listener) {
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
		
		// Abbruch wenn keine id vergeben wurde -> nicht authentifiziert
		if(userID.equalsIgnoreCase("-1")){
			callback.onFailure(null);
		}

		String OperationHandleKey = OperationKeys.getInstance().getKey();

		return new readMacOp(userID, OperationHandleKey, uri, port, acm).operate(timeout, callback);
		
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

