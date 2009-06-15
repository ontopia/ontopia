package ontopoly.pages;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreeModel;

import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.Topic;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.TopicMap;
import ontopoly.components.CreateInstanceFunctionBoxPanel;
import ontopoly.components.FunctionBoxesPanel;
import ontopoly.utils.TreeModels;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.PageParameters;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;


public class OccurrenceTypesPage extends AbstractTypesPage {

  public OccurrenceTypesPage() {	  
  }
  
  public OccurrenceTypesPage(PageParameters parameters) {
    super(parameters);
  }

  @Override
  protected int getSubMenuIndex() {
    return OCCURRENCE_TYPES_INDEX_IN_SUBMENU;
  }

  @Override
  protected Component createTreePanel(String id) {
    // create a tree
    TreeModel treeModel = TreeModels.createOccurrenceTypesTreeModel(getTopicMapModel().getTopicMap(), isAdministrationEnabled());
    return createTreePanel("tree", treeModel);
  }
  
  @Override
  protected void createFunctionBoxes(MarkupContainer parent, String id) {
    parent.add(new FunctionBoxesPanel(id) {
      @Override
      protected List getFunctionBoxesList(String id) {
        List list = new ArrayList();
        list.add(new CreateInstanceFunctionBoxPanel(id, getTopicMapModel()) {
          @Override
          protected Class getInstancePageClass() {
            return InstancePage.class;
          }
          @Override
          protected IModel getTitleModel() {
            return new ResourceModel("occurrence.types.create.text");
          }
          @Override
          protected Topic createInstance(TopicMap topicMap, String name) {
            return topicMap.createOccurrenceType(name);
          }          
        });
        return list;
      }
    });
  }

}
