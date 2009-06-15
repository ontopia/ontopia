
// $Id: SnapshotTopicName.java,v 1.3 2008/06/24 10:04:32 geir.gronmo Exp $

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
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: 
 */
public class SnapshotTopicName extends SnapshotTMObject implements TopicNameIF {

	protected TopicIF reifier;
  protected TopicIF topic;
  protected String value;
  protected Collection variants;
  protected Collection scope;
  protected TopicIF type;
  
  SnapshotTopicName(TopicNameIF original, int snapshotType, Map processed) {
    this.snapshotType = snapshotType;

    switch (snapshotType) {
    case SNAPSHOT_REFERENCE:
      this.objectId = original.getObjectId();
      break;
    case SNAPSHOT_COMPLETE:
      this.objectId = original.getObjectId();
      this.srclocs = new ArrayList(original.getItemIdentifiers());
      this.topic = SnapshotTopic.makeSnapshot(original.getTopic(), SnapshotTopic.SNAPSHOT_REFERENCE, processed);
      this.value = original.getValue();
      this.variants = new ArrayList();
      Iterator viter = original.getVariants().iterator();
      while (viter.hasNext()) {
        this.variants.add(SnapshotVariantName.makeSnapshot((VariantNameIF)viter.next(), snapshotType, processed));
      }
      this.scope = new ArrayList();
      Iterator siter = original.getScope().iterator();
      while (siter.hasNext()) {
        this.scope.add(SnapshotTopic.makeSnapshot((TopicIF)siter.next(), snapshotType, processed));
      }
      this.type = SnapshotTopic.makeSnapshot(original.getType(),
                                             SnapshotTopic.SNAPSHOT_REFERENCE, processed);
      this.reifier = SnapshotTopic.makeSnapshot(original.getReifier(),
																								SnapshotTopic.SNAPSHOT_REFERENCE, processed);
      break;
    default:
      throw new OntopiaRuntimeException("Unknown snapshot type: " +
                                        snapshotType);
    }
  }

  public static TopicNameIF makeSnapshot(TopicNameIF original, int snapshotType, Map processed) {
		return new SnapshotTopicName(original, snapshotType, processed);
  }

  // ---------------------------------------------------------------------------
  // TopicNameIF implementation
  // ---------------------------------------------------------------------------

  public TopicIF getTopic() {
    return topic;
  }
  
  public String getValue() {
    return value;
  }
  
  public void setValue(String value) {
    throw new ReadOnlyException();
  }
  
  public Collection getVariants() {
    return (variants == null ? Collections.EMPTY_SET : variants);
  }
  
  public Collection getScope() {
    return (scope == null ? Collections.EMPTY_SET : scope);
  }
  
  public void addTheme(TopicIF theme) {
    throw new ReadOnlyException();
  }
  
  public void removeTheme(TopicIF theme) {
    throw new ReadOnlyException();
  }
  
  public TopicIF getType() {
    return type;
  }
  
  public void setType(TopicIF type) {
    throw new ReadOnlyException();
  }

  public TopicIF getReifier() {
    return reifier;
	}
  
  public void setReifier(TopicIF reifier) {
    throw new ReadOnlyException();
	}

  public String toString() {
    return "[SnapshotTopicName, " + getObjectId() + "]";
  }
  

}
