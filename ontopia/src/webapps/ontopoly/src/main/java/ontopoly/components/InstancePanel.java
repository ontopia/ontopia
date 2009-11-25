package ontopoly.components;

import java.util.List;

import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.FieldsView;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.Topic;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.TopicType;
import ontopoly.models.FieldInstanceModel;
import ontopoly.models.FieldsViewModel;
import ontopoly.models.TopicModel;
import ontopoly.models.TopicTypeModel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;

public abstract class InstancePanel extends Panel {

  private TopicModel topicModel;
  private TopicTypeModel topicTypeModel;
  private FieldsViewModel fieldsViewModel;
  
  private boolean isReadOnly;
  
  public InstancePanel(String id, TopicModel<Topic> topicModel, TopicTypeModel topicTypeModel, FieldsViewModel fieldsViewModel, boolean _isReadOnly, boolean traversable) {
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
        protected void onLockLost(AjaxRequestTarget target, Topic topic) {
          InstancePanel.this.onLockLost(target, topic);
        }
        @Override
        protected void onLockWon(AjaxRequestTarget target, Topic topic) {        
          InstancePanel.this.onLockWon(target, topic);
        }
      };
      if (lockPanel.isLockedByOther()) isReadOnly = true;
      add(lockPanel);
    }

    // Add fields panel
    createFields(isReadOnly, traversable);
    
    // optional close button
    Button button = new Button("okButton", new ResourceModel("button.ok")) {
      @Override
      public boolean isVisible() {
        return isButtonsVisible();
      }
    };
    add(button);
  }
  
  protected boolean isButtonsVisible() {
    return getPage().getPageParameters().getString("buttons") != null;
  }
  
  protected abstract void onLockLost(AjaxRequestTarget target, Topic topic);
  
  protected abstract void onLockWon(AjaxRequestTarget target, Topic topic);
  
  public boolean isReadOnly() {
    return isReadOnly;
  }
  
  private void createFields(boolean isReadOnly, boolean traversable) {
    Topic topic = topicModel.getTopic();    
    TopicType type = topicTypeModel.getTopicType();
    TopicType specificType = topic.getMostSpecificTopicType(type);
    if (specificType == null)
      specificType = type;
    FieldsView fieldsView = fieldsViewModel.getFieldsView();
    
    List<FieldInstanceModel> fieldInstanceModels = FieldInstanceModel.wrapInFieldInstanceModels(topic.getFieldInstances(specificType, fieldsView));
    add(new FieldInstancesPanel("fieldsPanel", fieldInstanceModels, fieldsViewModel, isReadOnly, traversable));    
  }

  @Override
  public void onDetach() {
    topicModel.detach();
    topicTypeModel.detach();
    fieldsViewModel.detach();
    super.onDetach();
  }
  
}
