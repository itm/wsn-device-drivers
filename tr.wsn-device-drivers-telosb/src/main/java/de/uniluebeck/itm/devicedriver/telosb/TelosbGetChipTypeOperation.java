package de.uniluebeck.itm.devicedriver.telosb;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractOperation;
import de.uniluebeck.itm.devicedriver.operation.GetChipTypeOperation;

public class TelosbGetChipTypeOperation extends AbstractOperation<ChipType> implements GetChipTypeOperation {

	@Override
	public ChipType execute(Monitor monitor) throws Exception {
		return ChipType.TelosB;
	}

}
