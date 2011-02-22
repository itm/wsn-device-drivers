package de.uniluebeck.itm.tcp.client.operations;

import com.google.protobuf.ByteString;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.tcp.client.files.PacketServiceAnswerImpl;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.sendData;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.Operations.BlockingInterface;

/**
 * The send Operation
 * @author Bjoern Schuett
 *
 */
public class SendOperation extends AbstractOperation<Void> {

	/**
	 * MessagePacket which should be send to the device
	 */
	private MessagePacket packet;
	/**
	 * the Timeout for this operation
	 */
	private long timeout = 0L;	
	
	/**
	 * 
	 * Constructor
	 * @param channel the RpcClientChannel for a send Operation
	 * @param callback the AsyncCallback for a send Operation
	 * @param operationService the blocking Interface of Operations for a send Operation
	 * @param packetServiceAnswerImpl the PacketServiceAnswerImpl for a send Operation
	 * @param packet the MessagePacket which should be send to the device
	 * @param timeout the timeout for a send Operation
	 */
	public SendOperation(final RpcClientChannel channel, final AsyncCallback<Void> callback, final BlockingInterface operationService, final PacketServiceAnswerImpl packetServiceAnswerImpl, final MessagePacket packet, final long timeout){
		super(channel, packetServiceAnswerImpl, operationService, callback);
		this.packet = packet;
		this.timeout = timeout;
	}
	
	@Override
	public void operate() throws ServiceException {
		final sendData data = sendData.newBuilder().addData(ByteString.copyFrom(packet.getContent())).setTimeout(timeout).setType(packet.getType()).setOperationKey(String.valueOf(this.getController().toString())).build();
		
		setOperationKey(data.getOperationKey());
		
		this.getPacketServiceAnswerImpl().addCallback(data.getOperationKey(), this.getCallback());
		
		this.getOperationService().send(this.getController(), data);
	}

}
