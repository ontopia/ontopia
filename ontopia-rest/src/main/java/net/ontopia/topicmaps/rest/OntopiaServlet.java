/*
 * #!
 * Ontopia Rest
 * #-
 * Copyright (C) 2001 - 2017 The Ontopia Project
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

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.engine.log.LoggerFacade;
import org.restlet.ext.servlet.ServerServlet;
import org.restlet.ext.slf4j.Slf4jLoggerFacade;
import org.restlet.service.LogService;

public class OntopiaServlet extends ServerServlet {
	private static final long serialVersionUID = 201701091214L;
	
	public static final String LOGGER_NAME_ATTRIBUTE = LogService.class.getName() + ".logger";
	public static final String LOGGER_NAME_ATTRIBUTE_DEFAULT_VALUE = OntopiaServlet.class.getPackage().getName() + ".logger";
	
	public static final String LOGGER_FORMAT_ATTRIBUTE = LogService.class.getName() + ".format";
	public static final String LOGGER_FORMAT_ATTRIBUTE_DEFAULT_VALUE = "{p} {m} {rp} {rq} {emt} {S}";

	@Override
	protected Application createApplication(Context parentContext) {
		Application application = super.createApplication(parentContext);
		
		if (application == null) {
			application = new OntopiaRestApplication(parentContext.createChildContext());
		}
		
		return application;
	}

	@Override
	protected Component createComponent() {
		Component component = super.createComponent();
		
		// a bit more configuration by initParams
		LogService log = component.getLogService();
		
		// allow access to the LogService in the application via the context
		component.getContext().getAttributes().put(LogService.class.getName(), log);
		
		// modify the logger name
		String loggerName = getInitParameter(LOGGER_NAME_ATTRIBUTE, LOGGER_NAME_ATTRIBUTE_DEFAULT_VALUE);
		log.setLoggerName(loggerName);
		
		// modify the logger pattern
		// note: overridden to be smaller than the restlet default value
		log.setResponseLogFormat(getInitParameter(LOGGER_FORMAT_ATTRIBUTE, LOGGER_FORMAT_ATTRIBUTE_DEFAULT_VALUE));
		
		return component;
	}

  /**
   * Creates a new logger facade to be set in the Restlet Engine. By default it redirects all
   * logging call to the {@link Slf4jLoggerFacade}.
   *
   * @return A new logger facade to be set in the Restlet Engine.
   */
  @Override
  protected LoggerFacade createLoggerFacade() {
    // fallback to slf4j when not overridden
    return new Slf4jLoggerFacade();
  }
}
