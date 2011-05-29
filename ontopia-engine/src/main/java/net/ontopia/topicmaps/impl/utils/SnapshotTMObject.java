
package net.ontopia.topicmaps.impl.utils;

import java.util.Collection;
import java.util.Collections;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.core.ConstraintViolationException;

/**
 * INTERNAL: 
 */
public abstract class SnapshotTMObject implements TMObjectIF {

  public static final int SNAPSHOT_REFERENCE = 1;
  public static final int SNAPSHOT_COMPLETE = 2;

  protected int snapshotType;

  protected String objectId;
  protected Collection<LocatorIF> srclocs;
  
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

  public Collection<LocatorIF> getItemIdentifiers() {
    Collection<LocatorIF> empty = Collections.emptyList();
    return (srclocs == null ? empty : srclocs);
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
