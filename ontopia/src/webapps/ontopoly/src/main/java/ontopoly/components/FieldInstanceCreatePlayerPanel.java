package ontopoly.components;

import java.util.Collection;

import ontopoly.model.FieldInstance;
import ontopoly.model.RoleField;
import ontopoly.model.Topic;
import ontopoly.model.TopicType;
import ontopoly.models.FieldInstanceModel;
import ontopoly.models.FieldsViewModel;
import ontopoly.models.PlayerTypesModel;
import ontopoly.models.RoleFieldModel;
import ontopoly.models.TopicModel;
import ontopoly.models.TopicTypeModel;
import ontopoly.pages.AbstractOntopolyPage;
import ontopoly.pages.ModalInstancePage;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;

public abstract class FieldInstanceCreatePlayerPanel extends Panel {

  public static final int CREATE_ACTION_NONE = 1;
  public static final int CREATE_ACTION_POPUP = 2;
  public static final int CREATE_ACTION_NAVIGATE = 4;
  
  FieldInstanceModel fieldInstanceModel;
  FieldsViewModel fieldsViewModel;
  RoleFieldModel roleFieldModel;
  AbstractFieldInstancePanel fieldInstancePanel;
  
  int createAction;
  
  public FieldInstanceCreatePlayerPanel(String id, 
      FieldInstanceModel _fieldInstanceModel, FieldsViewModel fieldsViewModel, 
      RoleFieldModel _roleFieldModel, AbstractFieldInstancePanel fieldInstancePanel, int createAction) {
    super(id);
    this.fieldInstanceModel = _fieldInstanceModel;
    this.fieldsViewModel = fieldsViewModel;
    this.roleFieldModel = _roleFieldModel;
    this.fieldInstancePanel = fieldInstancePanel;
    this.createAction = createAction;
    
    RoleField associationField = roleFieldModel.getRoleField();
    Collection allowedValueTypes = associationField.getAllowedPlayerTypes(_fieldInstanceModel.getFieldInstance().getInstance());
    if (allowedValueTypes.isEmpty()) {
      setVisible(false);
      add(new Label("button"));
      add(new Label("createMenu"));
      add(new Label("createModal"));
    } else if (allowedValueTypes.size() == 1) {
      final TopicTypeModel topicTypeModel = new TopicTypeModel((TopicType)allowedValueTypes.iterator().next());
      AjaxLink createLink = new AjaxLink("button") {
        @Override
        public void onClick(AjaxRequestTarget target) {
          FieldInstanceCreatePlayerPanel.this.onClick(target, topicTypeModel.getTopicType());
        }        
      };
      add(createLink);
      add(new Label("createMenu").setVisible(false));
      add(new Label("createModal").setVisible(false));
    } else  {
      final String menuId = id + "_" + 
        associationField.getId() + "_" + 
        _fieldInstanceModel.getFieldInstance().getInstance().getId();
      
      add(new WebMarkupContainer("button") {      
        @Override
        protected void onComponentTag(ComponentTag tag) {
          tag.put("onclick", "menuItemPopup(this)");
          tag.put("id", "main" + menuId);
          super.onComponentTag(tag);
        }      
      });
      add(new ContextMenuPanel("createMenu", menuId) {
        @Override
        protected ListView createListView(final String menuId, final String menuItemId) {
          return new ListView<TopicType>(menuId, new PlayerTypesModel(fieldInstanceModel, roleFieldModel)) {
            @Override
            public void populateItem(final ListItem<TopicType> item) {
              AjaxLink createLink = new AjaxLink(menuItemId) {
                @Override
                public void onClick(AjaxRequestTarget target) {
                  FieldInstanceCreatePlayerPanel.this.onClick(target, item.getModelObject());
                }
              };
              createLink.add(new Label("label", item.getModelObject().getName()));
              item.add(createLink);
            }
          };
        }
      });      
      add(new Label("createModal").setVisible(false));
    }
  }
  
  protected void onClick(AjaxRequestTarget target, TopicType selectedTopicType) {
    // create instance and redirect
    Topic instance = createInstance(selectedTopicType);
    if (instance == null) {
      hideInstancePage(target);
    } else if (createAction == CREATE_ACTION_POPUP) {
      showInstancePage(target, instance, selectedTopicType, this);
    } else if (createAction == CREATE_ACTION_NAVIGATE) {
      AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
      setResponsePage(page.getPageClass(instance), page.getPageParameters(instance));
      setRedirect(true);
    } else {
      hideInstancePage(target);
    }
  }
  
  protected Topic createInstance(TopicType topicType) {

    FieldInstance fieldInstance = fieldInstanceModel.getFieldInstance();
    Topic currentTopic = fieldInstance.getInstance();
    RoleField currentField = (RoleField)fieldInstance.getFieldAssignment().getFieldDefinition();
    Topic createdTopic = null;
    RoleField createField = roleFieldModel.getRoleField();

    // check with page to see if create is allowed
    AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
    if (page.isCreateAllowed(currentTopic, currentField, topicType, createField)) {
    
      // create a new topic instance
      createdTopic = topicType.createInstance(null);
      
      performNewSelection(roleFieldModel, createdTopic);
      
//      // create association; selected player
//      RoleField.ValueIF value = RoleField.createValue(2);
//      value.addPlayer(createField, createdTopic);
//      
//      // create association; parent player
//      value.addPlayer(currentField, currentTopic);
//      
//      // create association and return instance
//      fieldInstance.addValue(value);
    }
    return createdTopic;
  }
  
  protected abstract void performNewSelection(RoleFieldModel ofieldModel, Topic selectedTopic);

    
  protected void showInstancePage(AjaxRequestTarget target, Topic topic, TopicType topicType, Component c) {
    // open modal window
    final ModalWindow createModal = new ModalWindow("createModal");
    TopicModel<Topic> topicModel = new TopicModel<Topic>(topic);
    TopicTypeModel topicTypeModel = new TopicTypeModel(topicType);
    createModal.setContent(new ModalInstancePage(createModal.getContentId(), topicModel, topicTypeModel, fieldsViewModel) {
      @Override
      protected void onCloseOk(AjaxRequestTarget target) {
    	  // close modal and update parent
          createModal.close(target);              
          FieldInstanceCreatePlayerPanel.this.hideInstancePage(target);
      }
    });
    createModal.setTitle(new ResourceModel("ModalWindow.title.edit.new").getObject().toString() + topicType.getName() + "...");
    createModal.setCookieName("createModal");
    
    createModal.setCloseButtonCallback(new ModalWindow.CloseButtonCallback() {
      public boolean onCloseButtonClicked(AjaxRequestTarget target) {
    	// modal already closed, now update parent 
        FieldInstanceCreatePlayerPanel.this.hideInstancePage(target);
        return true;
      }
    });
    
    replace(createModal);    
    createModal.show(target);
    target.addComponent(this);
  }

  protected void hideInstancePage(AjaxRequestTarget target) {
    fieldInstancePanel.onUpdate(target);
  }
  
  @Override
  protected void onDetach() {
    super.onDetach();
    fieldInstanceModel.detach();
    fieldsViewModel.detach();
    roleFieldModel.detach();
  }

}
