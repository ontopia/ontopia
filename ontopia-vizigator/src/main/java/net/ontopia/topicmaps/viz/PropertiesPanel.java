/*
 * #!
 * Ontopia Vizigator
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

package net.ontopia.topicmaps.viz;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JTree.DynamicUtilTreeNode;
import javax.swing.event.MouseInputAdapter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.utils.CollectionUtils;

/**
 * INTERNAL: PRIVATE: Description: A properties dialog for topics
 */

public class PropertiesPanel extends JScrollPane {

  private TopicIF target;
  private JTree tree;
  private VizController controller;
  private JPopupMenu popup;
  private JMenuItem goToMenuItem;

  /**
   * @throws java.awt.HeadlessException
   */
  public PropertiesPanel(VizController aController) {

    controller = aController;
    new JScrollPane(tree);
    tree = new JTree();
    this.setViewportView(tree);
    tree.getSelectionModel().setSelectionMode(
        TreeSelectionModel.SINGLE_TREE_SELECTION);

    popup = new JPopupMenu();
    goToMenuItem = new JMenuItem(Messages.getString("Viz.PopupGoTo"));
    goToMenuItem.setEnabled(controller.isApplet());
    goToMenuItem.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent anEvent) {

        controller.openPropertiesURL(anEvent.getActionCommand());
      }
    });

    popup.add(goToMenuItem);

    tree.addMouseListener(new MouseInputAdapter() {
      @Override
      public void mousePressed(MouseEvent anEvent) {
        maybeShowPopupMenu(anEvent);
      }

      @Override
      public void mouseReleased(MouseEvent anEvent) {
        maybeShowPopupMenu(anEvent);
      }

      private void maybeShowPopupMenu(MouseEvent anEvent) {
        if (anEvent.isPopupTrigger()) {
          DefaultMutableTreeNode node = ((DefaultMutableTreeNode) tree
              .getSelectionPath().getLastPathComponent());
          if (node.isLeaf()) {
            String string = (String) node.getUserObject();

            boolean success = true;
            try {
              new URL(string);
            } catch (MalformedURLException e) {
              success = false;
            }
            if (success) {
              goToMenuItem.setActionCommand(string);
              popup.show(tree, anEvent.getX(), anEvent.getY());
            }
          }
        }
      }
    });

    tree.setShowsRootHandles(true);
    tree.setRootVisible(false);
  }

  public void setTarget(TopicIF topic) {
    target = topic;
    this.rebuildContents();
    this.expandAll();
  }

  private void expandAll() {
    DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel()
        .getRoot();

    for (Enumeration enumeration = root.preorderEnumeration(); enumeration
        .hasMoreElements();) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration
          .nextElement();
      if (node.getChildCount() != 0)
          tree.expandPath(new TreePath(node.getPath()));
    }
  }

  private void rebuildContents() {
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");

    this.addTopicNamesTreeNodeTo(root);
    this.addTypesTreeNodeTo(root);
    this.addSubjectIdentifiersTreeNodeTo(root);
    this.addSubjectTreeNodeTo(root);
    this.addOccurrencesTreeNodeTo(root);

    tree.setModel(new DefaultTreeModel(root, false));
  }

  private void addSubjectIdentifiersTreeNodeTo(DefaultMutableTreeNode parent) {
    Iterator subjInds = target.getSubjectIdentifiers().iterator();
    if (subjInds.hasNext()) {

      DefaultMutableTreeNode root = new DefaultMutableTreeNode(Messages
          .getString("Viz.PropertiesSubjectIndicators"));

      while (subjInds.hasNext()) {
        root.add(new DynamicUtilTreeNode(((LocatorIF) subjInds.next())
            .getAddress(), null));
      }
      parent.add(root);
    }
  }

  private void addSubjectTreeNodeTo(DefaultMutableTreeNode parent) {
    LocatorIF subject = (LocatorIF)CollectionUtils.getFirst(target.getSubjectLocators()); // NOTE: gets only the first one

    if (subject == null)
      return;

    String subjectAddress = subject.getAddress();

    if (subjectAddress != null) {
      DefaultMutableTreeNode root = new DefaultMutableTreeNode(Messages
          .getString("Viz.PropertiesSubject"));

      root.add(new DynamicUtilTreeNode(subjectAddress, null));
      parent.add(root);
    }
  }

  private void addOccurrencesTreeNodeTo(DefaultMutableTreeNode parent) {
    Iterator occurrences = target.getOccurrences().iterator();
    if (occurrences.hasNext()) {
      DefaultMutableTreeNode root = new DefaultMutableTreeNode(Messages
          .getString("Viz.PropertiesOccurrences"));
      HashMap occurrencesMap = new HashMap();

      while (occurrences.hasNext()) {
        OccurrenceIF occ = (OccurrenceIF) occurrences.next();
        TopicIF type = occ.getType();
        List list = (List) occurrencesMap.get(type);
        if (list == null) {
          list = new ArrayList();
          occurrencesMap.put(type, list);
        }
        list.add(occ);
      }

      for (Iterator occsByType = occurrencesMap.entrySet().iterator();
           occsByType.hasNext();) {
        Entry entry = (Map.Entry) occsByType.next();
        TopicIF type = (TopicIF) entry.getKey();
        DefaultMutableTreeNode sub = new DefaultMutableTreeNode(
            controller.getStringifier().apply(type));

        for (Iterator occs = ((List) entry.getValue()).iterator(); occs
            .hasNext();) {
          OccurrenceIF occ = (OccurrenceIF) occs.next();

          String result;
          if (occ.getLocator() == null) {
            result = occ.getValue();
            result = result.replace('\n', ' ');
          }
          else result = occ.getLocator().getAddress();

          sub.add(new DynamicUtilTreeNode(result, null));
        }
        root.add(sub);
      }
      parent.add(root);
    }

  }

  private void addTypesTreeNodeTo(DefaultMutableTreeNode parent) {
    Iterator<TopicIF> types = target.getTypes().iterator();
    if (types.hasNext()) {

      DefaultMutableTreeNode root = new DefaultMutableTreeNode(Messages
          .getString("Viz.PropertiesTypes"));

      while (types.hasNext()) {
        root.add(new DynamicUtilTreeNode(controller.getStringifier()
            .apply(types.next()), null));
      }
      parent.add(root);
    }
  }

  private void addTopicNamesTreeNodeTo(DefaultMutableTreeNode parent) {
    Iterator baseNames = target.getTopicNames().iterator();
    if (baseNames.hasNext()) {

      DefaultMutableTreeNode root = new DefaultMutableTreeNode(Messages
          .getString("Viz.PropertiesTopicNames"));

      while (baseNames.hasNext()) {
        TopicNameIF name = (TopicNameIF) baseNames.next();
        StringBuilder buff = new StringBuilder();
        buff.append(name.getValue());
        if (!name.getScope().isEmpty()) {
          buff.append(" - ");
          boolean first = true;
          for (Iterator<TopicIF> iter = name.getScope().iterator(); iter.hasNext();) {
            if (!first) buff.append(" : ");
            buff.append(controller.getStringifier().apply(iter.next()));
            first = false;
          }
        }
        root.add(new DynamicUtilTreeNode(buff.toString(), null));
      }
      parent.add(root);
    }
  }
}
