/**********************************************************************************************************************
 * Copyright (c) 2011, Institute of Telematics, University of Luebeck                                                 *
 * All rights reserved.                                                                                               *
 *                                                                                                                    *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the   *
 * following conditions are met:                                                                                      *
 *                                                                                                                    *
 * - Redistributions of source code must retain the above copyright notice, this list of conditions and the following *
 *   disclaimer.                                                                                                      *
 * - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the        *
 *   following disclaimer in the documentation and/or other materials provided with the distribution.                 *
 * - Neither the name of the University of Luebeck nor the names of its contributors may be used to endorse or promote*
 *   products derived from this software without specific prior written permission.                                   *
 *                                                                                                                    *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, *
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE      *
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,         *
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE *
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF    *
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY   *
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.                                *
 **********************************************************************************************************************/
package eu.smartsantander.wsn.drivers.waspmote.operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.wsn.drivers.core.exception.UnexpectedResponseException;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractSendOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import eu.smartsantander.wsn.drivers.waspmote.WaspmoteDevice;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.XBeeFrame;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbeeDigi.XBeeDigiRequest;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbeeDigi.XBeeDigiStatusResponse;



/**
 * @author massel
 *
 */
public class XBeeSendOperation extends AbstractSendOperation {
    
    /**
     * Logger for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(XBeeSendOperation.class);

    private static final int MAX_LENGTH = 150;
    
    private final WaspmoteDevice device;
    private final int operationID;
    
    public XBeeSendOperation(WaspmoteDevice device, int operationID) {
        this.device = device;
        this.operationID = operationID;
    
    }
    
    /* (non-Javadoc)
     * @see de.uniluebeck.itm.wsn.drivers.core.operation.Operation#execute(de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager)
     */
    @Override
    public Void execute(ProgressManager progressManager) throws Exception {
    LOG.debug("Executing send operation");
        
        final byte content[] = getMessage();
        if (content.length > MAX_LENGTH) {
            LOG.warn("Skipping too large packet (length " + content.length + ")");
            return null;
        }
        XBeeDigiRequest xBeeDigiRequest = new XBeeDigiRequest(device.getNodeID(), content);
        device.sendXBeeMessage(xBeeDigiRequest, true, operationID);
        progressManager.worked(0.25f);
        XBeeFrame xbeeFrame;
        xbeeFrame = device.receiveXBeeFrame(operationID);
        if (xbeeFrame instanceof XBeeDigiStatusResponse) {
            LOG.debug("localACK from node " + device.getNodeID() + " arrived");
            progressManager.worked(1f);
            return null;
        }else{
            throw new UnexpectedResponseException("The node didn't reply as expected", 1, 0);    
        }
               
    }

}
