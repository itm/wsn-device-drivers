package de.uniluebeck.itm.tcp.client.operations;

import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.tcp.client.files.PacketServiceAnswerImpl;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.Timeout;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.Operations.BlockingInterface;

/**
 * The getChipType Operation
 * @author Bjoern Schuett
 *
 */
public class GetChipTypeOperation extends AbstractOperation<ChipType> {

	/**
	 * the Timeout for this operation
	 */
	private long timeout = 0L;
	
	/**
	 * Constructor
	 * @param channel the RpcClientChannel for a getChipType Operation
	 * @param callback the AsyncCallback for a getChipType Operation
	 * @param operationService the blocking Interface of Operations for a getChipType Operation
	 * @param packetServiceAnswerImpl the PacketServiceAnswerImpl for a getChipType Operation
	 * @param timeout the timeout for a getChipType Operation
	 */
	public GetChipTypeOperation(final RpcClientChannel channel, final AsyncCallback<ChipType> callback, final BlockingInterface operationService, final PacketServiceAnswerImpl packetServiceAnswerImpl, final long timeout){
		super(channel,packetServiceAnswerImpl, operationService, callback);
		this.timeout = timeout;
	}
	
	@Override
	public void operate() throws ServiceException {
		
		final Timeout request = Timeout.newBuilder().setOperationKey(String.valueOf(this.getController().toString())).setTimeout(timeout).build();

		setOperationKey(request.getOperationKey());
		
		this.getPacketServiceAnswerImpl().addCallback(request.getOperationKey(), this.getCallback());
		
		// Blockierender Aufruf, Antwort erfolgt Asynchron per CallBack
		this.getOperationService().getChipType(this.getController(), request);
	}
}
