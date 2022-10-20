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

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import ontopoly.utils.OntopolyModelUtils;

/**
 * Represents an identify field.
 */
public class IdentityField extends FieldDefinition {
  private IdentityType identityType;

  /**
   * Creates a new IdentityField object.
   */
  public IdentityField(TopicIF topic, TopicMap tm) {
    this(topic, tm, null);
  }

  public IdentityField(TopicIF topic, TopicMap tm, IdentityType identityType) {
    super(topic, tm);
    this.identityType = identityType;
  }

  @Override
  public int getFieldType() {
    return FIELD_TYPE_IDENTITY;
  }

  /**
   * Returns the name of the IdentityField object.
   */
  @Override
  public String getFieldName() {
    return getTopicMap().getTopicName(getTopicIF(), getIdentityType());
  }

  @Override
  public LocatorIF getLocator() {
    return PSI.ON_IDENTITY_FIELD;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof IdentityField)) {
      return false;
    }

    IdentityField other = (IdentityField)obj;
    return (getTopicIF().equals(other.getTopicIF()));
  }

  /**
   * Gets the identity type.
   * 
   * @return the identity type.
   */
  public IdentityType getIdentityType() {
    if (identityType == null) {
      TopicIF identityTypeIf = OntopolyModelUtils.findBinaryPlayer(getTopicMap(), 
          PSI.ON_HAS_IDENTITY_TYPE, getTopicIF(), PSI.ON_IDENTITY_FIELD, PSI.ON_IDENTITY_TYPE);
      this.identityType = (identityTypeIf == null ? null : new IdentityType(identityTypeIf, getTopicMap()));      
    }
    return identityType;
  }

  /**
   * True if this is the subject locator field type.
   */
  public boolean isSubjectLocator() {
    IdentityType itype = getIdentityType();
    if (itype == null) {
      return false;
    }
    TopicIF itypeIF = itype.getTopicIF();
    return itypeIF.getSubjectIdentifiers().contains(PSI.ON_SUBJECT_LOCATOR);
  }

  /**
   * True if this is the subject identifier field type.
   */
  public boolean isSubjectIdentifier() {
    IdentityType itype = getIdentityType();
    if (itype == null) {
      return false;
    }
    TopicIF itypeIF = itype.getTopicIF();
    return itypeIF.getSubjectIdentifiers().contains(PSI.ON_SUBJECT_IDENTIFIER);
  }

  /**
   * True if this is the item identifier field type.
   */
  public boolean isItemIdentifier() {
    IdentityType itype = getIdentityType();
    if (itype == null) {
      return false;
    }
    TopicIF itypeIF = itype.getTopicIF();
    return itypeIF.getSubjectIdentifiers().contains(PSI.ON_ITEM_IDENTIFIER);
  }

  /**
   * Returns either the subject locator or every subject identifier associated
   * with the topic.
   * 
   * @param topic
   *            topic from which the values is retrieved.
   * @return A collection of LocatorIF objects.
   */
  @Override
  public Collection<? extends Object> getValues(Topic topic) {
    TopicIF topicIf = topic.getTopicIF();
    if (isSubjectLocator()) {
      return topicIf.getSubjectLocators();
    } else if (isItemIdentifier()) {
      return topicIf.getItemIdentifiers();
    } else {
      return topicIf.getSubjectIdentifiers();
    }
  }

  /**
   * Replaces a subject locator of or adds a subject identifier to a topic.
   * 
   * @param _value
   *            value which is going to be added to the topic.
   */
  @Override
  public void addValue(Topic topic, Object _value, LifeCycleListener listener) {
    TopicIF topicIf = topic.getTopicIF();
    LocatorIF value = (_value instanceof LocatorIF ? (LocatorIF) _value : 
                       URILocator.create((String) _value));
    if (value != null) {
      if (isSubjectLocator()) {
        topicIf.addSubjectLocator(value);
      } else if (isItemIdentifier()) {
        topicIf.addItemIdentifier(value);
      } else {
        topicIf.addSubjectIdentifier(value);
      }
    }
    
    if (listener != null) {
      listener.onAfterAdd(topic, this, value);
    }
  }

  /**
   * Removes the subject locator or a subject identifier from a topic.
   * 
   * @param _value
   *            value which is going to be removed from the topic.
   */
  @Override
  public void removeValue(Topic topic, Object _value, LifeCycleListener listener) {
    TopicIF topicIf = topic.getTopicIF();
    LocatorIF value = (_value instanceof LocatorIF ? (LocatorIF) _value : 
                       URILocator.create((String) _value));
    if (value != null) {
      if (listener != null) {
        listener.onBeforeRemove(topic, this, value);
      }
		  
      if (isSubjectLocator()) {
        topicIf.removeSubjectLocator(value);
      } else if (isItemIdentifier()) {
        topicIf.removeItemIdentifier(value);
      } else {
        topicIf.removeSubjectIdentifier(value);
      }
    }
  }

  /**
   * Returns the assigned height of the identity text field.
   */
  public int getHeight() {
    TopicIF oType = OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON_HEIGHT);
    OccurrenceIF occ = OntopolyModelUtils.findOccurrence(oType, getTopicIF());
    return (occ == null ? 1 : Integer.parseInt(occ.getValue()));
  }

  /**
   * Returns the assigned width of the identity text field.
   */
  public int getWidth() {
    TopicIF oType = OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON_WIDTH);
    OccurrenceIF occ = OntopolyModelUtils.findOccurrence(oType, getTopicIF());
    return (occ == null ? 50 : Integer.parseInt(occ.getValue()));
  }

}
