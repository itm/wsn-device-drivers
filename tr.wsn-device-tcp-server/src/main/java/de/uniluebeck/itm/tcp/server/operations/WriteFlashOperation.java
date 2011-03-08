package de.uniluebeck.itm.tcp.server.operations;

import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.apache.shiro.subject.Subject;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.googlecode.protobuf.pro.duplex.execute.ServerRpcController;

import de.uniluebeck.itm.devicedriver.async.OperationHandle;
import de.uniluebeck.itm.tcp.server.utils.ClientID;
import de.uniluebeck.itm.tcp.server.utils.OperationType;
import de.uniluebeck.itm.tcp.server.utils.ReverseMessage;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.FlashData;

/**
 * The writeFlash Operation
 * @author Andreas Maier
 *
 */
public class WriteFlashOperation extends AbstractOperation<Void> {

	/**
	 * the request of type FlashData
	 */
	private FlashData request = null;
	
	/**
	 * Constructor
	 * @param controller the RpcController for a writeFlash Operation
	 * @param done the RpcCallback<EmptyAnswer> for a writeFlash Operation
	 * @param user the Shiro-User-Object a writeFlash Operation
	 * @param id the ClientID-Instance for a writeFlash Operation
	 * @param request the FlashData request for a writeFlash Operation
	 */
	public WriteFlashOperation(final RpcController controller, final RpcCallback<EmptyAnswer> done, final Subject user, final ClientID id, final FlashData request) {
		super(controller, done, user, id);
		this.request =  request;
		setMessage(new ReverseMessage(request.getOperationKey(),ServerRpcController.getRpcChannel(controller)));
		setOperationType(OperationType.WRITEOPERATION);
	}

	@Override
	protected void operate() {
		
		if (!getUser().isPermitted("write:program")) {
			getController().setFailed("Unauthorized: You are not allowed to write");
			getDone().run(null);
			return;
		}

		/* ueberpruefen der Chucksum, um eine korrekte Uebertragung der Daten sicherzustellen */
		final byte[] data = request.getDataList().get(0).toByteArray();
		
		final Checksum checksum = new CRC32();
		checksum.update(data,0,data.length);
		
		if(request.getCrc() != checksum.getValue()){
			getController().setFailed("CRC-Error");
			getDone().run(null);
		}else{
		
			// erzeugen eines OperationHandle zur der Operation
			final OperationHandle <Void> handle = getDeviceAsync().writeFlash(request.getAddress(), data, request.getLength(), request.getTimeout(), getAsyncAdapter());
			
			// ein channel-einzigartiger OperationKey wird vom Client zu jeder Operation mitgeschickt
			getId().addHandleElement(request.getOperationKey(), handle);
			
			// hinzufuegen des OperationType dieser operation zur OperationTypeList
			getId().addOperationType(request.getOperationKey(), getOperationType());
			
			// ausfuehren des Callbacks
			getDone().run(EmptyAnswer.newBuilder().build());
		}
		
	}
	
	

}
