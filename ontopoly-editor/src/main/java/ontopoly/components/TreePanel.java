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
package ontopoly.components;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.tree.AbstractTree;
import org.apache.wicket.markup.html.tree.BaseTree;
import org.apache.wicket.markup.html.tree.BaseTree.LinkType;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public abstract class TreePanel extends Panel {

  protected final Tree tree;
  
  public TreePanel(String id, IModel<TreeModel> treeModelModel) {
    super(id, treeModelModel);
    
    final WebMarkupContainer treeContainer = new WebMarkupContainer("treeContainer") {
      @Override
      protected void onComponentTag(ComponentTag tag) {
        TreeModel treeModel = (TreeModel)TreePanel.this.getDefaultModelObject();
        if (treeModel.getChildCount(treeModel.getRoot()) == 0) {
          tag.put("class", "hide");
        }
      }
    };
    treeContainer.setOutputMarkupId(true);
    add(treeContainer);

    tree = new Tree("tree", treeModelModel);
    
    tree.setLinkType(LinkType.AJAX);

    tree.setRootLess(true);
    initializeTree(tree);

    treeContainer.add(tree);

    final WebMarkupContainer menuTop = new WebMarkupContainer("menuTop") {
      @Override
      public boolean isVisible() {
       return isMenuEnabled(); 
      }      
    };
    treeContainer.add(menuTop);
    
    final WebMarkupContainer expandTop = new WebMarkupContainer("expandTop");
    expandTop.setOutputMarkupId(true);
    menuTop.add(expandTop);

    final WebMarkupContainer collapseTop = new WebMarkupContainer("collapseTop");
    collapseTop.add(new SimpleAttributeModifier("class", "hide"));
    collapseTop.setOutputMarkupId(true);
    menuTop.add(collapseTop);

    final WebMarkupContainer menuBottom = new WebMarkupContainer("menuBottom") {
      @Override
      public boolean isVisible() {
       return isMenuEnabled(); 
      }      
    };
    treeContainer.add(menuBottom);

    final WebMarkupContainer expandBottom = new WebMarkupContainer("expandBottom");
    expandBottom.setOutputMarkupId(true);
    menuBottom.add(expandBottom);

    final WebMarkupContainer collapseBottom = new WebMarkupContainer("collapseBottom");
    collapseBottom.add(new SimpleAttributeModifier("class", "hide"));
    collapseBottom.setOutputMarkupId(true);
    menuBottom.add(collapseBottom);

    expandTop.add(new AjaxFallbackLink<Object>("expandAllTop") {
      @Override
      public void onClick(AjaxRequestTarget target) {
        tree.getTreeState().expandAll();
        expandTop.add(new SimpleAttributeModifier("class", "hide"));
        expandBottom.add(new SimpleAttributeModifier("class", "hide"));
        collapseTop.add(new AttributeModifier("class", false, new Model<String>("")));
        collapseBottom.add(new AttributeModifier("class", false, new Model<String>("")));
        if (target != null) {
          target.addComponent(tree);
          target.addComponent(expandTop);
          target.addComponent(collapseTop);
          target.addComponent(expandBottom);
          target.addComponent(collapseBottom);
        }
      }
    });

    collapseTop.add(new AjaxFallbackLink<Object>("collapseAllTop") {
      @Override
      public void onClick(AjaxRequestTarget target) {
        tree.getTreeState().collapseAll();
        collapseTop.add(new SimpleAttributeModifier("class", "hide"));
        collapseBottom.add(new SimpleAttributeModifier("class", "hide"));
        expandTop.add(new AttributeModifier("class", false, new Model<String>("")));
        expandBottom.add(new AttributeModifier("class", false, new Model<String>("")));
        if (target != null) {
          target.addComponent(tree);
          target.addComponent(expandTop);
          target.addComponent(collapseTop);
          target.addComponent(expandBottom);
          target.addComponent(collapseBottom);
        }
      }
    });

    expandBottom.add(new AjaxFallbackLink<Object>("expandAllBottom") {
      @Override
      public void onClick(AjaxRequestTarget target) {
        tree.getTreeState().expandAll();
        expandTop.add(new SimpleAttributeModifier("class", "hide"));
        expandBottom.add(new SimpleAttributeModifier("class", "hide"));
        collapseTop.add(new AttributeModifier("class", false, new Model<String>("")));
        collapseBottom
            .add(new AttributeModifier("class", false, new Model<String>("")));
        if (target != null) {
          target.addComponent(tree);
          target.addComponent(expandTop);
          target.addComponent(collapseTop);
          target.addComponent(expandBottom);
          target.addComponent(collapseBottom);
        }
      }
    });

    collapseBottom.add(new AjaxFallbackLink<Object>("collapseAllBottom") {
      @Override
      public void onClick(AjaxRequestTarget target) {
        tree.getTreeState().collapseAll();
        collapseTop.add(new SimpleAttributeModifier("class", "hide"));
        collapseBottom.add(new SimpleAttributeModifier("class", "hide"));
        expandTop.add(new AttributeModifier("class", false, new Model<String>("")));
        expandBottom.add(new AttributeModifier("class", false, new Model<String>("")));
        if (target != null) {
          target.addComponent(tree);
          target.addComponent(expandTop);
          target.addComponent(collapseTop);
          target.addComponent(expandBottom);
          target.addComponent(collapseBottom);
        }
      }
    });
  }

  protected boolean isMenuEnabled() {
    return true;
  }
  
  protected void initializeTree(AbstractTree tree) {
    tree.getTreeState().collapseAll();
  }

  protected void expandNode(AbstractTree tree, DefaultMutableTreeNode treeNode) {
    if (treeNode.getParent() != null) {
      expandNode(tree, (DefaultMutableTreeNode)treeNode.getParent());
    }
    tree.getTreeState().expandNode(treeNode);
  }      

  /**
   * Renders a tree node. Subclasses should override this method so that the tree's nodes can be rendered.
   * @param id The id of the node component.
   * @param treeNode The tree node that should be rendered.
   */
  protected abstract Component populateNode(String id, TreeNode treeNode);

  @Override
  public MarkupContainer setDefaultModel(IModel<?> model) {
    // notify nested tree about new tree model
    tree.setDefaultModel(model);
    return super.setDefaultModel(model);
  }
  
  private class Tree extends BaseTree {
    public Tree(String id, IModel<TreeModel> treeModelModel) {
      super(id, treeModelModel);
    }
    @Override
    protected Component newNodeComponent(String id, IModel<java.lang.Object> model) {
      TreeNode treeNode = (TreeNode) model.getObject();
      return populateNode(id, treeNode);
    }
  }
}
