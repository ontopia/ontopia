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
import org.restlet.ext.jackson.JacksonConverter;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Resource;

/**
 * Extension of the default jackson converter that allows the use of @JsonView.
 */
public class JacksonConverterImpl extends JacksonConverter {
	private static final float MULTIPLIER = 1.1F;

	@Override
	protected <T> JacksonRepresentation<T> create(MediaType mediaType, T source) {
		return new JacksonRepresentationImpl<>(mediaType, source);
	}

	@Override
	protected <T> JacksonRepresentation<T> create(Representation source, Class<T> objectClass) {
		return new JacksonRepresentationImpl<>(source, objectClass);
	}

	@Override
	public float score(Object source, Variant target, Resource resource) {
		return super.score(source, target, resource) * MULTIPLIER;
	}

	@Override
	public <T> float score(Representation source, Class<T> target, Resource resource) {
		return super.score(source, target, resource) * MULTIPLIER;
	}
}
