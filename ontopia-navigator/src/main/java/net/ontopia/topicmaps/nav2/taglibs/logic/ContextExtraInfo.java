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

package net.ontopia.topicmaps.nav2.taglibs.logic;

import jakarta.servlet.jsp.tagext.TagExtraInfo;
import jakarta.servlet.jsp.tagext.TagData;
import jakarta.servlet.jsp.tagext.VariableInfo;

import net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF;

/**
 * INTERNAL: Defines the names and types of variables used by the
 * <code>ContextTag</code>. This makes it possible to access the
 * variables within the JSP.
 *
 * @see net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag
 */
public class ContextExtraInfo extends TagExtraInfo {

  @Override
  public VariableInfo[] getVariableInfo(TagData data) {
    return new VariableInfo[] {
      new VariableInfo(NavigatorApplicationIF.NAV_APP_KEY,
                       "net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF",
                       true, VariableInfo.NESTED),
      new VariableInfo(NavigatorApplicationIF.CONTEXT_KEY,
                       "net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag",
                       true, VariableInfo.NESTED)
    };
  }
  
}
