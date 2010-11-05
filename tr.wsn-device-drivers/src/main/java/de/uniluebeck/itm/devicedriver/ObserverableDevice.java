package de.uniluebeck.itm.devicedriver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * Abstract device with a default implementation for <code>MessagePacketListener</code> notification.
 * 
 * @author Malte Legenhausen
 */
public abstract class ObserverableDevice implements Device {
	
	/**
	 * Mapping from message packet type to a list of <code>MessagePacketListener</code> instances.
	 */
	private final Map<Integer, List<MessagePacketListener>> listeners = new TreeMap<Integer, List<MessagePacketListener>>();
	
	@Override
	public void addListener(MessagePacketListener listener, int... types) {
		for (final int type : types) {
			if (!listeners.containsKey(type)) {
				listeners.put(type, new ArrayList<MessagePacketListener>());
			}
			listeners.get(type).add(listener);
		}
	}
	
	@Override
	public void addListener(MessagePacketListener listener, PacketType... packetTypes) {
		final int[] types = new int[packetTypes.length];
		int i = 0;
		for (PacketType type : packetTypes) {
			types[i++] = type.getValue();
		}
		addListener(listener, types);
	}
	
	@Override
	public void removeListener(MessagePacketListener listener) {
		for (List<MessagePacketListener> listeners : this.listeners.values()) {
			listeners.remove(listener);
		}
	}
	
	/**
	 * Notify all listeners about the given <code>MessagePacket</code>.
	 * 
	 * @param packet The message packet that has to be send to all listeners of the packet type.
	 */
	protected void notifyMessagePacketListener(MessagePacket packet) {
		List<MessagePacketListener> listeners = this.listeners.get(packet.getType());
		for (MessagePacketListener listener : listeners.toArray(new MessagePacketListener[listeners.size()])) {
			listener.onMessagePacketReceived(packet);
			listener.onMessagePlainTextReceived(new MessagePlainText(packet.getContent()));
		}
	}
	
	
}
