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
package ontopoly.utils;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import ontopoly.model.RoleField;
import ontopoly.model.Topic;
import ontopoly.models.RoleFieldModel;
import ontopoly.models.TopicModel;

public class RoleFieldsValueComparator implements Comparator<Object>, Serializable {

  private TopicModel<Topic> topicModel;
  private List<RoleFieldModel> roleFieldModels;
  
  public RoleFieldsValueComparator(TopicModel<Topic> topicModel, List<RoleFieldModel> roleFieldModels) {
    this.topicModel = topicModel;
    this.roleFieldModels = roleFieldModels;
  }
  
  @Override
  public int compare(Object o1, Object o2) {
	RoleField.ValueIF rfv1 = (RoleField.ValueIF)o1;
	RoleField.ValueIF rfv2 = (RoleField.ValueIF)o2;
    for (int i=0; i < roleFieldModels.size(); i++) {
      RoleFieldModel roleFieldModel = roleFieldModels.get(i);
      RoleField roleField = roleFieldModel.getRoleField();
      Topic topic = topicModel.getTopic();
    
      Topic t1 = rfv1.getPlayer(roleField, topic);
      Topic t2 = rfv2.getPlayer(roleField, topic);
      int retval = TopicComparator.INSTANCE.compare(t1, t2);
      if (retval != 0) {
        return retval;
      }
    }
    return 0;
  }

}
