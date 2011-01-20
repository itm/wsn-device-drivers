package de.uniluebeck.itm.tcp.operations;

import java.util.zip.CRC32;
import java.util.zip.Checksum;

import com.google.protobuf.ByteString;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.tcp.files.PacketServiceAnswerImpl;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.ProgramPacket;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.Operations.BlockingInterface;

public class programOperation extends AbstractOperation<Void> {
	
	byte[] bytes;
	long timeout;

	public programOperation(RpcClientChannel channel, AsyncCallback<Void> callback, BlockingInterface operationService, PacketServiceAnswerImpl packetServiceAnswerImpl, byte[] bytes, long timeout) {
		super(channel,packetServiceAnswerImpl, operationService, callback);
		this.bytes = bytes;
		this.timeout = timeout;
	}

	public void operate() throws ServiceException {
		
		
		Checksum checksum = new CRC32();
		checksum.update(bytes,0,bytes.length);
		
		ProgramPacket packet = ProgramPacket.newBuilder().addBinaryPacket(ByteString.copyFrom(bytes)).setCrc(checksum.getValue()).setTimeout(timeout).setOperationKey(controller.toString()).build();
		
		setOperationKey(packet.getOperationKey());

		packetServiceAnswerImpl.addCallback(packet.getOperationKey(), callback);
		
		
		
//		operationService.program(controller, packet, new RpcCallback<EmptyAnswer>() {
//			@Override
//			public void run(EmptyAnswer parameter) {
//				if(controller.failed()){
//					callback.onFailure(new Throwable(controller.errorText()));
//				}
//			}
//		});
		
		operationService.program(controller, packet);
	}
}
