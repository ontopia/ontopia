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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicArrowButton;

import net.ontopia.topicmaps.core.TopicIF;

/**
 * INTERNAL: A General Configuration frame for the VizDesktop
 */
public class GeneralConfigFrame extends JFrame {
  private VizController controller;
  private JColorChooser backgroundChooser;
  private JRadioButton[] singleClickOptions;
  private ButtonGroup singleClickGroup;
  private JRadioButton[] doubleClickOptions;
  private ButtonGroup doubleClickGroup;
  private Vector includedTopicTypes;
  private JList included;
  private Vector excludedTopicTypes;
  private JList excluded;

  private JRadioButton[] localityOptions;
  private ButtonGroup localityGroup;
  private OSpinner motionSpinner;

  public GeneralConfigFrame(VizController aController) {
    super(Messages.getString("Viz.GeneralConfigWindowTitle"));
    controller = aController;
    this.build();
  }

  private void build() {
    JTabbedPane tabbedPane = new JTabbedPane();

    backgroundChooser = new JColorChooser();

    // Setting #setPreviewPanel() to a new empty JPanel should
    // remove the preview panel from use.

    backgroundChooser.setPreviewPanel(new JPanel());
    backgroundChooser.getSelectionModel().addChangeListener(
      new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
          Color c = backgroundChooser.getColor();
          setPanelBackgroundColour(c);
        }
      }
    );

    tabbedPane
        .addTab(
            Messages.getString("Viz.GeneralConfigTitle"), null, this.createMainPanel(),
            Messages.getString("Viz.GeneralConfigHoverHelp"));
    tabbedPane
        .addTab(
            Messages.getString("Viz.BackgroundColourConfigTitle"), null, backgroundChooser, Messages.getString("Viz.BackgroundColourConfigHoverHelp"));
    tabbedPane
        .addTab(
            Messages.getString("Viz.TopicTypeExclusion"), null, this.createTypeExcludePanel(),
            Messages.getString("Viz.TopicTypeExclusionHoverHelp"));

    this.getContentPane().add(tabbedPane);
    this.pack();

    this.initializeValues();

    this.setResizable(false);
  }

  private JPanel createTypeExcludePanel() {
    JPanel border = new JPanel();

    border.setLayout(new GridBagLayout());
    border.setBorder(BorderFactory.createTitledBorder(BorderFactory
        .createEtchedBorder(), Messages.getString("Viz.TopicTypeExclusion")));

    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridheight = 1;
    c.weightx = 1;
    c.weighty = 1;

    included = new JList();
    included.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    included.addMouseListener(new MouseAdapter() {

      @Override
      public void mouseClicked(MouseEvent anEvent) {

        if (anEvent.getClickCount() == 2) {
          exclude((TopicListItem) included.getSelectedValue());
        }
      }
    });

    excluded = new JList();
    excluded.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    excluded.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent anEvent) {
        if (anEvent.getClickCount() == 2) {
          include((TopicListItem) excluded.getSelectedValue());
        }
      }
    });

    JScrollPane left = new JScrollPane(included);
    left.setBorder(BorderFactory.createTitledBorder(BorderFactory
        .createEtchedBorder(), Messages.getString("Viz.IncludedTopicTypes")));

    c.gridx = 0;
    border.add(left, c);

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
    buttonPanel.add(Box.createVerticalStrut(8));

    BasicArrowButton includeButton = new BasicArrowButton(BasicArrowButton.EAST);
    includeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent action) {
        exclude((TopicListItem) included.getSelectedValue());
      }
    });
    buttonPanel.add(includeButton);
    BasicArrowButton excludeButton = new BasicArrowButton(BasicArrowButton.WEST);
    excludeButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent action) {
        include((TopicListItem) excluded.getSelectedValue());
      }
    });
    buttonPanel.add(excludeButton);

    c.gridx = 1;
    border.add(buttonPanel, c);

    JScrollPane right = new JScrollPane(excluded);
    right.setBorder(BorderFactory.createTitledBorder(BorderFactory
        .createEtchedBorder(), Messages.getString("Viz.ExcludedTopicTypes")));

    c.gridx = 2;
    border.add(right, c);

    return border;
  }

  /**
   * Initialize the GUI values
   */
  private void initializeValues() {
    singleClickGroup.setSelected(singleClickOptions[controller
        .getConfigurationManager().getGeneralSingleClick()].getModel(), true);
    doubleClickGroup.setSelected(doubleClickOptions[controller
        .getConfigurationManager().getGeneralDoubleClick()].getModel(), true);
    backgroundChooser.setColor(controller.getConfigurationManager()
        .getPanelBackgroundColour());
    localityGroup.setSelected(localityOptions[controller
                              .getConfigurationManager().getGeneralLocalityAlgorithm()].getModel(), true);
    initializeTopicLists();
  }

  protected void initializeTopicLists() {
    Collection topics = controller.getAllTopicTypes();
    this.excludedTopicTypes = new Vector(topics.size());
    this.includedTopicTypes = new Vector(topics.size());
    Iterator iterator = topics.iterator();
    while (iterator.hasNext()) {
      TopicIF type = (TopicIF) iterator.next();
      if (controller.getConfigurationManager().isTypeExcluded(type)) {
        this.excludedTopicTypes
                .add(new TopicListItem(type, controller.getStringifier()));
      } else {
        this.includedTopicTypes.add(new TopicListItem(type, controller.getStringifier()));
      }
    }
    this.setListDate(included, includedTopicTypes);
    this.setListDate(excluded, excludedTopicTypes);
  }

  protected void setPanelBackgroundColour(Color aColor) {
    controller.setPanelBackgroundColour(aColor);
  }

  private JPanel createMainPanel() {
    JPanel main = new JPanel(new GridLayout(0, 1));

    main.add(createMouseButtonConfigurationPanel());
    main.add(createHoverHelpPanel());
    main.add(createLocalityPanel());
    if (VizDebugUtils.ENABLE_MOTION_CONFIGURATION) {
      main.add(createMotionPanel());
    }
    main.add(createNameLengthPanel());
    return main;
  }

  private Component createMouseButtonConfigurationPanel() {
    // Single Click Options
    singleClickOptions = new JRadioButton[2];

    JPanel singleClickPanel = new JPanel(new GridLayout(0, 1));
    singleClickPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory
        .createEtchedBorder(), Messages.getString("Viz.SingleClick")));

    JRadioButton expandNode = this.createSingleClickRadioButton(Messages
        .getString("Viz.ExpandNode"), VizTopicMapConfigurationManager.EXPAND_NODE);
    singleClickPanel.add(expandNode);
    singleClickOptions[VizTopicMapConfigurationManager.EXPAND_NODE] = expandNode;

    JRadioButton setFocusNode = this.createSingleClickRadioButton(Messages
        .getString("Viz.SetFocusNode"), VizTopicMapConfigurationManager.SET_FOCUS_NODE);
    singleClickPanel.add(setFocusNode);
    singleClickOptions[VizTopicMapConfigurationManager.SET_FOCUS_NODE] = setFocusNode;

    //Group the radio buttons.
    singleClickGroup = new ButtonGroup();
    singleClickGroup.add(expandNode);
    singleClickGroup.add(setFocusNode);

    // Double click options
    doubleClickOptions = new JRadioButton[3];

    JPanel doubleClickPanel = new JPanel(new GridLayout(0, 1));
    doubleClickPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory
        .createEtchedBorder(), Messages.getString("Viz.DoubleClick")));

    expandNode = this.createDoubleClickRadioButton(Messages
        .getString("Viz.ExpandNode"), VizTopicMapConfigurationManager.EXPAND_NODE);
    doubleClickPanel.add(expandNode);
    doubleClickOptions[VizTopicMapConfigurationManager.EXPAND_NODE] = expandNode;

    setFocusNode = this.createDoubleClickRadioButton(Messages
        .getString("Viz.SetFocusNode"), VizTopicMapConfigurationManager.SET_FOCUS_NODE);
    doubleClickPanel.add(setFocusNode);
    doubleClickOptions[VizTopicMapConfigurationManager.SET_FOCUS_NODE] = setFocusNode;

    JRadioButton goToTopic = this.createDoubleClickRadioButton(Messages
        .getString("Viz.GoToTopic"), VizTopicMapConfigurationManager.GO_TO_TOPIC);
    doubleClickPanel.add(goToTopic);
    doubleClickOptions[VizTopicMapConfigurationManager.GO_TO_TOPIC] = goToTopic;

    //Group the radio buttons.
    doubleClickGroup = new ButtonGroup();
    doubleClickGroup.add(expandNode);
    doubleClickGroup.add(setFocusNode);
    doubleClickGroup.add(goToTopic);

    JPanel border = new JPanel();
    border.setLayout(new BoxLayout(border, BoxLayout.X_AXIS));
    border.setBorder(BorderFactory.createTitledBorder(BorderFactory
        .createEtchedBorder(), Messages.getString("Viz.MouseButtons")));

    border.add(singleClickPanel);
    border.add(doubleClickPanel);

    return border;
  }

  /**
   * Allows the user to select how the locality is computed.
   */
  private Component createLocalityPanel() {
    // Locality Options
    localityOptions = new JRadioButton[2];

    JPanel localityPanel = new JPanel(new GridLayout(0, 1));
    localityPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory
        .createEtchedBorder(), Messages.getString("Viz.LocalityAlgorithm")));

    JRadioButton nodeOriented = createLocalityRadioButton(Messages
        .getString("Viz.NodeOriented"), VizTopicMapConfigurationManager.NODE_ORIENTED);
    localityPanel.add(nodeOriented);
    localityOptions[VizTopicMapConfigurationManager.NODE_ORIENTED] = nodeOriented;

    JRadioButton edgeOriented = createLocalityRadioButton(Messages
        .getString("Viz.EdgeOriented"), VizTopicMapConfigurationManager.EDGE_ORIENTED);
    localityPanel.add(edgeOriented);
    localityOptions[VizTopicMapConfigurationManager.EDGE_ORIENTED] = edgeOriented;

    //Group the radio buttons.
    localityGroup = new ButtonGroup();
    localityGroup.add(nodeOriented);
    localityGroup.add(edgeOriented);

    return localityPanel;
  }
  
  /**
   * Allows the user to select how the locality is computed.
   */
  private Component createMotionPanel() {
    // Create checkbox that enables/disables motion killer (and the spinner).
    JCheckBox enableMotionKiller = new JCheckBox(Messages
        .getString("Viz.EnableMotionKiller"), controller
        .getConfigurationManager().isMotionKillerEnabled());
    enableMotionKiller.addActionListener(new ActionListener() {
      private boolean currentValue = controller.getConfigurationManager()
          .isMotionKillerEnabled();

      @Override
      public void actionPerformed(ActionEvent e) {
        currentValue = !currentValue;
        controller.setMotionKillerEnabled(currentValue);
        motionSpinner.setEnabled(currentValue);
        controller.getVizPanel().updateEnableMotionKillerMenuItem();
        controller.getVizPanel()
            .enableDisableMotionKillerMenuItem(currentValue);
      }
    });
    
    // Create spinner that can change the motion killer delay (if enabled).
    motionSpinner = new OSpinner();
    motionSpinner.setEnabled(controller.getConfigurationManager()
        .isMotionKillerEnabled());
    motionSpinner.setPreferredSize(new Dimension(40, 20));
    motionSpinner.setValue(controller.getConfigurationManager()
        .getGeneralMotionKillerDelay());

    motionSpinner.addPropertyChangeListener("value", new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        setMotionKillerDelay(((Integer) evt.getNewValue()).intValue());
      }
    });

    // Create panel containing motion killer controls.
    JPanel border = new JPanel();
    border.setLayout(new FlowLayout(FlowLayout.LEFT));
    border.setBorder(BorderFactory.createTitledBorder(BorderFactory
        .createEtchedBorder(), Messages.getString("Viz.MotionProperties")));
    border.add(enableMotionKiller);
    border.add(motionSpinner);

    return border;
  }
  
  /**
   * Allows the user to specify the maximum name length on nodes.
   */
  private Component createNameLengthPanel() {
    // Create checkbox that enables/disables motion killer (and the spinner).
    JLabel text = new JLabel(Messages.getString("Viz.MaxTopicNameLength"));
    
    // Create spinner that can change the motion killer delay (if enabled).
    motionSpinner = new OSpinner();
    motionSpinner.setPreferredSize(new Dimension(40, 20));
    motionSpinner.setMax(50);
    motionSpinner.setValue(controller.getConfigurationManager()
        .getMaxTopicNameLength());

    motionSpinner.addPropertyChangeListener("value",
        new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        setMaxTopicNameLength(((Integer) evt.getNewValue()).intValue());
      }
    });

    // Create panel containing name length controls.
    JPanel border = new JPanel();
    border.setLayout(new FlowLayout(FlowLayout.LEFT));
    border.setBorder(BorderFactory.createTitledBorder(BorderFactory
        .createEtchedBorder(), Messages.getString("Viz.NameProperties")));
    border.add(text);
    border.add(motionSpinner);

    return border;
  }
  
  private JRadioButton createSingleClickRadioButton(String title,
      final int action) {
    JRadioButton node = new JRadioButton(title);
    node.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setSingleClick(action);
      }
    });

    return node;
  }

  private JRadioButton createDoubleClickRadioButton(String title,
      final int action) {
    JRadioButton node = new JRadioButton(title);
    node.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setDoubleClick(action);
      }
    });

    return node;
  }

  private JRadioButton createLocalityRadioButton(String title,
      final int action) {
    JRadioButton node = new JRadioButton(title);
    node.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setLocalityAlgorithm(action);
      }
    });

    return node;
  }

  protected void setDoubleClick(int anAction) {
    controller.setGeneralDoubleClick(anAction);
  }

  protected void setSingleClick(int action) {
    controller.setGeneralSingleClick(action);
  }

  protected void setLocalityAlgorithm(int action) {
    controller.setGeneralLocalityAlgorithm(action);
  }

  public void setMotionKillerDelay(int seconds) {
    controller.setMotionKillerDelay(seconds);
  }

  public void setMaxTopicNameLength(int length) {
    controller.setMaxTopicNameLength(length);
  }

  private JPanel createHoverHelpPanel() {
    JPanel border = new JPanel();
    border.setLayout(new BoxLayout(border, BoxLayout.Y_AXIS));
    border.setBorder(BorderFactory.createTitledBorder(BorderFactory
        .createEtchedBorder(), Messages.getString("Viz.HoverHelpTitle")));

    JCheckBox displayRolePopup = new JCheckBox(Messages
        .getString("Viz.DisplayRoleHoverHelp"), controller
        .getConfigurationManager().shouldDisplayRoleHoverHelp());
    displayRolePopup.addActionListener(new ActionListener() {

      private boolean currentValue = controller.getConfigurationManager()
          .shouldDisplayRoleHoverHelp();

      @Override
      public void actionPerformed(ActionEvent e) {

        currentValue = !currentValue;
        controller.shouldDisplayRoleHoverHelp(currentValue);

      }
    });

    border.add(displayRolePopup);

    JCheckBox displayAssocScopedNames = new JCheckBox(Messages
        .getString("Viz.DisplayAssocScopedNames"), controller
        .getConfigurationManager().shouldDisplayScopedAssociationNames());

    displayAssocScopedNames.addActionListener(new ActionListener() {

      private boolean currentValue = controller.getConfigurationManager()
          .shouldDisplayScopedAssociationNames();

      @Override
      public void actionPerformed(ActionEvent e) {

        currentValue = !currentValue;
        controller.shouldDisplayScopedAssociationNames(currentValue);

      }
    });

    border.add(displayAssocScopedNames);

    return border;
  }

  private void include(TopicListItem selected) {
    if (selected != null) {
      excludedTopicTypes.remove(selected);
      setListDate(excluded, excludedTopicTypes);
      includedTopicTypes.add(selected);
      setListDate(included, includedTopicTypes);
      controller.setTypeIncluded(selected.getTopic());
    }
  }

  private void setListDate(JList list, Vector vector) {
    sortCollection(vector);
    list.setListData(vector);
  }

  private void exclude(TopicListItem selected) {
    if (selected != null) {
      includedTopicTypes.remove(selected);
      setListDate(included, includedTopicTypes);
      excludedTopicTypes.add(selected);
      setListDate(excluded, excludedTopicTypes);
      controller.setTypeExcluded(selected.getTopic());
    }
  }

  private void sortCollection(List list) {
    TopicListItem.sort(list);
  }
}
