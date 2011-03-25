package de.uniluebeck.itm.rsc.remote.server.operations;

import org.apache.shiro.subject.Subject;

import com.google.protobuf.ByteString;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;

import de.uniluebeck.itm.rsc.drivers.core.MacAddress;
import de.uniluebeck.itm.rsc.drivers.core.async.OperationHandle;
import de.uniluebeck.itm.rsc.remote.files.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.rsc.remote.files.MessageServiceFiles.MacData;
import de.uniluebeck.itm.rsc.remote.files.MessageServiceFiles.ReverseAnswer;
import de.uniluebeck.itm.rsc.remote.files.MessageServiceFiles.Timeout;
import de.uniluebeck.itm.rsc.remote.server.utils.ClientID;

/**
 * The readMac Operation
 * @author Andreas Maier
 *
 */
public class ReadMacOperation extends AbstractReadOperation<MacAddress> {

	/**
	 * the request of type Timeout
	 */
	private Timeout request = null;
	
	/**
	 * Constructor
	 * @param controller the RpcController for a readMac Operation
	 * @param done the RpcCallback<EmptyAnswer> for a readMac Operation
	 * @param user the Shiro-User-Object a readMac Operation
	 * @param id the ClientID-Instance for a readMac Operation
	 * @param request the Timeout request for a readMac Operation
	 */
	public ReadMacOperation(final RpcController controller, final RpcCallback<EmptyAnswer> done, final Subject user, final ClientID id, final Timeout request){
		super(controller,done, user, id, request.getOperationKey());
		this.request = request;
	}
	
	@Override
	/* ueberschreiben der standard OnSuccess-Methode, da hier eine MacAdress als Antwort benoetigt wird */
	public void setOnSuccess(final MacAddress result) {
		if(!getId().getCalledGet(request.getOperationKey())){
			final MacData mac = MacData.newBuilder().setOperationKey(request.getOperationKey()).addMACADDRESS(ByteString.copyFrom(result.getMacBytes())).build();
			getMessage().reverseSuccess(ReverseAnswer.newBuilder().setMacAddress(mac).build());
		}
	}

	@Override
	protected OperationHandle<MacAddress> operate() {
		return getDeviceAsync().readMac(request.getTimeout(), getAsyncAdapter());
	}
	
}
