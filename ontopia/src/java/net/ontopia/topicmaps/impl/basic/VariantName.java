
// $Id: VariantName.java,v 1.51 2008/06/12 14:37:14 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.basic;

import java.io.Reader;
import java.io.InputStream;
import java.io.StringReader;
import java.io.IOException;
import net.ontopia.utils.ReaderInputStream;

import java.util.Collection;
import java.util.Collections;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.CrossTopicMapException;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.impl.utils.DeletionUtils;
import net.ontopia.topicmaps.impl.utils.ObjectStrings;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.UniqueSet;
import net.ontopia.utils.StreamUtils;
  
/**
 * INTERNAL: The basic variant name implementation.
 */

public class VariantName extends TMObject implements VariantNameIF {

  static final long serialVersionUID = -7350019735868904034L;

	protected TopicIF reifier;
  protected String value;
  protected UniqueSet scope;
  protected LocatorIF datatype;

  VariantName(TopicMap tm) {
    super(tm);
    // WARNING: This means that the unique set will not get
    // dereferenced even though the variant name has been
    // dereferenced.
    scope = topicmap.setpool.get(Collections.EMPTY_SET);
  }
  
  // -----------------------------------------------------------------------------
  // TopicNameIF implementation
  // -----------------------------------------------------------------------------
  
  public TopicIF getTopic() {
    if (parent == null)
      return null;
    else 
      return (TopicIF)((TopicName)parent).parent;
  }

  public TopicNameIF getTopicName() {
    return (TopicNameIF)parent;
  }

  /**
   * INTERNAL: Set the topic name that the variant name belongs to. [parent]
   */
  void setTopicName(TopicName parent) {
    // Validate topic map
    if (parent != null && parent.topicmap != this.topicmap)
      throw new ConstraintViolationException("Cannot move objects across topic maps: "
                                             + this.topicmap + " and " + parent.topicmap);
    // (De)reference pooled sets
    if (scope != null) {
      if (parent == null)
        topicmap.setpool.dereference(scope);
      else
        scope = topicmap.setpool.get(scope);
    }
    
    // Set parent
    this.parent = parent;
  }

  public LocatorIF getDataType() {
    return datatype;    
  }

  protected void setDataType(LocatorIF datatype) {
    // Notify listeners
    fireEvent("VariantNameIF.setDataType", value, getDataType());
    this.datatype = datatype;
  }
  
  public String getValue() {
    return value;
  }
  
  public void setValue(String value) {
    setValue(value, DataTypes.TYPE_STRING);
  }

  public void setValue(String value, LocatorIF datatype) {
		if (value == null) throw new NullPointerException("Variant value must not be null.");
		if (datatype == null) throw new NullPointerException("Variant value datatype must not be null.");
		if (!"URI".equals(datatype.getNotation()))
			throw new ConstraintViolationException("Only datatypes with notation 'URI' are supported: " + datatype);
    setDataType(datatype);
    // Notify listeners
    fireEvent("VariantNameIF.setValue", value, getValue());
    this.value = value;
  }

  public Reader getReader() {
    return (value == null ? null : new StringReader(value));
  }
  
  public void setReader(Reader value, long length, LocatorIF datatype) {
		if (value == null) throw new NullPointerException("Variant value must not be null.");
		if (datatype == null) throw new NullPointerException("Variant value datatype must not be null.");
		if (!"URI".equals(datatype.getNotation()))
			throw new ConstraintViolationException("Only datatypes with notation 'URI' are supported: " + datatype);
    try {
      setValue(StreamUtils.readString(value, length), datatype);
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  public LocatorIF getLocator() {
    if (!DataTypes.TYPE_URI.equals(getDataType())) return null;
    String value = getValue();
    return (value == null ? null : URILocator.create(value));
  }
  
  public void setLocator(LocatorIF locator) {
		if (locator == null) throw new NullPointerException("Variant locator must not be null.");
		if (!"URI".equals(locator.getNotation()))
			throw new ConstraintViolationException("Only locators with notation 'URI' are supported: " + locator);
    setValue(locator.getAddress(), DataTypes.TYPE_URI);
  }

  public long getLength() {
    return (value == null ? 0 : value.length());
  }

  // -----------------------------------------------------------------------------
  // ScopedIF implementation
  // -----------------------------------------------------------------------------
  
  public Collection getScope() {
    // Return scope defined on this object
    return scope;
  }
  public void addTheme(TopicIF theme) {
    _addTheme(theme, true);
  }
  void _addTheme(TopicIF theme, boolean validate) {
    if (theme == null) throw new NullPointerException("null is not a valid argument.");
		CrossTopicMapException.check(theme, this);
    // Notify listeners
    fireEvent("VariantNameIF.addTheme", theme, null);
    // Add theme to scope
    scope = topicmap.setpool.add(scope, theme, true);
  }
  public void removeTheme(TopicIF theme) {
    _removeTheme(theme, true);
  }
  void _removeTheme(TopicIF theme, boolean validate) {
    if (theme == null) throw new NullPointerException("null is not a valid argument.");
		CrossTopicMapException.check(theme, this);
    // Notify listeners
    fireEvent("VariantNameIF.removeTheme", null, theme);
    // Remove theme from scope
    scope = topicmap.setpool.remove(scope, theme, true);

    // complain if theme already defined on parent
    if (validate && parent != null && ((TopicNameIF)parent).getScope().contains(theme)) {
      throw new ConstraintViolationException("Can't remove theme from variant when theme is declared on topic name value.");
    }
  }

  public void remove() {
    if (parent != null) {
			DeletionUtils.removeDependencies(this);
      ((TopicName)parent).removeVariant(this);
		}
  }
  
  // -----------------------------------------------------------------------------
  // ReifiableIF implementation
  // -----------------------------------------------------------------------------

  public TopicIF getReifier() {
		return reifier;
	}
  
  public void setReifier(TopicIF _reifier) {
		if (_reifier != null) CrossTopicMapException.check(_reifier, this);
    // Notify listeners
		Topic reifier = (Topic)_reifier;
		Topic oldReifier = (Topic)getReifier();
    fireEvent("ReifiableIF.setReifier", reifier, oldReifier);
    this.reifier = reifier;
		if (oldReifier != null) oldReifier.setReified(null);
		if (reifier != null) reifier.setReified(this);
	}

  // -----------------------------------------------------------------------------
  // Misc. methods
  // -----------------------------------------------------------------------------

  protected void fireEvent(String event, Object new_value, Object old_value) {
    if (parent == null || parent.parent == null) return;
    else topicmap.processEvent(this, event, new_value, old_value);
  }

  protected boolean isConnected() {
    if (parent != null && parent.parent != null)
      return true;
    else
      return false;
  }

  public String toString() {
    return ObjectStrings.toString("basic.VariantName", (VariantNameIF)this);
  }

}
