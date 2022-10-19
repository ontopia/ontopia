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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.index.ScopeIndexIF;
import net.ontopia.topicmaps.nav.utils.comparators.TopicComparator;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.utils.OntopiaRuntimeException;

public class AssociationScopeFilterMenu extends JMenu {
  private ButtonGroup radioButtons;
  private JRadioButtonMenuItem[] strictnessMap;
  private ArrayList scopes;
  private ArrayList checkBoxes;

  public AssociationScopeFilterMenu(String s) {
    super(s);
  }

  /**
   * Configure/reconfigure this menu.
   * @param currentTopicMap The topicmap holding the associations (and their
   *     scopes) to be used in this menu.
   * @param parentListener Allows the caller to act upon actions.
   * @param controller manages the interaction between the gui, model and 
   *     configuration manager
   */
  public void configure(TopicMapIF currentTopicMap, ActionListener parentListener,
      VizController controller) {
    if (currentTopicMap == null)
      return;

    ScopeIndexIF scopeix = (ScopeIndexIF) currentTopicMap.getIndex(
            "net.ontopia.topicmaps.core.index.ScopeIndexIF");
    
    // Remove all menu items from this menu.
    removeAll();

    // Add radio-button menu part for "Show All", "Loose Filter" 
    // and "Strict Filter"
    int SHOW_ALL = VizTopicMapConfigurationManager.SHOW_ALL_ASSOCIATION_SCOPES;
    int LOOSE = VizTopicMapConfigurationManager.LOOSE_ASSOCIATION_SCOPES;
    int STRICT = VizTopicMapConfigurationManager.STRICT_ASSOCIATION_SCOPES;
    
    // Create radio buttons for the different ways of filtering.
    
    // Determine which radio button is selected
    int selectedStrictness = controller.getAssociationScopeFilterStrictness();

    String showAll = Messages.getString("Viz.ShowAll");
    String looseFilter = Messages.getString("Viz.LooseFilter");
    String strictFilter = Messages.getString("Viz.StrictFilter");
    
    strictnessMap = new JRadioButtonMenuItem[4];

    // Create radio buttons
    JRadioButtonMenuItem radio1 = new JRadioButtonMenuItem(showAll,
        SHOW_ALL == selectedStrictness);
    JRadioButtonMenuItem radio2 = new JRadioButtonMenuItem(looseFilter,
        LOOSE == selectedStrictness);
    JRadioButtonMenuItem radio3 = new JRadioButtonMenuItem(strictFilter,
        STRICT == selectedStrictness);

    strictnessMap[SHOW_ALL] = radio1;
    strictnessMap[LOOSE] = radio2;
    strictnessMap[STRICT] = radio3;
    
    // Add radio buttons to this menu.
    add(radio1);
    add(radio2);
    add(radio3);

    // Add ActionListeners to the radiobuttons.
    radio1.addActionListener(new StrictnessActionListener(controller, SHOW_ALL,
        parentListener));
    radio2.addActionListener(new StrictnessActionListener(controller, LOOSE,
        parentListener));
    radio3.addActionListener(new StrictnessActionListener(controller, STRICT,
        parentListener));

    // Make a group of radiobuttons to make them all exclusive to each other.
    radioButtons = new ButtonGroup();
    radioButtons.add(radio1);
    radioButtons.add(radio2);
    radioButtons.add(radio3);

    // Add a separating line to this menu.
    addSeparator();
    
    // Create checkboxes for each association scope to determine whether it
    // should be used for filtering.
    
    // Get all topics that are used to scope associations and sort them.
    scopes = new ArrayList(scopeix.getAssociationThemes());
    Collections.sort(scopes, new TopicComparator());
    
    // For each association scoping topic...
    checkBoxes = new ArrayList(scopes.size());
    for (Iterator iter = scopes.iterator(); iter.hasNext();) {
      TopicIF scope = (TopicIF)iter.next();
      
      // Add checkbox for this scope with appropriate name to the menu.
      String name = TopicStringifiers.toString(scope);
      boolean checked = controller.isInAssociationScopeFilter(scope);
      JCheckBoxMenuItem mItem = new JCheckBoxMenuItem(name, checked);
      add(mItem);
      checkBoxes.add(mItem);
      
      // Set the checkbox to always be enabled.
      boolean enabled = true;
      mItem.addActionListener(new ScopeFilterActionListener(scope, controller, 
          parentListener));
      mItem.setEnabled(enabled);
    }
  }
  
  /**
   * Listens for changes to a checkbox controlling whether an association
   * scoping topic should be part of the association scope filter.
   */
  protected class ScopeFilterActionListener implements ActionListener {
    private TopicIF scope;
    private ActionListener parentListener;
    private VizController controller;

    /**
     * Create new.
     * @param scope The scoping topic controlled by this checkbox. 
     * @param parentListener The parent listener receives (and may act upon) any
     *     events that happen in this ActionListener.
     * @param controller Notified when the scope is added to/removed from the
     *     filter (i.e. any checkbox state change).
     */
    protected ScopeFilterActionListener(TopicIF scope, VizController controller,
        ActionListener parentListener) {
      this.parentListener = parentListener;
      this.scope = scope;
      this.controller = controller;
    }

    @Override
    public void actionPerformed(ActionEvent aE) {
      boolean setPreviously = controller.isInAssociationScopeFilter(scope);
      controller.setInAssociationScopeFilter(scope, !setPreviously);
      if (parentListener != null) 
        parentListener.actionPerformed(aE);
    }
  }
  
  public void setStrictnessSelection(int strictness) {
    radioButtons.setSelected(strictnessMap[strictness].getModel(), true);

    Enumeration elements = radioButtons.getElements();
    while(elements.hasMoreElements())
      ((AbstractButton)elements.nextElement()).repaint();
  }

  public void setInAssociationScopeFilter(TopicIF scope, boolean useInFilter) {
    Iterator scopesIt = scopes.iterator();
    Iterator checkBoxesIt = checkBoxes.iterator();
    
    TopicIF currentScope = null;
    JCheckBoxMenuItem checkBox = null;
    
    // Assume scopes and checkBoxes have the same size, which they should!
    while (scopesIt.hasNext()) {
      currentScope = (TopicIF)scopesIt.next();
      
      if (currentScope.equals(scope)) {
        checkBox = (JCheckBoxMenuItem)checkBoxesIt.next();
      } else
        checkBoxesIt.next();
    }
    
    if (checkBox == null)
      throw new OntopiaRuntimeException("Internal error. There should be a " +
          "scoping topic menu item for every scoping topic.");
    
    checkBox.setState(useInFilter);
  }
  
  /**
   * Listens for changes to the scrictness of the scope filter.
   */
  protected class StrictnessActionListener implements ActionListener {
    private VizController controller;
    private int strictnessLevel;
    private ActionListener parentListener;
    
    /**
     * 
     * @param controller Notified whenever the strictness level changes.
     * @param strictnessLevel The strictnes level attached to this menu item.
     * @param parentListener The parent listener receives (and may act upon) any
     *     events that happen in this ActionListener.
     */
    protected StrictnessActionListener(VizController controller,
        int strictnessLevel, ActionListener parentListener) {
      this.controller = controller;
      this.strictnessLevel = strictnessLevel;
      this.parentListener = parentListener;
    }
    
    @Override
    public void actionPerformed(ActionEvent aE) {
      controller.setAssociationScopeFilterStrictness(strictnessLevel);
      parentListener.actionPerformed(aE);
    }
  }
}
