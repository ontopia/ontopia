
package net.ontopia.topicmaps.nav2.impl.basic;

import java.util.*;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.topicmaps.utils.NameGrabber;
import net.ontopia.utils.StringifierIF;
import net.ontopia.utils.GrabberStringifier;

/**
 * INTERNAL: Helper class for storing one triple consisting of
 * (Association Type, AssociationRoleType, Associations).
 *
 * @see net.ontopia.topicmaps.nav2.taglibs.TMvalue.AssociationTypeLoopTag
 */
public class AssocInfoStorage {

  // constant
  private static final StringifierIF DEF_NAME_STRINGIFIER = new CustomNameStringifier();

  // members
  private Collection associations;
  private TopicIF type;
  private TopicIF roleType;
  private String sortName;
  
  public AssocInfoStorage(TopicIF type, TopicIF roleType) {
    this.type = type;
    this.roleType = roleType;
    this.associations = null;
    this.sortName = stringify(type, roleType);
  }

  private String stringify(TopicIF type, TopicIF roleType) {
    Collection scope = Collections.singleton(roleType);
    StringifierIF grabber = new GrabberStringifier(new NameGrabber(scope),
                                                   DEF_NAME_STRINGIFIER);
    return grabber.toString(type);
  }
  
  public void setType(TopicIF type) {
    this.type = type;
  }
  
  public TopicIF getType() {
    return type;
  }
  
  public void setRoleType(TopicIF roleType) {
    this.roleType = roleType;
  }
  
  public TopicIF getRoleType() {
    return roleType;
  }

  public void setAssociations(Collection associations) {
    this.associations = associations;
  }

  /**
   * get collection of AssociationIF objects.
   */
  public Collection getAssociations() {
    return associations;
  }

  public String getSortName() {
    return sortName;
  }

  public boolean equals(Object object) {
    if (!(object instanceof AssocInfoStorage))
      return false;
    AssocInfoStorage cmp = (AssocInfoStorage) object;
    return compare(cmp.getRoleType(), roleType) &&
      compare(cmp.getType(), type);
  }

  public int hashCode() {
    return (roleType != null ? roleType.hashCode() : 0) +
      (type != null ? type.hashCode() : 0);
  }
  
  public String toString() {
    return "[AssocInfoStorage, AssocRoleType: " + getRoleType() +
      ", AssocType: " + getType() + "]";
  }

  // --- Internal methods

  private static final boolean compare(Object o1, Object o2) {
    return o1 == o2 ||
      (o1 != null && o1.equals(o2));
  }
  
}
