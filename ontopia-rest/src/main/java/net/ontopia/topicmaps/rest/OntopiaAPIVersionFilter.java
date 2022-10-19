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

import net.ontopia.Ontopia;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.routing.Filter;

// sets the version object in the request and response attributes
class OntopiaAPIVersionFilter extends Filter {

	private final APIVersions version;

	public OntopiaAPIVersionFilter(Context context, Restlet next, APIVersions version) {
		super(context, next);
		this.version = version;
		setName("Ontopia API version filter");
		setDescription("Sets the version of the API that the resource was called in, as displayed in the response header");
	}

	@Override
	protected int beforeHandle(Request request, Response response) {
		response.getHeaders().add(Constants.HEADER_ONTOPIA_API_VERSION, version.getName());
		response.getServerInfo().setAgent(Ontopia.getInfo() + " rest API " + version.getName());
		return super.beforeHandle(request, response);
	}
}
