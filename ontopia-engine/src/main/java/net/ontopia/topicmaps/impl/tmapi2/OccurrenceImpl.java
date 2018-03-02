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

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.OccurrenceIF;

import org.tmapi.core.Locator;
import org.tmapi.core.Occurrence;
import org.tmapi.core.MalformedIRIException;
import org.tmapi.core.Topic;

/**
 * INTERNAL: OKS->TMAPI 2 object wrapper.
 */

public class OccurrenceImpl extends DatatypeAwareImpl implements Occurrence {

  private OccurrenceIF wrapped;

  public OccurrenceImpl(TopicMapImpl topicMap, OccurrenceIF occ) {
    super(topicMap);
    wrapped = occ;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.topicmaps.impl.tmapi2.Construct#getWrapped()
   */
  
  @Override
  public OccurrenceIF getWrapped() {
    return wrapped;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Occurrence#getParent()
   */
  
  @Override
  public Topic getParent() {
    return topicMap.wrapTopic(wrapped.getTopic());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Typed#getType()
   */
  
  @Override
  public Topic getType() {
    return topicMap.wrapTopic(wrapped.getType());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Typed#setType(org.tmapi.core.Topic)
   */
  
  @Override
  public void setType(Topic type) {
    Check.typeNotNull(this, type);
    Check.typeInTopicMap(getTopicMap(), type);
    wrapped.setType(topicMap.unwrapTopic(type));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.DatatypeAware#getDatatype()
   */
  
  @Override
  public Locator getDatatype() {
    return topicMap.wrapLocator(wrapped.getDataType());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.DatatypeAware#getValue()
   */
  
  @Override
  public String getValue() {
    return wrapped.getValue();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.DatatypeAware#locatorValue()
   */
  
  @Override
  public Locator locatorValue() {
    Locator loc = topicMap.wrapLocator(wrapped.getLocator());
    if (loc != null) {
      return loc;
    }
    else {
      try {
        return getTopicMap().createLocator(getValue());
      }
      catch (MalformedIRIException ex) {
        // according to Lars Marius not a goot choice, but specified in TMAPI 2.0
        throw new IllegalArgumentException("The value cannot be represented as Locator");
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.DatatypeAware#setValue(java.lang.String)
   */
  
  @Override
  public void setValue(String value) {
    Check.valueNotNull(this, value);
    wrapped.setValue(value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.DatatypeAware#setValue(org.tmapi.core.Locator)
   */
  
  @Override
  public void setValue(Locator value) {
    Check.valueNotNull(this, value);
    wrapped.setLocator(topicMap.unwrapLocator(value));
  }

  /* (non-Javadoc)
   * @see net.ontopia.topicmaps.impl.tmapi2.DatatypeAware#setValue(java.lang.String, net.ontopia.infoset.core.LocatorIF)
   */
  
  @Override
  protected void setValue(String value, LocatorIF datatype) {
    wrapped.setValue(value, datatype);
  }

}
