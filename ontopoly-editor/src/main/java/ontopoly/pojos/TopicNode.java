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
package ontopoly.pojos;

import java.io.Serializable;
import java.util.Objects;

import ontopoly.OntopolyContext;
import ontopoly.model.Topic;
import ontopoly.model.TopicMap;

public class TopicNode implements Serializable {
  
  private String topicMapId;
  private String topicId;
  private String name;
  
  /**
   * Used for serialization only.
   */
  public TopicNode() {
  }

  public TopicNode(Topic topic) {
    Objects.requireNonNull(topic, "topic parameter cannot be null.");
    this.topicMapId = topic.getTopicMap().getId();
    this.topicId = topic.getId();
  }
  
  public TopicNode(String topicMapId, String topicId) {
    Objects.requireNonNull(topicMapId, "topicMapId parameter cannot be null.");
    Objects.requireNonNull(topicId, "topicId parameter cannot be null.");
    this.topicMapId = topicMapId;
    this.topicId = topicId;
  }

  public String getTopicMapId() {
    return topicMapId;
  }

  public String getTopicId() {
    return topicId;
  }
  
  public String getName() {
    if (name != null) { 
      return name;
    } else {
      return getTopic().getName();
    }
  }
  
  public Topic getTopic() {
    TopicMap tm = OntopolyContext.getTopicMap(topicMapId);
    return tm.getTopicById(topicId);
  }
  
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof TopicNode)) {
      return false;
    }
    TopicNode other = (TopicNode)o;
    return topicId.equals(other.topicId) && topicMapId.equals(other.topicMapId);
  }
  
  @Override
  public int hashCode() {
    return topicMapId.hashCode() + topicId.hashCode();
  }
  
}
