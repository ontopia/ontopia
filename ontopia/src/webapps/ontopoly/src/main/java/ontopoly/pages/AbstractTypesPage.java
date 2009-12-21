package ontopoly.pages;

import java.util.Arrays;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import ontopoly.components.LinkPanel;
import ontopoly.components.MenuHelpPanel;
import ontopoly.components.TreePanel;
import ontopoly.model.Topic;
import ontopoly.model.TopicMap;
import ontopoly.models.HelpLinkResourceModel;
import ontopoly.pojos.MenuItem;
import ontopoly.pojos.TopicNode;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

public abstract class AbstractTypesPage extends OntopolyAbstractPage {
  
  protected static final int TOPIC_TYPES_INDEX_IN_SUBMENU = 0;

  protected static final int OCCURRENCE_TYPES_INDEX_IN_SUBMENU = 1;

  protected static final int ASSOCIATION_TYPES_INDEX_IN_SUBMENU = 2;

  protected static final int ROLE_TYPES_INDEX_IN_SUBMENU = 3;

  protected static final int NAME_TYPES_INDEX_IN_SUBMENU = 4;
  
  public AbstractTypesPage() {	  
  }
  
  public AbstractTypesPage(PageParameters parameters) {
    super(parameters);

    Form form = new Form("form");
    add(form);
    form.setOutputMarkupId(true);

    // add part containing title and help link
    createMenuPanel();

    // topic types title
    add(new Label("typesTitle", getNameModelForType(getSubMenuIndex())));

    // function boxes
    createFunctionBoxes(form, "functionBoxes");

    // add tree panel
    add(createTreePanel("tree"));
    
    // initialize parent components
    initParentComponents();
  }

  @Override
  protected int getMainMenuIndex() {
    return ONTOLOGY_INDEX_IN_MAINMENU; 
  }
  
  private void createMenuPanel() {
    add(new MenuHelpPanel("menuHelpPart", getSubMenuItem(getTopicMapModel()), getSubMenuIndex(),
        getNameModelForHelpLinkAddress(getSubMenuIndex())));
  }

  protected abstract void createFunctionBoxes(MarkupContainer parent, String id);

  protected abstract int getSubMenuIndex();

  private static IModel getNameModelForType(int type) {
    if (type == TOPIC_TYPES_INDEX_IN_SUBMENU) {
      return new ResourceModel("topic.types");
    } else if (type == OCCURRENCE_TYPES_INDEX_IN_SUBMENU) {
      return new ResourceModel("occurrence.types");
    } else if (type == ASSOCIATION_TYPES_INDEX_IN_SUBMENU) {
      return new ResourceModel("association.types");
    } else if (type == ROLE_TYPES_INDEX_IN_SUBMENU) {
      return new ResourceModel("role.types");
    } else if (type == NAME_TYPES_INDEX_IN_SUBMENU) {
      return new ResourceModel("name.types");
    } else {
      return null;
    }
  }

  private static IModel<String> getNameModelForHelpLinkAddress(int type) {
    if (type == TOPIC_TYPES_INDEX_IN_SUBMENU) {
      return new HelpLinkResourceModel("help.link.topictypespage");
    } else if (type == OCCURRENCE_TYPES_INDEX_IN_SUBMENU) {
      return new HelpLinkResourceModel("help.link.occurrencetypespage");
    } else if (type == ASSOCIATION_TYPES_INDEX_IN_SUBMENU) {
      return new HelpLinkResourceModel("help.link.associationtypespage");
    } else if (type == ROLE_TYPES_INDEX_IN_SUBMENU) {
      return new HelpLinkResourceModel("help.link.roletypespage");
    } else if (type == NAME_TYPES_INDEX_IN_SUBMENU) {
      return new HelpLinkResourceModel("help.link.nametypespage");
    } else {
      return null;
    }
  }

  private static List<MenuItem> getSubMenuItem(IModel model) {
    PageParameters parameters = new PageParameters();
    parameters.add("topicMapId", ((TopicMap) model.getObject()).getId());

    List<MenuItem> subMenuItems = Arrays.asList(new MenuItem[] {
        new MenuItem(new Label("caption", new ResourceModel("topic.types")), TopicTypesPage.class, parameters),
        new MenuItem(new Label("caption",new ResourceModel("occurrence.types")), OccurrenceTypesPage.class, parameters),
        new MenuItem(new Label("caption",new ResourceModel("association.types")), AssociationTypesPage.class, parameters),
        new MenuItem(new Label("caption",new ResourceModel("role.types")), RoleTypesPage.class, parameters),
        new MenuItem(new Label("caption",new ResourceModel("name.types")), NameTypesPage.class, parameters) });
    return subMenuItems;
  }

  protected abstract Component createTreePanel(String id);
  
  protected TreePanel createTreePanel(String id, IModel<TreeModel> treeModelModel) {
    TreePanel treePanel = new TreePanel(id, treeModelModel) {
      @Override
      protected Component populateNode(String id, TreeNode treeNode) {
        DefaultMutableTreeNode mTreeNode = (DefaultMutableTreeNode)treeNode; 
        final TopicNode node = (TopicNode)mTreeNode.getUserObject();
        // create link with label
        return new LinkPanel(id) {
          @Override
          protected Label newLabel(String id) {
            Topic topic = node.getTopic();
            final boolean isSystemTopic = topic.isSystemTopic();
            return new Label(id, new Model<String>(topic.getName())) {
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
            Topic topic = node.getTopic();
            return new BookmarkablePageLink<Page>(id, getPageClass(topic), getPageParameters(topic));
          }
        };
      }
    };
    treePanel.setOutputMarkupId(true);
    return treePanel;
  }

  @Override
  public Class<? extends Page> getPageClass(Topic topic) {
    return InstancePage.class;
  }
  
  @Override
  public PageParameters getPageParameters(Topic topic) {
    PageParameters params = new PageParameters();
    params.put("topicMapId", topic.getTopicMap().getId());
    params.put("topicId", topic.getId());
    params.put("ontology", "true");
    return params;    
  }
  
}
