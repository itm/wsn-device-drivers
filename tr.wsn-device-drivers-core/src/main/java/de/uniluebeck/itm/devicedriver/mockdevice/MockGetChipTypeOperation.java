package de.uniluebeck.itm.devicedriver.mockdevice;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.operation.GetChipTypeOperation;


/**
 * Returns the <code>ChipType</code> setted in the given <code>MockConfiguration</code>.
 * 
 * @author Malte Legenhausen
 */
public class MockGetChipTypeOperation extends AbstractMockOperation<ChipType> implements GetChipTypeOperation {
	
	/**
	 * The <code>MockConfiguration</code> from which the <code>ChipType</code> has to be received.
	 */
	private final MockConfiguration configuration;
	
	/**
	 * Constructor.
	 * 
	 * @param configuration The <code>MockConfiguration</code> from which the <code>ChipType</code> has to be gotten.
	 */
	public MockGetChipTypeOperation(final MockConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	public ChipType returnResult() {
		return configuration.getChipType();
	}
}
