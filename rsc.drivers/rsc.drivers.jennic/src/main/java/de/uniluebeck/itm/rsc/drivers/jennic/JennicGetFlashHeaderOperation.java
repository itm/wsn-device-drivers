package de.uniluebeck.itm.rsc.drivers.jennic;

import de.uniluebeck.itm.rsc.drivers.core.ChipType;
import de.uniluebeck.itm.rsc.drivers.core.Monitor;
import de.uniluebeck.itm.rsc.drivers.core.operation.AbstractOperation;
import de.uniluebeck.itm.rsc.drivers.core.operation.ReadFlashOperation;

public class JennicGetFlashHeaderOperation extends AbstractOperation<byte[]> implements GetFlashHeaderOperation {

	private JennicDevice device;
	
	public JennicGetFlashHeaderOperation(JennicDevice device) {
		this.device = device;
	}
	
	@Override
	public byte[] execute(Monitor monitor) throws Exception {
		ChipType chipType = executeSubOperation(device.createGetChipTypeOperation(), monitor);
		monitor.onProgressChange(0.5f);
		
		final int address = chipType.getHeaderStart();
		final int length = chipType.getHeaderLength();
		
		final ReadFlashOperation readFlashOperation = device.createReadFlashOperation();
		readFlashOperation.setAddress(address, length);
		final byte[] result = executeSubOperation(readFlashOperation, monitor);
		monitor.onProgressChange(1.0f);
		return result;
	}

}
