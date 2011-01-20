package de.uniluebeck.itm.tcp.server.operations;

import org.apache.shiro.subject.Subject;

import com.google.protobuf.ByteString;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.googlecode.protobuf.pro.duplex.execute.ServerRpcController;

import de.uniluebeck.itm.devicedriver.async.OperationHandle;
import de.uniluebeck.itm.tcp.server.utils.ClientID;
import de.uniluebeck.itm.tcp.server.utils.ReverseMessage;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.ByteData;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.FlashData;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.ReverseAnswer;

public class readFlashOperation extends AbstractOperation<byte[]> {

	FlashData request = null;
	
	public readFlashOperation(RpcController controller, RpcCallback<EmptyAnswer> done, Subject user, ClientID id, FlashData request){
		super(controller,done, user, id);
		this.request = request;
		message = new ReverseMessage(request.getOperationKey(),ServerRpcController.getRpcChannel(controller));
	}
	
	@Override
	public void setOnSuccess(byte[] result) {
		if(!id.getCalledGet(request.getOperationKey())){
			ByteData data = ByteData.newBuilder().setOperationKey(request.getOperationKey()).addData(ByteString.copyFrom(result)).build();
			message.reverseSuccess(ReverseAnswer.newBuilder().setData(data).build());
		}
	}
	
	@Override
	protected void operate(){

		// erzeugen eines OperationHandle zur der Operation
		OperationHandle <byte[]> handle = deviceAsync.readFlash(request.getAddress(), request.getLength(), request.getTimeout(), getAsyncAdapter());
		
		// ein channel-einzigartiger OperationKey wird vom Client zu jeder Operation mitgeschickt
		id.setHandleElement(request.getOperationKey(), handle);
		
		done.run(EmptyAnswer.newBuilder().build());
	}
	
}
