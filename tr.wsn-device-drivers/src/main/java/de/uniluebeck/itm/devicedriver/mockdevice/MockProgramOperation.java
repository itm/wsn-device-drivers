package de.uniluebeck.itm.devicedriver.mockdevice;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractProgramOperation;

public class MockProgramOperation extends AbstractProgramOperation {

	private byte[] flashRom;
	
	public MockProgramOperation(byte[] flashRom) {
		this.flashRom = flashRom;
	}
	
	@Override
	public Void execute(Monitor monitor) throws Exception {
		for(int i = 1; i <= 10 && !isCanceled(); ++i) {
			Thread.sleep(500 * i);
			monitor.onProgressChange(0.1f * i);
		}
		System.arraycopy(binaryImage, 0, flashRom, 0, binaryImage.length);
		return null;
	}

}
