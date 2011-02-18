package de.uniluebeck.itm.tcp.client.files;

import java.lang.reflect.Constructor;
import java.rmi.RemoteException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.Connection;
import de.uniluebeck.itm.devicedriver.Device;
import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.MessagePacketListener;
import de.uniluebeck.itm.devicedriver.MessagePlainText;
import de.uniluebeck.itm.devicedriver.MessagePlainTextListener;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.event.MessageEvent;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.FailureException;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.ListenerData;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.OpKey;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.PacketServiceAnswer;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.ReverseAnswer;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.clientMessage;

// Implementierung der Methoden fuer das ReverseRPC
public class PacketServiceAnswerImpl implements PacketServiceAnswer.Interface {

	/**
	 * the logger.
	 */
	private static Logger log = LoggerFactory.getLogger(PacketServiceAnswerImpl.class);
	
	private HashMap<String, MessagePacketListener> packetListenerList = new HashMap<String, MessagePacketListener>();
	private HashMap<String, MessagePlainTextListener> plainTextListenerList = new HashMap<String, MessagePlainTextListener>();

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

	public AsyncCallback<?> getCallback(String key) {
		return callbackList.get(key);
	}

	public HashMap<String, AsyncCallback<?>> getCallbackMap() {
		return callbackList;
	}

	public void addCallback(String key, AsyncCallback<?> callback) {
		this.callbackList.put(key, callback);
	}

	public void removeCallback(String key) {
		this.callbackList.remove(key);
	}

	@Override
	public void sendReversePacketMessage(RpcController controller,
			ListenerData request, RpcCallback<EmptyAnswer> done) {

		packetListenerList.get(request.getOperationKey())
				.onMessagePacketReceived(
						new MessageEvent<MessagePacket>(request.getSource(),
								new MessagePacket(request.getType(), request
										.getDataList().get(0).toByteArray())));

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
	public void reverseExecuteEvent(RpcController controller, OpKey request,
			RpcCallback<EmptyAnswer> done) {

		getCallback(request.getOperationKey()).onExecute();
		done.run(EmptyAnswer.newBuilder().build());
	}

	@Override
	public void reverseOnCancel(RpcController controller, OpKey request,
			RpcCallback<EmptyAnswer> done) {
		
		getCallback(request.getOperationKey()).onCancel();
		removeCallback(request.getOperationKey());
		done.run(EmptyAnswer.newBuilder().build());
	}
	
	@Override
	public void reverseOnFailure(RpcController controller,
			FailureException request, RpcCallback<EmptyAnswer> done) {
		
		Exception exception = null;
		Class<?> except;

		try {
			except = Class.forName(request.getExceptionName());
			for(Constructor<?> constructor : except.getConstructors()){
				final Class<?>[] types = constructor.getParameterTypes();
				if (types.length == 1) {
					final Class<?> type = types[0];
					if(type.isInstance(request.getExceptionMessage())){
						exception = (Exception) constructor.newInstance(new Object[] {request.getExceptionMessage()});
						break;
					}
				}
			}
		} catch (final Exception e) {
			log.error(e.getMessage(),e);
			e.printStackTrace();
		}
		
		getCallback(request.getOperationKey()).onFailure(exception);
		
		removeCallback(request.getOperationKey());
		done.run(EmptyAnswer.newBuilder().build());
	}
	
	@Override
	public void reverseChangeEvent(RpcController controller,
			clientMessage request, RpcCallback<EmptyAnswer> done) {

			getCallback(request.getOperationKey()).onProgressChange(
					Float.parseFloat(request.getQuery()));
		done.run(EmptyAnswer.newBuilder().build());

	}

	@SuppressWarnings("unchecked")
	@Override
	public void reverseSuccess(RpcController controller, ReverseAnswer request,
			RpcCallback<EmptyAnswer> done) {

		if (request.hasSuccess()) {
			AsyncCallback<Void> call = (AsyncCallback<Void>) getCallback(request
					.getSuccess().getOperationKey());
			call.onSuccess(null);
		} else if (request.hasChipData()) {
			AsyncCallback<ChipType> call = (AsyncCallback<ChipType>) getCallback(request
					.getChipData().getOperationKey());
			call.onSuccess(ChipType.valueOf(request.getChipData().getType()));
		} else if (request.hasMacAddress()) {
			AsyncCallback<MacAddress> call = (AsyncCallback<MacAddress>) getCallback(request
					.getMacAddress().getOperationKey());
			call.onSuccess(new MacAddress(request.getMacAddress()
					.getMACADDRESSList().get(0).toByteArray()));
		} else if (request.hasData()) {
			AsyncCallback<byte[]> call = (AsyncCallback<byte[]>) getCallback(request
					.getData().getOperationKey());
			call
					.onSuccess(request.getData().getDataList().get(0)
							.toByteArray());
		}
		removeCallback(request.getSuccess().getOperationKey());

	}
}
