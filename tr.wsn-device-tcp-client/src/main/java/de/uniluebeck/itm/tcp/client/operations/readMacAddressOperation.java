package de.uniluebeck.itm.tcp.client.operations;

import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.tcp.client.files.PacketServiceAnswerImpl;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.Timeout;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.Operations.BlockingInterface;


public class readMacAddressOperation extends AbstractOperation<MacAddress> {

	private long timeout = 0L;
	
	public readMacAddressOperation(final RpcClientChannel channel, final AsyncCallback<MacAddress> callback, final BlockingInterface operationService, final PacketServiceAnswerImpl packetServiceAnswerImpl, final long timeout){
		super(channel,packetServiceAnswerImpl, operationService, callback);
		this.timeout = timeout;
	}
	
	public void operate() throws ServiceException{
		
		final Timeout request = Timeout.newBuilder().setOperationKey(String.valueOf(this.getController().toString())).setTimeout(timeout).build();

		setOperationKey(request.getOperationKey());
		
		this.getPacketServiceAnswerImpl().addCallback(request.getOperationKey(), this.getCallback());
		
//		operationService.readMac(controller, request, new RpcCallback<EmptyAnswer>() {
//			@Override
//			public void run(EmptyAnswer parameter) {
//				if(controller.failed()){
//					callback.onFailure(new Throwable(controller.errorText()));
//				}
//			}
//		});

		this.getOperationService().readMac(this.getController(), request);
	}
}
