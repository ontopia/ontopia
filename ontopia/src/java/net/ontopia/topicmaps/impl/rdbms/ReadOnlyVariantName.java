
// $Id: ReadOnlyVariantName.java,v 1.5 2008/06/12 14:37:15 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.rdbms;
import java.io.Reader;
import java.io.InputStream;
import java.util.*;

import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.utils.*;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.persistence.proxy.*;
  
/**
 * INTERNAL: The read-only rdbms variant name implementation.
 */
public class ReadOnlyVariantName extends ReadOnlyTMObject implements VariantNameIF {
  
  // -----------------------------------------------------------------------------
  // Data members
  // -----------------------------------------------------------------------------

  public ReadOnlyVariantName() {  
  }

  // -----------------------------------------------------------------------------
  // PersistentIF implementation
  // -----------------------------------------------------------------------------

  public int _p_getFieldCount() {
    return VariantName.fields.length;
  }
  
  // -----------------------------------------------------------------------------
  // TMObjectIF implementation
  // -----------------------------------------------------------------------------

  public String getClassIndicator() {
    return VariantName.CLASS_INDICATOR;
  }

  public String getObjectId() {
    return (id == null ? null : VariantName.CLASS_INDICATOR + id.getKey(0));
  }
  
  // -----------------------------------------------------------------------------
  // TopicNameIF implementation
  // -----------------------------------------------------------------------------
  
  public TopicIF getTopic() {
    TopicNameIF name = getTopicName();
    if (name == null) return null;
    return name.getTopic();
  }

  public TopicNameIF getTopicName() {
    return (TopicNameIF)loadField(VariantName.LF_name);
  }

  public LocatorIF getDataType() {
    return (LocatorIF)loadField(VariantName.LF_datatype);    
  }

  public void setDataType(LocatorIF datatype) {
    throw new ReadOnlyException();
  }

  public String getValue() {
    return (String)loadField(VariantName.LF_value);    
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
    Number length = (Number)loadField(VariantName.LF_length);
    long len = (length == null ? 0 : length.longValue());
    if (len < 0)
      return len * -1L;
    else
      return len;
  }

  // -----------------------------------------------------------------------------
  // ScopedIF implementation
  // -----------------------------------------------------------------------------

  public Collection getScope() {
    return loadCollectionField(VariantName.LF_scope);
  }

  public void addTheme(TopicIF theme) {
    throw new ReadOnlyException();
  }

  public void removeTheme(TopicIF theme) {
    throw new ReadOnlyException();
  }
  
  // -----------------------------------------------------------------------------
  // ReifiableIF implementation
  // -----------------------------------------------------------------------------

  public TopicIF getReifier() {
		return (TopicIF)loadField(VariantName.LF_reifier);
	}
  
  public void setReifier(TopicIF reifier) {
    throw new ReadOnlyException();
	}

  // -----------------------------------------------------------------------------
  // Misc. methods
  // -----------------------------------------------------------------------------

  public String toString() {
    return ObjectStrings.toString("rdbms.ReadOnlyVariantName", (VariantNameIF)this);
  }

}





