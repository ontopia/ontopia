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
import net.ontopia.topicmaps.core.TopicNameIF;
import ontopoly.utils.OntopolyModelUtils;

/**
 * Represents a name field.
 */
public class NameField extends FieldDefinition {
  private NameType nameType;

  public NameField(TopicIF topic, TopicMap tm) {
    this(topic, tm, null);
  }

  public NameField(TopicIF topic, TopicMap tm, NameType nameType) {
    super(topic, tm);
    this.nameType = nameType;
  }

  @Override
  public int getFieldType() {
    return FIELD_TYPE_NAME;
  }

  @Override
  public String getFieldName() {
    return getTopicMap().getTopicName(getTopicIF(), getNameType());
  }

  @Override
  public LocatorIF getLocator() {
    return PSI.ON_NAME_FIELD;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof NameField)) {
      return false;
    }
		
    NameField other = (NameField)obj;
    return (getTopicIF().equals(other.getTopicIF()));
  }

  /**
   * Gets the name type.
   * 
   * @return the name type.
   */
  public NameType getNameType() {
    if (nameType == null) {
      TopicIF nameTypeIf = OntopolyModelUtils.findBinaryPlayer(getTopicMap(), 
          PSI.ON_HAS_NAME_TYPE, getTopicIF(), PSI.ON_NAME_FIELD, PSI.ON_NAME_TYPE);
      this.nameType = (nameTypeIf == null ? null : new NameType(nameTypeIf, getTopicMap()));      
    }
    return nameType;
  }

  /**
   * Returns the names, which have this NameType object as type, associated with
   * the topic.
   * 
   * @param topic the topic from which the values is retrieved.
   * @return a collection of TopicNameIFs.
   */
  @Override
  public List<TopicNameIF> getValues(Topic topic) {
    TopicIF topicIf = topic.getTopicIF();
    NameType ntype = getNameType();
    if (ntype == null) {
      return Collections.emptyList();
    }
    TopicIF typeIf = ntype.getTopicIF();
		
//    Collection<TopicIF> scope = Collections.emptySet();
    return OntopolyModelUtils.findTopicNames(typeIf, topicIf); // , scope);
  }

  /**
   * Adds a name to a topic. The name has this NameType object as type. If no
   * identical names are associated with the topic, a new one is added, but if
   * some names already exist, all except the first one is deleted.
   * 
   * @param _value
   *            value which is going to be added to the topic.
   */
  @Override
  public void addValue(Topic topic, Object _value, LifeCycleListener listener) {
    TopicIF topicIf = topic.getTopicIF();
    String value = (String) _value;
    NameType ntype = getNameType();
    if (ntype == null) {
      return;
    }
    TopicIF typeIf = ntype.getTopicIF();
   
    Collection<TopicIF> scope = Collections.emptySet();
    Collection<TopicNameIF> names = OntopolyModelUtils.findTopicNames(typeIf, topicIf, value); //, scope);
    if (names.isEmpty()) {
      // create new
      OntopolyModelUtils.makeTopicName(typeIf, topicIf, value, scope);
    } else {
      // remove all except the first one
      Iterator<TopicNameIF> iter = names.iterator();
      iter.next();
      while (iter.hasNext()) {
        TopicNameIF name = iter.next();
        name.remove();
      }
    }
    
    if (listener != null) {
      listener.onAfterAdd(topic, this, value);
    }
  }

  /**
   * Removes a name from a topic. The name has this NameType object as type. If
   * names with the value, _value, are associated with the topic, topic, they
   * will be deleted.
   * 
   * @param _value
   *            value which is going to be removed from the topic.
   */
  @Override
  public void removeValue(Topic topic, Object _value, LifeCycleListener listener) {
    TopicIF topicIf = topic.getTopicIF();
    String value = (_value instanceof TopicNameIF ? ((TopicNameIF) _value)
        .getValue() : (String) _value);
    NameType ntype = getNameType();
    if (ntype == null) {
      return;
    }
    TopicIF typeIf = ntype.getTopicIF();

    if (listener != null) {
      listener.onBeforeRemove(topic, this, value);
    }
		
//    Collection<TopicIF> scope = Collections.emptySet();
    Collection<TopicNameIF> names = OntopolyModelUtils.findTopicNames(typeIf, topicIf, value); // , scope);
    if (!names.isEmpty()) {
      // remove all matching
      Iterator<TopicNameIF> iter = names.iterator();
      while (iter.hasNext()) {
        TopicNameIF name = iter.next();
        name.remove();
      }
    }
  }

  /**
   * Returns the assigned height of the name text field.
   */
  public int getHeight() {
    TopicIF oType = OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON_HEIGHT);
    OccurrenceIF occ = OntopolyModelUtils.findOccurrence(oType, getTopicIF());
    return (occ == null ? 1 : Integer.parseInt(occ.getValue()));
  }

  /**
   * Returns the assigned width of the name text field.
   */
  public int getWidth() {
    TopicIF oType = OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON_WIDTH);
    OccurrenceIF occ = OntopolyModelUtils.findOccurrence(oType, getTopicIF());
    return (occ == null ? 50 : Integer.parseInt(occ.getValue()));
  }

}
