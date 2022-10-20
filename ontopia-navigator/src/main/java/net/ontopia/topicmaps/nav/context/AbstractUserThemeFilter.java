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

package net.ontopia.topicmaps.nav.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;

/**
 * INTERNAL: Abstract class which provides access to filtering themes
 * out which are not relevant to the user context.
 */
public abstract class AbstractUserThemeFilter implements UserThemeFilterIF {

  protected TopicMapIF topicMap;

  public AbstractUserThemeFilter() {
    this.topicMap = null;
  }

  public AbstractUserThemeFilter(TopicMapIF topicMap) {
    this.topicMap = topicMap;
  }

  @Override
  public TopicMapIF getTopicMap() {
    return this.topicMap;
  }

  @Override
  public void setTopicMap(TopicMapIF topicMap) {
    this.topicMap = topicMap;
  }

  //
  // helper methods
  //
  
  /**
   * Use method <code>shouldNotBeUsed</code> for every theme
   * of the collection of <code>unfilteredThemes</code> and
   * allows therefore to retrieve a filtered collection of themes.
   */
  public Collection filterThemes(Collection unfilteredThemes) {
    Collection themes = new ArrayList();
    TopicIF actTheme = null;
    Iterator it = unfilteredThemes.iterator();
    while (it.hasNext()) {
      actTheme = (TopicIF) it.next();
      if (!shouldNotBeUsed(actTheme)) {
        themes.add( actTheme );
      } 
    }

    return themes;
  }
  
  
}





