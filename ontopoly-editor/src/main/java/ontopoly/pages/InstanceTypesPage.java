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

import java.util.HashMap;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import ontopoly.components.LinkPanel;
import ontopoly.components.TitleHelpPanel;
import ontopoly.components.TreePanel;
import ontopoly.model.Topic;
import ontopoly.models.HelpLinkResourceModel;
import ontopoly.pojos.TopicNode;
import ontopoly.utils.TreeModels;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
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
    final TreeModel treeModel = TreeModels.createTopicTypesTreeModel(getTopicMapModel().getTopicMap(), isAnnotationEnabled(), isAdministrationEnabled());
    IModel<TreeModel> treeModelModel = new AbstractReadOnlyModel<TreeModel>() {
      @Override
      public TreeModel getObject() {
        return treeModel;      
      }
    };
    Panel treePanel = new TreePanel("tree", treeModelModel) {
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
            Map<String,String> pageParametersMap = new HashMap<String,String>(2);            
            pageParametersMap.put("topicMapId", node.getTopicMapId());
            pageParametersMap.put("topicId", node.getTopicId());            
            return new BookmarkablePageLink<Page>(id, InstancesPage.class, new PageParameters(pageParametersMap));
          }
        };
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
        new ResourceModel("instancetypes"), new HelpLinkResourceModel("help.link.instancetypespage")));
  }
}
