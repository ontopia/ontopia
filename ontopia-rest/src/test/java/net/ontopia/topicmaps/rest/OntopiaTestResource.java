/*
 * #!
 * Ontopia Rest
 * #-
 * Copyright (C) 2001 - 2017 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.topicmaps.rest;

import java.io.IOException;
import net.ontopia.topicmaps.rest.model.Error;
import org.restlet.Client;
import org.restlet.data.ClientInfo;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

public class OntopiaTestResource extends ClientResource {
	private static final Client client;
	
	static {
		// hides 'Starting the internal HTTP client' log spam
		System.setProperty("org.restlet.engine.loggerFacadeClass", "org.restlet.ext.slf4j.Slf4jLoggerFacade");

		client = new Client(Protocol.HTTP);
	}

	private Error ontopiaErrorPojo = null;

	public OntopiaTestResource(Method method, String url, MediaType preferredMime) {
		super(method, url);
		setNext(client);
		setClientInfo(new ClientInfo(preferredMime));
	}

	@Override
	public void doError(Status errorStatus) {
		if (getOntopiaError() != null) {
			throw new ResourceException(errorStatus, ontopiaErrorPojo.getMessage());
		}
		super.doError(errorStatus);
	}

	public Error getOntopiaError() {
		if ((ontopiaErrorPojo == null) && (getResponse() != null) && getResponse().isEntityAvailable()) {
			try {
				ontopiaErrorPojo = getConverterService().toObject(getResponseEntity(), Error.class, this);
				getResponseEntity().exhaust();
			} catch (IOException ioe) {
				// ignore
			}
		}
		return ontopiaErrorPojo;
	}

	public Representation request() {
		try {
			handle();
			handleInbound(getResponse());
			return getResponseEntity();
		} finally {
			release();
		}
	}

	public <T> T request(Object object, Class<T> expected) {
		try {
			return handle(getMethod(), object, expected);
		} finally {
			release();
		}
	}
}
