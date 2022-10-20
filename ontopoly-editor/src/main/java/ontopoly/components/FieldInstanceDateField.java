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
import org.apache.wicket.validation.IValidatable;

public class FieldInstanceDateField extends TextField<String> implements ITextFormatProvider, IValidatable<String> {

  private FieldValueModel fieldValueModel;
  private String oldValue;
  private String cols = "10";
  private static final String DATE_FORMAT = "yyyy-MM-dd";
  
  public FieldInstanceDateField(String id, FieldValueModel fieldValueModel) {
    super(id);
    this.fieldValueModel = fieldValueModel;
    
    OccurrenceIF occ = (OccurrenceIF)fieldValueModel.getObject();
    this.oldValue = (occ == null ? null : occ.getValue());
    setModel(new Model<String>(oldValue));

    // NOTE: the date format syntax used for DatePickerBehavior is the jquery
    // date format syntax, which is NOT the same as the Java syntax. it's
    // documented here: http://docs.jquery.com/UI/Datepicker/formatDate
    add(new DatePickerBehavior("yy-mm-dd")); // yy-mm-dd == 2013-01-18
    add(new DateFormatValidator(this, fieldValueModel.getFieldInstanceModel()) {
      @Override
      public DateFormat createDateFormat() {
        return new SimpleDateFormat(DATE_FORMAT);
      }
      @Override
      protected String resourceKey() {
        return super.resourceKey() + ".date";
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
    return DATE_FORMAT;
  }

  @Override
  protected void onModelChanged() {
    super.onModelChanged();
    String newValue = (String)getModelObject();
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
