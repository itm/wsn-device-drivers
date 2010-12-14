package de.uniluebeck.itm.tcp.Server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.async.DeviceAsync;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;


public class ClientID {

	String Message = "init";
	private HashMap<String,OperationHandle<Void>> handleVoidList = new HashMap<String,OperationHandle<Void>>();
	private HashMap<String,OperationHandle<MacAddress>> handleMacList = new HashMap<String,OperationHandle<MacAddress>>();
	
	private DeviceAsync device;
	
	public ClientID(DeviceAsync device){
		this.device = device;
	}

	// Eintragen des OperationHandle mit dem OperationKey in eine HashMap
	public void setHandleVoidList(String OperationKey, OperationHandle<Void> handle) {
		handleVoidList.put(OperationKey, handle);
	}
	
	public void setHandleMacList(String OperationKey, OperationHandle<MacAddress> handle) {
		handleMacList.put(OperationKey, handle);
	}

	// Rueckgabe des richtigen OperationHandle
	public OperationHandle<Void> getHandleVoidList(String OperationKey) {
		return handleVoidList.get(OperationKey);
	}

	// Rueckgabe des richtigen OperationHandle
	public OperationHandle<MacAddress> getHandleMacList(String OperationKey) {
		return handleMacList.get(OperationKey);
	}
	
	public DeviceAsync getDevice() {
		return device;
	}
}
