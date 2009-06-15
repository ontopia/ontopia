
// $Id: TopicName.java,v 1.9 2008/06/12 14:37:14 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.oks2tmapi;

import java.util.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.infoset.core.LocatorIF;

/**
 * INTERNAL: OKS->TMAPI object wrapper.
 */

public class TopicName extends ScopedObject implements org.tmapi.core.TopicName {

  TopicNameIF other;

  TopicName(TopicMap tm, TopicNameIF other) {
    super(tm);
    this.other = other;
  }

  public TMObjectIF getWrapped() {
    return other;
  }

  public void remove() throws org.tmapi.core.TMAPIException {
    other.remove();
  }

  public org.tmapi.core.Topic getTopic() {
    return tm.wrapTopic(other.getTopic());
  }

  public String getValue() {
    return other.getValue();
  }

  public void setValue(String value) {
    other.setValue(value);
  }

  public Set getVariants() {
    return tm.wrapSet(other.getVariants());
  }

  public org.tmapi.core.Variant createVariant(String value, Collection scope) {
    TopicMapIF otm = other.getTopicMap();
    VariantNameIF variant = otm.getBuilder().makeVariantName(other, value);
    if (scope != null && !scope.isEmpty()) {
      Iterator iter = scope.iterator();
      while (iter.hasNext()) {
	variant.addTheme(tm.unwrapTopic((org.tmapi.core.Topic)iter.next()));
      }
    }
    return tm.wrapVariant(variant);
  }

  public org.tmapi.core.Variant createVariant(org.tmapi.core.Locator resource, Collection scope) {
    TopicMapIF otm = other.getTopicMap();
    VariantNameIF variant = otm.getBuilder().makeVariantName(other, tm.unwrapLocator(resource));
    if (scope != null && !scope.isEmpty()) {
      Iterator iter = scope.iterator();
      while (iter.hasNext()) {
	variant.addTheme(tm.unwrapTopic((org.tmapi.core.Topic)iter.next()));
      }
    }
    return tm.wrapVariant(variant);
  }

  public org.tmapi.core.Topic getReifier() {
    return tm._getReifier(this, tm);
  }

  public org.tmapi.core.Topic getType() {
    // NOTE: not yet supported
    return null;
  }
  
  public void setType(org.tmapi.core.Topic type)
    throws UnsupportedOperationException {
    throw new UnsupportedOperationException("XTM 1.1 not yet supported.");
  }
    
}
