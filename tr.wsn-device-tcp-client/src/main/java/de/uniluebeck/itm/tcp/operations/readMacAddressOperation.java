package de.uniluebeck.itm.tcp.operations;

import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.tcp.files.PacketServiceAnswerImpl;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.Timeout;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.Operations.BlockingInterface;


public class readMacAddressOperation extends AbstractOperation<MacAddress> {

	long timeout = 0L;
	
	public readMacAddressOperation(RpcClientChannel channel, AsyncCallback<MacAddress> callback, BlockingInterface operationService, PacketServiceAnswerImpl packetServiceAnswerImpl, long timeout){
		super(channel,packetServiceAnswerImpl, operationService, callback);
		this.timeout = timeout;
	}
	
	public void operate() throws ServiceException{
		
		Timeout request = Timeout.newBuilder().setOperationKey(String.valueOf(controller.toString())).setTimeout(timeout).build();

		setOperationKey(request.getOperationKey());
		
		packetServiceAnswerImpl.addCallback(request.getOperationKey(), callback);
		
//		operationService.readMac(controller, request, new RpcCallback<EmptyAnswer>() {
//			@Override
//			public void run(EmptyAnswer parameter) {
//				if(controller.failed()){
//					callback.onFailure(new Throwable(controller.errorText()));
//				}
//			}
//		});

		operationService.readMac(controller, request);
	}
}
