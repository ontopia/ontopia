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

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.DeclarationContextIF;
import net.ontopia.topicmaps.query.parser.AntlrWrapException;
import net.ontopia.topicmaps.query.parser.ParseContextIF;
import net.ontopia.topicmaps.query.parser.QName;
import net.ontopia.topicmaps.rest.OntopiaRestApplication;
import net.ontopia.topicmaps.rest.core.ParameterResolverIF;
import net.ontopia.topicmaps.rest.exceptions.OntopiaClientException;
import net.ontopia.topicmaps.rest.exceptions.OntopiaRestErrors;
import net.ontopia.topicmaps.rest.exceptions.OntopiaServerException;
import org.restlet.Request;
import org.restlet.data.Status;

public class DefaultParameterResolver implements ParameterResolverIF {

	protected final OntopiaRestApplication application;

	public DefaultParameterResolver(OntopiaRestApplication application) {
		this.application = application;
	}

	@Override
	public <C> C resolve(TopicMapIF topicmap, Request request, String name, Class<C> klass, boolean allowNull) {
		return resolveAttribute(topicmap, request, name, klass, allowNull);
	}

	@SuppressWarnings("unchecked")
	public <C> C resolveAttribute(TopicMapIF topicmap, Request request, String attribute, Class<C> klass, boolean allowNull) {
		Object resolved = null;
		
		Object id = request.getAttributes().get(attribute);
		if (id instanceof String) {
			String stringId = (String) id;
			
			if (String.class.isAssignableFrom(klass)) {
				return (C) stringId;
			} else if (LocatorIF.class.isAssignableFrom(klass)) {
				try {
					resolved = new URILocator(stringId);
				} catch (URISyntaxException me) {
					throw new OntopiaClientException(Status.CLIENT_ERROR_BAD_REQUEST, "Malformed URI: " + stringId, me);
				}
			} else if (TMObjectIF.class.isAssignableFrom(klass)) {
				resolved = resolve(stringId, topicmap, allowNull);
			} else {
				throw new OntopiaServerException("Don't know how to resolve '" + stringId + "' to " + klass.getName());
			}
		}
		
		// filters may pass locators
		if (id instanceof LocatorIF) {
			LocatorIF locator = (LocatorIF) id;
			
			if (LocatorIF.class.isAssignableFrom(klass)) {
				return (C) locator;
			} else if (String.class.isAssignableFrom(klass)) {
				return (C) locator.getAddress();
			} else if (TMObjectIF.class.isAssignableFrom(klass)) {
				resolved = resolveAsLocator(topicmap, locator);
			} else {
				throw new OntopiaServerException("Don't know how to resolve '" + locator + "' to " + klass.getName());
			}
		}
		
		if ((resolved == null) && (!allowNull)) {
			throw OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_NULL.build(attribute, klass.getName());
		}

		if (resolved != null) {
			if (!klass.isAssignableFrom(resolved.getClass())) {
				throw OntopiaRestErrors.MANDATORY_ATTRIBUTE_IS_WRONG_TYPE.build(attribute, klass.getName(), resolved);
			} else {
				return (C) resolved;
			}
		}
		
		return null;
		
	}

	private TMObjectIF resolve(String stringId, TopicMapIF topicmap, boolean allowNull) {
		DeclarationContextIF context = application.getDeclarationContext(topicmap);
		
		// try as object id
		TMObjectIF objectById = resolveAsObjectId(topicmap, stringId);
		
		// decode before going to locators
		try {
			stringId = URLDecoder.decode(stringId, "utf-8");
		} catch (UnsupportedEncodingException uee) {
			throw new RuntimeException("Impossible unsupported utf-8: " + uee.getMessage(), uee);
		}
		
		// try as prefixed locator
		if ((objectById == null) && (context != null)) {
			objectById = resolveAsPrefixedLocator(context, topicmap, stringId);
		}
		
		// try as full locator
		if (objectById == null) {
			objectById = resolveAsLocator(topicmap, stringId);
		}

		return objectById;
	}

	private TMObjectIF resolveAsObjectId(TopicMapIF topicmap, String stringId) {
		return topicmap.getObjectById(stringId);
	}

	private TMObjectIF resolveAsPrefixedLocator(DeclarationContextIF context, TopicMapIF topicmap, String stringId) {
		try {
			ParseContextIF pc = (ParseContextIF) context;
			return pc.getObject(new QName(stringId));
		} catch (AntlrWrapException e) {
			// not a match, ignore
		}
		return null;
	}

	private TMObjectIF resolveAsLocator(TopicMapIF topicmap, String stringId) {
		try {
			LocatorIF locator = new URILocator(stringId);
			return resolveAsLocator(topicmap, locator);
		} catch (URISyntaxException me) {
			// not an uri, ignore
		}
		return null;
	}

	private TMObjectIF resolveAsLocator(TopicMapIF topicmap, LocatorIF locator) {
		TMObjectIF o = topicmap.getTopicBySubjectIdentifier(locator);
		if (o == null) {
			o = topicmap.getTopicBySubjectLocator(locator);
		}
		if (o == null) {
			o = topicmap.getObjectByItemIdentifier(locator);
		}
		return o;
	}
}
