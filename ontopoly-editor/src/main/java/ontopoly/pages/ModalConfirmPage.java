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

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;

public abstract class ModalConfirmPage extends Panel {
  
  public ModalConfirmPage(String id) {
    super(id);

    final WebMarkupContainer popupContent = new WebMarkupContainer("popupContent");
    popupContent.setOutputMarkupId(true);
    add(popupContent);
    
    popupContent.add(getTitleComponent("title"));
    popupContent.add(getMessageComponent("message"));
    
    Button closeOkButton = new Button("closeOK");
    closeOkButton.add(new AjaxFormComponentUpdatingBehavior("onclick") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        // trigger callback
        onCloseOk(target);
      }
    });
    popupContent.add(closeOkButton);
    
    Button closeCancelButton = new Button("closeCancel");
    closeCancelButton.add(new AjaxFormComponentUpdatingBehavior("onclick") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        // trigger callback
        onCloseCancel(target);
      }
    });
    popupContent.add(closeCancelButton);
  }  
  
  protected abstract Component getTitleComponent(String id);

  protected abstract Component getMessageComponent(String id);
  
  protected abstract void onCloseOk(AjaxRequestTarget target);

  protected abstract void onCloseCancel(AjaxRequestTarget target);    

}
