package de.uniluebeck.itm.rsc.remote.client.utils;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;

import de.uniluebeck.itm.rsc.drivers.core.ChipType;
import de.uniluebeck.itm.rsc.drivers.core.MacAddress;
import de.uniluebeck.itm.rsc.drivers.core.MessagePacket;
import de.uniluebeck.itm.rsc.drivers.core.MessagePacketListener;
import de.uniluebeck.itm.rsc.drivers.core.MessagePlainText;
import de.uniluebeck.itm.rsc.drivers.core.MessagePlainTextListener;
import de.uniluebeck.itm.rsc.drivers.core.async.AsyncCallback;
import de.uniluebeck.itm.rsc.drivers.core.event.MessageEvent;
import de.uniluebeck.itm.rsc.remote.files.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.rsc.remote.files.MessageServiceFiles.FailureException;
import de.uniluebeck.itm.rsc.remote.files.MessageServiceFiles.ListenerData;
import de.uniluebeck.itm.rsc.remote.files.MessageServiceFiles.OpKey;
import de.uniluebeck.itm.rsc.remote.files.MessageServiceFiles.PacketServiceAnswer;
import de.uniluebeck.itm.rsc.remote.files.MessageServiceFiles.ReverseAnswer;
import de.uniluebeck.itm.rsc.remote.files.MessageServiceFiles.changeMessage;

/**
 * Implements the methods for the ReverseRPC
 * @author Andreas Maier
 *
 */
public class PacketServiceAnswerImpl implements PacketServiceAnswer.Interface {

	/**
	 * the logger.
	 */
	private final static Logger log = LoggerFactory.getLogger(PacketServiceAnswerImpl.class);
	
	/**
	 * packetListenerList
	 */
	private final HashMap<String, MessagePacketListener> packetListenerList = new HashMap<String, MessagePacketListener>();
	/**
	 * plainTextListenerList
	 */
	private final HashMap<String, MessagePlainTextListener> plainTextListenerList = new HashMap<String, MessagePlainTextListener>();
	/**
	 * callbackList
	 */
	private final HashMap<String, AsyncCallback<?>> callbackList = new HashMap<String, AsyncCallback<?>>();

	/**
	 * standard constructor
	 */
	public PacketServiceAnswerImpl() {
	}

	/**
	 * constructor
	 * @param key the key for a listener
	 * @param listener Add a MessagePacketListener to the list of MessagePacketListener
	 */
	public void addPacketListener(final String key, final MessagePacketListener listener) {
		packetListenerList.put(key, listener);
	}
	
	/**
	 * Add a MessagePlainTextListener to the list of MessagePlainTextListener
	 * @param key the key for a listener 
	 * @param listener a MessagePlainTextListener
	 */
	public void addPlainTextListener(final String key,
			final MessagePlainTextListener listener) {
		plainTextListenerList.put(key, listener);
	}

	/**
	 * remove a MessagePacketListener from the list of MessagePacketListener
	 * @param key the key of a listener 
	 */
	public void removePacketListener(final String key) {
		packetListenerList.remove(key);
	}

	/**
	 * remove a MessagePlainTextListener from the list of MessagePlainTextListener
	 * @param key the key of a listener 
	 */
	public void removePlainTextListener(final String key) {
		plainTextListenerList.remove(key);
	}

	/**
	 * Get a callback from the saved callbacks
	 * @param key the key for the requested callback
	 * @return the callback for a key
	 */
	public AsyncCallback<?> getCallback(final String key) {
		return callbackList.get(key);
	}

	/**
	 * Get all saved callbacks
	 * @return all saved callbacks
	 */
	public HashMap<String, AsyncCallback<?>> getCallbackMap() {
		return callbackList;
	}

	/**
	 * add a callback to the saved callbacks
	 * @param key the key for the callback
	 * @param callback the callback which should be saved
	 */
	public void addCallback(final String key, final AsyncCallback<?> callback) {
		this.callbackList.put(key, callback);
	}

	/**
	 * remove a callback from the saved callbacks
	 * @param key the key for the callback which should be deleted
	 */
	public void removeCallback(final String key) {
		this.callbackList.remove(key);
	}

	/**
	 * listen for a MessagePacket from the Server
	 */
	@Override
	public void sendReversePacketMessage(final RpcController controller,
			final ListenerData request, final RpcCallback<EmptyAnswer> done) {

		/* find the right callback, create a new Message and call onMessagePacketReceived */
		packetListenerList.get(request.getOperationKey())
				.onMessagePacketReceived(
						new MessageEvent<MessagePacket>(request.getSource(),
								new MessagePacket(request.getType(), request
										.getDataList().get(0).toByteArray())));

		/* return a empty Answer to the Server to quit this connection */
		done.run(EmptyAnswer.newBuilder().build());

	}

	/**
	 * listen for a MessagePlainTextPacket from the Server
	 */
	@Override
	public void sendReversePlainTextMessage(final RpcController controller,
			final ListenerData request, final RpcCallback<EmptyAnswer> done) {

		plainTextListenerList.get(request.getOperationKey())
				.onMessagePlainTextReceived(
						new MessageEvent<MessagePlainText>(request.getSource(),
								new MessagePlainText(request.toByteArray())));
		done.run(EmptyAnswer.newBuilder().build());

	}

	/**
	 * listen for an onExecute-Event from the Server
	 */
	@Override
	public void reverseExecuteEvent(final RpcController controller, final OpKey request,
			final RpcCallback<EmptyAnswer> done) {

		/* If a onSuccess-Message arrived before a onExecute-Message, the onExecute-Message will be ignored */
		if(null != getCallback(request.getOperationKey())){
			getCallback(request.getOperationKey()).onExecute();
			done.run(EmptyAnswer.newBuilder().build());
		}
	}

	/**
	 * listen for an onCancel-Event from the Server
	 */
	@Override
	public void reverseOnCancel(final RpcController controller, final OpKey request,
			final RpcCallback<EmptyAnswer> done) {
		
		/* If a onSuccess-Message arrived before a onCancel-Message, the onCancel-Message will be ignored */
		if(null != getCallback(request.getOperationKey())){
			getCallback(request.getOperationKey()).onCancel();
			removeCallback(request.getOperationKey());
			done.run(EmptyAnswer.newBuilder().build());
		}
	}
	
	/**
	 * listen for an onFailure-Event from the Server
	 */
	@Override
	public void reverseOnFailure(final RpcController controller,
			final FailureException request, final RpcCallback<EmptyAnswer> done) {
		
		Exception exception = null;
		Class<?> except;

		try {
			/* rebuild the right Exception with Reflections */
			except = Class.forName(request.getExceptionName());
			/* finden des richtigen Konstruktors */
			for(final Constructor<?> constructor : except.getConstructors()){
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
		}
		
		/* If a onSuccess-Message arrived before a onFailure-Message, the onFailure-Message will be ignored */
		if(null != request.getOperationKey()){
			getCallback(request.getOperationKey()).onFailure(exception);
			removeCallback(request.getOperationKey());
			done.run(EmptyAnswer.newBuilder().build());
		}
	}
	
	/**
	 * listen for an onProgressChange-Event from the Server
	 */
	@Override
	public void reverseChangeEvent(final RpcController controller,
			final changeMessage request, final RpcCallback<EmptyAnswer> done) {
			
		/* If a onSuccess-Message arrived before a onProgressChange-Message, the onProgressChange-Message will be ignored */
		if (null != request.getOperationKey()) {
			getCallback(request.getOperationKey()).onProgressChange(
						Float.parseFloat(request.getQuery()));
			done.run(EmptyAnswer.newBuilder().build());
		}

	}

	/**
	 * listen for an onSuccess-Event from the Server
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void reverseSuccess(final RpcController controller, final ReverseAnswer request,
			final RpcCallback<EmptyAnswer> done) {

		/* if the answer has the type OpKey */
		if (request.hasSuccess()) {
			final AsyncCallback<Void> call = (AsyncCallback<Void>) getCallback(request
					.getSuccess().getOperationKey());
			call.onSuccess(null);
			/* if the answer has the type ChipData */
		} else if (request.hasChipData()) {
			final AsyncCallback<ChipType> call = (AsyncCallback<ChipType>) getCallback(request
					.getChipData().getOperationKey());
			call.onSuccess(ChipType.valueOf(request.getChipData().getType()));
			/* if the answer has the type MacData */
		} else if (request.hasMacAddress()) {
			final AsyncCallback<MacAddress> call = (AsyncCallback<MacAddress>) getCallback(request
					.getMacAddress().getOperationKey());
			call.onSuccess(new MacAddress(request.getMacAddress()
					.getMACADDRESSList().get(0).toByteArray()));
			/* if the answer has the type ByteData */
		} else if (request.hasData()) {
			final AsyncCallback<byte[]> call = (AsyncCallback<byte[]>) getCallback(request
					.getData().getOperationKey());
			call
					.onSuccess(request.getData().getDataList().get(0)
							.toByteArray());
		}
		removeCallback(request.getSuccess().getOperationKey());

	}
}
