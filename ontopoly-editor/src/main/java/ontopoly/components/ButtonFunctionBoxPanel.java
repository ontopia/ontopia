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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.IBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

public abstract class ButtonFunctionBoxPanel extends FunctionBoxPanel {

  public ButtonFunctionBoxPanel(String id) {
    super(id);
  }

  @Override
  protected List<List<Component>> getFunctionBoxComponentList(String id) {
    List<Component> heading = Arrays.asList(new Component[] { new Label(id, getText()) });
    List<Component> box = Arrays.asList(new Component[] { getButton(id) });
    List<List<Component>> result = new ArrayList<List<Component>>();
    result.add(heading);
    result.add(box);
    return result;    
  }

  protected Component getButton(String id) {

    AjaxLink<Object> button = new AjaxLink<Object>(id) {

      @Override
      protected void onComponentTag(ComponentTag tag) {
        tag.setName("input");
        tag.put("type", "button");
        tag.put("value", getButtonLabel().getObject().toString());
        super.onComponentTag(tag);
      }

      @Override
      public void onClick(AjaxRequestTarget target) {
        ButtonFunctionBoxPanel.this.onClick(target);
      }
    };

    List<IBehavior> behaviors = getButtonBehaviors();
    if (behaviors != null) {
      Iterator<IBehavior> it = behaviors.iterator();
      while (it.hasNext()) {
        button.add(it.next());
      }
    }
    return button;
  }

  protected abstract void onClick(AjaxRequestTarget target);

  protected abstract IModel<String> getText();

  protected abstract IModel<String> getButtonLabel();

  protected List<IBehavior> getButtonBehaviors() {
    return null;
  }
}
