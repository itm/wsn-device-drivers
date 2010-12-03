package de.uniluebeck.itm.devicedriver.mockdevice;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractOperation;
import de.uniluebeck.itm.devicedriver.operation.EraseFlashOperation;

public class MockEraseFlashOperation extends AbstractOperation<Void> implements
		EraseFlashOperation {

	private byte[] flashRom;
	
	public MockEraseFlashOperation(byte[] flashRom) {
		this.flashRom = flashRom;
	}
	
	@Override
	public Void execute(Monitor monitor) throws Exception {
		for (int i = 0; i < flashRom.length; ++i) {
			flashRom[i] = 0x00;
			float progress = (i * 1.0f) / flashRom.length;
			monitor.onProgressChange(progress);
		}
		return null;
	}

}
