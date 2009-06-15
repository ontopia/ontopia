package ontopoly.components;

import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.Cardinality;
import ontopoly.models.FieldInstanceModel;
import ontopoly.models.FieldValuesModel;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.lang.Objects;

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
      error(new ResourceModel("validators.CardinalityValidator.toofew").getObject().toString());
    else if (card.isMaxOne() && size > 1)
      error(new ResourceModel("validators.CardinalityValidator.toomany").getObject().toString());      
  }
  
  protected class AbstractFieldInstancePanelFeedbackMessageFilter implements IFeedbackMessageFilter {

    public boolean accept(FeedbackMessage message) {
      Component current = message.getReporter();
      while (true) {
        if (current == null) return false;
        if (current instanceof AbstractFieldInstancePanel)
          return (Objects.equal(AbstractFieldInstancePanel.this, current));                    
        current = current.getParent();
      }
    }
  }
  
}
