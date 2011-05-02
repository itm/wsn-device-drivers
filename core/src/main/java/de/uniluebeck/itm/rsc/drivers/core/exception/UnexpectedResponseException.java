/**********************************************************************************************************************
 * Copyright (c) 2010, coalesenses GmbH                                                                               *
 * All rights reserved.                                                                                               *
 *                                                                                                                    *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the   *
 * following conditions are met:                                                                                      *
 *                                                                                                                    *
 * - Redistributions of source code must retain the above copyright notice, this list of conditions and the following *
 *   disclaimer.                                                                                                      *
 * - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the        *
 *   following disclaimer in the documentation and/or other materials provided with the distribution.                 *
 * - Neither the name of the coalesenses GmbH nor the names of its contributors may be used to endorse or promote     *
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

package de.uniluebeck.itm.rsc.drivers.core.exception;


/**
 * Exception is thrown when a response occured that was not expected from the device.
 * 
 * @author Malte Legenhausen
 */
public class UnexpectedResponseException extends Exception {
	
	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 2887597938554231905L;

	/**
	 * The expected response from the device.
	 */
	private int expectedResponse = -1;

	/**
	 * The unexpected received response from the device.
	 */
	private int receivedResponse = -1;

	/**
	 * Constructor.
	 * 
	 * @param expectedResponse The expected response from the device.
	 * @param receivedResponse The received response from the device.
	 */
	public UnexpectedResponseException(final int expectedResponse, final int receivedResponse) {
		this.expectedResponse = expectedResponse;
		this.expectedResponse = receivedResponse;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param message A exception description
	 * @param expectedResponse The expected response from the device.
	 * @param receivedResponse The received response from the device.
	 */
	public UnexpectedResponseException(final String message, final int expectedResponse, final int receivedResponse) {
		super(message);
		this.expectedResponse = expectedResponse;
		this.expectedResponse = receivedResponse;
	}

	/**
	 * Returns the expected response.
	 * 
	 * @return The expected response.
	 */
	public int getExpectedResponse() {
		return expectedResponse;
	}

	/**
	 * Returns the received response.
	 * 
	 * @return The received response.
	 */
	public int getReceivedResponse() {
		return receivedResponse;
	}

	@Override
	public String getMessage() {
		if (expectedResponse == -1 || receivedResponse == -1) {
			return getMessage();
		} else {
			return String.format(getMessage() + " Expected type: 0x%02x, received type: 0x%02x.",expectedResponse, receivedResponse);
		}
	}
}
