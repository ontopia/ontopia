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

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

public abstract class CustomLinkFunctionBoxPanel extends LinkFunctionBoxPanel {

  public CustomLinkFunctionBoxPanel(String id) {
    super(id);
  }

  protected abstract IModel<String> getFirstResourceModel();

  protected abstract IModel<String> getSecondResourceModel();
  
  @Override
  protected Label getLabel(String id) {
    return new Label(id) {
      @Override
      protected void onComponentTagBody(MarkupStream markupStream,
          ComponentTag openTag) {
        replaceComponentTagBody(markupStream, openTag, 
              getFirstResourceModel().getObject()
            + "<span class='emphasis'>"
            + getSecondResourceModel().getObject()
            + "</span>");
      }
    };
  }
}
