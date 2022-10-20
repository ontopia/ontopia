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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.ScopeUtils;
import net.ontopia.utils.OntopiaRuntimeException;

import com.touchgraph.graphlayout.Node;
import com.touchgraph.graphlayout.TGException;
import com.touchgraph.graphlayout.TGPaintListener;
import com.touchgraph.graphlayout.TGPanel;

/**
 * INTERNAL: Node class representing n-ary associations as nodes.
 */
public class TMAssociationNode extends TMAbstractNode
  implements VizTMObjectIF, VizTMAssociationIF, TGPaintListener {

  // We need to hold our own version of this for GUI display purposes in the
  // Vizlet
  private int roleCount = 0;

  private static final String SHORT_NAME = "http://psi.ontopia.net/basename/" +
      "#short-name";

  private AssociationIF association;

  private int lineWeight = TMAbstractEdge.DEFAULT_LINE_WEIGHT;

  private TopicIF scopingTopic;
  private boolean shouldDisplayScopedAssociationNames;

  protected static String getAssociationText(AssociationIF association,
                                             boolean displScopedAssocNames,
                                             TopicIF scopingTopic) {
    String main = "[No name]";
    if (association.getType() != null) {

      List scope = new ArrayList(2);
      TopicMapIF tm = association.getTopicMap();
      TopicIF shortName = (tm == null ? null : tm.getTopicBySubjectIdentifier(VizUtils
          .makeLocator(SHORT_NAME)));
      if (shortName != null) {
        scope.add(shortName);
      }
      if (scopingTopic != null) {
        scope.add(scopingTopic);
      }

      Collection bnames = association.getType().getTopicNames();
      int nsize = bnames.size();
      if (nsize > 0) {
       // rank names by scope relevancy
       List ranked = ScopeUtils.rankByScope(bnames, scope);       
       main = ((TopicNameIF)ranked.get(0)).getValue();
       // keep track of names already seen
       Collection visited = new HashSet(ranked.size());
       visited.add(main);

       // output remaining names
       if (nsize > 1 && displScopedAssocNames) {
         String names = "";
         boolean first = true;
         for (int i=1; i < nsize; i++) {
           TopicNameIF element = (TopicNameIF) ranked.get(i);
           String name = element.getValue();
           if (!visited.contains(name)) {
             names += first ? " (" : " / ";
             names += name;
             visited.add(name);
             first = false;
           }
         }
         if (!first) {
           names += ")";
         }
         return main + names;
       }
      }
    }
    return main;
  }

  public void setEdgeCount(int visibleEdgeCount) {
    visibleEdgeCnt = visibleEdgeCount;
  }

  public int getEdgeCount() {
    return visibleEdgeCnt;
  }

  public void setRoleCount(int count) {
    this.roleCount = count;
  }

  protected void drawMissingEdgesIndicator(Graphics g, TGPanel tgPanel) {
    int hiddenEdgeCount = roleCount - visibleEdgeCount();

    if (hiddenEdgeCount <= 0) {
      return;
    }

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

  public TMAssociationNode(AssociationIF assoc, TopicIF aScopingTopic,
                           TopicMapView topicMapView) {
    super(assoc.getObjectId());
    this.association = assoc;
    setScopingTopic(aScopingTopic);
    this.setLabel("");
    this.setType(Node.TYPE_CIRCLE);
    this.topicMapView = topicMapView;

  }

  @Override
  public void addTo(TGPanel tgpanel) {
    try {
      tgpanel.addNode(this);
    } catch (TGException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  @Override
  public void deleteFrom(TGPanel tgpanel) {
    tgpanel.deleteNode(this);
  }

  public AssociationIF getAssociation() {
    return this.association;
  }
  
  public int getLineWeight() {
    return this.lineWeight * 2;
  }

  public String getMainText() {
    return getAssociationText(association,
                              shouldDisplayScopedAssociationNames,
                              scopingTopic);
  }

  public List getTargetsFrom(Node find) {
    ArrayList targets = new ArrayList(this.edgeCount());

    for (Iterator iter = this.getEdges(); iter.hasNext();) {
      TMRoleEdge element = (TMRoleEdge) iter.next();
      Node target = element.getOtherEndpt(this);
      if (!target.equals(find)) {
        targets.add(target);
      }
    }
    return targets;
  }

  @Override
  public TopicIF getTopicMapType() {
    return association.getType();
  }

  @Override
  public int getWidth() {
    if (icon == null) {
      return this.lineWeight * 2;
    }
    return icon.getIconWidth();
  }

  @Override
  public boolean isAssociation() {
    return true;
  }

  @Override
  public boolean isEdge() {
    return false;
  }

  @Override
  public void paint(Graphics g, TGPanel tgPanel) {
    setUnderMouse(tgPanel);
    if (underMouse) {
      topicMapView.setHighlightNode(this, g);
    } else if (tgPanel.getMouseOverN() == null && tgPanel.getSelect() == this) {
      topicMapView.setHighlightNode(null, g);
    }
    
    miniPaint(g, tgPanel);
  }

  public void miniPaint(Graphics g, TGPanel tgPanel) {
    super.paint(g, tgPanel);
    this.drawMissingEdgesIndicator(g, tgPanel);

    if (icon != null) {
      icon.paintIcon(tgPanel, g, (int) drawx - icon.getIconWidth() / 2,
          (int) drawy - icon.getIconHeight() / 2);
    }
  }

  @Override
  public void paintAfterEdges(Graphics g) {
    // Currently do nothing
  }

  @Override
  public void paintFirst(Graphics g) {
    // Currently do nothing
  }

  /**
   * This is our hover help support. This method is called after all
   * other painting has been completed, hence ensuring that ToolTips
   * (HoverHelp) is always drawn ontop.
   */
  @Override
  public void paintLast(Graphics g) {
    if (underMouse) {
      this.paintToolTip(g);
    }
  }

  private void paintToolTip(Graphics g) {
    this.paintToolTipText(g, this.getMainText(), (int) drawx, (int) drawy);
  }

  /**
   * @param g -
   *               The graphic context for the drawing operation.
   * @param string -
   *               The String to be rendered.
   * @param x -
   *               The x coordinate where the String should be positioned.
   * @param y -
   *               The y coordinate where the String should be positioned. NOTE: The
   *               text is <b>centered </b> over the given coordinates.
   */
  private void paintToolTipText(Graphics g, String string, int x, int y) {
    g.setFont(this.getFont());
    FontMetrics fontMetrics = g.getFontMetrics();

    int a = fontMetrics.getAscent();
    int h = a + fontMetrics.getDescent();
    int w = fontMetrics.stringWidth(string);

    int xPosition = x - (w / 2);
    int yPosition = y - (h / 2);

    // Draw the background

    Color c = this.getBackColor();
    g.setColor(c);

    int r = h / 2;
    int vPad = h / 8;
    int hPad = h / 4;

    g.fillRoundRect(xPosition - hPad, yPosition - vPad, w + (2 * hPad), h
        + (2 * vPad), r, r);

    // Draw a defined edge to the popup
    g.setColor(TMTopicNode.textColourForBackground(c));
    g.drawRoundRect(xPosition - hPad, yPosition - vPad, w + (2 * hPad), h
        + (2 * vPad), r, r);

    // Draw the text
    g.drawString(string, xPosition, yPosition + a);
  }

  @Override
  public boolean represents(Object object) {
    return association.equals(object);
  }

  @Override
  public void setColor(Color color) {
    this.setBackColor(color);
  }

  @Override
  public void setLineWeight(int lineWeight) {
    this.lineWeight = lineWeight;
  }

  @Override
  public void setScopingTopic(TopicIF aTopic) {
    scopingTopic = aTopic;
  }

  @Override
  public void setShape(int shape) {
    // Shape is not applicable for Association Nodes
  }

  @Override
  public void setShouldDisplayScopedAssociationNames(boolean newValue) {
    shouldDisplayScopedAssociationNames = newValue;
  }

  @Override
  public RecoveryObjectIF getDesctructor() {
    return new DeleteTMAssociationNode(association);
  }

  @Override
  public RecoveryObjectIF getRecreator() {
    return new CreateTMAssociationNode(association);
  }
}
