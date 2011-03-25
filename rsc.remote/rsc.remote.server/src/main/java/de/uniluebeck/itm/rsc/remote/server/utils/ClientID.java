package de.uniluebeck.itm.rsc.remote.server.utils;

import java.util.HashMap;

import de.uniluebeck.itm.rsc.drivers.core.MessagePacketListener;
import de.uniluebeck.itm.rsc.drivers.core.MessagePlainTextListener;
import de.uniluebeck.itm.rsc.drivers.core.async.DeviceAsync;
import de.uniluebeck.itm.rsc.drivers.core.async.OperationHandle;

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
	 * List with ReverseMessages for every Operation that a User has started
	 */
	private final HashMap<String,ReverseMessage> reverseMessageList = new HashMap<String,ReverseMessage>();

	
	/**
	 * List with entry's for every Operation where getHandle was called 
	 */
	private final HashMap<String,Boolean> getList = new HashMap<String,Boolean>();
	
	/**
	 * List for the OperationType for every Handle
	 */
	private final HashMap<String,OperationType> operationTypeList = new HashMap<String,OperationType>();
	
	/**
	 * packetListenerList
	 */
	private HashMap<String, MessagePacketListener> packetListenerList = new HashMap<String, MessagePacketListener>();
	/**
	 * plainTextListenerList
	 */
	private HashMap<String, MessagePlainTextListener> plainTextListenerList = new HashMap<String, MessagePlainTextListener>();

	
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
	 * add a HandleElement for a key to the HandleList
	 * @param operationKey the key where the HandleElement should be set
	 * @param handle the HandleElement should be set
	 */
	public void addHandleElement(final String operationKey, final OperationHandle<?> handle) {
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
	 * Return all HandleElements
	 * @return all HandleElements
	 */
	public HashMap<String, OperationHandle<?>> getHandleList() {
		return handleList;
	}

	/**
	 * Return all ReverMessageElements
	 * @return all ReverMessageElements
	 */
	public HashMap<String,ReverseMessage> getReverseMessageList() {
		return reverseMessageList;
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

	/**
	 * Add a OperationType to the operationTypeList
	 * @param key the OperationHandle key
	 * @param type the operationType
	 */
	public void addOperationType(final String key, final OperationType type) {
		this.operationTypeList.put(key, type);
	}
	
	/**
	 * return a OperationType from the operationTypeList
	 * @param key the key of the Operation for which the Type should be find
	 * @return the operationType
	 */
	public OperationType getOperationType(final String key) {
		return operationTypeList.get(key);
	}

	/**
	 * return a PacketListener from the PacketListenerList
	 * @param key the key of the Listener which should be find
	 * @return the Listener
	 */
	public MessagePacketListener getPacketListener(final String key) {
		return this.packetListenerList.get(key);
	}
	
	/**
	 * return a the PacketListenerList
	 * @return the PacketListenerList
	 */
	public HashMap<String, MessagePacketListener> getPacketListenerList() {
		return this.packetListenerList;
	}

	/**
	 * add a PacketListener to the PacketListenerList
	 * @param key the key of the Listener which should be add
	 * @param packetListener the Listener which should be add
	 */
	public void addPacketListener(final String key, final MessagePacketListener packetListener) {
		this.packetListenerList.put(key, packetListener);
	}
	
	/**
	 * remove a PacketListener from the PacketListenerList
	 * @param key the key of the Listener which should be removed
	 */
	public void removePacketListener(final String key) {
		this.packetListenerList.remove(key);
	}

	/**
	 * return a PlainTextListener from the PlainTextListenerList
	 * @param key the key of the Listener which should be find
	 * @return the Listener
	 */
	public MessagePlainTextListener getPlainTextListener(final String key) {
		return this.plainTextListenerList.get(key);
	}
	
	/**
	 * return the PlainTextListenerList
	 * @return the PlainTextListenerList
	 */
	public HashMap<String, MessagePlainTextListener> getPlainTextListenerList() {
		return this.plainTextListenerList;
	}

	/**
	 * add a PlainTextListener to the PlainTextListenerList
	 * @param key the key of the Listener which should be add
	 * @param plainTextListener the Listener which should be add
	 */
	public void addPlainTextListener(final String key, final MessagePlainTextListener plainTextListener) {
		this.plainTextListenerList.put(key, plainTextListener);
	}
	
	/**
	 * remove a PlainTextListener from the PlainTextListenerList
	 * @param key the key of the Listener which should be removed
	 */
	public void removePlainTextListener(final String key) {
		this.plainTextListenerList.remove(key);
	}
}
