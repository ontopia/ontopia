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

package net.ontopia.topicmaps.rest.resources;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.rest.OntopiaRestApplication;
import net.ontopia.topicmaps.rest.converters.jackson.JacksonRepresentationImpl;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.utils.HeaderUtils;
import org.restlet.data.MediaType;
import org.restlet.data.Preference;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

public class AbstractOntopiaResource extends ServerResource {

	@Override
	protected void doInit() throws ResourceException {
		setInfoHeaders();
	}
	
	protected OntopiaRestApplication getOntopia() {
		return (OntopiaRestApplication) getApplication();
	}

	protected TopicMapReferenceIF getTopicMapReference() {
		return getOntopia().getTopicMapReference(getRequest());
	}

	protected void setInfoHeaders() {
		HeaderUtils.addResponseHeader(getResponse(), "X-Ontopia-Resource", getClass().getName());
		HeaderUtils.addResponseHeader(getResponse(), "X-Ontopia-Application", getOntopia().getClass().getName());
	}
	
	/**
	 * Blocks the use of specified mime types for this resource, as it is known that the converter for that mime type
	 * cannot produce the representation for the Resource's target class.
	 * @param types The mime types to block
	 */
	protected void blockMimeType(MediaType... types) {
		List<Preference<MediaType>> acceptedMediaTypes = getClientInfo().getAcceptedMediaTypes();
		if (acceptedMediaTypes.size() > types.length) {
			return;
		}	
	
		Set<MediaType> accepted = new HashSet<>(acceptedMediaTypes.size());
		for (Preference<MediaType> p : acceptedMediaTypes) {
			accepted.add(p.getMetadata());
		}
		
		accepted.removeAll(Arrays.asList(types));
		
		if (accepted.isEmpty()) {
			throw OntopiaRestErrors.UNSUPPORTED_MIME_TYPE.build(getClass().getName(), Arrays.toString(types));
		}
	}

	protected void addMixInAnnotations(Class<?> target, Class<?> mixin) {
		getMixInAnnotationsMap().put(target, mixin);
	}

	protected Map<Class<?>, Class<?>> getMixInAnnotationsMap() {
		ConcurrentMap<String, Object> attributes = getResponse().getAttributes();
		@SuppressWarnings(value = "unchecked")
		Map<Class<?>, Class<?>> mixins = (Map<Class<?>, Class<?>>) attributes.get(JacksonRepresentationImpl.ADDITIONAL_MIXINS_ATTRIBUTE);
		if (mixins == null) {
			mixins = new HashMap<>();
			attributes.put(JacksonRepresentationImpl.ADDITIONAL_MIXINS_ATTRIBUTE, mixins);
		}
		return mixins;
	}
	
	protected int getIntegerFromQuery(String name, int fallback) {
		String value = getQueryValue(name);
		if (value == null) {
			return fallback;
		}
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException nfe) {
			throw OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE.build(nfe, name, "integer", value);
		}
	}
}
