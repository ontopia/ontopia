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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import ontopoly.OntopolyAccessStrategy.Privilege;
import ontopoly.OntopolySession;
import ontopoly.components.ButtonFunctionBoxPanel;
import ontopoly.components.DeleteTopicMapFunctionBoxPanel;
import ontopoly.components.FunctionBoxesPanel;
import ontopoly.components.InstancePanel;
import ontopoly.components.TitleHelpPanel;
import ontopoly.model.FieldsView;
import ontopoly.model.Topic;
import ontopoly.model.TopicMap;
import ontopoly.model.TopicType;
import ontopoly.models.FieldsViewModel;
import ontopoly.models.HelpLinkResourceModel;
import ontopoly.models.TopicMapModel;
import ontopoly.models.TopicModel;
import ontopoly.models.TopicTypeModel;
import ontopoly.utils.OntopolyUtils;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

public class DescriptionPage extends OntopolyAbstractPage {
  private TopicModel<Topic> topicModel;
  private TopicTypeModel topicTypeModel;
  private FieldsViewModel fieldsViewModel;

  protected WebMarkupContainer instanceContainer;

  private TitleHelpPanel titlePartPanel;
  private WebMarkupContainer functionBoxesContainer; 
  
  public DescriptionPage() {	  
  }
  
  public DescriptionPage(PageParameters parameters) {
    super(parameters);
 
    String topicMapId = parameters.getString("topicMapId");
    TopicMap topicMap = getTopicMap();
    Topic reifier = topicMap.getReifier();

    this.topicModel = new TopicModel<Topic>(reifier);
    Topic topic = topicModel.getTopic();
    
    // if "topicType" parameter is specified, pull out most specific direct type    
    TopicType tt = null;
    String topicTypeId = parameters.getString("topicTypeId");
    if (topicTypeId != null) {
      tt = topic.getMostSpecificTopicType(new TopicTypeModel(topicMapId, topicTypeId).getTopicType());
    }
    
    // if not topic type found, use first available direct type
    if (tt == null) {
      tt = OntopolyUtils.getDefaultTopicType(topic);
    }

    this.topicTypeModel = new TopicTypeModel(tt);

    String viewId = parameters.getString("viewId");
    if (viewId != null) {
      this.fieldsViewModel = new FieldsViewModel(topicMapId, viewId);
    } else {
      this.fieldsViewModel = new FieldsViewModel(FieldsView.getDefaultFieldsView(topic.getTopicMap()));
    }
         
    Privilege privilege = ((OntopolySession)Session.get()).getPrivilege(topic);

    // Block access to page if user has privilege NONE
    if (privilege == Privilege.NONE) {
      setResponsePage(new AccessDeniedPage(parameters));
      return;
    }

    // page is read-only if topic type is read-only
    setReadOnlyPage(tt.isReadOnly() || 
        Objects.equals(getRequest().getParameter("ro"), "true") || 
        !((AbstractOntopolyPage)this).filterTopic(topic) ||
        privilege != Privilege.EDIT);

    // Adding part containing title and help link
    createTitle();

    // Add fields panel
    createFields();
    
    // Function boxes
    createFunctionBoxes();
    
    // initialize parent components
    initParentComponents();    
  }

  @Override
  protected int getMainMenuIndex() {
    return DESCRIPTION_PAGE_INDEX_IN_MAINMENU; 
  }
  
  private void createTitle() {

    // Adding part containing title and help link
    this.titlePartPanel = new TitleHelpPanel("titlePartPanel",
        new PropertyModel<String>(getTopicMapModel(), "name"),
        new HelpLinkResourceModel("help.link.descriptionpage"));
    this.titlePartPanel.setOutputMarkupId(true);
    add(titlePartPanel);
  }
  
  private void createFields() {
    Form<Object> form = new Form<Object>("form");
    add(form);
    
    // display fields
    this.instanceContainer = new WebMarkupContainer("instanceContainer");
    instanceContainer.setOutputMarkupId(true);
    form.add(instanceContainer);

    InstancePanel instancePanel = createInstancePanel("instancePanel");
    if (instancePanel.isReadOnly()) {
      setReadOnlyPage(true); // page is readonly if instance panel is    
    }
    instanceContainer.add(instancePanel);
  }  

  protected InstancePanel createInstancePanel(final String id) {
    return new InstancePanel(id, topicModel, topicTypeModel, fieldsViewModel, isReadOnlyPage(), isTraversablePage()) {
      @Override
      protected void onLockLost(AjaxRequestTarget target, Topic topic) {
        setResponsePage(getPageClass(), getPageParameters());
      }      
      @Override
      protected void onLockWon(AjaxRequestTarget target, Topic topic) {
        setResponsePage(getPageClass(), getPageParameters());
      }      
    };
  }

  private void createFunctionBoxes() {
    functionBoxesContainer = new WebMarkupContainer("functionBoxesContainer");
    functionBoxesContainer.setOutputMarkupId(true);
    add(functionBoxesContainer);
    
    FunctionBoxesPanel functionBoxesPanel = new FunctionBoxesPanel("functionBoxes") {
      @Override
      protected List<Component> getFunctionBoxesList(String id) {
        // TODO Auto-generated method stub
        return createFunctionBoxesList(id);
      }      
    };    
    functionBoxesContainer.add(functionBoxesPanel);
  }

  private List<Component> createFunctionBoxesList(String id) {
    List<Component> list = new ArrayList<Component>();
    
    TopicMap topicMap = getTopicMap();
    if (topicMap.isDeleteable()) {
      list.add(new DeleteTopicMapFunctionBoxPanel(id) {
        @Override
        public boolean isVisible() {
          return !isReadOnlyPage();
        }          
        @Override
        public void onDeleteConfirmed(TopicMap topicMap) {
          setResponsePage(StartPage.class);                      
        }
        @Override
        public TopicMapModel getTopicMapModel() {
          return DescriptionPage.this.getTopicMapModel();
        }            
      });
    }
    list.add(new ButtonFunctionBoxPanel(id) {
      @Override
      protected IModel<String> getText() {
        return new ResourceModel(isShortcutsEnabled() ? "disable.shortcuts" : "enable.shortcuts");
      }
      @Override
      protected IModel<String> getButtonLabel() {
        return new ResourceModel(isShortcutsEnabled() ? "disable" : "enable");
      }
      @Override
      public void onClick(AjaxRequestTarget target) {
        getOntopolySession().setShortcutsEnabled(!isShortcutsEnabled());

        PageParameters pageParameters = new PageParameters();
        pageParameters.put("topicMapId", getTopicMap().getId());
        setResponsePage(DescriptionPage.class, pageParameters);
        setRedirect(true);
      }
    });
    
    list.add(new ButtonFunctionBoxPanel(id) {
      @Override
      protected IModel<String> getText() {
        return new ResourceModel(isAnnotationEnabled() ? "disable.ontology.annotation" : "enable.ontology.annotation");
      }
      @Override
      protected IModel<String> getButtonLabel() {
        return new ResourceModel(isAnnotationEnabled() ? "disable" : "enable");
      }
      @Override
      public void onClick(AjaxRequestTarget target) {
        getOntopolySession().setAnnotationEnabled(!isAnnotationEnabled());

        PageParameters pageParameters = new PageParameters();
        pageParameters.put("topicMapId", getTopicMap().getId());
        setResponsePage(DescriptionPage.class, pageParameters);
        setRedirect(true);
      }
    });
    
    list.add(new ButtonFunctionBoxPanel(id) {
      @Override
      protected IModel<String> getText() {
        return new ResourceModel(isAdministrationEnabled() ? "disable.administration.mode" : "enable.administration.mode");
      }
      @Override
      protected IModel<String> getButtonLabel() {
        return new ResourceModel(isAdministrationEnabled() ? "disable" : "enable");
      }
      @Override
      public void onClick(AjaxRequestTarget target) {
        getOntopolySession().setAdministrationEnabled(!isAdministrationEnabled());

        PageParameters pageParameters = new PageParameters();
        pageParameters.put("topicMapId", getTopicMap().getId());
        setResponsePage(DescriptionPage.class, pageParameters);
        setRedirect(true);
      }
    });
    return list;
  }

  protected boolean isTraversablePage() {
    return true; // NOTE: hardcoded
  }

  @Override
  public Class<? extends Page> getPageClass(Topic topic) {
    return InstancePage.class;
  }
  
}
