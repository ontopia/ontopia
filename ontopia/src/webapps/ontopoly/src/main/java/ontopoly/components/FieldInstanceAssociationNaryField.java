package ontopoly.components;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ontopoly.model.EditModeIF;
import ontopoly.model.FieldInstanceIF;
import ontopoly.model.InterfaceControlIF;
import ontopoly.model.RoleFieldIF;
import ontopoly.model.OntopolyTopicIF;
import ontopoly.model.OntopolyTopicMapIF;
import ontopoly.model.TopicTypeIF;
import ontopoly.models.FieldInstanceModel;
import ontopoly.models.FieldValueModel;
import ontopoly.models.FieldsViewModel;
import ontopoly.models.PossiblePlayersModel;
import ontopoly.models.RoleFieldModel;
import ontopoly.models.TopicModel;
import ontopoly.pages.AbstractOntopolyPage;
import ontopoly.pages.ModalFindPage;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

public class FieldInstanceAssociationNaryField extends Panel {
  protected int arity;
  protected final FieldValueModel fieldValueModel;
  protected final Map<RoleFieldModel,TopicModel> selectedPlayers = new HashMap<RoleFieldModel,TopicModel>();
  protected final FieldInstanceAssociationNaryPanel parentPanel;
  protected boolean needsUpdate;
  
  protected final RoleFieldModel currentFieldModel;
  protected final TopicModel<OntopolyTopicIF> currentTopicModel;

  public FieldInstanceAssociationNaryField(String id, 
      FieldInstanceAssociationNaryPanel _parentPanel,
      RoleFieldModel roleFieldModel, List otherRoleFieldModels,
      FieldValueModel _fieldValueModel, 
      FieldsViewModel fieldsViewModel, 
      final boolean readonly, boolean traversable, final int arity) {
    super(id);
    this.fieldValueModel = _fieldValueModel;
    this.parentPanel = _parentPanel;
    this.arity = arity;
    FieldInstanceModel fieldInstanceModel = fieldValueModel.getFieldInstanceModel();
    
    // register current player
    this.currentFieldModel = roleFieldModel;
    this.currentTopicModel = new TopicModel<OntopolyTopicIF>(fieldInstanceModel.getFieldInstance().getInstance());
    selectedPlayers.put(roleFieldModel, currentTopicModel);

    RoleFieldIF.ValueIF fieldValue = (RoleFieldIF.ValueIF)fieldValueModel.getFieldValue();

    RepeatingView rv = new RepeatingView("roles");
    rv.setVisible(!readonly || fieldValueModel.isExistingValue());
    add(rv);
    
    Iterator oiter =  otherRoleFieldModels.iterator();
    while (oiter.hasNext()) {
      final RoleFieldModel ofieldModel = (RoleFieldModel)oiter.next();
      RoleFieldIF ofield = ofieldModel.getRoleField();

      final WebMarkupContainer parent =  new WebMarkupContainer(rv.newChildId()) {
        @Override
        public boolean isVisible() {
          // hide if read-only and not complete
          if (readonly && selectedPlayers.size() != arity)
            return false;
          else
            return true;
        }          
      };
      parent.setOutputMarkupId(true);
      rv.add(parent);
      parent.add(new Label("label", new Model<String>(ofield.getRoleType().getName())));
      
      // register other player
      OntopolyTopicIF topic = (fieldValue == null ? null : fieldValue.getPlayer(ofield, fieldInstanceModel.getFieldInstance().getInstance()));
      final TopicModel<OntopolyTopicIF> topicModel = new TopicModel<OntopolyTopicIF>(topic);
      // NOTE: should not use same model as selected model as the model would then be updated immediately
      selectedPlayers.put(ofieldModel, new TopicModel<OntopolyTopicIF>(topic));

      TopicLink playerLink = new TopicLink("player", topicModel);
      playerLink.setEnabled(traversable);
      playerLink.setVisible(topic != null);
      parent.add(playerLink);
      
      EditModeIF editMode = ofield.getEditMode();
      final boolean allowAdd = !editMode.isNewValuesOnly();
      final boolean allowCreate = !editMode.isExistingValuesOnly();
      
      if (readonly || fieldValueModel.isExistingValue() || !allowAdd) {
        // unused components
        parent.add(new Label("select").setVisible(false));
        parent.add(new Label("find").setVisible(false));
        parent.add(new Label("findModal").setVisible(false));
        
      } else {

        InterfaceControlIF interfaceControl = ofield.getInterfaceControl();
        if (interfaceControl.isAutoComplete()) {
          final AssociationFieldAutoCompleteTextField autoCompleteField 
            = new AssociationFieldAutoCompleteTextField("select", new Model<String>(null), ofieldModel) {
            @Override
            protected void filterPlayers(List<OntopolyTopicIF> players) {
              AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
              page.filterTopics(players);
            }            
            @Override
            protected void onTopicSelected(OntopolyTopicIF topic) {
              topicModel.setObject(topic);
              boolean changesMade = onNewSelection(ofieldModel, topic);
              // replace ourselves with a topic link
              if (changesMade)
                parent.replace(new TopicLink("select", new TopicModel<OntopolyTopicIF>(topic)));
            }                
          };
          autoCompleteField.getTextField().add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
              if (needsUpdate)
                FieldInstanceAssociationNaryField.this.onUpdate(target);
              else 
                target.addComponent(parent);
            }
          });          

          autoCompleteField.setOutputMarkupId(true);
          parent.add(autoCompleteField);
          
          // unused components
          parent.add(new Label("find").setVisible(false));
          parent.add(new Label("findModal").setVisible(false));
          
        } else if (interfaceControl.isDropDownList()) {
          PossiblePlayersModel choicesModel = new PossiblePlayersModel(fieldInstanceModel, ofieldModel) {
            @Override
            protected void filterPlayers(Collection<OntopolyTopicIF> players) {
              AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
              page.filterTopics(players);
            }            
          };
          
          TopicDropDownChoice<OntopolyTopicIF> choice = new TopicDropDownChoice<OntopolyTopicIF>("select", topicModel, choicesModel) {        
            @Override
            protected void onModelChanged() {
              super.onModelChanged();            
              onNewSelection(ofieldModel, ((TopicModel)getModel()).getTopic());
            }        
          };
          choice.setOutputMarkupId(true);
          choice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
              FieldInstanceAssociationNaryField.this.onUpdate(target);
            }     
            protected void onError(AjaxRequestTarget target, RuntimeException e) {
              FieldInstanceAssociationNaryField.this.onError(target, e);
            }
          });          
          parent.add(choice);
          
          // unused components
          parent.add(new Label("find").setVisible(false));
          parent.add(new Label("findModal").setVisible(false));

        } else if (interfaceControl.isSearchDialog() || interfaceControl.isBrowseDialog()) {

          // unused components
          parent.add(new TopicLink("select", topicModel));

          // "search"/"browse" button
          final ModalWindow findModal = new ModalWindow("findModal");
          parent.add(findModal);
          
          int activeTab = (interfaceControl.isSearchDialog() ?
                           ModalFindPage.ACTIVE_TAB_SEARCH : ModalFindPage.ACTIVE_TAB_BROWSE);
          
          findModal.setContent(new ModalFindPage(findModal.getContentId(), fieldInstanceModel, activeTab) {
            @Override
            protected boolean isMaxOneCardinality() {
              return true;
            }
            @Override
            protected void onSelectionConfirmed(AjaxRequestTarget target, Collection selected) {
              if (!selected.isEmpty()) {
                String topicId = (String)selected.iterator().next();
                OntopolyTopicMapIF topicMap = fieldValueModel.getFieldInstanceModel().getFieldInstance().getInstance().getTopicMap();
                OntopolyTopicIF topic = topicMap.getTopicById(topicId);
                topicModel.setObject(topic);
                onNewSelection(ofieldModel, topic);
                if (needsUpdate)
                  FieldInstanceAssociationNaryField.this.onUpdate(target);
                else
                  target.addComponent(parent);
              }
            }
            @Override
            protected void onCloseCancel(AjaxRequestTarget target) {
              findModal.close(target);              
            }
            @Override
            protected void onCloseOk(AjaxRequestTarget target) {
              findModal.close(target);
            }              
          });
          findModal.setTitle(new ResourceModel("ModalWindow.title.find.topic").getObject().toString());
          findModal.setCookieName("findModal");
         
          OntopolyImageLink findButton = new OntopolyImageLink("find", "search.gif", new ResourceModel("icon.search.find-topic")) { 
            @Override
            public boolean isVisible() {
              return !readonly;
            }
            @Override
            public void onClick(AjaxRequestTarget target) {
              findModal.show(target);
            }
          };  
          parent.add(findButton);

        } else {
          throw new RuntimeException("Unsupported interface control: " + interfaceControl);
        }
      }
      
      // create button
      if (readonly || fieldValueModel.isExistingValue() || !allowCreate) {
        parent.add(new Label("create").setVisible(false));
      } else {
        // always use popup window
        int createAction = FieldInstanceCreatePlayerPanel.CREATE_ACTION_POPUP;        
        FieldInstanceCreatePlayerPanel createPanel = new FieldInstanceCreatePlayerPanel("create", fieldInstanceModel, fieldsViewModel, ofieldModel, parentPanel, createAction) {
          @Override
          protected OntopolyTopicIF createInstance(TopicTypeIF topicType) {
            OntopolyTopicIF currentTopic = currentTopicModel.getTopic();
            RoleFieldIF currentField = currentFieldModel.getRoleField();            
            RoleFieldIF createField = ofieldModel.getRoleField();
            OntopolyTopicIF createdTopic = null;
            
            // check with page to see if add is allowed
            AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
            if (page.isCreateAllowed(currentTopic, currentField, topicType, createField)) {
              // create a new topic instance
              createdTopic = topicType.createInstance(null);              
              topicModel.setObject(createdTopic);
              performNewSelection(ofieldModel, createdTopic);
            }            
            return createdTopic;
          }
          @Override
          protected void performNewSelection(RoleFieldModel ofieldModel,
                                             OntopolyTopicIF selectedTopic) {
            FieldInstanceAssociationNaryField.this.performNewSelection(ofieldModel, selectedTopic);           
          }          
          @Override
          protected void hideInstancePage(AjaxRequestTarget target) {
            if (needsUpdate)
              FieldInstanceAssociationNaryField.this.onUpdate(target);
            else
              target.addComponent(parent);
          }
        };
        createPanel.setOutputMarkupId(true);
        parent.add(createPanel);
      }        
    } 
  }

  protected boolean onNewSelection(RoleFieldModel ofieldModel,
                                   OntopolyTopicIF selectedTopic) {
    OntopolyTopicIF currentTopic = currentTopicModel.getTopic();
    RoleFieldIF currentField = currentFieldModel.getRoleField();            
    RoleFieldIF selectedField = ofieldModel.getRoleField();
    
    // check with page to see if add is allowed
    AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
    if (page.isAddAllowed(currentTopic, currentField, selectedTopic, selectedField)) {
      performNewSelection(ofieldModel, selectedTopic);
      return true;
    } else {
      return false;
    }
  }
  
  protected void performNewSelection(RoleFieldModel ofieldModel,
                                     OntopolyTopicIF selectedTopic) {
    AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
    // check to see if old state was complete
    if (selectedPlayers.size() == arity) {
      // remove existing association if old state was complete
      RoleFieldIF.ValueIF oldValue = createValue();
      if (oldValue != null) {
        FieldInstanceIF fieldInstance = fieldValueModel.getFieldInstanceModel().getFieldInstance();
        fieldInstance.removeValue(oldValue, page.getListener());
      }
    }    
    // add new selection
    selectedPlayers.put(ofieldModel, new TopicModel<OntopolyTopicIF>(selectedTopic));
    // check to see if new state was complete
    if (selectedPlayers.size() == arity) {
      // add new association as state is now complete
      RoleFieldIF.ValueIF newValue = createValue();
      if (newValue != null) {
        FieldInstanceIF fieldInstance = fieldValueModel.getFieldInstanceModel().getFieldInstance();
        fieldInstance.addValue(newValue, page.getListener());
        fieldValueModel.setExistingValue(newValue);          
        this.needsUpdate = true;
      }
    }
  }
  
  protected void onUpdate(AjaxRequestTarget target) {
    if (needsUpdate) {
      parentPanel.fieldValuesModel.setShowExtraField(false, false);
      parentPanel.onUpdate(target);              
    }
    needsUpdate = false;
  } 
  
  protected void onError(AjaxRequestTarget target, RuntimeException e) {
    if (needsUpdate)
      parentPanel.onError(target, e);
    needsUpdate = false;    
  }
  
  protected RoleFieldIF.ValueIF createValue() {
    RoleFieldIF.ValueIF value = null;
    Iterator iter = selectedPlayers.keySet().iterator();
    while (iter.hasNext()) {
      RoleFieldModel roleFieldModel = (RoleFieldModel)iter.next(); 
      TopicModel topicModel = (TopicModel)selectedPlayers.get(roleFieldModel);
      RoleFieldIF roleField = roleFieldModel.getRoleField();
      OntopolyTopicIF topic = topicModel.getTopic();
      // if topic is null then the value is not complete, so we return null
      if (topic == null)
        return null;
      if (value == null)
        // we need a RoleFieldIF to initialize it, so delaying until we have one
        value = roleField.createValue(arity);
      value.addPlayer(roleField, topic);
    }
    return value;
  }

  @Override
  public void onDetach() {
    fieldValueModel.detach();
    currentFieldModel.detach();
    currentTopicModel.detach();
    for (Map.Entry<RoleFieldModel,TopicModel> selectedPlayer : selectedPlayers.entrySet()) {
      selectedPlayer.getKey().detach();
      selectedPlayer.getValue().detach();
    }
    super.onDetach();
  }

}
