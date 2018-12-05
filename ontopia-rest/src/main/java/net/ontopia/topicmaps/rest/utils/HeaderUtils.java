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

import net.ontopia.topicmaps.rest.Constants;
import org.restlet.Response;
import org.restlet.engine.header.Header;
import org.restlet.util.Series;

public class HeaderUtils {

	public static void addResponseHeader(Response response, String header, String value) {
		getHeaders(response).add(new Header(header, value));
	}

	@SuppressWarnings("unchecked")
	public static Series<Header> getHeaders(Response response) {
		Series<Header> headers = (Series<Header>) response.getAttributes().get("org.restlet.http.headers");
		if (headers == null) {
			headers = new Series<>(Header.class);
			response.getAttributes().put("org.restlet.http.headers", headers);
		}
		return headers;
	}
	
	public static int getCount(Response response) {
		return getIntHeader(response, Constants.HEADER_PAGING_COUNT);
	}

	public static int getLimit(Response response) {
		return getIntHeader(response, Constants.HEADER_PAGING_LIMIT);
	}

	public static int getOffset(Response response) {
		return getIntHeader(response, Constants.HEADER_PAGING_OFFSET);
	}

	private static int getIntHeader(Response response, String header) {
		try {
			return Integer.parseInt(getHeaders(response).getFirstValue(header));
		} catch (NumberFormatException nfe) {
			return -1;
		}
	}
}
