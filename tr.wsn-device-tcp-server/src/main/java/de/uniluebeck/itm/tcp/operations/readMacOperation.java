package de.uniluebeck.itm.tcp.operations;

import org.apache.shiro.subject.Subject;

import com.google.protobuf.ByteString;
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
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.ReverseAnswer;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.Timeout;

public class readMacOperation extends AbstractOperation<EmptyAnswer> {

	Timeout request = null;
	
	public readMacOperation(RpcController controller, ClientID id, RpcCallback<EmptyAnswer> done, Subject user, Timeout request){
		super(controller,done, user, id);
		this.request = request;
	}
	
	@Override
	protected void operate(){
		
		final ReverseMessage message = new ReverseMessage(request.getOperationKey(),ServerRpcController.getRpcChannel(controller));

		// erzeugen eines OperationHandle zur der Operation
		OperationHandle <MacAddress> handle = deviceAsync.readMac(request.getTimeout(), new AsyncCallback<MacAddress>(){

			@Override
			public void onCancel() {
				//TODO bessere Fehlermeldung
				controller.setFailed("readMac wurde vom Device abgebrochen");
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
			public void onSuccess(MacAddress result) {
				// ausfuehren des Callbacks
				// message.reverseSuccessMac(result);
				if(!id.getCalledGet()){
					MacData mac = MacData.newBuilder().setOperationKey(request.getOperationKey()).addMACADDRESS(ByteString.copyFrom(result.getMacBytes())).build();
					message.reverseSuccess(ReverseAnswer.newBuilder().setMacAddress(mac).build());
				}
			}

			//TODO wozu onExecute und wo wird es abgefangen
			@Override
			public void onExecute() {
				
			}});
		
		// ein channel-einzigartiger OperationKey wird vom Client zu jeder Operation mitgeschickt
		id.setHandleElement(request.getOperationKey(), handle);
		
		done.run(EmptyAnswer.newBuilder().build());
	}
	
}
