package de.uniluebeck.itm.rsc.remote.client.operations;

import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.rsc.drivers.core.async.AsyncCallback;
import de.uniluebeck.itm.rsc.remote.client.utils.PacketServiceAnswerImpl;
import de.uniluebeck.itm.rsc.remote.files.MessageServiceFiles.Timeout;
import de.uniluebeck.itm.rsc.remote.files.MessageServiceFiles.Operations.BlockingInterface;

/**
 * The erase Operation
 * @author Bjoern Schuett
 *
 */
public class EraseFlashOperation extends AbstractOperation<Void> {

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
	 * @param timeout the timeout for a erase Operation
	 */
	public EraseFlashOperation(final RpcClientChannel channel, final AsyncCallback<Void> callback, final BlockingInterface operationService,
			final PacketServiceAnswerImpl packetServiceAnswerImpl, final long timeout) {
		super(channel, packetServiceAnswerImpl, operationService, callback);
		this.timeout = timeout;
	}
	
	@Override
	public void operate() throws ServiceException {
		final Timeout request = Timeout.newBuilder().setOperationKey(String.valueOf(this.getController().toString())).setTimeout(timeout).build();

		setOperationKey(request.getOperationKey());
		
		this.getPacketServiceAnswerImpl().addCallback(request.getOperationKey(), this.getCallback());

		this.getOperationService().eraseFlash(this.getController(), request);
		
	}

}
