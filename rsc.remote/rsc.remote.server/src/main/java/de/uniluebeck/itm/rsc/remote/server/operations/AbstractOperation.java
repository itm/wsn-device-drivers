package de.uniluebeck.itm.rsc.remote.server.operations;

import com.google.protobuf.RpcCallback;

import de.uniluebeck.itm.rsc.drivers.core.async.AsyncAdapter;
import de.uniluebeck.itm.rsc.drivers.core.async.DeviceAsync;
import de.uniluebeck.itm.rsc.remote.files.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.rsc.remote.files.MessageServiceFiles.OpKey;
import de.uniluebeck.itm.rsc.remote.files.MessageServiceFiles.ReverseAnswer;
import de.uniluebeck.itm.rsc.remote.server.utils.ClientID;
import de.uniluebeck.itm.rsc.remote.server.utils.ReverseMessage;

/**
 * 
 * @author Andreas Maier
 * 
 * @param <T>
 *            Type of the Answer from the Device
 */
public abstract class AbstractOperation<T> {

	// private static Logger log =
	// LoggerFactory.getLogger(AbstractOperation.class);

	/**
	 * RpcCallback<EmptyAnswer>
	 */
	private RpcCallback<EmptyAnswer> done = null;
	/**
	 * ClientID Instance
	 */
	private ClientID id;
	/**
	 * DeviceAsync-Instance
	 */
	private DeviceAsync deviceAsync;

	/**
	 * ReverseMessage-Instance
	 */
	private ReverseMessage message;

	/**
	 * Constructor
	 * 
	 * @param controller
	 *            the RpcController for the Operation
	 * @param done
	 *            the RpcCallback<EmptyAnswer> for the Operation
	 * @param user
	 *            the Shiro-User-Object
	 * @param id
	 *            the ClientID-Instance for the Operation
	 */
	public AbstractOperation(final RpcCallback<EmptyAnswer> done,
			final ClientID id) {
		this.done = done;
		this.id = id;
		this.deviceAsync = id.getDevice();
	}

	public ReverseMessage getMessage() {
		return message;
	}

	/**
	 * set the ReverseMessage and add it to the ReverseMessageList in the clientID-Object
	 * @param message the ReverseMessage for this operation
	 */
	public void setMessage(final ReverseMessage message) {
		this.message = message;
		id.getReverseMessageList().put(message.getOperationKey(), message);
	}

	public DeviceAsync getDeviceAsync() {
		return deviceAsync;
	}

	public RpcCallback<EmptyAnswer> getDone() {
		return done;
	}

	public ClientID getId() {
		return id;
	}

	/**
	 * Method to start the Operation
	 */
	abstract protected void execute();

	/**
	 * set the OnSuccess-Method for the AsyncAdapter <br>
	 * 
	 * @param result
	 *            the result from the Device
	 */
	public void setOnSuccess(final T result) {
		message.reverseSuccess(ReverseAnswer.newBuilder().setSuccess(
				OpKey.newBuilder().setOperationKey(message.getOperationKey()))
				.build());
	}

	/**
	 * set the OnExecute-Method for the AsyncAdapter
	 */
	public void setOnExecute() {
		message.reverseExecute();
	}

	/**
	 * set the OnCancel-Method for the AsyncAdapter
	 */
	public void setOnCancel() {
		message.reverseOnCancel();
	}

	/**
	 * set the OnFailure-Method for the AsyncAdapter
	 * 
	 * @param throwable
	 *            the Exception thrown by the device
	 */
	public void setOnFailure(final Throwable throwable) {
		message.reverseOnFailure(throwable);
	}

	/**
	 * set the OnProgressChange-Method for the AsyncAdapter
	 * 
	 * @param fraction
	 *            value from the device
	 */
	public void setOnProgressChange(final float fraction) {
		message.reverseChangeEvent(String.valueOf(fraction));
	}

	/**
	 * Create a AsyncAdapter
	 * 
	 * @return the AsyncAdapter for the Operation
	 */
	public AsyncAdapter<T> getAsyncAdapter() {
		return new AsyncAdapter<T>() {

			@Override
			public void onExecute() {
				setOnExecute();
			}

			@Override
			public void onCancel() {
				setOnCancel();
			}

			@Override
			public void onFailure(final Throwable throwable) {
				setOnFailure(throwable);
			}

			@Override
			public void onProgressChange(final float fraction) {
				setOnProgressChange(fraction);
			}

			@Override
			public void onSuccess(final T result) {
				setOnSuccess(result);
			}
		};
	}
}
