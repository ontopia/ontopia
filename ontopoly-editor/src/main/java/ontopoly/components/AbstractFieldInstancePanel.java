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

import java.io.Serializable;

import ontopoly.model.Cardinality;
import ontopoly.model.FieldInstance;
import ontopoly.models.FieldInstanceModel;
import ontopoly.models.FieldValueModel;
import ontopoly.models.FieldValuesModel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

public abstract class AbstractFieldInstancePanel extends Panel {

  protected FieldInstanceModel fieldInstanceModel;
  protected FieldValuesModel fieldValuesModel;
  protected WebMarkupContainer fieldValuesContainer;
  protected WebMarkupContainer fieldInstanceButtons;  
  protected ListView<FieldValueModel> listView;
  protected FeedbackPanel feedbackPanel;
  
  public AbstractFieldInstancePanel(String id, FieldInstanceModel fieldInstanceModel) {
    super(id);
    this.fieldInstanceModel = fieldInstanceModel;
  }

  public FieldInstanceModel getFieldInstanceModel() {
    return fieldInstanceModel;
  }
	
  public FieldValuesModel getFieldValuesModel() {
    return fieldValuesModel;
  }

  /**
   * Update any dependent components as the value of the field
   * instance panel has changed.
   */  
  protected void updateDependentComponents(AjaxRequestTarget target) {
    target.addComponent(fieldValuesContainer);
    target.addComponent(fieldInstanceButtons);
  }
 	
  public void onUpdate(AjaxRequestTarget target) {
    // NOTE: callback to use when entire field needs to be refreshed
    listView.removeAll();
    updateDependentComponents(target);
  }
  
  public void onError(AjaxRequestTarget target, RuntimeException e) {
    // NOTE: callback to use when entire field needs to be refreshed
    updateDependentComponents(target);
    e.printStackTrace();
  }

  protected void addNewFieldValueCssClass(WebMarkupContainer component, FieldValuesModel fieldValuesModel, FieldValueModel fieldValueModel) {
    // add css class if field is new, there are no other exiting values, and the cardinality requires at least one value
//    Cardinality cardinality = fieldValuesModel.getFieldInstanceModel().getFieldInstance().getFieldAssignment().getCardinality();
//    if (!fieldValueModel.isExistingValue() && !fieldValuesModel.containsExisting() && cardinality.isMinOne()) 
//      component.add(new SimpleAttributeModifier("class", "newFieldValue"));
    
    // add css class if field value is new, and the display of it was user triggered.
    if (!fieldValueModel.isExistingValue() && fieldValuesModel.getShowExtraField() && fieldValuesModel.getShowExtraFieldUserTriggered()) {
      component.add(new SimpleAttributeModifier("class", "newFieldValue"));
    }
  }

  @Override
  protected void onDetach() {
    super.onDetach();
    fieldInstanceModel.detach();
    if (fieldValuesModel != null) {
      fieldValuesModel.detach();
    }
  }

  protected class FieldUpdatingBehaviour extends AjaxFormComponentUpdatingBehavior {
    protected boolean updateListView;
    
    FieldUpdatingBehaviour(boolean updateListView) {
      super("onchange");
      this.updateListView = updateListView;
    }   
    @Override
    protected void onUpdate(AjaxRequestTarget target) {
      fieldValuesModel.setShowExtraField(false, false);
      if (updateListView) {
        listView.removeAll();
      }
      updateDependentComponents(target);
    }   
    @Override
    protected void onError(AjaxRequestTarget target, RuntimeException e) {
      target.addComponent(feedbackPanel);
    }
  }
	
  protected void validateCardinality() {
    Cardinality card = fieldValuesModel.getFieldInstanceModel().getFieldInstance().getFieldAssignment().getCardinality();
    int size = fieldValuesModel.getNumberOfValues();
    if (card.isMinOne() && size < 1) {
      error(createErrorMessage(fieldInstanceModel, new ResourceModel("validators.CardinalityValidator.toofew")));
    } else if (card.isMaxOne() && size > 1) {
      error(createErrorMessage(fieldInstanceModel, new ResourceModel("validators.CardinalityValidator.toomany")));
    }      
  }
  
  protected class AbstractFieldInstancePanelFeedbackMessageFilter implements IFeedbackMessageFilter {

    @Override
    public boolean accept(FeedbackMessage message) {
      Serializable value = message.getMessage();
      if (value instanceof AbstractFieldInstanceMessage<?>) {
        return matchesThisField((AbstractFieldInstanceMessage<?>)value);
      }
      return false;
    }
  }
 
  protected static abstract class AbstractFieldInstanceMessage<T> implements Serializable {
    private String identifier;
    private T message;
    
    public AbstractFieldInstanceMessage(String identifier, T message) {
      this.identifier = identifier;
      this.message = message;     
    }
    public String getIdentifier() {
      return identifier;
    }    
    public T getMessage() {
      return message;
    }
    @Override
    public String toString() {
      return getMessage().toString();
    }    
  }
  
  protected boolean matchesThisField(AbstractFieldInstanceMessage<?> fim) {
    return createIdentifier(fieldInstanceModel).equals(fim.getIdentifier());
  }
  
  protected static String createIdentifier(FieldInstanceModel fieldInstanceModel) {
    FieldInstance fieldInstance = fieldInstanceModel.getFieldInstance();
    return fieldInstance.getInstance().getId() + ':' + fieldInstance.getFieldAssignment().getFieldDefinition().getId();
  }
  
  public static Serializable createErrorMessage(FieldInstanceModel fieldInstanceModel, IModel<String> message) {
    return new AbstractFieldInstanceMessage<IModel<String>>(createIdentifier(fieldInstanceModel), message) {      
      @Override
      public String toString() {
        return getMessage().getObject();
      }
    };
  }
  
  public static Serializable createErrorMessage(FieldInstanceModel fieldInstanceModel, Throwable t) {
    return new AbstractFieldInstanceMessage<Throwable>(createIdentifier(fieldInstanceModel), t) {
      @Override
      public String toString() {
        return getMessage().getMessage();
      }
    };
  }

}
