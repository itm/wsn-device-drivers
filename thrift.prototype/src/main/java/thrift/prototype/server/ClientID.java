package thrift.prototype.server;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import de.uniluebeck.itm.devicedriver.DeviceBinData;
import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;

/**
 * Client-repraesantition im Server
 * @author Andreas Maier
 *
 */
public class ClientID {
	private String Message = "init";
	private AtomicBoolean blocked = new AtomicBoolean();
	
	private static Set<String> keys= new HashSet<String>();
	private HashMap<String, OperationHandle<Void>> handleList = new HashMap<String, OperationHandle<Void>>();
	private HashMap<String, OperationHandle<MacAddress>> handleListMac = new HashMap<String, OperationHandle<MacAddress>>();

	ClientID(){
	}
	
	public void setMessage(String message){
		for(int i=0;i<1;i++){
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
		}
		Message = message;
	}
	
	public String getMessage() {
		for(int i=0;i<1;i++){
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return Message;
	}
	
	public void saveBinFile(List<ByteBuffer> values, String description){
		
		Iterator<ByteBuffer> valuesIterator = values.iterator();
		
		while(valuesIterator.hasNext()){
			// TODO DeviceBinFile aus Rohdaten erstellen und auf Festplatte zwischenspeichern
		}
		
		
	}
	
	public DeviceBinData getBinFile(){
		
		// TODO DeviceBinFile von Festplatte auslesen und zurueckgeben
		
		return null;
	}

	public void setBlocked(AtomicBoolean blocked) {
		this.blocked = blocked;
	}

	public AtomicBoolean getBlocked() {
		return blocked;
	}

	public void setHandleList(String HandleKey, OperationHandle<Void> handle) {
		
		keys.add(HandleKey);
		this.handleList.put(HandleKey, handle);
	}

	public void removeHandle(String HandleKey){
		
		handleList.remove(HandleKey);
		keys.remove(HandleKey);
	}
	public OperationHandle<Void> getHandle(String HandleKey) {
		return handleList.get(HandleKey);
	}
	
	public void setHandleListMac(String HandleKey, OperationHandle<MacAddress> handle) {
		keys.add(HandleKey);
		this.handleListMac.put(HandleKey, handle);
	}
	public OperationHandle<MacAddress> getHandleMac(String HandleKey) {
		return handleListMac.get(HandleKey);
	}
	public void removeHandleMac(String HandleKey){
		handleListMac.remove(HandleKey);
		keys.remove(HandleKey);
	}
}
