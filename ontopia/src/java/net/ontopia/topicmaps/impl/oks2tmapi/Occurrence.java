
// $Id: Occurrence.java,v 1.9 2008/01/11 12:22:18 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.oks2tmapi;

import java.util.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.infoset.core.LocatorIF;

/**
 * INTERNAL: OKS->TMAPI object wrapper.
 */

public class Occurrence extends ScopedObject implements org.tmapi.core.Occurrence {

  OccurrenceIF other;

  Occurrence(TopicMap tm, OccurrenceIF other) {
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

  public void setType(org.tmapi.core.Topic type) {
    other.setType(tm.unwrapTopic(type));
  }

  public org.tmapi.core.Topic getType() {
    return tm.wrapTopic(other.getType());
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
