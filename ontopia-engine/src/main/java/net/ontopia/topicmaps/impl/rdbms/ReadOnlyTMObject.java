
package net.ontopia.topicmaps.impl.rdbms;

import java.util.*;
import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.impl.utils.*;
import net.ontopia.persistence.proxy.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: 
 */
public abstract class ReadOnlyTMObject extends AbstractROPersistent implements TMObjectIF {

  public ReadOnlyTMObject() {
  }
  
  // ---------------------------------------------------------------------------
  // PersistentIF implementation
  // ---------------------------------------------------------------------------

  /**
   * INTERNAL: Returns the token that can be used to indicate the
   * class of this instance. This indicator is currently only used by
   * item identifiers.
   */
  public abstract String getClassIndicator();
  
  long getLongId() {
    return ((Long)id.getKey(0)).longValue();
  }
  
  // ---------------------------------------------------------------------------
  // TMObjectIF implementation
  // ---------------------------------------------------------------------------

  public abstract String getObjectId();

  public boolean isReadOnly() {
    return true;
  }

  public TopicMapIF getTopicMap() {
    try {
      return (TopicMapIF)loadField(TMObject.LF_topicmap);
    } catch (IdentityNotFoundException e) {
      // object has been deleted by somebody else, so return null
      return null;
    }
  }

  public Collection<LocatorIF> getItemIdentifiers() {
    return (Collection<LocatorIF>) loadCollectionField(TMObject.LF_sources);
  }

  public void addItemIdentifier(LocatorIF source_locator) throws ConstraintViolationException {
    throw new ReadOnlyException();
  }

  public void removeItemIdentifier(LocatorIF source_locator) {
    throw new ReadOnlyException();
  }

  public void remove() {
    throw new ReadOnlyException();
  }
  
}
