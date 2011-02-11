package de.uniluebeck.itm.devicedriver.mockdevice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractWriteMacAddressOperation;

public class MockWriteMacAddressOperation extends AbstractWriteMacAddressOperation {

	private static final int STEPS = 10;
	
	private static final int SLEEP = 100;
	
	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(MockWriteMacAddressOperation.class);
	
	private final MockConfiguration configuration;
	
	public MockWriteMacAddressOperation(final MockConfiguration configuration) {
		this.configuration = configuration;
	}
	
	@Override
	public Void execute(final Monitor monitor) throws Exception {
		for(int i = 1; i <= STEPS && !isCanceled(); ++i) {
			Thread.sleep(SLEEP);
			final float progress = 0.1f * i;
			monitor.onProgressChange(progress);
		}
		LOG.debug("Writing mac address: " + getMacAddress().getMacString());
		configuration.setMacAddress(getMacAddress());
		return null;
	}

}
