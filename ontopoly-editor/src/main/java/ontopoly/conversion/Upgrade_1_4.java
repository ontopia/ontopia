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

public class Upgrade_1_4 extends UpgradeBase {
  
  Upgrade_1_4(TopicMap topicmap) throws InvalidQueryException {
    super(topicmap);
  }
  
  @Override
  protected void importLTM(StringBuilder sb) {
    sb.append("[on:untyped-name : on:system-topic]\n");
  }
  
  @Override
  protected void transform() throws InvalidQueryException {
  }
  
}
