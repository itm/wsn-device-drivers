package de.uniluebeck.itm.tcp.operations;

import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.Timeout;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.Operations.BlockingInterface;
import de.uniluebeck.itm.tcp.files.PacketServiceAnswerImpl;

public class resetOperation extends AbstractOperation<Void> {

	long timeout = 0L;	
	
	public resetOperation(RpcClientChannel channel, AsyncCallback<Void> callback, BlockingInterface operationService, PacketServiceAnswerImpl packetServiceAnswerImpl, long timeout){
		super(channel, packetServiceAnswerImpl, operationService, callback);
		this.timeout = timeout;
	}
	
	@Override
	public void operate() throws ServiceException {
		Timeout request = Timeout.newBuilder().setOperationKey(String.valueOf(controller.toString())).setTimeout(timeout).build();

		setOperationKey(request.getOperationKey());
		
		packetServiceAnswerImpl.addCallback(request.getOperationKey(), callback);
		
		// Blockierender Aufruf, Antwort erfolgt Asynchron per CallBack
		operationService.reset(controller, request);

	}

}
