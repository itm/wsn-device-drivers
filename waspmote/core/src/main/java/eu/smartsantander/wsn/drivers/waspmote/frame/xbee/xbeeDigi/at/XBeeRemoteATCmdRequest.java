package eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbeeDigi.at;

import com.google.common.base.Preconditions;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.AbstractXBeeRequest;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.XBeeFrameType;

/**
 * @author TLMAT UC
 */
public class XBeeRemoteATCmdRequest extends AbstractXBeeRequest {

    private final String commandName;
    private final byte options;

    public XBeeRemoteATCmdRequest(int nodeID, String commandName, byte[] commandParameter) {
        this(nodeID, commandName, commandParameter, (byte) 0x02);
    }

    public XBeeRemoteATCmdRequest(int nodeID, String commandName, byte[] commandParameter, byte options) {
        super(nodeID, XBeeFrameType.REMOTE_AT_CMD_REQUEST_DIGIMESH, commandParameter);
        Preconditions.checkArgument(commandName.length() == 2, "AT command name MUST be 2 characters long.");
        this.commandName = commandName.toUpperCase();
        this.options = options;
    }

    public String getCommandName() {
        return commandName;
    }

    public byte getRemoteCommandOptions() {
        return options;
    }
}
