/*
 * #!
 * Ontopia TMRAP
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

package net.ontopia.topicmaps.impl.remote;

import java.util.*;
import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.core.index.*;
import net.ontopia.topicmaps.impl.utils.*;
import net.ontopia.topicmaps.impl.basic.*;
import net.ontopia.topicmaps.impl.basic.index.IndexManager;

/**
 * INTERNAL: The remote (remote in the sense that it deals with remote topics - 
 *           and not that the transactions work in a remote or distributed
 *           fashion) implementation of a topicMapBuilder.
 */
public class RemoteTopicMapBuilder extends TopicMapBuilder {
    
  RemoteTopicMapBuilder(TopicMap tm) {
    super(tm);
  }
  
  protected TopicIF createTopic() { // overrides method in parent
    TopicIF topic = new RemoteTopic(tm);
    tm.addTopic(topic);
    return topic;
  }

}
