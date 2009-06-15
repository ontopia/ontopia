
// $Id: SnapshotOccurrence.java,v 1.9 2008/06/24 10:04:33 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.utils;

import java.io.Reader;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.DataTypes;
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
public class SnapshotOccurrence extends SnapshotTMObject implements OccurrenceIF {

	protected TopicIF reifier;
  protected TopicIF topic;
  protected LocatorIF datatype;
  protected String value;
  protected Collection scope;
  protected TopicIF type;
  
  SnapshotOccurrence(OccurrenceIF original, int snapshotType, Map processed) {
    this.snapshotType = snapshotType;

    switch (snapshotType) {
    case SNAPSHOT_REFERENCE:
      this.objectId = original.getObjectId();
      break;
    case SNAPSHOT_COMPLETE:
      this.objectId = original.getObjectId();
      this.srclocs = new ArrayList(original.getItemIdentifiers());
      this.topic = SnapshotTopic.makeSnapshot(original.getTopic(), SnapshotTopic.SNAPSHOT_REFERENCE, processed);
      this.datatype = original.getDataType();
      this.value = original.getValue();
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
      throw new OntopiaRuntimeException("Unknown snapshot type: " + snapshotType);
    }
  }

  public static OccurrenceIF makeSnapshot(OccurrenceIF original, int snapshotType, Map processed) {
    return new SnapshotOccurrence(original, snapshotType, processed);
  }

  // -----------------------------------------------------------------------------
  // OccurrenceIF implementation
  // -----------------------------------------------------------------------------

  public TopicIF getTopic() {
    return topic;
  }

  public LocatorIF getDataType() {
    return datatype;
  }

  //! public void setDataType(LocatorIF datatype) {
  //!   throw new ReadOnlyException();
  //! }
  
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    setValue(value, DataTypes.TYPE_STRING);
  }
  
  public void setValue(String value, LocatorIF datatype) {
    throw new ReadOnlyException();
  }

  public Reader getReader() {
    throw new UnsupportedOperationException();
  }

  public void setReader(Reader value, long length, LocatorIF datatype) {
    throw new UnsupportedOperationException();
  }
  
  public LocatorIF getLocator() {
    if (!DataTypes.TYPE_URI.equals(getDataType())) return null;
    String value = getValue();
    return (value == null ? null : URILocator.create(value));
  }
  
  public void setLocator(LocatorIF locator) {
    throw new ReadOnlyException();
  }

  public long getLength() {
    return (value == null ? 0 : value.length());
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
    return "[SnapshotOccurrence, " + getObjectId() + "]";
  }
  

}
