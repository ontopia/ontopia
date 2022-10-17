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
package ontopoly.models;

import java.util.Objects;
import net.ontopia.topicmaps.core.TopicIF;
import ontopoly.OntopolyContext;
import ontopoly.model.AssociationType;
import ontopoly.model.NameType;
import ontopoly.model.OccurrenceType;
import ontopoly.model.RoleType;
import ontopoly.model.Topic;
import ontopoly.model.TopicMap;
import ontopoly.model.TopicType;

public class TopicModel<T extends Topic> extends MutableLoadableDetachableModel<T> {

  private static final long serialVersionUID = -8374148020034895666L;

  public static final int TYPE_TOPIC = 1;
  public static final int TYPE_ASSOCIATION_TYPE = 2;
  public static final int TYPE_ROLE_TYPE = 4;
  public static final int TYPE_NAME_TYPE = 8;
  public static final int TYPE_OCCURRENCE_TYPE = 16;
  public static final int TYPE_TOPIC_TYPE = 32;
  
  private int returnType = TYPE_TOPIC;
  
  private String topicMapId;
  private String topicId;
  
  public TopicModel(T topic) {
    super(topic);
    if (topic != null) {
      this.topicMapId = topic.getTopicMap().getId();
      this.topicId = topic.getId();
    }
  }

  public TopicModel(T topic, int returnType) {
    this(topic);
    this.returnType = returnType;
  }
  
  public TopicModel(String topicMapId, String topicId) {
    Objects.requireNonNull(topicMapId, "topicMapId parameter cannot be null.");
    Objects.requireNonNull(topicId, "topicId parameter cannot be null.");
    this.topicMapId = topicMapId;
    this.topicId = topicId;
  }
 
  public Topic getTopic() {    
    return getObject();
  }
  
  @SuppressWarnings("unchecked")
  @Override
  protected T load() {
    if (topicMapId == null) return null;
    TopicMap tm = OntopolyContext.getTopicMap(topicMapId);
    TopicIF topicIf = tm.getTopicIFById(topicId);
    if (topicIf == null) return null;
    switch (returnType) {
      case TYPE_ASSOCIATION_TYPE:
        return (T)new AssociationType(topicIf, tm);
      case TYPE_ROLE_TYPE:
        return (T)new RoleType(topicIf, tm);
      case TYPE_OCCURRENCE_TYPE:
        return (T)new OccurrenceType(topicIf, tm);
      case TYPE_NAME_TYPE:
        return (T)new NameType(topicIf, tm);
      case TYPE_TOPIC_TYPE:
        return (T)new TopicType(topicIf, tm);
      default:
        return (T)new Topic(topicIf, tm);
    }
  }

  @Override
  public void setObject(T object) {
    super.setObject(object);
    if (object == null) {
      this.topicMapId = null;
      this.topicId = null;      
    } else {
      Topic topic = (Topic)object;
      this.topicMapId = topic.getTopicMap().getId();
      this.topicId = topic.getId();
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof TopicModel)
      return Objects.equals(topicMapId, ((TopicModel)obj).topicMapId) &&
        Objects.equals(topicId, ((TopicModel)obj).topicId);
    else
      return false;
  }

  @Override
  public int hashCode() {
    return topicMapId.hashCode() + topicId.hashCode();
  }
  
}
