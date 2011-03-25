package de.uniluebeck.itm.rsc.remote.server.operations;

import org.apache.shiro.subject.Subject;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;

import de.uniluebeck.itm.rsc.drivers.core.MacAddress;
import de.uniluebeck.itm.rsc.drivers.core.async.OperationHandle;
import de.uniluebeck.itm.rsc.remote.files.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.rsc.remote.files.MessageServiceFiles.MacData;
import de.uniluebeck.itm.rsc.remote.server.utils.ClientID;

/**
 * The writeMac Operation
 * @author Andreas Maier
 *
 */
public class WriteMacOperation extends AbstractWriteOperation<Void> {

	/**
	 * the request of type MacData
	 */
	private MacData request = null;
	
	/**
	 * Constructor
	 * @param controller the RpcController for a writeMac Operation
	 * @param done the RpcCallback<EmptyAnswer> for a writeMac Operation
	 * @param user the Shiro-User-Object a writeMac Operation
	 * @param id the ClientID-Instance for a writeMac Operation
	 * @param request the MacData request for a writeMac Operation
	 */
	public WriteMacOperation(final RpcController controller, final RpcCallback<EmptyAnswer> done, final Subject user, final ClientID id, final MacData request) {
		super(controller, done, user, id, request.getOperationKey());
		this.request =  request;
	}

	@Override
	protected OperationHandle<Void> operate() {
		return getDeviceAsync().writeMac(new MacAddress(request.getMACADDRESSList().get(0).toByteArray()), request.getTimeout(), getAsyncAdapter());
	}
	
	

}
