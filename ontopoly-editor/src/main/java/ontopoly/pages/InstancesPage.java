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

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import ontopoly.components.CreateInstanceFunctionBoxPanel;
import ontopoly.components.FunctionBoxesPanel;
import ontopoly.components.InstanceSearchPanel;
import ontopoly.components.LinkFunctionBoxPanel;
import ontopoly.components.LinkPanel;
import ontopoly.components.OntopolyBookmarkablePageLink;
import ontopoly.components.TitleHelpPanel;
import ontopoly.components.TopicListPanel;
import ontopoly.components.TreePanel;
import ontopoly.model.Topic;
import ontopoly.model.TopicMap;
import ontopoly.model.TopicType;
import ontopoly.models.HelpLinkResourceModel;
import ontopoly.models.TopicTypeModel;
import ontopoly.pojos.TopicNode;
import ontopoly.utils.TreeModels;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
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
    Form<Object> form = new Form<Object>("form");
    add(form);
    form.setOutputMarkupId(true);

    // Add list of instances
    TopicType topicType = topicTypeModel.getTopicType();

    if (topicType.isLargeInstanceSet()) {
      form.add(new InstanceSearchPanel("instancesPanel", topicTypeModel));
    } else if (topicType.hasHierarchy()) {
      // create a tree
      final TreeModel treeModel = TreeModels.createInstancesTreeModel(topicTypeModel.getTopicType(), isAdministrationEnabled());
      IModel<TreeModel> treeModelModel = new AbstractReadOnlyModel<TreeModel>() {
        @Override
        public TreeModel getObject() {
          return treeModel;
        }        
      };
      Panel treePanel = new TreePanel("instancesPanel", treeModelModel) {
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
      form.add(new TopicListPanel("instancesPanel", new AbstractReadOnlyModel<List<Topic>>() {

          // short time cache because getObject is called for each object in the list
          transient private List<Topic> instances = null;

          @Override
          public List<Topic> getObject() {
            if (instances == null) {
              instances = topicTypeModel.getTopicType().getInstances();
            }
            return instances;
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
        TopicType topicType = topicTypeModel.getTopicType();
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
            protected Topic createInstance(TopicMap topicMap, String name) {
              TopicType topicType = topicTypeModel.getTopicType();
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
            TopicMap tm = getTopicMapModel().getTopicMap();
            TopicType tt = topicTypeModel.getTopicType();
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
