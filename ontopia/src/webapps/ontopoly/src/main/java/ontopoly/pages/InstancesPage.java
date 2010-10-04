package ontopoly.pages;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import ontopoly.OntopolySession;
import ontopoly.components.CreateInstanceFunctionBoxPanel;
import ontopoly.components.FunctionBoxesPanel;
import ontopoly.components.InstanceSearchPanel;
import ontopoly.components.LinkFunctionBoxPanel;
import ontopoly.components.LinkPanel;
import ontopoly.components.OntopolyBookmarkablePageLink;
import ontopoly.components.TitleHelpPanel;
import ontopoly.components.TreePanel;
import ontopoly.components.TopicListPanel;
import ontopoly.model.OntopolyTopicIF;
import ontopoly.model.OntopolyTopicMapIF;
import ontopoly.model.TopicTypeIF;
import ontopoly.models.HelpLinkResourceModel;
import ontopoly.models.TopicTypeModel;
import ontopoly.pojos.TopicNode;
import ontopoly.utils.TreeModels;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

public class InstancesPage extends OntopolyAbstractPage {

  protected TopicTypeModel topicTypeModel;
  
  public InstancesPage() {	  
  }
  
  public InstancesPage(PageParameters parameters) {
    super(parameters);
	
    this.topicTypeModel = new TopicTypeModel(parameters.getString("topicMapId"), parameters.getString("topicId"));
    
    // Adding part containing title and help link
    createTitle();
    
    // Add form
    Form form = new Form("form");
    add(form);
    form.setOutputMarkupId(true);

    // Add list of instances
    TopicTypeIF topicType = topicTypeModel.getTopicType();
    
    if (topicType.isLargeInstanceSet()) {
      form.add(new InstanceSearchPanel("contentPanel", topicTypeModel));
    } else if (topicType.hasHierarchy()) {
      // create a tree
      final TreeModel treeModel = TreeModels.createInstancesTreeModel(topicTypeModel.getTopicType(), isAdministrationEnabled());
      IModel<TreeModel> treeModelModel = new AbstractReadOnlyModel<TreeModel>() {
        @Override
        public TreeModel getObject() {
          return treeModel;
        }        
      };
      Panel treePanel = new TreePanel("contentPanel", treeModelModel) {
        @Override
        protected Component populateNode(String id, TreeNode treeNode) {
          DefaultMutableTreeNode mTreeNode = (DefaultMutableTreeNode)treeNode; 
          final TopicNode node = (TopicNode)mTreeNode.getUserObject();
          // create link with label
          return new LinkPanel(id) {
            @Override
            protected Label newLabel(String id) {
              OntopolyTopicIF topic = node.getTopic();
              final boolean isSystemTopic = topic.isSystemTopic();
              return new Label(id, new Model<String>(getLabel(topic))) {
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
              PageParameters params = new PageParameters();            
              params.put("topicMapId", node.getTopicMapId());
              params.put("topicId", node.getTopicId());            
              params.put("topicTypeId", topicTypeModel.getTopicType().getId());
              return new BookmarkablePageLink<Page>(id, InstancePage.class, params);
            }
          };
        }
      };
      treePanel.setOutputMarkupId(true);
      form.add(treePanel);
    } else {
      // just make a list
      form.add(new TopicListPanel("contentPanel", new AbstractReadOnlyModel<List<OntopolyTopicIF>>() {
          @Override
          public List<OntopolyTopicIF> getObject() {
            return topicTypeModel.getTopicType().getInstances();
          }
        }));
    }
    
    // Function boxes
    createFunctionBoxes(form, "functionBoxes");
    
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
        new PropertyModel<String>(topicTypeModel, "name"), new HelpLinkResourceModel("help.link.instancespage")));
  }

  private void createFunctionBoxes(MarkupContainer parent, String id) {
    parent.add(new FunctionBoxesPanel(id) {
      @Override
      protected List<Component> getFunctionBoxesList(String id) {
        List<Component> list = new ArrayList<Component>();
        TopicTypeIF topicType = topicTypeModel.getTopicType();
        if (!topicType.isAbstract() && !topicType.isReadOnly()) {
          list.add(new CreateInstanceFunctionBoxPanel(id, getTopicMapModel()) {
            @Override
            protected Class<? extends Page> getInstancePageClass() {
              return InstancePage.class;
            }
            @Override
            protected IModel<String> getTitleModel() {
              return new ResourceModel("instances.create.text");
            }
            @Override
            protected OntopolyTopicIF createInstance(OntopolyTopicMapIF topicMap, String name) {
              TopicTypeIF topicType = topicTypeModel.getTopicType();
              return topicType.createInstance(name);
            }
            
          });
        }
        list.add(new LinkFunctionBoxPanel(id) {
          @Override
          public boolean isVisible() {
            return true;
          }
          @Override
          protected Component getLabel(String id) {
            return new Label(id, new ResourceModel("edit.topic.type"));
          }
          @Override
          protected Component getLink(String id) {
            OntopolyTopicMapIF tm = getTopicMapModel().getTopicMap();
            TopicTypeIF tt = topicTypeModel.getTopicType();
            PageParameters params = new PageParameters();
            params.put("topicMapId", tm.getId());
            params.put("topicId", tt.getId());
            params.put("ontology", "true");
            //TODO direct link to correct instance page
            return new OntopolyBookmarkablePageLink(id, InstancePage.class, params, tt.getName());
          }
        });
        
        return list;
      }
    });
  }

  @Override
  public void onDetach() {
    topicTypeModel.detach();
    super.onDetach();
  }
   
}
