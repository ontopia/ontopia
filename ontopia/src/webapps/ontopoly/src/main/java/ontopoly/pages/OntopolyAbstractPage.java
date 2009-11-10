package ontopoly.pages;

import java.util.Arrays;
import java.util.List;

import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.TopicMap;
import ontopoly.components.FooterPanel;
import ontopoly.components.MenuPanel;
import ontopoly.components.SubSubHeaderPanel;
import ontopoly.models.TopicMapModel;
import ontopoly.pojos.MenuItem;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.ResourceModel;

public abstract class OntopolyAbstractPage extends AbstractProtectedOntopolyPage {

  protected static final int NONE_SELECTED = -1;
  
  protected static final int DESCRIPTION_PAGE_INDEX_IN_MAINMENU = 0;

  protected static final int ADMIN_PAGE_INDEX_IN_MAINMENU = 1;

  protected static final int ONTOLOGY_INDEX_IN_MAINMENU = 2;

  protected static final int INSTANCES_PAGE_INDEX_IN_MAINMENU = 3;

  private TopicMapModel topicMapModel;
  
  public OntopolyAbstractPage() {	  
  }
  
  public OntopolyAbstractPage(PageParameters parameters) {
    super(parameters);
    this.topicMapModel = new TopicMapModel(parameters.getString("topicMapId"));
    // NOTE: subclasses must call initParentComponents
  }

  protected void initParentComponents() {
    add(new SubSubHeaderPanel("header", topicMapModel , getMainMenuIndex(), getMainMenuItem(topicMapModel)));
    add(new FooterPanel("footer"));
    
    add(new Label("title", new AbstractReadOnlyModel() {
      @Override
      public Object getObject() {
        return "[Ontopoly] " + getTopicMapModel().getTopicMap().getName();   
      }
    }));
    add(new MenuPanel("lowerMenu", getMainMenuItem(topicMapModel)));        
  }
  
  protected abstract int getMainMenuIndex();
  
  protected TopicMapModel getTopicMapModel() {
    return topicMapModel;    
  }
  
  protected TopicMap getTopicMap() {
    return topicMapModel.getTopicMap();
  }
  
  public void onDetach() {
    topicMapModel.detach();
    super.onDetach();
  }
  
  private static List<MenuItem> getMainMenuItem(TopicMapModel topicMapModel) {
    PageParameters parameters = new PageParameters();
    parameters.add("topicMapId", topicMapModel.getTopicMap().getId());

    List<MenuItem> mainMenuItems = Arrays.asList(new MenuItem[] {
        new MenuItem(new Label("caption", new ResourceModel("description")),
            DescriptionPage.class, parameters),
        new MenuItem(new Label("caption", new ResourceModel("administration")),
            AdminPage.class, parameters),
        new MenuItem(new Label("caption", new ResourceModel("ontology")),
            TopicTypesPage.class, parameters),
        new MenuItem(new Label("caption", new ResourceModel("instances")),
            InstanceTypesPage.class, parameters) });
    return mainMenuItems;
  }
  
}
