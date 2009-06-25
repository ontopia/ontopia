// $Id:$

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

public class VariantImpl extends DatatypeAwareImpl implements
    Variant {

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

  protected void setValue(String value, LocatorIF datatype) {
    wrapped.setValue(value, datatype);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.topicmaps.impl.tmapi2.Construct#getWrapped()
   */

  public VariantNameIF getWrapped() {
    return wrapped;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.Variant#getParent()
   */

  public Name getParent() {
    return parent;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.DatatypeAware#getDatatype()
   */

  public Locator getDatatype() {
    return topicMap.wrapLocator(wrapped.getDataType());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.DatatypeAware#getValue()
   */

  public String getValue() {
    return wrapped.getValue();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.DatatypeAware#locatorValue()
   */

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

  public void setValue(String value) {
    Check.valueNotNull(this, value);
    wrapped.setValue(value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.DatatypeAware#setValue(org.tmapi.core.Locator)
   */

  public void setValue(Locator value) {
    Check.valueNotNull(this, value);
    wrapped.setLocator(topicMap.unwrapLocator(value));
  }

  @Override
  public void removeTheme(Topic theme) {
    explicitScope.remove(theme);
    if (!getParent().getScope().contains(theme))
      super.removeTheme(theme);
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
