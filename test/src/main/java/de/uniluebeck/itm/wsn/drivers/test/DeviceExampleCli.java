package de.uniluebeck.itm.wsn.drivers.test;

import de.uniluebeck.itm.wsn.drivers.jennic.JennicModule;
import de.uniluebeck.itm.wsn.drivers.mock.MockModule;
import de.uniluebeck.itm.wsn.drivers.pacemate.PacemateModule;
import de.uniluebeck.itm.wsn.drivers.telosb.TelosbModule;
import de.uniluebeck.itm.wsn.drivers.test.isense.MessagePacketReceiver;

public class DeviceExampleCli {

	private static final String JENNIC_IMAGE = "/de/uniluebeck/itm/wsn/drivers/test/jennic.bin";
	
	private static final String PACEMATE_IMAGE = "/de/uniluebeck/itm/wsn/drivers/test/pacemate.bin";
	
	private static final String TELOSB_IMAGE = "/de/uniluebeck/itm/wsn/drivers/test/telosb.ihex";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Please add device type [jennic, pacemate, telosb, mock] and add the port");
			System.exit(1);
		}
		
		String device = args[0];
		String port = args[1];
		
		final GenericDeviceExample example = new GenericDeviceExample();
		example.addByteReceiver(new MessagePacketReceiver());
		example.setUri(port);
		if ("jennic".equals(device)) {
			example.setModule(new JennicModule());
			example.setImageInputStream(DeviceExampleCli.class.getResourceAsStream(JENNIC_IMAGE));
			example.setMessage(new byte[] { 11, 17 });
		} else if ("pacemate".equals(device)) {
			example.setModule(new PacemateModule());
			example.setImageInputStream(DeviceExampleCli.class.getResourceAsStream(PACEMATE_IMAGE));
			example.setUri(port);
			example.setMessage(new byte[] { 11, 17 });
		} else if ("telosb".equals(device)) {
			example.setModule(new TelosbModule());
			example.setUri(port);
			example.setImageInputStream(DeviceExampleCli.class.getResourceAsStream(TELOSB_IMAGE));
			example.setMessage(new byte[] { 11, 17 });
		} else if ("mock".equals(device)) {
			example.setModule(new MockModule());
			example.setMessage("This is a test message".getBytes());
			example.setUri(port);
		}
		example.run();
	}

}
