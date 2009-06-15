package ontopoly.pages;

import java.util.ArrayList;
import java.util.List;

import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.FieldsView;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.Topic;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.TopicMap;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.TopicType;
import net.ontopia.utils.ObjectUtils;
import ontopoly.components.ButtonFunctionBoxPanel;
import ontopoly.components.DeleteTopicMapFunctionBoxPanel;
import ontopoly.components.FunctionBoxesPanel;
import ontopoly.components.InstancePanel;
import ontopoly.components.TitleHelpPanel;
import ontopoly.models.FieldsViewModel;
import ontopoly.models.TopicMapModel;
import ontopoly.models.TopicModel;
import ontopoly.models.TopicTypeModel;
import ontopoly.utils.OntopolyUtils;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

public class DescriptionPage extends OntopolyAbstractPage {
  private TopicModel topicModel;
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

    this.topicModel = new TopicModel(reifier);
    Topic topic = topicModel.getTopic();
    
    // if "topicType" parameter is specified, pull out most specific direct type    
    TopicType tt = null;
    String topicTypeId = parameters.getString("topicTypeId");
    if (topicTypeId != null)
      tt = topic.getMostSpecificTopicType(new TopicTypeModel(topicMapId, topicTypeId).getTopicType());
    
    // if not topic type found, use first available direct type
    if (tt == null)
      tt = OntopolyUtils.getDefaultTopicType(topic);

    this.topicTypeModel = new TopicTypeModel(tt);

    String viewId = parameters.getString("viewId");
    if (viewId != null)
      this.fieldsViewModel = new FieldsViewModel(topicMapId, viewId);
    else
      this.fieldsViewModel = new FieldsViewModel(FieldsView.getDefaultFieldsView(topic.getTopicMap()));
         
    setReadOnlyPage(ObjectUtils.equals(getRequest().getParameter("ro"), "true"));
    
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
        new PropertyModel(getTopicMapModel(), "name"),
        new ResourceModel("help.link.descriptionpage"));
    this.titlePartPanel.setOutputMarkupId(true);
    add(titlePartPanel);
  }
  
  private void createFields() {
    Form form = new Form("form");
    add(form);
    
    // display fields
    this.instanceContainer = new WebMarkupContainer("instanceContainer");
    instanceContainer.setOutputMarkupId(true);
    form.add(instanceContainer);

    InstancePanel instancePanel = createInstancePanel("instancePanel");
    if (instancePanel.isReadOnly()) setReadOnlyPage(true); // page is readonly if instance panel is    
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
      protected List getFunctionBoxesList(String id) {
        // TODO Auto-generated method stub
        return createFunctionBoxesList(id);
      }      
    };    
    functionBoxesContainer.add(functionBoxesPanel);
  }

  private List createFunctionBoxesList(String id) {
    List list = new ArrayList();
    
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
      protected IModel getText() {
        return new ResourceModel(isShortcutsEnabled() ? "disable.shortcuts" : "enable.shortcuts");
      }
      @Override
      protected IModel getButtonLabel() {
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
      protected IModel getText() {
        return new ResourceModel(isAnnotationEnabled() ? "disable.ontology.annotation" : "enable.ontology.annotation");
      }
      @Override
      protected IModel getButtonLabel() {
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
      protected IModel getText() {
        return new ResourceModel(isAdministrationEnabled() ? "disable.administration.mode" : "enable.administration.mode");
      }
      @Override
      protected IModel getButtonLabel() {
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
  public Class getPageClass(Topic topic) {
    return InstancePage.class;
  }
  
}
