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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.plaf.basic.BasicArrowButton;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.nav.utils.comparators.TopicComparator;

/**
 * INTERNAL: A General Configuration frame for the VizDesktop
 */
public class TypesPrecedenceFrame extends JFrame {
  private VizController controller;
  private Vector topicTypes;
  private JList topicTypesList;

  public TypesPrecedenceFrame(VizController aController) {
    super(Messages.getString("Viz.TopicTypePrecedenceWindowTitle"));
    controller = aController;
    this.build();
  }

  private void build() {
    this.getContentPane().add(this.createTypeExcludePanel());
    this.pack();
    this.initializeValues();
  }

  private JPanel createTypeExcludePanel() {
    JPanel border = new JPanel();

    border.setLayout(new GridBagLayout());

    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridheight = 1;
    c.weightx = 1;
    c.weighty = 1;

    topicTypesList = new JList();
    topicTypesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JScrollPane left = new JScrollPane(topicTypesList);

    c.gridx = 0;
    border.add(left, c);

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
    buttonPanel.add(Box.createVerticalStrut(8));

    BasicArrowButton rankUpButton = new BasicArrowButton(BasicArrowButton.NORTH);
    rankUpButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent action) {
        rank((TopicListItem) topicTypesList.getSelectedValue(), true);
      }
    });
    buttonPanel.add(rankUpButton);

    BasicArrowButton rankDownButton = new BasicArrowButton(BasicArrowButton.SOUTH);
    rankDownButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent action) {
        rank((TopicListItem) topicTypesList.getSelectedValue(), false);
      }
    });
    buttonPanel.add(rankDownButton);

    c.gridx = 1;
    border.add(buttonPanel, c);

    return border;
  }

  private void initializeValues() {
    initializeTopicLists();
  }

  protected void initializeTopicLists() {
    VizTopicMapConfigurationManager confMan = controller
        .getConfigurationManager();

    List allTopicTypes = new ArrayList(controller.getAllTopicTypes());
    Collections.sort(allTopicTypes, new TopicComparator());
    confMan.getTTPriorityManager().augmentTopicTypeRank(allTopicTypes);

    VizTopicTypePriorityConfigManager priorityManager 
        = confMan.getTTPriorityManager();
    TopicIF defaultPrecedence = priorityManager.getDefaultTypePrecedenceTopic(); 
    
    priorityManager.augmentTopicTypeRank(allTopicTypes);
    Collection topics = confMan.getTTPriorityManager()
        .getRankedTopicTypes(allTopicTypes);

    topicTypes = new Vector(topics.size());
    Iterator iterator = topics.iterator();
    while (iterator.hasNext()) {
      TopicIF type = (TopicIF) iterator.next();
      
      if (type.equals(defaultPrecedence)) {
        topicTypes.add(new TopicListItem(type,
            Messages.getString("Viz.UnknownTypes")));
      } else { 
        topicTypes.add(new TopicListItem(type,
            controller.getStringifier()));
      }
    }
    setListData(topicTypesList, topicTypes);
  }

  protected void setPanelBackgroundColour(Color aColor) {
    controller.setPanelBackgroundColour(aColor);
  }

  protected void setDoubleClick(int anAction) {
    controller.setGeneralDoubleClick(anAction);
  }

  protected void setSingleClick(int action) {
    controller.setGeneralSingleClick(action);
  }

  private void setListData(JList list, Vector vector) {
    list.setListData(vector);
  }

  /**
   * Ranks a given topic list item either up or down depending on 'up'.
   * Consequently, the topic below or above will also change rank (opposite way)
   * @param selected The topic list item to rank up or down.
   * @param up Whether to move it up or down.
   */
  private void rank(TopicListItem selected, boolean up) {
    if (selected != null) {
      // If there's no further ranked element, do nothing.
      if (up && topicTypes.firstElement() == selected) {
        return;
      }
      if (!up && topicTypes.lastElement() == selected) {
        return;
      }
      
      // Determine the new index of the element.
      int newIndex = topicTypes.indexOf(selected) + (up ? -1 : 1);
      
      // Get the topics that will get swapped from the the position 'selected'
      // will get moved to.
      TopicIF swapTopic = ((TopicListItem)topicTypes.get(newIndex)).getTopic();
      
      // Move the element one place along the list of elements.       
      topicTypes.remove(selected);
      topicTypes.add(newIndex, selected);
      setListData(topicTypesList, topicTypes);
      
      // Set the moved element to be the selected cell.
      topicTypesList.setSelectedIndex(newIndex);

      controller.getConfigurationManager().getTTPriorityManager()
          .changeRank(selected.getTopic(), up);
      
      // Update the highest ranked topic (of the two) in the view.
      if (up) {
        controller.updateViewType(selected.getTopic());
      } else {
        controller.updateViewType(swapTopic);
      }
    }
  }
}
