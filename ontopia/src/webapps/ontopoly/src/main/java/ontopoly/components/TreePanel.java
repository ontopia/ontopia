package ontopoly.components;

import java.io.Serializable;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.tree.DefaultAbstractTree;
import org.apache.wicket.extensions.markup.html.tree.DefaultAbstractTree.LinkType;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.tree.AbstractTree;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public abstract class TreePanel extends Panel {

  public TreePanel(String id, final TreeModel treeModel) {
    this(id, new Model((Serializable)treeModel));
  }
  
  public TreePanel(String id, IModel treeModelModel) {
    super(id, treeModelModel);
    
    final WebMarkupContainer treeContainer = new WebMarkupContainer("treeContainer") {
      protected void onComponentTag(ComponentTag tag) {
        TreeModel treeModel = (TreeModel)TreePanel.this.getModelObject();
        if (treeModel.getChildCount(treeModel.getRoot()) == 0) {
          tag.put("class", "hide");
        }
      }
    };
    treeContainer.setOutputMarkupId(true);
    add(treeContainer);

    final Tree tree = new Tree("tree", treeModelModel);

    tree.setLinkType(LinkType.AJAX_FALLBACK);

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

    expandTop.add(new AjaxFallbackLink("expandAllTop") {
      @Override
      public void onClick(AjaxRequestTarget target) {
        tree.getTreeState().expandAll();
        expandTop.add(new SimpleAttributeModifier("class", "hide"));
        expandBottom.add(new SimpleAttributeModifier("class", "hide"));
        collapseTop.add(new AttributeModifier("class", false, new Model("")));
        collapseBottom.add(new AttributeModifier("class", false, new Model("")));
        if (target != null) {
          target.addComponent(tree);
          target.addComponent(expandTop);
          target.addComponent(collapseTop);
          target.addComponent(expandBottom);
          target.addComponent(collapseBottom);
        }
      }
    });

    collapseTop.add(new AjaxFallbackLink("collapseAllTop") {
      @Override
      public void onClick(AjaxRequestTarget target) {
        tree.getTreeState().collapseAll();
        collapseTop.add(new SimpleAttributeModifier("class", "hide"));
        collapseBottom.add(new SimpleAttributeModifier("class", "hide"));
        expandTop.add(new AttributeModifier("class", false, new Model("")));
        expandBottom.add(new AttributeModifier("class", false, new Model("")));
        if (target != null) {
          target.addComponent(tree);
          target.addComponent(expandTop);
          target.addComponent(collapseTop);
          target.addComponent(expandBottom);
          target.addComponent(collapseBottom);
        }
      }
    });

    expandBottom.add(new AjaxFallbackLink("expandAllBottom") {
      @Override
      public void onClick(AjaxRequestTarget target) {
        tree.getTreeState().expandAll();
        expandTop.add(new SimpleAttributeModifier("class", "hide"));
        expandBottom.add(new SimpleAttributeModifier("class", "hide"));
        collapseTop.add(new AttributeModifier("class", false, new Model("")));
        collapseBottom
            .add(new AttributeModifier("class", false, new Model("")));
        if (target != null) {
          target.addComponent(tree);
          target.addComponent(expandTop);
          target.addComponent(collapseTop);
          target.addComponent(expandBottom);
          target.addComponent(collapseBottom);
        }
      }
    });

    collapseBottom.add(new AjaxFallbackLink("collapseAllBottom") {
      @Override
      public void onClick(AjaxRequestTarget target) {
        tree.getTreeState().collapseAll();
        collapseTop.add(new SimpleAttributeModifier("class", "hide"));
        collapseBottom.add(new SimpleAttributeModifier("class", "hide"));
        expandTop.add(new AttributeModifier("class", false, new Model("")));
        expandBottom.add(new AttributeModifier("class", false, new Model("")));
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
    if (treeNode.getParent() != null)
      expandNode(tree, (DefaultMutableTreeNode)treeNode.getParent());
    tree.getTreeState().expandNode(treeNode);
  }      

  /**
   * Renders a tree node. Subclasses should override this method so that the tree's nodes can be rendered.
   * @param container The container to add the node component to.
   * @param id The id of the node component.
   * @param treeNode The tree node that should be rendered.
   * @param level The level in the tree that the tree node is placed.
   */
  protected abstract void populateNode(WebMarkupContainer container, String id, TreeNode treeNode, int level);

  private class Tree extends DefaultAbstractTree {
    public Tree(String id, IModel treeModelModel) {
      super(id, treeModelModel);
    }
    @Override
    protected void populateTreeItem(WebMarkupContainer item, int level) {
      final TreeNode node = (TreeNode) item.getModelObject();

      item.add(newIndentation(item, "indent", (TreeNode) item.getModelObject(), level));

      item.add(newJunctionLink(item, "link", "image", node));

      // let subclass populate node container
      TreePanel.this.populateNode(item, "node", node, level);
    }
  };
}
