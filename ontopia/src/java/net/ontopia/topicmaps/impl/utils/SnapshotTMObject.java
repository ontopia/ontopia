
// $Id: SnapshotTMObject.java,v 1.5 2008/06/13 08:17:52 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: 
 */
public abstract class SnapshotTMObject implements TMObjectIF {

  public static final int SNAPSHOT_REFERENCE = 1;
  public static final int SNAPSHOT_COMPLETE = 2;

  protected int snapshotType;

  protected String objectId;
  protected Collection srclocs;
  
  // -----------------------------------------------------------------------------
  // TMObjectIF implementation
  // -----------------------------------------------------------------------------
  
  public String getObjectId() {
    return objectId;
  }
  
  public boolean isReadOnly() {
    return true;
  }
  
  public TopicMapIF getTopicMap() {
    return null;
  }

  public Collection getItemIdentifiers() {
    return (srclocs == null ? Collections.EMPTY_SET : srclocs);
  }

  public void addItemIdentifier(LocatorIF locator) throws ConstraintViolationException {
    throw new ReadOnlyException();
  }

  public void removeItemIdentifier(LocatorIF locator) {
    throw new ReadOnlyException();
  }
  
  public void remove() {
    throw new ReadOnlyException();
  }

}
