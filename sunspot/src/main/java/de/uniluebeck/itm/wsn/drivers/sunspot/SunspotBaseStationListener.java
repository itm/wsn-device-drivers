package de.uniluebeck.itm.wsn.drivers.sunspot;


public interface SunspotBaseStationListener {

    void messageReceived(byte[] messsageBytes);

    String getMacAddress();

}
