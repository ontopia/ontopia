
// $Id: Variant.java,v 1.8 2008/06/12 14:37:14 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.oks2tmapi;

import java.util.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.infoset.core.LocatorIF;

/**
 * INTERNAL: OKS->TMAPI object wrapper.
 */

public class Variant extends ScopedObject implements org.tmapi.core.Variant {

  VariantNameIF other;

  Variant(TopicMap tm, VariantNameIF other) {
    super(tm);
    this.other = other;
  }

  public TMObjectIF getWrapped() {
    return other;
  }

  public void remove() throws org.tmapi.core.TMAPIException {
    other.remove();
  }

  public org.tmapi.core.TopicName getTopicName() {
    return tm.wrapTopicName(other.getTopicName());
  }

  public String getValue() {
    return other.getValue();
  }

  public void setValue(String value) {
    if (other.getLocator() != null) other.setLocator(null);
    other.setValue(value);
  }

  public org.tmapi.core.Locator getResource() {
    return tm.wrapLocator(other.getLocator());
  }

  public void setResource(org.tmapi.core.Locator resource) {
    if (other.getValue() != null) other.setValue(null);
    other.setLocator(tm.unwrapLocator(resource));
  }

  public org.tmapi.core.Topic getReifier() {
    return tm._getReifier(this, tm);
  }
    
}
