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
	
	private Constants() {
		// don't call me
	}
	
	public static final MediaType LTM_MEDIA_TYPE = new MediaType("text/ltm", "LTM topicmaps format");
	public static final MediaType CTM_MEDIA_TYPE = new MediaType("text/ctm", "CTM topicmaps format");
	public static final MediaType TMXML_MEDIA_TYPE = new MediaType("application/xml+tmxml", "TMXML topicmaps format");
	public static final MediaType XTM_MEDIA_TYPE = new MediaType("application/xml+xtm", "XTM 2.1 topicmaps format");
	public static final MediaType JTM_MEDIATYPE = new MediaType("application/jtm", "JTM 1.1 topicmap json format");

}
