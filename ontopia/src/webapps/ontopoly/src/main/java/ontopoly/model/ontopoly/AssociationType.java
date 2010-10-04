
package ontopoly.model.ontopoly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ontopoly.model.PSI;
import ontopoly.model.RoleTypeIF;
import ontopoly.model.OntopolyTopicMapIF;
import ontopoly.model.AssociationTypeIF;
import ontopoly.utils.OntopolyModelUtils;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.RowMapperIF;
import net.ontopia.topicmaps.utils.ObjectIdComparator;
import net.ontopia.utils.ObjectUtils;

/**
 * Represents an association type.
 */
public class AssociationType extends AbstractTypingTopic
  implements AssociationTypeIF {

  /**
   * Creates a new AssociationType object.
   */
  public AssociationType(TopicIF currTopic, OntopolyTopicMapIF tm) {
    super(currTopic, tm);
  }

  @Override
  public LocatorIF getLocatorIF() {
    return PSI.ON_ASSOCIATION_TYPE;
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof AssociationType))
      return false;

    AssociationTypeIF other = (AssociationTypeIF) obj;
    return (getTopicIF().equals(other.getTopicIF()));
  }

  /**
   * Indicates whether the association type is symmetric.
   */
  public boolean isSymmetric() {
    OntopolyTopicMapIF tm = getTopicMap();
    TopicIF aType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "is-symmetric");
    TopicIF rType = OntopolyModelUtils.getTopicIF(tm, PSI.ON, "association-type");
    TopicIF player = getTopicIF();
    return OntopolyModelUtils.isUnaryPlayer(tm, aType, player, rType);
  }

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

  @Override
  public Collection<RoleFieldIF> getDeclaredByFields() {
    String query = "select $AF, $RF, $RT from "
      + "on:has-association-type(%AT% : on:association-type, $AF : on:association-field), "
      + "on:has-association-field($AF : on:association-field, $RF : on:role-field), "
      + "on:has-role-type($RF : on:role-field, $RT : on:role-type)?";
    Map<String,TopicIF> params = Collections.singletonMap("AT", getTopicIF());

    QueryMapper<RoleFieldIF> qm = getTopicMap().newQueryMapper(RoleField.class);
    return qm.queryForList(query,
        new RowMapperIF<RoleFieldIF>() {
          public RoleFieldIF mapRow(QueryResultIF result, int rowno) {
            TopicIF associationFieldTopic = (TopicIF)result.getValue(0);
            TopicIF roleFieldTopic = (TopicIF)result.getValue(1);
            TopicIF roleType = (TopicIF)result.getValue(2);
            return new RoleField(roleFieldTopic, getTopicMap(), new RoleType(roleType, getTopicMap()), new AssociationField(associationFieldTopic, getTopicMap()));
          }
                           }, params);
	}

  /**
   * Returns all role types that have been declared for this association type.
   * @return list of role types
   */
  public List<RoleTypeIF> getDeclaredRoleTypes() {
    List<RoleTypeIF> result = new ArrayList<RoleTypeIF>();
    AssociationField associationField = null;
    Collection roleFields = this.getDeclaredByFields();
    Iterator iter = roleFields.iterator();
    while (iter.hasNext()) {
      RoleFieldIF roleField = (RoleFieldIF)iter.next();
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
   * Returns a collection of lists that contain the role type
   * combinations that have been used in actual associations. The
   * RoleTypes are sorted by object id.
   */
  public Collection<List<RoleTypeIF>> getUsedRoleTypeCombinations() {
    Collection<List<RoleTypeIF>> result = new HashSet<List<RoleTypeIF>>();
	  
    TopicIF associationType = getTopicIF();
    ClassInstanceIndexIF cindex = (ClassInstanceIndexIF)associationType.getTopicMap().getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");
    Iterator iter = cindex.getAssociations(associationType).iterator();
    
    List<RoleTypeIF> tuple = new ArrayList<RoleTypeIF>();
    while (iter.hasNext()) {
      AssociationIF assoc = (AssociationIF)iter.next();
      Iterator riter = assoc.getRoles().iterator();
      while (riter.hasNext()) {
        AssociationRoleIF role = (AssociationRoleIF)riter.next();
        tuple.add(new RoleType(role.getType(), getTopicMap()));
      }
      Collections.sort(tuple, new Comparator<RoleTypeIF>() {
          public int compare(RoleTypeIF o1, RoleTypeIF o2) {
            return ObjectIdComparator.INSTANCE.compare(o1.getTopicIF(), o2.getTopicIF());
          }
        });
      if (result.contains(tuple)) {
        tuple.clear();
      } else {
        result.add(tuple);
        tuple = new ArrayList<RoleTypeIF>();
      }	     
    }
    return result;
  }
  
  /**
   * Transforms associations from the role types of the given form to
   * the new one as given.
   * @param roleTypesFrom list of role types that should match
   * existing associations
   * @param roleTypesTo list of role types to which the associations
   * should be changed
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
            RoleTypeIF fromType = (RoleTypeIF)roleTypesFrom.get(i);
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
        for (int i=0; i < size; i++) {
          AssociationRoleIF role = (AssociationRoleIF)roleMatches[i];
          RoleTypeIF fromType = (RoleTypeIF)roleTypesFrom.get(i);
          RoleTypeIF toType = (RoleTypeIF)roleTypesTo.get(i);
          if (role.getType().equals(fromType.getTopicIF())) {
            if (!role.getType().equals(toType.getTopicIF())) role.setType(toType.getTopicIF());
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
      RoleFieldIF rf1 = (RoleFieldIF) o1;
      RoleFieldIF rf2 = (RoleFieldIF) o2;

      return ObjectUtils.compare(rf1.getFieldName(), rf2.getFieldName());
    }
  }

}
