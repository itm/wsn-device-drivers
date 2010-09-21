package de.uniluebeck.itm.devicedriver.pacemate;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractReadFlashOperation;

public class PacemateReadFlashOperation extends AbstractReadFlashOperation {

	private PacemateDevice device;
	
	public PacemateReadFlashOperation(PacemateDevice device) {
		this.device = device;
	}
	
	@Override
	public byte[] execute(Monitor monitor) throws Exception {
		// Send flash program request
		device.sendBootLoaderMessage(Messages.flashReadRequestMessage(address, length));

		// Read flash program response
		byte[] response = device.receiveBootLoaderReply(Messages.DATA);

		// Return data
		return response;
	}

}
