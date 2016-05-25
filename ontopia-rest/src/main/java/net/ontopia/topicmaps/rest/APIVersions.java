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

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Filter;
import org.restlet.routing.Router;

public enum APIVersions {
	
	V1("v1") {
		@Override
		public Restlet createChain(Application application) {
			final Context context = application.getContext();
			
			// v1 has gzip compression
			// todo: en/disable via options
			Filter encoder = application.getEncoderService().createInboundFilter(context);
			encoder.setName("Response encoder");
			encoder.setDescription("Allows Ontopia API call responses to be compressed");

			// under compression, router
			Router router = new Router(context);
			router.setName("v1 main router");
			encoder.setNext(router);
			
			return encoder;
		}
	};
	
	private final String name;
	
	private APIVersions(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public abstract Restlet createChain(Application application);
	
}
