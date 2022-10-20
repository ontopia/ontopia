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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.touchgraph.graphlayout.graphelements.ImmutableGraphEltSet;

import net.ontopia.topicmaps.core.TopicIF;

public class VizigatorUser extends TimerTask {
  
  // Note: VizPanel uses this variable set the initial state of a menu.
  public static final boolean INITIALLY_ENABLED = false;

  protected static final int EXPAND_NODE = 0;
  protected static final int FOCUS_NODE = 1;
  protected static final int HIDE_NODE = 2;
  protected static final int HIDE_EDGE = 3;
  protected static final int UNDO = 4;
  protected static final int REDO = 5;
  
  protected int runCount;
  protected int hideNodeCount;
  protected int hideFocusNodeCount;
  protected int hideEdgeCount;
  protected int focusNodeCount;
  protected int expandNodeCount;
  protected int undoCount;
  protected int redoCount;

  private VizController controller;
  protected boolean enabled;
  private Random random;
  
  private long millis;
  private Timer timer;  

  public VizigatorUser(VizController controller, long millis) {
    this.controller = controller;
    this.millis = millis;

    random = new Random();
    runCount = 0;
    hideNodeCount = 0;
    hideFocusNodeCount = 0;
    hideEdgeCount = 0;
    focusNodeCount = 0;
    expandNodeCount = 0;
    undoCount = 0;
    redoCount = 0;
    timer = null;
    
    enabled = INITIALLY_ENABLED;
    setEnabled(enabled);
  }
  
  /**
   * This method is called on schedule by the timer.
   */
  @Override
  public void run() {
    if (!enabled) {
      return;
    }
    useVizigator();
    runCount++;
  }
  
  protected void useVizigator() {
    ImmutableGraphEltSet ges = controller.getView().getTGPanel().getGES();

    int nodeCount = ges.nodeCount();
    
    VizDebugUtils.debug("**** useVizigator() + nodes: " + nodeCount + 
        " runs: " + runCount + " ****");
    VizDebugUtils.debug("******** operations summary " +
                        "- hideNode(" + hideNodeCount +
                        ") hideFocusNode(" + hideFocusNodeCount +
                        ") hideEdge(" + hideEdgeCount +
                        ") expandNode(" + expandNodeCount +
                        ") focusNode(" + focusNodeCount +
                        ") undo(" + undoCount +
                        ") redo(" + redoCount +
                        ")");
    
    int expandNodeWeight = 500;
    int focusNodeWeight = 30;
    int hideNodeWeight = 300;
    int hideEdgeWeight = 200;
    int undoWeight = 200;
    int redoWeight = 200;
    
    int hideFocusNodeProbability = 20;
    
    if (nodeCount < 5) {
      expandNodeWeight = 900;
      hideNodeWeight = 70;
      hideEdgeWeight = 30;
    } else if (nodeCount < 10) {
      expandNodeWeight = 750;
      hideNodeWeight = 170;
      hideEdgeWeight = 80;
    } else if (nodeCount < 50) {
      expandNodeWeight = 600;
      hideNodeWeight = 270;
      hideEdgeWeight = 130;
    } else if (nodeCount < 100) {
      expandNodeWeight = 400;
      hideNodeWeight = 400;
      hideEdgeWeight = 200;
    } else if (nodeCount < 120) {
      expandNodeWeight = 300;
      hideNodeWeight = 470;
      hideEdgeWeight = 230;
    } else if (nodeCount < 140) {
      expandNodeWeight = 200;
      hideNodeWeight = 540;
      hideEdgeWeight = 260;
    } else if (nodeCount < 150) {
      expandNodeWeight = 150;
      hideNodeWeight = 580;
      hideEdgeWeight = 270;
    } else if (nodeCount < 160) {
      expandNodeWeight = 100;
      hideNodeWeight = 600;
      hideEdgeWeight = 300;
    } else if (nodeCount < 170) {
      expandNodeWeight = 50;
      hideNodeWeight = 630;
      hideEdgeWeight = 320;
    } else if (nodeCount < 180) {
      expandNodeWeight = 40;
      hideNodeWeight = 640;
      hideEdgeWeight = 320;
    } else if (nodeCount < 190) {
      expandNodeWeight = 20;
      hideNodeWeight = 650;
      hideEdgeWeight = 330;
    } else if (nodeCount < 200) {
      expandNodeWeight = 10;
      hideNodeWeight = 660;
      hideEdgeWeight = 330;
    } else {
      expandNodeWeight = 2;
      hideNodeWeight = 666;
      hideEdgeWeight = 332;
    }

    int totalWeight = expandNodeWeight + hideNodeWeight + hideEdgeWeight + 
        focusNodeWeight + undoWeight + redoWeight;
    int choice = random.nextInt(totalWeight);

    int opcode;    
    if (choice < expandNodeWeight) {
      opcode = EXPAND_NODE;
    } else if (choice < expandNodeWeight + focusNodeWeight) {
      opcode = FOCUS_NODE;
    } else if (choice < expandNodeWeight + focusNodeWeight + hideNodeWeight) {
      opcode = HIDE_NODE;
    } else if (choice < expandNodeWeight + focusNodeWeight + hideNodeWeight
        + hideEdgeWeight) {
      opcode = HIDE_EDGE;
    } else if (choice < expandNodeWeight + focusNodeWeight + hideNodeWeight
        + hideEdgeWeight + undoWeight) {
      opcode = UNDO;
    } else {
      opcode = REDO;
    }

    if (opcode == EXPAND_NODE || opcode == FOCUS_NODE || opcode == HIDE_NODE) {
      TMAbstractNode node = (TMAbstractNode)ges.getRandomNode();
      if (node == null) {
        return;
      }
      
      if (opcode == EXPAND_NODE) {
        controller.expandNode(node);
        expandNodeCount++;
      } else if (opcode == FOCUS_NODE) {
        controller.focusNode(node);
        focusNodeCount++;
      } else if (opcode == HIDE_NODE) {
        if (node == controller.getFocusNode()) {
          if (random.nextInt(100) < hideFocusNodeProbability) {
            controller.hideNode(node);
            hideFocusNodeCount++;
          }
        } else {
          controller.hideNode(node);
          hideNodeCount++;
        }
      }
    } else if (opcode == HIDE_EDGE) {
      // Note: Using argument false (not distinct) since this gives a list.
      // This makes finding an edge more efficient.
      Collection edges = controller.getView().debug
          .getObjectsOfType(TMAbstractEdge.class, false);
      TMAbstractEdge edge = (TMAbstractEdge)pickRandom(edges);
      if (edge == null) {
        return;
      }
      
      if (opcode == HIDE_EDGE) {
        controller.hideEdge(edge);
        hideEdgeCount++;
      }
    } else if (opcode == UNDO) {
      controller.undo();
      undoCount++;
    } else if (opcode == REDO) {
      controller.redo();
      redoCount++;
    }
  }
  
  protected TMAbstractNode pickRandomNode() {
    TopicIF type = null;
    TMAbstractNode node = null;
    
    int tries = 50;
    while (tries > 0 && node == null) {
      tries++;
      type = pickRandomType();
      node = pickRandomNode(type);
    }
    
    return node;
  }
  
  protected Object pickRandom(Collection items) {
    int size = items.size();
    if (size == 0) {
      return null;
    }
    
    int index = random.nextInt(size);
    if (items instanceof List) {
      List itemsList = (List)items;
      return itemsList.get(index);
    }
    
    Iterator itemsIt = items.iterator();
    for (int i = 0; i < index; i++) {
      itemsIt.next();
    }
    return itemsIt.next();
  }
  
  protected TopicIF pickRandomType() {
    return (TopicIF)pickRandom(controller.getAllTopicTypes());
  }
  
  protected TMAbstractNode pickRandomNode(TopicIF type) {
    if (type == null) {
      return null;
    }
    
    return (TMAbstractNode)pickRandom(controller.getView()
        .getTopicNodesFor(type));
  }

  /**
   * Enables/disables this motion killer.
   * Note: VizPanel uses the value of enabled to build menus, so this method
   *     should only be changed (indirectly) from there.
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
    
    if (enabled) {
      timer = new Timer();
      timer.scheduleAtFixedRate(this, millis, millis);
    } else if (timer != null) {
      timer.cancel();
      timer = null;
    }
  }
  
  public boolean getEnabled() {
    return enabled;
  }
}
