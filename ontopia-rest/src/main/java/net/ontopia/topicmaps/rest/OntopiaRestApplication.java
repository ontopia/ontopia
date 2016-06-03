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

import java.util.HashMap;
import java.util.Map;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.query.core.DeclarationContextIF;
import net.ontopia.topicmaps.rest.controller.AbstractController;
import net.ontopia.topicmaps.rest.core.ParameterResolverIF;
import net.ontopia.topicmaps.rest.core.TopicMapResolverIF;
import net.ontopia.topicmaps.rest.exceptions.OntopiaServerException;
import net.ontopia.topicmaps.rest.resources.APIInfoResource;
import net.ontopia.topicmaps.rest.utils.DefaultParameterResolver;
import net.ontopia.topicmaps.rest.utils.DefaultTopicMapResolver;
import org.restlet.Application;
import org.restlet.Request;
import org.restlet.Restlet;
import org.restlet.routing.Router;
import org.restlet.routing.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OntopiaRestApplication extends Application {
	private static final Logger logger = LoggerFactory.getLogger(OntopiaRestApplication.class);
	
	protected final ParameterResolverIF objectResolver;
	protected final TopicMapResolverIF topicmapResolver;
	
	protected final Map<Class<? extends AbstractController>, AbstractController> controllers = new HashMap<>();
	
	public OntopiaRestApplication() {
		objectResolver = new DefaultParameterResolver(this);
		topicmapResolver = new DefaultTopicMapResolver();
		
		setStatusService(new OntopiaStatusService());
	}
	
	public TopicMapReferenceIF getTopicMapReference(Request request) {
		return topicmapResolver.resolve(request);
	}
	
	public ParameterResolverIF getResolver() {
		return objectResolver;
	}

	@Override
	public Restlet createInboundRoot() {
		// encoding service that allows disabling
		setEncoderService(new OntopiaEncoderService());
		
		Router versions = new Router(getContext());
		versions.setDefaultMatchingMode(Template.MODE_STARTS_WITH);
		versions.setRoutingMode(Router.MODE_BEST_MATCH);
		versions.setName("Ontopia API root router");
		
		versions.attach("/", APIInfoResource.class);
		
		for (APIVersions version : APIVersions.values()) {
			if (isEnabled(version)) {
				logger.info("Exposing API {}", version.getName());
				versions.attach("/" + version.getName(), new OntopiaAPIVersionFilter(getContext(), version.createChain(this), version));
			}
		}

		return versions;
	}

	public DeclarationContextIF getDeclarationContext(TopicMapIF topicmap) {
		return null; // todo
	}

	protected boolean isEnabled(APIVersions version) {
		return true;
	}

	@Override
	public synchronized void stop() throws Exception {
		topicmapResolver.close();
		super.stop();
	}
	
	// docs: provided extension point for controllers
	@SuppressWarnings("unchecked")
	public synchronized <C extends AbstractController> C getController(Class<C> controllerClass) {
		C controller = (C) controllers.get(controllerClass);
		if (controller == null) {
			try {
				controller = controllerClass.newInstance();
			} catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException e) {
				throw new OntopiaServerException(e);
			}
			controllers.put(controllerClass, controller);
			controller.setOntopia(this);
		}
		return controller;
	}
}
