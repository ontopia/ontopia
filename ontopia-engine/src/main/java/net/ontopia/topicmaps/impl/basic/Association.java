
package net.ontopia.topicmaps.impl.basic;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.CrossTopicMapException;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.impl.utils.DeletionUtils;
import net.ontopia.topicmaps.impl.utils.ObjectStrings;
import net.ontopia.utils.UniqueSet;

/**
 * INTERNAL: The basic association implementation.
 */

public class Association extends TMObject implements AssociationIF {

  static final long serialVersionUID = -8986947932370957132L;
  
  protected TopicIF reifier;
  protected TopicIF type;
  protected UniqueSet<TopicIF> scope;
  protected Set<AssociationRoleIF> roles;
  
  protected Association(TopicMap tm) {
    super(tm);
    roles = topicmap.cfactory.makeSmallSet();
  }
  
  // -----------------------------------------------------------------------------
  // AssociationIF implementation
  // -----------------------------------------------------------------------------
  
  /**
   * INTERNAL: Sets the topic map that the object belongs to. [parent]
   */
  void setTopicMap(TopicMap parent) {
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

  public Collection<TopicIF> getRoleTypes() {
    Collection<TopicIF> result = topicmap.cfactory.makeSmallSet();
    synchronized (roles) {    
      Iterator<AssociationRoleIF> iter = roles.iterator();
      while (iter.hasNext()) {
        AssociationRoleIF role = iter.next();
        TopicIF type = role.getType();
        if (type != null)
          result.add(role.getType());
      }
    }
    return result;
  }
  
  public Collection<AssociationRoleIF> getRolesByType(TopicIF roletype) {
    if (roletype == null) throw new NullPointerException("Role type must not be null.");
    CrossTopicMapException.check(roletype, this);
    Collection<AssociationRoleIF> result = topicmap.cfactory.makeSmallSet();
    synchronized (roles) {
      Iterator<AssociationRoleIF> iter = roles.iterator();
      while (iter.hasNext()) {
        AssociationRoleIF role = iter.next();
        if (role.getType() == roletype)
          result.add(role);
      }
    }
    return result;
  }

  public Collection<AssociationRoleIF> getRoles() {
    return Collections.unmodifiableSet(roles);
  }

  void addRole(AssociationRoleIF _assoc_role) {
    AssociationRole assoc_role = (AssociationRole)_assoc_role;
    if (assoc_role == null)
      throw new NullPointerException(MSG_NULL_ARGUMENT);
    // Check to see if association role is already a member of this association
    if (assoc_role.parent == this)
      return;
    // Check if used elsewhere.
    if (assoc_role.parent != null)
      throw new ConstraintViolationException("Moving objects is not allowed.");
    // Notify listeners
    fireEvent("AssociationIF.addRole", assoc_role, null);    
    // Set association property
    assoc_role.setAssociation(this);
    // Add association role to list of association roles
    roles.add(assoc_role);
    // Make sure role is added to player's list
    Topic player = (Topic) assoc_role.getPlayer();
    if (player != null && parent != null)
      player.addRole(assoc_role);
  }

  void removeRole(AssociationRoleIF _assoc_role) {
    AssociationRole assoc_role = (AssociationRole)_assoc_role;
    if (assoc_role == null)
      throw new NullPointerException(MSG_NULL_ARGUMENT);
    // Check to see if association role is not a member of this association
    if (assoc_role.parent != this)
      return;
    // Notify listeners
    fireEvent("AssociationIF.removeRole", null, assoc_role);
    // Remove role from list of roles
    roles.remove(assoc_role);
    // Removing role from player's list of roles
    Topic player = (Topic) assoc_role.getPlayer();
    if (player != null && parent != null)
      player.removeRole(assoc_role);
    // Unset association property
    assoc_role.setAssociation(null);
  }

  public void remove() {
    if (topicmap != null) {
      DeletionUtils.removeDependencies(this);
      topicmap.removeAssociation(this);
    }
  }

  // -----------------------------------------------------------------------------
  // ScopedIF implementation
  // -----------------------------------------------------------------------------

  public Collection<TopicIF> getScope() {
    // Return scope defined on this object
    Collection<TopicIF> empty = Collections.emptyList();
    return (scope == null ? empty : scope);
  }

  public void addTheme(TopicIF theme) {
    if (theme == null)
      throw new NullPointerException(MSG_NULL_ARGUMENT);
    CrossTopicMapException.check(theme, this);
    // Notify listeners
    fireEvent("AssociationIF.addTheme", theme, null);
    // Add theme to scope
    if (scope == null) {
      Set<TopicIF> empty = Collections.emptySet();
      scope = topicmap.setpool.get(empty);
    }
    scope = topicmap.setpool.add(scope, theme, true);
  }

  public void removeTheme(TopicIF theme) {
    if (theme == null)
      throw new NullPointerException(MSG_NULL_ARGUMENT);
    CrossTopicMapException.check(theme, this);
    // Notify listeners
    fireEvent("AssociationIF.removeTheme", null, theme);
    // Remove theme from scope
    if (scope == null)
      return;
    scope = topicmap.setpool.remove(scope, theme, true);
  }

  // -----------------------------------------------------------------------------
  // TypedIF implementation
  // -----------------------------------------------------------------------------

  public TopicIF getType() {
    return type;
  }

  public void setType(TopicIF type) {
    if (type == null) throw new NullPointerException("Association type must not be null.");
    CrossTopicMapException.check(type, this);
    // Notify listeners
    fireEvent("AssociationIF.setType", type, getType());
    this.type = type;
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
  
  public String toString() {
    return ObjectStrings.toString("basic.Association", (AssociationIF)this);
  }
  
}
