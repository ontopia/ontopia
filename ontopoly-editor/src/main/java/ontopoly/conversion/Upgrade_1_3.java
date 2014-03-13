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

public class Upgrade_1_3 extends UpgradeBase {
  
  Upgrade_1_3(TopicMap topicmap) throws InvalidQueryException {
    super(topicmap);
  }
  
  @Override
  protected void importLTM(StringBuilder sb) {
    sb.append("[on:datatype-html   : on:datatype = \"HTML\"]\n");
    sb.append("[on:datatype-image   : on:datatype = \"Image\"]\n");
    sb.append("[on:ontology-topic-type : on:topic-type]\n");
  }
  
  @Override
  protected void transform() throws InvalidQueryException {
    
    removeObjects(topicmap, dc,
        "select $A from association-role($A, $R1), type($A, xtm:superclass-subclass), " +
        "type($R1, xtm:superclass), role-player($R1, on:topic-type), " +
        "association-role($A, $R2), " +
        "type($R2, xtm:subclass), role-player($R2, on:ontology-topic-type)?");
  }
  
}
