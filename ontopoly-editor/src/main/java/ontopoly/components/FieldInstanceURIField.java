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

import java.util.Objects;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import ontopoly.model.FieldDefinition;
import ontopoly.model.FieldInstance;
import ontopoly.models.FieldValueModel;
import ontopoly.pages.AbstractOntopolyPage;
import ontopoly.validators.ExternalValidation;
import ontopoly.validators.IdentityValidator;
import ontopoly.validators.URIValidator;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;


public class FieldInstanceURIField extends Panel {

  protected FieldValueModel fieldValueModel;
  protected String oldValue;
  protected TextField<String> textField;
  protected String cols = "60";
  protected ExternalLink button;
  
  public FieldInstanceURIField(String id, FieldValueModel _fieldValueModel) {
    super(id);
    this.fieldValueModel = _fieldValueModel;
    
    if (!fieldValueModel.isExistingValue()) {
      this.oldValue = null;
    } else {
      Object value = fieldValueModel.getObject();
      if (value instanceof OccurrenceIF) {
        OccurrenceIF occ = (OccurrenceIF)value;
        this.oldValue = occ.getValue();
      } else if (value instanceof LocatorIF) {      
        LocatorIF identity = (LocatorIF)value;
        this.oldValue = identity.getAddress();      
      } else {
        throw new RuntimeException("Unsupported field value: " + value);
      }
    }

    this.textField = new TextField<String>("input", new Model<String>(oldValue)) {      
      @Override
      public boolean isEnabled() {
        return FieldInstanceURIField.this.isEnabled();
      }
      @Override
      protected void onComponentTag(ComponentTag tag) {
        tag.setName("input");
        tag.put("type", "text");
        tag.put("size", cols);
        super.onComponentTag(tag);
      }
      
      @Override
      protected void onModelChanged() {
        super.onModelChanged();
        String newValue = getModelObject();
        if (Objects.equals(newValue, oldValue)) {
          return;
        }
        AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
        FieldInstance fieldInstance = fieldValueModel.getFieldInstanceModel().getFieldInstance();
        if (fieldValueModel.isExistingValue() && oldValue != null) {
          fieldInstance.removeValue(oldValue, page.getListener());
        }
        if (newValue != null && !newValue.equals("")) {
          fieldInstance.addValue(newValue, page.getListener());
          fieldValueModel.setExistingValue(newValue);
        }
        oldValue = newValue;
      }
      
    };
    if (fieldValueModel.getFieldInstanceModel().getFieldType() == FieldDefinition.FIELD_TYPE_IDENTITY) {
        textField.add(new IdentityValidator(this, fieldValueModel.getFieldInstanceModel()));
    } else { 
        textField.add(new URIValidator(this, fieldValueModel.getFieldInstanceModel()));
    }
    add(textField);
    
    this.button = new ExternalLink("button", new AbstractReadOnlyModel<String>() {
          @Override
          public String getObject() {
            return textField.getModelObject();
          }      
        }) {

      @Override
      public boolean isVisible() {
        return textField.getModelObject() != null;
      }      
    };
    button.setOutputMarkupId(true);
    button.setPopupSettings(new PopupSettings(PopupSettings.LOCATION_BAR | PopupSettings.MENU_BAR | 
                                              PopupSettings.RESIZABLE | PopupSettings.SCROLLBARS | 
                                              PopupSettings.STATUS_BAR | PopupSettings.TOOL_BAR));
    button.add(new OntopolyImage("icon", "goto.gif"));
    add(button);

    // validate field using registered validators
    ExternalValidation.validate(textField, oldValue);
  }

  public TextField<String> getTextField() {
    return textField;
  }
  
  public ExternalLink getLinkButton() {
    return button;
  }
  
  public void setCols(int cols) {
    this.cols = Integer.toString(cols);
  }
  
}
