package de.uniluebeck.itm.tcp.client.operations;

import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.rsc.drivers.core.async.AsyncCallback;
import de.uniluebeck.itm.tcp.client.files.PacketServiceAnswerImpl;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.FlashData;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.Operations.BlockingInterface;

/**
 * The readFlash Operation
 * @author Bjoern Schuett
 *
 */
public class ReadFlashOperation extends AbstractOperation<byte[]> {

	/**
	 * the address where the reading should start
	 */
	private int address = 0;
	/**
	 * the length of the data
	 */
	private int length = 0;
	/**
	 * the Timeout for this operation
	 */
	private long timeout = 0L;
	
	/**
	 * Constructor
	 * @param channel the RpcClientChannel for a readFlash Operation
	 * @param callback the AsyncCallback for a readFlash Operation
	 * @param operationService the blocking Interface of Operations for a readFlash Operation
	 * @param packetServiceAnswerImpl the PacketServiceAnswerImpl for a readFlash Operation
	 * @param address the address where the reading should start
	 * @param length the length of the data
	 * @param timeout the timeout for a readFlash Operation
	 */
	public ReadFlashOperation(final RpcClientChannel channel, final AsyncCallback<byte[]> callback, final BlockingInterface operationService, final PacketServiceAnswerImpl packetServiceAnswerImpl, final int address,
			final int length, final long timeout) {
		super(channel,packetServiceAnswerImpl, operationService, callback);
		this.address = address;
		this.length = length;
		this.timeout = timeout;
	}
	
	@Override
	public void operate() throws ServiceException {
		final FlashData request = FlashData.newBuilder().setAddress(address).setLength(length).setTimeout(timeout).setOperationKey(this.getController().toString()).build();
		
		setOperationKey(request.getOperationKey());
		
		this.getPacketServiceAnswerImpl().addCallback(request.getOperationKey(), this.getCallback());
		
		this.getOperationService().readFlash(this.getController(), request);

	}

}
