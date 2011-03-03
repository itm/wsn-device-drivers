package de.uniluebeck.itm.tcp.server.operations;

import org.apache.shiro.subject.Subject;

import com.google.protobuf.ByteString;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.googlecode.protobuf.pro.duplex.execute.ServerRpcController;

import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;
import de.uniluebeck.itm.tcp.server.utils.ClientID;
import de.uniluebeck.itm.tcp.server.utils.ReverseMessage;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.MacData;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.ReverseAnswer;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.Timeout;

/**
 * The readMac Operation
 * @author Andreas Maier
 *
 */
public class ReadMacOperation extends AbstractOperation<MacAddress> {

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
		super(controller,done, user, id);
		this.request = request;
		setMessage(new ReverseMessage(request.getOperationKey(),ServerRpcController.getRpcChannel(controller)));
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
	protected void operate(){

		// erzeugen eines OperationHandle zur der Operation
		final OperationHandle <MacAddress> handle = getDeviceAsync().readMac(request.getTimeout(), getAsyncAdapter());
		
		// ein channel-einzigartiger OperationKey wird vom Client zu jeder Operation mitgeschickt
		getId().setHandleElement(request.getOperationKey(), handle);
		
		getDone().run(EmptyAnswer.newBuilder().build());
	}
	
}
