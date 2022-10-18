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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import ontopoly.utils.OntopolyModelUtils;
import ontopoly.utils.Ordering;

/**
 * Represents a field as assigned to a topic type. The field itself is a
 * FieldDefinition, and the topic type a TopicType. This object primarily
 * holds the cardinality and order in the list of fields.
 */
public final class FieldAssignment {
  private FieldDefinition fieldDefinition;
  private TopicType topicType;
  private TopicType declaredTopicType;

  private TopicMap tm;

  private int cachedOrder = Integer.MAX_VALUE;

  /**
   * Creates a new field assignment object.
   */
  public FieldAssignment(TopicType topicType, TopicType declaredTopicType, FieldDefinition fieldDefinition) {
    this.fieldDefinition = fieldDefinition;
    this.topicType = topicType;
    this.declaredTopicType = declaredTopicType;
    this.tm = topicType.getTopicMap();
  }

  public FieldAssignment(TopicType topicType, TopicType declaredTopicType, FieldDefinition fieldDefinition, int cachedOrder) {
    this(topicType, declaredTopicType, fieldDefinition);
    this.cachedOrder = (cachedOrder == Integer.MAX_VALUE ? cachedOrder - 1 : cachedOrder);
  }

  /**
   * Returns the topic type.
   */
  public TopicType getTopicType() {
    return topicType;
  }

  /**
   * Returns the topic type.
   */
  public TopicType getDeclaredTopicType() {
    return declaredTopicType;
  }

  /**
   * Returns the field type.
   */
  public FieldDefinition getFieldDefinition() {
    return fieldDefinition;
  }

  public Cardinality getCardinality() {
    return getFieldDefinition().getCardinality();
  }

  /**
   * Returns the ordering key of the field on this topic type.
   */
  public int getOrder() {
    if (cachedOrder < Integer.MAX_VALUE) return cachedOrder;

    int order = getOrder(topicType);
    this.cachedOrder = (order == Integer.MAX_VALUE ? order - 1 : order);
    return cachedOrder;
  }

  /**
   * Returns the ordering key of the field on the topic type sent in as an
   * argument.
   */
  public int getOrder(TopicType t) {
    String value;
    Map<String,Object> queryResult;
    TopicIF tt = t.getTopicIF();

    String query = "field-order-value($tt, $f, $v) :- "
        + "occurrence($tt, $OCC), scope($OCC, $f), value($OCC, $v). "
        + "select $value, $super from "
        + "$tt = %tt%, "
        + "$f = %f%, "
        + "{field-order-value($tt, $f, $value)}, "
        + "{xtm:superclass-subclass($super : xtm:superclass , $tt : xtm:subclass) } limit 1? ";

    Map<String,TopicIF> params = new HashMap<String,TopicIF>();
    params.put("tt", tt);
    params.put("f", fieldDefinition.getTopicIF());

    QueryMapper<Object> qm = tm.newQueryMapperNoWrap();
    
    while (true) {
      queryResult = qm.queryForMap(query, params);
      value = (String) queryResult.get("value");
      tt = (TopicIF) queryResult.get("super");
      params.put("tt", tt);
      return Ordering.stringToOrder(value);
    }
  }

  private void setOrder(int order) {
    setOrder(getTopicType().getTopicMap(), getTopicType().getTopicIF(), getFieldDefinition().getTopicIF(), order, true);
    this.cachedOrder = order;
  }

  public static void setOrder(TopicMap topicmap, TopicIF tt, TopicIF fd, int order, boolean replace) {
    String value = Ordering.orderToString(order);
    LocatorIF datatype = DataTypes.TYPE_STRING;

    TopicIF topicIf = tt;
    TopicIF typeIf = OntopolyModelUtils.getTopicIF(topicmap, PSI.ON_FIELD_ORDER);
    TopicIF themeIf = fd;

    Collection<TopicIF> scope = Collections.singleton(themeIf);
    Collection<OccurrenceIF> occs = OntopolyModelUtils.findOccurrences(typeIf, topicIf, datatype, scope);
    if (!occs.isEmpty()) {
      if (!replace) return; // stop here if we're not replacing
      Iterator<OccurrenceIF> iter = occs.iterator();
      while (iter.hasNext()) {
        OccurrenceIF occ = iter.next();
        occ.remove();
      }
    }
    OntopolyModelUtils.makeOccurrence(typeIf, topicIf, value, datatype, scope);
  }

  /**
   * Change field order so that this field is ordered directly after the other field.
   * @param other the field to order after.
   */
  public void moveAfter(FieldAssignment other) {

    if (!Objects.equals(getTopicType(), other.getTopicType()))
      throw new RuntimeException("Cannot reorder fields that are assigned to different topic types.");

    List<FieldAssignment> fieldAssignments = getTopicType().getFieldAssignments();
    int length = fieldAssignments.size();

    // find next field assignment
    FieldAssignment fa_next = null;
    int indexOfThis = fieldAssignments.indexOf(this);
    if (indexOfThis < (length-1))
      fa_next = fieldAssignments.get(indexOfThis+1);

    // get last field order
    int fieldOrderMax = Ordering.MAX_ORDER;
    for (int i=0; i < length; i++) {
      FieldAssignment fa = fieldAssignments.get(i);
      int fieldOrder = fa.getOrder();
      if (fieldOrder != Ordering.MAX_ORDER &&
          (fieldOrderMax == Ordering.MAX_ORDER || fieldOrder > fieldOrderMax))
        fieldOrderMax = fieldOrder;
    }

    // make sure this field assignment has a field order
    int fieldOrderThis = getOrder();
    if (fieldOrderThis == Ordering.MAX_ORDER) {
      fieldOrderThis = (fieldOrderMax == Ordering.MAX_ORDER ? 0 : (fieldOrderMax + Ordering.ORDER_INCREMENTS));
      setOrder(fieldOrderThis);
    }

    // find next available order
    if (fa_next == null || fa_next.getOrder() == Ordering.MAX_ORDER) {
      // if no next then just increment
      other.setOrder(fieldOrderThis + Ordering.ORDER_INCREMENTS);
    } else {
      // if next then average this and next field orders
      int nextAvailableOrder = (fieldOrderThis + fa_next.getOrder())/2;
      if (nextAvailableOrder != fieldOrderThis) {
        other.setOrder(nextAvailableOrder);
      } else {
        // we need to reshuffle field assignments after this one
        nextAvailableOrder = fieldOrderThis + Ordering.ORDER_INCREMENTS;
        other.setOrder(nextAvailableOrder);
        for (int i=indexOfThis+1; i < length; i++) {
          FieldAssignment fa = fieldAssignments.get(i);
          if (!Objects.equals(fa, other)) {
            nextAvailableOrder += Ordering.ORDER_INCREMENTS;
            fa.setOrder(nextAvailableOrder);
          }
        }
      }
    }
  }
  
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof FieldAssignment))
      return false;

    FieldAssignment fa = (FieldAssignment) obj;
    return (topicType.getTopicIF().equals(fa.topicType.getTopicIF()) &&
			fieldDefinition.getTopicIF().equals(fa.getFieldDefinition().getTopicIF()));
  }

  @Override
  public int hashCode() {
    return topicType.getTopicIF().hashCode() * fieldDefinition.getTopicIF().hashCode();
  }

}
