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
import ontopoly.model.TopicMap;
import ontopoly.model.TopicType;

import org.apache.wicket.model.LoadableDetachableModel;

public class TopicTypeModel extends LoadableDetachableModel<TopicType> {

  private static final long serialVersionUID = -8374148020034895666L;

  private String topicMapId;

  private String topicId;

  public TopicTypeModel(TopicType topicType) {
    super(topicType);
    if (topicType != null) {
      this.topicMapId = topicType.getTopicMap().getId();
      this.topicId = topicType.getId();
    }
  }
  
  public TopicTypeModel(String topicMapId, String topicId) {
    Objects.requireNonNull(topicMapId, "topicMapId parameter cannot be null.");
    Objects.requireNonNull(topicId, "topicId parameter cannot be null.");
    this.topicMapId = topicMapId;
    this.topicId = topicId;
  }

  public TopicType getTopicType() {
    return getObject();
  }

  @Override
  protected TopicType load() {
    if (topicMapId == null) return null;
    TopicMap tm = OntopolyContext.getTopicMap(topicMapId);
    TopicIF topicIf = tm.getTopicIFById(topicId);
    return new TopicType(topicIf, tm);
  }
}
