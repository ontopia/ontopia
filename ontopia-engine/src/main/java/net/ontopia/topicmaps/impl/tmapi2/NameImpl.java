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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;

import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Name;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;

/**
 * INTERNAL: OKS->TMAPI 2 object wrapper.
 */

public class NameImpl extends ScopedImpl implements Name {

  private TopicNameIF wrapped;

  // The scope of variants is handled different in TMAPI2. Therefore
  // the wrappers of variants have a state, containing explicitly set
  // themes from the name.  These wrappers need to be stored in the
  // name wrapper.
  private Set<Variant> wrappedVariants = null;

  public NameImpl(TopicMapImpl topicMap, TopicNameIF name) {
    super(topicMap);
    wrapped = name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.topicmaps.impl.tmapi2.Construct#getWrapped()
   */

  @Override
  public TopicNameIF getWrapped() {
    return wrapped;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Name#createVariant(java.lang.String,
   * org.tmapi.core.Topic[])
   */

  @Override
  public Variant createVariant(String value, Topic... scope) {
    Check.scopeNotNull(this, scope);
    Check.scopeNotEmpty(this, scope);
    return createVariant(value, Arrays.asList(scope));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Name#createVariant(java.lang.String,
   * java.util.Collection)
   */

  @Override
  public Variant createVariant(String value, Collection<Topic> scope) {
    Check.valueNotNull(this, value);
    Check.scopeNotNull(this, scope);
    Check.scopeNotEmpty(this, scope);
    
    checkScope(scope);
    Collection<Topic> explScope = new ArrayList<Topic>(scope);

    VariantNameIF variant = topicMap.getWrapped().getBuilder().makeVariantName(
        wrapped, value, unwrapScope(scope));

    VariantImpl v = topicMap.wrapVariant(variant);
    v.setExplicitScope(explScope);

    return v;
  }

  private void checkScope(Collection<org.tmapi.core.Topic> scope) {
    if (getScope().containsAll(scope)) {
      throw new ModelConstraintException(this,
          "The variant has the same scope as the name!");
    }

  }

  protected void addVariant(Variant variant) {
    if (wrappedVariants == null) {
      wrappedVariants = new HashSet<Variant>();
    }
    wrappedVariants.add(variant);
  }
  
  protected void clearVariants() {
    if (wrappedVariants != null) {
        wrappedVariants.clear();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Name#createVariant(org.tmapi.core.Locator,
   * org.tmapi.core.Topic[])
   */

  @Override
  public Variant createVariant(Locator value, Topic... scope) {
    Check.scopeNotNull(this, scope);
    return createVariant(value, Arrays.asList(scope));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Name#createVariant(org.tmapi.core.Locator,
   * java.util.Collection)
   */

  @Override
  public Variant createVariant(Locator value, Collection<Topic> scope) {
    Check.valueNotNull(this, value);
    Check.scopeNotNull(this, scope);
    Check.scopeNotEmpty(this, scope);
    
    Collection<Topic> explicitScope = new ArrayList<Topic>(scope);

    VariantNameIF variant = topicMap.getWrapped().getBuilder().makeVariantName(
        wrapped, topicMap.unwrapLocator(value), unwrapScope(scope));
    VariantImpl v = topicMap.wrapVariant(variant);
    v.setExplicitScope(explicitScope);

    return v;
  }

  /*
   * (non-Javadoc)
   * 
   * @see Name#createVariant(java.lang.String, org.tmapi.core.Locator,
   * org.tmapi.core.Topic[])
   */

  @Override
  public Variant createVariant(String value, Locator datatype, Topic... scope) {
    Check.scopeNotNull(this, scope);
    Check.scopeNotEmpty(this, scope);
    return createVariant(value, datatype, Arrays.asList(scope));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Name#createVariant(java.lang.String,
   * org.tmapi.core.Locator, java.util.Collection)
   */

  @Override
  public Variant createVariant(String value, Locator datatype,
      Collection<Topic> scope) {
    Check.valueNotNull(this, value, datatype);
    Check.scopeNotNull(this, scope);
    Check.scopeNotEmpty(this, scope);
    Check.scopeInTopicMap(getTopicMap(), scope.toArray(new Topic[scope.size()]));
    VariantNameIF variant = topicMap.getWrapped().getBuilder().makeVariantName(
        wrapped, value, topicMap.unwrapLocator(datatype), unwrapScope(scope));
    return topicMap.wrapVariant(variant);
  }

  private Collection<TopicIF> unwrapScope(Collection<Topic> scope) {
    Collection<TopicIF> result = new ArrayList<TopicIF>(scope.size());
    for (Topic theme : scope) {
      result.add(topicMap.unwrapTopic(theme));
    }
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Name#getParent()
   */

  @Override
  public Topic getParent() {
    return topicMap.wrapTopic(wrapped.getTopic());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Name#getValue()
   */

  @Override
  public String getValue() {
    return wrapped.getValue();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Name#getVariants()
   */

  @Override
  public Set<Variant> getVariants() {
    return wrappedVariants != null ? wrappedVariants : Collections.<Variant>emptySet();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Name#setValue(java.lang.String)
   */

  @Override
  public void setValue(String value) {
    Check.valueNotNull(this, value);
    wrapped.setValue(value);
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

  @Override
  public void removeTheme(Topic theme) {
    super.removeTheme(theme);

    // when a theme is removed from a name it also is removed from
    // all variants. However TMAPI defines, if a scope of a name is explicitly
    // added to the variant, it may not be removed when the theme is removed 
    // from the name. So the following line will add the theme to
    // the variant again.
    
    for (Variant v : getVariants()) {
      if (((VariantImpl) v).getExplicitScope().contains(theme)) {
        v.addTheme(theme);
      }
    }

  }

  protected void removeVariant(VariantImpl variantImpl) {
    wrappedVariants.remove(variantImpl);
  }
}
