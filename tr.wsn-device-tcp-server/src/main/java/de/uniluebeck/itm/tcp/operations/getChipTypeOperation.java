package de.uniluebeck.itm.tcp.operations;

import org.apache.shiro.subject.Subject;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.googlecode.protobuf.pro.duplex.execute.ServerRpcController;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;
import de.uniluebeck.itm.tcp.Server.ClientID;
import de.uniluebeck.itm.tcp.Server.ReverseMessage;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.ChipData;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.ReverseAnswer;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.Timeout;

public class getChipTypeOperation extends AbstractOperation<EmptyAnswer> {

	Timeout request = null;
	
	public getChipTypeOperation(RpcController controller, RpcCallback<EmptyAnswer> done, Subject user, ClientID id, Timeout request) {
		super(controller, done, user, id);
		this.request =  request;
	}

	@Override
	protected void operate() {
		
		final ReverseMessage message = new ReverseMessage(request.getOperationKey(),ServerRpcController.getRpcChannel(controller));

		// erzeugen eines OperationHandle zur der Operation
		OperationHandle <ChipType> handle = deviceAsync.getChipType(request.getTimeout(), new AsyncCallback<ChipType>(){

			@Override
			public void onCancel() {
				controller.setFailed("getChipType was canceled by the device");
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
			public void onSuccess(ChipType result) {
				// ausfuehren des Callbacks
				// message.reverseSuccessMac(result);
				if(!id.getCalledGet()){
					ChipData chipData = ChipData.newBuilder().setOperationKey(request.getOperationKey()).setType(result.name()).build();
					message.reverseSuccess(ReverseAnswer.newBuilder().setChipData(chipData).build());
				}
			}
			
			@Override
			public void onExecute() {
				
			}});
		
		// ein channel-einzigartiger OperationKey wird vom Client zu jeder Operation mitgeschickt
		id.setHandleElement(request.getOperationKey(), handle);
		
		done.run(EmptyAnswer.newBuilder().build());
		
	}
}
