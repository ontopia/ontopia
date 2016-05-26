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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.rest.model.mixin.MAssociation;
import net.ontopia.topicmaps.rest.model.mixin.MAssociationRole;
import net.ontopia.topicmaps.rest.model.mixin.MLocator;
import net.ontopia.topicmaps.rest.model.mixin.MOccurrence;
import net.ontopia.topicmaps.rest.model.mixin.MTopic;
import net.ontopia.topicmaps.rest.model.mixin.MTopicMapAsValue;
import net.ontopia.topicmaps.rest.model.mixin.MTopicName;
import net.ontopia.topicmaps.rest.model.mixin.MVariantName;
import org.restlet.Response;
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

	@Override
	protected ObjectMapper createObjectMapper() {
		ObjectMapper mapper = super.createObjectMapper();
		mapper.addMixInAnnotations(LocatorIF.class, MLocator.class);
		mapper.addMixInAnnotations(TopicIF.class, MTopic.class);
		mapper.addMixInAnnotations(TopicNameIF.class, MTopicName.class);
		mapper.addMixInAnnotations(VariantNameIF.class, MVariantName.class);
		mapper.addMixInAnnotations(OccurrenceIF.class, MOccurrence.class);
		mapper.addMixInAnnotations(AssociationIF.class, MAssociation.class);
		mapper.addMixInAnnotations(AssociationRoleIF.class, MAssociationRole.class);
		mapper.addMixInAnnotations(TopicMapIF.class, MTopicMapAsValue.class);
		
		@SuppressWarnings("unchecked")
		Map<Class<?>, Class<?>> additional = (Map<Class<?>, Class<?>>) Response.getCurrent().getAttributes().get(ADDITIONAL_MIXINS_ATTRIBUTE);

		if ((additional != null) && (!additional.isEmpty())) {
			for (Map.Entry<Class<?>, Class<?>> entry : additional.entrySet()) {
				mapper.addMixInAnnotations(entry.getKey(), entry.getValue());
			}
		}
		
		return mapper;
	}
}
