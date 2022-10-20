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

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import net.ontopia.utils.OntopiaRuntimeException;
import ontopoly.components.AjaxOntopolyTextField;
import ontopoly.components.TitleHelpPanel;
import ontopoly.model.TopicMap;
import ontopoly.models.HelpLinkResourceModel;
import ontopoly.utils.ExportUtils;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebResource;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.resource.IResourceStream;

public class AdminPage extends OntopolyAbstractPage {
  
  private String content;
  private String syntax;
  private String action;
  protected String filename;
  
  public AdminPage() {	  
  }
  
  public AdminPage(PageParameters parameters) {
    super(parameters);

    // Adding part containing title and help link
    createTitle();
    
    final Form<Object> form = new Form<Object>("form");
    form.setOutputMarkupId(true);
    add(form);

    // First column of radio buttons
    final List<String> contentCategories = Arrays.asList(
        new ResourceModel("AdminPage.export.entire.topic.map").getObject().toString(), 
        new ResourceModel("AdminPage.export.topic.map.without.schema").getObject().toString(),
        new ResourceModel("AdminPage.export.only.schema").getObject().toString());
    content = (String)contentCategories.get(0);
    RadioChoice<String> contentRadioChoice = new RadioChoice<String>("content", new PropertyModel<String>(this, "content"), contentCategories);
    contentRadioChoice.add(new AjaxFormChoiceComponentUpdatingBehavior() {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        // no-op
      }
    });
    form.add(contentRadioChoice);

    TopicMap topicMap = getTopicMap();            
    
    // Second column of radio buttons
    List<String> syntaxCategories = 
      Arrays.asList("ltm", "xtm1", "xtm2", "xtm21", "rdf", "tmxml");
    
    syntax = syntaxCategories.get(1);
    filename = topicMap.getId();
    
    final AjaxOntopolyTextField fileNameTextField = new AjaxOntopolyTextField("fileNameTextField", new PropertyModel<String>(this, "filename"));
      
    RadioChoice<String> syntaxRadioChoice = new RadioChoice<String>("syntax", new PropertyModel<String>(this, "syntax"), syntaxCategories, 
      new IChoiceRenderer<String>() {
        @Override
        public Object getDisplayValue(String object) {
          return new ResourceModel("AdminPage.export.syntax." + object).getObject(); // "export.syntax.ltm", "export.syntax.xtm1", "export.syntax.xtm2", "export.syntax.rdf" ...
        }
        @Override
        public String getIdValue(String object, int index) {
          return object;
        }
      });  
    syntaxRadioChoice.add(new AjaxFormChoiceComponentUpdatingBehavior() {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        if (target != null) {
          target.addComponent(form);
        }
      }
    });
    form.add(syntaxRadioChoice);

    // Third column of radio buttons
    List<String> actionCategories = Arrays.asList(
        new ResourceModel("AdminPage.export.download").getObject().toString(), 
        new ResourceModel("AdminPage.export.view").getObject().toString());
    action = actionCategories.get(0).toString();
    RadioChoice<String> actionRadioChoice = new RadioChoice<String>("action", new PropertyModel<String>(this, "action"), actionCategories);
    form.add(actionRadioChoice);
    actionRadioChoice.add(new AjaxFormChoiceComponentUpdatingBehavior() {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        if (target != null) {
          target.addComponent(form);
        }
      }
    });
    form.add(fileNameTextField);
    

    WebResource export = new WebResource() {

      @Override
      public IResourceStream getResourceStream() {
        AbstractResourceStreamWriter abstractResourceStreamWriter = new AbstractResourceStreamWriter() {
          @Override
          public void write(OutputStream output) {
            ExportUtils.Content contentchoice =
              ExportUtils.Content.ENTIRE_TOPIC_MAP;
            if (content.equals((String) contentCategories.get(1))) {
              contentchoice = ExportUtils.Content.INSTANCES_ONLY;
            } else if (content.equals((String) contentCategories.get(2))) {
              contentchoice = ExportUtils.Content.SCHEMA_ONLY;
            }
            try {
              ExportUtils.export(getTopicMap(), syntax, contentchoice, new OutputStreamWriter(output, "utf-8"));
            } catch (UnsupportedEncodingException e) {
              throw new OntopiaRuntimeException(e);
            }
            
          }
          @Override
          public String getContentType() { 
            return syntax.equalsIgnoreCase("ltm") ? "text/plain" : "text/xml";
          }         
        };
              
        return abstractResourceStreamWriter;
      }
      
      @Override
      protected void setHeaders(WebResponse response) {
        super.setHeaders(response);
        if(action.equals(new ResourceModel("AdminPage.export.download").getObject().toString())) {
            response.setAttachmentHeader(fileNameTextField.getModel().getObject());
        }
      }
    };
    export.setCacheable(false);
    
    ResourceLink<Object> resourceLink = new ResourceLink<Object>("export", export);
    resourceLink.add(new SimpleAttributeModifier("value", new ResourceModel("AdminPage.export.button").getObject().toString()));
    form.add(resourceLink);
    
    // initialize parent components
    initParentComponents();    
  }

  protected static class Syntax {
    private final String id;

    Syntax(String id) {
      this.id = id;
    }    
    public String getId() {
      return id;
    }    
  }
  @Override
  protected int getMainMenuIndex() {
    return ADMIN_PAGE_INDEX_IN_MAINMENU; 
  }
  
  private void createTitle() {
    add(new TitleHelpPanel("titlePartPanel", 
        new ResourceModel("AdminPage.export.title"), new HelpLinkResourceModel("help.link.exportpage")));
  }
  
}
