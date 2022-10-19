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
import ontopoly.OntopolyContext;
import ontopoly.model.TopicMap;

import org.apache.wicket.model.LoadableDetachableModel;

public class TopicMapModel extends LoadableDetachableModel<TopicMap> {

  private static final long serialVersionUID = -6589204980069242599L;

  private String topicMapId;

  public TopicMapModel(TopicMap topicMap) {
    super(topicMap);
    if (topicMap != null) {
      this.topicMapId = topicMap.getId();
    }
  }

  public TopicMapModel(String topicMapId) {
    Objects.requireNonNull(topicMapId, "topicMapId parameter cannot be null.");
    this.topicMapId = topicMapId;    
  }

  public String getTopicMapId() {
    return topicMapId;
  }
  
  public TopicMap getTopicMap() {
    return (TopicMap)getObject();
  }

  @Override
  protected TopicMap load() {
    // retrive topicMap from ontopoly model
    return OntopolyContext.getTopicMap(topicMapId);
  }
}
