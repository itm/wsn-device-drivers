package de.uniluebeck.itm.tcp.Server;

import java.util.HashMap;

import de.uniluebeck.itm.devicedriver.async.DeviceAsync;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;


public class ClientID {

	String Message = "init";
	Boolean calledGet = false;
	
	private HashMap<String,OperationHandle<?>> handleList = new HashMap<String,OperationHandle<?>>();
	
	private DeviceAsync device;
	
	public ClientID(DeviceAsync device){
		this.device = device;
	}
	
	// Rueckgabe des richtigen OperationHandle
	public OperationHandle<?> getHandleElement(String OperationKey) {
		return handleList.get(OperationKey);
	}
	
	public void setHandleElement(String OperationKey, OperationHandle<?> handle) {
		handleList.put(OperationKey, handle);
	}
	
	public void deleteHandleElement(OperationHandle<?> handle){
		handleList.remove(handle);
	}
	
	public DeviceAsync getDevice() {
		return device;
	}

	public Boolean getCalledGet() {
		return calledGet;
	}

	public void setCalledGet(Boolean calledGet) {
		this.calledGet = calledGet;
	}
}
