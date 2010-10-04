package ontopoly.pages;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.ontopia.topicmaps.core.TopicIF;
import ontopoly.components.OntopolyBookmarkablePageLink;
import ontopoly.components.TitleHelpPanel;
import ontopoly.model.Topic;
import ontopoly.model.TopicMap;
import ontopoly.models.HelpLinkResourceModel;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

public class SearchPage extends OntopolyAbstractPage {
   
  private boolean errorInSearch = false;
  
  public SearchPage() {	  
  }
  
  public SearchPage(PageParameters parameters) {
    super(parameters);

    String searchText = parameters.getString("searchTerm");
    
    // Adding part containing title and help link
    createTitle();

    final TextField<String> searchField = new TextField<String>("searchField", new Model<String>(searchText));
    Form<Object> form = new Form<Object>("searchForm") {
      @Override
      protected void onSubmit() {
          Map<String,String> pageParametersMap = new HashMap<String,String>();
          pageParametersMap.put("topicMapId", getTopicMap().getId());
          pageParametersMap.put("searchTerm", searchField.getDefaultModelObjectAsString());
          setResponsePage(SearchPage.class, new PageParameters(pageParametersMap));
          setRedirect(false);
      }
    };
    add(form);
    form.add(searchField);
        
    final IModel<List<Topic>> searchResultModel = new LoadableDetachableModel<List<Topic>>() {
      @Override
      protected List<Topic> load() {
        try {
          errorInSearch = false;
          List<Topic> result = getTopicMap().searchAll(searchField.getDefaultModelObjectAsString());
          AbstractOntopolyPage page = (AbstractOntopolyPage)getPage();
          page.filterTopics(result);
          return result;
        }
        catch(Exception e) {
          errorInSearch = true;
          return Collections.emptyList();
        }
      }     
    };
    
    final WebMarkupContainer searchResultContainer = new WebMarkupContainer("searchResultContainer") {
      public boolean isVisible() {
        return ((List<Topic>)searchResultModel.getObject()).isEmpty() ? false : true;      
      }
    };
    searchResultContainer.setOutputMarkupPlaceholderTag(true);
    add(searchResultContainer);
    
    final WebMarkupContainer unsuccessfulSearchContainer = new WebMarkupContainer("unsuccessfulSearchContainer") {
      public boolean isVisible() {
        return !searchField.getDefaultModelObjectAsString().equals("") && ((List<Topic>)searchResultModel.getObject()).isEmpty() ? true : false;      
      }
    };
    unsuccessfulSearchContainer.setOutputMarkupPlaceholderTag(true);
    add(unsuccessfulSearchContainer);
       
    form.add(new Button("searchButton", new ResourceModel("button.find")));
      
    Label message = new Label("message", new ResourceModel(errorInSearch ? "search.error" : "search.empty"));
    unsuccessfulSearchContainer.add(message);
  
    ListView<Topic> searchResult = new ListView<Topic>("searchResult", searchResultModel) {

      @Override
      protected void populateItem(ListItem<Topic> item) {
        Topic topic = item.getModelObject();
        TopicMap topicMap = topic.getTopicMap();
                
        Map<String,String> pageParametersMap = new HashMap<String,String>();
        pageParametersMap.put("topicMapId", topicMap.getId());
        pageParametersMap.put("topicId", topic.getId());
   
        // link to instance
        item.add(new OntopolyBookmarkablePageLink("topic", InstancePage.class, new PageParameters(pageParametersMap), topic.getName())); 
        
        // link to type
        Iterator<TopicIF> it = topic.getTopicIF().getTypes().iterator();
        if (it.hasNext()) {
          Topic tt = new Topic(it.next(), topicMap);
          if(!tt.isSystemTopic()) {
            pageParametersMap.put("topicId", tt.getId());            
            item.add(new OntopolyBookmarkablePageLink("topicType", InstancesPage.class, new PageParameters(pageParametersMap), tt.getName()));          
          } else {
            item.add(new Label("topicType", tt.getName()));
          }          
        } else {
          item.add(new Label("topicType"));
        }
      }
      
    };
    searchResultContainer.add(searchResult);

    // initialize parent components
    initParentComponents();        
  }

  @Override
  protected int getMainMenuIndex() {
    return NONE_SELECTED; 
  }
  
  private void createTitle() {
    TitleHelpPanel titlePartPanel = new TitleHelpPanel("titlePartPanel",
        new ResourceModel("search.results"), new HelpLinkResourceModel("help.link.namefieldconfigpage"));
    titlePartPanel.setRenderBodyOnly(true);
    add(titlePartPanel);
  }

}
