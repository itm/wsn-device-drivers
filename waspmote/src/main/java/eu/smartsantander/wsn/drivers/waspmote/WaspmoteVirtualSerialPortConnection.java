package eu.smartsantander.wsn.drivers.waspmote;

import java.io.IOException;

import com.google.common.io.Closeables;

import de.uniluebeck.itm.wsn.drivers.core.AbstractConnection;
import eu.smartsantander.wsn.drivers.waspmote.multiplexer.WaspmoteConnectionMultiplexer;

/**
 * @author TLMAT UC
 */
public class WaspmoteVirtualSerialPortConnection extends AbstractConnection {

	/**
	 * Real multiplexed serial port connection.
	 */
	private final WaspmoteMultiplexedSerialPortConnection connection;

	/**
	 * Virtual device node identifier attached to this virtual connection.
	 */
	private int nodeID = -1;

	/**
	 * Constructor
	 */
	public WaspmoteVirtualSerialPortConnection() {
		super();
		this.connection = WaspmoteMultiplexedSerialPortConnection.getInstance();
	}

	/**
	 * @param nodeID
	 *            The Waspmote virtual device node identifier attached to this
	 *            virtual serial port connection.
	 */
	public void setDeviceNodeID(int nodeID) {
		this.nodeID = nodeID;
	}

	public WaspmoteConnectionMultiplexer getSerialPortMultiplexer() {
		return connection.getSerialPortMultiplexer();
	}

	@Override
	public void connect(String uri) {
		if (!isConnected() && nodeID != -1) {
			connection.connect(uri);
			this.setUri(uri);
			this.setInputStream(connection.getSerialPortMultiplexer().registerNode(nodeID));
			this.setOutputStream(connection.getOutputStream());
			this.setConnected(true);
		}
	}

	@Override
	public void close() throws IOException {
		Closeables.close(getInputStream(), true);
		setInputStream(null);
		setOutputStream(null);
		connection.getSerialPortMultiplexer().deregisterNode(nodeID);
		connection.shutdown(false);
		this.setConnected(false);
	}

}
