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

public class resetOperation extends AbstractOperation<Void> {

	Timeout request = null;
	
	public resetOperation(RpcController controller, RpcCallback<EmptyAnswer> done, Subject user, ClientID id, Timeout request) {
		super(controller, done, user, id);
		this.request =  request;
		message = new ReverseMessage(request.getOperationKey(),ServerRpcController.getRpcChannel(controller));
	}

	@Override
	protected void operate() {
		
		if (!user.isPermitted("write:program")) {
			controller.setFailed("Unauthorized: You are not allowed to write");
			done.run(null);
			return;
		}
		
		// erzeugen eines OperationHandle zur der Operation
		OperationHandle <Void> handle = deviceAsync.reset(request.getTimeout(), getAsyncAdapter());
		
		// ein channel-einzigartiger OperationKey wird vom Client zu jeder Operation mitgeschickt
		id.setHandleElement(request.getOperationKey(), handle);
		
		// ausfuehren des Callbacks
		done.run(EmptyAnswer.newBuilder().build());
		
	}
	
	

}
