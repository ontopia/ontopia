package ontopoly.components;

import java.io.Serializable;

import ontopoly.model.Cardinality;
import ontopoly.model.FieldInstance;
import ontopoly.models.FieldInstanceModel;
import ontopoly.models.FieldValuesModel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;

public abstract class AbstractFieldInstancePanel extends Panel {

  protected FieldInstanceModel fieldInstanceModel;
  protected FieldValuesModel fieldValuesModel;
  protected WebMarkupContainer fieldValuesContainer;
  protected WebMarkupContainer fieldInstanceButtons;  
  protected ListView listView;
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
   * Update any dependent components as the value of the field instance panel has changed.
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

  @Override
  protected void onDetach() {
    super.onDetach();
    fieldInstanceModel.detach();
    fieldValuesModel.detach();
  }

  protected class FieldUpdatingBehaviour extends AjaxFormComponentUpdatingBehavior {
    protected boolean updateListView;
    
    FieldUpdatingBehaviour(boolean updateListView) {
      super("onchange");
      this.updateListView = updateListView;
    }   
    @Override
    protected void onUpdate(AjaxRequestTarget target) {
      fieldValuesModel.setShowExtraField(false);
      if (updateListView)
        listView.removeAll();
      updateDependentComponents(target);
    }   
    protected void onError(AjaxRequestTarget target, RuntimeException e) {
      target.addComponent(feedbackPanel);
    }
  }
	
  protected void validateCardinality() {
    Cardinality card = fieldValuesModel.getFieldInstanceModel().getFieldInstance().getFieldAssignment().getCardinality();
    int size = fieldValuesModel.getNumberOfValues();
    if (card.isMinOne() && size < 1)
      error(createErrorMessage(fieldInstanceModel, new ResourceModel("validators.CardinalityValidator.toofew").getObject().toString()));
    else if (card.isMaxOne() && size > 1)
      error(createErrorMessage(fieldInstanceModel, new ResourceModel("validators.CardinalityValidator.toomany").getObject().toString()));      
  }
  
  protected class AbstractFieldInstancePanelFeedbackMessageFilter implements IFeedbackMessageFilter {

    public boolean accept(FeedbackMessage message) {
      Serializable value = message.getMessage();
      if (value instanceof FieldInstanceMessage) {
        FieldInstanceMessage fim = (FieldInstanceMessage)value;        
        return matchesThisField(fim);
      }
      return false;
    }
  }
 
  protected static class FieldInstanceMessage implements Serializable {
    private String identifier;
    private String message;
    public FieldInstanceMessage(String identifier, String message) {
      this.identifier = identifier;
      this.message = message;     
    }
    public String getIdentifier() {
      return identifier;
    }
    public String getMessage() {
      return message;
    }
    public String toString() {
      return message;
    }
  }
  
  protected boolean matchesThisField(FieldInstanceMessage fim) {
    return createIdentifier(fieldInstanceModel).equals(fim.getIdentifier());
  }
  
  protected static String createIdentifier(FieldInstanceModel fieldInstanceModel) {
    FieldInstance fieldInstance = fieldInstanceModel.getFieldInstance();
    return fieldInstance.getInstance().getId() + ':' + fieldInstance.getFieldAssignment().getFieldDefinition().getId();
  }
  
  public static Serializable createErrorMessage(FieldInstanceModel fieldInstanceModel, String message) {
    return new FieldInstanceMessage(createIdentifier(fieldInstanceModel), message);
  }
}
