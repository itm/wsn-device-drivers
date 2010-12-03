package de.uniluebeck.itm.devicedriver.mockdevice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractWriteMacAddressOperation;

public class MockWriteMacAddressOperation extends AbstractWriteMacAddressOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(MockWriteMacAddressOperation.class);
	
	private final MockConfiguration configuration;
	
	public MockWriteMacAddressOperation(MockConfiguration configuration) {
		this.configuration = configuration;
	}
	
	@Override
	public Void execute(Monitor monitor) throws Exception {
		for(int i = 1; i <= 10 && !isCanceled(); ++i) {
			Thread.sleep(100);
			monitor.onProgressChange(0.1f * i);
		}
		log.debug("Writing mac address: " + macAddress.getMacString());
		configuration.setMacAddress(macAddress);
		return null;
	}

}
