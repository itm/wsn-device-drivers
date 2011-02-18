package de.uniluebeck.itm.tcp.server.operations;

import org.apache.shiro.subject.Subject;

import com.google.protobuf.ByteString;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.googlecode.protobuf.pro.duplex.execute.ServerRpcController;

import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;
import de.uniluebeck.itm.tcp.server.utils.ClientID;
import de.uniluebeck.itm.tcp.server.utils.ReverseMessage;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.MacData;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.ReverseAnswer;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.Timeout;

public class readMacOperation extends AbstractOperation<MacAddress> {

	Timeout request = null;
	
	public readMacOperation(final RpcController controller, final RpcCallback<EmptyAnswer> done, final Subject user, final ClientID id, final Timeout request){
		super(controller,done, user, id);
		this.request = request;
		message = new ReverseMessage(request.getOperationKey(),ServerRpcController.getRpcChannel(controller));
	}
	
	@Override
	public void setOnSuccess(final MacAddress result) {
		if(!id.getCalledGet(request.getOperationKey())){
			final MacData mac = MacData.newBuilder().setOperationKey(request.getOperationKey()).addMACADDRESS(ByteString.copyFrom(result.getMacBytes())).build();
			message.reverseSuccess(ReverseAnswer.newBuilder().setMacAddress(mac).build());
		}
	}
	
	@Override
	protected void operate(){

		// erzeugen eines OperationHandle zur der Operation
		final OperationHandle <MacAddress> handle = deviceAsync.readMac(request.getTimeout(), getAsyncAdapter());
		
		// ein channel-einzigartiger OperationKey wird vom Client zu jeder Operation mitgeschickt
		id.setHandleElement(request.getOperationKey(), handle);
		
		done.run(EmptyAnswer.newBuilder().build());
	}
	
}
