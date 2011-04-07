package de.uniluebeck.itm.rsc.remote.client.operations;

import java.util.zip.CRC32;
import java.util.zip.Checksum;

import com.google.protobuf.ByteString;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.rsc.drivers.core.async.AsyncCallback;
import de.uniluebeck.itm.rsc.remote.client.utils.PacketServiceAnswerImpl;
import de.uniluebeck.itm.rsc.remote.files.MessageServiceFiles.FlashData;
import de.uniluebeck.itm.rsc.remote.files.MessageServiceFiles.Operations.BlockingInterface;

/**
 * The writeFlash Operation
 * @author Bjoern Schuett
 *
 */
public class WriteFlashOperation extends AbstractOperation<Void> {

	/**
	 * 
	 */
	private int address = 0;
	/**
	 * 
	 */
	private byte[] data = null;
	/**
	 * 
	 */
	private int length = 0;
	/**
	 * the Timeout for this operation
	 */
	private long timeout = 0L;

	/**
	 * Constructor
	 * @param channel the RpcClientChannel for a erase Operation
	 * @param callback the AsyncCallback for a erase Operation
	 * @param operationService the blocking Interface of Operations for a erase Operation
	 * @param packetServiceAnswerImpl the PacketServiceAnswerImpl for a erase Operation
	 * @param address 
	 * @param data 
	 * @param length 
	 * @param timeout the timeout for a erase Operation
	 */
	public WriteFlashOperation(final RpcClientChannel channel, final AsyncCallback<Void> callback, final BlockingInterface operationService, final PacketServiceAnswerImpl packetServiceAnswerImpl, final int address, final byte[] data,
			final int length, final long timeout) {

		super(channel,packetServiceAnswerImpl, operationService, callback);
		this.address = address;
		this.data = data;
		this.length = length;
		this.timeout = timeout;
	}

	@Override
	public void operate() throws ServiceException {
		
		final Checksum checksum = new CRC32();
		checksum.update(data,0,data.length);
		
		final FlashData request = FlashData.newBuilder().setAddress(address).addData(ByteString.copyFrom(data)).setLength(length).setTimeout(timeout).setCrc(checksum.getValue()).setOperationKey(this.getController().toString()).build();
		
		setOperationKey(request.getOperationKey());
		
		this.getPacketServiceAnswerImpl().addCallback(request.getOperationKey(), this.getCallback());
		
		this.getOperationService().writeFlash(this.getController(), request);
	}
}
