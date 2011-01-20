package de.uniluebeck.itm.tcp.operations;

import com.google.protobuf.ByteString;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.sendData;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.Operations.BlockingInterface;
import de.uniluebeck.itm.tcp.files.PacketServiceAnswerImpl;

public class sendOperation extends AbstractOperation<Void> {

MessagePacket packet;
long timeout = 0L;	
	
	public sendOperation(RpcClientChannel channel, AsyncCallback<Void> callback, BlockingInterface operationService, PacketServiceAnswerImpl packetServiceAnswerImpl, MessagePacket packet, long timeout){
		super(channel, packetServiceAnswerImpl, operationService, callback);
		this.packet = packet;
		this.timeout = timeout;
	}
	
	@Override
	public void operate() throws ServiceException {
		sendData data = sendData.newBuilder().addData(ByteString.copyFrom(packet.getContent())).setTimeout(timeout).setType(packet.getType()).setOperationKey(String.valueOf(controller.toString())).build();
		
		setOperationKey(data.getOperationKey());
		
		packetServiceAnswerImpl.addCallback(data.getOperationKey(), callback);
		
		operationService.send(controller, data);
	}

}
