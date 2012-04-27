package de.uniluebeck.itm.wsn.drivers.pacemate;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.tr.util.StringUtils;
import de.uniluebeck.itm.wsn.drivers.core.MacAddress;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationFactory;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadMacAddressOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.TimeLimitedOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

public class PacemateReadMacAddressOperation extends TimeLimitedOperation<MacAddress>
		implements ReadMacAddressOperation {

	private static final Logger log = LoggerFactory.getLogger(PacemateReadMacAddressOperation.class);

	private static final int MAC_START = 0x2ff8;

	private static final int MAC_LENGTH = 8;

	private final OperationFactory operationFactory;

	@Inject
	public PacemateReadMacAddressOperation(final TimeLimiter timeLimiter,
										   final OperationFactory operationFactory,
										   @Assisted final long timeoutMillis,
										   @Assisted @Nullable final OperationListener<MacAddress> operationCallback) {
		super(timeLimiter, timeoutMillis, operationCallback);
		this.operationFactory = operationFactory;
	}

	@Override
	protected MacAddress callInternal() throws Exception {

		byte[] header = runSubOperation(
				operationFactory.createReadFlashOperation(MAC_START, MAC_LENGTH, 60000, null),
				1f
		);

		byte[] macUUcode = new byte[4];
		byte[] mac = new byte[8];

		byte[] checksum = new byte[header.length - 13 - 2 - 2];
		System.arraycopy(header, 15, checksum, 0, header.length - 13 - 2 - 2);
		log.debug("Checksum: " + StringUtils.toHexString(checksum));

		System.arraycopy(header, 1, macUUcode, 0, 4);
		byte[] macpart2 = decode(macUUcode);
		mac[0] = macpart2[0];
		mac[1] = macpart2[1];
		mac[2] = macpart2[2];

		System.arraycopy(header, 5, macUUcode, 0, 4);
		macpart2 = decode(macUUcode);
		mac[3] = macpart2[0];
		mac[4] = macpart2[1];
		mac[5] = macpart2[2];

		System.arraycopy(header, 9, macUUcode, 0, 4);
		macpart2 = decode(macUUcode);
		mac[6] = macpart2[0];
		mac[7] = macpart2[1];

		log.debug("Read raw MAC: " + StringUtils.toHexString(mac));
		final MacAddress macAddress = new MacAddress(mac);
		log.debug("Read MAC: " + macAddress);

		log.debug("Done, result is: " + macAddress);
		return macAddress;
	}

	private static byte[] decode(byte[] temp) {

		final byte[] outbyte = new byte[3];

		outbyte[0] = decodeByte(temp[0]);
		outbyte[1] = decodeByte(temp[1]);
		outbyte[0] <<= 2;
		outbyte[0] |= (outbyte[1] >> 4) & 0x03;
		outbyte[1] <<= 4;
		outbyte[2] = decodeByte(temp[2]);
		outbyte[1] |= (outbyte[2] >> 2) & 0x0F;
		outbyte[2] <<= 6;
		outbyte[2] |= decodeByte(temp[3]) & 0x3F;

		return outbyte;
	}

	private static byte decodeByte(final byte b) {
		return b == 0x60 ? 0 : (byte) (b - 0x20);
	}
}
