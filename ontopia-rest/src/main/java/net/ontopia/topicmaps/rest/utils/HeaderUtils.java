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

package net.ontopia.topicmaps.rest.utils;

import org.restlet.Response;
import org.restlet.engine.header.Header;
import org.restlet.util.Series;

public class HeaderUtils {

	@SuppressWarnings("unchecked")
	public static void addResponseHeader(Response response, String header, String value) {
		Series<Header> responseHeaders = (Series<Header>) response.getAttributes().get("org.restlet.http.headers");
		if (responseHeaders == null) {
			responseHeaders = new Series<>(Header.class);
			response.getAttributes().put("org.restlet.http.headers", responseHeaders);
		}
		responseHeaders.add(new Header(header, value));
	}
}
