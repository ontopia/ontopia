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
package ontopoly.components;

import java.util.List;

import ontopoly.model.Topic;
import ontopoly.utils.TopicChoiceRenderer;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

public class TopicDropDownChoice<T extends Topic> extends DropDownChoice<T> {

  public TopicDropDownChoice(String id, IModel<T> model, IModel<List<T>> choices) {
    super(id, model, choices, new TopicChoiceRenderer<T>());
  }

  public TopicDropDownChoice(String id, IModel<T> model, IModel<List<T>> choices, IChoiceRenderer<T> renderer) {
    super(id, model, choices, renderer);
  }

  @Override
  protected void onComponentTag(ComponentTag tag) {
    tag.setName("select");
    super.onComponentTag(tag);
  }

}
