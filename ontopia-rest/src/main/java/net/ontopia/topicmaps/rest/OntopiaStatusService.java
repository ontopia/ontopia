/*
 * #!
 * Ontopia Rest
 * #-
 * Copyright (C) 2001 - 2016 The Ontopia Project
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

import net.ontopia.topicmaps.rest.exceptions.OntopiaRestException;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.service.StatusService;

public class OntopiaStatusService extends StatusService {
	
	@Override
    public Status getStatus(Throwable throwable, Request request, Response response) {
		
		if (throwable instanceof ResourceException) {
			ResourceException re = (ResourceException) throwable;
			if (re.getCause() == null) {
				return (re.getCause() != null ? new Status(re.getStatus(), re.getCause()) : re.getStatus());
			} else {
				return getStatus(throwable.getCause(), request, response);
			}
		}
		
        if (throwable instanceof OntopiaRestException) {
			OntopiaRestException re = (OntopiaRestException) throwable;
            return re.getStatus();
		}
		
		// fallback
		return new Status(Status.SERVER_ERROR_INTERNAL, throwable);
    }	

	@Override
	public Representation getRepresentation(Status status, Request request, Response response) {
		//if (status.isClientError()) {
			return new JacksonRepresentation<>(new Error(status));
		//}
		//return null;
	}
}
