package ontopoly.pages;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.TopicMap;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.sysmodel.OntopolyRepository;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.sysmodel.TopicMapReference;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.sysmodel.TopicMapSource;
import ontopoly.components.AjaxOntopolyDropDownChoice;
import ontopoly.components.AjaxOntopolyTextField;
import ontopoly.components.AjaxRadioGroupPanel;
import ontopoly.components.TitleHelpPanel;
import ontopoly.conversion.ConversionUtils;
import ontopoly.models.HelpLinkResourceModel;
import ontopoly.models.TopicMapModel;
import ontopoly.models.TopicMapSourceModel;
import ontopoly.utils.OntopolyContext;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

public class ConvertPage extends NonOntopolyAbstractPage {
  
  private int NUMBER_OF_SOURCES;
  
  private Map properties;
  
  public ConvertPage() {
  }

  public ConvertPage(PageParameters parameters) {
    super(parameters);
	  
    TopicMapModel topicMapModel = getTopicMapModel();
    TopicMap topicMap = topicMapModel.getTopicMap();
    
    // redirect to topic types page if the topic map for some reason already contains the ontology
    if (topicMap.containsOntology()) {
      // need upgrade if version number is lower than current version
      if (topicMap.getOntologyVersion() < OntopolyRepository.CURRENT_VERSION_NUMBER) {
        PageParameters pageParameters = new PageParameters();
        pageParameters.put("topicMapId", topicMap.getId());
        setResponsePage(UpgradePage.class, pageParameters);
        setRedirect(true);
      } else {
        // make the topic map an ontopoly topic map
        TopicMapReference ref = topicMap.getOntopolyRepository().getReference(topicMap.getId());
        ConversionUtils.makeOntopolyTopicMap(ref, topicMap.getName());
        // redirect
        PageParameters pageParameters = new PageParameters();
        pageParameters.put("topicMapId", topicMap.getId());
        setResponsePage(TopicTypesPage.class, pageParameters);
        setRedirect(true);
      }
    }
    
    properties = new HashMap();
    
    // Adding part containing title and help link
    createTitle();
    
    final Form form = new Form("form");
    form.setOutputMarkupId(true);
    add(form);
    
    final WebMarkupContainer sourcesDropDownContainer = new WebMarkupContainer("sourcesDropDownContainer") {
      public boolean isVisible() {
        return (NUMBER_OF_SOURCES > 1 && getProperties().get("choice")
            .equals(new ResourceModel("ConvertPage.create.copy").getObject())) ? true : false;
      }
    };
    sourcesDropDownContainer.setOutputMarkupPlaceholderTag(true);
    form.add(sourcesDropDownContainer);
    
    List sources = topicMap.getOntopolyRepository().getSources();  
    NUMBER_OF_SOURCES = sources.size();
    
    final List contentCategories = Arrays.asList(
        new ResourceModel("ConvertPage.update.existing").getObject(), 
        new ResourceModel("ConvertPage.create.copy").getObject());
    getProperties().put("choice", contentCategories.get(1));
    
    String topicMapId = topicMap.getId();
    if(NUMBER_OF_SOURCES > 0 && 
        (topicMapId.endsWith("xtm") || 
         topicMap.getTopicMapIF().getStore().getImplementation() == TopicMapStoreIF.RDBMS_IMPLEMENTATION)) {          
      add(new Label("action", new ResourceModel("ConvertPage.information.action.copyorupdate")));
      
      AjaxRadioGroupPanel ajaxRadioGroupPanel = new AjaxRadioGroupPanel("exchangeableComponent", form, contentCategories, new PropertyModel(getProperties(), "choice"));
      ajaxRadioGroupPanel.addAjaxTarget(form);
      ajaxRadioGroupPanel.addAjaxTarget(sourcesDropDownContainer);
      form.add(ajaxRadioGroupPanel);
    } else {
      add(new Label("action", new ResourceModel("ConvertPage.information.action.copy")));
      form.add(new Label("exchangeableComponent", new ResourceModel("ConvertPage.create.copy")));
    }
       
    // sources dropdown   
    IModel sourcesChoicesModel = new LoadableDetachableModel() {
      @Override
      protected Object load() {
        return OntopolyContext.getOntopolyRepository().getSources(); 
      }
    };
    
    TopicMapSourceModel topicMapSourceModel = null;
    if(NUMBER_OF_SOURCES > 0) {
      topicMapSourceModel = new TopicMapSourceModel((TopicMapSource)sources.get(0));
    }
      
    final AjaxOntopolyDropDownChoice sourcesDropDown = new AjaxOntopolyDropDownChoice("sourcesDropDown", 
        topicMapSourceModel, sourcesChoicesModel, new ChoiceRenderer("title", "id"));
         
    sourcesDropDownContainer.add(sourcesDropDown);
    
    
    final AjaxOntopolyTextField textField = new AjaxOntopolyTextField("filename", new Model(""));
    form.add(textField);
    
    Button okButton = new Button("ok", new ResourceModel("button.ok")) {
      @Override
      public void onSubmit() {
        TopicMap topicMap = getTopicMapModel().getTopicMap();
        String name = textField.getModelObjectAsString();
        
        if(getProperties().get("choice").equals(new ResourceModel("ConvertPage.create.copy").getObject())) {
          
            TopicMapSource topicMapSource = (TopicMapSource) sourcesDropDown.getModelObject();
            String newTopicMapId = ConversionUtils.convertNew(topicMap, name, topicMapSource);
            PageParameters pageParameters = new PageParameters("topicMapId="
                + newTopicMapId);
            setResponsePage(TopicTypesPage.class, pageParameters);
          
        } else {
          ConversionUtils.convertExisting(topicMap, name);
          PageParameters pageParameters = new PageParameters("topicMapId="
              + topicMap.getId());
          setResponsePage(TopicTypesPage.class, pageParameters);
        }
      }
    };
    form.add(okButton);
    
    Button cancelButton = new Button("cancel", new ResourceModel("button.cancel"));
    cancelButton.add(new AjaxFormComponentUpdatingBehavior("onclick") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        setResponsePage(StartPage.class);
      }          
    });
    form.add(cancelButton);   
  }
  
  public Map getProperties() {
    return properties;
  }

  private void createTitle() {
    add(new TitleHelpPanel("titlePartPanel", 
          new PropertyModel(topicMapModel, "id"), new HelpLinkResourceModel("help.link.convertpage")));
  }
  
}
