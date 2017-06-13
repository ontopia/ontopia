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

package net.ontopia.topicmaps.rest.exceptions;

import org.restlet.data.Status;

public class OntopiaRestException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private Status status;
	private int ontopiaCode = -1;
	
	public OntopiaRestException(OntopiaRestErrors error, Object... parameters) {
		this(error, null, parameters);
	}
	public OntopiaRestException(OntopiaRestErrors error, Throwable cause, Object... parameters) {
		super(error.getMessage(parameters), cause);
		status = new Status(error.getStatus(), this, getMessage());
		ontopiaCode = error.getCode();
	}

	public OntopiaRestException(Status status) {
		super(status.getDescription());
		this.status = status;
	}

	public OntopiaRestException(Status status, String description) {
		super(description);
		this.status = new Status(status, this, description);
	}

	public OntopiaRestException(Status status, String description, Throwable cause) {
		super(description, cause);
		this.status = new Status(status, this, description);
	}

	public OntopiaRestException(Status status, Throwable cause) {
		super(cause);
		this.status = new Status(status, this);
	}

	public Status getStatus() {
		return status;
	}

	public int getOntopiaCode() {
		return ontopiaCode;
	}
}
