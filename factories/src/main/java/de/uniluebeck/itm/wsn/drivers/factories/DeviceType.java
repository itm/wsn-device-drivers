package de.uniluebeck.itm.wsn.drivers.factories;


public enum DeviceType {

	ISENSE,
	TELOSB,
	PACEMATE,
	MOCK;
	
	public static DeviceType fromString(String deviceTypeString) {
		for (DeviceType deviceType : DeviceType.values()) {
			if (deviceType.name().equalsIgnoreCase(deviceTypeString))
				return deviceType;
		}
		throw new RuntimeException("Unknown device type \"" + deviceTypeString + "\"");
	}
	
}
