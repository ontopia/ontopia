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

import net.ontopia.topicmaps.core.TopicIF;

import com.touchgraph.graphlayout.Node;
import com.touchgraph.graphlayout.TGPanel;
import java.util.function.Function;

/**
 * INTERNAL: Node class representing topics.
 */
public class TMTopicNode extends TMAbstractNode {

  private int shapePadding;

  // We need to hold our own version of this for GUI display purposes in the
  // Vizlet
  private int associationCount = 0;

  protected boolean forceColor = false;
  
  private TopicIF topic;
  public static int DEFAULT_SHAPE_PADDING = 0;
  public static int MAX_SHAPE_PADDING = 20;

  private Function<TopicIF, String> stringifier;

  private TopicIF scopingTopic;
  
  public TMTopicNode(TopicIF topic, TopicIF scopingTopic,
                     TopicMapView topicMapView) {
    super(topic.getObjectId());
    this.topicMapView = topicMapView;
    this.topic = topic;
    this.setScopingTopic(scopingTopic);
    updateLabel();
  }
  
  public void updateLabel() {
    setLabel(getTopicName());
  }

  public void setScopingTopic(TopicIF aScopingTopic) {
    scopingTopic = aScopingTopic;
    this.setStringifier(VizUtils.stringifierFor(scopingTopic));
  }

  public String getTopicName() {
    return this.getStringifier().apply(topic);
  }
  
  /**
   * Repaints the name. Some topic names are shortened when initially displayed.
   * When the user moves the mouse over the node the full name is displayed.
   */
  @Override
  public void paint(Graphics g, TGPanel tgPanel) {
    setUnderMouse(tgPanel);
    String oldLabel = this.getLabel();
  
    if (underMouse) {
      topicMapView.setHighlightNode(this, g);
      super.setLabel(this.getStringifier().apply(topic));
    } else if (tgPanel.getMouseOverN() == null && tgPanel.getSelect() == this) {
      topicMapView.setHighlightNode(null, g);
    }
    
    miniPaint(g, tgPanel);
      
    if (!intersects(tgPanel.getSize())) return;
    this.paintNodeBody(g, tgPanel);
  
    this.drawMissingEdgesIndicator(g, tgPanel);
    
    if (icon != null) {
      icon.paintIcon(tgPanel, g, (int) drawx - (this.getWidth() / 2)
          - icon.getIconWidth(), (int) drawy - (icon.getIconHeight() / 2));
    }
  
    super.setLabel(oldLabel);
  }

  public void miniPaint(Graphics g, TGPanel tgPanel) {
    String oldLabel = this.getLabel();
  
    if (!intersects(tgPanel.getSize())) return;
    this.paintNodeBody(g, tgPanel);
  
    this.drawMissingEdgesIndicator(g, tgPanel);
    
    if (icon != null) {
      icon.paintIcon(tgPanel, g, (int) drawx - (this.getWidth() / 2)
          - icon.getIconWidth(), (int) drawy - (icon.getIconHeight() / 2));
    }
  
    super.setLabel(oldLabel);
  }

  protected void drawMissingEdgesIndicator(Graphics g, TGPanel tgPanel) {
    int hiddenEdgeCount = associationCount - visibleEdgeCount();

    if (hiddenEdgeCount <= 0) return;

    int ix = (int) drawx;
    int iy = (int) drawy;
    int h = getHeight();
    int w = getWidth();
    int tagX = ix + (w - 7) / 2 - 2 + w % 2;
    int tagY = iy - h / 2 - 2;
    char character;
    character = (hiddenEdgeCount < 9) ? (char) ('0' + hiddenEdgeCount) : '*';
    paintSmallTag(g, tgPanel, tagX, tagY, Color.red, Color.white, character);
  }

  @Override
  public void setLabel(String name) {
    int maxNameLength = topicMapView.getMaxTopicNameLength();
    if (name != null && name.length() > maxNameLength)
      name = name.substring(0, maxNameLength) + "...";

    super.setLabel(name);
  }

  public TopicIF getTopic() {
    return topic;
  }

  public void setShapePadding(int value) {
    shapePadding = value;
  }

  public int getShapePadding() {
    return this.shapePadding;
  }

  @Override
  public int getWidth() {
    return super.getWidth() + this.getShapePadding();
  }

  @Override
  public int getHeight() {
    return super.getHeight() + this.getShapePadding();
  }

  /**
   * This method enables TMTopicNode to force a particular color upon a given
   * node, even if it is the focus node.
   * This was necessary because blinking didn't work on the focus node.
   */
  @Override
  public Color getPaintBackColor(TGPanel tgPanel) {
    if (forceColor)
      return getBackColor();
    else
      return super.getPaintBackColor(tgPanel);
  }        

  public void repaint(Color aColor, TGPanel tgPanel) {
    repaint(aColor, tgPanel, false);
  }

  public void repaint(Color aColor, TGPanel tgPanel, boolean forceColor) {
    Color oldValue = getBackColor();
    setBackColor(aColor);
    this.forceColor = forceColor;
    paint(tgPanel.getGraphics(), tgPanel);
    this.forceColor = false;
    setBackColor(oldValue);
  }

  public void setAssociationCount(int count) {
    this.associationCount = count;
  }

  @Override
  public boolean containsPoint(double aPx, double aPy) {
    if (this.getType() == Node.TYPE_CIRCLE) {
      double deltaX = Math.abs((drawx - aPx) / 2);
      double deltaY = Math.abs((drawy - aPy) / 2);
      double result = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
      int radius = (this.getWidth() / 4);
      return result <= radius;
    } else if (this.getType() == Node.TYPE_ELLIPSE) {
      double deltaX = Math.abs((drawx - aPx) / 2);
      double deltaY = Math.abs((drawy - aPy) / 2);
      double result = (Math.pow(deltaX, 2) / Math.pow(this.getWidth() / 4, 2))
          + (Math.pow(deltaY, 2) / Math.pow(this.getHeight() / 4, 2));
      return result <= 1;
    }
    return super.containsPoint(aPx, aPy);
  }

  public Function<TopicIF, String> getStringifier() {
    return this.stringifier;
  }

  private void setStringifier(Function<TopicIF, String> aStringifier) {
    this.stringifier = aStringifier;
    this.setLabel(stringifier.apply(topic));
  }

  @Override
  public RecoveryObjectIF getDesctructor() {
    return new DeleteTMTopicNode(getTopic());
  }

  @Override
  public RecoveryObjectIF getRecreator() {
    return new CreateTMTopicNode(getTopic());
  }
}
