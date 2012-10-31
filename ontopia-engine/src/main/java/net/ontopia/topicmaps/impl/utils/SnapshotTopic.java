
package net.ontopia.topicmaps.impl.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: 
 */
public class SnapshotTopic extends SnapshotTMObject implements TopicIF {

	protected ReifiableIF reified;

  protected Collection<LocatorIF> sublocs;
  protected Collection<LocatorIF> subinds;

  protected Collection<TopicNameIF> basenames;
  protected Collection<OccurrenceIF> occurrences;
  protected Collection<TopicIF> types;
  
  SnapshotTopic(TopicIF original, int snapshotType, Map<TMObjectIF, SnapshotTMObject> processed) {
    this.snapshotType = snapshotType;

    switch (snapshotType) {
    case SNAPSHOT_REFERENCE:
      this.objectId = original.getObjectId();
      break;
    case SNAPSHOT_COMPLETE:
      this.objectId = original.getObjectId();
      this.sublocs = new ArrayList<LocatorIF>(original.getSubjectLocators());
      this.subinds = new ArrayList<LocatorIF>(original.getSubjectIdentifiers());
      this.srclocs = new ArrayList<LocatorIF>(original.getItemIdentifiers());
      this.basenames = new ArrayList<TopicNameIF>();
      Iterator<TopicNameIF> biter = original.getTopicNames().iterator();
      while (biter.hasNext()) {
        this.basenames.add(SnapshotTopicName.makeSnapshot(biter.next(), snapshotType, processed));
      }
      this.occurrences = new ArrayList<OccurrenceIF>();
      Iterator<OccurrenceIF> oiter = original.getOccurrences().iterator();
      while (oiter.hasNext()) {
        this.occurrences.add(SnapshotOccurrence.makeSnapshot(oiter.next(), snapshotType, processed));
      }
      this.types = new ArrayList<TopicIF>();
      Iterator<TopicIF> titer = original.getTypes().iterator();
      while (titer.hasNext()) {
        this.types.add(SnapshotTopic.makeSnapshot(titer.next(), snapshotType, processed));
      }
			// TODO: add support for this when needed
      //! this.reified = SnapshotTopic.makeSnapshot(original.getReified(),
      //!                                        SnapshotTopic.SNAPSHOT_REFERENCE);      
      break;
    default:
      throw new OntopiaRuntimeException("Unknown snapshot type: " + snapshotType);
    }
  }

  public static TopicIF makeSnapshot(TopicIF original, int snapshotType, Map<TMObjectIF, SnapshotTMObject> processed) {
    if (original == null)
      return null; // this avoids a thousand ifs elsewhere
    else if (processed.containsKey(original))
			return (TopicIF)processed.get(original);
		
		SnapshotTopic st = new SnapshotTopic(original, snapshotType, processed);
		processed.put(original, st);
		return st;
  }

  // ---------------------------------------------------------------------------
  // TopicIF implementation
  // ---------------------------------------------------------------------------

  public Collection<LocatorIF> getSubjectLocators() {
    Collection<LocatorIF> empty = Collections.emptyList();
    return (sublocs == null ? empty : sublocs);
	}

  public void addSubjectLocator(LocatorIF subject_locator) throws ConstraintViolationException {
    throw new UnsupportedOperationException();
    // should this be throw new ReadOnlyException(); ?
	}

	public void removeSubjectLocator(LocatorIF subject_locator) {
    throw new UnsupportedOperationException();
    // should this be throw new ReadOnlyException(); ?
	}

  public Collection<LocatorIF> getSubjectIdentifiers() {
    Collection<LocatorIF> empty = Collections.emptyList();
    return (subinds == null ? empty : subinds);
  }

  public void addSubjectIdentifier(LocatorIF locator) throws ConstraintViolationException {
    throw new ReadOnlyException();
  }

  public void removeSubjectIdentifier(LocatorIF loc) {
    throw new ReadOnlyException();
  }

  public Collection<TopicIF> getTypes() {
    Collection<TopicIF> empty = Collections.emptyList();
    return (types == null ? empty : types);
  }
  
  public void addType(TopicIF type) {
    throw new ReadOnlyException();
  }
  
  public void removeType(TopicIF type) {
    throw new ReadOnlyException();
  }
  
  public Collection<TopicNameIF> getTopicNames() {
    Collection<TopicNameIF> empty = Collections.emptyList();
    return (basenames == null ? empty : basenames);
  }
  
  public Collection<OccurrenceIF> getOccurrences() {
    Collection<OccurrenceIF> empty = Collections.emptyList();
    return (occurrences == null ? empty : occurrences);
  }

  public Collection<OccurrenceIF> getOccurrencesByType(TopicIF type) {
    return Collections.emptyList();
  }
  
  public Collection<AssociationRoleIF> getRoles() {
    return Collections.emptyList();
  }
  
  public Collection<AssociationRoleIF> getRolesByType(TopicIF rtype) {
    return Collections.emptyList();
  }
  
  public Collection<AssociationRoleIF> getRolesByType(TopicIF rtype, TopicIF atype) {
    return Collections.emptyList();
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
