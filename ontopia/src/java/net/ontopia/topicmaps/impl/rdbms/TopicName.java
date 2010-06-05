
// $Id: TopicName.java,v 1.18 2008/06/12 14:37:15 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.rdbms;

import java.util.*;
import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.utils.*;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.persistence.proxy.*;
  
/**
 * INTERNAL: The rdbms topic name implementation.
 */

public class TopicName extends TMObject implements TopicNameIF {
  
  // ---------------------------------------------------------------------------
  // Persistent property declarations
  // ---------------------------------------------------------------------------

  protected static final int LF_topic = 2;
  protected static final int LF_scope = 3;
  protected static final int LF_type = 4;
  protected static final int LF_value = 5;
  protected static final int LF_variants = 6;
  protected static final int LF_reifier = 7;
  protected static final String[] fields = {"sources", "topicmap", "topic",
                                            "scope", "type", "value",
                                            "variants", "reifier"};

  public void detach() {
    detachCollectionField(LF_sources);
    detachField(LF_topicmap);
    detachField(LF_topic);
    detachField(LF_reifier);
    detachCollectionField(LF_scope);
    detachField(LF_type);
    detachField(LF_value);
    detachCollectionField(LF_variants);
  }

  // ---------------------------------------------------------------------------
  // Data members
  // ---------------------------------------------------------------------------

  public static final String CLASS_INDICATOR = "B";

  public TopicName() {
  }
  
  public TopicName(TransactionIF txn) {
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
  // NameIF implementation
  // ---------------------------------------------------------------------------
  
  public TopicIF getTopic() {
    return (TopicIF)loadField(LF_topic);
  }

  /**
   * INTERNAL: Set the topic that the topic name belongs to. [parent]
   */
  void setTopic(TopicIF topic) {
    // Set parent topic map
    setTopicMap((topic == null ? null : (TopicMap)topic.getTopicMap()));
    // Notify transaction
    valueChanged(LF_topic, topic, true);
  }

  void setTopicMap(TopicMap topicmap) {
    // Notify transaction
    transactionChanged(topicmap);
    valueChanged(LF_topicmap, topicmap, true);

    // Inform variants
    Collection variants = loadCollectionField(LF_variants);
    Iterator iter = variants.iterator();
    while (iter.hasNext()) {
      ((VariantName)iter.next()).setTopicMap(topicmap);
    }
  }
  
  public String getValue() {
    return (String)loadField(LF_value);    
  }
  
  public void setValue(String value) {
    if (value == null)
      throw new NullPointerException("Topic name value must not be null.");
    // Notify listeners
    fireEvent("TopicNameIF.setValue", value, getValue());
    // Notify transaction
    valueChanged(LF_value, value, true);
  }

  public Collection<VariantNameIF> getVariants() {
    return loadCollectionField(LF_variants);
  }

  void addVariant(VariantNameIF variant) {
    if (variant == null)
      throw new NullPointerException("null is not a valid argument.");
    // Check to see if variant is already a member of this topic name
    if (variant.getTopicName() == this)
      return;
    // Check if used elsewhere.
    if (variant.getTopicName() != null)
      throw new ConstraintViolationException("Moving objects is not allowed.");
    
    // Notify listeners
    fireEvent("TopicNameIF.addVariant", variant, null);    
    // Set parent name property
    ((VariantName)variant).setTopicName(this);
    // Notify transaction
    valueAdded(LF_variants, variant, false);

    // Add inherited themes to variant name
    for (TopicIF theme : getScope())
      ((VariantName)variant)._addTheme(theme, false);
  }

  void removeVariant(VariantNameIF variant) {
    if (variant == null)
      throw new NullPointerException("null is not a valid argument.");
    // Check to see if variant is not a member of this topic name
    if (variant.getTopicName() != this)
      return;
    
    // Notify listeners
    fireEvent("TopicNameIF.removeVariant", null, variant);

    // Remove inherited themes from variant name
    for (TopicIF theme : getScope())
      ((VariantName)variant)._removeTheme(theme, false);
    
    // Unset parent name property
    ((VariantName)variant).setTopicName(null);
    // Notify transaction
    valueRemoved(LF_variants, variant, false);
  }

  public void remove() {
    Topic parent = (Topic)getTopic();
    if (parent != null) {
      DeletionUtils.removeDependencies(this);
      parent.removeTopicName(this);
    }
  }

  // ---------------------------------------------------------------------------
  // ScopedIF implementation
  // ---------------------------------------------------------------------------

  public Collection<TopicIF> getScope() {
    return loadCollectionField(LF_scope);
  }

  public void addTheme(TopicIF theme) {
    if (theme == null)
      throw new NullPointerException("null is not a valid argument.");
    CrossTopicMapException.check(theme, this);    
    // Notify listeners
    fireEvent("TopicNameIF.addTheme", theme, null);
    // Notify transaction
    valueAdded(LF_scope, theme, true);

    // add theme to variants
    Collection variants = getVariants();
    if (!variants.isEmpty()) {
      Iterator iter = variants.iterator();
      while (iter.hasNext()) {
        VariantName v = (VariantName)iter.next();
        v._addTheme(theme, false);
      }
    }
  }

  public void removeTheme(TopicIF theme) {
    if (theme == null)
      throw new NullPointerException("null is not a valid argument.");
    CrossTopicMapException.check(theme, this);
    // Notify listeners
    fireEvent("TopicNameIF.removeTheme", null, theme);

    // remove theme from variants
    Collection variants = getVariants();
    if (!variants.isEmpty()) {
      Iterator iter = variants.iterator();
      while (iter.hasNext()) {
        VariantName v = (VariantName)iter.next();
        v._removeTheme(theme, false);
      }
    }
    
    // Notify transaction
    valueRemoved(LF_scope, theme, true);
  }

  // ---------------------------------------------------------------------------
  // TypedIF implementation
  // ---------------------------------------------------------------------------

  public TopicIF getType() {
    return (TopicIF)loadField(LF_type);
  }

  public void setType(TopicIF type) {
    if (type != null)
      CrossTopicMapException.check(type, this);
    // Notify listeners
    fireEvent("TopicNameIF.setType", type, getType());
    // Notify transaction
    valueChanged(LF_type, type, true);
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
    return ObjectStrings.toString("rdbms.TopicName", (TopicNameIF)this);
  }

}
