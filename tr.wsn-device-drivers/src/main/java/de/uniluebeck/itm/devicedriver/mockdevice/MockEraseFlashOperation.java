package de.uniluebeck.itm.devicedriver.mockdevice;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractOperation;
import de.uniluebeck.itm.devicedriver.operation.EraseFlashOperation;

public class MockEraseFlashOperation extends AbstractOperation<Void> implements
		EraseFlashOperation {

	private MockConfiguration configuration;
	
	public MockEraseFlashOperation(MockConfiguration flashRom) {
		this.configuration = flashRom;
	}
	
	@Override
	public Void execute(Monitor monitor) throws Exception {
		byte[] flashRom = configuration.getFlashRom();
		for (int i = 0; i < flashRom.length; ++i) {
			flashRom[i] = 0x00;
			float progress = (i * 1.0f) / flashRom.length;
			monitor.onProgressChange(progress);
		}
		configuration.setFlashRom(flashRom);
		return null;
	}

}
