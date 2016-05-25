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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashSet;
import java.util.Set;
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
			return new JacksonRepresentation<>(new ErrorJson(status));
		//}
		//return null;
	}

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public static class ErrorJson {

		@JsonProperty private int httpcode;
		@JsonProperty private int code = -1;
		@JsonProperty private String message;
		@JsonProperty private String description;
		@JsonProperty private String[] causes = new String[0];
		
		public ErrorJson(Status status) {
			this.httpcode = status.getCode();
			this.message = status.toString();
			this.description = status.getDescription();

			if (status.getThrowable() != null) {
				Throwable t = status.getThrowable();
				if (t instanceof OntopiaRestException) {
					code = ((OntopiaRestException)t).getOntopiaCode();
				}
				
				Set<String> c = new HashSet<>();
				while (t.getCause() != null) {
					t = t.getCause();
					c.add(t.getClass().getName() + ": " + (t.getMessage() != null ? t.getMessage() : ""));
				}
				if (!c.isEmpty()) {
					causes = c.toArray(causes);
				}
			}
		}
	}

}
