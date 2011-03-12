package de.uniluebeck.itm.tcp.server.operations;

import org.apache.shiro.subject.Subject;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.googlecode.protobuf.pro.duplex.execute.ServerRpcController;

import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;
import de.uniluebeck.itm.tcp.server.utils.ClientID;
import de.uniluebeck.itm.tcp.server.utils.OperationType;
import de.uniluebeck.itm.tcp.server.utils.ReverseMessage;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.sendData;

/**
 * The send Operation
 * @author Andreas Maier
 *
 */
public class SendOperation extends AbstractOperation<Void> {

	/**
	 * the request of type sendData
	 */
	private sendData request = null;
	
	/**
	 * Constructor
	 * @param controller the RpcController for a send Operation
	 * @param done the RpcCallback<EmptyAnswer> for a send Operation
	 * @param user the Shiro-User-Object a send Operation
	 * @param id the ClientID-Instance for a send Operation
	 * @param request the sendData request for a send Operation
	 */
	public SendOperation(final RpcController controller, final RpcCallback<EmptyAnswer> done, final Subject user, final ClientID id, final sendData request) {
		super(controller, done, user, id);
		this.request =  request;
		setMessage(new ReverseMessage(request.getOperationKey(),ServerRpcController.getRpcChannel(controller)));
		setOperationType(OperationType.WRITEOPERATION);
	}

	@Override
	protected void operate() {
		
		if (!getUser().isPermitted("write:program")) {
			getController().setFailed("Unauthorized: You are not allowed to write");
			getDone().run(null);
			return;
		}
		
		final MessagePacket packet = new MessagePacket(request.getType(), request.getDataList().get(0).toByteArray());
		
		// erzeugen eines OperationHandle zur der Operation
		final OperationHandle <Void> handle = getDeviceAsync().send(packet, request.getTimeout(), getAsyncAdapter());
		
		// ein channel-einzigartiger OperationKey wird vom Client zu jeder Operation mitgeschickt
		getId().addHandleElement(request.getOperationKey(), handle);
		
		// hinzufuegen des OperationType dieser operation zur OperationTypeList
		getId().addOperationType(request.getOperationKey(), getOperationType());
		
		// ausfuehren des Callbacks
		getDone().run(EmptyAnswer.newBuilder().build());
		
	}
	
	

}
