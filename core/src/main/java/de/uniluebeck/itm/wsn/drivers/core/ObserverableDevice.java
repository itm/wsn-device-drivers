package de.uniluebeck.itm.wsn.drivers.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.uniluebeck.itm.wsn.drivers.core.event.MessageEvent;


/**
 * Abstract device with a default implementation for <code>MessagePacketListener</code> notification.
 * 
 * @author Malte Legenhausen
 * 
 * @param <C> The type of the connection used by this device.
 */
public abstract class ObserverableDevice<C extends Connection> implements Device<C> {
	
	/**
	 * Mapping from message packet type to a list of <code>MessagePacketListener</code> instances.
	 */
	private final Map<Integer, List<MessagePacketListener>> messagePacketMap = new TreeMap<Integer, List<MessagePacketListener>>();
	
	/**
	 * A list of message packet listener that want to be notified when any type of message packet has arrived.
	 */
	private final List<MessagePacketListener> messagePacketListener = new ArrayList<MessagePacketListener>();
	
	/**
	 * A list of plaintext listener that will be notified when a plain text message was receivced.
	 */
	private final List<MessagePlainTextListener> messagePlainTextListener = new ArrayList<MessagePlainTextListener>();
	
	@Override
	public void addListener(final MessagePacketListener listener, final int... types) {
		for (final int type : types) {
			if (!messagePacketMap.containsKey(type)) {
				messagePacketMap.put(type, new ArrayList<MessagePacketListener>());
			}
			messagePacketMap.get(type).add(listener);
		}
	}
	
	@Override
	public void addListener(final MessagePacketListener listener, final PacketType... packetTypes) {
		final int[] types = new int[packetTypes.length];
		int i = 0;
		for (PacketType type : packetTypes) {
			types[i++] = type.getValue();
		}
		addListener(listener, types);
	}
	
	@Override
	public void addListener(final MessagePacketListener listener) {
		messagePacketListener.add(listener);
	}
	
	@Override
	public void addListener(final MessagePlainTextListener listener) {
		messagePlainTextListener.add(listener);
	}
	
	@Override
	public void removeListener(final MessagePacketListener listener) {
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
	public void removeListener(final MessagePlainTextListener listener) {
		if (messagePlainTextListener.contains(listener)) {
			messagePlainTextListener.remove(listener);
		}
	}
	
	/**
	 * Notify all listeners about the given Event that contains the <code>MessagePacket</code>.
	 * 
	 * @param event The event that has to be send to all listeners.
	 */
	public void fireMessagePacketEvent(final MessageEvent<MessagePacket> event) {
		final List<MessagePacketListener> listeners = this.messagePacketMap.get(event.getMessage().getType());
		if (listeners != null) {
			for (final MessagePacketListener listener : listeners.toArray(new MessagePacketListener[listeners.size()])) {
				listener.onMessagePacketReceived(event);
			}
		}
		for (final MessagePacketListener listener : messagePacketListener.toArray(new MessagePacketListener[messagePacketListener.size()])) {
			listener.onMessagePacketReceived(event);
		}
	}
	
	/**
	 * Fire the given event to all listeners.
	 * 
	 * @param event A message event.
	 */
	public void fireMessagePlainTextEvent(final MessageEvent<MessagePlainText> event) {
		for (final MessagePlainTextListener listener : messagePlainTextListener.toArray(new MessagePlainTextListener[messagePlainTextListener.size()])) {
			listener.onMessagePlainTextReceived(event);
		}
	}
}
