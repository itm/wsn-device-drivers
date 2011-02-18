package de.uniluebeck.itm.tcp.client.operations;

import java.util.zip.CRC32;
import java.util.zip.Checksum;

import com.google.protobuf.ByteString;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.tcp.client.files.PacketServiceAnswerImpl;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.FlashData;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.Operations.BlockingInterface;

public class writeFlashOperation extends AbstractOperation<Void> {

	
	private int address = 0;
	private byte[] data = null;
	private int length = 0;
	private long timeout = 0L;

	public writeFlashOperation(final RpcClientChannel channel, final AsyncCallback<Void> callback, final BlockingInterface operationService, final PacketServiceAnswerImpl packetServiceAnswerImpl, final int address, final byte[] data,
			final int length, final long timeout) {

		super(channel,packetServiceAnswerImpl, operationService, callback);
		this.address = address;
		this.data = data;
		this.length = length;
		this.timeout = timeout;
	}

	public void operate() throws ServiceException {
		
		final Checksum checksum = new CRC32();
		checksum.update(data,0,data.length);
		
		final FlashData request = FlashData.newBuilder().setAddress(address).addData(ByteString.copyFrom(data)).setLength(length).setTimeout(timeout).setCrc(checksum.getValue()).setOperationKey(this.getController().toString()).build();
		
		setOperationKey(request.getOperationKey());
		
		this.getPacketServiceAnswerImpl().addCallback(request.getOperationKey(), this.getCallback());
		
//		operationService.writeFlash(controller, flash, new RpcCallback<EmptyAnswer>() {
//			
//			@Override
//			public void run(EmptyAnswer parameter) {
//				if(controller.failed()){
//					callback.onFailure(new Throwable(controller.errorText()));
//				}
//			}
//		});
		
		this.getOperationService().writeFlash(this.getController(), request);
	}
}
