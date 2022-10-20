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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import net.ontopia.topicmaps.core.TopicMapIF;

import com.touchgraph.graphlayout.TGLensSet;
import com.touchgraph.graphlayout.TGPanel;
import com.touchgraph.graphlayout.TGPoint2D;
import com.touchgraph.graphlayout.interaction.HVScroll;
import com.touchgraph.graphlayout.interaction.TGUIManager;
import com.touchgraph.graphlayout.interaction.ZoomScroll;

/**
 * EXPERIMENTAL: A panel in which topic map visualization can be shown.
 */
public class VizPanel extends JPanel {
  private static final String ITEM_ID_SHOW_SEARCH_BAR = "show.search.bar";
  private static final String ITEM_ID_TOPIC_STYLES = "topic.styles";
  private static final String ITEM_ID_ASSOCIATION_STYLES = "association.styles";
  private static final String ITEM_ID_TOGGLE_CONTROLS = "toggle.controls";

  private static final String ITEM_ID_SET_ALL_FIXED = "set.all.nodes.sticky";
  private static final String ITEM_ID_SET_ALL_UNFIXED = 
    "set.all.nodes.unsticky";
  private static final String ITEM_ID_STOP_MOVING_NODES = "stop.moving.nodes";
  private static final String ITEM_ID_ENABLE_MOTION_KILLER = 
    "enable.motion.killer";
  private static final String ITEM_ID_ENABLE_ANIMATION = "enable.animation";
  private static final String ITEM_ID_ASSOCIATION_SCOPING =
      "association.scoping";
  private static final String ITEM_FOCUS_NEXT = "redo";
  private static final String ITEM_FOCUS_PREVIOUS = "undo";
  private static final String ITEM_ID_ENABLE_NEIGH_CIRC = 
    "enable.neighbour.circle";

  public JPopupMenu glPopup;

  private HVScroll hvScroll;
  private ZoomScroll zoomScroll;
  private TGPanel tgPanel;
  private TGLensSet tgLensSet;
  private TGUIManager tgUIManager;
  private VizController controller;
  private OSpinner locSpinner;

  private JTextField searchTextField;
  private BlinkingThread blinkThread;
  private JButton clearButton;
  private JPanel searchPanel;
  private boolean searchPanelVisible = true;
  private boolean controlsVisible = true;
  private JCheckBoxMenuItem searchMenuItem;
  private JMenuItem topicStylesMenuItem;
  private JMenuItem associationStylesMenuItem;
  private JPanel topPanel;

  // Used for filtering out associations whose scope doesn't match a filter.
  private AssociationScopeFilterMenu associationScopeFilterMenu;

  private ParsedMenuFile enabledItemIds;
  
  private JMenuItem enableMotionKillerMenuItem;
  private String startMotionKiller;
  private String stopMotionKiller;
  private JMenuItem redoMenuItem;
  private JMenuItem undoMenuItem;
  
  private TypesConfigFrame topicFrame;
  private TypesConfigFrame assocFrame;
  
  private VizFrontEndIF vizFrontEnd;

  public VizPanel(VizFrontEndIF vizFrontEnd) throws IOException  {
    this.vizFrontEnd = vizFrontEnd;
    this.controlsVisible = vizFrontEnd.getDefaultControlsVisible();
    String wallpapSrc = vizFrontEnd.getWallpaper();
    if(wallpapSrc != null) {
      tgPanel = new ExtendedTGPanel(wallpapSrc);
    } else {
      tgPanel = new TGPanel();
    }
    init();
    controller = new VizController(this, this.vizFrontEnd, tgPanel);
    enabledItemIds = controller.getEnabledItemIds();
    buildPanel();
    if (vizFrontEnd.mapPreLoaded()) {
      setLocality(controller.getDefaultLocality());
      locSpinner.setMax(controller.getMaxLocality());
    }
    addUIs();
    if (vizFrontEnd.mapPreLoaded()) {
      controller.initializeMotionKillerEnabled();
      controller.undoManager.reset();
    }
  }

  public void init() {
    tgLensSet = new TGLensSet();
    hvScroll = new HVScroll(tgPanel, tgLensSet);
    zoomScroll = new ZoomScroll(tgPanel);
    tgPanel.setBackColor(VizTopicMapConfigurationManager
        .DEFAULT_PANEL_BACKGROUND_COLOUR);
    buildLens();
    tgPanel.setLensSet(tgLensSet);
    setVisible(true);
  }

  /**
   * Returns the controller for this panel.
   */
  public VizController getController() {
    return controller;
  }

  /**
   * Return the TGPanel used with this GLPanel.
   */
  public TGPanel getTGPanel() {
    return tgPanel;
  }

  // navigation .................

  /**
   * Return the HVScroll used with this GLPanel.
   */
  public HVScroll getHVScroll() {
    return hvScroll;
  }

  /**
   * Sets the horizontal offset to p.x, and the vertical offset to p.y
   * given a Point <tt>p<tt>.
   */
  public void setOffset(Point p) {
    hvScroll.setOffset(p);
  }

  /**
   * Return the horizontal and vertical offset position as a Point.
   */
  public Point getOffset() {
    return hvScroll.getOffset();
  }

  // zoom .......................

  /**
   * Return the ZoomScroll used with this GLPanel.
   */
  public ZoomScroll getZoomScroll() {
    return zoomScroll;
  }

  /**
   * Set the zoom value of this GLPanel (allowable values between -100 to 100).
   */
  public void setZoomValue(int zoomValue) {
    zoomScroll.setZoomValue(zoomValue);
  }

  /** Return the zoom value of this GLPanel. */
  public int getZoomValue() {
    return zoomScroll.getZoomValue();
  }

  public void buildLens() {
    tgLensSet.addLens(hvScroll.getLens());
    tgLensSet.addLens(zoomScroll.getLens());
    tgLensSet.addLens(tgPanel.getAdjustOriginLens());
  }

  /**
   * Construct display panel, with H/V scroll bars, locality stepper
   * and zoom scrollbar.
   */
  public void buildPanel() {
    final JScrollBar horizontalSB = hvScroll.getHorizontalSB();
    final JScrollBar verticalSB = hvScroll.getVerticalSB();
    final JScrollBar zoomSB = zoomScroll.getZoomSB();

    zoomSB.setToolTipText(Messages.getString("Viz.ZoomBarHoverHelp"));
    //        set the initial zoom value to 2/3 of the total value
    zoomSB.setValue(zoomSB.getMinimum()
        + (((zoomSB.getMaximum() - zoomSB.getMinimum()) / 3) * 2));

    setLayout(new BorderLayout());

    JPanel scrollPanel = new JPanel();
    scrollPanel.setLayout(new GridBagLayout());

    GridBagConstraints c = new GridBagConstraints();

    JPanel modeSelectPanel = new JPanel();
    modeSelectPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

    JPanel locPanel = new JPanel();
    locPanel.setLayout(new GridBagLayout());
    c.gridy = 0;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.weightx = 0;
    c.insets = new Insets(0, 10, 0, 10);
    locPanel.add(modeSelectPanel, c);
    c.insets = new Insets(0, 0, 0, 0);
    c.gridx = 1;
    c.weightx = 1;

    JPanel spinnerPanel = buildSpinnerPanel();
    locPanel.add(spinnerPanel, c);

    if (shouldShowSearchPanel()) {
      this.buildSearchPanel();
    }

    topPanel = new JPanel(new BorderLayout());
    
    if (shouldShowSearchPanel()) {
      topPanel.add(searchPanel, BorderLayout.NORTH);
    }

    topPanel.add(locPanel, BorderLayout.SOUTH);

    add(topPanel, BorderLayout.NORTH);

    c.fill = GridBagConstraints.BOTH;
    c.gridwidth = 1;
    c.gridx = 0;
    c.gridy = 1;
    c.weightx = 1;
    c.weighty = 1;
    scrollPanel.add(tgPanel, c);

    c.gridx = 1;
    c.gridy = 1;
    c.weightx = 0;
    c.weighty = 0;
    scrollPanel.add(verticalSB, c);

    c.gridx = 0;
    c.gridy = 2;
    scrollPanel.add(horizontalSB, c);

    add(scrollPanel, BorderLayout.CENTER);

    glPopup = new JPopupMenu();

    JMenuItem menuItem = new JMenuItem(Messages
        .getString("Viz.PopupToggleControls"));
    menuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        controlsVisible = !controlsVisible;
        horizontalSB.setVisible(controlsVisible);
        verticalSB.setVisible(controlsVisible);
        topPanel.setVisible(controlsVisible);
      }
    });
    add(glPopup, menuItem, ITEM_ID_TOGGLE_CONTROLS);
    
    // Create association scope filter menu
    createAssociationScopeFilterMenu();

    // Create topic styles menu item
    createTopicStylesMenuItem();

    // Create topic styles menu item
    createAssociationStylesMenuItem();

    // Create search menu items.
    createSearchMenuItems();
   
    // All-sticky implementation
    createStickyMenuItems();
    
    // Create menu item that stops moving nodes.
    createStopMovingNodesMenuItem();
 
    // Create menu item for turning motion reduction on/off.
    createMotionReductionMenuItem();
    
    // Create menu items for undoing and redoing actions.
    if (UndoManager.ENABLE_UNDO_MANAGER) {
      createUndoRedoMenuItems();
    }    
    
    // If enabled, create neighbouring circle menu items.
    if (VizDebugUtils.isNeighbouringCircleEnabled()) {
      createDisEnableNeighCircMenuItem();
    }    
    
    // Create menu item for turing animation on/off.
    if (VizDebugUtils.isAnimatorEnabled()) {
      addAnimationMenuItem();
    }

    horizontalSB.setVisible(controlsVisible);
    verticalSB.setVisible(controlsVisible);
    topPanel.setVisible(controlsVisible);
  }
  
  /**
   * Updates the text of the motion killer menu item.
   */
  public void updateEnableMotionKillerMenuItem() {
    boolean enable = controller.isMotionKillerEnabled();
    if (enableMotionKillerMenuItem != null) {
      enableMotionKillerMenuItem.setText(enable ? stopMotionKiller 
                                                : startMotionKiller);
    }
  }
  
  /**
   * Enables/disables the motion killer menu item.
   * @param enabled true iff the motion killer menu item should be enabled.
   */
  public void enableDisableMotionKillerMenuItem(boolean enabled) {
    enableMotionKillerMenuItem.setEnabled(enabled);
  }
  
  protected void addAnimationMenuItem() {
    // FIXME: IF ever releasing this, remember to use message files.
    final String startAnimation = "[Start Animation]";
    final String stopAnimation = "[Stop Animation]";
    final JMenuItem enableAnimationMenuItem = 
        new JMenuItem(VizigatorUser.INITIALLY_ENABLED
            ? stopAnimation : startAnimation);
    enableAnimationMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        boolean enable = !controller.isAnimationEnabled();
        controller.enableAnimation(enable);
        enableAnimationMenuItem.setText(enable ? stopAnimation : 
                                                 startAnimation);
      }
    });
    add(glPopup, enableAnimationMenuItem, ITEM_ID_ENABLE_ANIMATION);
  }

  /**
   * Creates four menu items to make/force all nodes (un)sticky.
   */
  protected void createStickyMenuItems() {
    JMenuItem setAllFixedMenuItem = new JMenuItem(
        Messages.getString("Viz.SetAllNodesSticky"));
    setAllFixedMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        controller.setAllNodesFixed(true);
      }
    });
    add(glPopup, setAllFixedMenuItem, ITEM_ID_SET_ALL_FIXED);

    JMenuItem setAllUnfixedMenuItem = new JMenuItem(
        Messages.getString("Viz.SetAllNodesUnsticky"));
    setAllUnfixedMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        controller.setAllNodesFixed(false);
      }
    });
    add(glPopup, setAllUnfixedMenuItem, ITEM_ID_SET_ALL_UNFIXED);
  }
  
  /**
   * Creates search menu items.
   */
  protected void createSearchMenuItems() {
    searchMenuItem = new JCheckBoxMenuItem(Messages
        .getString("Viz.ShowSearchBar"),
        searchPanelVisible);
    searchMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent action) {
        switchSearchPanel();
        searchMenuItem.setSelected(searchPanelVisible);
      }
    });
    add(glPopup, searchMenuItem, ITEM_ID_SHOW_SEARCH_BAR);
  }
  
  /**
   * Creates topic styles menu items.
   */
  protected void createTopicStylesMenuItem() {
    topicStylesMenuItem = new JMenuItem(Messages
        .getString("Viz.TopicTypeConfiguration"));
    topicStylesMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent action) {
        menuOpenTopicConfig();
      }
    });
    add(glPopup, topicStylesMenuItem, ITEM_ID_TOPIC_STYLES);
  }
  
  /**
   * Creates association styles menu items.
   */
  protected void createAssociationStylesMenuItem() {
    associationStylesMenuItem = new JMenuItem(Messages
        .getString("Viz.AssociationTypeConfiguration"));
    associationStylesMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent action) {
        menuOpenAssociationConfig();
      }
    });
    add(glPopup, associationStylesMenuItem, ITEM_ID_ASSOCIATION_STYLES);
  }
  
  /**
   * Creates association scope filter menu.
   */
  protected void createAssociationScopeFilterMenu() {
    associationScopeFilterMenu = new AssociationScopeFilterMenu(Messages
        .getString("Viz.AssociationScopingMenuTitle"));
    add(glPopup, associationScopeFilterMenu, ITEM_ID_ASSOCIATION_SCOPING);
  }
  
  /**
   * Creates four menu items to make/force all nodes (un)sticky.
   */
  protected void createStopMovingNodesMenuItem() {
    JMenuItem stopMovingNodesMenuItem = new JMenuItem(
        Messages.getString("Viz.StopMovingNodes"));
    stopMovingNodesMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        controller.stopMovingNodes();
      }
    });
    add(glPopup, stopMovingNodesMenuItem, ITEM_ID_STOP_MOVING_NODES);
  }
  
  public void setUndoEnabled(boolean enabled) {
     if (undoMenuItem != null) {
       undoMenuItem.setEnabled(enabled);
     }
  }
  
  public void setRedoEnabled(boolean enabled) {
    if (undoMenuItem != null) {
      redoMenuItem.setEnabled(enabled);
    }
  }
 
  /**
   * Creates menu items for retrieving the previous/next focus node.
   */
  protected void createUndoRedoMenuItems() {
    undoMenuItem = new JMenuItem(Messages
        .getString("Viz.Undo"));
    undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
        KeyInputManager.KEY_MASK));
    undoMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        controller.undo();
      }
    });
    add(glPopup, undoMenuItem, ITEM_FOCUS_PREVIOUS);

    redoMenuItem = new JMenuItem(Messages
        .getString("Viz.Redo"));
    redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y,
        KeyInputManager.KEY_MASK));
    redoMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        controller.redo();
      }
    });
    add(glPopup, redoMenuItem, ITEM_FOCUS_NEXT);
  }
  
  protected void createMotionReductionMenuItem() {
    startMotionKiller = Messages
        .getString("Viz.StartMotionKiller");
    stopMotionKiller = Messages.getString("Viz.StopMotionKiller");
    enableMotionKillerMenuItem = 
        new JMenuItem(startMotionKiller);
    enableMotionKillerMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        boolean enable = !controller.isMotionKillerEnabled();
        controller.enableMotionKiller(enable);
        updateEnableMotionKillerMenuItem();
      }
    });
    add(glPopup, enableMotionKillerMenuItem, ITEM_ID_ENABLE_MOTION_KILLER);
  }
  
  /**
   * Creates a menu item for hiding showing the neighbouring circle.
   */
  protected void createDisEnableNeighCircMenuItem() {
    final String enableNeighbCirc = Messages
        .getString("Viz.EnableNeighbouringCircle");
    final String disableNeighbCirc = Messages
        .getString("Viz.DisableNeighbouringCircle");

    final JMenuItem disEnableNeighCircMenuItem = 
        new JMenuItem(controller.showNeighbouringCircle ?
            disableNeighbCirc : enableNeighbCirc);
    disEnableNeighCircMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (disEnableNeighCircMenuItem.getText().equals(enableNeighbCirc)) {
          disEnableNeighCircMenuItem.setText(disableNeighbCirc);
          controller.showNeighbouringCircle = true;
          controller.showNeighboursOnMouseover = true;
        } else {
          disEnableNeighCircMenuItem.setText(enableNeighbCirc);
          controller.showNeighbouringCircle = false;
          controller.showNeighboursOnMouseover = false;
        }
      }
    });
    add(glPopup, disEnableNeighCircMenuItem, ITEM_ID_ENABLE_NEIGH_CIRC);
  }
  
  protected void switchSearchPanel() {
    if (searchPanelVisible) {
      topPanel.remove(searchPanel);
    } else {
      topPanel.add(searchPanel, BorderLayout.NORTH);
    }

    searchPanelVisible = !searchPanelVisible;
    this.revalidate();
    this.setSearchFocus();
  }

  private boolean shouldShowSearchPanel() {
    return enabled("search.bar");
  }
  
  private JPanel buildSearchPanel() {
    searchPanel = new JPanel();
    searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));

    searchPanel.add(Box.createHorizontalStrut(10));
    searchPanel
        .add(new JLabel(Messages.getString("Viz.EnterSearchExpression")));
    searchPanel.add(Box.createHorizontalStrut(10));
    searchTextField = new JTextField();
    searchTextField.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        performSearch();
      }
    });
    searchPanel.add(searchTextField);

    JButton searchButton = new JButton(Messages
        .getString("Viz.SearchButtonText"));
    searchButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        performSearch();
      }
    });

    searchPanel.add(searchButton);

    clearButton = new JButton(Messages.getString("Viz.ClearButtonText"));
    clearButton.setEnabled(false);
    clearButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        clearSearch();
      }
    });

    searchPanel.add(clearButton);

    return searchPanel;
  }

  /**
   * Clears the search results, the search string and sets the input
   * focus in the search field.
   */
  public void clearSearch() {
    this.clearSearchResults();
    searchTextField.setText("");
    this.setSearchFocus();
  }

  protected void performSearch() {
    this.clearSearchResults();
  
    String searchString = searchTextField.getText().trim();
    if (searchString.length() == 0) {
      return;
    }
  
    List results;
  
    this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    try {
      results = controller.performSearch(searchString);
    } finally {
      this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
  
    if (results.size() > 0) {
      this.showSearchResults(results);
    } else {
      ErrorDialog.showError(this, Messages
        .getString("Viz.NoSearchResultsFound"));
    }
  
    this.setSearchFocus();
    clearButton.setEnabled(true);
  }

  private void showSearchResults(List results) {
    // Start them blinking
    blinkThread = new BlinkingThread(results);

    // Find the node with the max number of edges.
    // We assume this is the most important node.

    TMTopicNode maxElement = (TMTopicNode) results.get(0);
    for (int i = 1; i < results.size(); i++) {
      TMTopicNode element = (TMTopicNode) results.get(i);
      if (element.edgeCount() > maxElement.edgeCount()) {
        maxElement = element;
      }
    }

    // Center !!! this node. This code is not correct, but it is close
    // and I don't have any more time !

    Point firstLocation = (maxElement).getLocation();
    TGPoint2D target = new TGPoint2D(firstLocation.getX(), firstLocation.getY());
    Point fromLocation = hvScroll.getOffset();
    TGPoint2D from = new TGPoint2D(fromLocation.getX(), fromLocation.getY());

    hvScroll.scrollAtoB(target, from);
    tgPanel.repaintAfterMove();
  }

  /**
   * Constructs top level panel consisting of the LocalitySpinner and
   * the ZoomScroll Bar.
   */
  protected JPanel buildSpinnerPanel() {
    locSpinner = new OSpinner();
    locSpinner.setPreferredSize(new Dimension(40, 20));

    locSpinner.addPropertyChangeListener("value", new PropertyChangeListener() {
          @Override
          public void propertyChange(PropertyChangeEvent evt) {
            controller.setLocality(((Integer) evt.getNewValue()).intValue());
          }
        });

    final JPanel sbp = new JPanel(new GridBagLayout());

    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 0;
    sbp.add(locSpinner, c);
    c.gridx = 1;
    c.gridy = 0;
    c.weightx = 1;
    c.insets = new Insets(0, 10, 0, 17);
    c.fill = GridBagConstraints.HORIZONTAL;

    zoomScroll.getZoomSB().setVisible(true);
    zoomScroll.getZoomSB().setMinimumSize(new Dimension(200, 17));
    sbp.add(zoomScroll.getZoomSB(), c);

    return sbp;
  }

  /**
   * Add and activate the Touch Graph 'Navigate GUI Manager' to the TG display.
   */
  private void addUIs() {
    tgUIManager = new TGUIManager();
    NavigateUI navigateUI = new NavigateUI(this, controller);
    tgUIManager.addUI(navigateUI, "Navigate");
    tgUIManager.activate("Navigate");
  }

  public void clearSearchResults() {
    if (blinkThread != null) {
      blinkThread.stopBlinking();
      blinkThread = null;
      clearButton.setEnabled(false);
    }
  }

  public void setSearchFocus() {
    searchTextField.setSelectionStart(0);
    searchTextField.setSelectionEnd(searchTextField.getText().length());
    searchTextField.requestFocus();
  }

  public int getLocality() {
    return locSpinner.getValue();
  }

  public void setLocality(int value) {
    locSpinner.setValue(value);
  }

  public AssociationScopeFilterMenu getAssociationScopeFilterMenu() {
    return associationScopeFilterMenu;
  }

  public void configureDynamicMenus(ActionListener parentListener) {
    TopicMapIF topicMap = controller.getTopicMap();
    controller.configure(associationScopeFilterMenu,
        topicMap, parentListener);
  }

  public TypesConfigFrame getTopicFrame() {
    return topicFrame; 
  }
  
  public TypesConfigFrame getAssocFrame() {
    return assocFrame; 
  }
  
  protected boolean enabled(String itemId) {
    VizDebugUtils.debug("VizPanel.enabled(" + itemId + ") - " + enabledItemIds);
    return enabledItemIds.enabled(itemId);
  }
  
  public void add(JPopupMenu menu, JMenuItem item, String itemId) {
    if (enabled(itemId)) {
      menu.add(item);
    }
  }

  private void menuOpenAssociationConfig() {
    if (!controller.hasTopicMap()) {
      return;
    }

    if (assocFrame == null) {
      assocFrame = vizFrontEnd.getTypesConfigFrame(controller, false);
      assocFrame.show();
    } else {
      assocFrame.setVisible(true);
      assocFrame.toFront();
    }
  }

  private void menuOpenTopicConfig() {
    if (!controller.hasTopicMap()) {
      return;
    }

    if (topicFrame == null) {
      topicFrame = vizFrontEnd.getTypesConfigFrame(controller, true);
      topicFrame.show();
    } else {
      topicFrame.setVisible(true);
      topicFrame.toFront();
    }
  }
  
  // --- BlinkingThread
  
  private class BlinkingThread extends Thread {
    private boolean blinking = true;
    private boolean blink = true;
    private HashMap blinkMap;
    private final List results;

    private BlinkingThread(List list) {
      super("Blinking Thread");
      this.results = list;
      this.initializeBlinkMap();
      start();
    }

    public void stopBlinking() {
      blinking = false;
      blink = false;
      paintResults();
    }

    @Override
    public void run() {
      while (blinking) {
        try {
          Thread.sleep(200);
        } catch (InterruptedException e) {
          // Do nothing
        }
        paintResults();
        blink = !blink;
      }
    }

    private void paintResults() {
      for (Iterator iter = results.iterator(); iter.hasNext();) {
        TMTopicNode element = (TMTopicNode) iter.next();
        if (element.isVisible()) {
          if (blink) {
            element.repaint((Color) blinkMap.get(element
                .getPaintBackColor(tgPanel)), tgPanel, true);
          } else {
            element.repaint(element.getPaintBackColor(tgPanel), tgPanel, true);
          }
        }
      }
    }

    private void initializeBlinkMap() {
      blinkMap = new HashMap(results.size());
      for (Iterator iter = results.iterator(); iter.hasNext();) {
        Color colour = ((TMTopicNode) iter.next()).getPaintBackColor(tgPanel);
        blinkMap.put(colour, this.blinkColourFor(colour));
      }
    }

    private Color blinkColourFor(Color aColour) {
      return new Color(0xFF - aColour.getRed(), 0xFF - aColour.getGreen(),
          0xFF - aColour.getBlue());
    }
  }
}
