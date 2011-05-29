
package net.ontopia.topicmaps.impl.rdbms;

import java.util.*;
import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.utils.*;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.persistence.proxy.*;

/**
 * INTERNAL: The read-only rdbms association implementation.
 */
public class ReadOnlyAssociation extends ReadOnlyTMObject implements AssociationIF {

  // ---------------------------------------------------------------------------
  // Data members
  // ---------------------------------------------------------------------------

  public ReadOnlyAssociation() {  
  }

  // ---------------------------------------------------------------------------
  // PersistentIF implementation
  // ---------------------------------------------------------------------------

  public int _p_getFieldCount() {
    return Association.fields.length;
  }

  // ---------------------------------------------------------------------------
  // TMObjectIF implementation
  // ---------------------------------------------------------------------------

  public String getClassIndicator() {
    return Association.CLASS_INDICATOR;
  }

  public String getObjectId() {
    return (id == null ? null : Association.CLASS_INDICATOR + id.getKey(0));
  }
  
  // ---------------------------------------------------------------------------
  // AssociationIF implementation
  // ---------------------------------------------------------------------------

  public Collection<TopicIF> getRoleTypes() {
    Collection<TopicIF> result = new CompactHashSet<TopicIF>();
    for (AssociationRoleIF role : (Collection<AssociationRoleIF>)
           loadCollectionField(Association.LF_roles)) {
      TopicIF type = role.getType();
      if (type != null)
        result.add(role.getType());
    }
    return result;
  }
  
  public Collection<AssociationRoleIF> getRolesByType(TopicIF roletype) {
    Collection<AssociationRoleIF> result = new CompactHashSet<AssociationRoleIF>();
    for (AssociationRoleIF role : (Collection<AssociationRoleIF>)
           loadCollectionField(Association.LF_roles)) {
      if (role.getType() == roletype)
        result.add(role);
    }
    return result;
  }

  public Collection<AssociationRoleIF> getRoles() {
    try {
      return (Collection<AssociationRoleIF>) loadCollectionField(Association.LF_roles);
    } catch (IdentityNotFoundException e) {
      // association has been deleted by somebody else, so return empty set
      return Collections.EMPTY_SET;
    }
  }

  void addRole(AssociationRoleIF assoc_role) {
    throw new ReadOnlyException();
  }

  void removeRole(AssociationRoleIF assoc_role) {
    throw new ReadOnlyException();
  }

  // ---------------------------------------------------------------------------
  // ScopedIF implementation
  // ---------------------------------------------------------------------------

  public Collection<TopicIF> getScope() {
    return (Collection<TopicIF>) loadCollectionField(Association.LF_scope);
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
    try {
      return (TopicIF)loadField(Association.LF_type);
    } catch (IdentityNotFoundException e) {
      // association has been deleted by somebody else, so return null
      return null;
    }
  }

  public void setType(TopicIF type) {
    throw new ReadOnlyException();
  }
  
  // ---------------------------------------------------------------------------
  // ReifiableIF implementation
  // ---------------------------------------------------------------------------

  public TopicIF getReifier() {
    try {
      return (TopicIF)loadField(Association.LF_reifier);
    } catch (IdentityNotFoundException e) {
      // association has been deleted by somebody else, so return null
      return null;
    }
  }
  
  public void setReifier(TopicIF reifier) {
    throw new ReadOnlyException();
  }

  // ---------------------------------------------------------------------------
  // Misc. methods
  // ---------------------------------------------------------------------------
  
  public String toString() {
    return ObjectStrings.toString("rdbms.ReadOnlyAssociation", (AssociationIF) this);
  }
  
}
