package ontopoly.pages;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.TopicMap;
import net.ontopia.utils.OntopiaRuntimeException;
import ontopoly.components.AjaxOntopolyTextField;
import ontopoly.components.TitleHelpPanel;
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
  
  String content;
  String syntax;
  String action;
  String filename;
  
  public AdminPage() {	  
  }
  
  public AdminPage(PageParameters parameters) {
    super(parameters);

    // Adding part containing title and help link
    createTitle();
    
    final Form form = new Form("form");
    form.setOutputMarkupId(true);
    add(form);

    // First column of radio buttons
    final List contentCategories = Arrays.asList(
        new ResourceModel("AdminPage.export.entire.topic.map").getObject().toString(), 
        new ResourceModel("AdminPage.export.topic.map.without.schema").getObject().toString());
    content = (String)contentCategories.get(0);
    RadioChoice contentRadioChoice = new RadioChoice("content", new PropertyModel(this, "content"), contentCategories);
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
      Arrays.asList("ltm", "xtm1", "xtm2", "rdf"); // TODO: cxtm and tm/xml
    
    syntax = syntaxCategories.get(1);
    filename = topicMap.getId();
    
    final AjaxOntopolyTextField fileNameTextField = new AjaxOntopolyTextField("fileNameTextField", new PropertyModel(this, "filename"));
      
    RadioChoice syntaxRadioChoice = new RadioChoice("syntax", new PropertyModel(this, "syntax"), syntaxCategories, 
      new IChoiceRenderer() {
        public Object getDisplayValue(Object object) {
          return new ResourceModel("AdminPage.export.syntax." + object).getObject(); // "export.syntax.ltm", "export.syntax.xtm1", "export.syntax.xtm2", "export.syntax.rdf"
        }
        public String getIdValue(Object object, int index) {
          return (String)object;
        }
      });  
    syntaxRadioChoice.add(new AjaxFormChoiceComponentUpdatingBehavior() {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        if (target != null)
          target.addComponent(form);
      }
    });
    form.add(syntaxRadioChoice);

    // Third column of radio buttons
    List actionCategories = Arrays.asList(
        new ResourceModel("AdminPage.export.download").getObject().toString(), 
        new ResourceModel("AdminPage.export.view").getObject().toString());
    action = actionCategories.get(0).toString();
    RadioChoice actionRadioChoice = new RadioChoice("action", new PropertyModel(this, "action"), actionCategories);
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
          public void write(OutputStream output) {
            boolean includeSchema = false;
            if(content.equals((String)contentCategories.get(0))) {
              includeSchema = true;
            }
            try {
              ExportUtils.export(getTopicMap(), syntax, includeSchema, new OutputStreamWriter(output, "utf-8"));
            } catch (UnsupportedEncodingException e) {
              throw new OntopiaRuntimeException(e);
            }
            
          }
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
          response.setAttachmentHeader(fileNameTextField.getModelObjectAsString());
        }
      }
    };
    export.setCacheable(false);
    
    ResourceLink resourceLink = new ResourceLink("export", export);
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
        new ResourceModel("AdminPage.export.title"), new ResourceModel("help.link.exportpage")));
  }
  
}
