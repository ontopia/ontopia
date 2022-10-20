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
/*
 * NOTE! ExtendedTGPanel is a version of TGPanel that supports using an image
 * background in the VizPanel. This class contains Java 1.4 code for reading in
 * and image, but this code has been commented out, since we go for being 1.3
 * compatible. If this class should be used in the distribution, a 1.3 way must
 * be found for reading in the image.
 * 
 * 
 * 
 * 
 * TouchGraph LLC. Apache-Style Software License
 *
 *
 * Copyright (c) 2001-2002 Alexander Shapiro. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by 
 *        TouchGraph LLC (http://www.touchgraph.com/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "TouchGraph" or "TouchGraph LLC" must not be used to endorse 
 *    or promote products derived from this software without prior written 
 *    permission.  For written permission, please contact 
 *    alex@touchgraph.com
 *
 * 5. Products derived from this software may not be called "TouchGraph",
 *    nor may "TouchGraph" appear in their name, without prior written
 *    permission of alex@touchgraph.com.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL TOUCHGRAPH OR ITS CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR 
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 *
 */

package net.ontopia.topicmaps.viz;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFrame;

import net.ontopia.utils.OntopiaRuntimeException;

import com.touchgraph.graphlayout.Edge;
import com.touchgraph.graphlayout.GraphListener;
import com.touchgraph.graphlayout.LocalityUtils;
import com.touchgraph.graphlayout.Node;
import com.touchgraph.graphlayout.TGAbstractLens;
import com.touchgraph.graphlayout.TGException;
import com.touchgraph.graphlayout.TGLayout;
import com.touchgraph.graphlayout.TGLensSet;
import com.touchgraph.graphlayout.TGPaintListener;
import com.touchgraph.graphlayout.TGPanel;
import com.touchgraph.graphlayout.TGPoint2D;
import com.touchgraph.graphlayout.graphelements.GraphEltSet;
import com.touchgraph.graphlayout.graphelements.ImmutableGraphEltSet;
import com.touchgraph.graphlayout.graphelements.TGForEachEdge;
import com.touchgraph.graphlayout.graphelements.TGForEachNode;
import com.touchgraph.graphlayout.graphelements.VisibleLocality;
import com.touchgraph.graphlayout.interaction.GLEditUI;
import com.touchgraph.graphlayout.interaction.TGAbstractClickUI;

/**
 * TGPanel contains code for drawing the graph, and storing which nodes are
 * selected, and which ones the mouse is over.
 * 
 * It houses methods to activate TGLayout, which performs dynamic layout.
 * Whenever the graph is moved, or repainted, TGPanel fires listner methods on
 * associated objects.
 * 
 * <p>
 * <b> Parts of this code build upon Sun's Graph Layout example.
 * http://java.sun.com/applets/jdk/1.1/demo/GraphLayout/Graph.java </b>
 * </p>
 * 
 * @author Alexander Shapiro
 * @author Murray Altheim (2001-11-06; 2002-01-14 cleanup)
 */
public class ExtendedTGPanel extends TGPanel {

  // static variables for use within the package

  public static Color BACK_COLOR = Color.white;

  // ....

  private GraphEltSet completeEltSet;
  private VisibleLocality visibleLocality;
  private LocalityUtils localityUtils;
  protected BasicMouseMotionListener basicMML;

  protected Edge mouseOverE; // mouseOverE is the edge the mouse is over

  protected Node mouseOverN; // mouseOverN is the node the mouse is over

  // If true, then don't change mouseOverN or mouseOverE
  protected boolean maintainMouseOver = false;

  protected Node select;

  private Node dragNode; // Node currently being dragged

  protected Point mousePos; // Mouse location, updated in the
                            // mouseMotionListener

  private Image offscreen;

  private Dimension offscreensize;

  private Graphics offgraphics;

  private Vector graphListeners;

  private Vector paintListeners;

  // Converts between a nodes visual position (drawx, drawy), and its absolute
  // position (x,y).
  private TGLensSet tgLensSet;

  private TGPanel.AdjustOriginLens adjustOriginLens;

  private TGPanel.SwitchSelectUI switchSelectUI;

  public Image image;

  private TGPoint2D topLeftDraw = null;

  private TGPoint2D bottomRightDraw = null;

  // ............

  /**
   * Default constructor.
   */
  public ExtendedTGPanel(String imageSource) {
    super();
    this.image = getImage(imageSource);
    setLayout(null);

    setGraphEltSet(new GraphEltSet());
    addMouseListener(new BasicMouseListener());
    basicMML = new BasicMouseMotionListener();
    addMouseMotionListener(basicMML);

    graphListeners = new Vector();
    paintListeners = new Vector();

    adjustOriginLens = super.getAdjustOriginLens();
    switchSelectUI = super.getSwitchSelectUI();

    TGLayout tgLayout = new TGLayout(this);
    setTGLayout(tgLayout);
    tgLayout.start();
    setGraphEltSet(new GraphEltSet());
  }

  @Override
  public void setLensSet(TGLensSet lensSet) {
    tgLensSet = lensSet;
  }

  @Override
  public void setTGLayout(TGLayout tgl) {
    tgLayout = tgl;
  }

  @Override
  public void setGraphEltSet(GraphEltSet ges) {
    completeEltSet = ges;
    visibleLocality = new VisibleLocality(completeEltSet);
    localityUtils = new LocalityUtils(visibleLocality, this);
  }

  @Override
  public TGPanel.AdjustOriginLens getAdjustOriginLens() {
    return adjustOriginLens;
  }

  @Override
  public TGPanel.SwitchSelectUI getSwitchSelectUI() {
    return switchSelectUI;
  }

  // color and font setters ......................

  @Override
  public void setBackColor(Color color) {
    BACK_COLOR = color;
  }

  // Node manipulation ...........................

  /**
   * Returns an Iterator over all nodes in the complete graph.
   */
  @Override
  public Iterator getAllNodes() {
    return completeEltSet.getNodes();
  }

  /**
   * Return the current visible locality.
   */
  @Override
  public ImmutableGraphEltSet getGES() {
    return visibleLocality;
  }

  /**
   * Returns the current node count.
   */
  @Override
  public int getNodeCount() {
    return completeEltSet.nodeCount();
  }

  /**
   * Returns the current node count within the VisibleLocality.
   * @deprecated this method has been replaced by the
   *             <tt>visibleNodeCount()</tt> method.
   */
  @Override
  public int nodeNum() {
    return visibleLocality.nodeCount();
  }

  /**
   * Returns the current node count within the VisibleLocality.
   */
  @Override
  public int visibleNodeCount() {
    return visibleLocality.nodeCount();
  }

  /**
   * Return the Node whose ID matches the String <tt>id</tt>, null if no
   * match is found.
   * 
   * @param id
   *          The ID identifier used as a query.
   * @return The Node whose ID matches the provided 'id', null if no match is
   *         found.
   */
  @Override
  public Node findNode(String id) {
    if (id == null) {
      return null; // ignore
    }
    return completeEltSet.findNode(id);
  }

  /**
   * Return a Collection of all Nodes whose label matches the String
   * <tt>label</tt>, null if no match is found.
   */
  @Override
  public Collection findNodesByLabel(String label) {
    if (label == null) {
      return null; // ignore
    }
    return completeEltSet.findNodesByLabel(label);
  }

  /**
   * Return the first Nodes whose label contains the String <tt>substring</tt>,
   * null if no match is found.
   * @param substring
   *          The Substring used as a query.
   */
  @Override
  public Node findNodeLabelContaining(String substring) {
    if (substring == null) {
      return null; // ignore
    }
    return completeEltSet.findNodeLabelContaining(substring);
  }

  /**
   * Adds a Node, with its ID and label being the current node count plus 1.
   * @see com.touchgraph.graphlayout.Node
   */
  @Override
  public Node addNode() throws TGException {
    String id = String.valueOf(getNodeCount() + 1);
    return addNode(id, null);
  }

  /**
   * Adds a Node, provided its label. The node is assigned a unique ID.
   * @see com.touchgraph.graphlayout.graphelements.GraphEltSet
   */
  @Override
  public Node addNode(String label) throws TGException {
    return addNode(null, label);
  }

  /**
   * Adds a Node, provided its ID and label.
   * @see com.touchgraph.graphlayout.Node
   */
  @Override
  public Node addNode(String id, String label) throws TGException {
    Node node;
    if (label == null) {
      node = new Node(id);
    } else {
      node = new Node(id, label);
    }

    // The addNode() call should probably take a position, this just sets it
    // at 0,0
    updateDrawPos(node);

    addNode(node);
    return node;
  }

  /**
   * Add the Node <tt>node</tt> to the visibleLocality, checking for ID
   * uniqueness.
   */
  @Override
  public void addNode(final Node node) throws TGException {
    synchronized (localityUtils) {
      visibleLocality.addNode(node);
      resetDamper();
    }
  }

  /**
   * Remove the Node object matching the ID <code>id</code>, returning true
   * if the deletion occurred, false if a Node matching the ID does not exist
   * (or if the ID value was null).
   * @param id The ID identifier used as a query.
   * @return true if the deletion occurred.
   */
  @Override
  public boolean deleteNodeById(String id) {
    if (id == null) {
      return false; // ignore
    }

    Node node = findNode(id);

    if (node == null) {
      return false;
    } else {
      return deleteNode(node);
    }
  }

  @Override
  public boolean deleteNode(Node node) {
    synchronized (localityUtils) {
      if (visibleLocality.deleteNode(node)) {
        // delete from visibleLocality, *AND completeEltSet
        if (node.equals(select)) {
          clearSelect();
        }
        resetDamper();
        return true;
      }
      return false;
    }
  }

  @Override
  public void clearAll() {
    synchronized (localityUtils) {
      visibleLocality.clearAll();
    }
  }

  @Override
  public Node getSelect() {
    return select;
  }

  @Override
  public Node getMouseOverN() {
    return mouseOverN;
  }

  @Override
  public synchronized void setMouseOverN(Node node) {
    if (dragNode != null || maintainMouseOver) {
      return; // So you don't accidentally switch nodes while dragging
    }

    if (!mouseOverN.equals(node)) {
      mouseOverN = node;
    }
  }

  // Edge manipulation ...........................

  /** Returns an Iterator over all edges in the complete graph. */
  @Override
  public Iterator getAllEdges() {
    return completeEltSet.getEdges();
  }

  @Override
  public void deleteEdge(Edge edge) {
    synchronized (localityUtils) {
      visibleLocality.deleteEdge(edge);
      resetDamper();
    }
  }

  @Override
  public void deleteEdge(Node from, Node to) {
    synchronized (localityUtils) {
      visibleLocality.deleteEdge(from, to);
    }
  }

  /**
   * Returns the current edge count in the complete graph.
   */
  @Override
  public int getEdgeCount() {
    return completeEltSet.edgeCount();
  }

  /**
   * Return the number of Edges in the Locality.
   * 
   * @deprecated this method has been replaced by the
   *             <tt>visibleEdgeCount()</tt> method.
   */
  @Override
  public int edgeNum() {
    return visibleLocality.edgeCount();
  }

  /**
   * Return the number of Edges in the Locality.
   */
  @Override
  public int visibleEdgeCount() {
    return visibleLocality.edgeCount();
  }

  @Override
  public Edge findEdge(Node f, Node t) {
    return visibleLocality.findEdge(f, t);
  }

  @Override
  public void addEdge(Edge e) {
    synchronized (localityUtils) {
      visibleLocality.addEdge(e);
      resetDamper();
    }
  }

  @Override
  public Edge addEdge(Node f, Node t, int tens) {
    synchronized (localityUtils) {
      return visibleLocality.addEdge(f, t, tens);
    }
  }

  @Override
  public Edge getMouseOverE() {
    return mouseOverE;
  }

  @Override
  public synchronized void setMouseOverE(Edge edge) {
    if (dragNode != null || maintainMouseOver) {
      return; // No funny business while dragging
    }
    if (!mouseOverE.equals(edge)) {
      mouseOverE = edge;
    }
  }

  // miscellany ..................................

  protected class AdjustOriginLens extends TGAbstractLens {
    @Override
    protected void applyLens(TGPoint2D p) {
      p.x = p.x + ExtendedTGPanel.this.getSize().width / 2;
      p.y = p.y + ExtendedTGPanel.this.getSize().height / 2;
    }

    @Override
    protected void undoLens(TGPoint2D p) {
      p.x = p.x - ExtendedTGPanel.this.getSize().width / 2;
      p.y = p.y - ExtendedTGPanel.this.getSize().height / 2;
    }
  }

  public class SwitchSelectUI extends TGAbstractClickUI {
    @Override
    public void mouseClicked(MouseEvent e) {
      if (mouseOverN != null) {
        if (!mouseOverN.equals(select)) {
          setSelect(mouseOverN);
        } else {
          clearSelect();
        }
      }
    }
  }

  private void fireMovedEvent() {
    Vector listeners;

    // The following condition was added because the synchronized line below
    // would sometimes cause a NullPointerException stack trace to be printed
    // in the console.
    if (graphListeners != null) {
      synchronized (this) {
        listeners = (Vector) graphListeners.clone();
      }

      for (int i = 0; i < listeners.size(); i++) {
        GraphListener gl = (GraphListener) listeners.elementAt(i);
        gl.graphMoved();
      }
    }
  }

  @Override
  public void fireResetEvent() {
    Vector listeners;

    synchronized (this) {
      listeners = (Vector) graphListeners.clone();
    }

    for (int i = 0; i < listeners.size(); i++) {
      GraphListener gl = (GraphListener) listeners.elementAt(i);
      gl.graphReset();
    }
  }

  @Override
  public synchronized void addGraphListener(GraphListener gl) {
    graphListeners.addElement(gl);
  }

  @Override
  public synchronized void removeGraphListener(GraphListener gl) {
    graphListeners.removeElement(gl);
  }

  @Override
  public synchronized void addPaintListener(TGPaintListener pl) {
    paintListeners.addElement(pl);
  }

  @Override
  public synchronized void removePaintListener(TGPaintListener pl) {
    paintListeners.removeElement(pl);
  }

  @Override
  public void setMaintainMouseOver(boolean maintain) {
    maintainMouseOver = maintain;
  }

  @Override
  public void clearSelect() {
    if (select != null) {
      select = null;
      repaint();
    }
  }

  /**
   * A convenience method that selects the first node of a graph, so that hiding
   * works.
   */
  @Override
  public void selectFirstNode() {
    setSelect(getGES().getFirstNode());
  }

  @Override
  public void setSelect(Node node) {
    if (node != null) {
      select = node;
      repaint();
    } else if (node == null) {
      clearSelect();
    }
  }

  @Override
  public void multiSelect(TGPoint2D from, TGPoint2D to) {
    final double minX, minY, maxX, maxY;

    if (from.x > to.x) {
      maxX = from.x;
      minX = to.x;
    } else {
      minX = from.x;
      maxX = to.x;
    }
    if (from.y > to.y) {
      maxY = from.y;
      minY = to.y;
    } else {
      minY = from.y;
      maxY = to.y;
    }

    final Vector selectedNodes = new Vector();

    TGForEachNode fen = new TGForEachNode() {
      @Override
      public void forEachNode(Node node) {
        double x = node.drawx;
        double y = node.drawy;
        if (x > minX && x < maxX && y > minY && y < maxY) {
          selectedNodes.addElement(node);
        }
      }
    };

    visibleLocality.forAllNodes(fen);

    if (selectedNodes.size() > 0) {
      int r = (int) (Math.random() * selectedNodes.size());
      setSelect((Node) selectedNodes.elementAt(r));
    } else {
      clearSelect();
    }
  }

  @Override
  public void updateLocalityFromVisibility() throws TGException {
    visibleLocality.updateLocalityFromVisibility();
  }

  @Override
  public void setLocale(Node node, int radius, int maxAddEdgeCount,
      int maxExpandEdgeCount, boolean unidirectional) throws TGException {
    localityUtils.setLocale(node, radius, maxAddEdgeCount, maxExpandEdgeCount,
        unidirectional);
  }

  @Override
  public void fastFinishAnimation() {
    // Quickly wraps up the add node animation
    localityUtils.fastFinishAnimation();
  }

  @Override
  public void setLocale(Node node, int radius) throws TGException {
    localityUtils.setLocale(node, radius);
  }

  @Override
  public void expandNode(Node node) {
    localityUtils.expandNode(node);
  }

  @Override
  public void hideNode(Node hideNode) {
    localityUtils.hideNode(hideNode);
  }

  @Override
  public void collapseNode(Node collapseNode) {
    localityUtils.collapseNode(collapseNode);
  }

  @Override
  public void hideEdge(Edge hideEdge) {
    visibleLocality.removeEdge(hideEdge);
    if (mouseOverE.equals(hideEdge)) {
      setMouseOverE(null);
    }
    resetDamper();
  }

  @Override
  public void setDragNode(Node node) {
    dragNode = node;
    super.setDragNode(node);
  }

  @Override
  public Node getDragNode() {
    return dragNode;
  }

  @Override
  public Point getMousePos() {
    return mousePos;
  }

  /** Start and stop the damper. Should be placed in the TGPanel too. */
  @Override
  public void startDamper() {
    if (tgLayout != null) {
      tgLayout.startDamper();
    }
  }

  @Override
  public void stopDamper() {
    if (tgLayout != null) {
      tgLayout.stopDamper();
    }
  }

  /** Makes the graph mobile, and slowly slows it down. */
  @Override
  public void resetDamper() {
    if (tgLayout != null) {
      tgLayout.resetDamper();
    }
  }

  /** Gently stops the graph from moving */
  @Override
  public void stopMotion() {
    if (tgLayout != null) {
      tgLayout.stopMotion();
    }
  }

  class BasicMouseListener extends MouseAdapter {

    @Override
    public void mouseEntered(MouseEvent e) {
      addMouseMotionListener(basicMML);
    }

    @Override
    public void mouseExited(MouseEvent e) {
      removeMouseMotionListener(basicMML);
      mousePos = null;
      setMouseOverN(null);
      setMouseOverE(null);
      repaint();
    }
  }

  class BasicMouseMotionListener implements MouseMotionListener {
    @Override
    public void mouseDragged(MouseEvent e) {
      mousePos = e.getPoint();
      findMouseOver();
      try {
        // An attempt to make the cursor flicker less
        Thread.currentThread().sleep(6);
      } catch (InterruptedException ex) {
      }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
      mousePos = e.getPoint();
      synchronized (this) {
        Edge oldMouseOverE = mouseOverE;
        Node oldMouseOverN = mouseOverN;
        findMouseOver();

        if (!oldMouseOverE.equals(mouseOverE) || !oldMouseOverN.equals(mouseOverN)) {
          repaint();
        }
      }
    }
  }

  @Override
  protected synchronized void findMouseOver() {

    if (mousePos == null) {
      setMouseOverN(null);
      setMouseOverE(null);
      return;
    }

    final int mpx = mousePos.x;
    final int mpy = mousePos.y;

    final Node[] monA = new Node[1];
    final Edge[] moeA = new Edge[1];

    TGForEachNode fen = new TGForEachNode() {

      double minoverdist = 100; // Kind of a hack (see second if statement)

      // Nodes can be as wide as 200 (=2*100)
      @Override
      public void forEachNode(Node node) {
        double x = node.drawx;
        double y = node.drawy;

        double dist = Math.sqrt((mpx - x) * (mpx - x) + (mpy - y) * (mpy - y));

        if ((dist < minoverdist) && node.containsPoint(mpx, mpy)) {
          minoverdist = dist;
          monA[0] = node;
        }
      }
    };
    visibleLocality.forAllNodes(fen);

    TGForEachEdge fee = new TGForEachEdge() {

      double minDist = 8; // Tangential distance to the edge

      double minFromDist = 1000; // Distance to the edge's "from" node

      @Override
      public void forEachEdge(Edge edge) {
        double x = edge.from.drawx;
        double y = edge.from.drawy;
        double dist = edge.distFromPoint(mpx, mpy);
        if (dist < minDist) {
          // Set the over edge to the edge with the minimun tangential
          // distance
          minDist = dist;
          minFromDist = Math
              .sqrt((mpx - x) * (mpx - x) + (mpy - y) * (mpy - y));
          moeA[0] = edge;
        } else if (dist == minDist) {
          // If tangential distances are identical, chose
          // the edge whose "from" node is closest.
          double fromDist = Math.sqrt((mpx - x) * (mpx - x) + (mpy - y)
              * (mpy - y));
          if (fromDist < minFromDist) {
            minFromDist = fromDist;
            moeA[0] = edge;
          }
        }
      }
    };
    visibleLocality.forAllEdges(fee);

    setMouseOverN(monA[0]);
    if (monA[0] == null) {
      setMouseOverE(moeA[0]);
    } else {
      setMouseOverE(null);
    }
  }

  @Override
  public TGPoint2D getTopLeftDraw() {
    return new TGPoint2D(topLeftDraw);
  }

  @Override
  public TGPoint2D getBottomRightDraw() {
    return new TGPoint2D(bottomRightDraw);
  }

  @Override
  public TGPoint2D getCenter() {
    return tgLensSet.convDrawToReal(getSize().width / 2, getSize().height / 2);
  }

  @Override
  public TGPoint2D getDrawCenter() {
    return new TGPoint2D(getSize().width / 2, getSize().height / 2);
  }

  @Override
  public void updateGraphSize() {
    if (topLeftDraw == null) {
      topLeftDraw = new TGPoint2D(0, 0);
    }
    if (bottomRightDraw == null) {
      bottomRightDraw = new TGPoint2D(0, 0);
    }

    TGForEachNode fen = new TGForEachNode() {
      boolean firstNode = true;

      @Override
      public void forEachNode(Node node) {
        if (firstNode) { // initialize topRight + bottomLeft
          topLeftDraw.setLocation(node.drawx, node.drawy);
          bottomRightDraw.setLocation(node.drawx, node.drawy);
          firstNode = false;
        } else { // Standard max and min finding
          topLeftDraw.setLocation(Math.min(node.drawx, topLeftDraw.x), Math
              .min(node.drawy, topLeftDraw.y));
          bottomRightDraw.setLocation(Math.max(node.drawx, bottomRightDraw.x),
              Math.max(node.drawy, bottomRightDraw.y));
        }
      }
    };

    visibleLocality.forAllNodes(fen);
  }

  @Override
  public synchronized void processGraphMove() {
    updateDrawPositions();
    updateGraphSize();
  }

  @Override
  public synchronized void repaintAfterMove() {
    // Called by TGLayout + others to indicate that graph has moved
    processGraphMove();
    findMouseOver();
    fireMovedEvent();
    repaint();
  }

  @Override
  public void updateDrawPos(Node node) {
    // sets the visual position from the real position
    TGPoint2D p = tgLensSet.convRealToDraw(node.x, node.y);
    node.drawx = p.x;
    node.drawy = p.y;
  }

  @Override
  public void updatePosFromDraw(Node node) {
    // sets the real position from the visual position
    TGPoint2D p = tgLensSet.convDrawToReal(node.drawx, node.drawy);
    node.x = p.x;
    node.y = p.y;
  }

  @Override
  public void updateDrawPositions() {
    TGForEachNode fen = new TGForEachNode() {
      @Override
      public void forEachNode(Node node) {
        updateDrawPos(node);
      }
    };
    visibleLocality.forAllNodes(fen);
  }

  @Override
  public synchronized void paint(Graphics g) {
    update(g);
  }

  public Image getImage(String imageSource) {
    try {
      URL sourceURL = new URL(imageSource);
      if (sourceURL.getProtocol().equals("file") &&
          !(new File(sourceURL.getPath())).exists()) {
        ErrorDialog.showError(this, "File not found: " + imageSource);
        return null;
      }
      
      Image other = Toolkit.getDefaultToolkit().createImage(sourceURL);
      MediaTracker tracker = new MediaTracker(this);
      int imageId = 0;
      tracker.addImage(other, imageId);
      
      tracker.waitForID(0);
      boolean isError = tracker.isErrorID(imageId);
      if (isError) {
        ErrorDialog.showError(this, 
            "There were problems reading the background image: " + 
                imageSource);
        return null;
      }
      
      return other;
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    } catch (InterruptedException ie) {
      throw new OntopiaRuntimeException("InterruptedException caused during" +
          " wallpaper_image error check.", ie);
    }
  }

  @Override
  public synchronized void update(Graphics g) {
    Dimension d = getSize();
    if ((offscreen == null) || (d.width != offscreensize.width)
        || (d.height != offscreensize.height)) {
      offscreen = createImage(d.width, d.height);
      offscreensize = d;
      offgraphics = offscreen.getGraphics();

      processGraphMove();
      findMouseOver();
      fireMovedEvent();
    }

    offgraphics.setColor(BACK_COLOR);
    offgraphics.fillRect(0, 0, d.width, d.height);
    offgraphics.drawImage(image, 0, 0, null);

    synchronized (this) {
      paintListeners = (Vector) paintListeners.clone();
    }

    for (int i = 0; i < paintListeners.size(); i++) {
      TGPaintListener pl = (TGPaintListener) paintListeners.elementAt(i);
      pl.paintFirst(offgraphics);
    }

    TGForEachEdge fee = new TGForEachEdge() {
      @Override
      public void forEachEdge(Edge edge) {
        edge.paint(offgraphics, ExtendedTGPanel.this);
      }
    };

    visibleLocality.forAllEdges(fee);

    for (int i = 0; i < paintListeners.size(); i++) {
      TGPaintListener pl = (TGPaintListener) paintListeners.elementAt(i);
      pl.paintAfterEdges(offgraphics);
    }

    TGForEachNode fen = new TGForEachNode() {
      @Override
      public void forEachNode(Node node) {
        node.paint(offgraphics, ExtendedTGPanel.this);
      }
    };

    visibleLocality.forAllNodes(fen);

    if (mouseOverE != null) {
      // Make the edge the mouse is over appear on top.
      mouseOverE.paint(offgraphics, this);
      mouseOverE.from.paint(offgraphics, this);
      mouseOverE.to.paint(offgraphics, this);
    }

    if (select != null) { // Make the selected node appear on top.
      select.paint(offgraphics, this);
    }

    if (mouseOverN != null) {
      // Make the node the mouse is over appear on top.
      mouseOverN.paint(offgraphics, this);
    }

    for (int i = 0; i < paintListeners.size(); i++) {
      TGPaintListener pl = (TGPaintListener) paintListeners.elementAt(i);
      pl.paintLast(offgraphics);
    }

    // Paint any components that have been added to this panel
    paintComponents(offgraphics);
    g.drawImage(offscreen, 0, 0, null);

  }

  public static void main(String[] args) {
    JFrame frame;
    frame = new JFrame("TGPanel");
    TGPanel tgPanel = new TGPanel();
    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });

    TGLensSet tgls = new TGLensSet();
    tgls.addLens(tgPanel.getAdjustOriginLens());
    tgPanel.setLensSet(tgls);
    try {
      tgPanel.addNode(); // Add a starting node.
    } catch (TGException tge) {
      System.err.println(tge.getMessage());
    }
    tgPanel.setVisible(true);
    new GLEditUI(tgPanel).activate();
    frame.getContentPane().add("Center", tgPanel);
    frame.setSize(500, 500);
    frame.setVisible(true);
  }
} // end com.touchgraph.graphlayout.TGPanel
