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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.entry.TopicMapSourceIF;
import net.ontopia.topicmaps.rest.model.mixin.MAssociation;
import net.ontopia.topicmaps.rest.model.mixin.MAssociationRole;
import net.ontopia.topicmaps.rest.model.mixin.MLocator;
import net.ontopia.topicmaps.rest.model.mixin.MOccurrence;
import net.ontopia.topicmaps.rest.model.mixin.MTopic;
import net.ontopia.topicmaps.rest.model.mixin.MTopicMapAsValue;
import net.ontopia.topicmaps.rest.model.mixin.MTopicMapReference;
import net.ontopia.topicmaps.rest.model.mixin.MTopicMapSource;
import net.ontopia.topicmaps.rest.model.mixin.MTopicName;
import net.ontopia.topicmaps.rest.model.mixin.MVariantName;
import net.ontopia.topicmaps.rest.utils.ContextUtils;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Parameter;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;

public class JacksonRepresentationImpl<T> extends JacksonRepresentation<T> {
	public static final String ADDITIONAL_MIXINS_ATTRIBUTE = JacksonConverterImpl.class.getName() + ".mixins";

	private static final String JSONPARSER_FEATURE = JsonParser.class.getName() + ".Feature.";
	private static final String SERIALIZATION_FEATURE = SerializationFeature.class.getName() + ".";

	private static final Collection<JsonParser.Feature> DEFAULT_PARSER_FEATURES = Arrays.asList(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
	private static final Collection<SerializationFeature> DEFAULT_SERIALIZATION_FEATURES = Arrays.asList(SerializationFeature.INDENT_OUTPUT);
	
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
		mapper.addMixIn(LocatorIF.class, MLocator.class);
		mapper.addMixIn(TopicIF.class, MTopic.class);
		mapper.addMixIn(TopicNameIF.class, MTopicName.class);
		mapper.addMixIn(VariantNameIF.class, MVariantName.class);
		mapper.addMixIn(OccurrenceIF.class, MOccurrence.class);
		mapper.addMixIn(AssociationIF.class, MAssociation.class);
		mapper.addMixIn(AssociationRoleIF.class, MAssociationRole.class);
		mapper.addMixIn(TopicMapIF.class, MTopicMapAsValue.class);
		mapper.addMixIn(TopicMapReferenceIF.class, MTopicMapReference.class);
		mapper.addMixIn(TopicMapSourceIF.class, MTopicMapSource.class);
		
		@SuppressWarnings("unchecked")
		Map<Class<?>, Class<?>> additional = (Map<Class<?>, Class<?>>) Response.getCurrent().getAttributes().get(ADDITIONAL_MIXINS_ATTRIBUTE);

		if ((additional != null) && (!additional.isEmpty())) {
			for (Map.Entry<Class<?>, Class<?>> entry : additional.entrySet()) {
				mapper.addMixIn(entry.getKey(), entry.getValue());
			}
		}
		
		for (JsonParser.Feature feature : JsonParser.Feature.values()) {
			Parameter parameter = ContextUtils.getParameter(ContextUtils.getCurrentApplicationContext(), JSONPARSER_FEATURE + feature.name());
			if (parameter != null) {
				mapper.configure(feature, ContextUtils.getParameterAsBoolean(parameter, feature.enabledByDefault() || DEFAULT_PARSER_FEATURES.contains(feature)));
			}
		}
		
		return mapper;
	}

	@Override
	protected ObjectWriter createObjectWriter() {
		ObjectWriter writer = super.createObjectWriter();

		for (SerializationFeature feature : SerializationFeature.values()) {
			boolean hasDefault = DEFAULT_SERIALIZATION_FEATURES.contains(feature);
			Parameter parameter = ContextUtils.getParameter(ContextUtils.getCurrentApplicationContext(), SERIALIZATION_FEATURE + feature.name());
			if ((parameter != null) || hasDefault) {
				if (ContextUtils.getParameterAsBoolean(parameter, feature.enabledByDefault() || hasDefault)) {
					writer = writer.with(feature);
				} else {
					writer = writer.without(feature);
				}
			}
		}
		return writer;
	}
}
