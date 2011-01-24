package de.uniluebeck.itm.tcp.server.utils;

import java.util.HashMap;

import de.uniluebeck.itm.devicedriver.async.DeviceAsync;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;


public class ClientID {

	String Message = "init";
	
	private HashMap<String,OperationHandle<?>> handleList = new HashMap<String,OperationHandle<?>>();
	private HashMap<String,Boolean> getList = new HashMap<String,Boolean>();
	
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

	public Boolean getCalledGet(String key) {
		
		if(this.getList.isEmpty() ){
			return false;
		}
		
		if(this.getList.get(key) == true ){
			return true;
		}else {
			return false;
		}
	}

	public void setCalledGet(String key) {
		this.getList.put(key, true);
	}
	public void removeCalledGet(String key) {
		this.getList.remove(key);
	}
}
