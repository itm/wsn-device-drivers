package de.uniluebeck.itm.wsn.drivers.pacemate;

import com.google.inject.Inject;

import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractReadFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.serialport.Program;

public class PacemateReadFlashOperation extends AbstractReadFlashOperation {
	
	private final PacemateHelper helper;
	
	@Inject
	public PacemateReadFlashOperation(PacemateHelper helper) {
		this.helper = helper;
	}
	
	@Override
	@Program
	public byte[] run(ProgressManager progressManager, OperationContext context) throws Exception {		
		// Send flash program request
		helper.sendBootLoaderMessage(Messages.flashReadRequestMessage(getAddress(), getLength()));
		progressManager.worked(0.5f);
		return helper.receiveBootLoaderReplyReadData();
	}
}
