package de.uniluebeck.itm.devicedriver.mockdevice;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractOperation;
import de.uniluebeck.itm.devicedriver.operation.GetChipTypeOperation;

public class MockGetChipTypeOperation extends AbstractOperation<ChipType>
		implements GetChipTypeOperation {

	private final MockConfiguration configuration;
	
	public MockGetChipTypeOperation(MockConfiguration chipType) {
		this.configuration = chipType;
	}
	
	@Override
	public ChipType execute(Monitor monitor) throws Exception {
		for(int i = 1; i <= 10 && !isCanceled(); ++i) {
			Thread.sleep(100);
			monitor.onProgressChange(0.1f * i);
		}
		return configuration.getChipType();
	}

}
