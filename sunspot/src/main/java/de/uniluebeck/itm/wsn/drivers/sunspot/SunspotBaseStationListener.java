package de.uniluebeck.itm.wsn.drivers.sunspot;


public interface SunspotBaseStationListener {

    void messageReceived(byte[] messageBytes);

    String getMacAddress();

}
