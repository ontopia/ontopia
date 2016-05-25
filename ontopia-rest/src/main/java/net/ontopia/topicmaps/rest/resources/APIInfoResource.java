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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.ontopia.topicmaps.rest.utils.ClassUtils;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.ext.velocity.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Finder;
import org.restlet.resource.Get;
import org.restlet.routing.Filter;
import org.restlet.routing.Route;
import org.restlet.routing.Router;
import org.restlet.routing.TemplateRoute;
import org.restlet.util.RouteList;

public class APIInfoResource extends AbstractOntopiaResource {
	
	@Get
	public Representation getAPIInfo() throws IOException {
		TemplateRepresentation r = new TemplateRepresentation("net/ontopia/topicmaps/rest/resources/info.html", MediaType.TEXT_HTML);
		r.getEngine().addProperty(VelocityEngine.RESOURCE_LOADER, "classpath");
		r.getEngine().addProperty("classpath." + VelocityEngine.RESOURCE_LOADER + ".class", ClasspathResourceLoader.class.getName());
		
		Map<Restlet, String> allRoutes = new HashMap<>();
		list(allRoutes, getApplication().getInboundRoot(), "");
		Map<String, Object> data = new HashMap<>();
		data.put("util", this);
		data.put("root", getApplication().getInboundRoot());
		data.put("routes", allRoutes);
		data.put("cutil", ClassUtils.class);
		
		r.setDataModel(data);

		return r;
	}

	private void describe(StringBuilder b, Restlet restlet, String path) {
		if (restlet instanceof Router) {
			describeRoutes(b, (Router) restlet, path);
		} else if (restlet instanceof Finder) {
			Finder f = (Finder) restlet;
			b.append(path).append(" = ").append(ClassUtils.collapsedName(f.getTargetClass())).append("\n");
		} else if (restlet instanceof Filter) {
			describe(b, ((Filter)restlet).getNext(), path);
		}
	}

	private void describeRoutes(StringBuilder b, Router router, String path) {
		RouteList routes = router.getRoutes();
		
		b.append("[").append(path).append("] = Router: ").append(router.getName()).append(": ").append(router.getDescription()).append("\n");
		
		for (Route r : routes) {
			if (r instanceof TemplateRoute) {
				describe(b, r.getNext(), path + ((TemplateRoute)r).getTemplate().getPattern());
			}
			
		}
		
	}
	
	private void list(Map<Restlet, String> all, Restlet restlet, String path) {
		all.put(restlet, path);
		if (restlet instanceof Router) {
			for (Route r : ((Router)restlet).getRoutes()) {
				list(all, r, path + ((TemplateRoute)r).getTemplate().getPattern());
			}
		} else if (restlet instanceof Filter) {
			list(all, ((Filter) restlet).getNext(), path);
		}
	}


	
	public boolean isRouter(Restlet restlet) {
		return restlet instanceof Router;
	}
	public boolean isFilter(Restlet restlet) {
		return restlet instanceof Filter;
	}
	public boolean isFinder(Restlet restlet) {
		return restlet instanceof Finder;
	}
}
