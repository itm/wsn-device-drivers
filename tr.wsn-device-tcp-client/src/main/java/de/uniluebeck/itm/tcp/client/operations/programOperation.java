package de.uniluebeck.itm.tcp.client.operations;

import java.util.zip.CRC32;
import java.util.zip.Checksum;

import com.google.protobuf.ByteString;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.tcp.client.files.PacketServiceAnswerImpl;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.ProgramPacket;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.Operations.BlockingInterface;

public class programOperation extends AbstractOperation<Void> {
	
	private byte[] bytes;
	private long timeout;

	public programOperation(final RpcClientChannel channel, final AsyncCallback<Void> callback, final BlockingInterface operationService, final PacketServiceAnswerImpl packetServiceAnswerImpl, final byte[] bytes, final long timeout) {
		super(channel,packetServiceAnswerImpl, operationService, callback);
		this.bytes = bytes;
		this.timeout = timeout;
	}

	public void operate() throws ServiceException {
		
		
		final Checksum checksum = new CRC32();
		checksum.update(bytes,0,bytes.length);
		
		final ProgramPacket packet = ProgramPacket.newBuilder().addBinaryPacket(ByteString.copyFrom(bytes)).setCrc(checksum.getValue()).setTimeout(timeout).setOperationKey(this.getController().toString()).build();
		
		setOperationKey(packet.getOperationKey());

		this.getPacketServiceAnswerImpl().addCallback(packet.getOperationKey(), this.getCallback());
		
		
		
//		operationService.program(controller, packet, new RpcCallback<EmptyAnswer>() {
//			@Override
//			public void run(EmptyAnswer parameter) {
//				if(controller.failed()){
//					callback.onFailure(new Throwable(controller.errorText()));
//				}
//			}
//		});
		
		this.getOperationService().program(this.getController(), packet);
	}
}
