package de.uniluebeck.itm.wsn.drivers.sunspot;

import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.operation.GetChipTypeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;

public class SunspotgetChipTypeOperationRunnable implements GetChipTypeOperation {


    public SunspotgetChipTypeOperationRunnable() {
    }

    @Override
    public ChipType run(ProgressManager progressManager, OperationContext context) throws Exception {
        return ChipType.SUNSPOT;
    }
}
