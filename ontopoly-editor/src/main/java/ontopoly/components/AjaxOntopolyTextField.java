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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

public class AjaxOntopolyTextField extends TextField<String> {

  private String cols;

  public AjaxOntopolyTextField(String id, IModel<String> model) {
    super(id, model);
    
    setOutputMarkupId(true);

    add(new AjaxFormComponentUpdatingBehavior("onchange") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        AjaxOntopolyTextField.this.onUpdate(target);
      }
    });
  }

  public void setCols(int cols) {
    this.cols = Integer.toString(cols);
  }
  
  @Override
  protected void onComponentTag(ComponentTag tag) {
    tag.setName("input");
    tag.put("type", "text");
    tag.put("size", (cols != null ? cols : new ResourceModel("textfield.default.size").getObject().toString()));
    super.onComponentTag(tag);
  }
  
  protected void onUpdate(AjaxRequestTarget target) { /* no-op */ }
  
}
