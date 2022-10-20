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
 * Represents a name type.
 */
public class NameType extends AbstractTypingTopic {

  /**
   * Creates a new NameType object.
   */
  public NameType(TopicIF currTopic, TopicMap tm) {
    super(currTopic, tm);
  }

  @Override
  public LocatorIF getLocatorIF() {
    return PSI.ON_NAME_TYPE;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof NameType)) {
      return false;
    }

    NameType other = (NameType) obj;
    return (getTopicIF().equals(other.getTopicIF()));
  }

  /**
   * Returns true if name type is on:untyped.
   */
  public boolean isUntypedName() {
    return getTopicIF().getSubjectIdentifiers().contains(PSI.TMDM_TOPIC_NAME);
  }

  @Override
  public Collection<NameField> getDeclaredByFields() {
    String query = "select $FD from on:has-name-type(%TYPE% : on:name-type, $FD : on:name-field)?";
    Map<String,TopicIF> params = Collections.singletonMap("TYPE", getTopicIF());

    QueryMapper<NameField> qm = getTopicMap().newQueryMapper(NameField.class);    
    return qm.queryForList(query,
        new RowMapperIF<NameField>() {
          @Override
          public NameField mapRow(QueryResultIF result, int rowno) {
            TopicIF fieldTopic = (TopicIF)result.getValue(0);
            return new NameField(fieldTopic, getTopicMap(), new NameType(getTopicIF(), getTopicMap()));
          }
       }, params);
  }

}
