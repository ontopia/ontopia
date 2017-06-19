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

public enum OntopiaRestErrors {
	
	// Topicmap IO related
	TOPICMAP_NOT_FOUND(Status.CLIENT_ERROR_NOT_FOUND, 100, "The specified topicmap could not be found"),
	CANNOT_OPEN_STORE(Status.SERVER_ERROR_INTERNAL, 101, "Could not open store"), 
	COULD_NOT_READ_FRAGMENT(Status.CLIENT_ERROR_BAD_REQUEST, 102, "Could not read the provided topicmap fragment"),
	INDEX_NOT_SUPPORTED(Status.CLIENT_ERROR_EXPECTATION_FAILED, 103, "This topicmap does not support index of type %s"),
	INDEX_USAGE_ERROR(Status.CLIENT_ERROR_BAD_REQUEST, 104, "Error during index %s usage"),
	TOPICMAP_DELETE_NOT_SUPPORTED(Status.CLIENT_ERROR_FORBIDDEN, 105, "Cannot remove topicmap %s, source does not support deletion"),
	
	// Attribute/parameter failures
	MANDATORY_ATTRIBUTE_IS_NULL(Status.CLIENT_ERROR_BAD_REQUEST, 201, "Attribute '%s' expected %s, found null"),
	MANDATORY_ATTRIBUTE_IS_WRONG_TYPE(Status.CLIENT_ERROR_BAD_REQUEST, 202, "Attribute '%s' expected %s, found %s"), 
	MANDATORY_FIELD_IS_NULL(Status.CLIENT_ERROR_BAD_REQUEST, 203, "Field '%s' expected, found null"),
	MANDATORY_FIELD_IS_WRONG_TYPE(Status.CLIENT_ERROR_BAD_REQUEST, 204, "Field '%s' expected %s, found %s"),
	MANDATORY_OBJECT_IS_NULL(Status.CLIENT_ERROR_BAD_REQUEST, 205, "Object of type '%s' expected, found null"),
	MANDATORY_OBJECT_IS_WRONG_TYPE(Status.CLIENT_ERROR_BAD_REQUEST, 206, "Object of type '%s' expected, found %s"),
	MALFORMED_LOCATOR(Status.CLIENT_ERROR_BAD_REQUEST, 207, "Invalid locator '%s'"),
	EMPTY_ENTITY(Status.CLIENT_ERROR_BAD_REQUEST, 208, "Unexpected empty request entity"),
	
	// mime mismatch
	UNSUPPORTED_MIME_TYPE(Status.CLIENT_ERROR_NOT_ACCEPTABLE, 300, "%s cannot provide %s"),
	
	// query
	UNKNOWN_QUERY_LANGUAGE(Status.CLIENT_ERROR_BAD_REQUEST, 400, "Unknown query language '%s'"),
	INVALID_QUERY(Status.CLIENT_ERROR_BAD_REQUEST, 401, "Invalid query: %s");

	private final Status status;
	private final int code;
	private final String message;
	
	OntopiaRestErrors(Status status, int ontopiaCode, String message) {
		this.code = ontopiaCode;
		this.message = message;
		this.status = status;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public String getMessage(Object... parameters) {
		return String.format(message, parameters);
	}

	public boolean isClientError() {
		return status.isClientError();
	}

	public Status getStatus() {
		return status;
	}
	
	public OntopiaRestException build(Object... parameters) {
		return build(null, parameters);
	}
	public OntopiaRestException build(Throwable cause, Object... parameters) {
		OntopiaRestException exception;
		if (isClientError()) {
			exception = new OntopiaClientException(this, cause, parameters);
		} else {
			exception = new OntopiaServerException(this, cause, parameters);
		}
		return exception;
	}
}
