package de.uniluebeck.itm.rsc.drivers.core.mockdevice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.rsc.drivers.core.MacAddress;
import de.uniluebeck.itm.rsc.drivers.core.operation.WriteMacAddressOperation;


/**
 * Mock operation for writing a <code>MacAddress</code> in the given <code>MockConfiguration</code>.
 * 
 * @author Malte Legenhausen
 */
public class MockWriteMacAddressOperation extends AbstractMockOperation<Void> implements WriteMacAddressOperation {
	
	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(MockWriteMacAddressOperation.class);
	
	/**
	 * The <code>MockConfiguration</code> to which the <code>MacAddress</code> has to be assigned.
	 */
	private final MockConfiguration configuration;
	
	/**
	 * The <code>MacAddress</code> that has to be written to the configuration.
	 */
	private MacAddress macAddress;
	
	/**
	 * Constructor.
	 * 
	 * @param configuration The configuration of the <code>MockDevice</code>.
	 */
	public MockWriteMacAddressOperation(final MockConfiguration configuration) {
		this.configuration = configuration;
	}
	
	@Override
	protected Void returnResult() {
		LOG.debug("Writing mac address: " + macAddress);
		configuration.setMacAddress(macAddress);
		return null;
	}

	@Override
	public void setMacAddress(final MacAddress macAddress) {
		this.macAddress = macAddress;
	}

}
