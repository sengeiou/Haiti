/*******************************************************************************
 * Copyright (c) 2014, Institute for Pervasive Computing, ETH Zurich.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * This file is part of the Californium (Cf) CoAP framework.
 ******************************************************************************/
package com.aimir.fep.protocol.coap.server.resources;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static org.eclipse.californium.core.coap.CoAP.ResponseCode.*;
import static org.eclipse.californium.core.coap.MediaTypeRegistry.*;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.server.resources.CoapExchange;
import com.aimir.fep.protocol.coap.server.ResourceBase;
/**
 * This resource implements a test of specification for the
 * ETSI IoT CoAP Plugtests, Las Vegas, NV, USA, 19 - 22 Nov 2013.
 * 
 * @author Matthias Kovatsch
 */
public class ObservePumping extends ResourceBase {

	// Members ////////////////////////////////////////////////////////////////

	private final static String PADDING = "----------------------------------------------------------------";

	// The current time represented as string
	private String time;

	/*
	 * Constructor for a new TimeResource
	 */
	public ObservePumping() {
		super("obs-pumping");
		setObservable(true);
		getAttributes().setTitle("Observable resource which changes every 5 seconds");
		getAttributes().addResourceType("observe");
		getAttributes().setObservable();
		setObserveType(Type.CON);

		// Set timer task scheduling
		Timer timer = new Timer();
		timer.schedule(new TimeTask(), 0, 5000);
	}

	/*
	 * Defines a new timer task to return the current time
	 */
	private class TimeTask extends TimerTask {

		@Override
		public void run() {
			if (Math.random()>0.5) {
				time = String.format("%.31s\n%19s\n%.31s\n", PADDING, getTime(), PADDING);
			} else if (Math.random()>0.5) {
				time = String.format("%.63s\n%35s\n%.63s\n", PADDING, getTime(), PADDING);
			} else {
				time = String.format("%s", getTime());
			}
			
			// Call changed to notify subscribers
			changed();
		}
	}

	/*
	 * Returns the current time
	 * 
	 * @return The current time
	 */
	private String getTime() {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date time = new Date();
		return dateFormat.format(time);
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		
		exchange.setMaxAge(5);
		exchange.respond(CONTENT, time, TEXT_PLAIN);
	}

}
