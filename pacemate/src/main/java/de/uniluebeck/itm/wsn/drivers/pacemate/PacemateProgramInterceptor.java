package de.uniluebeck.itm.wsn.drivers.pacemate;

import com.google.inject.Provider;

import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortProgramInterceptor;

public class PacemateProgramInterceptor extends SerialPortProgramInterceptor {
	
	private final Provider<PacemateHelper> helperProvider;

	public PacemateProgramInterceptor(SerialPortConnection connection, Provider<PacemateHelper> helperProvider) {
		super(connection);
		this.helperProvider = helperProvider;
	}

	@Override
	public void enterProgramMode(SerialPortConnection connection) throws Exception {
		super.enterProgramMode(connection);
		PacemateHelper helper = helperProvider.get();
		helper.clearStreamData();
		helper.autobaud();
		helper.waitForBootLoader();
	}
}
