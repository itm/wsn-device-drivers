package de.uniluebeck.itm.tcp.files;

import java.util.HashMap;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;

import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.MessagePacketListener;
import de.uniluebeck.itm.devicedriver.MessagePlainText;
import de.uniluebeck.itm.devicedriver.MessagePlainTextListener;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.event.MessageEvent;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.ListenerData;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.MacData;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.OpKey;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.PacketServiceAnswer;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.clientMessage;

// Implementierung der Methoden fuer das ReverseRPC
public class PacketServiceAnswerImpl implements PacketServiceAnswer.Interface {

	private HashMap<String, MessagePacketListener> packetListenerList = new HashMap<String, MessagePacketListener>();
	private HashMap<String, MessagePlainTextListener> plainTextListenerList = new HashMap<String, MessagePlainTextListener>();
	//@SuppressWarnings("unchecked")
	private HashMap<String, AsyncCallback<?>> callbackList = new HashMap<String, AsyncCallback<?>>();

	public PacketServiceAnswerImpl() {
	}

	public void addPacketListener(String key, MessagePacketListener listener) {
		packetListenerList.put(key, listener);
	}

	public void addPlainTextListener(String key,
			MessagePlainTextListener listener) {
		plainTextListenerList.put(key, listener);
	}

	public void removePacketListener(String key) {
		packetListenerList.remove(key);
	}

	public void removePlainTextListener(String key) {
		plainTextListenerList.remove(key);
	}

	//@SuppressWarnings("unchecked")
	public AsyncCallback<?> getCallback(String key) {
		return callbackList.get(key);
	}

	@SuppressWarnings("unchecked")
	public void addCallback(String key, AsyncCallback callback) {
		this.callbackList.put(key, callback);
	}

	@Override
	public void sendReversePacketMessage(RpcController controller,
			ListenerData request, RpcCallback<EmptyAnswer> done) {

		packetListenerList.get(request.getOperationKey())
				.onMessagePacketReceived(
						new MessageEvent<MessagePacket>(request.getSource(),
								new MessagePacket(request.getType(), request
										.toByteArray())));
		done.run(EmptyAnswer.newBuilder().build());

	}

	@Override
	public void sendReversePlainTextMessage(RpcController controller,
			ListenerData request, RpcCallback<EmptyAnswer> done) {

		plainTextListenerList.get(request.getOperationKey())
				.onMessagePlainTextReceived(
						new MessageEvent<MessagePlainText>(request.getSource(),
								new MessagePlainText(request.toByteArray())));
		done.run(EmptyAnswer.newBuilder().build());

	}

	@Override
	public void sendReverseMessage(RpcController controller,
			clientMessage request, RpcCallback<EmptyAnswer> done) {
		
		getCallback(request.getOperationKey()).onProgressChange(Float.parseFloat(request.getQuery()));
		
		done.run(EmptyAnswer.newBuilder().build());
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void reverseSuccessMac(RpcController controller, MacData request,
			RpcCallback<EmptyAnswer> done) {
		
		AsyncCallback<MacAddress> call = (AsyncCallback<MacAddress>) getCallback(request.getOperationKey());
		call.onSuccess(new MacAddress(request.getMACADDRESSList().get(0).toByteArray()));
		
		done.run(EmptyAnswer.newBuilder().build());
		
	}

	@Override
	public void reverseSuccessMessage(RpcController controller, OpKey request,
			RpcCallback<EmptyAnswer> done) {
		
		getCallback(request.getOperationKey()).onSuccess(null);
		done.run(EmptyAnswer.newBuilder().build());
		
	}
	
	

}
