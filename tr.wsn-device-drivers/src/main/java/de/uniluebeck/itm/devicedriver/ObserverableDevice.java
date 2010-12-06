package de.uniluebeck.itm.devicedriver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.uniluebeck.itm.devicedriver.event.MessageEvent;


/**
 * Abstract device with a default implementation for <code>MessagePacketListener</code> notification.
 * 
 * @author Malte Legenhausen
 */
public abstract class ObserverableDevice implements Device {
	
	/**
	 * Mapping from message packet type to a list of <code>MessagePacketListener</code> instances.
	 */
	private final Map<Integer, List<MessagePacketListener>> messagePacketMap = new TreeMap<Integer, List<MessagePacketListener>>();
	
	private final List<MessagePacketListener> messagePacketListener = new ArrayList<MessagePacketListener>();
	
	private final List<MessagePlainTextListener> messagePlainTextListener = new ArrayList<MessagePlainTextListener>();
	
	@Override
	public void addListener(MessagePacketListener listener, int... types) {
		for (final int type : types) {
			if (!messagePacketMap.containsKey(type)) {
				messagePacketMap.put(type, new ArrayList<MessagePacketListener>());
			}
			messagePacketMap.get(type).add(listener);
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
	public void addListener(MessagePacketListener listener) {
		messagePacketListener.add(listener);
	}
	
	@Override
	public void addListener(MessagePlainTextListener listener) {
		messagePlainTextListener.add(listener);
	}
	
	@Override
	public void removeListener(MessagePacketListener listener) {
		if (messagePacketListener.contains(listener)) {
			messagePacketListener.remove(listener);
		} else {
			for (List<MessagePacketListener> listeners : this.messagePacketMap.values()) {
				if (listeners.contains(listener)) {
					listeners.remove(listener);
				}
			}
		}
	}
	
	@Override
	public void removeListener(MessagePlainTextListener listener) {
		if (messagePlainTextListener.contains(listener)) {
			messagePlainTextListener.remove(listener);
		}
	}
	
	/**
	 * Notify all listeners about the given <code>MessagePacket</code>.
	 * 
	 * @param packet The message packet that has to be send to all listeners of the packet type.
	 */
	protected void fireMessagePacketEvent(MessageEvent<MessagePacket> event) {
		final List<MessagePacketListener> listeners = this.messagePacketMap.get(event.getMessage().getType());
		for (final MessagePacketListener listener : listeners.toArray(new MessagePacketListener[listeners.size()])) {
			listener.onMessagePacketReceived(event);
		}
		for (final MessagePacketListener listener : messagePacketListener.toArray(new MessagePacketListener[messagePacketListener.size()])) {
			listener.onMessagePacketReceived(event);
		}
	}
	
	protected void fireMessagePlainTextEvent(MessageEvent<MessagePlainText> event) {
		for (final MessagePlainTextListener listener : messagePlainTextListener.toArray(new MessagePlainTextListener[messagePlainTextListener.size()])) {
			listener.onMessagePlainTextReceived(event);
		}
	}
	
	
}
