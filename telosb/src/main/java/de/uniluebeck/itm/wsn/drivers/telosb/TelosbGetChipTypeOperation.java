package de.uniluebeck.itm.wsn.drivers.telosb;

import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.GetChipTypeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;

public class TelosbGetChipTypeOperation extends AbstractOperation<ChipType> implements GetChipTypeOperation {
	
	@Override
	public ChipType execute(ProgressManager progressManager) throws Exception {
		return ChipType.TelosB;
	}

}
