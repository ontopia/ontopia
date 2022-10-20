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

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.RowMapperIF;
import ontopoly.utils.OntopolyModelUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Represents an association field.
 */
public class AssociationField extends Topic {
  private AssociationType cachedAssociationType;
  private List<RoleField> cachedFieldsForRoles;

  public AssociationField(TopicIF topic, TopicMap tm) {
    super(topic, tm);
  }

  public AssociationField(TopicIF topic, TopicMap tm, AssociationType associationType) {
    super(topic, tm);
    this.cachedAssociationType = associationType;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof AssociationField)) {
      return false;
    }
		
    AssociationField other = (AssociationField)obj;
    return (getTopicIF().equals(other.getTopicIF()));
  }

  /**
   * Gets the association type that is assigned to this association field.
   * 
   * @return the association type.
   */
  public AssociationType getAssociationType() {
    if (cachedAssociationType == null) {
      TopicIF associationTypeIf = OntopolyModelUtils.findBinaryPlayer(getTopicMap(), 
          PSI.ON_HAS_ASSOCIATION_TYPE, getTopicIF(), PSI.ON_ASSOCIATION_FIELD, PSI.ON_ASSOCIATION_TYPE);
      this.cachedAssociationType = (associationTypeIf == null ? null : new AssociationType(associationTypeIf, getTopicMap()));      
		}
    return cachedAssociationType;
  }

  /**
   * Returns the arity of the association field, i.e. the number of roles that
   * can be played.
   * 
   * @return integer representing the number of allowed roles.
   */
  public int getArity() {
    return getFieldsForRoles().size();
  }

  /**
   * Returns the fields for the roles in this association type.
   * 
   * @return List of RoleField objects
   */
  public List<RoleField> getFieldsForRoles() {
    if (cachedFieldsForRoles != null) {
      return cachedFieldsForRoles;
    }
                
    String query = "select $RF from "
			+ "on:has-association-field(%AF% : on:association-field, $RF : on:role-field)?";
    Map<String,TopicIF> params = Collections.singletonMap("AF", getTopicIF());

    QueryMapper<RoleField> qm = getTopicMap().newQueryMapper(RoleField.class);
    
    List<RoleField> roleFields = qm.queryForList(query,
        new RowMapperIF<RoleField>() {
          @Override
          public RoleField mapRow(QueryResultIF result, int rowno) {
						TopicIF roleFieldTopic = (TopicIF)result.getValue(0);
						return new RoleField(roleFieldTopic, getTopicMap());
					}
				}, params);

		if (roleFields.size() == 1 && getAssociationType().isSymmetric()) {
			// if association is symmetric we have to add the other field manually
			RoleField rfield = roleFields.get(0);
			roleFields.add(rfield);
		} else {
			Collections.sort(roleFields, RoleFieldComparator.getInstance());
		}
		this.cachedFieldsForRoles = roleFields;
    return roleFields;
  }

  @Override
  public void remove(LifeCycleListener listener) {
    // remove all associated role fields
    Iterator<RoleField> iter = getFieldsForRoles().iterator();
    while (iter.hasNext()) {
      RoleField rf = iter.next();
      rf.remove(listener);
    }
    // remove association type topic
    if (listener != null) {
      listener.onBeforeDelete(this);
    }
    getTopicIF().remove();
  }
  
  /**
   * Gets the role fields that are assigned to this association field.
   * @return Collection of RoleField
   */
  public Collection<RoleField> getDeclaredByFields() {
    return getFieldsForRoles();
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
