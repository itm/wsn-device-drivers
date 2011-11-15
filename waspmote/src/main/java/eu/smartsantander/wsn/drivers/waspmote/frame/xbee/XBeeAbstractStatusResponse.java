package eu.smartsantander.wsn.drivers.waspmote.frame.xbee;


public abstract class XBeeAbstractStatusResponse extends XBeeFrame {

	public XBeeAbstractStatusResponse(int nodeID, XBeeFrameType frameType) {
		super(nodeID, frameType);
	}

	abstract public boolean isSuccess();

}
