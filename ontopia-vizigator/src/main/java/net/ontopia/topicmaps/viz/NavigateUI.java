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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import net.ontopia.topicmaps.core.TopicIF;

import com.touchgraph.graphlayout.Node;
import com.touchgraph.graphlayout.TGPanel;
import com.touchgraph.graphlayout.interaction.DragNodeUI;
import com.touchgraph.graphlayout.interaction.TGAbstractDragUI;
import com.touchgraph.graphlayout.interaction.TGUserInterface;

/**
 * INTERNAL.
 */
public class NavigateUI extends TGUserInterface {
  private VizPanel glPanel;
  private VizController controller;
  private TGPanel tgPanel;
  private GLNavigateMouseListener ml;
  private TGAbstractDragUI hvDragUI;
  private DragNodeUI dragNodeUI;
  private JPopupMenu nodePopup;
  private JPopupMenu edgePopup;
  private TMAbstractNode popupNode;
  private TMAbstractEdge popupEdge;
  private JMenuItem propertiesMenuItem;
  private JMenuItem setStartNodeMenuItem;
  private JMenuItem copyNameMenuItem;
  private JMenuItem gotoTopicMenuItem;
  private TextTransfer textTransfer = new TextTransfer();
  
  public static final String ITEM_ID_EXPAND_NODE = "expand.node";
  public static final String ITEM_ID_COLLAPSE_NODE = "collapse.node";
  public static final String ITEM_ID_HIDE_NODE = "hide.node";
  public static final String ITEM_ID_STICKY = "sticky";
  public static final String ITEM_ID_PROPERTIES = "properties";
  public static final String ITEM_ID_SET_START_TOPIC = "set.start.topic";
  public static final String ITEM_ID_COPY_NAME = "copy.name";
  public static final String ITEM_ID_GO_TO_TOPIC_PAGE = "go.to.topic.page";
  
  public static final String ITEM_ID_HIDE_EDGE = "hide.edge";

  private ParsedMenuFile enabledItemIds;
  
  private static final int OP_EXPAND_NODE = 0;
  private static final int OP_COLLAPSE_NODE = 1;
  private static final int OP_HIDE_NODE = 2;
  private static final int OP_SET_AS_START_NODE = 3;
  private static final int OP_GO_TO_TOPIC = 4;
  private static final int OP_DEBUG = 5;
  private static final int OP_OPEN_PROPERTIES = 6;
  private static final int OP_STICKY = 7;
  private static final int OP_COPY_NAME = 8;
  protected JCheckBoxMenuItem stickyMenu;

  public NavigateUI(VizPanel glp, VizController controller) {
    this.glPanel = glp;
    this.controller = controller;
    tgPanel = glPanel.getTGPanel();

    hvDragUI = glPanel.getHVScroll().getHVDragUI();

    dragNodeUI = new DragNodeUI(tgPanel);

    ml = new GLNavigateMouseListener();
    enabledItemIds = controller.getEnabledItemIds();
    nodePopup = setUpNodePopup();
    edgePopup = setUpEdgePopup();
  }

  private JPopupMenu setUpEdgePopup() {
    JPopupMenu edgePopup = new JPopupMenu();

    JMenuItem menuItem = new JMenuItem(Messages.getString("Viz.PopupHideEdge"));

    ActionListener hideAction = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (popupEdge != null) {
          controller.hideEdge(popupEdge);
        }
      }
    };

    menuItem.addActionListener(hideAction);
    addMenuItem(edgePopup, menuItem, ITEM_ID_HIDE_EDGE);

    edgePopup.addPopupMenuListener(
      new PopupMenuListener() {
        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
          // Do nothing
        }
  
        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
          tgPanel.setMaintainMouseOver(false);
          tgPanel.setMouseOverE(null);
          tgPanel.repaint();
        }
  
        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
          // Do nothing
        }
      }
    );
    
    return edgePopup;
  }

  private JPopupMenu setUpNodePopup() {
    JPopupMenu nodePopup = new JPopupMenu();
    nodePopup.addPopupMenuListener(new PopupMenuListener() {
      @Override
      public void popupMenuCanceled(PopupMenuEvent e) {
        // Do nothing
      }

      @Override
      public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        tgPanel.setMaintainMouseOver(false);
        tgPanel.setMouseOverN(null);
        tgPanel.repaint();
      }

      @Override
      public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        // This is handled in GLNavigateMouseListener>>#processPopupRequest
      }
    });

    boolean group1 = isEnabled(ITEM_ID_EXPAND_NODE) ||
                     isEnabled(ITEM_ID_COLLAPSE_NODE) ||
                     isEnabled(ITEM_ID_HIDE_NODE); 
    boolean group2 = isEnabled(ITEM_ID_STICKY);
    boolean group3 = isEnabled(ITEM_ID_PROPERTIES);
    boolean group4 = isEnabled(ITEM_ID_SET_START_TOPIC) ||
                     isEnabled(ITEM_ID_COPY_NAME) ||
                     isEnabled(ITEM_ID_GO_TO_TOPIC_PAGE);
      
    makeMenuItem(nodePopup,
                Messages.getString("Viz.MenuExpandNode"), OP_EXPAND_NODE,
                ITEM_ID_EXPAND_NODE);
    makeMenuItem(nodePopup,
                Messages.getString("Viz.MenuCollapseNode"), OP_COLLAPSE_NODE,
                ITEM_ID_COLLAPSE_NODE);
    makeMenuItem(nodePopup,
                Messages.getString("Viz.MenuHideNode"), OP_HIDE_NODE,
                ITEM_ID_HIDE_NODE);
    

    if (group1 && (group2 || group3 || group4)) {
      nodePopup.addSeparator();
    }
    stickyMenu = new JCheckBoxMenuItem(
        Messages.getString("Viz.PopupSticky"), false);
    stickyMenu.addActionListener(new NodeMenuListener(OP_STICKY));
    addMenuItem(nodePopup, stickyMenu, ITEM_ID_STICKY);

    if (group2 && (group3 || group4)) {
      nodePopup.addSeparator();
    }
    propertiesMenuItem = 
      makeMenuItem(nodePopup, Messages.getString("Viz.PopupProperties"),
          OP_OPEN_PROPERTIES, ITEM_ID_PROPERTIES);


    if (group3 && group4) {
      nodePopup.addSeparator();
    }
    setStartNodeMenuItem = 
      makeMenuItem(nodePopup, Messages.getString("Viz.PopupSetStartNode"),
          OP_SET_AS_START_NODE, ITEM_ID_SET_START_TOPIC);

    copyNameMenuItem = 
        makeMenuItem(nodePopup, Messages.getString("Viz.CopyName"),
            OP_COPY_NAME, ITEM_ID_COPY_NAME);

    // addMenuItem(nodePopup, "Debug", OP_DEBUG);

    gotoTopicMenuItem = 
        makeMenuItem(nodePopup, Messages.getString("Viz.PopupGoToTopic"),
                    OP_GO_TO_TOPIC, ITEM_ID_GO_TO_TOPIC_PAGE);

    return nodePopup;
  }
  
  @Override
  public void activate() {
    tgPanel.addMouseListener(ml);
  }

  @Override
  public void deactivate() {
    tgPanel.removeMouseListener(ml);
  }

  /**
   * Create and add menu item with a given label to a given menu.
   * @param menu The menu to holde the menu item.
   * @param label The label of the menu item.
   * @param opcode Identifies the operation performed upon selecting this item.
   * @param itemId ID for testing if the item is enabled.
   * @return The JMenuItem created adn added to the menu.
   */
  private JMenuItem makeMenuItem(JPopupMenu menu, String label, int opcode,
                           String itemId) {
    JMenuItem item = new JMenuItem(label);
    item.addActionListener(new NodeMenuListener(opcode));
    addMenuItem(menu, item, itemId);
    return item;
  }

  /**
   * Add a given menu item to a given menu.
   * @param menu The menu to holde the menu item.
   * @param item The menu item.
   * @param itemId ID for testing if the item is enabled.
   */
  public void addMenuItem(JPopupMenu menu, JMenuItem item, String itemId) {
    if (isEnabled(itemId)) {
      menu.add(item);
    }
  }

  /**
   * Test if the menu item with the given ID is enabled.
   * @param itemId The ID of the menu item.
   * @return true iff the item is enabled.
   */
  protected boolean isEnabled(String itemId) {
    return enabledItemIds.enabled(itemId);
  }
  
  class GLNavigateMouseListener extends MouseAdapter {
    private Integer doubleClickInterval = ((Integer) Toolkit
        .getDefaultToolkit().getDesktopProperty("awt.multiClickInterval"));
    private ClickThread clickThread;

    @Override
    public void mousePressed(MouseEvent e) {
      // MacOS & Linux (Unix) popup handling
      if (e.isPopupTrigger()) {
        processPopupRequest(e);
        return;
      }

      if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {
        Node mouseOverN = tgPanel.getMouseOverN();
        if (mouseOverN == null) {
          hvDragUI.activate(e);
        } else {
          dragNodeUI.activate(e);
        }
      }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
      TMAbstractNode mouseOverN = (TMAbstractNode)tgPanel.getMouseOverN();

      if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {
        if (mouseOverN != null) {
          if (e.getClickCount() == 2) {
            this.killSingleClick();
            this.performOperation(controller.getConfigurationManager()
                .getGeneralDoubleClick(), mouseOverN);
          } else if (e.getClickCount() == 1) {
            this.spawnSingleClick(mouseOverN);
          }
        }
      }
    }

    private void spawnSingleClick(TMAbstractNode node) {
      if (clickThread != null) {
        clickThread.stopExecution();
      }
      int interval;
      if (doubleClickInterval == null) {
        interval = 500;
      } else {
        interval = doubleClickInterval.intValue();
      }
      clickThread = new ClickThread(interval, this, node);
      clickThread.start();
    }

    public void processSingleClick(TMAbstractNode node) {
      this.performOperation(controller.getConfigurationManager()
          .getGeneralSingleClick(), node);
    }

    private void killSingleClick() {
      if (clickThread != null) {
        clickThread.stopExecution();
      }
      clickThread = null;

    }

    private void performOperation(int anAction, TMAbstractNode node) {
      switch (anAction) {
      case VizTopicMapConfigurationManager.EXPAND_NODE:
        controller.expandNode(node);
        break;
      case VizTopicMapConfigurationManager.SET_FOCUS_NODE:
        controller.focusNode((TMAbstractNode) node);
        break;
      case VizTopicMapConfigurationManager.GO_TO_TOPIC:
        controller.goToTopic(((TMTopicNode) node).getTopic());
      }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      // Windows Popup handling
      if (e.isPopupTrigger()) {
        processPopupRequest(e);
      }
    }

    private void processPopupRequest(MouseEvent e) {
      popupNode = (TMAbstractNode) tgPanel.getMouseOverN();
      popupEdge = (TMAbstractEdge) tgPanel.getMouseOverE();

      if (popupNode != null) {
        stickyMenu.setSelected(popupNode.getFixed());
        tgPanel.setMaintainMouseOver(true);

        if (popupNode instanceof TMAssociationNode) {
          propertiesMenuItem.setEnabled(false);
          setStartNodeMenuItem.setEnabled(false);
          gotoTopicMenuItem.setEnabled(false);
        } else {
          propertiesMenuItem.setEnabled(true);
          setStartNodeMenuItem.setEnabled(!controller.isApplet()
              && !((TMTopicNode) popupNode).getTopic().equals(
                  controller.getStartTopic()));
          gotoTopicMenuItem.setEnabled(controller.isApplet());
        }
        
        if (popupNode instanceof TMTopicNode) {
          copyNameMenuItem.setEnabled(true);
        } else {
          copyNameMenuItem.setEnabled(false);
        }

        if (isEnabled(ITEM_ID_EXPAND_NODE) ||
            isEnabled(ITEM_ID_COLLAPSE_NODE) ||
            isEnabled(ITEM_ID_COPY_NAME) ||
            isEnabled(ITEM_ID_GO_TO_TOPIC_PAGE) ||
            isEnabled(ITEM_ID_HIDE_NODE) ||
            isEnabled(ITEM_ID_PROPERTIES) ||
            isEnabled(ITEM_ID_SET_START_TOPIC) ||
            isEnabled(ITEM_ID_STICKY)) {
          nodePopup.show(e.getComponent(), e.getX(), e.getY());
        }

      } else if (popupEdge != null) {
        if (isEnabled(ITEM_ID_HIDE_EDGE)) {
          tgPanel.setMaintainMouseOver(true);
          edgePopup.show(e.getComponent(), e.getX(), e.getY());
        }
      } else {
        glPanel.glPopup.show(e.getComponent(), e.getX(), e.getY());
      }
    }
  }

  // --- Node menu class

  class NodeMenuListener implements ActionListener {
    private int opcode;

    public NodeMenuListener(int opcode) {
      this.opcode = opcode;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if (popupNode == null) {
        return;
      }

      switch (opcode) {
      case OP_EXPAND_NODE:
        controller.expandNode(popupNode);
        break;
      case OP_COLLAPSE_NODE:
        controller.collapseNode(popupNode);
        break;
      case OP_HIDE_NODE:
        controller.hideNode(popupNode);
        break;
      case OP_SET_AS_START_NODE:
        TMTopicNode target = (TMTopicNode) popupNode;
        controller.focusNode(target);
        controller.setStartTopic(target.getTopic());
        break;
      case OP_DEBUG:
        System.out.println("----------------------------------------");
        VizUtils.debug(popupNode);
        VizUtils.debug(((TMTopicNode) popupNode).getTopic());
        break;
      case OP_GO_TO_TOPIC:
        TopicIF topic = ((TMTopicNode) popupNode).getTopic();
        controller.goToTopic(topic);
        break;
      case OP_OPEN_PROPERTIES:
        controller.openProperties((TMTopicNode) popupNode);
        break;
      case OP_STICKY:
        // FIXME: All-sticky implementation <<<
        TMAbstractNode node = popupNode;
        node.setFixed(!node.getFixed());
        break;
      case OP_COPY_NAME:
        textTransfer.setClipboardContents(TopicMapView.fullName(popupNode));
      }
    }
  }
  
  public final class TextTransfer implements ClipboardOwner {
    /**
     * Place a String on the clipboard, and make this class the
     * owner of the Clipboard's contents.
     */
    public void setClipboardContents( String aString ){
      StringSelection stringSelection = new StringSelection( aString );
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      clipboard.setContents( stringSelection, this );
    }
  
    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
      // Do nothing
    }
  }
 
  /**
   * INTERNAL: Implementation to get around Swing's stupid double
   * click handling.
   */
  private class ClickThread extends Thread {
    private int delta;
    private boolean execute = true;
    private TMAbstractNode target;
    private GLNavigateMouseListener parent;

    public ClickThread(int threashhold,
                       GLNavigateMouseListener p, 
                       TMAbstractNode node) {
      super("ClickThread");
      delta = threashhold; 
      target = node;
      parent = p;
    }

    @Override
    public void run() {
      try {
        ClickThread.sleep(delta);
      } catch (InterruptedException e) {
        // Do nothing
      }
      if (execute) {
        parent.processSingleClick(target);
      }
    }

    public void stopExecution() {
      execute = false;
    }
  }
}
