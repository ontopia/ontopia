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

package net.ontopia.topicmaps.webed.impl.basic;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.FileValueIF;
import net.ontopia.topicmaps.webed.core.WebEdRequestIF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Default implementation of ActionParametersIF.
 */
public class ActionParameters implements ActionParametersIF {
  private List params;
  private String[] fieldvalues;
  private TopicMapIF topicmap;
  private WebEdRequestIF request;
  private FileValueIF filevalue;
  
  static Logger logger = LoggerFactory.getLogger(ActionParameters.class.getName());

  public ActionParameters(String fieldname, String[] fieldvalues,
                          FileValueIF filevalue,
                          List params, TopicMapIF topicmap, WebEdRequestIF request) {

    this.fieldvalues = fieldvalues;
    this.filevalue = filevalue;
    this.params = params;
    this.topicmap = topicmap;
    this.request = request;

    logger.debug("Field " + fieldname + " had params " + params);
  }

  private ActionParameters(ActionParameters old, List newparams) {
    this.fieldvalues = old.fieldvalues;
    this.filevalue = old.filevalue;
    this.params = newparams;
    this.topicmap = old.topicmap;
    this.request = old.request;
  }

  // ------------------------------------------------------------
  // implementation of ActionParametersIF
  // ------------------------------------------------------------
  
  @Override
  public Object get(int ix) {
    Collection values = getCollection(ix);
    if (values == null || values.isEmpty())
      return null;

    return values.iterator().next();
  }

  @Override
  public Collection getCollection(int ix) {
    if (ix >= params.size())
      return null;
    else
      return (Collection) params.get(ix);
  }

  @Override
  public int getParameterCount() {
    return params.size();
  }

  @Override
  public String getStringValue() {
    if (fieldvalues == null)
      return null;
    return fieldvalues[0];
  }

  @Override
  public String[] getStringValues() {
    return fieldvalues;
  }

  @Override
  public boolean getBooleanValue() {
    return fieldvalues != null && fieldvalues[0].equals("on");  
  }
  
  @Override
  public TMObjectIF getTMObjectValue() {
    if (fieldvalues == null || fieldvalues[0] == null)
      return null;
    
    return topicmap.getObjectById(fieldvalues[0]);
  }

  @Override
  public Collection getTMObjectValues() {
    if (fieldvalues == null)
      return Collections.EMPTY_SET;
    
    Set objects = new HashSet();
    for (int ix = 0; ix < fieldvalues.length; ix++) {
      TMObjectIF object = topicmap.getObjectById(fieldvalues[ix]);
      if (object != null)
        objects.add(object);
    }
    return objects;
  }

  @Override
  public FileValueIF getFileValue() {
    return filevalue;
  }

  @Override
  public WebEdRequestIF getRequest() {
    return request;
  }

  @Override
  public ActionParametersIF cloneAndOverride(List newparams) {
    return new ActionParameters(this, newparams);
  }
  
}
