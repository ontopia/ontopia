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

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import ontopoly.model.TopicMap;

public class Upgrade_2_1 extends UpgradeBase {
  
  Upgrade_2_1(TopicMap topicmap) throws InvalidQueryException {
    super(topicmap);
  }
  
  @Override
  protected void importLTM(StringBuilder sb) {
    // Add support for query fields
    sb.append("[on:query-field : on:topic-type on:system-topic @\"http://psi.ontopia.net/ontology/query-field\" = \"Query field\"]\n");
    sb.append("xtm:superclass-subclass(on:field-definition : xtm:superclass, on:query-field : xtm:subclass)\n");

    // Add view mode "Popup"
    sb.append("[on:view-mode-popup : on:view-mode on:system-topic = \"Popup\"]");

    // Mark on:topic-type as being on:field-set
    sb.append("[on:field-set : on:topic-type on:system-topic @\"http://psi.ontopia.net/ontology/field-set\" = \"Field set\"]\n");
    sb.append("[on:topic-type : on:field-set]\n");

    // Validation type
    sb.append("[on:validation-type : on:occurrence-type on:system-topic @\"http://psi.ontopia.net/ontology/validation-type\" = \"Validation type\"]\n");

    // FIXME: add xsd:boolean?
    
  }
  
  @Override
  protected void transform() throws InvalidQueryException {
    // no-op
  }

}
