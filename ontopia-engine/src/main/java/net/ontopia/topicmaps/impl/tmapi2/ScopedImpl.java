/*
 * #!
 * Ontopia Engine
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

package net.ontopia.topicmaps.impl.tmapi2;

import java.util.Set;

import net.ontopia.topicmaps.core.ScopedIF;

import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;

/**
 * INTERNAL: OKS->TMAPI 2 object wrapper.
 */

public abstract class ScopedImpl extends ReifiableImpl implements Scoped {

  public ScopedImpl(TopicMapImpl topicMap) {
    super(topicMap);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Scoped#getScope()
   */
  
  @Override
  public Set<Topic> getScope() {
    return topicMap.wrapSet(((ScopedIF) getWrapped()).getScope());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Scoped#addTheme(org.tmapi.core.Topic)
   */
  
  @Override
  public void addTheme(Topic theme) {
    Check.themeNotNull(this, theme);
    Check.scopeInTopicMap(getTopicMap(), theme);
    ((ScopedIF) getWrapped()).addTheme(topicMap.unwrapTopic(theme));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Scoped#removeTheme(org.tmapi.core.Topic)
   */
  
  @Override
  public void removeTheme(Topic theme) {
    Check.themeNotNull(this, theme);
    ((ScopedIF) getWrapped()).removeTheme(topicMap.unwrapTopic(theme));
  }

}
