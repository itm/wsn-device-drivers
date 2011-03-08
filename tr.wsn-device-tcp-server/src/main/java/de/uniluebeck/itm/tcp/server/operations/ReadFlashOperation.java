package de.uniluebeck.itm.tcp.server.operations;

import org.apache.shiro.subject.Subject;

import com.google.protobuf.ByteString;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.googlecode.protobuf.pro.duplex.execute.ServerRpcController;

import de.uniluebeck.itm.devicedriver.async.OperationHandle;
import de.uniluebeck.itm.tcp.server.utils.ClientID;
import de.uniluebeck.itm.tcp.server.utils.OperationType;
import de.uniluebeck.itm.tcp.server.utils.ReverseMessage;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.ByteData;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.FlashData;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.ReverseAnswer;

/**
 * The readFlash Operation
 * @author Andreas Maier
 *
 */
public class ReadFlashOperation extends AbstractOperation<byte[]> {

	/**
	 * the request of type FlashData
	 */
	private FlashData request = null;
	
	/**
	 * Constructor
	 * @param controller the RpcController for a readFlash Operation
	 * @param done the RpcCallback<EmptyAnswer> for a readFlash Operation
	 * @param user the Shiro-User-Object a readFlash Operation
	 * @param id the ClientID-Instance for a readFlash Operation
	 * @param request the FlashData request for a readFlash Operation
	 */
	public ReadFlashOperation(final RpcController controller, final RpcCallback<EmptyAnswer> done, final Subject user, final ClientID id, final FlashData request){
		super(controller,done, user, id);
		this.request = request;
		setMessage(new ReverseMessage(request.getOperationKey(),ServerRpcController.getRpcChannel(controller)));
		setOperationType(OperationType.READOPERATION);
	}
	
	@Override
	/* ueberschreiben der standard OnSuccess-Methode, da hier ein byte[] als Antwort benoetigt wird */
	public void setOnSuccess(final byte[] result) {
		if(!getId().getCalledGet(request.getOperationKey())){
			final ByteData data = ByteData.newBuilder().setOperationKey(request.getOperationKey()).addData(ByteString.copyFrom(result)).build();
			getMessage().reverseSuccess(ReverseAnswer.newBuilder().setData(data).build());
		}
	}
	
	@Override
	protected void operate(){

		// erzeugen eines OperationHandle zur der Operation
		final OperationHandle <byte[]> handle = getDeviceAsync().readFlash(request.getAddress(), request.getLength(), request.getTimeout(), getAsyncAdapter());
		
		// ein channel-einzigartiger OperationKey wird vom Client zu jeder Operation mitgeschickt
		getId().addHandleElement(request.getOperationKey(), handle);
		
		// hinzufuegen des OperationType dieser operation zur OperationTypeList
		getId().addOperationType(request.getOperationKey(), getOperationType());
		
		getDone().run(EmptyAnswer.newBuilder().build());
	}
	
}
