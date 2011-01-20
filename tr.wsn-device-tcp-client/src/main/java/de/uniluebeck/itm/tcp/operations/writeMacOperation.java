package de.uniluebeck.itm.tcp.operations;

import com.google.protobuf.ByteString;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.tcp.files.PacketServiceAnswerImpl;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.MacData;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.Operations.BlockingInterface;

public class writeMacOperation extends AbstractOperation<Void>{
	
	MacAddress macAddress;
	long timeout;

	public writeMacOperation(RpcClientChannel channel, PacketServiceAnswerImpl packetServiceAnswerImpl, BlockingInterface operationService, AsyncCallback<Void> callback, MacAddress macAddress, long timeout) {
		super(channel,packetServiceAnswerImpl, operationService, callback);
		this.macAddress = macAddress;
		this.timeout = timeout;
	}

	public void operate() throws ServiceException {
		
		MacData request = MacData.newBuilder().addMACADDRESS(ByteString.copyFrom(macAddress.getMacBytes())).setTimeout(timeout).setOperationKey(controller.toString()).build();
		
		setOperationKey(request.getOperationKey());
		
		packetServiceAnswerImpl.addCallback(request.getOperationKey(), callback);
		
//		operationService.writeMac(controller, address, new RpcCallback<EmptyAnswer>() {
//			
//			@Override
//			public void run(EmptyAnswer parameter) {
//				if(controller.failed()){
//					callback.onFailure(new Throwable(controller.errorText()));
//				}
//			}
//		});
		
		operationService.writeMac(controller, request);
	}
}
