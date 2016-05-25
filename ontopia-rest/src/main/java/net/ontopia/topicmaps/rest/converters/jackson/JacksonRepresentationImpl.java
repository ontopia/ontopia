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

package net.ontopia.topicmaps.rest.converters.jackson;

import org.restlet.data.MediaType;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JacksonRepresentationImpl<T> extends JacksonRepresentation<T> {
	private static final Logger logger = LoggerFactory.getLogger(JacksonRepresentationImpl.class);
	public static final String ADDITIONAL_MIXINS_ATTRIBUTE = JacksonConverterImpl.class.getName() + ".mixins";
	
	public JacksonRepresentationImpl(MediaType mediaType, T object) {
		super(mediaType, object);
	}

	public JacksonRepresentationImpl(Representation representation, Class<T> objectClass) {
		super(representation, objectClass);
	}

	public JacksonRepresentationImpl(T object) {
		super(object);
	}
}
