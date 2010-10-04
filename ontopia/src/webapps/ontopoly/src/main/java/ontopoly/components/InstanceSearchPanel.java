package ontopoly.components;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.ontopia.topicmaps.core.TopicIF;
import ontopoly.model.Topic;
import ontopoly.model.TopicMap;
import ontopoly.models.TopicTypeModel;
import ontopoly.pages.InstancePage;
import ontopoly.pages.InstancesPage;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

public class InstanceSearchPanel extends Panel {

  private boolean errorInSearch = false;
  
  public InstanceSearchPanel(String id, IModel model) {
    super(id, model);
    
    final TopicTypeModel topicTypeModel = (TopicTypeModel) model; 
    
    
    final AjaxOntopolyTextField searchField = new AjaxOntopolyTextField("searchField", new Model<String>("")); 
    add(searchField);
    
    final IModel<List<Topic>> searchResultModel = new LoadableDetachableModel<List<Topic>>() {
      @Override
      protected List<Topic> load() {
        try {
          errorInSearch = false;
          return topicTypeModel.getTopicType().searchAll(searchField.getDefaultModelObjectAsString());
        }
        catch(Exception e) {
          errorInSearch = true;
          return Collections.emptyList();
        }
      }     
    };
    
    final WebMarkupContainer searchResultContainer = new WebMarkupContainer("searchResultContainer") {
      public boolean isVisible() {
        return ((Collection)searchResultModel.getObject()).isEmpty() ? false : true;      
      }
    };
    searchResultContainer.setOutputMarkupPlaceholderTag(true);
    add(searchResultContainer);
    
    final WebMarkupContainer unsuccessfulSearchContainer = new WebMarkupContainer("unsuccessfulSearchContainer") {
      public boolean isVisible() {
        return !searchField.getDefaultModelObjectAsString().equals("") && ((Collection)searchResultModel.getObject()).isEmpty() ? true : false;      
      }
    };
    unsuccessfulSearchContainer.setOutputMarkupPlaceholderTag(true);
    add(unsuccessfulSearchContainer);
    
    Button button = new Button("searchButton", new ResourceModel("button.find"));
    button.add(new AjaxFormComponentUpdatingBehavior("onclick") {
      @Override
      protected void onUpdate(AjaxRequestTarget target) {  
        if(target != null) {
          target.addComponent(searchResultContainer);
          target.addComponent(unsuccessfulSearchContainer);
        }             
      }
    });
    add(button);

    Label message = new Label("message", new ResourceModel(errorInSearch ? "search.error" : "search.empty"));
    unsuccessfulSearchContainer.add(message);
    
    ListView searchResult = new ListView<Topic>("searchResult", searchResultModel) {
      @Override
      protected void populateItem(ListItem<Topic> item) {
        Topic topic = item.getModelObject();
        TopicMap topicMap = topic.getTopicMap();
        
        Map<String,String> pageParametersMap = new HashMap<String,String>();
        pageParametersMap.put("topicMapId", topicMap.getId());
        pageParametersMap.put("topicId", topic.getId());
        pageParametersMap.put("topicTypeId", topicTypeModel.getTopicType().getId());
   
        // link to instance
        item.add(new OntopolyBookmarkablePageLink("topic", InstancePage.class, new PageParameters(pageParametersMap), topic.getName()));
        
        // link to type
        Iterator it = topic.getTopicIF().getTypes().iterator();
        if (it.hasNext()) {
          Topic tt = new Topic((TopicIF)it.next(), topicMap);
          if(!tt.isSystemTopic()) {
            pageParametersMap.put("topicId", tt.getId());            
            item.add(new OntopolyBookmarkablePageLink("topicType", InstancesPage.class, new PageParameters(pageParametersMap), tt.getName()));          
          } else {
            item.add(new Label("topicType"));
          }          
        } else {
          item.add(new Label("topicType"));
        }
        
      }
    };
    searchResultContainer.add(searchResult);
  }

}
