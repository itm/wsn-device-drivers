package de.uniluebeck.itm.devicedriver.mockdevice;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractOperation;
import de.uniluebeck.itm.devicedriver.operation.GetChipTypeOperation;

public class MockGetChipTypeOperation extends AbstractOperation<ChipType>
		implements GetChipTypeOperation {

	private final ChipType chipType;
	
	public MockGetChipTypeOperation(ChipType chipType) {
		this.chipType = chipType;
	}
	
	@Override
	public ChipType execute(Monitor monitor) throws Exception {
		for(int i = 1; i <= 10 && !isCanceled(); ++i) {
			Thread.sleep(100 * i);
			monitor.onProgressChange(0.1f * i);
		}
		return chipType;
	}

}
