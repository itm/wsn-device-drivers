package de.uniluebeck.itm.tcp.operations;

import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.tcp.files.PacketServiceAnswerImpl;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.Timeout;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.Operations.BlockingInterface;


public class getChipTypeOperation extends AbstractOperation<ChipType> {

	long timeout = 0L;
	
	public getChipTypeOperation(RpcClientChannel channel, AsyncCallback<ChipType> callback, BlockingInterface operationService, PacketServiceAnswerImpl packetServiceAnswerImpl, long timeout){
		super(channel,packetServiceAnswerImpl, operationService, callback);
		this.timeout = timeout;
	}
	
	public void operate() throws ServiceException {
		
		Timeout request = Timeout.newBuilder().setOperationKey(String.valueOf(controller.toString())).setTimeout(timeout).build();

		setOperationKey(request.getOperationKey());
		
		packetServiceAnswerImpl.addCallback(request.getOperationKey(), callback);
		
		
//		operationService.getChipType(controller, request, new RpcCallback<EmptyAnswer>() {
//			@Override
//			public void run(EmptyAnswer parameter) {
//				if(controller.failed()){
//					callback.onFailure(new Throwable(controller.errorText()));
//				}
//			}
//		});
		
		// Blockierender Aufruf, Antwort erfolgt Asynchron per CallBack
		operationService.getChipType(controller, request);
	}
}
