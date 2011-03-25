package de.uniluebeck.itm.rsc.remote.client.operations;

import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.rsc.drivers.core.async.AsyncCallback;
import de.uniluebeck.itm.rsc.remote.client.utils.PacketServiceAnswerImpl;
import de.uniluebeck.itm.rsc.remote.files.MessageServiceFiles.Timeout;
import de.uniluebeck.itm.rsc.remote.files.MessageServiceFiles.Operations.BlockingInterface;

/**
 * The reset Operation
 * @author Bjoern Schuett
 *
 */
public class ResetOperation extends AbstractOperation<Void> {

	/**
	 * the Timeout for this operation
	 */
	private long timeout = 0L;	
	
	/**
	 * Constructor
	 * @param channel the RpcClientChannel for a reset Operation
	 * @param callback the AsyncCallback for a reset Operation
	 * @param operationService the blocking Interface of Operations for a reset Operation
	 * @param packetServiceAnswerImpl the PacketServiceAnswerImpl for a reset Operation
	 * @param timeout the timeout for a reset Operation
	 */
	public ResetOperation(final RpcClientChannel channel, final AsyncCallback<Void> callback, final BlockingInterface operationService, final PacketServiceAnswerImpl packetServiceAnswerImpl, final long timeout){
		super(channel, packetServiceAnswerImpl, operationService, callback);
		this.timeout = timeout;
	}
	
	@Override
	public void operate() throws ServiceException {
		final Timeout request = Timeout.newBuilder().setOperationKey(String.valueOf(this.getController().toString())).setTimeout(timeout).build();

		setOperationKey(request.getOperationKey());
		
		this.getPacketServiceAnswerImpl().addCallback(request.getOperationKey(), this.getCallback());
		
		// Blockierender Aufruf, Antwort erfolgt Asynchron per CallBack
		this.getOperationService().reset(this.getController(), request);

	}

}
