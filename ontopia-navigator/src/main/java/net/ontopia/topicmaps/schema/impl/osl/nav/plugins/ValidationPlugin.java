/*
 * #!
 * Ontopia Navigator
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

package net.ontopia.topicmaps.schema.impl.osl.nav.plugins;

import java.io.File;
import javax.servlet.ServletContext;

import net.ontopia.topicmaps.nav2.plugins.DefaultPlugin;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;

public class ValidationPlugin extends DefaultPlugin {

  @Override
  public String generateHTML(ContextTag context) {

    ServletContext ctxt = context.getPageContext().getServletContext();
    String tm = context.getTopicMapId();

    // Does the schme exist?
    String path = ctxt.getRealPath("/WEB-INF/schemas/" + tm + ".osl");
    if (!new File(path).exists()) {
      return "<span title=\"No OSL schema found at: " + path + "\">No schema</span>";
    } else {
      return super.generateHTML(context);
    }
  }
  
}
