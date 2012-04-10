package de.uniluebeck.itm.wsn.drivers.core.io;

import com.google.inject.Inject;
import de.uniluebeck.itm.wsn.drivers.core.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;


/**
 * This wrapper allows to the write to the device through an <code>OutputStream</code>.
 *
 * @author Malte Legenhausen
 * @author Dennis Pfisterer
 * @author Daniel Bimschas
 */
public class SendOutputStreamWrapper extends OutputStream {

	private static final Logger log = LoggerFactory.getLogger(SendOutputStreamWrapper.class);

	/**
	 * The maximum timeout for the send operation.
	 */
	private static final int SEND_TIMEOUT = 30000;

	/**
	 * The device that is used for sending data to the device.
	 */
	private final Device device;

	/**
	 * Constructor.
	 *
	 * @param device
	 * 		The async device.
	 */
	@Inject
	public SendOutputStreamWrapper(Device device) {
		this.device = device;
	}


	@Override
	public void write(int b) throws IOException {
		write(new byte[]{(byte) b});
	}

	@Override
	public void write(final byte[] b) throws IOException {
		try {
			log.trace("Sending {} bytes to the device", b.length);
			device.send(b, SEND_TIMEOUT, null);
		} catch (RuntimeException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void write(final byte[] b, final int off, final int len) throws IOException {

		if (b == null) {
			throw new NullPointerException();
		} else if ((off < 0) || (off > b.length) || (len < 0) ||
				((off + len) > b.length) || ((off + len) < 0)) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return;
		}

		if (off != 0 || len != b.length) {
			byte[] slice = new byte[len];
			System.arraycopy(b, off, slice, 0, len);
			write(slice);
		} else {
			write(b);
		}
	}
}
