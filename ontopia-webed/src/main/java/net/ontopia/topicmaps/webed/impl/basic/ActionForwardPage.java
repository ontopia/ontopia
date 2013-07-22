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

import java.util.HashMap;
import java.util.Map;

/**
 * INTERNAL: Default implementation of the interface ActionForwardPageIF
 */
public class ActionForwardPage implements ActionForwardPageIF {

  protected static final ParamRuleIF DEF_PARAM_RULE = new IdentityParamRule();
  
  protected String url;
  protected String framename;
  protected String nextActionTemplate;
  protected ParamRuleIF paramRule;
  protected Map reqParams;

  public ActionForwardPage(String relativeURL) {
    this(relativeURL, "", "", null);
  }
  
  public ActionForwardPage(String aUrlString, Map parameters) {
    this.url = aUrlString;
    this.paramRule = DEF_PARAM_RULE;
    this.reqParams = parameters;
  }
  
  public ActionForwardPage(String relativeURL, ParamRuleIF paramRule) {
    this(relativeURL, "", "", paramRule);
  }
  
  public ActionForwardPage(String relativeURL, String framename) {
    this(relativeURL, framename, "", null);
  }
  
  public ActionForwardPage(String relativeURL, String framename,
                           String nextActionTemplate) {
    this(relativeURL, framename, nextActionTemplate, null);
  }
  
  public ActionForwardPage(String aUrlString, String framename,
                           String nextActionTemplate, ParamRuleIF paramRule) {
    this.url = aUrlString;
    this.framename = framename;
    if (nextActionTemplate == null)
      this.nextActionTemplate = "";
    else
      this.nextActionTemplate = nextActionTemplate;
    if (paramRule == null)
      this.paramRule = DEF_PARAM_RULE;
    else
      this.paramRule = paramRule;
    this.reqParams = new HashMap();
  }
  
  // ------------------------------------------------------------
  // implementation of ActionForwardPageIF
  // ------------------------------------------------------------
  
  public String getURL() {
    return url;
  }

  public String getFramename() {
    return framename;
  }

  public void addParameter(String paramName, String paramValue) {
    reqParams.put(paramName, paramValue);
  }

  public Map getParameters() {
    return reqParams;
  }
  
  public String getNextActionTemplate() {
    return nextActionTemplate;
  }
  
  public ParamRuleIF getNextActionParamRule() {
    return paramRule;
  }

  // --- overwrite method(s) from Object implementation
  
  public String toString() {
    return "[ActionForwardPage: URL=" + url +
      ", framename=" + framename +
      ", nextActionTemplate=" + nextActionTemplate +
      ", paramRule=" + paramRule +
      ", reqParams=" + reqParams + "]";
  }

  public boolean equals(Object comp) {
    if (!(comp instanceof ActionForwardPageIF))
      return false;
    ActionForwardPageIF compPage = (ActionForwardPageIF) comp;
    return (url.equals(compPage.getURL())
            && framename.equals(compPage.getFramename())
            && nextActionTemplate.equals(compPage.getNextActionTemplate())
            && paramRule.getClass().getName().equals(compPage.getNextActionParamRule().getClass().getName())
            && reqParams.equals(compPage.getParameters()));
  }
  
}
