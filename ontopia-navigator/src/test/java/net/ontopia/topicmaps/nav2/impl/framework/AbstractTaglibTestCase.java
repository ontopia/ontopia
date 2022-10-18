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

package net.ontopia.topicmaps.nav2.impl.framework;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * INTERNAL: Abstract class for handling a test case related to the
 * navigator taglib framework.
 */
public abstract class AbstractTaglibTestCase {
  protected String jspfile;
  protected String topicmapId;
  protected Map<String, String[]> reqParams;

  /** Use this character sequence to separate attribute values */
  protected final static String SEPARATOR = "\\|";
  
  /**
   * Default constructor.
   */
  public AbstractTaglibTestCase(String jspfile, 
                                String topicmapId) {
    this.jspfile = jspfile;
    this.topicmapId = topicmapId;
  }

  @Override
  public String toString() {
    return this.getClass().getName() + "  file: " + jspfile +
      " with topicmap: " + topicmapId;
  }

  /**
   * Sets the parameters of the Request in the fake servlet
   * environment from the map containing parameter key-value pairs.
   */
  protected void setRequestParameters(Map<String, String> params) {
    reqParams = new LinkedHashMap<String, String[]>();
    for (String key : params.keySet()) {
      reqParams.put(key, params.get(key).split(SEPARATOR));
    }
  }

  /**
   * Gets the request parameters used by the JSP as input.
   */
  protected Map<String, String[]> getRequestParameters() {
    return reqParams;
  }

  protected String getTopicMapId() {
    return topicmapId;
  }

  protected String getJspFileName() {
    return jspfile;
  }

}
