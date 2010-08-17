package de.uniluebeck.itm.devicedriver;

public interface ConnectOperation extends Operation<Void> {

	void setSerialPortName(String serialPortName);
}
