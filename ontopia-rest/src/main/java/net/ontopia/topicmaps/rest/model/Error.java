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
package net.ontopia.topicmaps.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashSet;
import java.util.Set;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestException;
import org.restlet.data.Status;

@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class Error {

	@JsonProperty
	private int httpcode;
	
	@JsonProperty
	private int code = -1;
	
	@JsonProperty
	private String message;
	
	@JsonProperty
	private String description;
	
	@JsonProperty
	private String[] causes = new String[0];

	public Error(Status status) {
		this.httpcode = status.getCode();
		this.message = status.toString();
		this.description = status.getDescription();
		if (status.getThrowable() != null) {
			Throwable t = status.getThrowable();
			if (t instanceof OntopiaRestException) {
				code = ((OntopiaRestException) t).getOntopiaCode();
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

	// for jackson
	public Error() {
	}

	public int getHttpcode() {
		return httpcode;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public String getDescription() {
		return description;
	}

	public String[] getCauses() {
		return causes;
	}
}
