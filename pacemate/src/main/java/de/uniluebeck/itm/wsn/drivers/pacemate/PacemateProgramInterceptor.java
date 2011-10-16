package de.uniluebeck.itm.wsn.drivers.pacemate;

import com.google.inject.Provider;

import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection;
import de.uniluebeck.itm.wsn.drivers.isense.iSenseProgramInterceptor;

public class PacemateProgramInterceptor extends iSenseProgramInterceptor {
	
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
