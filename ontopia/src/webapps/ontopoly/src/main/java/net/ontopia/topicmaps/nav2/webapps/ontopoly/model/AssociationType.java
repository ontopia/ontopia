// $Id: AssociationType.java,v 1.8 2009/05/12 20:26:26 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.webapps.ontopoly.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.utils.OntopolyModelUtils;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.RowMapperIF;
import net.ontopia.topicmaps.utils.ObjectIdComparator;
import net.ontopia.utils.ObjectUtils;

/**
 * Represents an association type.
 */
public class AssociationType extends AbstractTypingTopic {

  /**
   * Creates a new AssociationType object.
   */
  public AssociationType(TopicIF currTopic, TopicMap tm) {
    super(currTopic, tm);
  }

  @Override
  public LocatorIF getLocatorIF() {
    return PSI.ON_ASSOCIATION_TYPE;
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof AssociationType))
      return false;

    AssociationType other = (AssociationType) obj;
    return (getTopicIF().equals(other.getTopicIF()));
  }

  /**
   * Indicates whether the association type is symmetric.
   */
  public boolean isSymmetric() {
    TopicMap tm = getTopicMap();
    TopicIF aType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "is-symmetric");
    TopicIF rType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "association-type");
    TopicIF player = getTopicIF();
    return OntopolyModelUtils.isUnaryPlayer(tm, aType, player, rType);
//    String query = "on:is-symmetric(%assocTopic% : on:association-type)?";
//    Map params = Collections.singletonMap("assocTopic", getTopicIF());
//    return getTopicMap().getQueryWrapper().isTrue(query, params);
  }

//  /**
//   * Decides whether this AssociationType object is going to be a symmetric
//   * association.
//   * 
//   * @param value
//   *            value indicates whether this AssociationType object is going to
//   *            be a symmetric association.
//   */
//  public void setSymmetric(boolean value) {
//    TopicMap tm = getTopicMap();
//    TopicIF aType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "is-symmetric");
//    TopicIF rType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "association-type");
//
//    AssociationIF assoc = OntopolyModelUtils.findUnaryAssociation(tm, aType,
//        getTopicIF(), rType);
//
//    if (value && assoc == null) 
//      OntopolyModelUtils.makeUnaryAssociation(aType, getTopicIF(), rType);
//    else if (!value && assoc != null)
//      assoc.remove();
//  }

  /**
   * Tests whether this association type is hierarchical.
   * 
   * @return true if this is association type is hierarchical.
   */
  public boolean isHierarchical() {
    TopicIF hierarchicalRelationType = 
			OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.TECH, "#hierarchical-relation-type");
    return getTopicIF().getTypes().contains(hierarchicalRelationType);
  }

//  /**
//   * Registers this association type as being hierarchical.
//   * 
//   * @param value true if association type should be hierarchical.
//   */
//  public void setHierarchical(boolean value) {
//    TopicIF hierarchicalRelationType = 
//			OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.TECH, "#hierarchical-relation-type");
//
//    boolean isHierarchical = isHierarchical();
//    if (value && !isHierarchical)
//      getTopicIF().addType(hierarchicalRelationType);
//    else if (!value && isHierarchical)
//      getTopicIF().removeType(hierarchicalRelationType);
//  }

	public Collection getDeclaredByFields() {
    String query = "select $AF, $RF, $RT from "
			+ "on:has-association-type(%AT% : on:association-type, $AF : on:association-field), "
			+ "on:has-association-field($AF : on:association-field, $RF : on:role-field), "
			+ "on:has-role-type($RF : on:role-field, $RT : on:role-type)?";
    Map params = Collections.singletonMap("AT", getTopicIF());

    return getTopicMap().getQueryWrapper().queryForList(query,
        new RowMapperIF() {
          public Object mapRow(QueryResultIF result, int rowno) {
						TopicIF associationFieldTopic = (TopicIF)result.getValue(0);
						TopicIF roleFieldTopic = (TopicIF)result.getValue(1);
						TopicIF roleType = (TopicIF)result.getValue(2);
						return new RoleField(roleFieldTopic, getTopicMap(), new RoleType(roleType, getTopicMap()), new AssociationField(associationFieldTopic, getTopicMap()));
					}
				}, params);
	}

//	public Collection getUsedBy() {
//    String query = "select $TT from "
//			+ "on:has-association-type(%AT% : on:association-type, $AF : on:role-field), "
//			+ "on:has-field($FD : on:field-definition, $TT : on:field-owner)?";
//    Map params = Collections.singletonMap("AT", getTopicIF());
//
//    return getTopicMap().getQueryWrapper().queryForList(query,
//        new RowMapperIF() {
//          public Object mapRow(QueryResultIF result, int rowno) {
//						TopicIF topicType = (TopicIF)result.getValue(0);
//						return new TopicType(topicType, getTopicMap());
//					}
//				}, params);
//	}

	/**
	 * Returns all role types that have been declared for this association type.
	 * @return list of role types
	 */
  public List getDeclaredRoleTypes() {
    List result = new ArrayList();
    AssociationField associationField = null;
    Collection roleFields = this.getDeclaredByFields();
    Iterator iter = roleFields.iterator();
    while (iter.hasNext()) {
      RoleField roleField = (RoleField)iter.next();
      if (associationField == null)
        associationField = roleField.getAssociationField();
      else if (!associationField.equals(roleField.getAssociationField()))
        continue;
      result.add(roleField.getRoleType());
    }
    // duplicate single role type if association type is symmetric
    if (result.size() == 1 && isSymmetric())
      result.add(result.get(0));    
    return result;
  }

	/**
	 * Returns a collection of lists that contain the role type combinations that have been used in actual associations. The RoleTypes are sorted by object id.
	 * 
	 * @return Collection<List<RoleType>>
	 */
	public Collection getUsedRoleTypeCombinations() {
	  Collection result = new HashSet();
	  
	  TopicIF associationType = getTopicIF();
	  ClassInstanceIndexIF cindex = (ClassInstanceIndexIF)associationType.getTopicMap().getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");
	  Iterator iter = cindex.getAssociations(associationType).iterator();

	  List tuple = new ArrayList();
	  while (iter.hasNext()) {
	    AssociationIF assoc = (AssociationIF)iter.next();
	    Iterator riter = assoc.getRoles().iterator();
	    while (riter.hasNext()) {
	      AssociationRoleIF role = (AssociationRoleIF)riter.next();
	      tuple.add(role.getType());
	    }
	    Collections.sort(tuple, ObjectIdComparator.INSTANCE);
	    if (result.contains(tuple)) {
	      tuple.clear();
	    } else {
	      result.add(tuple);
	      tuple = new ArrayList();
	    }	     
	  }
	  
	  // replace TopicIFs with RoleTypes
	  iter = result.iterator();
	  while (iter.hasNext()) {
	    List combo = (List)iter.next();
	    int size = combo.size();
	    for (int i=0; i < size; i++) {
	      TopicIF rtype = (TopicIF)combo.get(i);
	      combo.set(i, new RoleType(rtype, getTopicMap()));
	    }
	  }
	  return result;
	}
  
	/**
	 * Transforms associations from the role types of the given form to the new one as given.
	 * @param roleTypesFrom list of role types that should match existing associations
	 * @param roleTypesTo list of role types to which the associations should be changed
	 */
	public void transformInstances(List roleTypesFrom, List roleTypesTo) {
	  int size = roleTypesFrom.size(); 
	  if (size != roleTypesTo.size())
	    throw new RuntimeException("Incompatible role type sets: sizes are different");
	  
    TopicIF associationType = getTopicIF();
    ClassInstanceIndexIF cindex = (ClassInstanceIndexIF)associationType.getTopicMap().getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");
    Iterator iter = cindex.getAssociations(associationType).iterator();
    
    AssociationRoleIF[] roleMatches = new AssociationRoleIF[size];
    
    // for each association
    while (iter.hasNext()) {
      AssociationIF assoc = (AssociationIF)iter.next();
      Collection roles = assoc.getRoles();
      if (roles.size() != roleTypesFrom.size()) continue;
      boolean match = true;
      Arrays.fill(roleMatches, null);
      
      Iterator riter = roles.iterator();
      while (riter.hasNext()) {
        AssociationRoleIF role = (AssociationRoleIF)riter.next();
        int matchIndex = -1;
        TopicIF roleType = role.getType();
        for (int i=0; i < size; i++) {          
          if (roleMatches[i] == null) {
            RoleType fromType = (RoleType)roleTypesFrom.get(i);
            if (fromType.getTopicIF().equals(roleType)) {
              matchIndex = i;
              roleMatches[i] = role;
            }
          }
        }
        if (matchIndex == -1) {
          match = false;
          break;
        }
      }
      if (match) {
//        System.out.println("Match: " + assoc);
        for (int i=0; i < size; i++) {
          AssociationRoleIF role = (AssociationRoleIF)roleMatches[i];
          RoleType fromType = (RoleType)roleTypesFrom.get(i);
          RoleType toType = (RoleType)roleTypesTo.get(i);
          if (role.getType().equals(fromType.getTopicIF())) {
            if (!role.getType().equals(toType.getTopicIF())) role.setType(toType.getTopicIF());
          } else {
//            System.out.println("OOPS: " + role + " " + fromType + " " + toType);
          }
        }        
      }
    }
  }
  
  static class RoleFieldComparator implements Comparator {
    private static final RoleFieldComparator INSTANCE = new RoleFieldComparator();

    private RoleFieldComparator() {
      super();
    }

    public static RoleFieldComparator getInstance() {
      return INSTANCE;
    }

    public int compare(Object o1, Object o2) {
      RoleField rf1 = (RoleField) o1;
      RoleField rf2 = (RoleField) o2;

      return ObjectUtils.compare(rf1.getFieldName(), rf2.getFieldName());
    }
  }

}
