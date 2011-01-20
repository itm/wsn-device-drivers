package de.uniluebeck.itm.tcp.operations;

import java.util.zip.CRC32;
import java.util.zip.Checksum;

import com.google.protobuf.ByteString;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.tcp.files.PacketServiceAnswerImpl;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.FlashData;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.Operations.BlockingInterface;

public class writeFlashOperation extends AbstractOperation<Void> {

	
	int address = 0;
	byte[] data = null;
	int length = 0;
	long timeout = 0L;

	public writeFlashOperation(RpcClientChannel channel,  AsyncCallback<Void> callback, BlockingInterface operationService, PacketServiceAnswerImpl packetServiceAnswerImpl, int address, byte[] data,
			int length, long timeout) {

		super(channel,packetServiceAnswerImpl, operationService, callback);
		this.address = address;
		this.data = data;
		this.length = length;
		this.timeout = timeout;
	}

	public void operate() throws ServiceException {
		
		Checksum checksum = new CRC32();
		checksum.update(data,0,data.length);
		
		FlashData request = FlashData.newBuilder().setAddress(address).addData(ByteString.copyFrom(data)).setLength(length).setTimeout(timeout).setCrc(checksum.getValue()).setOperationKey(controller.toString()).build();
		
		setOperationKey(request.getOperationKey());
		
		packetServiceAnswerImpl.addCallback(request.getOperationKey(), callback);
		
//		operationService.writeFlash(controller, flash, new RpcCallback<EmptyAnswer>() {
//			
//			@Override
//			public void run(EmptyAnswer parameter) {
//				if(controller.failed()){
//					callback.onFailure(new Throwable(controller.errorText()));
//				}
//			}
//		});
		
		operationService.writeFlash(controller, request);
	}
}
