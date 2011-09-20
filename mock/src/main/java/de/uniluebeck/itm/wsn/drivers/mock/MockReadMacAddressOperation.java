package de.uniluebeck.itm.wsn.drivers.mock;

import com.google.inject.Inject;

import de.uniluebeck.itm.wsn.drivers.core.MacAddress;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadMacAddressOperation;


/**
 * OperationRunnable for getting the <code>MacAddress</code> of the given <code>MockConfiguration</code>.
 * 
 * @author Malte Legenhausen
 */
public class MockReadMacAddressOperation extends AbstractMockOperation<MacAddress> implements ReadMacAddressOperation {
	
	/**
	 * The <code>MockConfiguration</code> of the <code>MockDevice</code>.
	 */
	private final MockConfiguration configuration;
	
	/**
	 * Constructor.
	 * 
	 * @param configuration The <code>MockConfiguration</code> of the <code>MockDevice</code>.
	 */
	@Inject
	public MockReadMacAddressOperation(MockConfiguration configuration) {
		this.configuration = configuration;
	}
	
	@Override
	public MacAddress returnResult() {
		return configuration.getMacAddress();
	}
}
