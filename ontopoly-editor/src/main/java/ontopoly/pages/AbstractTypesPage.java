/*
 * #!
 * Ontopoly Editor
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */
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
import ontopoly.models.HelpLinkResourceModel;
import ontopoly.models.TopicMapModel;
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

    // add part containing title and help link
    int subMenuIndex = getSubMenuIndex();
    add(new MenuHelpPanel("titlePartPanel", getSubMenuItems(getTopicMapModel()), subMenuIndex,
        getNameModelForHelpLinkAddress(subMenuIndex)));

    Form<Object> form = new Form<Object>("form");
    add(form);
    form.setOutputMarkupId(true);

    // topic types title
    form.add(new Label("subTitle", getNameModelForType(subMenuIndex)));

    // function boxes
    createFunctionBoxes(form, "functionBoxes");

    // add tree panel
    form.add(createTreePanel("tree"));
    
    // initialize parent components
    initParentComponents();
  }

  @Override
  protected int getMainMenuIndex() {
    return ONTOLOGY_INDEX_IN_MAINMENU; 
  }

  protected abstract void createFunctionBoxes(MarkupContainer parent, String id);

  protected abstract int getSubMenuIndex();

  public static IModel<String> getNameModelForType(int type) {
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

  public static IModel<String> getNameModelForHelpLinkAddress(int type) {
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

  public static List<MenuItem> getSubMenuItems(TopicMapModel topicMapModel) {
    PageParameters parameters = new PageParameters();
    parameters.add("topicMapId", topicMapModel.getTopicMapId());

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
            return new Label(id, new Model<String>(getLabel(topic))) {
              @Override
              protected void onComponentTag(final ComponentTag tag) {
                if (isSystemTopic) {
                  tag.put("class", "italic");
                }
                super.onComponentTag(tag);              
              }
            };
          }
          @Override
          protected Link<Page> newLink(String id) {
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
