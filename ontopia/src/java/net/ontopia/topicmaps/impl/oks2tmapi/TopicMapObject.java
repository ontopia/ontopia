
// $Id: TopicMapObject.java,v 1.10 2008/06/13 08:17:51 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.oks2tmapi;

import java.util.*;
import net.ontopia.topicmaps.core.*;

/**
 * INTERNAL: OKS->TMAPI object wrapper.
 */

public abstract class TopicMapObject implements org.tmapi.core.TopicMapObject {

  protected TopicMap tm;

  TopicMapObject(TopicMap tm) {
    this.tm = tm;
  }

  public abstract TMObjectIF getWrapped();

  public org.tmapi.core.TopicMap getTopicMap() {
    return tm;
  }

  public Set getSourceLocators() {
    return tm.wrapSet(getWrapped().getItemIdentifiers());
  }

  public void addSourceLocator(org.tmapi.core.Locator sourceLocator) {
    try {
      getWrapped().addItemIdentifier(tm.unwrapLocator(sourceLocator));
    } catch (UniquenessViolationException e) {
      org.tmapi.core.TopicMapObject o = tm._getTopicMapObjectBySourceLocator(sourceLocator);

      if (o instanceof org.tmapi.core.Topic && this instanceof org.tmapi.core.Topic)
	throw new org.tmapi.core.TopicsMustMergeException((Topic)this, (Topic)o, "Another topic already has this source locator: " + sourceLocator);
      else
	throw new org.tmapi.core.DuplicateSourceLocatorException(this, o, sourceLocator, "Another topic map object already has this source locator: " + sourceLocator);
    }
  }
  
  public void removeSourceLocator(org.tmapi.core.Locator sourceLocator) {
    getWrapped().removeItemIdentifier(tm.unwrapLocator(sourceLocator));
  }

  public abstract void remove()
    throws org.tmapi.core.TMAPIException;

  public String getObjectId() {
    return getWrapped().getObjectId();
  }

  public boolean equals(Object o) {
    if (o == null || !(o instanceof org.tmapi.core.TopicMapObject))
      return false;    
    return getObjectId().equals(((TopicMapObject)o).getObjectId());
  }

  public int hashCode() {
    return getWrapped().getObjectId().hashCode();
  }

}
