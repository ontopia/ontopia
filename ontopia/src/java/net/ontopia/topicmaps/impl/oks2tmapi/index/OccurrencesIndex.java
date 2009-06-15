
// $Id: OccurrencesIndex.java,v 1.5 2008/06/24 12:43:40 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.oks2tmapi.index;

import java.util.*;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.index.IndexIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.core.index.OccurrenceIndexIF;
import net.ontopia.topicmaps.impl.oks2tmapi.TopicMap;

/**
 * INTERNAL: OKS->TMAPI object wrapper.
 */

public class OccurrencesIndex 
  implements org.tmapi.index.core.OccurrencesIndex, org.tmapi.index.IndexFlags {

  protected TopicMap tm;
  protected ClassInstanceIndexIF ci;
  protected OccurrenceIndexIF ii;

  public OccurrencesIndex(TopicMap tm, ClassInstanceIndexIF ci, OccurrenceIndexIF ii) {
    this.tm = tm;
    this.ci = ci;
    this.ii = ii;
  }

  public void configure(org.tmapi.core.TopicMap tm) {
    // no-op
  }

  public Collection getOccurrenceTypes() {
    return tm.wrapSet(ci.getOccurrenceTypes());
  }

  public Collection getOccurrencesByType(org.tmapi.core.Topic type) {
    return tm.wrapSet(ci.getOccurrences(tm.unwrapTopic(type)));
  }

  public Collection getOccurrencesByResource(org.tmapi.core.Locator locator) {
    return tm.wrapSet(ii.getOccurrences(tm.unwrapLocator(locator).getAddress(), DataTypes.TYPE_URI));
  }

  public Collection getOccurrencesByValue(String value) {
    return tm.wrapSet(ii.getOccurrences(value, DataTypes.TYPE_STRING));
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
