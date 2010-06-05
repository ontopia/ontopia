
// $Id: VariantName.java,v 1.47 2008/06/12 14:37:15 geir.gronmo Exp $

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
 * INTERNAL: The rdbms variant name implementation.
 */
public class VariantName extends TMObject implements VariantNameIF {
  
  // ---------------------------------------------------------------------------
  // Persistent property declarations
  // ---------------------------------------------------------------------------

  protected static final int LF_name = 2;
  protected static final int LF_scope = 3;
  protected static final int LF_datatype = 4;
  protected static final int LF_length = 5;
  protected static final int LF_hashcode = 6;
  protected static final int LF_value = 7;
  protected static final int LF_reifier = 8;
  protected static final String[] fields = {"sources", "topicmap", "name", "scope", "datatype", "length", "hashcode", "value", "reifier"};

  public void detach() {
    detachCollectionField(LF_sources);
    detachField(LF_topicmap);
    detachField(LF_name);
    detachField(LF_reifier);
    detachCollectionField(LF_scope);
    detachField(LF_datatype);
    detachField(LF_length);
    detachField(LF_hashcode);
    detachField(LF_value);
  }
  
  // ---------------------------------------------------------------------------
  // Data members
  // ---------------------------------------------------------------------------

  public static final String CLASS_INDICATOR = "N";

  public VariantName() {  
  }
  
  public VariantName(TransactionIF txn) {
    super(txn);
  }

  // ---------------------------------------------------------------------------
  // PersistentIF implementation
  // ---------------------------------------------------------------------------

  public int _p_getFieldCount() {
    return fields.length;
  }
  
  // ---------------------------------------------------------------------------
  // TMObjectIF implementation
  // ---------------------------------------------------------------------------

  public String getClassIndicator() {
    return CLASS_INDICATOR;
  }

  public String getObjectId() {
    return (id == null ? null : CLASS_INDICATOR + id.getKey(0));
  }
  
  // ---------------------------------------------------------------------------
  // TopicNameIF implementation
  // ---------------------------------------------------------------------------

  public void remove() {
    TopicName parent = (TopicName)getTopicName();
    if (parent != null) {
      DeletionUtils.removeDependencies(this);
      parent.removeVariant(this);
    }
  }
  
  public TopicIF getTopic() {
    TopicNameIF name = getTopicName();
    if (name == null) return null;
    return name.getTopic();
  }

  public TopicNameIF getTopicName() {
    return (TopicNameIF)loadField(LF_name);
  }

  /**
   * INTERNAL: Set the name that the variant name belongs to. [parent]
   */
  void setTopicName(TopicNameIF name) {
    // Set parent topic map
    setTopicMap((name == null ? null : (TopicMap)name.getTopicMap()));
    // Notify transaction
    valueChanged(LF_name, name, true);    
  }

  void setTopicMap(TopicMap topicmap) {
    // Notify transaction 
    transactionChanged(topicmap);
    valueChanged(LF_topicmap, topicmap, true);
  }

  public LocatorIF getDataType() {
    return (LocatorIF)loadField(LF_datatype);    
  }

  protected void setDataType(LocatorIF datatype) {
    LocatorIF _datatype = new DataTypeLocator(datatype);
    // Notify listeners
    fireEvent("VariantNameIF.setDataType", _datatype, getDataType());
    // Notify transaction
    valueChanged(LF_datatype, _datatype, true);
  }

  public String getValue() {
    Object value = loadField(LF_value);
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
      throw new OntopiaRuntimeException("VariantName value cannot be non-null at this point: " + value);
    } else {
      return null; // FIXME: or possibly something else
    }
  }

  public void setValue(String value) {
    setValue(value, DataTypes.TYPE_STRING);
  }

  public void setValue(String value, LocatorIF datatype) {
    if (value == null)
      throw new NullPointerException("Variant value must not be null.");
    if (datatype == null)
      throw new NullPointerException("Variant value datatype must not be null.");
    if (!"URI".equals(datatype.getNotation()))
      throw new ConstraintViolationException("Only datatypes with notation 'URI' are supported: " + datatype);
    setValue(value, datatype, value.length(), value.hashCode());
  }
  
  private void setValue(Object value, LocatorIF datatype, long length, long hashcode) {
    setDataType(datatype);
    valueChanged(LF_length, new Long(length), true);
    valueChanged(LF_hashcode, new Long(hashcode), true);
    // Notify listeners
    fireEvent("VariantNameIF.setValue", value, getValue());
    // Notify transaction
    valueChanged(LF_value, value, true);
  }

  public Reader getReader() {
    Object value = loadField(LF_value);
    if (value instanceof String) {
      return new StringReader((String)value);
    } else if (value instanceof OnDemandValue) {
      OnDemandValue odv = (OnDemandValue)value;
      return (Reader)odv.getValue(_p_getTransaction());
    } else if (value != null) {
      throw new OntopiaRuntimeException("VariantName value cannot be non-null at this point: " + value);
    } else {
      return null; // FIXME: or possibly something else
    }
  }

  public void setReader(Reader value, long length, LocatorIF datatype) {
    if (value == null)
      throw new NullPointerException("Variant value must not be null.");
    if (datatype == null)
      throw new NullPointerException("Variant value datatype must not be null.");
    if (length < 0)
      throw new OntopiaRuntimeException("Length of reader is negative.");
    if (!"URI".equals(datatype.getNotation()))
      throw new ConstraintViolationException("Only datatypes with notation 'URI' are supported: " + datatype);
    setValue(new OnDemandValue(new ContentReader(value, length)), datatype, length, length);
  }

  public LocatorIF getLocator() {
    if (!DataTypes.TYPE_URI.equals(getDataType()))
      return null;
    String value = getValue();
    return (value == null ? null : URILocator.create(value));
  }
  
  public void setLocator(LocatorIF locator) {
    if (locator == null)
      throw new NullPointerException("Variant locator must not be null.");
    if (!"URI".equals(locator.getNotation()))
      throw new ConstraintViolationException("Only locators with notation 'URI' are supported: " + locator);
    setValue(locator.getAddress(), DataTypes.TYPE_URI);
  }

  public long getLength() {
    Number length = (Number)loadField(LF_length);
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
    return loadCollectionField(LF_scope);
  }

  public void addTheme(TopicIF theme) {
    _addTheme(theme, true);
  }
  
  void _addTheme(TopicIF theme, boolean validate) {
    if (theme == null)
      throw new NullPointerException("null is not a valid argument.");
    CrossTopicMapException.check(theme, this);
    // Notify listeners
    fireEvent("VariantNameIF.addTheme", theme, null);
    // Notify transaction
    valueAdded(LF_scope, theme, true);
  }

  public void removeTheme(TopicIF theme) {
    _removeTheme(theme, true);
  }
  
  void _removeTheme(TopicIF theme, boolean validate) {
    if (theme == null)
      throw new NullPointerException("null is not a valid argument.");
    CrossTopicMapException.check(theme, this);
    // Notify listeners
    fireEvent("VariantNameIF.removeTheme", null, theme);
    // Notify transaction
    valueRemoved(LF_scope, theme, true);

    // complain if theme already defined on parent
    if (validate) {
      TopicNameIF parent = getTopicName();
      if (parent != null && parent.getScope().contains(theme))
        throw new ConstraintViolationException("Can't remove theme from variant when theme is declared on topic name value.");
    }
  }
  
  // ---------------------------------------------------------------------------
  // ReifiableIF implementation
  // ---------------------------------------------------------------------------

  public TopicIF getReifier() {
    return (TopicIF)loadField(LF_reifier);
  }
  
  public void setReifier(TopicIF _reifier) {
    if (_reifier != null)
      CrossTopicMapException.check(_reifier, this);
    // Notify listeners
    Topic reifier = (Topic)_reifier;
    Topic oldReifier = (Topic)getReifier();
    fireEvent("ReifiableIF.setReifier", reifier, oldReifier);
    valueChanged(LF_reifier, reifier, true);
    if (oldReifier != null)
      oldReifier.setReified(null);
    if (reifier != null)
      reifier.setReified(this);
  }

  // ---------------------------------------------------------------------------
  // Misc. methods
  // ---------------------------------------------------------------------------

  public String toString() {
    return ObjectStrings.toString("rdbms.VariantName", (VariantNameIF)this);
  }

}





