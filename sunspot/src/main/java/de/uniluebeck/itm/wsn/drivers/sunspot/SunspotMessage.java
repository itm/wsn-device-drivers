package de.uniluebeck.itm.wsn.drivers.sunspot;


public class SunspotMessage {
    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    private String macAddress;
    private byte [] payload;
    private long timestamp;


    public SunspotMessage(String macAddress, byte[] payload, long timestamp) {
        this.macAddress = macAddress;
        this.payload = payload;
        this.timestamp = timestamp;
    }
}

