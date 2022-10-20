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

/**
 * Represents a role type.
 */
public class RoleType extends AbstractTypingTopic {

  /**
   * Creates a new RoleType object.
   */
  public RoleType(TopicIF currTopic, TopicMap tm) {
    super(currTopic, tm);
  }

  @Override
  public LocatorIF getLocatorIF() {
    return PSI.ON_ROLE_TYPE;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof RoleType)) {
      return false;
    }

    RoleType other = (RoleType) obj;
    return (getTopicIF().equals(other.getTopicIF()));
  }

  @Override
	public Collection<RoleField> getDeclaredByFields() {
    String query = "select $RF from "
			+ "on:has-role-type(%RT% : on:role-type, $RF : on:role-field)?";
    Map<String,TopicIF> params = Collections.singletonMap("RT", getTopicIF());

    QueryMapper<RoleField> qm = getTopicMap().newQueryMapper(RoleField.class);
    return qm.queryForList(query, params);
	}

}
