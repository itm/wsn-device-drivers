package de.uniluebeck.itm.tcp.server.operations;

import org.apache.shiro.subject.Subject;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.googlecode.protobuf.pro.duplex.execute.ServerRpcController;

import de.uniluebeck.itm.devicedriver.async.OperationHandle;
import de.uniluebeck.itm.tcp.server.utils.ClientID;
import de.uniluebeck.itm.tcp.server.utils.ReverseMessage;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.Timeout;

/**
 * The reset Operation
 * @author Andreas Maier
 *
 */
public class ResetOperation extends AbstractOperation<Void> {

	/**
	 * the request of type Timeout
	 */
	private Timeout request = null;
	
	/**
	 * Constructor
	 * @param controller the RpcController for a reset Operation
	 * @param done the RpcCallback<EmptyAnswer> for a reset Operation
	 * @param user the Shiro-User-Object a reset Operation
	 * @param id the ClientID-Instance for a reset Operation
	 * @param request the Timeout request for a reset Operation
	 */
	public ResetOperation(final RpcController controller, final RpcCallback<EmptyAnswer> done, final Subject user, final ClientID id, final Timeout request) {
		super(controller, done, user, id);
		this.request =  request;
		setMessage(new ReverseMessage(request.getOperationKey(),ServerRpcController.getRpcChannel(controller)));
	}

	@Override
	protected void operate() {
		
		if (!getUser().isPermitted("write:program")) {
			getController().setFailed("Unauthorized: You are not allowed to write");
			getDone().run(null);
			return;
		}
		
		// erzeugen eines OperationHandle zur der Operation
		final OperationHandle <Void> handle = getDeviceAsync().reset(request.getTimeout(), getAsyncAdapter());
		
		// ein channel-einzigartiger OperationKey wird vom Client zu jeder Operation mitgeschickt
		getId().setHandleElement(request.getOperationKey(), handle);
		
		// ausfuehren des Callbacks
		getDone().run(EmptyAnswer.newBuilder().build());
		
	}
	
	

}
