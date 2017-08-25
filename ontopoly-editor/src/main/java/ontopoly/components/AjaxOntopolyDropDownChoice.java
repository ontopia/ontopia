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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

public class AjaxOntopolyDropDownChoice<T> extends DropDownChoice<T> {
   
  public AjaxOntopolyDropDownChoice(String id, IModel<T> model, IModel<List<T>> choices, IChoiceRenderer<? super T> renderer) {
    super(id, model, choices, renderer);

    setOutputMarkupId(true);
  
    add(new AjaxFormComponentUpdatingBehavior("onchange") {
      protected void onUpdate(AjaxRequestTarget target) {
        AjaxOntopolyDropDownChoice.this.onUpdate(target);
      }
    });
  }

  @Override
  protected void onComponentTag(ComponentTag tag) {
    tag.setName("select");
    super.onComponentTag(tag);
  }

  protected void onUpdate(AjaxRequestTarget target) { /* no-op */ }
  
}
