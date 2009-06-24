// $Id:$

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

import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;

/**
 * INTERNAL: OKS->TMAPI 2 object wrapper.
 */

public class NameImpl extends ScopedImpl implements org.tmapi.core.Name {

  private TopicNameIF wrapped;

  private Set<Variant> wrappedVariants = Collections.emptySet();

  public NameImpl(TopicMapImpl topicMap, TopicNameIF name) {
    super(topicMap);
    wrapped = name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.topicmaps.impl.tmapi2.Construct#getWrapped()
   */

  public TopicNameIF getWrapped() {
    return wrapped;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Name#createVariant(java.lang.String,
   * org.tmapi.core.Topic[])
   */

  public Variant createVariant(String value, org.tmapi.core.Topic... scope) {
    Check.scopeNotNull(this, scope);
    return createVariant(value, Arrays.asList(scope));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Name#createVariant(java.lang.String,
   * java.util.Collection)
   */

  public Variant createVariant(String value,
      Collection<org.tmapi.core.Topic> scope) {
    Check.valueNotNull(this, value);
    Check.scopeNotNull(this, scope);

    checkScope(scope);
    Collection<Topic> explScope = new ArrayList<Topic>(scope);

    VariantNameIF variant = topicMap.getWrapped().getBuilder().makeVariantName(
        wrapped, value, unwrapScope(scope));

    VariantImpl v = topicMap.wrapVariant(variant);
    v.setExplicitScope(explScope);
    addVariant(v);
    return v;
  }

  private void checkScope(Collection<org.tmapi.core.Topic> scope) {
    if (scope.isEmpty())
      throw new ModelConstraintException(this, "No scope for variant given!");

    if (getScope().containsAll(scope))
      throw new ModelConstraintException(this,
          "The variant has the same scope as the name!");

  }

  private void addVariant(Variant variant) {
    if (wrappedVariants == Collections.EMPTY_SET) {
      wrappedVariants = new HashSet<Variant>();
    }
    wrappedVariants.add(variant);
  }
  
 

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Name#createVariant(org.tmapi.core.Locator,
   * org.tmapi.core.Topic[])
   */

  public Variant createVariant(org.tmapi.core.Locator value,
      org.tmapi.core.Topic... scope) {
    Check.scopeNotNull(this, scope);
    return createVariant(value, Arrays.asList(scope));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Name#createVariant(org.tmapi.core.Locator,
   * java.util.Collection)
   */

  public Variant createVariant(org.tmapi.core.Locator value,
      Collection<org.tmapi.core.Topic> scope) {
    Check.valueNotNull(this, value);
    Check.scopeNotNull(this, scope);

    Collection<Topic> explicitScope = new ArrayList<Topic>(scope);

    VariantNameIF variant = topicMap.getWrapped().getBuilder().makeVariantName(
        wrapped, topicMap.unwrapLocator(value), unwrapScope(scope));
    VariantImpl v = topicMap.wrapVariant(variant);
    v.setExplicitScope(explicitScope);

    addVariant(v);
    return v;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Name#createVariant(java.lang.String,
   * org.tmapi.core.Locator, org.tmapi.core.Topic[])
   */

  public Variant createVariant(String value, org.tmapi.core.Locator datatype,
      org.tmapi.core.Topic... scope) {
    Check.scopeNotNull(this, scope);
    return createVariant(value, datatype, Arrays.asList(scope));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Name#createVariant(java.lang.String,
   * org.tmapi.core.Locator, java.util.Collection)
   */

  public Variant createVariant(String value, org.tmapi.core.Locator datatype,
      Collection<org.tmapi.core.Topic> scope) {
    Check.valueNotNull(this, value, datatype);
    Check.scopeNotNull(this, scope);
    VariantNameIF variant = topicMap.getWrapped().getBuilder().makeVariantName(
        wrapped, value, topicMap.unwrapLocator(datatype), unwrapScope(scope));
    return topicMap.wrapVariant(variant);
  }

  private Collection<TopicIF> unwrapScope(Collection<org.tmapi.core.Topic> scope) {
    Collection<TopicIF> result = new ArrayList<TopicIF>(scope.size());
    for (org.tmapi.core.Topic theme : scope) {
      result.add(topicMap.unwrapTopic(theme));
    }
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Name#getParent()
   */

  public Topic getParent() {
    return topicMap.wrapTopic(wrapped.getTopic());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Name#getValue()
   */

  public String getValue() {
    return wrapped.getValue();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Name#getVariants()
   */

  public Set<Variant> getVariants() {
    return wrappedVariants;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Name#setValue(java.lang.String)
   */

  public void setValue(String value) {
    Check.valueNotNull(this, value);
    wrapped.setValue(value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Typed#getType()
   */

  public Topic getType() {
    return topicMap.wrapTopic(wrapped.getType());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Typed#setType(org.tmapi.core.Topic)
   */

  public void setType(org.tmapi.core.Topic type) {
    Check.typeNotNull(this, type);
    wrapped.setType(topicMap.unwrapTopic(type));
  }

  @Override
  public void removeTheme(Topic theme) {
    super.removeTheme(theme);

    for (Variant v : wrappedVariants) {

      if (((VariantImpl) v).getExplicitScope().contains(theme))
        v.addTheme(theme);
    }

  }

  void removeVariant(VariantImpl variantImpl) {
    wrappedVariants.remove(variantImpl);
  }
}
