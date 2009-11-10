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
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;


public class AssociationTypesPage extends AbstractTypesPage {
  
  public AssociationTypesPage() {	  
  }
  
  public AssociationTypesPage(PageParameters parameters) {
    super(parameters);
  }

  @Override
  protected int getSubMenuIndex() {
    return ASSOCIATION_TYPES_INDEX_IN_SUBMENU;
  }
  
  @Override
  protected Component createTreePanel(String id) {
    // create a tree
    final TreeModel treeModel = TreeModels.createAssociationTypesTreeModel(getTopicMapModel().getTopicMap(), isAdministrationEnabled()); 
    IModel<TreeModel> treeModelModel = new AbstractReadOnlyModel<TreeModel>() {
      @Override
      public TreeModel getObject() {
        return treeModel;
      }
    };
    return createTreePanel("tree", treeModelModel);    
  }

  @Override
  protected void createFunctionBoxes(MarkupContainer parent, String id) {
    parent.add(new FunctionBoxesPanel(id) {
      @Override
      protected List<Component> getFunctionBoxesList(String id) {
        List<Component> list = new ArrayList<Component>();
        list.add(new CreateInstanceFunctionBoxPanel(id, getTopicMapModel()) {
          @Override
          protected Class<? extends Page> getInstancePageClass() {
            return InstancePage.class;
          }
          @Override
          protected IModel<String> getTitleModel() {
            return new ResourceModel("association.types.create.text");
          }
          @Override
          protected Topic createInstance(TopicMap topicMap, String name) {
            return topicMap.createAssociationType(name);
          }          
        });
        return list;
      }
    });
  }
  
}
