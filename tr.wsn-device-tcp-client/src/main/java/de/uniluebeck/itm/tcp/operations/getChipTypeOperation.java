package de.uniluebeck.itm.tcp.operations;

import com.google.protobuf.RpcCallback;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;
import de.uniluebeck.itm.tcp.files.PacketServiceAnswerImpl;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.Operations;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.Timeout;


public class getChipTypeOperation extends Operation<ChipType> {

	long timeout = 0L;
	
	public getChipTypeOperation(RpcClientChannel channel, AsyncCallback<ChipType> callback, Operations.Interface operationService, PacketServiceAnswerImpl packetServiceAnswerImpl, long timeout){
		super(channel,packetServiceAnswerImpl, operationService, callback);
		this.timeout = timeout;
	}
	
	public OperationHandle<ChipType> operate(){
		
		Timeout request = Timeout.newBuilder().setOperationKey(controller.toString()).setTimeout(timeout).build();

		packetServiceAnswerImpl.addCallback(request.getOperationKey(), callback);
		
		operationService.getChipType(controller, request, new RpcCallback<EmptyAnswer>() {
			@Override
			public void run(EmptyAnswer parameter) {
				if(controller.failed()){
					callback.onFailure(new Throwable(controller.errorText()));
				}
			}
		});
		return getHandle();
	}
}
