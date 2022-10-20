/*
 * #!
 * Ontopoly Editor
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package ontopoly.model;

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
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.RowMapperIF;
import net.ontopia.topicmaps.utils.ObjectIdComparator;
import ontopoly.utils.OntopolyModelUtils;
import org.apache.commons.lang3.StringUtils;

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

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof AssociationType)) {
      return false;
    }

    AssociationType other = (AssociationType) obj;
    return (getTopicIF().equals(other.getTopicIF()));
  }

  /**
   * Indicates whether the association type is symmetric.
   */
  public boolean isSymmetric() {
    TopicMap tm = getTopicMap();
    TopicIF aType = OntopolyModelUtils.getTopicIF(tm, PSI.ON_IS_SYMMETRIC);
    TopicIF rType = OntopolyModelUtils.getTopicIF(tm, PSI.ON_ASSOCIATION_TYPE);
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
			OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.TECH_HIERARCHICAL_RELATION_TYPE);
    return getTopicIF().getTypes().contains(hierarchicalRelationType);
  }

  @Override
	public Collection<RoleField> getDeclaredByFields() {
    String query = "select $AF, $RF, $RT from "
			+ "on:has-association-type(%AT% : on:association-type, $AF : on:association-field), "
			+ "on:has-association-field($AF : on:association-field, $RF : on:role-field), "
			+ "on:has-role-type($RF : on:role-field, $RT : on:role-type)?";
    Map<String,TopicIF> params = Collections.singletonMap("AT", getTopicIF());

    QueryMapper<RoleField> qm = getTopicMap().newQueryMapper(RoleField.class);
    return qm.queryForList(query,
        new RowMapperIF<RoleField>() {
          @Override
          public RoleField mapRow(QueryResultIF result, int rowno) {
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
  public List<RoleType> getDeclaredRoleTypes() {
    List<RoleType> result = new ArrayList<RoleType>();
    AssociationField associationField = null;
    Collection<RoleField> roleFields = this.getDeclaredByFields();
    Iterator<RoleField> iter = roleFields.iterator();
    while (iter.hasNext()) {
      RoleField roleField = iter.next();
      if (associationField == null) {
        associationField = roleField.getAssociationField();
      } else if (!associationField.equals(roleField.getAssociationField())) {
        continue;
      }
      result.add(roleField.getRoleType());
    }
    // duplicate single role type if association type is symmetric
    if (result.size() == 1 && isSymmetric()) {
      result.add(result.get(0));
    }    
    return result;
  }

	/**
	 * Returns a collection of lists that contain the role type combinations that have been used in actual associations. The RoleTypes are sorted by object id.
	 * 
	 * @return Collection<List<RoleType>>
	 */
	public Collection<List<RoleType>> getUsedRoleTypeCombinations() {
	  Collection<List<RoleType>> result = new HashSet<List<RoleType>>();
	  
	  TopicIF associationType = getTopicIF();
	  ClassInstanceIndexIF cindex = (ClassInstanceIndexIF)associationType.getTopicMap().getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");
	  Iterator<AssociationIF> iter = cindex.getAssociations(associationType).iterator();

	  List<RoleType> tuple = new ArrayList<RoleType>();
	  while (iter.hasNext()) {
	    AssociationIF assoc = iter.next();
	    Iterator<AssociationRoleIF> riter = assoc.getRoles().iterator();
	    while (riter.hasNext()) {
	      AssociationRoleIF role = riter.next();
	      tuple.add(new RoleType(role.getType(), getTopicMap()));
	    }
	    Collections.sort(tuple, new Comparator<RoleType>() {
        @Override
        public int compare(RoleType o1, RoleType o2) {
          return ObjectIdComparator.INSTANCE.compare(o1.getTopicIF(), o2.getTopicIF());
        }
	    });
	    if (result.contains(tuple)) {
	      tuple.clear();
	    } else {
	      result.add(tuple);
	      tuple = new ArrayList<RoleType>();
	    }	     
	  }
	  return result;
	}
  
	/**
	 * Transforms associations from the role types of the given form to the new one as given.
	 * @param roleTypesFrom list of role types that should match existing associations
	 * @param roleTypesTo list of role types to which the associations should be changed
	 */
	public void transformInstances(List<RoleType> roleTypesFrom, List<RoleType> roleTypesTo) {
	  int size = roleTypesFrom.size(); 
	  if (size != roleTypesTo.size()) {
      throw new RuntimeException("Incompatible role type sets: sizes are different");
    }
	  
    TopicIF associationType = getTopicIF();
    ClassInstanceIndexIF cindex = (ClassInstanceIndexIF)associationType.getTopicMap().getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");
    Iterator<AssociationIF> iter = cindex.getAssociations(associationType).iterator();
    
    AssociationRoleIF[] roleMatches = new AssociationRoleIF[size];
    
    // for each association
    while (iter.hasNext()) {
      AssociationIF assoc = iter.next();
      Collection<AssociationRoleIF> roles = assoc.getRoles();
      if (roles.size() != roleTypesFrom.size()) {
        continue;
      }
      boolean match = true;
      Arrays.fill(roleMatches, null);
      
      Iterator<AssociationRoleIF> riter = roles.iterator();
      while (riter.hasNext()) {
        AssociationRoleIF role = riter.next();
        int matchIndex = -1;
        TopicIF roleType = role.getType();
        for (int i=0; i < size; i++) {          
          if (roleMatches[i] == null) {
            RoleType fromType = roleTypesFrom.get(i);
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
          AssociationRoleIF role = roleMatches[i];
          RoleType fromType = roleTypesFrom.get(i);
          RoleType toType = roleTypesTo.get(i);
          if (role.getType().equals(fromType.getTopicIF())) {
            if (!role.getType().equals(toType.getTopicIF())) {
              role.setType(toType.getTopicIF());
            }
          }
        }        
      }
    }
  }
  
  static class RoleFieldComparator implements Comparator<RoleField> {
    private static final RoleFieldComparator INSTANCE = new RoleFieldComparator();

    private RoleFieldComparator() {
      super();
    }

    public static RoleFieldComparator getInstance() {
      return INSTANCE;
    }

    @Override
    public int compare(RoleField rf1, RoleField rf2) {
      return StringUtils.compare(rf1.getFieldName(), rf2.getFieldName());
    }
  }

}
