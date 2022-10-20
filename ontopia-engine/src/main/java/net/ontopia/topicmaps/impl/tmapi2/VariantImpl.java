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

import java.util.ArrayList;
import java.util.Collection;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.VariantNameIF;

import org.tmapi.core.Locator;
import org.tmapi.core.MalformedIRIException;
import org.tmapi.core.Name;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;

/**
 * INTERNAL: OKS->TMAPI 2 object wrapper.
 */

public class VariantImpl extends DatatypeAwareImpl implements Variant {

  private VariantNameIF wrapped;

  final private NameImpl parent;
  /**
   * this set contains all themes from the parent, which were in the scope list
   * of the variant
   */
  private Collection<Topic> explicitScope = new ArrayList<Topic>();

  public VariantImpl(TopicMapImpl topicMap, NameImpl parent,
      VariantNameIF variant) {
    super(topicMap);
    wrapped = variant;
    this.parent = parent;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.ontopia.topicmaps.impl.tmapi2.DatatypeAware#setValue(java.lang.String ,
   * net.ontopia.infoset.core.LocatorIF)
   */

  @Override
  protected void setValue(String value, LocatorIF datatype) {
    wrapped.setValue(value, datatype);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.topicmaps.impl.tmapi2.Construct#getWrapped()
   */

  @Override
  public VariantNameIF getWrapped() {
    return wrapped;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Variant#getParent()
   */

  @Override
  public Name getParent() {
    return parent;
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
    } else {
      try {
        return getTopicMap().createLocator(getValue());
      } catch (MalformedIRIException ex) {
        throw new IllegalArgumentException(
            "The value cannot be represented as Locator");
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

  @Override
  public void removeTheme(Topic theme) {
    explicitScope.remove(theme);
    if (!getParent().getScope().contains(theme)) {
      super.removeTheme(theme);
    }
  }

  @Override
  public void addTheme(Topic theme) {
    explicitScope.add(theme);
    super.addTheme(theme);
  }

  public void setExplicitScope(Collection<Topic> explicitScope) {
    this.explicitScope = explicitScope;
  }

  public Collection<Topic> getExplicitScope() {
    return explicitScope;
  }

  @Override
  public void remove() {
    super.remove();
    parent.removeVariant(this);
  }

}
