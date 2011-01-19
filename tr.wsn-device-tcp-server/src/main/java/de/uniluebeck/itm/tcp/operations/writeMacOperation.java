package de.uniluebeck.itm.tcp.operations;

import org.apache.shiro.subject.Subject;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.googlecode.protobuf.pro.duplex.execute.ServerRpcController;

import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;
import de.uniluebeck.itm.tcp.Server.ClientID;
import de.uniluebeck.itm.tcp.Server.ReverseMessage;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.MacData;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.OpKey;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.ReverseAnswer;

public class writeMacOperation extends AbstractOperation<EmptyAnswer> {

	MacData request = null;
	
	public writeMacOperation(RpcController controller, RpcCallback<EmptyAnswer> done, Subject user, ClientID id, MacData request) {
		super(controller, done, user, id);
		this.request =  request;
	}

	@Override
	protected void operate() {
		
		if (!user.isPermitted("write:program")) {
			controller.setFailed("Sie haben nicht die noetigen Rechte!");
			done.run(null);
			return;
		}
		
		final ReverseMessage message = new ReverseMessage(request.getOperationKey(),ServerRpcController.getRpcChannel(controller));
		
		// erzeugen eines OperationHandle zur der Operation
		OperationHandle <Void> handle = deviceAsync.writeMac(new MacAddress(request.getMACADDRESSList().get(0).toByteArray()), request.getTimeout(), new AsyncCallback<Void>(){
			@Override
			public void onCancel() {
				//TODO bessere Fehlermeldung
				controller.setFailed("writeMac wurde vom Device abgebrochen");
				done.run(null);
			}

			@Override
			public void onFailure(Throwable throwable) {
				controller.setFailed(throwable.getMessage());
				done.run(null);
			}

			@Override
			public void onProgressChange(float fraction) {
				message.reverseProgressChange(String.valueOf(fraction));
			}

			@Override
			public void onSuccess(Void result) {
				// ausfuehren des Callbacks
				message.reverseSuccess(ReverseAnswer.newBuilder().setSuccess(OpKey.newBuilder().setOperationKey(request.getOperationKey())).build());
			}

			//TODO wozu onExecute und wo wird es abgefangen
			@Override
			public void onExecute() {
				
			}});
		
		// ein channel-einzigartiger OperationKey wird vom Client zu jeder Operation mitgeschickt
		id.setHandleElement(request.getOperationKey(), handle);
		
		// ausfuehren des Callbacks
		done.run(EmptyAnswer.newBuilder().build());
		
	}
	
	

}
