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

import net.ontopia.topicmaps.core.ReifiableIF;

import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Topic;

/**
 * INTERNAL: OKS->TMAPI 2 object wrapper.
 */

public abstract class ReifiableImpl extends ConstructImpl implements
    Reifiable {

  public ReifiableImpl(TopicMapImpl topicMap) {
    super(topicMap);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Reifiable#getReifier()
   */
  
  @Override
  public TopicImpl getReifier() {
    return topicMap.wrapTopic(((ReifiableIF) getWrapped()).getReifier());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Reifiable#setReifier(org.tmapi.core.Topic)
   */
  
  @Override
  public void setReifier(Topic reifier)
      throws ModelConstraintException {
    if (reifier!=null) {
      Check.reifierInTopicMap(getTopicMap(), reifier);
    }
    if (reifier != null && reifier.getReified() != null && !reifier.getReified().equals(this)) {
      throw new ModelConstraintException(this, "The reifier reifies another construct");
    }
    
    ((ReifiableIF) getWrapped()).setReifier(topicMap.unwrapTopic(reifier));
  }

}
