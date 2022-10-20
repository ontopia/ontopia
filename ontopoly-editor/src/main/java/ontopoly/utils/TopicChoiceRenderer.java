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

import ontopoly.model.Topic;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

public class TopicChoiceRenderer<T extends Topic> implements IChoiceRenderer<T> {

  public static final TopicChoiceRenderer<Topic> INSTANCE = new TopicChoiceRenderer<Topic>();
  
  @SuppressWarnings("rawtypes")
  protected Topic getTopic(Object object) {
    // model objects are supported
    return (Topic)(object instanceof IModel ? ((IModel)object).getObject() : object);    
  }
  
  @Override
  public Object getDisplayValue(Topic object) {
    String name = object.getName();
    if (name == null || name.equals("")) {
      return "[No name]";
    } else {
      return name;
    }
  }

  @Override
  public String getIdValue(Topic object, int index) {
    Topic topic = getTopic(object);
    return topic.getId();
  }

}
