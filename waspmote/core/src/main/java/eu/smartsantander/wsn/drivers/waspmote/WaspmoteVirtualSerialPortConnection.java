package eu.smartsantander.wsn.drivers.waspmote;

import java.io.IOException;

import com.google.common.base.Preconditions;
import com.google.common.io.Closeables;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import de.uniluebeck.itm.wsn.drivers.core.AbstractConnection;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.AbstractXBeeRequest;
import eu.smartsantander.wsn.drivers.waspmote.multiplexer.*;

/**
 * @author TLMAT UC
 */
public class WaspmoteVirtualSerialPortConnection extends AbstractConnection implements SerialPortDataAvailableNotifier {

    /**
     * Virtual device node identifier attached to this virtual connection.
     */
    private final int nodeID;

    /**
     * Information regarding node's physical interface.
     */
    private final NodeConnectionInfo nodeMACs;

	/**
	 * Real multiplexed serial port connection.
	 */
   private final WaspmoteConnectionMultiplexer connectionMultiplexer;

	/**
	 * Constructor
     * @param nodeID
     *          The Waspmote virtual device node identifier attached to this virtual serial port connection.
     * @param connectionMultiplexer
     *          The controller that interacts with the unique real serial port.
	 */
    @Inject
	public WaspmoteVirtualSerialPortConnection(@Named("nodeID") int nodeID,
                                               @Named("nodeMACs")NodeConnectionInfo nodeMACs,
                                               WaspmoteConnectionMultiplexer connectionMultiplexer) {
		super();
        this.nodeID = nodeID;
        this.nodeMACs = nodeMACs;
        this.connectionMultiplexer = connectionMultiplexer;
	}

    public void write(AbstractXBeeRequest xbeeRequest, boolean generateLocalAck, WaspmoteSubchannel operationSubchannel) {
        try {
            connectionMultiplexer.write(xbeeRequest, generateLocalAck, operationSubchannel);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

	@Override
	public void connect(String uri) {
		if (!isConnected() && nodeID != -1) {
			this.setUri(uri);
            MultiplexedStreamInfo multiplexedStreamInfo = Preconditions.checkNotNull(connectionMultiplexer.registerNode(nodeID, nodeMACs, this));
            this.setOutputStream(multiplexedStreamInfo.getOutputStream());
            this.setInputStream(multiplexedStreamInfo.getInputStream());
			this.setConnected();
		}
	}

	@Override
	public void close() throws IOException {
		Closeables.close(getInputStream(), true);
		this.setInputStream(null);
		this.setOutputStream(null);
		connectionMultiplexer.deregisterNode(nodeID);
	}

	@Override
	public void notifyDataAvailable() {
		this.signalDataAvailable();
	}

    @Override
    public int[] getChannels() {
        return null;  //Do not know what it means
    }
}
