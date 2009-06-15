
// $Id: TopicNamesIndex.java,v 1.3 2008/06/12 14:37:15 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.oks2tmapi.index;

import java.util.*;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.index.IndexIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.core.index.NameIndexIF;
import net.ontopia.topicmaps.impl.oks2tmapi.TopicMap;

/**
 * INTERNAL: OKS->TMAPI object wrapper.
 */

public class TopicNamesIndex 
  implements org.tmapi.index.core.TopicNamesIndex, org.tmapi.index.IndexFlags {

  protected TopicMap tm;
  protected ClassInstanceIndexIF ci;
  protected NameIndexIF ni;

  public TopicNamesIndex(TopicMap tm, ClassInstanceIndexIF ci, NameIndexIF ni) {
    this.tm = tm;
    this.ci = ci;
    this.ni = ni;
  }

  public void configure(org.tmapi.core.TopicMap tm) {
    // no-op
  }
  
  public Collection getTopicNamesByValue(String value) {
    return tm.wrapSet(ni.getTopicNames(value));
  }

  public Collection getTopicNameTypes() {
    // ISSUE: base name types not supported
    return Collections.EMPTY_SET;
  }

  public Collection getTopicNamesByType(org.tmapi.core.Topic type) {
    // ISSUE: base name types not supported
    return Collections.EMPTY_SET;
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
