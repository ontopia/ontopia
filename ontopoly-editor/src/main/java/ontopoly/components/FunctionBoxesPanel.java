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

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

public abstract class FunctionBoxesPanel extends Panel {

  public FunctionBoxesPanel(String id) {
    super(id);
    
    Form<Object> form = new Form<Object>("functionBoxesForm");
    add(form);
    
    List<Component> list = getFunctionBoxesList("functionBox"); 
    ListView<Component> functionBoxes = new ListView<Component>("functionBoxesList", list) {
      protected void populateItem(ListItem<Component> item) {
        item.add(item.getModelObject());
      }
    };
    functionBoxes.setVisible(!list.isEmpty());
    form.add(functionBoxes);
  }

  protected abstract List<Component> getFunctionBoxesList(String id);
}
