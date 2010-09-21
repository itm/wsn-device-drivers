package de.uniluebeck.itm.devicedriver.jennic;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractOperation;
import de.uniluebeck.itm.devicedriver.operation.GetChipTypeOperation;
import de.uniluebeck.itm.devicedriver.operation.ReadFlashOperation;

public class JennicGetFlashHeaderOperation extends AbstractOperation<byte[]> implements GetFlashHeaderOperation {

	private JennicDevice device;
	
	public JennicGetFlashHeaderOperation(JennicDevice device) {
		this.device = device;
	}
	
	@Override
	public byte[] execute(Monitor monitor) throws Exception {
		final GetChipTypeOperation getChipTypeOperation = device.createGetChipTypeOperation(); 
		ChipType chipType = executeSubOperation(getChipTypeOperation);
		monitor.onProgressChange(0.5f);
		
		final int address = chipType.getHeaderStart();
		final int length = chipType.getHeaderLength();
		
		final ReadFlashOperation readFlashOperation = device.createReadFlashOperation();
		readFlashOperation.setAddress(address, length);
		final byte[] result = executeSubOperation(readFlashOperation);
		monitor.onProgressChange(1.0f);
		return result;
	}

}
