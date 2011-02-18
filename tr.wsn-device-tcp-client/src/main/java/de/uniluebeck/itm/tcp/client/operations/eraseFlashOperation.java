package de.uniluebeck.itm.tcp.client.operations;

import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.tcp.client.files.PacketServiceAnswerImpl;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.Timeout;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.Operations.BlockingInterface;

public class eraseFlashOperation extends AbstractOperation<Void> {

	private long timeout = 0L;	
	
	public eraseFlashOperation(final RpcClientChannel channel, final AsyncCallback<Void> callback, final BlockingInterface operationService,
			final PacketServiceAnswerImpl packetServiceAnswerImpl, final long timeout) {
		super(channel, packetServiceAnswerImpl, operationService, callback);
		this.timeout = timeout;
	}
	
	@Override
	public void operate() throws ServiceException {
		final Timeout request = Timeout.newBuilder().setOperationKey(String.valueOf(this.getController().toString())).setTimeout(timeout).build();

		setOperationKey(request.getOperationKey());
		
		this.getPacketServiceAnswerImpl().addCallback(request.getOperationKey(), this.getCallback());

		this.getOperationService().eraseFlash(this.getController(), request);
		
	}

}
