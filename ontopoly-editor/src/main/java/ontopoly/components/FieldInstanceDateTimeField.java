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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;
import net.ontopia.topicmaps.core.OccurrenceIF;
import ontopoly.jquery.DatePickerBehavior;
import ontopoly.model.FieldInstance;
import ontopoly.models.FieldValueModel;
import ontopoly.pages.AbstractOntopolyPage;
import ontopoly.validators.DateFormatValidator;
import ontopoly.validators.ExternalValidation;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.AbstractTextComponent.ITextFormatProvider;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;


public class FieldInstanceDateTimeField extends TextField<String> implements ITextFormatProvider {

  private FieldValueModel fieldValueModel;
  private String oldValue;
  private String cols = "21";
  
  public FieldInstanceDateTimeField(String id, FieldValueModel fieldValueModel) {
    super(id);
    this.fieldValueModel = fieldValueModel;
    
    OccurrenceIF occ = (OccurrenceIF)fieldValueModel.getObject();
    this.oldValue = (occ == null ? null : occ.getValue());
    setDefaultModel(new Model<String>(oldValue));

    add(new DatePickerBehavior("yy-mm-dd 12:00:00"));
    add(new DateFormatValidator(this, fieldValueModel.getFieldInstanceModel()) {
      @Override
      public DateFormat createDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      }
      @Override
      protected String resourceKey() {
        return super.resourceKey() + ".datetime";
      }
    });

    // validate field using registered validators
    ExternalValidation.validate(this, oldValue);
  }

  public void setCols(int cols) {
    this.cols = Integer.toString(cols);
  }
  
  @Override
  protected void onComponentTag(ComponentTag tag) {
    tag.setName("input");
    tag.put("type", "text");
    tag.put("size", cols);
    super.onComponentTag(tag);
  }
  
  @Override
  public String getTextFormat() {
    return "yyyy-MM-dd HH:mm:ss";
  }

  @Override
  protected void onModelChanged() {
    super.onModelChanged();
    // TODO: replace "HH:mm:ss" pattern with "12:00:00"
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
  
}
