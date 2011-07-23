package de.uniluebeck.itm.wsn.drivers.mock;

import com.google.inject.Inject;

import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.operation.GetChipTypeOperation;


/**
 * Returns the <code>ChipType</code> setted in the given <code>MockConfiguration</code>.
 * 
 * @author Malte Legenhausen
 */
public class MockGetChipTypeOperation extends AbstractMockOperationRunnable<ChipType> implements GetChipTypeOperation {
	
	/**
	 * The <code>MockConfiguration</code> from which the <code>ChipType</code> has to be received.
	 */
	private final MockConfiguration configuration;
	
	/**
	 * Constructor.
	 * 
	 * @param configuration The <code>MockConfiguration</code> from which the <code>ChipType</code> has to be gotten.
	 */
	@Inject
	public MockGetChipTypeOperation(MockConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	public ChipType returnResult() {
		return configuration.getChipType();
	}
}
