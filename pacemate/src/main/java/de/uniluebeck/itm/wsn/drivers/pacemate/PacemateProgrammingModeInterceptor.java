package de.uniluebeck.itm.wsn.drivers.pacemate;

import com.google.inject.Inject;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortProgrammingModeInterceptor;

public class PacemateProgrammingModeInterceptor extends SerialPortProgrammingModeInterceptor {

	@Inject
	private SerialPortConnection connection;

	@Inject
	private PacemateHelper helper;

	@Override
	public void enterProgrammingMode() throws Exception {

		super.enterProgrammingMode();

		helper.clearStreamData();
		helper.autobaud();
		helper.waitForBootLoader();
	}
}
