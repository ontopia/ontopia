package ontopoly.pages;

import java.util.HashMap;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.Topic;
import ontopoly.components.LinkPanel;
import ontopoly.components.TitleHelpPanel;
import ontopoly.components.TreePanel;
import ontopoly.pojos.TopicNode;
import ontopoly.utils.TreeModels;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

public class InstanceTypesPage extends OntopolyAbstractPage {
  
  public InstanceTypesPage() {	  
  }
  
  public InstanceTypesPage(PageParameters parameters) {
   super(parameters);

    // Adding part containing title and help link
    createTitle();

    // create a tree
    Panel treePanel = new TreePanel("tree", TreeModels.createTopicTypesTreeModel(getTopicMapModel().getTopicMap(), isAnnotationEnabled(), isAdministrationEnabled())) {
      @Override
      protected void populateNode(WebMarkupContainer container, String id, TreeNode treeNode, int level) {
        DefaultMutableTreeNode mTreeNode = (DefaultMutableTreeNode)treeNode; 
        final TopicNode node = (TopicNode)mTreeNode.getUserObject();
        // create link with label
        container.add(new LinkPanel(id) {
          @Override
          protected Label newLabel(String id) {
            Topic topic = node.getTopic();
            final boolean isSystemTopic = topic.isSystemTopic();
            return new Label(id, new Model(topic.getName())) {
              @Override
              protected void onComponentTag(final ComponentTag tag) {
                if (isSystemTopic)
                  tag.put("class", "italic");
                super.onComponentTag(tag);              
              }
            };
          }
          @Override
          protected Link newLink(String id) {
            Map pageParametersMap = new HashMap(2);            
            pageParametersMap.put("topicMapId", node.getTopicMapId());
            pageParametersMap.put("topicId", node.getTopicId());            
            return new BookmarkablePageLink(id, InstancesPage.class, new PageParameters(pageParametersMap));
          }
        });
      }
    };
    treePanel.setOutputMarkupId(true);
    add(treePanel);

    // initialize parent components
    initParentComponents();        
  }

  @Override
  protected int getMainMenuIndex() {
    return INSTANCES_PAGE_INDEX_IN_MAINMENU; 
  }
  
  private void createTitle() {
    // Adding part containing title and help link
    add(new TitleHelpPanel("titlePartPanel", 
        new ResourceModel("instancetypes"), new ResourceModel("help.link.instancetypespage")));
  }
}
