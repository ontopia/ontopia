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
package ontopoly.pages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import ontopoly.components.AjaxParentCheckChild;
import ontopoly.components.AjaxParentFormChoiceComponentUpdatingBehavior;
import ontopoly.components.AjaxParentRadioChild;
import ontopoly.components.CheckLabelPanel;
import ontopoly.components.TopicDropDownChoice;
import ontopoly.components.TreePanel;
import ontopoly.model.FieldAssignment;
import ontopoly.model.FieldInstance;
import ontopoly.model.RoleField;
import ontopoly.model.Topic;
import ontopoly.model.TopicMap;
import ontopoly.model.TopicType;
import ontopoly.models.FieldInstanceModel;
import ontopoly.models.TopicModel;
import ontopoly.models.TopicTypeModel;
import ontopoly.pojos.TopicNode;
import ontopoly.utils.TreeModels;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ModalFindPage<T> extends Panel {
 
  public static final int ACTIVE_TAB_SEARCH = 1;
  public static final int ACTIVE_TAB_BROWSE = 2;
  
  protected static Logger log = LoggerFactory.getLogger(ModalFindPage.class);
  protected FieldInstanceModel fieldInstanceModel;

  private AjaxLink<Object> searchTabLink;
  private AjaxLink<Object> browseTabLink;
  
  private boolean errorInSearch = false;

  protected TreeModel emptyTreeModel = TreeModels.createEmptyTreeModel();
  
  private IModel<List<TopicType>> playerTypesChoicesModel;
  private TopicModel<TopicType> selectedTypeModel;
  private IModel<List<Topic>> results;
  
  public ModalFindPage(String id, FieldInstanceModel fieldInstanceModel, int activeTab) {
    super(id);
    this.fieldInstanceModel = fieldInstanceModel;

    final WebMarkupContainer popupContent = new WebMarkupContainer("popupContent");
    popupContent.setOutputMarkupId(true);
    add(popupContent);
    
    FieldInstance fieldInstance = fieldInstanceModel.getFieldInstance();
    FieldAssignment fieldAssignment = fieldInstance.getFieldAssignment();
    RoleField roleField = (RoleField)fieldAssignment.getFieldDefinition();        
    popupContent.add(new Label("title", new Model<String>(roleField.getFieldName())));
  
    final WebMarkupContainer searchTab = createSearchTab();
    popupContent.add(searchTab);
    
    final WebMarkupContainer browseTab = createBrowseTab();
    popupContent.add(browseTab);
        
    this.searchTabLink = new AjaxLink<Object>("searchTabLink") {
      @Override
      public void onClick(AjaxRequestTarget target) {
        searchTab.setVisible(true);
        browseTab.setVisible(false);
        searchTabLink.setEnabled(false);
        browseTabLink.setEnabled(true);
        target.addComponent(popupContent);
      }      
    };
    popupContent.add(searchTabLink);
    
    this.browseTabLink = new AjaxLink<Object>("browseTabLink") {
      @Override
      public void onClick(AjaxRequestTarget target) {
        searchTab.setVisible(false);
        browseTab.setVisible(true);              
        searchTabLink.setEnabled(true);
        browseTabLink.setEnabled(false);
        target.addComponent(popupContent);
      }      
    };
    popupContent.add(browseTabLink);
    
    if (activeTab == ACTIVE_TAB_BROWSE) {
      searchTab.setVisible(false);
      browseTab.setVisible(true);
      searchTabLink.setEnabled(true);
      browseTabLink.setEnabled(false);
    } else {
      searchTab.setVisible(true);
      browseTab.setVisible(false);      
      searchTabLink.setEnabled(false);
      browseTabLink.setEnabled(true);
    }
    
  }
  
  protected boolean isMaxOneCardinality() {
    FieldInstance fieldInstance = fieldInstanceModel.getFieldInstance();
    return fieldInstance.getFieldAssignment().getFieldDefinition().getCardinality().isMaxOne();
  }
  
  private WebMarkupContainer createSearchTab() {    
    WebMarkupContainer searchTab = new WebMarkupContainer("searchTab");
    searchTab.setOutputMarkupId(true);
    
    final TextField<String> searchTermField = new TextField<String>("searchTerm", new Model<String>(null));
    searchTermField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        // do nothing here as we only want the server to be notified
      }
    });
    searchTab.add(searchTermField);

    final WebMarkupContainer resultsContainer = new WebMarkupContainer("resultsContainer");
    resultsContainer.setOutputMarkupId(true);
    searchTab.add(resultsContainer);
    
    this.results = new LoadableDetachableModel<List<Topic>>() {
      @Override
      protected List<Topic> load() {
        String searchTerm = (String)searchTermField.getModelObject();
        if (searchTerm == null) {
          return Collections.emptyList();
        } else {
          try {
            FieldInstance fieldInstance = fieldInstanceModel.getFieldInstance();
            RoleField associationField = (RoleField)fieldInstance.getFieldAssignment().getFieldDefinition();
            RoleField otherField = (RoleField)associationField.getFieldsForOtherRoles().iterator().next();
            return otherField.searchAllowedPlayers(searchTerm);
          } catch(Exception e) {
            errorInSearch = true;
            return Collections.emptyList();
          }
        }
      }      
    };
    
    Button searchButton = new Button("search");
    searchButton.add(new AjaxFormComponentUpdatingBehavior("onclick") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        results.detach();        
        target.addComponent(resultsContainer);
      }
    });
    searchTab.add(searchButton);
    
    final FormComponent<? extends Object> checkGroup;
    final boolean maxOneCardinality = isMaxOneCardinality();
    if (maxOneCardinality) {
      checkGroup = new RadioGroup<String>("checkGroup", new Model<String>());      
    } else {
      checkGroup = new CheckGroup<String>("checkGroup", new HashSet<String>());
    }
    checkGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        // notify server when selection is made
      }
    });
    resultsContainer.add(checkGroup);

    final WebMarkupContainer unsuccessfulSearchContainer = new WebMarkupContainer("unsuccessfulSearchContainer") {
      @Override
      public boolean isVisible() {
        IModel<String> model = searchTermField.getModel();
        if (model == null) {
          return false;
        }
        return !(model.getObject() == null ||
                 model.getObject().equals("")) &&
          ((List<Topic>)results.getObject()).isEmpty();
      }
    };
    unsuccessfulSearchContainer.setOutputMarkupPlaceholderTag(true);
    checkGroup.add(unsuccessfulSearchContainer);
      
    Label message = new Label("message", new ResourceModel(errorInSearch ? "search.error" : "search.empty"));
    unsuccessfulSearchContainer.add(message);
    
    ListView<Topic> listView = new ListView<Topic>("results", results) {
      @Override
      public void populateItem(ListItem<Topic> item) {
        Topic hit = item.getModelObject();
        if (maxOneCardinality) {
          Radio<String> check = new Radio<String>("check", new Model<String>(hit.getId())) {
            @Override
            protected void onComponentTag(final ComponentTag tag) {
              tag.put("type", "radio");
              super.onComponentTag(tag);
            }                        
          };
          item.add(check);
        } else {
          Check<String> check = new Check<String>("check", new Model<String>(hit.getId())) {
            @Override
            protected void onComponentTag(final ComponentTag tag) {
              tag.put("type", "checkbox");
              super.onComponentTag(tag);
            }                        
          }; 
          item.add(check);
        }
        item.add(new Label("topic", new Model<String>(hit.getName())));
        item.add(new Label("type", new Model<String>(((TopicType)hit.getTopicTypes().iterator().next()).getName())));
      }
    };
    checkGroup.add(listView);
    
    Button closeOkButton = new Button("closeOK");
    closeOkButton.add(new AjaxFormComponentUpdatingBehavior("onclick") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        Object modelObject = checkGroup.getModelObject();
        Collection selected;
        if (modelObject instanceof String) {
          selected = Collections.singleton(modelObject);
        } else {
          selected = (Collection)modelObject;
        }
        
        if (selected == null) {
          selected = Collections.emptyList();
        }
        
        // add associations for selected topics
        onSelectionConfirmed(target, selected);
        onCloseOk(target);
        
        // reset search term field
        searchTermField.getDefaultModel().setObject(null);
      }
    });
    searchTab.add(closeOkButton);
    
    Button closeCancelButton = new Button("closeCancel");
    closeCancelButton.add(new AjaxFormComponentUpdatingBehavior("onclick") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        onCloseCancel(target);
        // reset search term field
        searchTermField.getDefaultModel().setObject(null);
      }
    });
    searchTab.add(closeCancelButton);
    
    return searchTab;
  }
  
  private WebMarkupContainer createBrowseTab() {

    final WebMarkupContainer browseTab = new WebMarkupContainer("browseTab");
    browseTab.setOutputMarkupId(true);

    this.playerTypesChoicesModel = new LoadableDetachableModel<List<TopicType>>() {
      @Override
      protected List<TopicType> load() {
        // TODO: should merge with PlayerTypesModel.java (extend to filter my large instance types)
        FieldInstance fieldInstance = fieldInstanceModel.getFieldInstance();
        RoleField associationField = (RoleField)fieldInstance.getFieldAssignment().getFieldDefinition();
        // FIXME: this doesn't work for n+ary fields
        RoleField otherField = (RoleField)associationField.getFieldsForOtherRoles().iterator().next();
        TopicMap tm = associationField.getTopicMap();
        // include all topic types except those with large instance sets
        Collection<TopicType> allowedValueTypes = otherField.getDeclaredPlayerTypes();
        Collection<TopicType> largeInstanceSets = tm.getTopicTypesWithLargeInstanceSets(); 
        List<TopicType> topicTypes = new ArrayList<TopicType>(allowedValueTypes.size());
        Iterator<TopicType> iter = allowedValueTypes.iterator();
        while (iter.hasNext()) {
          TopicType topicType = iter.next();
          if (!largeInstanceSets.contains(topicType)) {
            topicTypes.add(topicType);
          }
        }
        return topicTypes; 
      }
    };
    List<TopicType> playerTypes = playerTypesChoicesModel.getObject();
    TopicType selectedType = playerTypes.size() == 1 ? playerTypes.get(0) : null;
    this.selectedTypeModel = new TopicModel<TopicType>(selectedType, TopicModel.TYPE_TOPIC_TYPE);
    
    final WebMarkupContainer resultsContainer = new WebMarkupContainer("resultsContainer");
    resultsContainer.setOutputMarkupId(true);
    browseTab.add(resultsContainer);
    
    final FormComponent checkGroup;
    final Model<String> radioGroupModel = new Model<String>();
    final Collection<String> checkGroupModel = new HashSet<String>();
    
    final boolean maxOneCardinality = isMaxOneCardinality();
    if (maxOneCardinality) {
      checkGroup = new RadioGroup<String>("checkGroup", radioGroupModel);      
    } else {
      checkGroup = new CheckGroup<String>("checkGroup", checkGroupModel);
    }
    final AjaxParentFormChoiceComponentUpdatingBehavior apfc = new AjaxParentFormChoiceComponentUpdatingBehavior() {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        // notify server when selection is made
      }
    };
    checkGroup.add(apfc);
    resultsContainer.add(checkGroup);
        
    // create a tree
    final TreePanel treePanel = new TreePanel("results", getTreeModel(null)) {
      @Override
      protected Component populateNode(String id, TreeNode treeNode) {
        DefaultMutableTreeNode mTreeNode = (DefaultMutableTreeNode)treeNode; 
        final TopicNode node = (TopicNode)mTreeNode.getUserObject();
        Topic selectedType = selectedTypeModel.getTopic();        
        final boolean selectable = node.getTopic().isInstanceOf(selectedType);
        
        // create link with label        
        return new CheckLabelPanel(id) {
          @Override
          protected Component newCheck(String id) {            
            if (maxOneCardinality) {
              return new AjaxParentRadioChild<String>(id, new Model<String>(node.getTopicId()), apfc) {
                @Override
                public boolean isVisible() {
                  return selectable;
                }
                @Override
                protected void onComponentTag(final ComponentTag tag) {
                  tag.put("type", "radio");
                  super.onComponentTag(tag);
                }                            
              };              
            } else {
              return new AjaxParentCheckChild(id, new Model<String>(node.getTopicId()), apfc) {
                @Override
                public boolean isVisible() {
                  return selectable;
                }
                @Override
                protected void onComponentTag(final ComponentTag tag) {
                  tag.put("type", "checkbox");
                  super.onComponentTag(tag);
                }                            
              }; 
            }
          }
          @Override
          protected Label newLabel(String id) {
            Label label = new Label(id, new Model<String>(node.getName()));
            label.setRenderBodyOnly(false);
            return label;
          }
        };
      }
    };    
    treePanel.setOutputMarkupId(true);
    checkGroup.add(treePanel);
    
    // NOTE: need to readd model here because page, which we depend on
    // in the construction of the tree model, is not available in
    // TreePanel constructor
    if (this.selectedTypeModel != null) {
      treePanel.setDefaultModel(getTreeModel(selectedType));
    }
    
    final TopicDropDownChoice<TopicType> playerTypesDropDown = new TopicDropDownChoice<TopicType>("playerTypes", this.selectedTypeModel, playerTypesChoicesModel);
    
    playerTypesDropDown.add(new AjaxFormComponentUpdatingBehavior("onchange") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        // replace tree model
        TopicType topicType = (TopicType)playerTypesDropDown.getDefaultModelObject();
        treePanel.setDefaultModel(getTreeModel(topicType));
        target.addComponent(resultsContainer);
      }
    });
    browseTab.add(playerTypesDropDown);
      
    Button closeOkButton = new Button("closeOK");
    closeOkButton.add(new AjaxFormComponentUpdatingBehavior("onclick") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        Object modelObject = checkGroup.getModelObject();
        Collection selected;
        if (modelObject instanceof String) {
          selected = Collections.singleton(modelObject);
        } else {
          selected = (Collection)modelObject;
        }
        
        if (selected == null) {
          selected = Collections.EMPTY_LIST;
        }
        
        // add associations for selected topics
        onSelectionConfirmed(target, selected);
        onCloseOk(target);

        // reset selected topic type, choice group and tree model
        selectedTypeModel.setObject(null);
        treePanel.setDefaultModel(getTreeModel(null));
        radioGroupModel.setObject(null);
        checkGroupModel.clear();
      }
    });
    browseTab.add(closeOkButton);
    
    Button closeCancelButton = new Button("closeCancel");
    closeCancelButton.add(new AjaxFormComponentUpdatingBehavior("onclick") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        onCloseCancel(target);

        // reset selected topic type, choice group and tree model
        selectedTypeModel.setObject(null);
        treePanel.setDefaultModel(getTreeModel(null));
        radioGroupModel.setObject(null);
        checkGroupModel.clear();
      }
    });
    browseTab.add(closeCancelButton);
    
    return browseTab;
  }
  
  protected IModel<TreeModel> getTreeModel(TopicType _topicType) {
    TopicTypeModel topicTypeModel = new TopicTypeModel(_topicType);
    TopicType topicType = topicTypeModel.getTopicType();
    if (topicType == null) {
      return new Model((Serializable)emptyTreeModel);
    } else {
      // AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
      boolean adminEnabled = false; // page.isAdministrationEnabled();
      return new Model((Serializable)TreeModels.createInstancesTreeModel(topicType, adminEnabled));
    }
  }

  protected abstract void onSelectionConfirmed(AjaxRequestTarget target, Collection<T> selected);
  
  protected abstract void onCloseOk(AjaxRequestTarget target);

  protected abstract void onCloseCancel(AjaxRequestTarget target);    

  @Override
  public void onDetach() {
    playerTypesChoicesModel.detach();
    selectedTypeModel.detach();
    results.detach();
    super.onDetach();
  }

}
