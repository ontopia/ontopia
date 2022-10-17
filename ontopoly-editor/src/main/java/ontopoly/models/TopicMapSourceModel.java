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
import ontopoly.sysmodel.TopicMapSource;

public class TopicMapSourceModel extends MutableLoadableDetachableModel<TopicMapSource> {

  private String topicMapSourceId;

  public TopicMapSourceModel(TopicMapSource topicMapSource) {
    super(topicMapSource);
    Objects.requireNonNull(topicMapSource, "topicMapSource parameter cannot be null.");
       
    this.topicMapSourceId = topicMapSource.getId(); 
  }
  
  public TopicMapSourceModel(String topicMapSourceId) {
    Objects.requireNonNull(topicMapSourceId, "topicMapSourceId parameter cannot be null.");
    this.topicMapSourceId = topicMapSourceId;    
  }

  public TopicMapSource getTopicMapSource() {
    return (TopicMapSource)getObject();
  }

  @Override
  public void setObject(TopicMapSource source) {
    super.setObject(source);
    this.topicMapSourceId = source.getId(); 
  }

  @Override
  protected TopicMapSource load() {
    // retrieve topicMapSource from ontopoly repository
    return new TopicMapSource(topicMapSourceId);
  }
}
