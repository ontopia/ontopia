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

public class Upgrade_1_1 extends UpgradeBase {
  
  Upgrade_1_1(TopicMap topicmap) throws InvalidQueryException {
    super(topicmap);
  }

  @Override
  protected void importLTM(StringBuilder sb) {
    sb.append("[on:ontology-topic-type : on:system-topic = \"Ontology Topic type\"]\n");
    sb.append("xtm:superclass-subclass(on:topic-type : xtm:superclass, on:ontology-topic-type : xtm:subclass)\n");
    sb.append("[on:topic-type : on:ontology-topic-type]\n");
    sb.append("[on:name-type : on:ontology-topic-type]\n");
    sb.append("[on:occurrence-type : on:ontology-topic-type]\n");
    sb.append("[on:association-type : on:ontology-topic-type]\n");
    sb.append("[on:role-type : on:ontology-topic-type]\n");
    sb.append("[on:has-large-instance-set : on:association-type on:system-topic = \"Has large instance set\"]\n");

    sb.append("[on:forms-hierarchy-for : on:association-type on:system-topic = \"Forms hierarchy for\" = \"Hierarchy\" / on:topic-type = \"Forms hierarchy for\" / on:association-type]\n");
    sb.append("[on:is-hierarchical : on:association-type on:system-topic = \"Is hierarchical\"]\n");
    sb.append("[tech:hierarchical-relation-type : on:system-topic = \"Hierarchical Relation Type\"]\n");
    sb.append("[tech:subordinate-role-type : on:system-topic = \"Subordinate Role Type\"]\n");
    sb.append("[tech:superordinate-role-type : on:system-topic = \"Superordinate Role Type\"]\n");
    
    sb.append("on:is-hidden(on:has-large-instance-set : on:field-type)\n");
    sb.append("on:default-cardinality(on:untyped-name : on:field, on:cardinality-1-1 : on:cardinality)\n");
  }

  @Override
  protected void transform() throws InvalidQueryException {
    // no-op
  }
  
}
