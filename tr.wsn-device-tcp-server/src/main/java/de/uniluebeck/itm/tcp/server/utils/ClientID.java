package de.uniluebeck.itm.tcp.server.utils;

import java.util.HashMap;

import de.uniluebeck.itm.devicedriver.async.DeviceAsync;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;

/**
 * Represent the Identity of every User in the TCP-Server
 * @author Andreas Maier
 *
 */
public class ClientID {
	
	/**
	 * List with Handles for every Operation that a User has started
	 */
	private final HashMap<String,OperationHandle<?>> handleList = new HashMap<String,OperationHandle<?>>();
	
	/**
	 * List with entry's for every Operation where getHandle was called 
	 */
	private final HashMap<String,Boolean> getList = new HashMap<String,Boolean>();
	
	/**
	 * The Device the User called
	 */
	private final DeviceAsync device;
	
	/**
	 * Constructor
	 * @param device The Device the User called
	 */
	public ClientID(final DeviceAsync device){
		this.device = device;
	}
	
	/**
	 * Return a HandleElement for a Key
	 * @param operationKey the key for the request
	 * @return the HandleElement for the Key
	 */
	public OperationHandle<?> getHandleElement(final String operationKey) {
		return handleList.get(operationKey);
	}
	
	/**
	 * set a HandleElement for a key
	 * @param operationKey the key where the HandleElement should be set
	 * @param handle the HandleElement should be set
	 */
	public void setHandleElement(final String operationKey, final OperationHandle<?> handle) {
		handleList.put(operationKey, handle);
	}
	
	/**
	 * delete a handleElement
	 * @param handle the HandleElement should be deleted
	 */
	public void deleteHandleElement(final OperationHandle<?> handle){
		handleList.remove(handle);
	}
	
	/**
	 * returns a reference to the device
	 * @return the device
	 */
	public DeviceAsync getDevice() {
		return device;
	}

	/**
	 * return the status of getHandle
	 * @param key the key where the status should be asked
	 * @return the status of getHandle
	 */
	public Boolean getCalledGet(final String key) {
		return !this.getList.isEmpty() && this.getList.get(key);
	}
	/**
	 * set the status of getHandle
	 * @param key the key where the status should be set
	 */
	public void setCalledGet(final String key) {
		this.getList.put(key, true);
	}
	/**
	 * remove a get from the List
	 * @param key the key which should be removed
	 */
	public void removeCalledGet(final String key) {
		this.getList.remove(key);
	}
}
