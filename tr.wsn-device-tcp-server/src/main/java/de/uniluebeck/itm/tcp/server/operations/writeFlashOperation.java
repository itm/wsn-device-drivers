package de.uniluebeck.itm.tcp.server.operations;

import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.apache.shiro.subject.Subject;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.googlecode.protobuf.pro.duplex.execute.ServerRpcController;

import de.uniluebeck.itm.devicedriver.async.OperationHandle;
import de.uniluebeck.itm.tcp.server.utils.ClientID;
import de.uniluebeck.itm.tcp.server.utils.ReverseMessage;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.FlashData;

public class writeFlashOperation extends AbstractOperation<Void> {

	FlashData request = null;
	
	public writeFlashOperation(RpcController controller, RpcCallback<EmptyAnswer> done, Subject user, ClientID id, FlashData request) {
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

		byte[] data = request.getDataList().get(0).toByteArray();
		
		Checksum checksum = new CRC32();
		checksum.update(data,0,data.length);
		
		if(request.getCrc() != checksum.getValue()){
			controller.setFailed("CRC-Error");
			done.run(null);
		}else{
		
			// erzeugen eines OperationHandle zur der Operation
			OperationHandle <Void> handle = deviceAsync.writeFlash(request.getAddress(), data, request.getLength(), request.getTimeout(), getAsyncAdapter());
			
			// ein channel-einzigartiger OperationKey wird vom Client zu jeder Operation mitgeschickt
			id.setHandleElement(request.getOperationKey(), handle);
			
			// ausfuehren des Callbacks
			done.run(EmptyAnswer.newBuilder().build());
		}
		
	}
	
	

}
