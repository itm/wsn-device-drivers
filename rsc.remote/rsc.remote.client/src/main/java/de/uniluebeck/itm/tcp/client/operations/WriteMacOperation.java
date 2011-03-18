package de.uniluebeck.itm.tcp.client.operations;

import com.google.protobuf.ByteString;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.rsc.drivers.core.MacAddress;
import de.uniluebeck.itm.rsc.drivers.core.async.AsyncCallback;
import de.uniluebeck.itm.tcp.client.files.PacketServiceAnswerImpl;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.MacData;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.Operations.BlockingInterface;

/**
 * The writeMac Operation
 * @author Bjoern Schuett
 *
 */
public class WriteMacOperation extends AbstractOperation<Void>{
	
	/**
	 * MAC address to write on node. 
	 */
	private MacAddress macAddress;
	/**
	 * timeout for the operation.
	 */
	private long timeout;

	/**
	 * Constructor
	 * @param channel the RpcClientChannel for a erase Operation
	 * @param callback the AsyncCallback for a erase Operation
	 * @param operationService the blocking Interface of Operations for a erase Operation
	 * @param packetServiceAnswerImpl the PacketServiceAnswerImpl for a erase Operation
	 * @param macAddress the MacAddress which should be written on the device
	 * @param timeout the timeout for a erase Operation
	 */
	public WriteMacOperation(final RpcClientChannel channel, final PacketServiceAnswerImpl packetServiceAnswerImpl, final BlockingInterface operationService, final AsyncCallback<Void> callback, final MacAddress macAddress, final long timeout) {
		super(channel,packetServiceAnswerImpl, operationService, callback);
		this.macAddress = macAddress;
		this.timeout = timeout;
	}
	
	@Override
	public void operate() throws ServiceException {
		
		final MacData request = MacData.newBuilder().addMACADDRESS(ByteString.copyFrom(macAddress.getMacBytes())).setTimeout(timeout).setOperationKey(this.getController().toString()).build();
		
		setOperationKey(request.getOperationKey());
		
		this.getPacketServiceAnswerImpl().addCallback(request.getOperationKey(), this.getCallback());
		
		this.getOperationService().writeMac(this.getController(), request);
	}
}
