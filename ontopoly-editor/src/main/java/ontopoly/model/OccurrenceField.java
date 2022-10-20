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
import java.util.Iterator;
import java.util.List;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import ontopoly.utils.OntopolyModelUtils;

/**
 * Represents an occurrence field.
 */
public class OccurrenceField extends FieldDefinition {
  private OccurrenceType occurrenceType;

  public OccurrenceField(TopicIF topic, TopicMap tm) {
    this(topic, tm, null);
  }

  public OccurrenceField(TopicIF topic, TopicMap tm, OccurrenceType occurrenceType) {
    super(topic, tm);
    this.occurrenceType = occurrenceType;
  }

  @Override
  public int getFieldType() {
    return FIELD_TYPE_OCCURRENCE;
  }

  @Override
  public String getFieldName() {
    return getTopicMap().getTopicName(getTopicIF(), getOccurrenceType());
  }

  @Override
  public LocatorIF getLocator() {
    return PSI.ON_OCCURRENCE_FIELD;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof OccurrenceField)) {
      return false;
    }
		
    OccurrenceField other = (OccurrenceField)obj;
    return (getTopicIF().equals(other.getTopicIF()));
  }

  /**
   * Gets the occurrence type to which this field is assigned.
   * 
   * @return the occurrence type.
   */
  public OccurrenceType getOccurrenceType() {
    if (occurrenceType == null) {      
      TopicIF occurrenceTypeIf = OntopolyModelUtils.findBinaryPlayer(getTopicMap(), 
          PSI.ON_HAS_OCCURRENCE_TYPE, getTopicIF(), PSI.ON_OCCURRENCE_FIELD, PSI.ON_OCCURRENCE_TYPE);
      this.occurrenceType = (occurrenceTypeIf == null ? null : new OccurrenceType(occurrenceTypeIf, getTopicMap()));      
    }
    return occurrenceType;
  }

  /**
   * Returns the data type of the occurrence type.
   */
  public DataType getDataType() {
    TopicIF dataTypeIf = OntopolyModelUtils.findBinaryPlayer(getTopicMap(), 
        PSI.ON_HAS_DATATYPE, getTopicIF(), PSI.ON_FIELD_DEFINITION, PSI.ON_DATATYPE);
    return dataTypeIf == null ? DataType.getDefaultDataType(getTopicMap()) : new DataType(dataTypeIf, getTopicMap());
  }

  /**
   * Returns the assigned height of the occurrence text field.
   */
  public int getHeight() {
    TopicIF oType = OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON_HEIGHT);
    OccurrenceIF occ = OntopolyModelUtils.findOccurrence(oType, getTopicIF());
    return (occ == null ? 1 : Integer.parseInt(occ.getValue()));
  }

  /**
   * Returns the assigned width of the occurrence text field.
   */
  public int getWidth() {
    TopicIF oType = OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON_WIDTH);
    OccurrenceIF occ = OntopolyModelUtils.findOccurrence(oType, getTopicIF());
    return (occ == null ? 50 : Integer.parseInt(occ.getValue()));
  }

  @Override
  public List<OccurrenceIF> getValues(Topic topic) {
    TopicIF topicIf = topic.getTopicIF();
    OccurrenceType otype = getOccurrenceType();
    if (otype == null) {
      return Collections.emptyList();
    }
		TopicIF typeIf = otype.getTopicIF();
    // FIXME: need to figure out how to do datatypes properly
    //! LocatorIF datatype = getDataType().getLocator();
    // HACK: we're ignoring the datatype when looking up existing ones
    // return OntopolyModelUtils.findOccurrences(getTopicIF(), topicIf,
    // datatype, Collections.EMPTY_SET);
    Collection<TopicIF> scope = Collections.emptySet();
    return OntopolyModelUtils.findOccurrences(typeIf, topicIf, scope);
  }

  @Override
  public void addValue(Topic topic, Object _value, LifeCycleListener listener) {
    TopicIF topicIf = topic.getTopicIF();
    String value = (String) _value;
    LocatorIF datatype = getDataType().getLocator();
    OccurrenceType otype = getOccurrenceType();
    if (otype == null) {
      return;
    }
		TopicIF typeIf = otype.getTopicIF();
		
    // HACK: we're ignoring the datatype when looking up existing ones
    // Collection occs = OntopolyModelUtils.findOccurrences(getTopicIF(),
    // topicIf, value, datatype, Collections.EMPTY_SET);
    Collection<TopicIF> scope = Collections.emptySet();      
    Collection<OccurrenceIF> occs = OntopolyModelUtils.findOccurrences(typeIf, topicIf, value, scope);
    if (occs.isEmpty()) {
      // create new
      OntopolyModelUtils.makeOccurrence(typeIf, topicIf, value, datatype, scope);
    } else {
      // remove all except the first one
      Iterator<OccurrenceIF> iter = occs.iterator();
      iter.next(); // skip first
      while (iter.hasNext()) {
        OccurrenceIF occ = iter.next();
        occ.remove();
      }
    }
    
    if (listener != null) {
      listener.onAfterAdd(topic, this, value);
    }
  }

  @Override
  public void removeValue(Topic topic, Object _value, LifeCycleListener listener) {
    TopicIF topicIf = topic.getTopicIF();
    String value = (_value instanceof OccurrenceIF ? ((OccurrenceIF) _value)
        .getValue() : (String) _value);
//    LocatorIF datatype = getDataType().getLocator();
    OccurrenceType otype = getOccurrenceType();
    if (otype == null) {
      return;
    }
		TopicIF typeIf = otype.getTopicIF();

		if (listener != null) {
      listener.onBeforeRemove(topic, this, value);
    }
		
    // HACK: we're ignoring the datatype when looking up existing ones
    // Collection occs = OntopolyModelUtils.findOccurrences(typeIf,
    // topicIf, value, datatype, Collections.EMPTY_SET);
    Collection<TopicIF> scope = Collections.emptySet();      
    Collection<OccurrenceIF> occs = OntopolyModelUtils.findOccurrences(typeIf, topicIf, value, scope);
    if (!occs.isEmpty()) {
      // remove all the matching
      Iterator<OccurrenceIF> iter = occs.iterator();
      while (iter.hasNext()) {
        OccurrenceIF occ = iter.next();
        occ.remove();
      }
    }
  }

}
