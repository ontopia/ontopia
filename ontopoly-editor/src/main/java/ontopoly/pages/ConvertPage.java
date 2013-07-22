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


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ontopia.topicmaps.core.TopicMapStoreIF;
import ontopoly.OntopolyApplication;
import ontopoly.OntopolyContext;
import ontopoly.components.AjaxOntopolyDropDownChoice;
import ontopoly.components.AjaxOntopolyTextField;
import ontopoly.components.AjaxRadioGroupPanel;
import ontopoly.components.TitleHelpPanel;
import ontopoly.conversion.ConversionUtils;
import ontopoly.model.TopicMap;
import ontopoly.models.HelpLinkResourceModel;
import ontopoly.models.TopicMapModel;
import ontopoly.models.TopicMapSourceModel;
import ontopoly.sysmodel.TopicMapSource;

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
  
  private Map<String,String> properties;
  
  public ConvertPage() {
  }

  public ConvertPage(PageParameters parameters) {
    super(parameters);
	  
    TopicMapModel topicMapModel = getTopicMapModel();
    TopicMap topicMap = topicMapModel.getTopicMap();
    
    // redirect to topic types page if the topic map for some reason already contains the ontology
    if (topicMap.containsOntology()) {
      // need upgrade if version number is lower than current version
      if (topicMap.getOntologyVersion() < OntopolyApplication.CURRENT_VERSION_NUMBER) {
        PageParameters pageParameters = new PageParameters();
        pageParameters.put("topicMapId", topicMap.getId());
        setResponsePage(UpgradePage.class, pageParameters);
        setRedirect(true);
      } else {
        // make the topic map an ontopoly topic map
        OntopolyContext.getOntopolyRepository().registerOntopolyTopicMap(topicMap.getId(), topicMap.getName());
        // redirect
        PageParameters pageParameters = new PageParameters();
        pageParameters.put("topicMapId", topicMap.getId());
        setResponsePage(TopicTypesPage.class, pageParameters);
        setRedirect(true);
      }
    }
    
    properties = new HashMap<String,String>();
    
    // Adding part containing title and help link
    createTitle();
    
    final Form<Object> form = new Form<Object>("form");
    form.setOutputMarkupId(true);
    add(form);
    
    final WebMarkupContainer sourcesDropDownContainer = new WebMarkupContainer("sourcesDropDownContainer") {
      @Override
      public boolean isVisible() {
        return (NUMBER_OF_SOURCES > 1 && properties.get("choice")
            .equals(new ResourceModel("ConvertPage.create.copy").getObject())) ? true : false;
      }
    };
    sourcesDropDownContainer.setOutputMarkupPlaceholderTag(true);
    form.add(sourcesDropDownContainer);
    
    List<TopicMapSource> sources = OntopolyContext.getOntopolyRepository().getEditableSources();
    NUMBER_OF_SOURCES = sources.size();
    
    final List<String> contentCategories = Arrays.asList(
        new ResourceModel("ConvertPage.update.existing").getObject(), 
        new ResourceModel("ConvertPage.create.copy").getObject());
    properties.put("choice", contentCategories.get(1));
    
    String topicMapId = topicMap.getId();
    if (NUMBER_OF_SOURCES > 0 && 
        (topicMapId.endsWith("xtm") || 
         topicMap.getTopicMapIF().getStore().getImplementation() == TopicMapStoreIF.RDBMS_IMPLEMENTATION)) {          
      add(new Label("action", new ResourceModel("ConvertPage.information.action.copyorupdate")));
      
      AjaxRadioGroupPanel ajaxRadioGroupPanel = new AjaxRadioGroupPanel("exchangeableComponent", form, contentCategories, new PropertyModel<Object>(getProperties(), "choice"));
      ajaxRadioGroupPanel.addAjaxTarget(form);
      ajaxRadioGroupPanel.addAjaxTarget(sourcesDropDownContainer);
      form.add(ajaxRadioGroupPanel);
    } else {
      add(new Label("action", new ResourceModel("ConvertPage.information.action.copy")));
      form.add(new Label("exchangeableComponent", new ResourceModel("ConvertPage.create.copy")));
    }
       
    // sources dropdown   
    IModel<List<TopicMapSource>> sourcesChoicesModel = new LoadableDetachableModel<List<TopicMapSource>>() {
      @Override
      protected List<TopicMapSource> load() {
        return OntopolyContext.getOntopolyRepository().getEditableSources();
      }
    };
    
    TopicMapSourceModel topicMapSourceModel = null;
    if(NUMBER_OF_SOURCES > 0) {
      topicMapSourceModel = new TopicMapSourceModel((TopicMapSource)sources.get(0));
    }
      
    final AjaxOntopolyDropDownChoice<TopicMapSource> sourcesDropDown = 
      new AjaxOntopolyDropDownChoice<TopicMapSource>("sourcesDropDown", 
        topicMapSourceModel, sourcesChoicesModel, new ChoiceRenderer<TopicMapSource>("title", "id"));
         
    sourcesDropDownContainer.add(sourcesDropDown);
    
    
    final AjaxOntopolyTextField textField = new AjaxOntopolyTextField("filename", new Model<String>(""));
    form.add(textField);
    
    Button okButton = new Button("ok", new ResourceModel("button.ok")) {
      @Override
      public void onSubmit() {
        TopicMap topicMap = getTopicMapModel().getTopicMap();
        String name = textField.getModel().getObject();
        if (name == null) {
          name = topicMap.getId();
          if (name == null) {
            name = "noname";
          }
        }
        if (properties.get("choice").equals(new ResourceModel("ConvertPage.create.copy").getObject())) {
          
            TopicMapSource topicMapSource = sourcesDropDown.getModelObject();
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
  
  public Map<String,String> getProperties() {
    return properties;
  }

  private void createTitle() {
    add(new TitleHelpPanel("titlePartPanel", 
          new PropertyModel<String>(topicMapModel, "id"), new HelpLinkResourceModel("help.link.convertpage")));
  }
  
}
