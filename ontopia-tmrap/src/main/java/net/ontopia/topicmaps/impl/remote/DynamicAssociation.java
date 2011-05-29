
package net.ontopia.topicmaps.impl.remote;

import java.util.Collection;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.basic.Association;
import net.ontopia.topicmaps.impl.basic.TopicMap;

/**
 * INTERNAL: PRIVATE: EXPERIMENTAL: Description: Dynamic proxy for associations
 */

public class DynamicAssociation extends Association implements AssociationIF {

  /**
   * @param tm
   */
  DynamicAssociation(TopicMap tm) {

    super(tm);

  }

  // This should be AssociationIF when this class becomes a proper proxy
  Association target;

  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.topicmaps.core.AssociationIF#getRoleTypes()
   */
  public Collection getRoleTypes() {

    if (target == null) return super.getRoleTypes();
    return target.getRoleTypes();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.topicmaps.core.AssociationIF#getRolesByType(net.ontopia.topicmaps.core.TopicIF)
   */
  public Collection getRolesByType(TopicIF roletype) {

    if (target == null) return super.getRolesByType(roletype);
    return target.getRolesByType(roletype);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.topicmaps.core.AssociationIF#getRoles()
   */
  public Collection getRoles() {

    if (target == null) return super.getRoles();
    return target.getRoles();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.topicmaps.core.ScopedIF#getScope()
   */
  public Collection getScope() {

    if (target == null) return super.getScope();
    return target.getScope();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.topicmaps.core.ScopedIF#addTheme(net.ontopia.topicmaps.core.TopicIF)
   */
  public void addTheme(TopicIF theme) {

    if (target == null) super.addTheme(theme);
    else target.addTheme(theme);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.topicmaps.core.ScopedIF#removeTheme(net.ontopia.topicmaps.core.TopicIF)
   */
  public void removeTheme(TopicIF theme) {

    if (target == null) super.removeTheme(theme);
    else target.removeTheme(theme);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.topicmaps.core.TypedIF#getType()
   */
  public TopicIF getType() {

    if (target == null) return super.getType();
    return target.getType();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.topicmaps.core.TypedIF#setType(net.ontopia.topicmaps.core.TopicIF)
   */
  public void setType(TopicIF type) {

    if (target == null) super.setType(type);
    else target.setType(type);
  }

  public String toString() {

    if (target == null) return super.toString();
    return "{" + target.toString() + "}";

  }

  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.topicmaps.core.TMObjectIF#getObjectId()
   */
  public String getObjectId() {

    if (target == null) return super.getObjectId();
    return target.getObjectId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.topicmaps.core.TMObjectIF#isReadOnly()
   */
  public boolean isReadOnly() {

    if (target == null) return super.isReadOnly();
    return target.isReadOnly();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.topicmaps.core.TMObjectIF#getTopicMap()
   */
  public TopicMapIF getTopicMap() {

    if (target == null) return super.getTopicMap();
    return target.getTopicMap();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.topicmaps.core.TMObjectIF#getItemIdentifiers()
   */
  public Collection getItemIdentifiers() {

    if (target == null) return super.getItemIdentifiers();
    return target.getItemIdentifiers();
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.topicmaps.core.TMObjectIF#addItemIdentifier(net.ontopia.infoset.core.LocatorIF)
   */
  public void addItemIdentifier(LocatorIF source_locator)
      throws ConstraintViolationException {

    if (target == null) super.addItemIdentifier(source_locator);
    else target.addItemIdentifier(source_locator);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.ontopia.topicmaps.core.TMObjectIF#removeItemIdentifier(net.ontopia.infoset.core.LocatorIF)
   */
  public void removeItemIdentifier(LocatorIF source_locator) {

    if (target == null) super.removeItemIdentifier(source_locator);
    else target.removeItemIdentifier(source_locator);
  }

  public void remove() {
    if (target == null) super.remove();
    target.remove();
  }
  
  public boolean equals(Object obj) {

    if (obj instanceof DynamicAssociation) {
      if (target == null) return ((DynamicAssociation) obj).equals(this);
      return obj.equals(target);
    }

    if (target == null) return super.equals(obj);
    return target.equals(obj);

  }

  public boolean equals(DynamicAssociation obj) {

    if (target == null) return super.equals(obj);
    return target.equals(obj);

  }

  public int hashCode() {

    if (target == null) return super.hashCode();
    return target.hashCode();
  }

  public void setTarget(AssociationIF newTarget) {

    // This should be an interface, but at present that is not possable.
    // So I must cast !!!
    
    this.target = (Association) newTarget;
  }

  protected boolean isConnected() {

    if (target == null) return super.isConnected();
    
    // IDM this cast to DynamicAssociation should not be
    // necessary and is not strictly correct, but #isConnected() 
    // is defined as protected on TMObject
    return ((DynamicAssociation)target).isConnected();
  }
}
