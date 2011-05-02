package de.uniluebeck.itm.rsc.drivers.telosb;

import de.uniluebeck.itm.rsc.drivers.core.ChipType;
import de.uniluebeck.itm.rsc.drivers.core.operation.AbstractOperation;
import de.uniluebeck.itm.rsc.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.rsc.drivers.core.operation.GetChipTypeOperation;

public class TelosbGetChipTypeOperation extends AbstractOperation<ChipType> implements GetChipTypeOperation {

	@Override
	public ChipType execute(ProgressManager progressManager) throws Exception {
		return ChipType.TelosB;
	}

}
