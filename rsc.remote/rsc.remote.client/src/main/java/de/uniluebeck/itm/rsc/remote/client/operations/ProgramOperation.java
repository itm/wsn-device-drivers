package de.uniluebeck.itm.rsc.remote.client.operations;

import java.util.zip.CRC32;
import java.util.zip.Checksum;

import com.google.protobuf.ByteString;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.rsc.drivers.core.async.AsyncCallback;
import de.uniluebeck.itm.rsc.remote.client.utils.PacketServiceAnswerImpl;
import de.uniluebeck.itm.rsc.remote.files.MessageServiceFiles.ProgramPacket;
import de.uniluebeck.itm.rsc.remote.files.MessageServiceFiles.Operations.BlockingInterface;

/**
 * The program Operation
 * @author Bjoern Schuett
 *
 */
public class ProgramOperation extends AbstractOperation<Void> {
	
	/**
	 * the bytes from the binaryImage for this program operation
	 */
	private byte[] bytes;
	/**
	 * the Timeout for this operation
	 */
	private long timeout;

	/**
	 * Constructor
	 * @param channel the RpcClientChannel for a program Operation
	 * @param callback the AsyncCallback for a program Operation
	 * @param operationService the blocking Interface of Operations for a program Operation
	 * @param packetServiceAnswerImpl the PacketServiceAnswerImpl for a program Operation
	 * @param bytes the bytes from the binaryImage for this program operation
	 * @param timeout the timeout for a program operation
	 */
	public ProgramOperation(final RpcClientChannel channel, final AsyncCallback<Void> callback, final BlockingInterface operationService, final PacketServiceAnswerImpl packetServiceAnswerImpl, final byte[] bytes, final long timeout) {
		super(channel,packetServiceAnswerImpl, operationService, callback);
		this.bytes = bytes;
		this.timeout = timeout;
	}

	@Override
	public void operate() throws ServiceException {
		
		/* create a CRC to check the Integrity */
		final Checksum checksum = new CRC32();
		checksum.update(bytes,0,bytes.length);
		
		final ProgramPacket packet = ProgramPacket.newBuilder().addBinaryPacket(ByteString.copyFrom(bytes)).setCrc(checksum.getValue()).setTimeout(timeout).setOperationKey(this.getController().toString()).build();
		
		setOperationKey(packet.getOperationKey());

		this.getPacketServiceAnswerImpl().addCallback(packet.getOperationKey(), this.getCallback());
		
		this.getOperationService().program(this.getController(), packet);
	}
}
