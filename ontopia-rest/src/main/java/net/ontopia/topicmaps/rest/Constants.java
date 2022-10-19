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

import org.restlet.data.MediaType;

public final class Constants {
	
	public static final String N_O_T_R = Constants.class.getPackage().getName();
	
	public static final String LOG_CLIENT_ERRORS_PARAMETER = N_O_T_R + ".logging.clientErrors";
	public static final boolean LOG_CLIENT_ERRORS_FALLBACK = false;
	
	// abstract ontopia resource
	public static final String STRICT_MIME_MATCHING_PARAMETER = N_O_T_R + ".strict";

	// abstract paged resource
	public static final String PAGING_PARAMETER = N_O_T_R + ".paging";
	public static final String DEFAULT_PAGING_LIMIT_PARAMETER = N_O_T_R + ".paging.limit";
	public static final String DEFAULT_PAGING_OFFSET_PARAMETER = N_O_T_R + ".paging.offset";
	public static final boolean PAGING_FALLBACK = true;
	public static final int PAGING_LIMIT_FALLBACK = 100;
	public static final int PAGING_OFFSET_FALLBACK = 0;
	
	// v1
	public static final String V1 = N_O_T_R + ".v1.";
	public static final String V1_COMPRESSION_PARAMETER = V1 + "compression";
	
	// controllers
	public static final String V1_TOPICMAP_CONTROLLER_ALLOW_DELETE_PARAMETER = V1 + "topicmap.supportDelete";
	public static final boolean V1_TOPICMAP_CONTROLLER_ALLOW_DELETE_FALLBACK = false;
	
	public static final MediaType LTM_MEDIA_TYPE = new MediaType("text/ltm", "LTM topicmaps format");
	public static final MediaType CTM_MEDIA_TYPE = new MediaType("text/ctm", "CTM topicmaps format");
	public static final MediaType TMXML_MEDIA_TYPE = new MediaType("application/xml+tmxml", "TMXML topicmaps format");
	public static final MediaType XTM_MEDIA_TYPE = new MediaType("application/xml+xtm", "XTM 2.1 topicmaps format");
	public static final MediaType JTM_MEDIATYPE = new MediaType("application/jtm", "JTM 1.1 topicmap json format");

	// headers
	public static final String HEADER_ONTOPIA_APPLICATION = "X-Ontopia-Application";
	public static final String HEADER_ONTOPIA_API_VERSION = "X-Ontopia-API-Version";
	public static final String HEADER_ONTOPIA_RESOURCE = "X-Ontopia-Resource";
	public static final String HEADER_ONTOPIA_TOPICMAP = "X-Ontopia-Topicmap";
	public static final String HEADER_PAGING_OFFSET = "X-Paging-Offset";
	public static final String HEADER_PAGING_COUNT = "X-Paging-Count";
	public static final String HEADER_PAGING = "X-Paging";
	public static final String HEADER_PAGING_LIMIT = "X-Paging-Limit";

	private Constants() {
		// don't call me
	}
}
