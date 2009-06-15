
// $Id: ReadOnlyTopicName.java,v 1.1 2008/06/12 14:37:15 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.rdbms;

import java.util.*;
import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.utils.*;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.persistence.proxy.*;
  
/**
 * INTERNAL: The read-only rdbms topic name implementation.
 */

public class ReadOnlyTopicName extends ReadOnlyTMObject implements TopicNameIF {

  // -----------------------------------------------------------------------------
  // Data members
  // -----------------------------------------------------------------------------

  public ReadOnlyTopicName() {
  }

  // -----------------------------------------------------------------------------
  // PersistentIF implementation
  // -----------------------------------------------------------------------------

  public int _p_getFieldCount() {
    return TopicName.fields.length;
  }
  
  // -----------------------------------------------------------------------------
  // TMObjectIF implementation
  // -----------------------------------------------------------------------------

  public String getClassIndicator() {
    return TopicName.CLASS_INDICATOR;
  }

  public String getObjectId() {
    return (id == null ? null : TopicName.CLASS_INDICATOR + id.getKey(0));
  }
  
  // -----------------------------------------------------------------------------
  // NameIF implementation
  // -----------------------------------------------------------------------------
  
  public TopicIF getTopic() {
    return (TopicIF)loadField(TopicName.LF_topic);
  }
  
  public String getValue() {
    return (String)loadField(TopicName.LF_value);    
  }
  
  public void setValue(String value) {
    throw new ReadOnlyException();
  }

  public Collection getVariants() {
    return loadCollectionField(TopicName.LF_variants);
  }

  void addVariant(VariantNameIF variant) {
    throw new ReadOnlyException();
  }

  void removeVariant(VariantNameIF variant) {
    throw new ReadOnlyException();
  }

  // -----------------------------------------------------------------------------
  // ScopedIF implementation
  // -----------------------------------------------------------------------------

  public Collection getScope() {
    return loadCollectionField(TopicName.LF_scope);
  }

  public void addTheme(TopicIF theme) {
    throw new ReadOnlyException();
  }

  public void removeTheme(TopicIF theme) {
    throw new ReadOnlyException();
  }

  // -----------------------------------------------------------------------------
  // TypedIF implementation
  // -----------------------------------------------------------------------------

  public TopicIF getType() {
    return (TopicIF)loadField(TopicName.LF_type);
  }

  public void setType(TopicIF type) {
    throw new ReadOnlyException();
  }
  
  // -----------------------------------------------------------------------------
  // ReifiableIF implementation
  // -----------------------------------------------------------------------------

  public TopicIF getReifier() {
		return (TopicIF)loadField(TopicName.LF_reifier);
	}
  
  public void setReifier(TopicIF reifier) {
    throw new ReadOnlyException();
	}

  // -----------------------------------------------------------------------------
  // Misc. methods
  // -----------------------------------------------------------------------------

  public String toString() {
    return ObjectStrings.toString("rdbms.ReadOnlyTopicName", (TopicNameIF)this);
  }

}
