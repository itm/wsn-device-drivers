package de.uniluebeck.itm.tcp.server.operations;

import org.apache.shiro.subject.Subject;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;

import de.uniluebeck.itm.devicedriver.async.AsyncAdapter;
import de.uniluebeck.itm.devicedriver.async.DeviceAsync;
import de.uniluebeck.itm.tcp.server.utils.ClientID;
import de.uniluebeck.itm.tcp.server.utils.ReverseMessage;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.OpKey;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.ReverseAnswer;

/**
 * 
 * @author Andreas Maier
 *
 * @param <T> Type of the Answer from the Device
 */
public abstract class AbstractOperation<T> {
	
	//private static Logger log = LoggerFactory.getLogger(AbstractOperation.class);
	
	/**
	 * RpcController
	 */
	private RpcController controller = null;
	/**
	 * RpcCallback<EmptyAnswer>
	 */
	private RpcCallback<EmptyAnswer> done = null;
	/**
	 * Shiro User
	 */
	private Subject user;
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
	 * @param controller the RpcController for the Operation
	 * @param done the RpcCallback<EmptyAnswer> for the Operation
	 * @param user the Shiro-User-Object
	 * @param id the ClientID-Instance for the Operation
	 */
	public AbstractOperation(final RpcController controller, final RpcCallback<EmptyAnswer> done, final Subject user, final ClientID id) {
		this.controller = controller;
		this.done = done;
		this.user = user;
		this.id = id;
	}
	
	public ReverseMessage getMessage() {
		return message;
	}

	public void setMessage(final ReverseMessage message) {
		this.message = message;
	}
	
	public DeviceAsync getDeviceAsync() {
		return deviceAsync;
	}
	
	public RpcController getController() {
		return controller;
	}

	public RpcCallback<EmptyAnswer> getDone() {
		return done;
	}

	public Subject getUser() {
		return user;
	}

	public ClientID getId() {
		return id;
	}

	/**
	 * Execute the different specific Operation-Functions on a physical-Device</br>
	 * Example:</br><PRE>
	 * 	final OperationHandle <Void> handle = getDeviceAsync().'OPERATIONNAME'('...',getAsyncAdapter());
	 * 	getId().setHandleElement(request.getOperationKey(), handle);
	 * 	getDone().run('...'); </PRE>
	 */
	abstract protected void operate();
	
	/**
	 * Method to start the Operation
	 */
	public void execute(){
		// Shiro 
		if(user==null || !user.isAuthenticated()){
			controller.setFailed("Sie sind nicht authentifiziert!");
			done.run(null);
			return;
		}
		
		this.deviceAsync = id.getDevice();
		operate();
	}
	
	/**
	 * set the OnSuccess-Method for the AsyncAdapter <br>
	 * @param result the result from the Device
	 */
	public void setOnSuccess(final T result) {
		message.reverseSuccess(ReverseAnswer.newBuilder().setSuccess(OpKey.newBuilder().setOperationKey(message.getOperationKey())).build());
	}
	/**
	 * set the OnExecute-Method for the AsyncAdapter
	 */
	public void setOnExecute(){
		message.reverseExecute();
	}
	/**
	 * set the OnCancel-Method for the AsyncAdapter
	 */
	public void setOnCancel(){
		message.reverseOnCancel();
	}
	/**
	 * set the OnFailure-Method for the AsyncAdapter
	 * @param throwable the Exception thrown by the device
	 */
	public void setOnFailure(final Throwable throwable){
		message.reverseOnFailure(throwable);
	}
	/**
	 * set the OnProgressChange-Method for the AsyncAdapter
	 * @param fraction value from the device
	 */
	public void setOnProgressChange(final float fraction){
		message.reverseChangeEvent(String.valueOf(fraction));
	}
	
	/**
	 * Create a AsyncAdapter 
	 * @return the AsyncAdapter for the Operation
	 */
	public  AsyncAdapter<T> getAsyncAdapter(){
		return new AsyncAdapter<T>(){
			
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
