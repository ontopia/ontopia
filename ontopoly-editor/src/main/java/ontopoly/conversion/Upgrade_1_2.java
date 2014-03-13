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
package ontopoly.conversion;

import ontopoly.model.TopicMap;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

public class Upgrade_1_2 extends UpgradeBase {
  
  Upgrade_1_2(TopicMap topicmap) throws InvalidQueryException {
    super(topicmap);
  }
  
  @Override
  protected void importLTM(StringBuilder sb) {
    // #2005 bug fix
    sb.append("[xtm:superclass-subclass : tech:hierarchical-relation-type]\n");
    sb.append("[xtm:superclass : tech:superordinate-role-type]\n");
    sb.append("[xtm:subclass : tech:subordinate-role-type]\n");

    // patterns support on datatypes (including date and datetime)
    sb.append("[on:pattern : on:occurrence-type on:system-topic = \"Pattern\"]\n");
    sb.append("on:is-hidden(on:pattern : on:field-type)\n");
    sb.append("{xsd:date, on:pattern, [[YYYY-MM-DD]]}\n");
    sb.append("{xsd:dateTime, on:pattern, [[YYYY-MM-DD HH:MM:SS]]}\n");

    // browse dialog as new interface control
    sb.append("[on:browse-dialog  : on:interface-control = \"Browse dialog\"]\n");

    // identity field should be instance of ontology topic type
    // ???
  }
  
  @Override
  protected void transform() throws InvalidQueryException {
  }
  
}
