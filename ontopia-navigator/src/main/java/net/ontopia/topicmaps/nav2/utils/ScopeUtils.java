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

package net.ontopia.topicmaps.nav2.utils;

import java.util.Collection;

import javax.servlet.jsp.PageContext;

import net.ontopia.utils.DeciderIF;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.ApplicableInContextDecider;
import net.ontopia.topicmaps.utils.IntersectionOfContextDecider;
import net.ontopia.topicmaps.utils.SupersetOfContextDecider;
import net.ontopia.topicmaps.utils.SubsetOfContextDecider;
import net.ontopia.topicmaps.utils.deciders.WithinScopeDecider;

import net.ontopia.topicmaps.nav2.core.UserIF;
import net.ontopia.topicmaps.nav2.core.NavigatorConfigurationIF;
import net.ontopia.topicmaps.nav2.core.ScopeSupportIF;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav.context.UserFilterContextStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Framework related utility class providing some helper
 * methods needed to easier for access to scope information.
 */
public final class ScopeUtils implements ScopeSupportIF {

  // initialization of logging facility
  private static final Logger log = LoggerFactory
    .getLogger(FrameworkUtils.class.getName());
  
  /**
   * INTERNAL: Get FilterIF object which provides the possibility to
   * decide if one topic map objects belongs to the wanted scope.
   * Default is to use the <code>IntersectionOfContextDecider</code>.
   *
   * @see net.ontopia.topicmaps.utils.IntersectionOfContextDecider
   * @see net.ontopia.topicmaps.utils.deciders.WithinScopeDecider
   */
  public static DeciderIF getScopeDecider(PageContext pageContext,
                                          ContextTag contextTag,
                                          int scopeType) {
    UserIF user = FrameworkUtils.getUser(pageContext); 
    UserFilterContextStore filterContext = user.getFilterContext();
    TopicMapIF tm = contextTag.getTopicMap();

    // get context in accordance to scopeType
    Collection context = null;
    String decPropName = null;
    switch (scopeType) {
    case SCOPE_BASENAMES:
      context = filterContext.getScopeTopicNames(tm);
      decPropName = NavigatorConfigurationIF.BASENAME_CONTEXT_DECIDER;
      break;
    case SCOPE_VARIANTS:
      context = filterContext.getScopeVariantNames(tm);
      decPropName = NavigatorConfigurationIF.VARIANT_CONTEXT_DECIDER;
      break;
    case SCOPE_OCCURRENCES:
      context = filterContext.getScopeOccurrences(tm);
      decPropName = NavigatorConfigurationIF.OCC_CONTEXT_DECIDER;
      break;
    case SCOPE_ASSOCIATIONS:
      context = filterContext.getScopeAssociations(tm);
      decPropName = NavigatorConfigurationIF.ASSOC_CONTEXT_DECIDER;
      break;
    }
    
    // construct decider in accordance to property setting
    String decSetting = contextTag.getNavigatorConfiguration()
      .getProperty(decPropName, DEC_INTERSECTION);
    DeciderIF scopeDecider = null;
    if (context != null && !context.isEmpty()) {
      if (decSetting.equalsIgnoreCase(DEC_INTERSECTION))
        scopeDecider = new IntersectionOfContextDecider(context);
      else if (decSetting.equalsIgnoreCase(DEC_APPLICABLE_IN))
        scopeDecider = new ApplicableInContextDecider(context);
      else if (decSetting.equalsIgnoreCase(DEC_WITHIN))
        scopeDecider = new WithinScopeDecider(context);
      else if (decSetting.equalsIgnoreCase(DEC_SUBSET))
        scopeDecider = new SubsetOfContextDecider(context);
      else if (decSetting.equalsIgnoreCase(DEC_SUPERSET))
        scopeDecider = new SupersetOfContextDecider(context);
    }

    return scopeDecider;
  }

}





