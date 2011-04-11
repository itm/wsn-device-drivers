package de.uniluebeck.itm.rsc.drivers.telosb;

import de.uniluebeck.itm.rsc.drivers.core.ChipType;
import de.uniluebeck.itm.rsc.drivers.core.operation.AbstractOperation;
import de.uniluebeck.itm.rsc.drivers.core.operation.AbstractProgressManager;
import de.uniluebeck.itm.rsc.drivers.core.operation.GetChipTypeOperation;

public class TelosbGetChipTypeOperation extends AbstractOperation<ChipType> implements GetChipTypeOperation {

	@Override
	public ChipType execute(AbstractProgressManager progressManager) throws Exception {
		return ChipType.TelosB;
	}

}
