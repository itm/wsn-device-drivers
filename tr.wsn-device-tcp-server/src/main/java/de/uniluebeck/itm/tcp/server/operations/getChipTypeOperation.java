package de.uniluebeck.itm.tcp.server.operations;

import org.apache.shiro.subject.Subject;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.googlecode.protobuf.pro.duplex.execute.ServerRpcController;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;
import de.uniluebeck.itm.tcp.server.utils.ClientID;
import de.uniluebeck.itm.tcp.server.utils.ReverseMessage;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.ChipData;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.ReverseAnswer;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.Timeout;

public class getChipTypeOperation extends AbstractOperation<ChipType> {

	Timeout request = null;
	
	public getChipTypeOperation(RpcController controller, RpcCallback<EmptyAnswer> done, Subject user, ClientID id, Timeout request) {
		super(controller, done, user, id);
		this.request =  request;
		message = new ReverseMessage(request.getOperationKey(),ServerRpcController.getRpcChannel(controller));
	}

	@Override
	public void setOnSuccess(ChipType result){
		// ausfuehren des Callbacks
		if(!id.getCalledGet(request.getOperationKey())){
			ChipData chipData = ChipData.newBuilder().setOperationKey(request.getOperationKey()).setType(result.name()).build();
			message.reverseSuccess(ReverseAnswer.newBuilder().setChipData(chipData).build());
		}
	}
	
	@Override
	protected void operate() {
		
		// erzeugen eines OperationHandle zur der Operation
		OperationHandle <ChipType> handle = deviceAsync.getChipType(request.getTimeout(),getAsyncAdapter());
		
		// ein channel-einzigartiger OperationKey wird vom Client zu jeder Operation mitgeschickt
		id.setHandleElement(request.getOperationKey(), handle);
		
		done.run(EmptyAnswer.newBuilder().build());
		
	}
}
