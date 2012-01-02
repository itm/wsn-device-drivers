package eu.smartsantander.wsn.drivers.waspmote.frame.xbee;


public abstract class AbstractXBeeStatusResponse extends AbstractXBeeFrame {

	public AbstractXBeeStatusResponse(int nodeID, XBeeFrameType frameType) {
		super(nodeID, frameType);
	}

	abstract public boolean isSuccess();

}
