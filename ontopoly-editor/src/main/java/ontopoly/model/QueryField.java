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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.RowMapperIF;
import ontopoly.utils.OntopolyModelUtils;

/**
 * Represents a query field.
 */
public class QueryField extends FieldDefinition {

  public QueryField(TopicIF topic, TopicMap tm) {
    super(topic, tm);
  }

  @Override
  public int getFieldType() {
    return FIELD_TYPE_QUERY;
  }


  @Override
  public String getFieldName() {
    return getTopicMap().getTopicName(getTopicIF(), null);
  }

  @Override
  public LocatorIF getLocator() {
    return PSI.ON_QUERY_FIELD;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof QueryField)) {
      return false;
    }
    
    QueryField other = (QueryField)obj;
    return (getTopicIF().equals(other.getTopicIF()));
  }

  /**
   * Returns the result from executing the values query.
   * 
   * @param topic the topic from which the values is retrieved.
   * @return a collection of objects.
   */
  @Override
  public List<Object> getValues(Topic topic) {

    String query = getValuesQuery();
    if (query != null) {
      Map<String,TopicIF> params = new HashMap<String,TopicIF>(2);
      params.put("field", getTopicIF());
      params.put("topic", topic.getTopicIF());

      QueryMapper<Object> qm = getTopicMap().newQueryMapperNoWrap();
      return qm.queryForList(query, new RowMapperIF<Object>() {
        @Override
        public Object mapRow(QueryResultIF queryResult, int rowno) {
          Object value = queryResult.getValue(0);
          if (value instanceof TopicIF) {
            return new Topic((TopicIF)value, getTopicMap());
          } else {
            return value;
          }
        }
      }, params);
    } else {
      return Collections.emptyList();
    }
  }

  private String getValuesQuery() {
    TopicIF topicIf = getTopicIF();   
    TopicIF typeIf = OntopolyModelUtils.getTopicIF(getTopicMap(), PSI.ON_VALUES_QUERY);
    OccurrenceIF occ = OntopolyModelUtils.findOccurrence(typeIf, topicIf);
    return (occ == null ? null : occ.getValue());
  }

  /**
   * Not supported.
   */
  @Override
  public void addValue(Topic topic, Object _value, LifeCycleListener listener) {
    throw new UnsupportedOperationException();
  }

  /**
   * Not supported.
   */
  @Override
  public void removeValue(Topic topic, Object _value, LifeCycleListener listener) {
    throw new UnsupportedOperationException();
  }

}
