package de.uniluebeck.itm.devicedriver.mockdevice;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractOperation;
import de.uniluebeck.itm.devicedriver.operation.GetChipTypeOperation;

public class MockGetChipTypeOperation extends AbstractOperation<ChipType> implements GetChipTypeOperation {

	private static final int STEPS = 10;
	
	private static final int SLEEP = 100;
	
	private final MockConfiguration configuration;
	
	public MockGetChipTypeOperation(final MockConfiguration chipType) {
		this.configuration = chipType;
	}
	
	@Override
	public ChipType execute(final Monitor monitor) throws Exception {
		for(int i = 1; i <= STEPS && !isCanceled(); ++i) {
			Thread.sleep(SLEEP);
			final float progress = 0.1f * i;
			monitor.onProgressChange(progress);
		}
		return configuration.getChipType();
	}

}
