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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import ontopoly.components.LinkPanel;
import ontopoly.components.TreePanel;
import ontopoly.model.PSI;
import ontopoly.model.Topic;
import ontopoly.model.TopicType;
import ontopoly.models.TopicModel;
import ontopoly.pojos.TopicNode;
import ontopoly.utils.OntopolyModelUtils;
import ontopoly.utils.OntopolyUtils;
import ontopoly.utils.TreeModels;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.tree.AbstractTree;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class EmbeddedHierarchicalInstancePage extends EmbeddedInstancePage {
  
  private TopicModel<Topic> hierarchyModel;
  
  public EmbeddedHierarchicalInstancePage(PageParameters parameters) {
    // expect there to be a topicId parameter
    super(parameters);
    
    // find hierarchy topic
    String hierarchyId = parameters.getString("hierarchyId");
    if (hierarchyId == null) {
      this.hierarchyModel = new TopicModel<Topic>(getHierarchyTopic(getTopic()));      
    } else {
      this.hierarchyModel = new TopicModel<Topic>(parameters.getString("topicMapId"), hierarchyId);
    }
    
    // create a tree
    TreePanel treePanel = createTreePanel("treePanel", createTreeModel(new TopicModel<Topic>(getHierarchyTopic()), new TopicModel<Topic>(getTopic())));
    treePanel.setOutputMarkupId(true);
    add(treePanel); 
  }
  
  @Override
  protected boolean isTraversable() {
    return true;
  }
  
  protected Topic getHierarchyTopic() {
    return hierarchyModel.getTopic();
  }

  protected Topic getHierarchyTopic(Topic topic) {
    // find hierarchy definition query for topic
    String query = getDefinitionQuery(topic);
    if (query != null) {
      return topic;
    }
    
    // find hierarchy definition query for topic's topic types
    Iterator<TopicType> titer = topic.getTopicTypes().iterator();
    while (titer.hasNext()) {
      TopicType topicType = titer.next();
      if (getDefinitionQuery(topicType) != null) {
        return topicType;
      }
    }
    return null;
  }

  protected String getDefinitionQuery(Topic topic) {
    TopicIF typeIf = OntopolyModelUtils.getTopicIF(topic.getTopicMap(), PSI.ON, "hierarchy-definition-query");
    if (typeIf == null) {
      return null;
    }
    OccurrenceIF occ = OntopolyModelUtils.findOccurrence(typeIf, topic.getTopicIF());
    return (occ == null ? null : occ.getValue());    
  }
  
  protected IModel<TreeModel> createTreeModel(final TopicModel<Topic> hierarchyTopicModel, final TopicModel<Topic> currentNodeModel) {
    final TreeModel treeModel;
    Topic hierarchyTopic = hierarchyTopicModel.getTopic();
    Topic currentNode = currentNodeModel.getTopic();
    
    // find hierarchy definition query for topic
    String query = (hierarchyTopic == null ? null : getDefinitionQuery(hierarchyTopic));

    if (query != null) {
      Map<String,TopicIF> params = new HashMap<String,TopicIF>(2);
      params.put("hierarchyTopic", hierarchyTopic.getTopicIF());
      params.put("currentNode", currentNode.getTopicIF());
      treeModel = TreeModels.createQueryTreeModel(currentNode.getTopicMap(), query, params);
    } else if (currentNode.isTopicType()) {
      // if no definition query found, then show topic in instance hierarchy
      treeModel = TreeModels.createTopicTypesTreeModel(currentNode.getTopicMap(), isAnnotationEnabled(), isAdministrationEnabled());
    } else {
      treeModel = TreeModels.createInstancesTreeModel(OntopolyUtils.getDefaultTopicType(currentNode), isAdministrationEnabled());
    }
    
    return new AbstractReadOnlyModel<TreeModel>() {
      @Override
      public TreeModel getObject() {
        return treeModel;
      }
    };
  }
  
  protected TreePanel createTreePanel(final String id, IModel<TreeModel> treeModel) {
    return new TreePanel(id, treeModel) {
      @Override
      protected boolean isMenuEnabled() {
        return true;
      }
      @Override
      protected void initializeTree(AbstractTree tree) {
        // expand current node
        TreeModel treeModel =  (TreeModel)tree.getModelObject();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)treeModel.getRoot();
        DefaultMutableTreeNode treeNode = findTopicNode(root, getTopic());
        if (treeNode != null) {
          expandNode(tree, treeNode);
        }
      }

      protected DefaultMutableTreeNode findTopicNode(DefaultMutableTreeNode parent, Topic topic) {
        @SuppressWarnings("rawtypes")
        Enumeration e = parent.children();
        while (e.hasMoreElements()) {
          DefaultMutableTreeNode child = (DefaultMutableTreeNode)e.nextElement();
          Topic nodeTopic = ((TopicNode)child.getUserObject()).getTopic();
          if (Objects.equals(nodeTopic, topic)) {
            return child;
          }
          DefaultMutableTreeNode found = findTopicNode(child, topic);
          if (found != null) {
            return found;
          }
        }
        return null;
      }
      
      @Override
      protected Component populateNode(String id, TreeNode treeNode) {
        DefaultMutableTreeNode mTreeNode = (DefaultMutableTreeNode)treeNode; 
        final TopicNode node = (TopicNode)mTreeNode.getUserObject();
        Topic topic = node.getTopic();
        final boolean isCurrentTopic = Objects.equals(topic, getTopic());
        // create link with label
        return new LinkPanel(id) {
          @Override
          protected Label newLabel(String id) {
            return new Label(id, new Model<String>(getLabel(node.getTopic()))) {
              @Override
              protected void onComponentTag(final ComponentTag tag) {
                if (isCurrentTopic) {
                  tag.put("class", "emphasis");
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
  }
  
  @Override  
  public PageParameters getPageParameters(Topic topic) {
    // add hierarchyId to parent parameters
    PageParameters params = super.getPageParameters(topic);            
    Topic hierarchyTopic = getHierarchyTopic();
    if (hierarchyTopic != null) {
      params.put("hierarchyId", hierarchyTopic.getId());
    }
    return params;
  }
  
}
