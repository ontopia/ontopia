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

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

public abstract class ContextMenuPanel<T> extends Panel implements IHeaderContributor {

  private ResourceReference jsReference = new ResourceReference(ContextMenuPanel.class, "ContextMenuPanel.js");
  private ResourceReference cssReference = new ResourceReference(ContextMenuPanel.class, "ContextMenuPanel.css");
  
  public ContextMenuPanel(String id, final String menuId) {
    super(id);
    WebMarkupContainer container = new WebMarkupContainer("contextMenu") {      
      @Override
      protected void onComponentTag(ComponentTag tag) {
        tag.put("id", "m" + menuId);
        super.onComponentTag(tag);
      }      
    };
    add(container);    
    container.add(createListView("menu", "menuitem"));
  }

  protected abstract ListView<T> createListView(String menuId, String menuItemId);
  
  public void renderHead(IHeaderResponse response) {
    // import script    
    response.renderJavascriptReference(jsReference);
    response.renderCSSReference(cssReference);
  }

}
