
// $Id: SnapshotTopic.java,v 1.11 2008/06/24 10:04:32 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: 
 */
public class SnapshotTopic extends SnapshotTMObject implements TopicIF {

	protected ReifiableIF reified;

  protected Collection sublocs;  
  protected Collection subinds;

  protected Collection basenames;
  protected Collection occurrences;
  protected Collection types;
  
  SnapshotTopic(TopicIF original, int snapshotType, Map processed) {
    this.snapshotType = snapshotType;

    switch (snapshotType) {
    case SNAPSHOT_REFERENCE:
      this.objectId = original.getObjectId();
      break;
    case SNAPSHOT_COMPLETE:
      this.objectId = original.getObjectId();
      this.sublocs = new ArrayList(original.getSubjectLocators());
      this.subinds = new ArrayList(original.getSubjectIdentifiers());
      this.srclocs = new ArrayList(original.getItemIdentifiers());
      this.basenames = new ArrayList();
      Iterator biter = original.getTopicNames().iterator();
      while (biter.hasNext()) {
        this.basenames.add(SnapshotTopicName.makeSnapshot((TopicNameIF)biter.next(), snapshotType, processed));
      }
      this.occurrences = new ArrayList();
      Iterator oiter = original.getOccurrences().iterator();
      while (oiter.hasNext()) {
        this.occurrences.add(SnapshotOccurrence.makeSnapshot((OccurrenceIF)oiter.next(), snapshotType, processed));
      }
      this.types = new ArrayList();
      Iterator titer = original.getTypes().iterator();
      while (titer.hasNext()) {
        this.types.add(SnapshotTopic.makeSnapshot((TopicIF)titer.next(), snapshotType, processed));
      }
			// TODO: add support for this when needed
      //! this.reified = SnapshotTopic.makeSnapshot(original.getReified(),
      //!                                        SnapshotTopic.SNAPSHOT_REFERENCE);      
      break;
    default:
      throw new OntopiaRuntimeException("Unknown snapshot type: " + snapshotType);
    }
  }

  public static TopicIF makeSnapshot(TopicIF original, int snapshotType, Map processed) {
    if (original == null)
      return null; // this avoids a thousand ifs elsewhere
    else if (processed.containsKey(original))
			return (SnapshotTopic)processed.get(original);
		
		SnapshotTopic st = new SnapshotTopic(original, snapshotType, processed);
		processed.put(original, st);
		return st;
  }

  // ---------------------------------------------------------------------------
  // TopicIF implementation
  // ---------------------------------------------------------------------------

  public Collection getSubjectLocators() {
    return (sublocs == null ? Collections.EMPTY_SET : sublocs);
	}

  public void addSubjectLocator(LocatorIF subject_locator) throws ConstraintViolationException {
    throw new UnsupportedOperationException();
	}

	public void removeSubjectLocator(LocatorIF subject_locator) {
    throw new UnsupportedOperationException();
	}

  public Collection getSubjectIdentifiers() {
    return (subinds == null ? Collections.EMPTY_SET : subinds);
  }

  public void addSubjectIdentifier(LocatorIF locator) throws ConstraintViolationException {
    throw new ReadOnlyException();
  }

  public void removeSubjectIdentifier(LocatorIF loc) {
    throw new ReadOnlyException();
  }

  public Collection getTypes() {
    return (types == null ? Collections.EMPTY_SET : types);
  }
  
  public void addType(TopicIF type) {
    throw new ReadOnlyException();
  }
  
  public void removeType(TopicIF type) {
    throw new ReadOnlyException();
  }
  
  public Collection getTopicNames() {
    return (basenames == null ? Collections.EMPTY_SET : basenames);
  }
  
  public Collection getOccurrences() {
    return (occurrences == null ? Collections.EMPTY_SET : occurrences);
  }
  
  public Collection getRoles() {
    return Collections.EMPTY_SET;
  }
  
  public Collection getRolesByType(TopicIF rtype) {
    return Collections.EMPTY_SET;
  }
  
  public Collection getRolesByType(TopicIF rtype, TopicIF atype) {
    return Collections.EMPTY_SET;
  }
  
  public void merge(TopicIF topic) {
    throw new ReadOnlyException();
  }

  public String toString() {
    return "[SnapshotTopic, " + getObjectId() + "]";
  }
  
	public ReifiableIF getReified() {
		return reified;
	}

}
