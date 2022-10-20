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
package ontopoly.pages;

import ontopoly.model.Topic;
import ontopoly.models.TopicModel;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;

public class EnterTopicPage extends AbstractProtectedOntopolyPage {

  public EnterTopicPage() {	  
  }
  
  public EnterTopicPage(PageParameters parameters) {
	super(parameters);
	
    Topic topic = new TopicModel<Topic>(parameters.getString("topicMapId"), parameters.getString("topicId")).getTopic();
    
    Class<? extends Page> pageClass;
    if (topic.isTopicType()) {
      pageClass = InstancesPage.class;
//    else if (topic.isAssociationType())
//      pageClass = InstancePage.class;
//    else if (topic.isOccurrenceType())
//      pageClass = InstancePage.class;
//    else if (topic.isNameType())
//      pageClass = InstancePage.class;
//    else if (topic.isRoleType())
//      pageClass = InstancePage.class;
  } else {
      pageClass = InstancePage.class;
  }
    
    // redirect page
    PageParameters params = new PageParameters();
    params.add("topicMapId", topic.getTopicMap().getId());
    params.add("topicId", topic.getId());
    setResponsePage(pageClass, params);
    setRedirect(true);
  }
  
}
