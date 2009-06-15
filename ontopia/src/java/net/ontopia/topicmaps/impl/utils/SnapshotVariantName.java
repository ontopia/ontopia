
// $Id: SnapshotVariantName.java,v 1.7 2008/06/24 10:04:32 geir.gronmo Exp $

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
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: 
 */
public class SnapshotVariantName extends SnapshotTMObject implements VariantNameIF {

	protected TopicIF reifier;
  protected TopicNameIF basename;
  protected TopicIF topic;
  protected LocatorIF datatype;
  protected String value;
  protected Collection scope;
  
  SnapshotVariantName(VariantNameIF original, int snapshotType, Map processed) {
    this.snapshotType = snapshotType;

    switch (snapshotType) {
    case SNAPSHOT_REFERENCE:
      this.objectId = original.getObjectId();
      break;
    case SNAPSHOT_COMPLETE:
      this.objectId = original.getObjectId();
      this.srclocs = new ArrayList(original.getItemIdentifiers());
      this.basename = SnapshotTopicName.makeSnapshot(original.getTopicName(), SnapshotTopic.SNAPSHOT_REFERENCE, processed);
      this.topic = SnapshotTopic.makeSnapshot(original.getTopic(), SnapshotTopic.SNAPSHOT_REFERENCE, processed);
      this.datatype = original.getDataType();
      this.value = original.getValue();
      this.scope = new ArrayList();
      Iterator siter = original.getScope().iterator();
      while (siter.hasNext()) {
        this.scope.add(SnapshotTopic.makeSnapshot((TopicIF)siter.next(), snapshotType, processed));
      }
      this.reifier = SnapshotTopic.makeSnapshot(original.getReifier(),
																								SnapshotTopic.SNAPSHOT_REFERENCE, processed);
      break;
    default:
      throw new OntopiaRuntimeException("Unknown snapshot type: " + snapshotType);
    }
  }

  public static VariantNameIF makeSnapshot(VariantNameIF original, int snapshotType, Map processed) {
    return new SnapshotVariantName(original, snapshotType, processed);
  }

  // -----------------------------------------------------------------------------
  // VariantNameIF implementation
  // -----------------------------------------------------------------------------

  public TopicNameIF getTopicName() {
    return basename;
  }
  
  public TopicIF getTopic() {
    return topic;
  }

  public LocatorIF getDataType() {
    return datatype;
  }
  
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

  public TopicIF getReifier() {
    return reifier;
	}
  
  public void setReifier(TopicIF reifier) {
    throw new ReadOnlyException();
	}

  public String toString() {
    return "[SnapshotVariantName, " + getObjectId() + "]";
  }
  

}
