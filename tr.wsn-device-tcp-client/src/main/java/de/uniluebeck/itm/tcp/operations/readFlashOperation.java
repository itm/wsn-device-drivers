package de.uniluebeck.itm.tcp.operations;

import com.google.protobuf.ByteString;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.tcp.files.PacketServiceAnswerImpl;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.FlashData;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.Operations.BlockingInterface;

public class readFlashOperation extends AbstractOperation<byte[]> {

	int address = 0;
	int length = 0;
	long timeout = 0L;
	
	public readFlashOperation(RpcClientChannel channel,  AsyncCallback<byte[]> callback, BlockingInterface operationService, PacketServiceAnswerImpl packetServiceAnswerImpl, int address,
			int length, long timeout) {
		super(channel,packetServiceAnswerImpl, operationService, callback);
		this.address = address;
		this.length = length;
		this.timeout = timeout;
	}
	
	@Override
	public void operate() throws ServiceException {
		FlashData request = FlashData.newBuilder().setAddress(address).setLength(length).setTimeout(timeout).setOperationKey(controller.toString()).build();
		
		setOperationKey(request.getOperationKey());
		
		packetServiceAnswerImpl.addCallback(request.getOperationKey(), callback);
		
		operationService.readFlash(controller, request);

	}

}
