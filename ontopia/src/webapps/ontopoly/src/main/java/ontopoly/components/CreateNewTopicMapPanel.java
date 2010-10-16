package ontopoly.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.ontopia.topicmaps.entry.TopicMapSourceIF;
import net.ontopia.utils.DeciderIF;

import ontopoly.OntopolyContext;
import ontopoly.models.TopicMapSourceModel;
import ontopoly.pages.TopicTypesPage;
import ontopoly.sysmodel.TopicMapReference;
import ontopoly.sysmodel.TopicMapSource;

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
  
  private int numberOfSources;
  
  public CreateNewTopicMapPanel(String id) {
    super(id);
    
    IModel<List<TopicMapSource>> sourcesChoicesModel = new LoadableDetachableModel<List<TopicMapSource>>() {
      @Override
      protected List<TopicMapSource> load() {
        List<TopicMapSource> result = OntopolyContext.getOntopolyRepository().getEditableSources();
        numberOfSources = result.size();
        return result;
      }
    };
    
    List<TopicMapSource> sources = sourcesChoicesModel.getObject();

    TopicMapSourceModel topicMapSourceModel = null;
    if (numberOfSources > 0) {
      topicMapSourceModel = new TopicMapSourceModel((TopicMapSource)sources.get(0));
    }
    
    WebMarkupContainer sourcesDropDownContainer = new WebMarkupContainer("sourcesDropDownContainer") {
      @Override
      public boolean isVisible() {
        return numberOfSources > 1 ? true : false;
      }
    };
    sourcesDropDownContainer.setOutputMarkupPlaceholderTag(true);
    add(sourcesDropDownContainer);
    
    final AjaxOntopolyDropDownChoice<TopicMapSource> sourcesDropDown = new AjaxOntopolyDropDownChoice<TopicMapSource>("sourcesDropDown", 
        topicMapSourceModel, sourcesChoicesModel, new ChoiceRenderer<TopicMapSource>("title", "id"));
         
    sourcesDropDownContainer.add(sourcesDropDown);
    
    
    final AjaxOntopolyTextField nameField = new AjaxOntopolyTextField("content", new Model<String>(""));
    add(nameField);

    final Button button = new Button("button", new ResourceModel("create"));
    button.setOutputMarkupId(true);
    button.add(new AjaxFormComponentUpdatingBehavior("onclick") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        String name = nameField.getModel().getObject();
        if(!name.equals("")) {
          TopicMapSource topicMapSource = (TopicMapSource) sourcesDropDown.getModelObject();
          String referenceId = OntopolyContext.getOntopolyRepository().createOntopolyTopicMap(topicMapSource.getId(), name);
          
          Map<String,String> pageParametersMap = new HashMap<String,String>();
          pageParametersMap.put("topicMapId", referenceId);
          setResponsePage(TopicTypesPage.class, new PageParameters(pageParametersMap));
        }
      }          
    });
    add(button);
  }
  
  @Override
  public boolean isVisible() {
    return numberOfSources > 0 ? true : false;
  }
}

