package de.uniluebeck.itm.wsn.drivers.core.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.uniluebeck.itm.wsn.drivers.core.async.AsyncAdapter;
import de.uniluebeck.itm.wsn.drivers.core.async.DeviceAsync;


/**
 * This wrapper allows to the write to the deviceAsync through an <code>OutputStream</code>.
 * Use the flush operation to send everything to the deviceAsync.
 * 
 * @author Malte Legenhausen
 */
public class SendOutputStreamWrapper extends ByteArrayOutputStream {

	/**
	 * The internal buffer size.
	 */
	private static final int BUFFER_SIZE = 2048;
	
	/**
	 * The maximum timeout for the send operation.
	 */
	private static final int SEND_TIMEOUT = 30000;
	
	/**
	 * The deviceAsync that is used for sending data to the deviceAsync.
	 */
	private final DeviceAsync deviceAsync;

	/**
	 * Constructor.
	 * 
	 * @param deviceAsync The async deviceAsync.
	 */
	public SendOutputStreamWrapper(final DeviceAsync device) {
		super(BUFFER_SIZE);
		this.deviceAsync = device;
	}
	
	/**
	 * Use this method to send all written data to the deviceAsync and reset the stream.
	 */
	@Override
	public void flush() throws IOException {
		try {
			deviceAsync.send(toByteArray(), SEND_TIMEOUT, new AsyncAdapter<Void>());
		} catch (RuntimeException e) {
			throw new IOException(e);
		}
		reset();
	}
}
