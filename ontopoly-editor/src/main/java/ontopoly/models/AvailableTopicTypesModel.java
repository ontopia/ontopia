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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import ontopoly.model.Topic;
import ontopoly.model.TopicMap;
import ontopoly.model.TopicType;
import ontopoly.utils.TopicComparator;

import org.apache.wicket.model.LoadableDetachableModel;

public abstract class AvailableTopicTypesModel extends LoadableDetachableModel<List<TopicType>> {

  private TopicModel<Topic> topicModel;  
  
  public AvailableTopicTypesModel(TopicModel<Topic> topicModel) {
    this.topicModel = topicModel;
  }
  
  protected boolean getShouldIncludeSelf() {
    return false;
  }
  
  protected boolean getShouldIncludeExistingTypes() {
    return false;
  }
  
  @Override
  protected List<TopicType> load() {
    // FIXME: use sorted set instead?
    Collection<TopicType> types = new HashSet<TopicType>();
    Topic topic = topicModel.getTopic();
    TopicMap topicMap = topic.getTopicMap();
    types.addAll(topicMap.getTopicTypes());
    if (!getShouldIncludeSelf()) { 
      types.remove(topic); // remove topic itself as it cannot be an instance of itself
    }
    if (!getShouldIncludeExistingTypes()) {
      types.removeAll(topic.getTopicTypes());
    }
    List<TopicType> result = new ArrayList<TopicType>(types.size()); 
    Iterator<TopicType> iter = types.iterator();
    while (iter.hasNext()) {
      TopicType o = iter.next();
      if (filter(o)) {
        result.add(o);
      }
    }
    Collections.sort(result, TopicComparator.INSTANCE);
    return result;
  }
  
  protected abstract boolean filter(Topic o);
  
}
