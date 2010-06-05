
// $Id: ReadOnlyOccurrence.java,v 1.7 2008/07/22 06:31:40 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.rdbms;

import java.io.*;
import java.util.*;

import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.utils.*;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.persistence.proxy.*;

/**
 * INTERNAL: The read-only rdbms occurrence implementation.
 */
public class ReadOnlyOccurrence extends ReadOnlyTMObject implements OccurrenceIF {
  
  // ---------------------------------------------------------------------------
  // Data members
  // ---------------------------------------------------------------------------

  public ReadOnlyOccurrence() {  
  }

  // ---------------------------------------------------------------------------
  // PersistentIF implementation
  // ---------------------------------------------------------------------------

  public int _p_getFieldCount() {
    return Occurrence.fields.length;
  }
  
  // ---------------------------------------------------------------------------
  // TMObjectIF implementation
  // ---------------------------------------------------------------------------

  public String getClassIndicator() {
    return Occurrence.CLASS_INDICATOR;
  }

  public String getObjectId() {
    return (id == null ? null : Occurrence.CLASS_INDICATOR + id.getKey(0));
  }
  
  // ---------------------------------------------------------------------------
  // OccurrenceIF implementation
  // ---------------------------------------------------------------------------

  public TopicIF getTopic() {
    return (TopicIF)loadField(Occurrence.LF_topic);
  }

  public LocatorIF getDataType() {
    return (LocatorIF)loadField(Occurrence.LF_datatype);    
  }

  public void setDataType(LocatorIF datatype) {
    throw new ReadOnlyException();
  }

  public String getValue() {
    Object value = loadField(Occurrence.LF_value);
    if (value instanceof String) {
      return (String) value;
    } else if (value instanceof OnDemandValue) {
      OnDemandValue odv = (OnDemandValue)value;
      try {
        Reader r = (Reader)odv.getValue(_p_getTransaction());
        try {
          return StreamUtils.readString(r, getLength());
        } finally {
          r.close();
        }
      } catch (IOException e) {
        throw new OntopiaRuntimeException(e);
      }
    } else if (value != null) {
      throw new OntopiaRuntimeException("Occurrence value cannot be non-null at this point: " + value);
    } else {
      return null; // FIXME: or possibly something else
    }
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
    Number length = (Number)loadField(Occurrence.LF_length);
    long len = (length == null ? 0 : length.longValue());
    if (len < 0)
      return len * -1L;
    else
      return len;
  }
  
  // ---------------------------------------------------------------------------
  // ScopedIF implementation
  // ---------------------------------------------------------------------------

  public Collection<TopicIF> getScope() {
    return (Collection<TopicIF>) loadCollectionField(Occurrence.LF_scope);
  }

  public void addTheme(TopicIF theme) {
    throw new ReadOnlyException();
  }

  public void removeTheme(TopicIF theme) {
    throw new ReadOnlyException();
  }

  // ---------------------------------------------------------------------------
  // TypedIF implementation
  // ---------------------------------------------------------------------------

  public TopicIF getType() {
    return (TopicIF)loadField(Occurrence.LF_type);
  }

  public void setType(TopicIF type) {
    throw new ReadOnlyException();
  }
  
  // ---------------------------------------------------------------------------
  // ReifiableIF implementation
  // ---------------------------------------------------------------------------

  public TopicIF getReifier() {
    return (TopicIF)loadField(Occurrence.LF_reifier);
  }
  
  public void setReifier(TopicIF reifier) {
    throw new ReadOnlyException();
  }

  // ---------------------------------------------------------------------------
  // Misc. methods
  // ---------------------------------------------------------------------------

  public String toString() {
    return ObjectStrings.toString("rdbms.ReadOnlyOccurrence", (OccurrenceIF)this);
  }
  
}
