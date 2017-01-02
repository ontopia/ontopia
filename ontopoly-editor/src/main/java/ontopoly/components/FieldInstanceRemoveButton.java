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

import ontopoly.model.FieldInstance;
import ontopoly.models.FieldValueModel;
import ontopoly.pages.AbstractOntopolyPage;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

public class FieldInstanceRemoveButton extends OntopolyImageLink {

  private FieldValueModel fieldValueModel;
  
  public FieldInstanceRemoveButton(String id, String image, FieldValueModel fieldValueModel) {
    super(id, image);
    this.fieldValueModel = fieldValueModel;
  }
  
  @Override
  public boolean isVisible() {
    return fieldValueModel.isExistingValue();
  }

  @Override
  public void onClick(AjaxRequestTarget target) {
    AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
    FieldInstance fieldInstance = fieldValueModel.getFieldInstanceModel().getFieldInstance();
    Object value = fieldValueModel.getObject();
    fieldInstance.removeValue(value, page.getListener());        
  }
  
  @Override
  public IModel<String> getTitleModel() {
    return new ResourceModel("icon.remove.remove-value");
  }
  
  @Override
  protected void onDetach() {
    super.onDetach();
    fieldValueModel.detach();
  }

}
