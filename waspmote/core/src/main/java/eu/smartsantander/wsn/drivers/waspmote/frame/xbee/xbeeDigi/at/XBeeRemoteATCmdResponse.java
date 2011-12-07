package eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbeeDigi.at;

import com.google.common.base.Preconditions;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.XBeeAbstractResponse;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.XBeeFrameType;

/**
 * @author TLMAT UC
 */
public class XBeeRemoteATCmdResponse extends XBeeAbstractResponse {

    private final String commandName;
    private final CommandStatus commandStatus;

    public XBeeRemoteATCmdResponse(int nodeID, String commandName, byte commandStatus, byte[] commandData) {
        super(nodeID, XBeeFrameType.REMOTE_AT_CMD_RESPONSE_DIGIMESH, commandData);
        Preconditions.checkArgument(commandName.length() == 2, "AT command name MUST be 2 characters long.");
        this.commandName = commandName;
        this.commandStatus = CommandStatus.getCommandStatus(commandStatus);
        Preconditions.checkArgument(this.commandStatus != null);
    }

    public String getCommandName() {
        return commandName;
    }

    public CommandStatus getCommandStatus() {
        return commandStatus;
    }

    public boolean isOK() {
		return commandStatus == CommandStatus.OK;
	}
}
