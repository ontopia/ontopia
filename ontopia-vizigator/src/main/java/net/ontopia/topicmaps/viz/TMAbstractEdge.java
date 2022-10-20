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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.Icon;

import net.ontopia.topicmaps.core.TopicIF;

import com.touchgraph.graphlayout.Edge;
import com.touchgraph.graphlayout.Node;
import com.touchgraph.graphlayout.TGPaintListener;
import com.touchgraph.graphlayout.TGPanel;
import java.util.function.Function;

/**
 * INTERNAL: Common abstract superclass for all edges representing
 * Topic Maps constructs.
 */
public abstract class TMAbstractEdge extends Edge
  implements VizTMObjectIF, TGPaintListener {

  protected int lineWeight = TMRoleEdge.DEFAULT_LINE_WEIGHT;
  protected int shape = TMRoleEdge.DEFAULT_SHAPE;
  protected Icon icon;
  protected Font font;
  protected Function<TopicIF, String> stringifier;
  protected boolean underMouse = false;
  static protected final int LOADING = 50;
  
  public static final int SHAPE_BOWTIE = 1;
  public static final int SHAPE_LINE = 2;
  public static int DEFAULT_SHAPE = SHAPE_BOWTIE;
  public static int DEFAULT_LINE_WEIGHT = 4;
  protected static int[] intBuffer = new int[64];
  protected boolean shouldDisplayRoleHoverHelp;
  protected TopicIF scopingTopic;

  /** 
   * setVisible is not supported in Vizigator.
   */
  @Override
  public final void setVisible(boolean visible) {
    // no-op
  }
  
  public TMAbstractEdge(Node f, Node t) {
    super(f, t);
    super.setVisible(true);
  }

  protected double calculateMidPointBetween(double from, double to) {
    return (from + to) / 2;
  }

  /**
   * Returns the mid point between the two assocaition targets.
   */
  public Point getMidPoint() {
    int index = getIndexInParents();
    if (index == 0) {
      return getSimpleMidPoint();
    }
    return getMiddleOf(getCurvedLine(index));
  }

  protected Point getFromRolePosition() {   
    int index = getIndexInParents();
    
    // Optimize out the general case
    if (index == 0) {
      return getMidPointBetween(from, getMidPoint());
    }
    
    return getMiddleOf(getFromCurve(index));
  }

  private Point getMidPointBetween(Node node, Point midPoint) {
    return new Point(((int)calculateMidPointBetween(node.drawx, midPoint.getX())),
        ((int)calculateMidPointBetween(node.drawy, midPoint.getY())));
  }

  private QuadCurve2D getToCurve(int index) {
    QuadCurve2D curve = getCurvedLine(index);
    QuadCurve2D result = new QuadCurve2D.Double();
    curve.subdivide(null,result);
    return result;
  }

  private QuadCurve2D getFromCurve(int index) {
    QuadCurve2D curve = getCurvedLine(index);
    QuadCurve2D result = new QuadCurve2D.Double();
    curve.subdivide(result,null);
    
    return result;
  }

  protected Point getToRolePosition() {
    int index = getIndexInParents();
    
    // Optimize out the general case
    if(index == 0) {
      return getMidPointBetween(to, getMidPoint());
    }
    
    return getMiddleOf(getToCurve(index));
  }

  private Point getMiddleOf(QuadCurve2D curve) {
    QuadCurve2D result = new QuadCurve2D.Double();
    curve.subdivide(result,null);
    return new Point((int)result.getP2().getX(), (int)result.getP2().getY());
  }

  private Point getSimpleMidPoint() {
    double midX = this.calculateMidPointBetween(this.from.drawx, this.to.drawx);
    double midY = this.calculateMidPointBetween(this.from.drawy, this.to.drawy);
    return new Point((int) midX, (int) midY);
  }

  @Override
  public void paintAfterEdges(Graphics g) {
    // Currently do nothing
  }

  protected void paintConnection(Graphics g) {
    // Optimize out the "normal" case where index = 0
    int index = getIndexInParents();
    Graphics2D g2D = (Graphics2D)g; 
    
    if (index == 0) {    
      switch (shape) {
      case TMRoleEdge.SHAPE_BOWTIE:
        this.paintBowTie(g2D);
        break;
      case TMRoleEdge.SHAPE_LINE:
        this.paintLine(g2D);
        break;
      }}
    else {
      switch (shape) {
      case TMRoleEdge.SHAPE_BOWTIE:
        this.paintCurvedBowTie(g2D, index);
        break;
      case TMRoleEdge.SHAPE_LINE:
        this.paintCurvedLine(g2D, index);
        break;  
      }
    }
  }

  protected void paintLine(Graphics2D g) {
    g.setColor(this.getColor());

    Stroke old = g.getStroke();
    g.setStroke(new BasicStroke(lineWeight));
    
    g.drawLine((int) from.drawx, (int) from.drawy, (int) to.drawx,
        (int) to.drawy);
    
    g.setStroke(old);
  }

  protected void paintCurvedLine(Graphics2D g, int index) {
    g.setColor(this.getColor());
    Stroke old = g.getStroke();
    g.setStroke(new BasicStroke(lineWeight));
    g.draw(getCurvedLine(index));
    g.setStroke(old);
  }

  protected QuadCurve2D getCurvedLine(int index) {
    double x1 = from.drawx;
    double x2 = to.drawx;
    double y1 = from.drawy;
    double y2 = to.drawy;
    double midx = calculateMidPointBetween(x1, x2);
    double midy = calculateMidPointBetween(y1, y2);
    
    int weight = index / 2;
    if (index % 2 == 1) {
      weight++;
      weight = -weight;
    }
    Dimension offset = calculateOffset(x1, x2, y1, y2, LOADING * weight);
    QuadCurve2D curve = new QuadCurve2D.Double(x1, y1,
        midx-offset.width, midy+offset.height,
        x2, y2);
    return curve;
  }

  protected void paintCurvedBowTie(Graphics2D g, int index) {    
    g.setColor(this.getColor());
    Shape path = getCurvedBowTie(index);    
    g.draw(path);
    g.fill(path);
    
  }

  protected GeneralPath getCurvedBowTie(int index) {
    double x1 = from.drawx;
    double x2 = to.drawx;
    double y1 = from.drawy;
    double y2 = to.drawy;
    double midx = calculateMidPointBetween(x1, x2);
    double midy = calculateMidPointBetween(y1, y2);
    int weight = index / 2;
    if (index % 2 == 1) {
      weight++;
      weight = -weight;
    }
    Dimension offset = calculateOffset(x1, x2, y1, y2, LOADING * weight);
    Dimension fromExtra = calculateOffset(x1, (int)midx - offset.width, y1, (int)midy + offset.height, getLineWeight());
    Dimension toExtra = calculateOffset(x2, (int)midx - offset.width, y2, (int)midy + offset.height, getLineWeight());
    GeneralPath path = new GeneralPath(GeneralPath.WIND_NON_ZERO);
    path.moveTo((int)x1-fromExtra.width, (int)y1+fromExtra.height);
    path.lineTo((int)x1+fromExtra.width, (int)y1-fromExtra.height);
    path.quadTo((float)midx-offset.width, (float)midy+offset.height, (float)x2-toExtra.width, (float)y2+toExtra.height);
    path.lineTo((int)x2+toExtra.width, (int)y2-toExtra.height);
    path.quadTo((float)midx-offset.width, (float)midy+offset.height, (float)x1-fromExtra.width, (float)y1+fromExtra.height);

    return path;
  }

  protected int getIndexInParents() {
    ArrayList possible = new ArrayList(); 
    
    for (Iterator iter = from.getEdges(); iter.hasNext();) {
      possible.add(iter.next());
    }
    
    if (possible.size() > intBuffer.length) {
      intBuffer = new int[possible.size()];
    }
    
    int index = 0;
    
    for (Iterator iter = to.getEdges(); iter.hasNext();) {
      TMAbstractEdge edge = (TMAbstractEdge) iter.next();
      if (possible.contains(edge)) {
        intBuffer[index] = System.identityHashCode(edge);
        index++;
      }
    }
    int[] result = new int[index];
    System.arraycopy(intBuffer, 0, result, 0, index);
    Arrays.sort(result);
    int offset = Arrays.binarySearch(result, System.identityHashCode(this));
    // If the total size is even, increase the index.
    if (result.length % 2 == 0) {
      offset++;
    }
    return offset;
  }

  protected void paintBowTie(Graphics2D g) {
    double x1 = from.drawx;
    double x2 = to.drawx;
    double y1 = from.drawy;
    double y2 = to.drawy;
  
    double midx = calculateMidPointBetween(x1, x2);
    double midy = calculateMidPointBetween(y1, y2);
  
    Dimension offset = calculateOffset(x1, x2, y1, y2, getLineWeight());
  
    g.setColor(getColor());
  
    int xPoints[] = new int[3];
    xPoints[0] = (int) midx;
    int yPoints[] = new int[3];
    yPoints[0] = (int) midy;
  
    xPoints[1] = (int) (x1 - offset.width);
    yPoints[1] = (int) (y1 + offset.height);
    xPoints[2] = (int) (x1 + offset.width);
    yPoints[2] = (int) (y1 - offset.height);
  
    g.fillPolygon(xPoints, yPoints, 3);
  
    xPoints[1] = (int) (x2 - offset.width);
    yPoints[1] = (int) (y2 + offset.height);
    xPoints[2] = (int) (x2 + offset.width);
    yPoints[2] = (int) (y2 - offset.height);
  
    g.fillPolygon(xPoints, yPoints, 3);
  
    g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
  
  }

  protected Dimension calculateOffset(double x1, double x2, double y1, double y2,
                                      int weight) {
    double beta = calculateBeta(x1, x2, y1, y2);

    return new Dimension((int) (weight * Math.cos(beta)),
        (int) (weight * Math.sin(beta)));
  }

  private double calculateBeta(double x1, double x2, double y1, double y2) {
    double deltaX = (x2 - x1);
    double deltaY = (y2 - y1);
    double alpha = 0;
    if (deltaY != 0 && deltaX != 0) {
      alpha = Math.atan(deltaY / deltaX);
    }
    double beta = (Math.PI / 2) - alpha;
    return beta;
  }

  @Override
  public void paintLast(Graphics g) {
    if (underMouse) {
      this.paintToolTip(g);
    }
  }

  protected void paintToolTip(Graphics g) {
    // Default is to do nothing
  }

  @Override
  public void setLineWeight(int lineWeight) {
    this.lineWeight = lineWeight;
  }

  public int getShape() {
    return this.shape;
  }

  @Override
  public void setShape(int shape) {
    this.shape = shape;
  }

  public Icon getIcon() {
    return this.icon;
  }

  @Override
  public void setIcon(Icon icon) {
    this.icon = icon;
  }

  public Font getFont() {
    return this.font;
  }

  @Override
  public void setFont(Font font) {
    this.font = font;
  }

  @Override
  public void addTo(TGPanel tgpanel) {
    tgpanel.addEdge(this);
  }

  public int getLineWeight() {
    return this.lineWeight;
  }

  @Override
  public void paintFirst(Graphics g) {
    // Currently do nothing
  }

  /**
   * @param g -
   *               The graphic context for the drawing operation.
   * @param string -
   *               The String to be rendered.
   * @param x -
   *               The x coordinate where the String should be positioned.
   * @param y -
   *               The y coordinate where the String should be positioned. NOTE: The text is <b>centered </b> over the given coordinates.
   */
  protected void paintToolTipText(Graphics g, String string, int x, int y) {

    g.setFont(this.getFont());
    FontMetrics fontMetrics = g.getFontMetrics();

    int a = fontMetrics.getAscent();
    int h = a + fontMetrics.getDescent();
    int w = fontMetrics.stringWidth(string);

    int xPosition = x - (w / 2);
    int yPosition = y - (h / 2);

    // Draw the background

    Color c = this.getColor();
    g.setColor(c);

    int r = h / 2;
    int vPad = h / 8;
    int hPad = h / 4;

    g.fillRoundRect(xPosition - hPad, yPosition - vPad, w + (2 * hPad), h
        + (2 * vPad), r, r);

    //Draw a defined edge to the popup
    g.setColor(TMTopicNode.textColourForBackground(c));
    g.drawRoundRect(xPosition - hPad, yPosition - vPad, w + (2 * hPad), h
        + (2 * vPad), r, r);

    // Draw the text
    g.drawString(string, xPosition, yPosition + a);
  }

  @Override
  public void paint(Graphics g, TGPanel tgPanel) {
    underMouse = (tgPanel.getMouseOverE() == this);
  
    if (intersects(tgPanel.getSize())) {
      paintConnection(g);
    }
  
    if (icon != null) {
      Point mid = this.getMidPoint();
      icon.paintIcon(tgPanel, g, mid.x - (icon.getIconHeight() / 2), mid.y
          - (icon.getIconHeight() / 2));
    } 
  }

  @Override
  public void deleteFrom(TGPanel tgpanel) {
    tgpanel.deleteEdge(this);
  }

  protected void paintTypeToolTip(Graphics g) {
    this.paintToolTipText(g, this.getMainHoverHelpText(), getMidPoint());
  }

  protected void paintToolTipText(Graphics g, String text, Point aPoint) {
    paintToolTipText(g, text, aPoint.x, aPoint.y);    
  }

  protected String getMainHoverHelpText() {

    return Messages.getString("Viz.Unknown");
  }

  public void setShouldDisplayRoleHoverHelp(boolean newValue) {
    shouldDisplayRoleHoverHelp = newValue;
  }

  @Override
  public boolean isEdge() {
    return true;
  }

  @Override
  public boolean isAssociation() {
    return false;
  }

  public List getTargetsFrom(Node find) {
    return Collections.singletonList(this.getOtherEndpt(find));
  }
  
  public Function<TopicIF, String> getStringifier() {
    return this.stringifier;
  }
  
  @Override
  public void setScopingTopic(TopicIF aTopic) {
    this.scopingTopic = aTopic;
    this.setStringifier(VizUtils.stringifierFor(aTopic));
  }
  
  protected void setStringifier(Function<TopicIF, String> aStringifier) {
    this.stringifier = aStringifier;   
  }

  @Override
  public TopicIF getTopicMapType() {
    return null;
  }
  
  protected Shape getDisplayShape() {
    int index = getIndexInParents();
    
    // Optimize out the "normal" case where index = 0
    if (index != 0){
      switch (shape) {
      case TMRoleEdge.SHAPE_BOWTIE:
        return this.getCurvedBowTie(index);
      case TMRoleEdge.SHAPE_LINE:
        return this.getCurvedLine(index);
      }}
    return null;
  }
  
  @Override
  public double distFromPoint(double x, double y) {
    Shape shape = getDisplayShape();
    if (shape == null) {
      return super.distFromPoint(x, y);
    }
    
    // Bit of a hack, but just because TG does something rather
    // stupid here anyway.
    if (shape.intersects(x -2, y-2, 4, 4)) {
      return 0;
    }
    return 1000;
  }
}
