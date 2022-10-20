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
import java.util.List;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * Represents a cardinality that can be assigned to a field.
 */
public class Cardinality extends Topic {

  /**
   * Creates a new Cardinality object.
   */
  public Cardinality(TopicIF topic, TopicMap tm) {
    super(topic, tm);
  }
  
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Cardinality)) {
      return false;
    }

    Cardinality cardinality = (Cardinality) obj;
    return (getTopicIF().equals(cardinality.getTopicIF()));
  }

  public LocatorIF getLocator() {
    Collection<LocatorIF> subjectIdentifiers = getTopicIF().getSubjectIdentifiers();
    if (subjectIdentifiers.contains(PSI.ON_CARDINALITY_0_1)) {
      return PSI.ON_CARDINALITY_0_1;
    } else if (subjectIdentifiers.contains(PSI.ON_CARDINALITY_1_1)) {
      return PSI.ON_CARDINALITY_1_1;
    } else if (subjectIdentifiers.contains(PSI.ON_CARDINALITY_0_M)) {
      return PSI.ON_CARDINALITY_0_M;
    } else if (subjectIdentifiers.contains(PSI.ON_CARDINALITY_1_M)) {
      return PSI.ON_CARDINALITY_1_M;
    } else {
      return null;
    }
  }
  
  public boolean isZeroOrOne() {
    return getTopicIF().getSubjectIdentifiers().contains(PSI.ON_CARDINALITY_0_1);
  }

  public boolean isExactlyOne() {
    return getTopicIF().getSubjectIdentifiers().contains(PSI.ON_CARDINALITY_1_1);
  }

  public boolean isZeroOrMore() {
    return getTopicIF().getSubjectIdentifiers().contains(PSI.ON_CARDINALITY_0_M);
  }

  public boolean isOneOrMore() {
    return getTopicIF().getSubjectIdentifiers().contains(PSI.ON_CARDINALITY_1_M);
  }
  
  /**
   * True if cardinality is 0..1 or 1..1.
   */
  public boolean isMaxOne() {
    return isZeroOrOne() || isExactlyOne();
  }
//
//  /**
//   * True if cardinality is 0..* or 1..*.
//   */
//  public boolean isMaxInfinite() {
//    return isZeroOrMore() || isOneOrMore();
//  }
//
//  /**
//   * True if cardinality is 0..* or 0..1.
//   */
//  public boolean isMinZero() {
//    return isZeroOrMore() || isZeroOrOne();
//  }

  /**
   * True if cardinality is 1..* or 1..1.
   */
  public boolean isMinOne() {
    return isOneOrMore() || isExactlyOne();
  }

  /**
   * Returns the default cardinality (zero or more)
   */
  public static Cardinality getDefaultCardinality(FieldDefinition fieldDefinition) {
    TopicMap tm = fieldDefinition.getTopicMap();
    LocatorIF cardPSI = PSI.ON_CARDINALITY_0_M; 
    switch (fieldDefinition.getFieldType()) {
      case FieldDefinition.FIELD_TYPE_IDENTITY: {
        IdentityField identityField = (IdentityField)fieldDefinition;
        if (identityField.isSubjectLocator()) {
          cardPSI = PSI.ON_CARDINALITY_1_1;
        } else {
          cardPSI = PSI.ON_CARDINALITY_0_M;
        }
        break;
      }
      case FieldDefinition.FIELD_TYPE_NAME: {
        cardPSI = PSI.ON_CARDINALITY_1_1;
        break;
      }
      case FieldDefinition.FIELD_TYPE_OCCURRENCE: {
        cardPSI = PSI.ON_CARDINALITY_0_1;
        break;
      }
      case FieldDefinition.FIELD_TYPE_ROLE: {
        cardPSI = PSI.ON_CARDINALITY_0_M;
        break;
      }
      case FieldDefinition.FIELD_TYPE_QUERY: {
        cardPSI = PSI.ON_CARDINALITY_0_M;
        break;
      }
      default:
        throw new OntopiaRuntimeException("Unknown field definition: " + fieldDefinition.getFieldType());
    }
    return new Cardinality(tm.getTopicMapIF().getTopicBySubjectIdentifier(cardPSI), tm);
  }

  /**
   * Returns all available cardinalities.
   * 
   * @return A list containing Cardinality objects of all available
   *         cardinalities.
   */
  public static List<Cardinality> getCardinalityTypes(TopicMap tm) {
    String query = "instance-of($d, on:cardinality)?";

    QueryMapper<Cardinality> qm = tm.newQueryMapper(Cardinality.class);
    return qm.queryForList(query);
  }

  public int getMinCardinality() {
    return isMinOne() ? 1 : 0;
  }

  public int getMaxCardinality() {
    return isMaxOne() ? 1 : 0;
  }

}
