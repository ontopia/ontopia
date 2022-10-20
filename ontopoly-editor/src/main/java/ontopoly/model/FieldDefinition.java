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
import java.util.Iterator;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.utils.CollectionUtils;
import ontopoly.utils.OntopolyModelUtils;

/**
 * Represents a field type, which may be a name type, an occurrence type, an
 * identity field, or a combination of an association role and an association
 * type.
 */
public abstract class FieldDefinition extends Topic {
  
  public static final int FIELD_TYPE_ROLE = 1;
  public static final int FIELD_TYPE_OCCURRENCE = 2;
  public static final int FIELD_TYPE_NAME = 4;
  public static final int FIELD_TYPE_IDENTITY = 8;
  public static final int FIELD_TYPE_QUERY = 16;

  private Cardinality cachedCardinality;

  protected FieldDefinition(TopicIF topic, TopicMap tm) {
    super(topic, tm);
  }

  /**
   * @return an int that identifies this fieldType
   */
  public abstract int getFieldType();

  /**
   * Returns the name of this field definition.
   */
  public abstract String getFieldName();
  
  public abstract LocatorIF getLocator();

  public ViewModes getViewModes(FieldsView view) {
    return new ViewModes(this, view);
  }

  public FieldsView getValueView(FieldsView view) {
    TopicMap tm = getTopicMap();
    TopicIF aType = OntopolyModelUtils.getTopicIF(tm, PSI.ON_USE_VALUE_VIEW);
    TopicIF rType1 = OntopolyModelUtils.getTopicIF(tm, PSI.ON_FIELD_DEFINITION);
    TopicIF player1 = getTopicIF();
    TopicIF rType2 = OntopolyModelUtils.getTopicIF(tm, PSI.ON_PARENT_VIEW);
    TopicIF player2 = view.getTopicIF();
    TopicIF rType3 = OntopolyModelUtils.getTopicIF(tm, PSI.ON_CHILD_VIEW);
    Collection<TopicIF> players = OntopolyModelUtils.findTernaryPlayers(tm, aType, player1, rType1, player2, rType2, rType3);
    TopicIF viewIf = CollectionUtils.getFirst(players);
    if (viewIf == null) {
      return FieldsView.getDefaultFieldsView(tm);
    } else {
      return new FieldsView(viewIf, tm);
    }
  }

  /**
   * Returns the cardinality of the field on this topic type.
   */
  public Cardinality getCardinality() {
    if (cachedCardinality == null) {
      TopicIF cardinalityIf = OntopolyModelUtils.findBinaryPlayer(getTopicMap(), 
          PSI.ON_HAS_CARDINALITY, getTopicIF(), PSI.ON_FIELD_DEFINITION, PSI.ON_CARDINALITY);
      this.cachedCardinality = (cardinalityIf == null ? Cardinality.getDefaultCardinality(this) : new Cardinality(cardinalityIf, getTopicMap()));
    }
    return cachedCardinality;
  }

  /**
   * Sets the cardinality of the field on this topic type.
   */
  public void setCardinality(Cardinality cardinality) {
    // NOTE: used by FieldsEditor
    TopicMap tm = getTopicMap();
    TopicIF aType = OntopolyModelUtils.getTopicIF(tm, PSI.ON_HAS_CARDINALITY);
    TopicIF type2 = OntopolyModelUtils.getTopicIF(tm, PSI.ON_FIELD_DEFINITION);
    TopicIF type3 = OntopolyModelUtils.getTopicIF(tm, PSI.ON_CARDINALITY);
    TopicIF player2 = getTopicIF();
    TopicIF player3 = cardinality.getTopicIF();

    Collection<AssociationIF> associationIFs = OntopolyModelUtils.findBinaryAssociations(
        tm, aType, player2, type2, type3);
    Iterator<AssociationIF> it = associationIFs.iterator();

    while (it.hasNext()) {
      it.next().remove();
    }
    OntopolyModelUtils.makeBinaryAssociation(aType, player2,
        type2, player3, type3);

    cachedCardinality = cardinality;
  }

  /**
   * Returns the validation type.
   */
  public String getValidationType() {
    TopicIF oType = OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON_VALIDATION_TYPE);
    OccurrenceIF occ = OntopolyModelUtils.findOccurrence(oType, getTopicIF());
    return occ == null ? null : occ.getValue();
  }

  public abstract Collection<? extends Object> getValues(Topic topic);

  public abstract void addValue(Topic topic, Object _value, LifeCycleListener listener);

  public abstract void removeValue(Topic topic, Object _value, LifeCycleListener listener);
  
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof FieldDefinition)) {
      return false;
    }

    FieldDefinition other = (FieldDefinition)obj;
    return getTopicIF().equals(other.getTopicIF());
  }

  @Override
  public int hashCode() {
    return getTopicIF().hashCode();
  }

  private static int getFieldType(TopicIF fieldTopic) {
    for (TopicIF topicType : fieldTopic.getTypes()) {
      Collection<LocatorIF> psis = topicType.getSubjectIdentifiers();
      if (psis.contains(PSI.ON_NAME_FIELD)) {
        return FieldDefinition.FIELD_TYPE_NAME;
      } else if (psis.contains(PSI.ON_IDENTITY_FIELD)) {
        return FieldDefinition.FIELD_TYPE_IDENTITY;
      } else if (psis.contains(PSI.ON_OCCURRENCE_FIELD)) {
        return FieldDefinition.FIELD_TYPE_OCCURRENCE;
      } else if (psis.contains(PSI.ON_ROLE_FIELD)) {
        return FieldDefinition.FIELD_TYPE_ROLE;
      } else if (psis.contains(PSI.ON_QUERY_FIELD)) {
        return FieldDefinition.FIELD_TYPE_QUERY;
      }
    }
    throw new RuntimeException("Not a field definition: " + fieldTopic);
  }
  
  public static FieldDefinition getFieldDefinition(String fieldId, TopicMap tm) {
    TopicIF fieldTopic = tm.getTopicIFById(fieldId);
    if (fieldTopic == null) {
      throw new RuntimeException("Could not find field with id '" + fieldId + "'");
    }
    int fieldType = getFieldType(fieldTopic);
    return getFieldDefinition(fieldId, fieldType, tm);
  }

  public static FieldDefinition getFieldDefinition(String fieldId, int fieldType, TopicMap tm) {    
    TopicIF fieldTopic = tm.getTopicIFById(fieldId);
    if (fieldTopic == null) {
      throw new RuntimeException("Could not find field with id '" + fieldId + "'");
    }
    return getFieldDefinition(fieldTopic, fieldType, tm);
  }
  
  public static FieldDefinition getFieldDefinition(TopicIF fieldTopic, TopicMap tm) {
    int fieldType = getFieldType(fieldTopic);
    return getFieldDefinition(fieldTopic, fieldType, tm);
  }
  
  private static FieldDefinition getFieldDefinition(TopicIF fieldTopic, int fieldType, TopicMap tm) {    
    
    switch (fieldType) {
    case FieldDefinition.FIELD_TYPE_ROLE:
      return new RoleField(fieldTopic, tm);
    case FieldDefinition.FIELD_TYPE_OCCURRENCE:
      return new OccurrenceField(fieldTopic, tm);
    case FieldDefinition.FIELD_TYPE_NAME:
      return new NameField(fieldTopic, tm);
    case FieldDefinition.FIELD_TYPE_IDENTITY:
      return new IdentityField(fieldTopic, tm);
    case FieldDefinition.FIELD_TYPE_QUERY:
      return new QueryField(fieldTopic, tm);
    default:
      throw new RuntimeException("Unknown field type: " + fieldType);
    }    
  }

}
