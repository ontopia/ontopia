
// $Id: TopicMapObjectsIndex.java,v 1.2 2008/06/13 08:36:26 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.oks2tmapi.index;

import java.util.*;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.index.IndexIF;
import net.ontopia.topicmaps.impl.oks2tmapi.TopicMap;

/**
 * INTERNAL: OKS->TMAPI object wrapper.
 */

public class TopicMapObjectsIndex 
  implements org.tmapi.index.core.TopicMapObjectsIndex, org.tmapi.index.IndexFlags {

  protected TopicMap tm;

  public TopicMapObjectsIndex(TopicMap tm) {
    this.tm = tm;
  }

  public void configure(org.tmapi.core.TopicMap tm) {
    // no-op
  }

  public org.tmapi.core.TopicMapObject getTopicMapObjectBySourceLocator(org.tmapi.core.Locator sourceLocator) {
    TopicMapIF otm = (TopicMapIF)tm.getWrapped();
    return tm.wrapTMObject(otm.getObjectByItemIdentifier(tm.unwrapLocator(sourceLocator)));
  }

  /* --- Index implementation */

  public void open()
    throws org.tmapi.index.TMAPIIndexException {
    // no-op
  }
   
  public void close()
    throws org.tmapi.index.TMAPIIndexException {
    // no-op
  }

  public boolean isOpen()
    throws org.tmapi.index.TMAPIIndexException {
    // always true
    return true;
  }
   
  public void reindex()
    throws org.tmapi.index.TMAPIIndexException {
  }

  public org.tmapi.index.IndexFlags getFlags()
    throws org.tmapi.index.TMAPIIndexException {
    return this;
  }
  
  public boolean isAutoUpdated() {
    return true;
  }
  
}
