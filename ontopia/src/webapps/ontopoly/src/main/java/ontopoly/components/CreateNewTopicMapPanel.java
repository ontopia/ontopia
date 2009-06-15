package ontopoly.components;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ontopia.topicmaps.nav2.webapps.ontopoly.sysmodel.OntopolyRepository;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.sysmodel.TopicMapReference;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.sysmodel.TopicMapSource;
import ontopoly.models.TopicMapSourceModel;
import ontopoly.pages.TopicTypesPage;
import ontopoly.utils.OntopolyContext;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

public class CreateNewTopicMapPanel extends Panel {
  
  private final int NUMBER_OF_SOURCES;
  
  public CreateNewTopicMapPanel(String id, OntopolyRepository repository) {
    super(id);
     
    List sources = repository.getSources();  
    NUMBER_OF_SOURCES = sources.size();
    
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
    
    WebMarkupContainer sourcesDropDownContainer = new WebMarkupContainer("sourcesDropDownContainer") {
      public boolean isVisible() {
        return NUMBER_OF_SOURCES > 1 ? true : false;
      }
    };
    sourcesDropDownContainer.setOutputMarkupPlaceholderTag(true);
    add(sourcesDropDownContainer);
    
    final AjaxOntopolyDropDownChoice sourcesDropDown = new AjaxOntopolyDropDownChoice("sourcesDropDown", 
        topicMapSourceModel, sourcesChoicesModel, new ChoiceRenderer("title", "id"));
         
    sourcesDropDownContainer.add(sourcesDropDown);
    
    
    final AjaxOntopolyTextField nameField = new AjaxOntopolyTextField("content", new Model(""));
    add(nameField);

    final Button button = new Button("button", new ResourceModel("create"));
    button.setOutputMarkupId(true);
    button.add(new AjaxFormComponentUpdatingBehavior("onclick") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        String name = nameField.getModelObjectAsString();
        if(!name.equals("")) {
          TopicMapSource topicMapSource = (TopicMapSource) sourcesDropDown.getModelObject();
          TopicMapReference topicMapReference = topicMapSource.createTopicMap((nameField.getModelObjectAsString()));       
          
          Map pageParametersMap = new HashMap();
          pageParametersMap.put("topicMapId", topicMapReference.getId());
          setResponsePage(TopicTypesPage.class, new PageParameters(pageParametersMap));
        }
      }          
    });
    add(button);
  }
  
  public boolean isVisible() {
    return NUMBER_OF_SOURCES > 0 ? true : false;
  }
}

