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
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;

import com.touchgraph.graphlayout.Edge;
import com.touchgraph.graphlayout.Node;
import com.touchgraph.graphlayout.TGPanel;

/**
 * INTERNAL: Common abstract superclass for all nodes representing
 * Topic Maps constructs.
 */
public abstract class TMAbstractNode extends Node implements Recoverable {
  protected Icon icon;
  protected boolean underMouse = false;

  protected TopicMapView topicMapView;
  
  public TMAbstractNode(String id) {
    super(id);
    super.setVisible(true);
  }

  /** 
   * setVisible is not supported in Vizigator.
   */
  @Override
  public final void setVisible(boolean visible) {
    // no-op
  }
  
  /**
   * Workaround that avoids mouseover icon hanging around after hiding node.
   */
  public void removeMouseoverIcon() {
    underMouse = false;
  }

  public static Color textColourForBackground(Color c) {
    if (((c.getRed() + c.getGreen() + c.getBlue()) / 3) > 130) {
      return Color.black;
    }
    return Color.white;
  }

  public boolean hasPathTo(TMAbstractNode target, Set visited) {
    if (equals(target)) {
      return true;
    }
    if (visited.contains(this)) {
      return false;
    }
    visited.add(this);
    for (Iterator iter = getVisibleEdges(); iter.hasNext();) {
      TMAbstractEdge edge = (TMAbstractEdge) iter.next();
      TMAbstractNode neighbour = (TMAbstractNode) edge.getOtherEndpt(this);
      if (neighbour.hasPathTo(target, visited)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @return An iterator on all the edges.
   */
  @Override
  public Iterator getEdges() {
    // For some strange reason, the superclass implementation
    // of this method returns NULL if there are no edges !!!
  
    Iterator result = super.getEdges();
    if (result == null) {
      return Collections.EMPTY_LIST.iterator();
    }
    return result;
  }

  /**
   * @return An iterator on the edges that are currently visible.
   */
  public Iterator getVisibleEdges() {
    ArrayList result = new ArrayList();
    for (Iterator iter = getEdges(); iter.hasNext();) {
      Edge edge = (Edge) iter.next();

      if (edge.isVisible()) {
        result.add(edge);
      }
    }
    return result.iterator();
  }

  /**
   * @return A list of the edges that are currently visible.
   */
  public List getVisibleEdgesList() {
    ArrayList result = new ArrayList();
    for (Iterator iter = getEdges(); iter.hasNext();) {
      Edge edge = (Edge) iter.next();
      if (edge.isVisible()) {
        result.add(edge);
      }
    }
    return result;
  }

  /**
   * Set the node background color. Also applies a simple colour inversion
   * algorithm on the text colour.
   */
  @Override
  public void setBackColor(Color bgColor) {
    super.setBackColor(bgColor);
    setTextColor(TMAbstractNode.textColourForBackground(bgColor));
  }

  public Icon getIcon() {
    return icon;
  }

  public void setIcon(Icon icon) {
    this.icon = icon;
  }

  @Override
  public Color getPaintBorderColor(TGPanel tgPanel) {
    if (this == tgPanel.getDragNode()) {
      return BORDER_DRAG_COLOR;
    } else if (this == tgPanel.getMouseOverN()) {
      return BORDER_MOUSE_OVER_COLOR;
    }
    return TGPanel.BACK_COLOR;
  }

  @Override
  public Color getPaintBackColor(TGPanel tgPanel) {
    // Overwritten to allways show @backColor except when selected
    if (this == tgPanel.getSelect()) {
      return BACK_SELECT_COLOR;
    }
    return backColor;
  }

  protected void drawNeighboursInForeground(Graphics g, TGPanel tgPanel) {
    int ix = (int)drawx;
    int iy = (int)drawy;
    double d = Math.sqrt(getWidth() * getWidth() + getHeight() * getHeight());

    // Calculate size of circle, based on neighbouring nodes.
    Iterator edges = getEdges();
    while (edges.hasNext()) {
      TMAbstractEdge currentEdge = (TMAbstractEdge)edges.next();
      TMAbstractNode currentNode = (TMAbstractNode)currentEdge
          .getOtherEndpt(this);
      topicMapView.queueInForeground(currentEdge);
      topicMapView.queueInForeground(currentNode);
      double currentX = currentNode.drawx;
      double currentY = currentNode.drawy;
      double currentWidth = Math.abs((drawx - currentX) * 2) + 
          currentNode.getWidth();
      double currentHeight = Math.abs((drawy - currentY) * 2) + 
          currentNode.getHeight();
      
      double currentD = (int)Math.sqrt(currentWidth * currentWidth + 
          currentHeight * currentHeight);
      
      if (currentD > d) {
        d = currentD;
      }
    }
    int diameter = (int)d;
    
    Color circleColor = Color.darkGray;
    int red = circleColor.getRed();
    int green = circleColor.getGreen();
    int blue = circleColor.getBlue();
    int alpha = (int)(circleColor.getAlpha() * 0.75);
    circleColor = new Color(red, green, blue, alpha);
    
    g.setColor(circleColor);
    if (topicMapView.controller.showNeighbouringCircle) {
      g.fillOval(ix - diameter/2, iy - diameter / 2, diameter, diameter);
    }
    topicMapView.queueInForeground(this);
  }
  
  /**
   * This is how TG gets the color it actually uses for the painting, which in
   * our case may be different from the preset text color.
   */
  @Override
  public Color getPaintTextColor(TGPanel tgPanel) {
    if (this == tgPanel.getSelect()) {
      // given the select color, black is right
      return Color.black;
    }
    return textColor; // this is what the TG impl of this method does
  }

  protected void setUnderMouse(TGPanel tgPanel) {
    underMouse = (tgPanel.getMouseOverN() == this);
  }
  
  public void setUnderMouseForced(boolean under) {
    underMouse = under;
  }
}
