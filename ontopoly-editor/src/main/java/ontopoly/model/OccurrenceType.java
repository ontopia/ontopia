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
import java.util.Map;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.RowMapperIF;

/**
 * Represents an occurrence type.
 */
public class OccurrenceType extends AbstractTypingTopic {

  /**
   * Creates a new OccurrenceType object.
   */
  public OccurrenceType(TopicIF type, TopicMap tm) {
    super(type, tm);
  }

  @Override
  public LocatorIF getLocatorIF() {
    return PSI.ON_OCCURRENCE_TYPE;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof OccurrenceType)) {
      return false;
    }

    OccurrenceType other = (OccurrenceType) obj;
    return (getTopicIF().equals(other.getTopicIF()));
  }

  @Override
  public Collection<OccurrenceField> getDeclaredByFields() {
    String query = "select $FD from on:has-occurrence-type(%OT% : on:occurrence-type, $FD : on:occurrence-field)?";
    Map<String,TopicIF> params = Collections.singletonMap("OT", getTopicIF());

    QueryMapper<OccurrenceField> qm = getTopicMap().newQueryMapper(OccurrenceField.class);
    return qm.queryForList(query,
        new RowMapperIF<OccurrenceField>() {
          @Override
          public OccurrenceField mapRow(QueryResultIF result, int rowno) {
              TopicIF fieldTopic = (TopicIF)result.getValue(0);
              return new OccurrenceField(fieldTopic, getTopicMap(), new OccurrenceType(getTopicIF(), getTopicMap()));
          }
        }, params);
  }

}
