package de.uniluebeck.itm.tcp.client.operations;

import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.tcp.client.files.PacketServiceAnswerImpl;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.Timeout;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.Operations.BlockingInterface;

/**
 * The readMac Operation
 * @author Bjoern Schuett
 *
 */
public class ReadMacAddressOperation extends AbstractOperation<MacAddress> {

	/**
	 * the Timeout for this operation
	 */
	private long timeout = 0L;
	
	/**
	 * Constructor
	 * @param channel the RpcClientChannel for a readMac Operation
	 * @param callback the AsyncCallback for a readMac Operation
	 * @param operationService the blocking Interface of Operations for a readMac Operation
	 * @param packetServiceAnswerImpl the PacketServiceAnswerImpl for a readMac Operation
	 * @param timeout the timeout for a readMac Operation
	 */
	public ReadMacAddressOperation(final RpcClientChannel channel, final AsyncCallback<MacAddress> callback, final BlockingInterface operationService, final PacketServiceAnswerImpl packetServiceAnswerImpl, final long timeout){
		super(channel,packetServiceAnswerImpl, operationService, callback);
		this.timeout = timeout;
	}
	
	@Override
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
