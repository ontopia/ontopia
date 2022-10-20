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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ontopoly.model.EditMode;
import ontopoly.model.FieldInstance;
import ontopoly.model.InterfaceControl;
import ontopoly.model.RoleField;
import ontopoly.model.Topic;
import ontopoly.model.TopicMap;
import ontopoly.model.TopicType;
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
  protected final Map<RoleFieldModel,TopicModel<Topic>> selectedPlayers = new HashMap<RoleFieldModel,TopicModel<Topic>>();
  protected final FieldInstanceAssociationNaryPanel parentPanel;
  protected boolean needsUpdate;
  
  protected final RoleFieldModel currentFieldModel;
  protected final TopicModel<Topic> currentTopicModel;

  public FieldInstanceAssociationNaryField(String id, 
      FieldInstanceAssociationNaryPanel _parentPanel,
      RoleFieldModel roleFieldModel, List<RoleFieldModel> otherRoleFieldModels,
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
    this.currentTopicModel = new TopicModel<Topic>(fieldInstanceModel.getFieldInstance().getInstance());
    selectedPlayers.put(roleFieldModel, currentTopicModel);

    RoleField.ValueIF fieldValue = (RoleField.ValueIF)fieldValueModel.getFieldValue();

    RepeatingView rv = new RepeatingView("roles");
    rv.setVisible(!readonly || fieldValueModel.isExistingValue());
    add(rv);
    
    Iterator<RoleFieldModel> oiter =  otherRoleFieldModels.iterator();
    while (oiter.hasNext()) {
      final RoleFieldModel ofieldModel = oiter.next();
      RoleField ofield = ofieldModel.getRoleField();

      final WebMarkupContainer parent =  new WebMarkupContainer(rv.newChildId()) {
        @Override
        public boolean isVisible() {
          // hide if read-only and not complete
          if (readonly && selectedPlayers.size() != arity) {
            return false;
          } else {
            return true;
          }
        }          
      };
      parent.setOutputMarkupId(true);
      rv.add(parent);
      parent.add(new Label("label", new Model<String>(ofield.getRoleType().getName())));
      //! parent.add(new Label("label", new Model(ofield.getFieldName())));
      
      // register other player
      Topic topic = (fieldValue == null ? null : fieldValue.getPlayer(ofield, fieldInstanceModel.getFieldInstance().getInstance()));
      final TopicModel<Topic> topicModel = new TopicModel<Topic>(topic);
      // NOTE: should not use same model as selected model as the model would then be updated immediately
      selectedPlayers.put(ofieldModel, new TopicModel<Topic>(topic));

      TopicLink<Topic> playerLink = new TopicLink<Topic>("player", topicModel);
      playerLink.setEnabled(traversable);
      playerLink.setVisible(topic != null);
      parent.add(playerLink);
      
      EditMode editMode = ofield.getEditMode();
      final boolean allowAdd = !editMode.isNewValuesOnly();
      final boolean allowCreate = !editMode.isExistingValuesOnly();
      
      if (readonly || fieldValueModel.isExistingValue() || !allowAdd) {
        // unused components
        parent.add(new Label("select").setVisible(false));
        parent.add(new Label("find").setVisible(false));
        parent.add(new Label("findModal").setVisible(false));
        
      } else {

        InterfaceControl interfaceControl = ofield.getInterfaceControl();
        if (interfaceControl.isAutoComplete()) {
          final AssociationFieldAutoCompleteTextField autoCompleteField 
            = new AssociationFieldAutoCompleteTextField("select", new TopicModel<Topic>(null), ofieldModel) {
            @Override
            protected void filterPlayers(List<Topic> players) {
              AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
              page.filterTopics(players);
            }            
            @Override
            protected void onTopicSelected(Topic topic) {
              topicModel.setObject(topic);
              boolean changesMade = onNewSelection(ofieldModel, topic);
              // replace ourselves with a topic link
              if (changesMade) {
                parent.replace(new TopicLink<Topic>("select", new TopicModel<Topic>(topic)));
              }
            }                
          };
          autoCompleteField.getTextField().add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if (needsUpdate) {
                  FieldInstanceAssociationNaryField.this.onUpdate(target);
                } else {
                  target.addComponent(parent);
                }
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
            protected void filterPlayers(Collection<Topic> players) {
              AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
              page.filterTopics(players);
            }            
          };
          
          TopicDropDownChoice<Topic> choice = new TopicDropDownChoice<Topic>("select", topicModel, choicesModel) {        
            @Override
            protected void onModelChanged() {
              super.onModelChanged();            
              onNewSelection(ofieldModel, getModel().getObject());
            }        
          };
          choice.setOutputMarkupId(true);
          choice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
              FieldInstanceAssociationNaryField.this.onUpdate(target);
            }     
            @Override
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
          parent.add(new TopicLink<Topic>("select", topicModel));

          // "search"/"browse" button
          final ModalWindow findModal = new ModalWindow("findModal");
          parent.add(findModal);
          
          int activeTab = (interfaceControl.isSearchDialog() ?
                           ModalFindPage.ACTIVE_TAB_SEARCH : ModalFindPage.ACTIVE_TAB_BROWSE);
          
          findModal.setContent(new ModalFindPage<String>(findModal.getContentId(), fieldInstanceModel, activeTab) {
            @Override
            protected boolean isMaxOneCardinality() {
              return true;
            }
            @Override
            protected void onSelectionConfirmed(AjaxRequestTarget target, Collection<String> selected) {
              if (!selected.isEmpty()) {
                String topicId = selected.iterator().next();
                TopicMap topicMap = fieldValueModel.getFieldInstanceModel().getFieldInstance().getInstance().getTopicMap();
                Topic topic = topicMap.getTopicById(topicId);
                topicModel.setObject(topic);
                onNewSelection(ofieldModel, topic);
                if (needsUpdate) {
                  FieldInstanceAssociationNaryField.this.onUpdate(target);
                } else {
                  target.addComponent(parent);
                }
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
          protected Topic createInstance(TopicType topicType) {
            Topic currentTopic = currentTopicModel.getTopic();
            RoleField currentField = currentFieldModel.getRoleField();            
            RoleField createField = ofieldModel.getRoleField();
            Topic createdTopic = null;
            
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
          protected void performNewSelection(RoleFieldModel ofieldModel, Topic selectedTopic) {
            FieldInstanceAssociationNaryField.this.performNewSelection(ofieldModel, selectedTopic);           
          }          
          @Override
          protected void hideInstancePage(AjaxRequestTarget target) {
            if (needsUpdate) {
              FieldInstanceAssociationNaryField.this.onUpdate(target);
            } else {
              target.addComponent(parent);
            }
          }
        };
        createPanel.setOutputMarkupId(true);
        parent.add(createPanel);
      }        
    } 
  }

  protected boolean onNewSelection(RoleFieldModel ofieldModel, Topic selectedTopic) {
    Topic currentTopic = currentTopicModel.getTopic();
    RoleField currentField = currentFieldModel.getRoleField();            
    RoleField selectedField = ofieldModel.getRoleField();
    
    // check with page to see if add is allowed
    AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
    if (page.isAddAllowed(currentTopic, currentField, selectedTopic, selectedField)) {
      performNewSelection(ofieldModel, selectedTopic);
      return true;
    } else {
      return false;
    }
  }
  
  protected void performNewSelection(RoleFieldModel ofieldModel, Topic selectedTopic) {
    try {
      AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
      // check to see if old state was complete
      if (selectedPlayers.size() == arity) {
        // remove existing association if old state was complete
        RoleField.ValueIF oldValue = createValue();
        if (oldValue != null) {
          FieldInstance fieldInstance = fieldValueModel.getFieldInstanceModel().getFieldInstance();
          fieldInstance.removeValue(oldValue, page.getListener());
        }
      }    
      // add new selection
      selectedPlayers.put(ofieldModel, new TopicModel<Topic>(selectedTopic));
      // check to see if new state was complete
      if (selectedPlayers.size() == arity) {
        // add new association as state is now complete
        RoleField.ValueIF newValue = createValue();
        if (newValue != null) {
          FieldInstance fieldInstance = fieldValueModel.getFieldInstanceModel().getFieldInstance();
          fieldInstance.addValue(newValue, page.getListener());
          fieldValueModel.setExistingValue(newValue);          
          this.needsUpdate = true;
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
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
    if (needsUpdate) {
      parentPanel.onError(target, e);
    }
    needsUpdate = false;    
  }
  
  protected RoleField.ValueIF createValue() {
    RoleField.ValueIF value = RoleField.createValue(arity);
    Iterator<RoleFieldModel> iter = selectedPlayers.keySet().iterator();
    while (iter.hasNext()) {
      RoleFieldModel roleFieldModel = iter.next(); 
      TopicModel<Topic> topicModel = selectedPlayers.get(roleFieldModel);
      RoleField roleField = roleFieldModel.getRoleField();
      Topic topic = topicModel.getTopic();
      // if topic is null then the value is not complete, so we return null
      if (topic == null) {
        return null;
      }
      value.addPlayer(roleField, topic);
    }
    return value;
  }

  @Override
  public void onDetach() {
    fieldValueModel.detach();
    currentFieldModel.detach();
    currentTopicModel.detach();
    for (Map.Entry<RoleFieldModel,TopicModel<Topic>> selectedPlayer : selectedPlayers.entrySet()) {
      selectedPlayer.getKey().detach();
      selectedPlayer.getValue().detach();
    }
    super.onDetach();
  }

}
