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

package net.ontopia.topicmaps.nav2.taglibs.value;

import java.util.function.Predicate;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.utils.FilterIF;
import net.ontopia.utils.DeciderFilter;

import net.ontopia.topicmaps.nav2.core.ScopeSupportIF;
import net.ontopia.topicmaps.nav2.utils.ScopeUtils;

/**
 * INTERNAL: Abstract Base class for value producing and accepting tags
 * which are taking the context filter into account.
 */
public abstract class BaseScopedTag extends BaseValueProducingAndAcceptingTag
  implements ScopeSupportIF {

  // tag attributes
  protected boolean useUserContextFilter;
  
  /**
   * INTERNAL: Get FilterIF object which provides the possibility to
   * decide if one topic map objects belongs to the wanted scope.
   * Default is to use the <code>IntersectionOfContextDecider</code>.
   *
   * @see net.ontopia.topicmaps.utils.IntersectionOfContextDecider
   * @see net.ontopia.topicmaps.utils.deciders.WithinScopeDecider
   */
  public Predicate<ScopedIF> getScopeDecider(int scopeType) {
    return ScopeUtils.getScopeDecider(pageContext, contextTag, scopeType);
  }

  /**
   * INTERNAL: Get FilterIF object which provides the possibility to
   * filter out topic map objects of a collection which have not the
   * wanted scope.
   */
  public FilterIF<ScopedIF> getScopeFilter(int scopeType) {
    Predicate decider = getScopeDecider(scopeType);
    if (decider == null)
      return null;
    else
      return new DeciderFilter(decider);
  }

  // -----------------------------------------------------------------
  // set methods for the tag attributes
  // -----------------------------------------------------------------

  /**
   * INTERNAL: sets up if the tag should use the context filter which is
   * implict contained in the user session. Default behaviour is to
   * not use the user context filter. Allowed values are:
   * <ul>
   *  <li>off</li>
   *  <li>user</li>
   * </ul>
   */
  public void setContextFilter(String contextFilter) {
    if (contextFilter.indexOf("off") != -1)
      this.useUserContextFilter = false;
    if (contextFilter.indexOf("user") != -1)
      this.useUserContextFilter = true;
  }
  
}
