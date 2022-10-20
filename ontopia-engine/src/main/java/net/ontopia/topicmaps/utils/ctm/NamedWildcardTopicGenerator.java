/*
 * #!
 * Ontopia Engine
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

package net.ontopia.topicmaps.utils.ctm;

import net.ontopia.topicmaps.core.TopicIF;

public class NamedWildcardTopicGenerator extends AbstractTopicGenerator {
  private ParseContextIF context;
  private String name;
  private TopicIF topic; // made only once

  public NamedWildcardTopicGenerator(ParseContextIF context, String name) {
    this.context = context;
    this.name = name;
  }
  
  @Override
  public TopicIF getTopic() {
    if (topic == null) {
      topic = context.makeAnonymousTopic(name);
    }
    return topic;
  }

  @Override
  public ValueGeneratorIF copy() {
    return this; // no state, so...
  }

  /**
   * Called when the parse context the named wildcard occurs in ends,
   * so that next time this named wildcard is seen a new topic will
   * need to be created.
   */
  public void contextEnd() {
    topic = null;
  }
}
