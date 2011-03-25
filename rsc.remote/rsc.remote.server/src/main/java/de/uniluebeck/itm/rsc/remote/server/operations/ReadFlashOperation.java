package de.uniluebeck.itm.rsc.remote.server.operations;

import org.apache.shiro.subject.Subject;

import com.google.protobuf.ByteString;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;

import de.uniluebeck.itm.rsc.drivers.core.async.OperationHandle;
import de.uniluebeck.itm.rsc.remote.files.MessageServiceFiles.ByteData;
import de.uniluebeck.itm.rsc.remote.files.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.rsc.remote.files.MessageServiceFiles.FlashData;
import de.uniluebeck.itm.rsc.remote.files.MessageServiceFiles.ReverseAnswer;
import de.uniluebeck.itm.rsc.remote.server.utils.ClientID;

/**
 * The readFlash Operation
 * @author Andreas Maier
 *
 */
public class ReadFlashOperation extends AbstractReadOperation<byte[]> {

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
		super(controller,done, user, id, request.getOperationKey());
		this.request = request;
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
	protected OperationHandle<byte[]> operate() {
		return getDeviceAsync().readFlash(request.getAddress(), request.getLength(), request.getTimeout(), getAsyncAdapter());
	}
	
}
