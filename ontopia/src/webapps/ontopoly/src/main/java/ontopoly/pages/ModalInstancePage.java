package ontopoly.pages;

import net.ontopia.utils.ObjectUtils;
import ontopoly.components.InstancePanel;
import ontopoly.model.OntopolyTopicIF;
import ontopoly.models.FieldsViewModel;
import ontopoly.models.TopicModel;
import ontopoly.models.TopicTypeModel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;

public abstract class ModalInstancePage extends Panel {
  
  WebMarkupContainer popupContent;
  TopicModel<OntopolyTopicIF> topicModel;
  TopicTypeModel topicTypeModel;
  FieldsViewModel fieldsViewModel;
  boolean isReadOnly;
  boolean traversable = false; // FIXME: hardcoded
  
  public ModalInstancePage(String id, TopicModel<OntopolyTopicIF> topicModel, TopicTypeModel topicTypeModel, FieldsViewModel fieldsViewModel) {
    super(id);
    this.topicModel = topicModel;
    this.topicTypeModel = topicTypeModel;
    this.fieldsViewModel = fieldsViewModel;
    
    // page is read-only if topic type is read-only
    this.isReadOnly = ((topicTypeModel != null && topicTypeModel.getTopicType().isReadOnly()) || (ObjectUtils.equals(getRequest().getParameter("ro"), "true")));

    this.popupContent = new WebMarkupContainer("popupContent");
    popupContent.setOutputMarkupId(true);
    add(popupContent);
    
    popupContent.add(createInstancePanel("instancePanel"));
    
    Button closeOkButton = new Button("closeOK");
    closeOkButton.add(new AjaxFormComponentUpdatingBehavior("onclick") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        onCloseOk(target);
      }
    });
    popupContent.add(closeOkButton);
  }  

  protected abstract void onCloseOk(AjaxRequestTarget target);

  private InstancePanel createInstancePanel(final String id) {
    return new InstancePanel(id, topicModel, topicTypeModel, fieldsViewModel, isReadOnly, traversable) {
      @Override
      protected boolean isButtonsVisible() {
        return false; // Don't show buttons as there will already be a set of buttons visible.
      }
      @Override
      protected void onLockLost(AjaxRequestTarget target, OntopolyTopicIF topic) {
        popupContent.replace(createInstancePanel(id));
        target.addComponent(popupContent);        
      }      
      @Override
      protected void onLockWon(AjaxRequestTarget target, OntopolyTopicIF topic) {
        popupContent.replace(createInstancePanel(id));
        target.addComponent(popupContent);        
      }      
    };
  }

  @Override
  protected void onDetach() {
    topicModel.detach();
    topicTypeModel.detach();
    fieldsViewModel.detach();
    super.onDetach();
  }
}
