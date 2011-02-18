package de.uniluebeck.itm.tcp.client.operations;

import com.google.protobuf.ByteString;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.tcp.client.files.PacketServiceAnswerImpl;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.MacData;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.Operations.BlockingInterface;

public class writeMacOperation extends AbstractOperation<Void>{
	
	private MacAddress macAddress;
	private long timeout;

	public writeMacOperation(final RpcClientChannel channel, final PacketServiceAnswerImpl packetServiceAnswerImpl, final BlockingInterface operationService, final AsyncCallback<Void> callback, final MacAddress macAddress, final long timeout) {
		super(channel,packetServiceAnswerImpl, operationService, callback);
		this.macAddress = macAddress;
		this.timeout = timeout;
	}

	public void operate() throws ServiceException {
		
		final MacData request = MacData.newBuilder().addMACADDRESS(ByteString.copyFrom(macAddress.getMacBytes())).setTimeout(timeout).setOperationKey(this.getController().toString()).build();
		
		setOperationKey(request.getOperationKey());
		
		this.getPacketServiceAnswerImpl().addCallback(request.getOperationKey(), this.getCallback());
		
//		operationService.writeMac(controller, address, new RpcCallback<EmptyAnswer>() {
//			
//			@Override
//			public void run(EmptyAnswer parameter) {
//				if(controller.failed()){
//					callback.onFailure(new Throwable(controller.errorText()));
//				}
//			}
//		});
		
		this.getOperationService().writeMac(this.getController(), request);
	}
}
