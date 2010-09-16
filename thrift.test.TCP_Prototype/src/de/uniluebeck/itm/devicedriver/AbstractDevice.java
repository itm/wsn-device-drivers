package de.uniluebeck.itm.devicedriver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public abstract class AbstractDevice implements Device {
	private final Map<Integer, List<MessagePacketListener>> listeners = new TreeMap<Integer, List<MessagePacketListener>>();

	@Override
	public void addMessagePacketListener(MessagePacketListener listener, int... types) {
		for (final int type : types) {
			if (!listeners.containsKey(type)) {
				listeners.put(type, new ArrayList<MessagePacketListener>());
			}
			listeners.get(type).add(listener);
		}
	}
	
	@Override
	public void addMessagePacketListener(MessagePacketListener listener, PacketType... types) {
		for (final PacketType type : types) {
			final int key = type.getValue();
			if (!listeners.containsKey(type)) {
				listeners.put(key, new ArrayList<MessagePacketListener>());
			}
			listeners.get(key).add(listener);
		}
	}
	
	@Override
	public void removeMessagePacketListener(MessagePacketListener listener) {
		for (List<MessagePacketListener> listeners : this.listeners.values()) {
			listeners.remove(listener);
		}
	}
	
	protected void notifyMessagePacketListener(MessagePacket packet) {
		List<MessagePacketListener> listeners = this.listeners.get(packet.getType());
		for (MessagePacketListener listener : listeners.toArray(new MessagePacketListener[listeners.size()])) {
			listener.onMessagePacketReceived(packet);
			listener.onMessagePlainTextReceived(new MessagePlainText(packet.getContent()));
		}
	}
}
