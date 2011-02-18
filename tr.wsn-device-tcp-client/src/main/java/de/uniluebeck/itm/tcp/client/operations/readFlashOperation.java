package de.uniluebeck.itm.tcp.client.operations;

import com.google.protobuf.ByteString;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.tcp.client.files.PacketServiceAnswerImpl;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.FlashData;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.Operations.BlockingInterface;

public class readFlashOperation extends AbstractOperation<byte[]> {

	private int address = 0;
	private int length = 0;
	private long timeout = 0L;
	
	public readFlashOperation(final RpcClientChannel channel, final AsyncCallback<byte[]> callback, final BlockingInterface operationService, final PacketServiceAnswerImpl packetServiceAnswerImpl, final int address,
			final int length, final long timeout) {
		super(channel,packetServiceAnswerImpl, operationService, callback);
		this.address = address;
		this.length = length;
		this.timeout = timeout;
	}
	
	@Override
	public void operate() throws ServiceException {
		final FlashData request = FlashData.newBuilder().setAddress(address).setLength(length).setTimeout(timeout).setOperationKey(this.getController().toString()).build();
		
		setOperationKey(request.getOperationKey());
		
		this.getPacketServiceAnswerImpl().addCallback(request.getOperationKey(), this.getCallback());
		
		this.getOperationService().readFlash(this.getController(), request);

	}

}
