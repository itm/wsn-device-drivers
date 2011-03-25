package de.uniluebeck.itm.rsc.remote.server.operations;

import org.apache.shiro.subject.Subject;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.googlecode.protobuf.pro.duplex.execute.ServerRpcController;

import de.uniluebeck.itm.rsc.drivers.core.async.OperationHandle;
import de.uniluebeck.itm.rsc.remote.files.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.rsc.remote.server.utils.ClientID;
import de.uniluebeck.itm.rsc.remote.server.utils.OperationType;
import de.uniluebeck.itm.rsc.remote.server.utils.ReverseMessage;

/**
 * The basis Class for all operation which do a write process
 * 
 * @author Andreas Maier
 * 
 * @param <T>
 *            The Type of the OperationHandle
 */
public abstract class AbstractWriteOperation<T> extends AbstractOperation<T> {

	/**
	 * the Key for this Operation
	 */
	private String opKey = null;
	/**
	 * Shiro User
	 */
	private Subject user;
	/**
	 * RpcController
	 */
	private RpcController controller = null;
	
	/**
	 * Constructor
	 * 
	 * @param controller
	 *            the RpcController for the Operation
	 * @param done
	 *            the RpcCallback<EmptyAnswer> for the Operation
	 * @param user
	 *            the Shiro-User-Object
	 * @param id
	 *            the ClientID-Instance for the Operation
	 * @param opKey
	 *            the Key for this Operation
	 */
	public AbstractWriteOperation(final RpcController controller,
			final RpcCallback<EmptyAnswer> done, final Subject user,
			final ClientID id, final String opKey) {
		super(done, id);
		this.controller = controller;
		this.opKey = opKey;
		this.user = user;
		setMessage(new ReverseMessage(this.opKey, ServerRpcController
				.getRpcChannel(controller)));
	}

	public RpcController getController() {
		return controller;
	}
	
	@Override
	public void execute() {

		if (!user.isPermitted("write:program")) {
			getController().setFailed(
					"Unauthorized: You are not allowed to write");
			getDone().run(null);
			return;
		}

		OperationHandle<T> handle = operate();

		if (null == handle) {
			return;
		}

		// ein channel-einzigartiger OperationKey wird vom Client zu jeder
		// Operation mitgeschickt
		getId().addHandleElement(opKey, handle);

		// hinzufuegen des OperationType dieser operation zur OperationTypeList
		getId().addOperationType(opKey, OperationType.WRITEOPERATION);

		// ausfuehren des Callbacks
		getDone().run(EmptyAnswer.newBuilder().build());

	}

	/**
	 * 
	 * @return the OperationHandle for the Operation
	 */
	abstract protected OperationHandle<T> operate();

}
