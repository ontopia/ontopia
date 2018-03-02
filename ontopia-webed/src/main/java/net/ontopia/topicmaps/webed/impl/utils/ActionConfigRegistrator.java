/*
 * #!
 * Ontopia Webed
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
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

package net.ontopia.topicmaps.webed.impl.utils;

import javax.servlet.ServletContext;
import net.ontopia.topicmaps.webed.impl.basic.ConfigurationObserverIF;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Keeps track with registering the action registry object
 * in the web application scope.
 */
public class ActionConfigRegistrator implements ConfigurationObserverIF {

  // initialization of log facility
  private static Logger log = LoggerFactory
    .getLogger(ActionConfigRegistrator.class.getName());
  
  protected ServletContext ctxt;
  
  public ActionConfigRegistrator(ServletContext ctxt) {
    this.ctxt = ctxt;
  }

  @Override
  public void configurationChanged(Object configuration) {
    // remove registry from servlet context
    ctxt.removeAttribute(Constants.AA_REGISTRY);
    // set new registry to servlet context
    log.info("setting action registry in application scope.");
    ctxt.setAttribute(Constants.AA_REGISTRY, configuration);
  }
  
}
