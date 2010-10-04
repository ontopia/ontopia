package ontopoly.components;

import java.util.List;

import ontopoly.model.FieldsViewIF;
import ontopoly.model.OntopolyTopicIF;
import ontopoly.model.TopicTypeIF;
import ontopoly.models.FieldInstanceModel;
import ontopoly.models.FieldsViewModel;
import ontopoly.models.TopicModel;
import ontopoly.models.TopicTypeModel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;

public abstract class InstancePanel extends Panel {
  private TopicModel topicModel;
  private TopicTypeModel topicTypeModel;
  private FieldsViewModel fieldsViewModel;
  
  private boolean isReadOnly;
  
  public InstancePanel(String id, TopicModel<OntopolyTopicIF> topicModel, TopicTypeModel topicTypeModel, FieldsViewModel fieldsViewModel, boolean _isReadOnly, boolean traversable) {
    super(id);
    this.topicModel = topicModel;
    this.topicTypeModel = topicTypeModel;
    this.fieldsViewModel = fieldsViewModel;
    this.isReadOnly = _isReadOnly;
    
    // Add lock panel
    if (isReadOnly) {
      add(new Label("lockPanel").setVisible(false));
    } else {
      LockPanel lockPanel = new LockPanel("lockPanel", topicModel, isReadOnly) {
        @Override
        protected void onLockLost(AjaxRequestTarget target, OntopolyTopicIF topic) {
          InstancePanel.this.onLockLost(target, topic);
        }
        @Override
        protected void onLockWon(AjaxRequestTarget target, OntopolyTopicIF topic) {        
          InstancePanel.this.onLockWon(target, topic);
        }
      };
      if (lockPanel.isLockedByOther()) isReadOnly = true;
      add(lockPanel);
    }

    // Add fields panel
    createFields(isReadOnly, traversable);
    
    // optional close button
    WebMarkupContainer instanceButtons = new WebMarkupContainer("instanceButtons") {
      @Override
      public boolean isVisible() {
        return isButtonsVisible();
      }     
    };
    instanceButtons.add(new Button("okButton", new ResourceModel("button.ok")));
    add(instanceButtons);
  }
  
  protected boolean isButtonsVisible() {
    return getPage().getPageParameters().getString("buttons") != null;
  }
  
  protected abstract void onLockLost(AjaxRequestTarget target, OntopolyTopicIF topic);
  
  protected abstract void onLockWon(AjaxRequestTarget target, OntopolyTopicIF topic);
  
  public boolean isReadOnly() {
    return isReadOnly;
  }
  
  private void createFields(boolean isReadOnly, boolean traversable) {
    OntopolyTopicIF topic = topicModel.getTopic();    
    TopicTypeIF type = topicTypeModel.getTopicType();
    TopicTypeIF specificType = topic.getMostSpecificTopicType(type);
    if (specificType == null)
      specificType = type;
    FieldsViewIF fieldsView = fieldsViewModel.getFieldsView();
    
    List<FieldInstanceModel> fieldInstanceModels = FieldInstanceModel.wrapInFieldInstanceModels(topic.getFieldInstances(specificType, fieldsView));
    FieldInstancesPanel fieldInstancesPanel = new FieldInstancesPanel("fieldsPanel", fieldInstanceModels, fieldsViewModel, isReadOnly, traversable);
    fieldInstancesPanel.setRenderBodyOnly(true);
    add(fieldInstancesPanel);    
  }

  @Override
  public void onDetach() {
    topicModel.detach();
    topicTypeModel.detach();
    fieldsViewModel.detach();
    super.onDetach();
  }
  
}
