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
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.impl.remote.RemoteTopicMapStore;
import net.ontopia.topicmaps.utils.tmrap.TopicIndexIF;
import net.ontopia.topicmaps.viz.TMClassInstanceAssociation.Key;
import net.ontopia.topicmaps.viz.VizController.VizHoverHelpManager;
import net.ontopia.utils.OntopiaRuntimeException;

import com.touchgraph.graphlayout.Edge;
import com.touchgraph.graphlayout.LocalityUtils;
import com.touchgraph.graphlayout.Node;
import com.touchgraph.graphlayout.TGException;
import com.touchgraph.graphlayout.TGPaintListener;
import com.touchgraph.graphlayout.TGPanel;
import com.touchgraph.graphlayout.graphelements.GESUtils;
import com.touchgraph.graphlayout.graphelements.GraphEltSet;
import com.touchgraph.graphlayout.graphelements.Locality;
import java.util.function.Function;

/**
 * INTERNAL: Maintains the TouchGraph view of the topic map.
 */
public class TopicMapView {
  private TopicMapIF topicmap;
  private TGPanel tgPanel;
  protected VizTopicMapConfigurationManager configman;
  protected VizController controller;
  private ClassInstanceIndexIF typeix;

  private int locality;
  private ArrayList nodesByType;
  private ArrayList nodeTypeIndex;
  protected ArrayList newNodes = new ArrayList();
  
  private TMAbstractNode ghost = null;
  
  private int associationScopeFilterStrictness = 
      VizTopicMapConfigurationManager.SHOW_ALL_ASSOCIATION_SCOPES;

  // Due to object redirection as a result of TopicMap merging in the "remote"
  // implementation, we cannot use Topics or Assocaitions as keys in HashMaps.
  // Therefore we have a list of types, the index of which gives the index of
  // the corresponding type collection in @objectsByType list.
  protected ArrayList objectTypeIndex;
  protected ArrayList objectsByType;
  protected TopicIF currentScopingTopic;
  
  protected Debug debug;

  protected Collection nodesUpdateCount;
  
  public PerformanceStat stat;
  public PerformanceStat stat1;

  protected MotionKiller motionKiller;
  protected VizigatorUser vizigatorUser;

  protected List foregroundQueue = new ArrayList();
  private int maxTopicNameLength = VizTopicMapConfigurationManager
      .DEFAULT_MAX_TOPIC_NAME_LENGTH;
  
  public static int NODE_LOCALITY = 0;
  public static int EDGE_LOCALITY = 1;

  /**
   * Creates the view and updates the TGPanel to show the new view. The TGPanel
   * may be showing an old view.
   */
  public TopicMapView(VizController controller, TopicMapIF topicmap,
      TGPanel tgPanel, VizTopicMapConfigurationManager configman) {
    this.controller = controller;
    this.topicmap = topicmap;
    this.tgPanel = tgPanel;
    this.configman = configman;
    debug = new Debug();

    nodesUpdateCount = new HashSet();
    
    stat = new PerformanceStat("loadNode");
    stat.init();
    stat1 = new PerformanceStat("createAssociations");
    stat1.init();
    
    motionKiller = new MotionKiller(getTGPanel(), 1000);
    vizigatorUser = new VizigatorUser(controller, 300);
    maxTopicNameLength = this.configman.getMaxTopicNameLength();

    init();
  }

  private void init() {
    typeix = (ClassInstanceIndexIF) topicmap.getIndex(
            "net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");

    locality = controller.getDefaultLocality();

    objectsByType = new ArrayList();
    objectTypeIndex = new ArrayList();
    nodesByType = new ArrayList();
    nodeTypeIndex = new ArrayList();

    setPanelBackgroundColour(configman.getPanelBackgroundColour());
  }
  
  public void updateDisplay() {
    if (ghost != null && ghost != getFocusNode())
      hideNode(ghost);

    updateAssociationCountForAllTopics();
    resetDamper();
  }
  
  public void updateDisplayLazily() {
    if (ghost != null && ghost != getFocusNode())
      hideNode(ghost);

    updateAssociationCountForMarkedTopics();
    resetDamper();
  }
  
  public void updateDisplayNoWork() {
    nodesUpdateCount.clear();
    resetDamper();
  }

  public void resetDamper() {
    getTGPanel().resetDamper();
    motionKiller.waitFor(2000, 3000);
  }
  
  public void retainNodes(Collection nodes) {
    // For each topic type.
    Iterator nodesByTypeIt = nodesByType.iterator();
    while (nodesByTypeIt.hasNext()) {
      List currentType = (List)nodesByTypeIt.next();
      
      // Remove from currentType any node that is not in nodes.
      currentType.retainAll(nodes);
    }
  }
  
  public void retainObjects(Collection nodes) {
    // For each object type.
    Iterator objectsByTypeIt = objectsByType.iterator();
    while (objectsByTypeIt.hasNext()) {
      List currentType = (List)objectsByTypeIt.next();
      
      // Remove from currentType any node that is not in nodes.
      currentType.retainAll(nodes);
    }
  }

  // --- view modifications ---------------------------------------------

  /**
   * Set the locality to a given value and adds/removes nodes where appropriate.
   * 
   * After a locality increase, all nodes and edges within locality's reach 
   * in addition to all nodes and edges that were there before will be visible.
   * 
   * After a locality decrease, all nodes and edges that were visible before and
   * that are still within locality's reach will be visible. 
   * @param newLocality The new locality.
   */
  public void setLocality(int newLocality) {
    int oldLocality = locality;
    locality = newLocality;
    
    if (getFocusNode() != null) {
      // Ensure all nodes within the new locality are loaded.
      // Get edges beyond locality's reach.
      // Only create new edges and nodes if locality has increased.
      loadNodesInLocality(getFocusNode(), 
          oldLocality < newLocality, newLocality < oldLocality);
    }
  }

  /**
   * If create is true, adds all nodes within locality's reach that were not
   * already visible.
   * Then (independent of 'create') collects all edges just beyond locality's
   * reach and returns them.
   * An edge with both end-points within locality's reach is counted as within
   * locality's reach..
   *
   * ALTERNATIVE EDGE ALGORITHM:
   * An edge is within locality's reach if one end-point is attached to a node
   * at least one step closer than locality's reach.
   * 
   * @param startNode The node to start from.
   * @param create Whether to create new nodes and edges.
   * @return edges beyond locality's reach.
   */
  public Collection loadNodesInLocality(TMAbstractNode startNode,
      boolean create, boolean delete) {
    // Don't do anything if in map view.
    if (startNode == null)
      return Collections.EMPTY_SET;

    // Visit all the nodes no more than 'locality' edges away from focus node.
    // Do this using a BREADTH-FIRST traversal, to avoid early cut-offs in
    // cycles in the graph.

    HashSet visited = new HashSet(getNodeCount());
    Collection currentLevel = Collections.singleton(startNode);

    // Perform a breadth first traversal. Each iteration of this loop is
    // another level of depth in the search.
    for (int distance = 0; distance < locality && !currentLevel.isEmpty();
        distance++) {
      Collection nextLevel = new HashSet(getNodeCount());
      
      // Iterate over all nodes at the current level of depth.
      Iterator currentLevelIt = currentLevel.iterator();
      while (currentLevelIt.hasNext()) {
        TMAbstractNode currentNode = (TMAbstractNode)currentLevelIt.next();
        
        // Load node, adding all neighbour nodes to the view (if create). 
        if (create)
          createAssociations(currentNode, true, false);
        
        // Add edges to the next level.
        Iterator edgesIt = currentNode.getEdges();
        while (edgesIt.hasNext())
          nextLevel.addAll(((TMAbstractEdge)edgesIt.next())
              .getTargetsFrom(currentNode));
        
        visited.add(currentNode);
      }
      currentLevel = nextLevel;
    }
    
    if (useNodeLocality()) {
      Iterator currentLevelIt = currentLevel.iterator();
      // Mark all nodes of the next level as visited (since they're displayed).
      while (currentLevelIt.hasNext()) {
        TMAbstractNode currentNode = (TMAbstractNode)currentLevelIt.next();
        
        // TODO: Check whether moving the following line up has had an impact on
        // the edte/node -oriented algorithms.
        // Should it only get executed with node orientation?
        visited.add(currentNode);
      }
    }

    // Collect edges that connect nodes in the last level of depth with nodes
    // that have not yet been visited (and consequently are not displayed).
    Collection farEdges = new ArrayList();
    Iterator currentLevelIt = currentLevel.iterator();
    while (currentLevelIt.hasNext()) {
      TMAbstractNode currentNode = (TMAbstractNode)currentLevelIt.next();
      
      Iterator edgesIt = currentNode.getEdges();
      while (edgesIt.hasNext()) {
        TMAbstractEdge currentEdge = (TMAbstractEdge)edgesIt.next();
        if (!(visited.contains(currentEdge.getOtherEndpt(currentNode))))
          farEdges.add(currentEdge);
      }
    }
    
    // Only delete edges beyond locality's reach if locality has decreased.
    if (delete) {
      deleteEdges(farEdges);
      removeDisconnectedNodes();
    }

    currentLevelIt = currentLevel.iterator();
    while (currentLevelIt.hasNext()) {
      TMAbstractNode currentNode = (TMAbstractNode)currentLevelIt.next();
      
      if (currentNode instanceof TMAssociationNode) {
        createAllRoles((TMAssociationNode)currentNode, false);
      }

      if (useNodeLocality()) {
        // Load node, adding all neighbour nodes to the view (if create). 
        if (create)
          createAssociations(currentNode, false, false);
      }
    }
    
    return farEdges;
  }

  public void setTypeFont(TopicIF type, Font font) {
    // Set the font in both Associations and Topic objects
    // Associations
    Iterator it = getObjectsFor(type).iterator();
    while (it.hasNext()) {
      VizTMObjectIF association = (VizTMObjectIF) it.next();
      association.setFont(font);
    }
    // Topics
    it = getTopicNodesFor(type).iterator();
    while (it.hasNext()) {
      TMAbstractNode node = (TMAbstractNode) it.next();
      if (isShowType(node, type))
        node.setFont(font);
    }
  }

  public void setTopicTypeShape(TopicIF type, int shape) {
    Iterator it = getTopicNodesFor(type).iterator();
    while (it.hasNext()) {
      TMAbstractNode node = (TMAbstractNode) it.next();
      if (isShowType(node, type))
        node.setType(shape);
    }
  }
  
  public void setTypeColor(TopicIF type, Color c) {
    // Change the colour of the association objects AND topic objects
    // Association objects:
    Iterator it = getObjectsFor(type).iterator();
    while (it.hasNext()) {
      VizTMObjectIF association = (VizTMObjectIF) it.next();
      association.setColor(c);
    }
    // Topic objects
    it = getTopicNodesFor(type).iterator();
    while (it.hasNext()) {
      TMAbstractNode node = (TMAbstractNode) it.next();
      if (isShowType(node, type))
        node.setBackColor(c);
    }
  }

  public void setTopicTypeShapePadding(TopicIF type, int value) {
    Iterator it = getTopicNodesFor(type).iterator();
    while (it.hasNext()) {
      TMTopicNode node = (TMTopicNode) it.next();
      if (isShowType(node, type))
        node.setShapePadding(value);
    }
  }

  public void setTypeIcon(TopicIF type, Icon icon) {
    // Set the font in both Associations and Topic objects
    // Associations
    Iterator it = getObjectsFor(type).iterator();
    while (it.hasNext()) {
      VizTMObjectIF association = (VizTMObjectIF) it.next();
      association.setIcon(icon);
    }
    // Toipics
    it = getTopicNodesFor(type).iterator();
    while (it.hasNext()) {
      TMTopicNode node = (TMTopicNode) it.next();
      if (isShowType(node, type))
        node.setIcon(icon);
    }
  }

  /**
   * Returns true iff 'type' is the appropriate config type for 'node'.
   * @param node The node to get configuration for.
   * @param type The type to check for appropriateness.
   * @return True iff 'type is the appropriate config type for 'node'.
   */
  private boolean isShowType(TMAbstractNode node, TopicIF type) {
    if (!(node instanceof TMTopicNode))
      return true;
    TopicIF primaryType = getPrimaryTypeFor((TMTopicNode)node);
    if (primaryType == null)
      return type == null;
    return primaryType.equals(type);
  }

  public void updateType(TopicIF type) {
    setTypeFont(type, controller.getTypeFont(type));
    setTopicTypeShape(type, controller.getTopicTypeShape(type));
    setTypeColor(type, controller.getTopicTypeColor(type));
    setTopicTypeShapePadding(type, controller.getTopicTypeShapePadding(type));
    setTypeIcon(type, controller.getTypeIcon(type));
    tgPanel.repaint();
  }
  
  private List getObjectsFor(TopicIF type) {
    int index = objectTypeIndex.indexOf(type);

    if (index == -1)
      return Collections.EMPTY_LIST;

    return (List) objectsByType.get(index);
  }

  protected List getTopicNodesFor(TopicIF type) {
    int index = nodeTypeIndex.indexOf(type);

    if (index == -1)
      return Collections.EMPTY_LIST;

    return (List) nodesByType.get(index);
  }

  public void setAssociationTypeShape(TopicIF type, int shape) {
    Iterator it = getObjectsFor(type).iterator();
    while (it.hasNext()) {
      VizTMObjectIF association = (VizTMObjectIF) it.next();
      association.setShape(shape);
    }
  }

  public void setAssociationTypeLineWeight(TopicIF type, int lineWeight) {
    Iterator it = getObjectsFor(type).iterator();
    while (it.hasNext()) {
      VizTMObjectIF association = (VizTMObjectIF) it.next();
      association.setLineWeight(lineWeight);
    }
  }

  protected void focusNode(TMAbstractNode node) {
    tgPanel.setSelect(node);

    // Add nodes and edges within locality.
    loadNodesInLocality(node, true, true);
  }

  public void clearFocusNode() {
    // Ensure all nodes are loaded
    buildAll();

    try {
      tgPanel.setLocale(getFocusNode(),
          LocalityUtils.INFINITE_LOCALITY_RADIUS);
      tgPanel.clearSelect();
    } catch (TGException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  /**
   * Called when a new configuration has been loaded. Switches the view over to
   * the new configuration.
   */
  public void setConfigManager(VizTopicMapConfigurationManager configman) {
    this.configman = configman;

    locality = 1;

    nodesByType = new ArrayList();
    nodeTypeIndex = new ArrayList();
    objectTypeIndex = new ArrayList();
    objectsByType = new ArrayList();

    build();
  }

  /**
   * Delete a node, all incident edges and all nodes and edges that no longer
   * have a path to the focus node as a consequence of this.
   * @param node The base node to delete.
   */
  public void hideNode(TMAbstractNode node) {
    // NOTE: THIS METHOD IS USED A LOT BY OTHER METHODS IN TopicMapView and
    // VisController. THEREFORE CHANGES TO THIS CLASS SHOULD BE MADE WITH
    // GREAT CARE.
    // Specifically VizController.hideNode(), VizController.collapseNode()
    // and TopicMapView.setTopicTypeVisible() rely on hideNode functioning
    // WITH THE CURRENT BEHAVIOUR!
    
    // Workaround that avoids mouseover icon hanging around after hiding node.
    if (node instanceof TMAssociationNode)
      ((TMAssociationNode)node).removeMouseoverIcon();
    
    Collection incidentEdges = new ArrayList();
    Iterator edgesIt = node.getEdges();
    while (edgesIt.hasNext()) {
      TMAbstractEdge currentEdge = (TMAbstractEdge)edgesIt.next();
      incidentEdges.add(currentEdge);
      
      // These are the only nodes that need updating both in map/topic view.
      // The nodes removed in removeDisconnectedNodes are of no importance,
      // since they are not adjacent to any nodes in the display.
      nodesUpdateCount.add(currentEdge.getOtherEndpt(node));
    }
    
    deleteEdges(incidentEdges);
    
    if (getFocusNode() == null) {
      if (node instanceof TMAssociationNode) {
        ((Locality) tgPanel.getGES()).deleteNode(node);
        deleteNode((TMAssociationNode)node);
      } else if (node instanceof TMTopicNode) {
        ((Locality) tgPanel.getGES()).deleteNode(node);
        deleteNode((TMTopicNode)node);
      }
    } else
      removeDisconnectedNodes();
  }

  public static String fullName(Node node) {
    if (node == null)
      return null;
      
    String name = node.getLabel();
    if (node instanceof TMTopicNode)
      name = ((TMTopicNode)node).getTopicName();
    return name;
  }
  
  public void headedDebug(String header, Object object) {
    if (!VizDebugUtils.isDebugEnabled())
      return;
    
    VizDebugUtils.debug("=====================" + header + "=====================");
    VizDebugUtils.debug("---- (focusNode: " + fullName(getFocusNode()) + " ---)");
    
    if (object != null) {
      VizDebugUtils.debug("---- (Object class: " + object.getClass().getName() + ")");
      if (object instanceof Node)
        VizDebugUtils.debug("---- (node fulltext: " + fullName((Node)object)
            + " ---)");
      else if (object instanceof Edge)
        VizDebugUtils.debug("---- (edge id: " + ((Edge)object).getID()
            + " ---)");
      else
        VizDebugUtils.debug(" ---- (Object stringified: " + ((Function) controller.getStringifier())
            .apply(object) + " ---)");
    }
    
    outputDebugInfo("count");
  }
  
  /**
   * NOTE! The calling mehtod is itself responsible for calling the
   * method: updateAssociationCountForAllTopics().
   */
  public void setTopicTypeVisible(TopicIF type, boolean visible) {
    if (visible) {
      if (getFocusNode() == null) {
        // For each topic of the given type.
        Iterator topicsIt = typeix.getTopics(type).iterator();
        while (topicsIt.hasNext()) {
          TopicIF currentTopic = (TopicIF)topicsIt.next();
          TMTopicNode newNode = assertNode(currentTopic, true);
          controller.loadNode(newNode);
        }
      }
      
      // Do nothing in graph view. The graph is being recomputed. No more nodes
      // or edges need adding.
    } else {
      int index = nodeTypeIndex.indexOf(type);
      if (index != -1) {
        List objects = new ArrayList(getTopicNodesFor(type));

        ArrayList nodesOfThisType = new ArrayList();
        nodesByType.set(index, nodesOfThisType);
        
        if (objects.contains(getFocusNode())) {
          nodesOfThisType.add(getFocusNode());
          
          ghost = getFocusNode();
        }
        
        Iterator it = objects.iterator();
        while (it.hasNext()) {
          TMTopicNode node = (TMTopicNode) it.next();
          
          nodesUpdateCount.add(node);
          
          // delete the incident edges
          Collection edgesToDelete = new ArrayList();
          Iterator edgesIt = node.getEdges();
          while (edgesIt.hasNext()) {
            Edge edge = (Edge)edgesIt.next();
            Node farNode = edge.getOtherEndpt(node);
            nodesUpdateCount.add(farNode);
            edgesToDelete.add(edge);
          }  
            
          deleteEdges(edgesToDelete);  

          if (node != getFocusNode()) {
            // The current functionality is that if a type is made 
            // invisible and a topic of that type has multiple types, then it 
            // should still be invisible.
            // Therefore we need to remove the topic from the other
            // type indexes it is included in.

            for (Iterator iter = getValidTypesFor(node.getTopic())
                .iterator(); iter.hasNext();) {
              TopicIF aType = (TopicIF) iter.next();
              ((List) nodesByType.get(nodeTypeIndex.indexOf(aType)))
                  .remove(node);
            }
            tgPanel.deleteNode(node); // this also removes the edges
          }
        }
        
        if (getFocusNode() != null)
          removeDisconnectedNodes();
      }
    }
  }

  /**
   * NOTE: The calling method is responsible for calling the
   * method: updateAssociationCountForAllTopics();
   * @param type The type to set (in)visible.
   * @param visible true if 'type' should be set visible. Otherwise false.
   */  
  public void setAssociationTypeVisible(TopicIF type, boolean visible) {
    if (visible) {
      if (getFocusNode() == null) {
        // create edges for the associations
        if (type != configman.getTypeInstanceType()) {
          Iterator it = typeix.getAssociations(type).iterator();
          while (it.hasNext()) {
            AssociationIF assoc = (AssociationIF) it.next();
            if (configman.isVisible(assoc)
                && findObject(assoc, assoc.getType()) == null) {
              // FIXME: With 2nd param false filter default association type out
              // followed by filter default assocication type in does not
              // recreate turnary associations.
              makeAssociation(assoc, true);
            }
          }
        } else {
          Iterator it = typeix.getTopicTypes().iterator();
          while (it.hasNext()) {
            TopicIF ttype = (TopicIF) it.next();
            if (!configman.isVisible(ttype) ||
                !configman.isTopicTypeVisible(ttype))
              continue;
  
            TMTopicNode tnode = getNode(ttype);
            if (tnode != null) {
              Iterator it2 = typeix.getTopics(ttype).iterator();
              while (it2.hasNext()) {
                TopicIF instance = (TopicIF) it2.next();
                if (configman.isVisible(instance)) {
                  TMTopicNode node = getNode(instance);
                  if (node != null)
                    makeTypeInstanceEdge(node, tnode);
                }
              }
            }
            for (Iterator nodesIt = getTopicNodesFor(ttype).iterator(); nodesIt
                .hasNext();) {
              setValidAssociationCountFor((TMTopicNode) nodesIt.next());
            }
          }
        }
      }
    } else {
      // delete the edges for the associations
      Collection edges = getObjectsFor(type);
      Iterator edgesIt = new ArrayList(edges).iterator();
      while (edgesIt.hasNext()) {
        VizTMObjectIF edge = (VizTMObjectIF)edgesIt.next();
        
        if (edge == getFocusNode() && edge instanceof TMAssociationNode)
          ghost = (TMAssociationNode)edge;
        else {
          deleteEdgeUndoable(edge);
        }
      }
    }

    // Make sure there are no "islands" not (in)directly connected to focus node
    removeDisconnectedNodes();
  }
  
  private void removeInvisibleEdges() {
    // For each edge:
    // If the association of the edge is not visible
      // Delete the edge.

    Collection filter = configman.getAssociationScopeFilter();
      
    if (!(filter.size() == 0)) {
      Iterator associationTypesIt = getAssociationTypes().iterator();
      
      // For each association type
      while (associationTypesIt.hasNext()) {
        TopicIF associationType = (TopicIF)associationTypesIt.next();
        Collection associationObjects = new ArrayList(getObjectsFor(
            associationType));
        Iterator associationObjectsIt = associationObjects.iterator();
        
        // For each association object (edge) of the current association type
        while (associationObjectsIt.hasNext()) {
          VizTMObjectIF associationObject = (VizTMObjectIF)associationObjectsIt
              .next();
          
          AssociationIF association = null;
          if (associationObject instanceof TMAssociationNode)
            association = ((TMAssociationNode)associationObject)
                .getAssociation();
          else if (associationObject instanceof TMAssociationEdge)
            association = ((TMAssociationEdge)associationObject)
                .getAssociation();
          // Note: No need to handle class-instance associations since they
          // they have no scope.
          
          if (association != null) {
            Collection associationScope = association.getScope();
      
            // If this is an edge (association) not matching the filter
            if (!configman.matchesFilter(associationScope, filter)) {
              // Delete the edge.
              deleteEdge(associationObject);
              associationObject.deleteFrom(tgPanel);
            }
          }
        }
      }
      removeDisconnectedNodes();
    }
  }
  
  private void createVisibleEdges() {
    // For each association
    Iterator associationsIt = topicmap.getAssociations().iterator();
    while (associationsIt.hasNext()) {
      AssociationIF association = (AssociationIF)associationsIt.next();
      if (association.getType() != configman.getTypeInstanceType()) {
        // If the edge does not exist, but the association of the edge is visible
        if (configman.isVisible(association)
            && findObject(association, association.getType()) == null) {
          
          // Get invisible players *before* making the edge.
          Collection invisiblePlayers = getInvisiblePlayers(association);

          // Create an edge for the associations
          makeAssociation(association, false);
          
          Iterator invisiblePlayersIt = invisiblePlayers.iterator();
          while (invisiblePlayersIt.hasNext()) {
            TopicIF player = (TopicIF)invisiblePlayersIt.next();
            TMTopicNode node = getNode(player);
            ((Locality) tgPanel.getGES()).removeNode(node);
          }
        }
      }
    }
    loadNodesInLocality(getFocusNode(), true, false);
  }
  
  private Set getInvisiblePlayers(AssociationIF association) {
    Set invisiblePlayers = new HashSet();
    
    Collection roles = association.getRoles();
    Iterator rolesIt = roles.iterator();
    
    while (rolesIt.hasNext()) {
      AssociationRoleIF role = (AssociationRoleIF)rolesIt.next();
      TopicIF player = role.getPlayer();
      
      TMTopicNode node = getNode(player);
      if (node == null || !((Locality)tgPanel.getGES()).contains(node))
        invisiblePlayers.add(player);
    }
    
    return invisiblePlayers;
  }

  /**
   * Set the current level of strictness of the association scope filter
   * and remove/add edges where needed.
   * @param strictness The new level of strictness
   */
  public void setAssociationScopeFilterStrictness(int strictness) {
    int SHOW_ALL = VizTopicMapConfigurationManager.SHOW_ALL_ASSOCIATION_SCOPES;
    int LOOSE = VizTopicMapConfigurationManager.LOOSE_ASSOCIATION_SCOPES;
    int STRICT = VizTopicMapConfigurationManager.STRICT_ASSOCIATION_SCOPES;
    
    if (associationScopeFilterStrictness == SHOW_ALL) {
      if ((strictness == LOOSE || strictness == STRICT)
          && configman.getAssociationScopeFilter().size() != 0)
        removeInvisibleEdges();
    } else if (associationScopeFilterStrictness == LOOSE) {
      if (strictness == SHOW_ALL
          && configman.getAssociationScopeFilter().size() != 0)
        createVisibleEdges();
      else if (strictness == STRICT
               && configman.getAssociationScopeFilter().size() != 0)
        removeInvisibleEdges();
    } else if (associationScopeFilterStrictness == STRICT) {
      if ((strictness == SHOW_ALL || strictness == LOOSE)
          && configman.getAssociationScopeFilter().size() != 0)
        createVisibleEdges();
    }
    associationScopeFilterStrictness = strictness;
  }  
    
  public void removeAssociationScopeFilterTopic(TopicIF scopingTopic) {
    int SHOW_ALL = VizTopicMapConfigurationManager.SHOW_ALL_ASSOCIATION_SCOPES;
    int LOOSE = VizTopicMapConfigurationManager.LOOSE_ASSOCIATION_SCOPES;

    if (associationScopeFilterStrictness == SHOW_ALL)
      return;
    
    // If this is the last topic in the filter.
    if (configman.getAssociationScopeFilter().size() == 0) {
      createVisibleEdges();
      updateAssociationCountForAllTopics();
      return;
    }
    
    if (associationScopeFilterStrictness == LOOSE) {
      removeInvisibleEdges();
      removeDisconnectedNodes();
    } else { // implied: associationScopeFilterStrictness == STRICT
      createVisibleEdges();
    }
    updateAssociationCountForAllTopics();
  }
  
  public void addAssociationScopeFilterTopic(TopicIF scopingTopic) {
    int SHOW_ALL = VizTopicMapConfigurationManager.SHOW_ALL_ASSOCIATION_SCOPES;
    int LOOSE = VizTopicMapConfigurationManager.LOOSE_ASSOCIATION_SCOPES;

    if (associationScopeFilterStrictness == SHOW_ALL)
      return;
    
    // If this is the first topic in the filter.
    if (configman.getAssociationScopeFilter().size() == 1)
      removeInvisibleEdges();
    else if (associationScopeFilterStrictness == LOOSE)
      createVisibleEdges();
    else // implied: associationScopeFilterStrictness == STRICT
      removeInvisibleEdges();
      // Possible optimisation: Instead remove edges that don't have this topic
      // in their scope.

    removeDisconnectedNodes();
  }

  public void updateAssociationCountForAllTopics() {
    for (Iterator iter = (new ArrayList(nodesByType)).iterator();
        iter.hasNext();) {
      ArrayList nodes = new ArrayList((ArrayList)iter.next());
      for (Iterator iterator = nodes.iterator(); iterator.hasNext();) {
        TMTopicNode node = (TMTopicNode) iterator.next();
        setValidAssociationCountFor(node);
      }
    }
    
    for (Iterator iter = (new ArrayList(objectsByType)).iterator();
        iter.hasNext();) {
      ArrayList objects = (ArrayList) iter.next();
      for (Iterator iterator = objects.iterator(); iterator.hasNext();) {
        VizTMObjectIF currentObject = (VizTMObjectIF) iterator.next();
        if (currentObject instanceof TMAssociationNode)
          setValidRoleCountFor((TMAssociationNode)currentObject);
      }
    }
    
    // No need to subsequently update count for marked nodes, since all nodes
    // have now been updated.
    nodesUpdateCount.clear();
  }

  public void updateAssociationCountForMarkedTopics() {
    Iterator markedTopicNodesIt = nodesUpdateCount.iterator();
    while (markedTopicNodesIt.hasNext()) {
      TMAbstractNode currentNode = (TMAbstractNode)markedTopicNodesIt.next();
      if (currentNode instanceof TMTopicNode)
        setValidAssociationCountFor((TMTopicNode)currentNode);
      else // currentNode instanceof TMAssociationNode
        setValidRoleCountFor((TMAssociationNode)currentNode);
    }
    nodesUpdateCount.clear();
  }

  public TopicMapIF getTopicMap() {
    return topicmap;
  }

  public Collection getPagesFor(TopicIF topic) {
    TopicIndexIF ix = ((RemoteTopicMapStore) topicmap.getStore())
        .getTopicIndex();
    return ix.getTopicPages(topic.getSubjectIdentifiers(), 
														topic.getItemIdentifiers(), topic.getSubjectLocators());
  }

  /**
   * Returns a collection of all association types in the topic map. Does not
   * work with remote topic maps.
   */
  public Collection getAssociationTypes() {
    List types = new ArrayList(typeix.getAssociationTypes());
    types.add(configman.getTypeInstanceType());
    types.add(configman.defaultAssociationType);
    return types;
  }

  /**
   * Returns a collection of all topic types in the topic map including null
   * (untyped). Does not work with remote topic maps.
   */
  public Collection getAllTopicTypesWithNull() {
    Collection types = getAllTopicTypes();
    types.add(null);
    types.add(configman.defaultType);
    return types;
  }

  /**
   * Returns a collection of all topic types in the topic map. Does not work
   * with remote topic maps.
   */
  public Collection getAllTopicTypes() {
    return new ArrayList(typeix.getTopicTypes());
  }

  // --- internal -------------------------------------------------------

  /**
   * Clears the panel and builds a new view. Assumes there is no existing graph.
   */
  protected void build() {
    clearModel();

    TopicIF startTopic = controller.getStartTopic(topicmap);
    if (startTopic == null) {
      Frame parentFrame = JOptionPane.getFrameForComponent(tgPanel);
      TopicSelectionPrompter prompter = new TopicSelectionPrompter(parentFrame,
          getAllVisibleTopics(), VizUtils
              .stringifierFor(currentScopingTopic));
      TopicIF selection = prompter.getSelection();
      if (selection != null)
        controller.focusNodeInternal(buildTopic(selection));
      else {
        int result = JOptionPane
            .showConfirmDialog(
                parentFrame,
                Messages.getString("Viz.NoInitialTopic"),
                Messages.getString("Viz.TopicSelection"), JOptionPane.YES_NO_CANCEL_OPTION);
        switch (result) {
        case JOptionPane.CANCEL_OPTION:
          break;
        case JOptionPane.YES_OPTION:
          buildAll();
          break;
        case JOptionPane.NO_OPTION:
          build();
          break;
        }
      }
      updateDisplay();
    } else
      controller.focusNodeInternal(buildTopic(startTopic));
    controller.undoManager.reset();
  }

  private Collection getAllVisibleTopics() {
    Collection topics = topicmap.getTopics();
    ArrayList result = new ArrayList(topics.size());
    
    for (Iterator topicsIt = topics.iterator(); topicsIt.hasNext();) {
      TopicIF topic = (TopicIF) topicsIt.next();
      if (configman.isVisible(topic))
        result.add(topic);
    }
    return result;
  }

  protected TMTopicNode buildTopic(TopicIF topic) {
    TMTopicNode node = assertNode(topic, true);
    return node;
  }

  /**
   *  EXPERIMENTAL: method to redraw the map, without starting a new
   *  thread or displaying the progress bar
   */
  public void buildAllSilent() {
    final Collection topics = getTopicMap().getTopics();
    Iterator it = topics.iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();
      TMTopicNode node = assertNode(topic, true);
      if (node != null)
        createAssociations(node);
    }
  }
  
  protected void buildAll() {
    final Collection topics = topicmap.getTopics();
    final boolean[] running = new boolean[] { true };
    final String[] cancelOptions = new String[] { Messages
        .getString("Viz.Cancel") };

    final JProgressBar progressBar = new JProgressBar();
    progressBar.setMinimum(0);
    progressBar.setMaximum(topics.size());
    progressBar.setValue(0);
    final JOptionPane pane = new JOptionPane(new Object[] {
        Messages.getString("Viz.BuildingModel"), progressBar },
        JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null,
        cancelOptions, null);

    Frame frame = JOptionPane.getFrameForComponent(tgPanel);
    final JDialog dialog = new JDialog(frame, Messages
        .getString("Viz.Information"), true);

    dialog.setContentPane(pane);
    dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    dialog.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent we) {
        running[0] = (JOptionPane
            .showConfirmDialog(
                pane,
                Messages.getString("Viz.CancelBuilding"), Messages.getString("Viz.Question"),
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION);
      }
    });
    pane.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent e) {
        if (pane.getValue() == cancelOptions[0]) {
          running[0] = (JOptionPane
              .showConfirmDialog(
                  pane,
                  Messages.getString("Viz.CancelBuilding"), Messages.getString("Viz.Question"),
                  JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION);
          // We need to reset the panel's value so that the changeListener
          // will get called again if the user presses the cancel button again.
          pane.setValue(null);
        }
      }
    });
    dialog.pack();
    dialog.setLocationRelativeTo(frame);

    final SwingWorker worker = new SwingWorker() {
      @Override
      public Object construct() {
        Iterator it = topics.iterator();
        int max = topics.size();
        int counter = 0;
        int increment = Math.max(1, (topics.size() / 20));
        int nextProgress = increment;
        final int[] progress = new int[] { 0 };
        while (it.hasNext() && running[0]) {
          TopicIF topic = (TopicIF) it.next();
          TMTopicNode node = assertNode(topic, true);
          if (node != null) {
            createAssociations(node);
          }
          counter++;
          if (counter == nextProgress) {
            progress[0] = counter;
            nextProgress = Math.min(nextProgress + increment, max);
            try {
              // Updating of the progress bar should allways occure
              // in the UI thread.
              // Using invokeLater() here makes the build a tad
              // faster, but the progressMonitor can become a little
              // out of sync. Using invokeAndWait() slows down the
              // load a tad, but means that the progress bar is allways
              // in sync.
              EventQueue.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                  progressBar.setValue(progress[0]);
                }
              });
            } catch (Exception e) {
              throw new OntopiaRuntimeException(e);
            }
          }
        }
        return null;
      }

      @Override
      public void finished() {
        dialog.hide();
      }
    };

    worker.start();
    dialog.show();
  }

  protected TMTopicNode assertNode(TopicIF topic, boolean create) {
    TMTopicNode node = getNode(topic);
    if (node == null && create) {
      controller.loadTopic(topic);
      if (configman.isVisible(topic)) {
        node = makeNode(topic);
        newNodes.add(node);
        nodesUpdateCount.add(node);
      }
    }
    return node;
  }

  private TMTopicNode makeNode(TopicIF topic) {
    TMTopicNode node = new TMTopicNode(topic, currentScopingTopic, this);

    // Index by type
    Iterator it = getValidTypesFor(topic).iterator();
    if (!it.hasNext()) // if no type
      indexNode(node, null);
    while (it.hasNext()) {
      TopicIF type = (TopicIF) it.next();
      indexNode(node, type);
    }

    initializeNode(node);
    controller.undoManager.addRecovery(node.getDesctructor());

    return node;
  }
  
  protected void queueInForeground(TMAbstractNode node) {
    foregroundQueue.add(node);
  }

  protected void queueInForeground(TMAbstractEdge edge) {
    foregroundQueue.add(edge);
  }
  
  protected void setHighlightNode(TMAbstractNode node, Graphics g) {
    controller.setHighlightNode(node, g);
  }
  
  protected void processForegroundQueue(Graphics graphics) {
    Iterator foregroundQueueIt = foregroundQueue.iterator();
    while (foregroundQueueIt.hasNext()) {
      Object o = foregroundQueueIt.next();
      if (o instanceof TMTopicNode) {
        TMTopicNode topicNode = (TMTopicNode)o;
        topicNode.miniPaint(graphics, tgPanel);
      } else if (o instanceof TMAssociationNode) {
        TMAssociationNode associationNode = (TMAssociationNode)o;
        associationNode.miniPaint(graphics, tgPanel);
      } else if (o instanceof TMAbstractNode) {
        TMAbstractNode abstractNode = (TMAbstractNode)o;
        abstractNode.paint(graphics, tgPanel);
      } else if (o instanceof TMAbstractEdge) {
        TMAbstractEdge abstractEdge = (TMAbstractEdge)o;
        abstractEdge.paint(graphics, tgPanel);
      }
    }
    foregroundQueue = new ArrayList();
  }

  private void setValidAssociationCountFor(TMTopicNode aNode) {
    int count = 0;
    
    TopicIF topic = aNode.getTopic();

    if (aNode == getFocusNode() && !(configman.isVisible(topic))) {
      // The rule when the focus node is made invisible is that everything *but*
      // the focus node is hidden from the display.
      // The rationale is that otherwise everything would be hidden, which is
      // not very useful.
      // So in this case, the count is necessarily 0.
      aNode.setAssociationCount(0);
      return;
    }

    // Standard associations
    for (Iterator rolesIt = topic.getRoles().iterator(); rolesIt.hasNext();) {
      AssociationIF association = ((AssociationRoleIF) rolesIt.next())
          .getAssociation();
      if (configman.isVisible(association)
          && (arePlayersVisible(association, topic) 
              || association.getRoles().size() != 2))
        // NOTE: n-ary associations (n > 2) can be shown even if the players
        // are not visible, as the players of an n-ary associaiton don't need
        // to be shown. An n-ary is a self contained node, not just an edge.
        count++;
    }

    if (configman.isAssociationTypeVisible(configman.getTypeInstanceType())) {
      // Instance-Class associations
      count = count + getValidVisibleTypesFor(topic).size();

      // Class-Instance associations
      if (configman.isTopicTypeVisible(topic))
        count = count + typeix.getTopics(topic).size();
    }

    aNode.setAssociationCount(count);
  }
  
  /**
   * Return the types for the passed in topic that are not "excluded"
   */
  private List getValidVisibleTypesFor(TopicIF aTopic) {
    Collection types = aTopic.getTypes();
    if (types.isEmpty())
      return Collections.EMPTY_LIST;
    ArrayList result = new ArrayList(types.size());
    for (Iterator typesIt = types.iterator(); typesIt.hasNext();) {
      TopicIF type = (TopicIF) typesIt.next();
      if (!isExcludedType(type) && configman.isVisible(type))
        result.add(type);
    }
    return result;
  }

  private void setValidRoleCountFor(TMAssociationNode aNode) {
    int count = 0;

    AssociationIF association = aNode.getAssociation();
    
    // The valid association count is the number of roles with players that
    // are visible.

    Iterator rolesIt = association.getRoles().iterator();
    while (rolesIt.hasNext()) {
      TopicIF currentPlayer = ((AssociationRoleIF)rolesIt.next()).getPlayer();
      if (configman.isVisible(currentPlayer))
        count++;
    }

    aNode.setRoleCount(count);
  }
  
  private boolean arePlayersVisible(AssociationIF association, TopicIF source) {
    for (Iterator rolesIt = association.getRoles().iterator(); 
        rolesIt.hasNext();) {
      TopicIF player = ((AssociationRoleIF) rolesIt.next()).getPlayer();
      if (player == null) continue;
      if (!source.equals(player) && configman.isVisible(player))
        return true;
    }
    return false;
  }

  private void initializeNode(TMTopicNode aNode) {
    initializeNode(aNode, getPrimaryTypeFor(aNode));
  }

  /**
   * Get the primary type for the given node. That is, the type to be used for
   * configuration purposes. 
   * (Previously this just returned the first of the types
   * that are not excluded, or null if all were excluded.)
   */
  private TopicIF getPrimaryTypeFor(TMTopicNode aNode) {
    List types = getValidTypesFor(aNode.getTopic());
    if (types.isEmpty())
      return null;
    
    return configman.getTTPriorityManager().highestRankedType(types);
  }

  /**
   * Return the types for the passed in topic that are not "excluded"
   */
  private List getValidTypesFor(TopicIF aTopic) {
    Collection types = aTopic.getTypes();
    if (types.isEmpty())
      return Collections.EMPTY_LIST;
    ArrayList result = new ArrayList(types.size());
    for (Iterator typesIt = types.iterator(); typesIt.hasNext();) {
      TopicIF type = (TopicIF) typesIt.next();
      if (!isExcludedType(type))
        result.add(type);
    }
    return result;
  }

  private boolean isExcludedType(TopicIF aType) {
    return configman.isTypeExcluded(aType);
  }

  /**
   * Initialize a node with the values associatied with the passed in type
   * 
   * @param node
   * @param type
   */
  private void initializeNode(TMTopicNode node, TopicIF type) {
    node.setBackColor(configman.getTopicTypeColor(type));
    node.setType(configman.getTopicTypeShape(type));
    node.setFont(configman.getTypeFont(type));
    node.setIcon(configman.getTypeIcon(type));
    node.setShapePadding(configman.getTopicTypeShapePadding(type));
  }

  private void indexNode(TMTopicNode node, TopicIF type) {
    ArrayList target;
    int index = nodeTypeIndex.indexOf(type);

    if (index == -1) {
      nodeTypeIndex.add(type);
      target = new ArrayList();
      nodesByType.add(target);
    } else
      target = (ArrayList) nodesByType.get(index);

    target.add(node);
  }

  protected VizTMObjectIF findObject(Object object, TopicIF type) {
    Iterator iterator = getObjectsFor(type).iterator();
    while (iterator.hasNext()) {
      VizTMObjectIF target = (VizTMObjectIF) iterator.next();
      if (target.represents(object))
        return target;
    }
    return null;
  }

  protected TMClassInstanceAssociation makeTypeInstanceEdge(TMTopicNode instance, TMTopicNode type) {
    TMClassInstanceAssociation newInstance = new TMClassInstanceAssociation(
        type, instance, configman.getTypeInstanceType());

    initializeObject(newInstance);
    // Next line not really needed, but keeps things consistent
    initializeEdge(newInstance);

    addAssociation(newInstance);
    
    controller.undoManager.addRecovery(newInstance.getDesctructor());
    
    return newInstance;
  }

  private void makeAssociation(AssociationIF assoc, boolean create) {
    makeAssociation(assoc, null, create);
  }
  
  /**
   * Create an associations, or, in the case of an n-ary association, if the
   * activePlayer is not null, create the associatin node itself and then only
   * the role that active player is involved in.
   */
  protected VizTMObjectIF makeAssociation(AssociationIF assoc, TMTopicNode activePlayer,
      boolean create) {
    VizTMObjectIF retVal = null;
    
    Collection roles = assoc.getRoles();
    int rolesSize = roles.size();
    
    ArrayList displayableRoles = new ArrayList(rolesSize);

    Iterator rolesIt = roles.iterator();
    while (rolesIt.hasNext()) {
      AssociationRoleIF role = (AssociationRoleIF) rolesIt.next();
      TopicIF player = role.getPlayer();
      if (player != null && 
          (rolesSize == 2 || activePlayer == null 
              || player.equals(activePlayer.getTopic()))) {
        displayableRoles.add(role);
      }
    }

    if (displayableRoles.size() == 0) {
      return retVal;
    }

    if (displayableRoles.size() == 2
        && (!((AssociationRoleIF) displayableRoles.get(0)).getPlayer().equals(
            ((AssociationRoleIF) displayableRoles.get(1)).getPlayer())))
      retVal = makeOptimizedAssociation(assoc, displayableRoles, create);
    else
      retVal = makeStandardAssociation(assoc, displayableRoles, create);

    // Add all the role players of the association to the TouchGraph panel.
    rolesIt = displayableRoles.iterator();
    while (rolesIt.hasNext()) {
      AssociationRoleIF role = (AssociationRoleIF)rolesIt.next();
      TopicIF player = role.getPlayer();
      TMAbstractNode node = getNode(player);
      if (node != null)
        try {
          tgPanel.addNode(node);
        } catch (TGException e) {
          lenientAddNode(node);
        }
    }
    return retVal;
  }
  
  /**
   * This method was created as a work-around for bug #1898, which happens
   * when two nodes have the same ID (the second node failing to get added to
   * tgPanel.
   * I don't know why two nodes sometimes get the same ID, which is important
   * information needed to avoid it, hence this work-around.
   * Adds a systematic suffix and tries to add the node until no exception
   * occurrs.
   * @param node The node that should be added to tgPanel.
   */
  protected void lenientAddNode(TMAbstractNode node) {
    boolean succeeded = false;

    // Find a potential duplicate
    Node collidingNode = tgPanel.getGES().findNode(node.getID());
    if (collidingNode != null && collidingNode.getClass().getName().equals(node.getClass().getName())) {
      // First try to find the collision. If found, remove it and try again
      if (node instanceof TMTopicNode) {
        TMTopicNode tNode = (TMTopicNode)node;
        TMTopicNode tCollNode = (TMTopicNode)collidingNode;
        if (tNode.getTopic().equals(tCollNode.getTopic())) {
          return;
        }
      }
    }
    
    while (!succeeded) {
      String id = node.getID();
      try {
        tgPanel.addNode(node);
        succeeded = true;
      } catch (TGException e) {
        // Check the message in case the TGException occurred for another reason
        String msg = e.getMessage();
        if (msg.equals("node ID '" + id + "' already exists."))
          VizDebugUtils.debug("********** Caught duplicate node id error.");
        else
          throw new OntopiaRuntimeException(e);
      }
      
      // Generate a new, hopefully unique ID.
      String midFix = "_vizigator_";
      int index = id.lastIndexOf(midFix);
      if (index == -1) {
        id = id + midFix + 1; 
      } else {
        int suffix = Integer.parseInt(id.substring(index + midFix.length()));
        suffix++;
        id = id.substring(0, index) + midFix + suffix;
      }
      node.setID(id);
    }
  }
  
  /**
   * Find the node of the given TMObjectIF. If no such node exists, then it
   * should be created. Then set that node to be the focus node, recomputing
   * the graph from this new focus node.
   * Supports TMObjectsIFs instanceof TopicIF or AssociationIF.
   * @param tmObject from which to create the node.
   */
  protected void setFocusNodeOf(TMObjectIF tmObject) {
    TMAbstractNode focusNode;
    if (tmObject instanceof TopicIF) {
      focusNode = assertNode((TopicIF)tmObject, true);
    } else if (tmObject instanceof AssociationIF) {
      AssociationIF assoc = (AssociationIF)tmObject;
      VizTMObjectIF target = (VizTMObjectIF) findObject(assoc, assoc.getType());
      if (target == null)
        focusNode = makeStandardAssociation(assoc, assoc.getRoles(), true);
      else {
        if (!(target instanceof TMAssociationNode))
          throw new OntopiaRuntimeException("Internal error! Got an " +
                "association node from the focus node history, " +
                "that is not a TMAssociationNode: " + 
                tmObject.getClass().getName());
        focusNode = (TMAssociationNode)target;
      }
    } else
      throw new OntopiaRuntimeException("Unsupported TMObjectIF type in " +
                                            "setFocusNodeOf operation: " + 
                                            tmObject.getClass().getName());
    
    controller.focusNodeInternal(focusNode);
  }

  private TMAssociationNode makeStandardAssociation(AssociationIF assoc,
      Collection roles, boolean create) {
    TMAssociationNode target = null;

    if (create) {
      target = new TMAssociationNode(assoc, currentScopingTopic, this);

      newNodes.add(target);
      initializeAssociation(target);
      addAssociation(target);

      for (Iterator rolesIt = roles.iterator(); rolesIt.hasNext();) {
        makeRole(target, (AssociationRoleIF) rolesIt.next(), create);
      }
      
      controller.undoManager.addRecovery(target.getDesctructor());
    }

    return target;
  }

  protected TMRoleEdge makeRole(TMAssociationNode assoc, AssociationRoleIF role,
      boolean create) {
    TMTopicNode target = assertNode(role.getPlayer(), create);

    if (target != null) {
      TMRoleEdge instance = new TMRoleEdge(assoc, target, role,
          currentScopingTopic);

      initializeObject(instance);
      initializeEdge(instance);

      addAssociation(instance);

      controller.undoManager.addRecovery(instance.getDesctructor());
      
      return instance;
    }
    return null;
  }

  private VizTMObjectIF makeOptimizedAssociation(AssociationIF assoc,
      Collection roles, boolean create) {
    // Optimized, i.e. those with only two players, are represented by Edges

    Iterator it = roles.iterator();
    TMTopicNode start = assertNode(((AssociationRoleIF) it.next()).getPlayer(),
        create);
    TMTopicNode end = assertNode(((AssociationRoleIF) it.next()).getPlayer(),
        create);

    TMAssociationEdge instance = null;

    if (start != null && end != null) {
      instance = new TMAssociationEdge(start, end, assoc, currentScopingTopic);
      initializeAssociation(instance);
      controller.undoManager.addRecovery(instance.getDesctructor());
      initializeEdge(instance);
      addAssociation(instance);
    }

    return instance;
  }

  /**
   * Initialize those things only associated with Association Objects
   * @param anInstance
   */
  protected void initializeAssociation(VizTMAssociationIF anInstance) {
    initializeObject(anInstance);
    anInstance.setShouldDisplayScopedAssociationNames(configman
        .shouldDisplayScopedAssociationNames());
  }

  protected void addAssociation(VizTMObjectIF object) {
    TopicIF type = object.getTopicMapType();
    indexObject(object, type);
    object.addTo(tgPanel);

    // Register for HoverHelp
    getHoverHelpManager().addPaintListener((TGPaintListener) object);
  }

  protected VizHoverHelpManager getHoverHelpManager() {
    return controller.getHoverHelpManager();
  }

  /**
   * Initialize those things only associated with Edge Objects
   */
  private void initializeEdge(TMAbstractEdge edge) {
    edge.setShouldDisplayRoleHoverHelp(configman.shouldDisplayRoleHoverHelp());
  }

  /**
   * Initialize thoes things only associated with all Objects
   * @param anInstance
   */
  private void initializeObject(VizTMObjectIF object) {
    TopicIF type = object.getTopicMapType();

    object.setColor(configman.getAssociationTypeColor(type));
    object.setShape(configman.getAssociationTypeShape(type));
    object.setLineWeight(configman.getAssociationTypeLineWeight(type));
    object.setFont(configman.getAssociationTypeFont(type));
    object.setIcon(configman.getTypeIcon(type));
  }

  private void indexObject(VizTMObjectIF tmAssoc, TopicIF type) {
    ArrayList target;
    int index = objectTypeIndex.indexOf(type);

    if (index == -1) {
      // Add 'type' to 'objectTypeIndex' in the last position and add a new
      // corresonding ArrayList at the same (last) position in objectsByType.
      objectTypeIndex.add(type);
      target = new ArrayList();
      objectsByType.add(target);
    } else
      // Get the ArrayList for this type.
      target = (ArrayList) objectsByType.get(index);

    target.add(tmAssoc);
  }
  
  /**
   * Creates all edges for this node, including type -> instance, instance ->
   * type, and ordinary associations.
   */
  public void createAssociations(TMAbstractNode abstractNode) {
    createAssociations(abstractNode, true);
  }

  public void createAssociations(TMAbstractNode abstractNode, boolean create) {
    createAssociations(abstractNode, create, true);
  }

    /**
   * Creates all edges for this node, including type -> instance, instance ->
   * type, and ordinary associations.
   * If create, then newnodes are created.
   */
  public void createAssociations(TMAbstractNode abstractNode, boolean create,
                                 boolean createEdgesToExistingNodes) {
    stat1.startOp();
    if (abstractNode instanceof TMTopicNode) {
      TMTopicNode topicNode = (TMTopicNode)abstractNode;
      createAllAssociations(topicNode, create);
      nodesUpdateCount.add(topicNode);
    } else if (abstractNode instanceof TMAssociationNode) {
      TMAssociationNode associationNode = (TMAssociationNode)abstractNode;
      createAllRoles(associationNode, create);
      nodesUpdateCount.add(associationNode);
    }
    stat1.stopOp();

    while (newNodes.isEmpty() == false) {
      TMAbstractNode newNode = (TMAbstractNode) newNodes.remove(0);
      nodesUpdateCount.add(newNode);
      try {
        tgPanel.addNode(newNode);
      } catch (TGException e) {
        lenientAddNode(newNode);
      }

      if (createEdgesToExistingNodes) {
        if (newNode instanceof TMTopicNode) {
          TMTopicNode topicNode = (TMTopicNode)newNode;

          if (useNodeLocality())
            // Create edges to already existing nodes.
            createAllAssociations(topicNode, false);
        } else if (newNode instanceof TMAssociationNode) {
          // Create roles for topics already in the display.
          TMAssociationNode associationNode = (TMAssociationNode)newNode;
        
          // Create edges to already existing nodes.
          createAllRoles(associationNode, false);
        }
      }
    }
  }
  
  public int getLocalityAlgorithm() {
    return configman.getGeneralLocalityAlgorithm();
  }
  
  public boolean useNodeLocality() {
    return getLocalityAlgorithm() == VizTopicMapConfigurationManager
        .NODE_ORIENTED;
  }
  
  private void createAllRoles(TMAssociationNode node, boolean create) {
    Collection roles = new ArrayList(node.getAssociation().getRoles());

    Iterator edgesIt = node.getEdges();
    while (edgesIt.hasNext()) {
      TMRoleEdge roleEdge = (TMRoleEdge)edgesIt.next();
      roles.remove(roleEdge.getRole());
    }
    
    Iterator rolesIt = roles.iterator();
    while (rolesIt.hasNext()) {
      // TODO : I think this assignment and subsequent tgPanel.addNode() call
      // is unnecessary. Adding of new nodes should be handles in a collective
      // loop. Check that this works correctly, and remove the commented lines
      // TMTopicNode newNode = 
      makeRole(node, (AssociationRoleIF) rolesIt.next(), create);
      // if (newNode != null)
      //  try {
      //    tgPanel.addNode(newNode);
      //  } catch (TGException e) {
      //    lenientAddNode(newNode);
      //  }
    }
  }

  private void createAllAssociations(TMTopicNode node, boolean create) {
    createClassInstanceAssociations(node, create);
    createActualTopicMapAssociations(node, create);
  }

  private void createActualTopicMapAssociations(TMTopicNode node, boolean create) {
    TopicIF topic = node.getTopic();
    Iterator it = new ArrayList(topic.getRoles()).iterator();

    while (it.hasNext()) {
      AssociationRoleIF role = (AssociationRoleIF) it.next();
      AssociationIF assoc = role.getAssociation();
      
      VizTMObjectIF target = (VizTMObjectIF) findObject(assoc, assoc.getType());

      if (configman.isVisible(role)
          && findObject(role, assoc.getType()) == null
          && target != null)
        makeRole((TMAssociationNode) target, role, create);
      else if (configman.isVisible(assoc) && target == null) {
        if (assoc.getRoles().size() != 2)
          makeAssociation(assoc, node, create);
        else
          makeAssociation(assoc, create);
      }
    }
  }

  private void createClassInstanceAssociations(TMTopicNode node, boolean create) {
    TopicIF topic = node.getTopic();
    TopicIF classInstanceType = configman.getTypeInstanceType();

    if (configman.isAssociationTypeVisible(classInstanceType)) {

      // Add Instance-Class associations
      Iterator it = getValidTypesFor(topic).iterator();
      while (it.hasNext()) {
        TopicIF type = (TopicIF) it.next();
        if (configman.isTopicTypeVisible(type)) {
          TMTopicNode typeNode = assertNode(type, create);
          if (typeNode != null) {
            if (findObject(new Key(type, topic), classInstanceType) == null) {
              makeTypeInstanceEdge(node, typeNode);
            }
          }
        }
      }

      // Add Class-Instance associations
      Collection instances = typeix.getTopics(topic);
      if (!instances.isEmpty() && !configman.isTypeExcluded(topic)) {
        it = instances.iterator();
        while (it.hasNext()) {
          TopicIF instance = (TopicIF) it.next();
          TMTopicNode instanceNode = assertNode(instance, create);
          if (instanceNode != null) {
            if (findObject(new Key(topic, instance), classInstanceType) == null) {
              makeTypeInstanceEdge(instanceNode, node);
            }
          }
        }
      }
    }
  }
  
  public void deleteEdges(Collection edges) {
    Iterator edgesIt = edges.iterator();
    while (edgesIt.hasNext()) {
      TMAbstractEdge currentEdge = (TMAbstractEdge)edgesIt.next();

      deleteEdgeUndoable(currentEdge);
    }
  }

  public void deleteEdgeUndoable(VizTMObjectIF edge) {
    controller.undoManager.addRecovery(edge.getRecreator());
    deleteEdge(edge);
  }
    
  public void deleteEdge(VizTMObjectIF vizTMObject) {
    getHoverHelpManager().removePaintListener((TGPaintListener)vizTMObject);

    // unindex by type
    ((ArrayList) objectsByType.get(objectTypeIndex.indexOf(vizTMObject
        .getTopicMapType()))).remove(vizTMObject);
    
    if (vizTMObject instanceof TMAssociationNode) {
      // Delete n-ary association (n > 2).
      TMAssociationNode associationNode = (TMAssociationNode)vizTMObject;
      deleteNode(associationNode);
      tgPanel.deleteNode(associationNode);
    } else if (vizTMObject instanceof TMAbstractEdge) {
      TMAbstractEdge edge = (TMAbstractEdge)vizTMObject;
      
      // Schedule all adjacent nodes for count update.
      nodesUpdateCount.add(edge.getFrom());
      nodesUpdateCount.add(edge.getTo());

      // Delete role in n-ary association.
      tgPanel.deleteEdge(edge);
    }
  }
  
  public void deleteSingleEdge(VizTMObjectIF edge) {
    deleteEdgeUndoable(edge);
    removeDisconnectedNodes();
  }

  public void deleteNode(TMAssociationNode node) {
    getHoverHelpManager().removePaintListener(node);
    
    // Schedule all adjacent nodes for count update.
    Iterator edgesIt = node.getEdges();
    while (edgesIt.hasNext()) {
      nodesUpdateCount.add(((TMRoleEdge)edgesIt.next()).getOtherEndpt(node)); 
    }

    // unindex by type
    ((ArrayList) objectsByType.get(objectTypeIndex.indexOf(node
        .getTopicMapType()))).remove(node);
  }

  public void deleteNode(TMTopicNode node) {
    // I'm not sure if there's a way to find the correct type of the topic, as
    // there may be multiple.  If there is a way, it would be a great
    // improvement as the node could just be removed from the appropriate
    // collection.
    
    Iterator nodesByTypeIt = nodesByType.iterator();
    while (nodesByTypeIt.hasNext()) {
      ((Collection)nodesByTypeIt.next()).remove(node);
    }
  }

  private TMTopicNode getNode(TopicIF topic) {
    Iterator iterator = getValidTypesFor(topic).iterator();

    if (iterator.hasNext()) {
      while (iterator.hasNext()) {
        TMTopicNode node = getTopicNode(topic, (TopicIF) iterator.next());
        if (node != null)
          return node;
      }
      return null;
    }
    return getTopicNode(topic, null);
  }

  protected TMAssociationEdge getEdge(AssociationIF association) {
    TopicIF type = association.getType();

    TMAssociationEdge edge = null;
    Iterator iter = getObjectsFor(type).iterator();
    while (edge == null && iter.hasNext()) {
      VizTMObjectIF element = (VizTMObjectIF)iter.next();
      // Need to check if element is TMAssociationEdge, as there may well exist
      // turnary associations of the same type.
      if (element instanceof TMAssociationEdge &&
          ((TMAssociationEdge)element).getAssociation().equals(association))
        edge = (TMAssociationEdge)element;
    }
    
    if (edge != null)
      return edge;

    return null;
  }

  protected TMRoleEdge getEdge(AssociationRoleIF role) {
    TopicIF type = role.getAssociation().getType();

    TMRoleEdge edge = null;
    Iterator iter = getObjectsFor(type).iterator();
    while (edge == null && iter.hasNext()) {
      VizTMObjectIF element = (VizTMObjectIF)iter.next();
      // Need to check if element is TMAssociationEdge, as there may well exist
      // turnary associations of the same type.
      if (element instanceof TMRoleEdge &&
          ((TMRoleEdge)element).getRole().equals(role))
        edge = (TMRoleEdge)element;
    }
    
    if (edge != null)
      return edge;

    return null;
  }

  protected TMClassInstanceAssociation getEdge(TopicIF type,
                                               TopicIF instance) {
    return (TMClassInstanceAssociation)findObject(
        new Key(type, instance), configman.getTypeInstanceType());
  }

  protected TMAssociationNode getNode(AssociationIF association) {
    TopicIF type = association.getType();

    TMAssociationNode node = getAssociationNode(association, type);
    if (node != null)
      return node;

    return null;
  }

  private TMTopicNode getTopicNode(TopicIF topic, TopicIF type) {
    Iterator iter = getTopicNodesFor(type).iterator();
    while (iter.hasNext()) {
      TMTopicNode element = (TMTopicNode) iter.next();
      if (element.getTopic().equals(topic))
        return element;
    }
    return null;
  }

  private TMAssociationNode getAssociationNode(AssociationIF assoc, 
                                               TopicIF type) {
    Iterator iter = getObjectsFor(type).iterator();
    while (iter.hasNext()) {
      VizTMObjectIF element = (VizTMObjectIF)iter.next();
      // Need to check if element is TMAssociationNode, as there may well exist
      // binary associations of the same type.
      if (element instanceof TMAssociationNode &&
          ((TMAssociationNode)element).getAssociation().equals(assoc))
        return (TMAssociationNode)element;
    }
    return null;
  }

  public void shouldDisplayRoleHoverHelp(boolean newValue) {
    Iterator objectCollections = objectsByType.iterator();
    while (objectCollections.hasNext()) {
      Iterator objects = ((ArrayList) objectCollections.next()).iterator();
      while (objects.hasNext()) {
        VizTMObjectIF object = (VizTMObjectIF) objects.next();
        if (object.isEdge())
          ((TMAbstractEdge) object).setShouldDisplayRoleHoverHelp(newValue);
      }
    }
  }
  
  public void setMotionKillerEnabled(boolean enabled) {
    motionKiller.setEnabled(enabled);
  }

  public void shouldDisplayScopedAssociationNames(boolean newValue) {
    Iterator objectCollections = objectsByType.iterator();
    while (objectCollections.hasNext()) {
      Iterator objects = ((ArrayList) objectCollections.next()).iterator();
      while (objects.hasNext()) {
        VizTMObjectIF object = (VizTMObjectIF) objects.next();
        if (object.isAssociation())
          ((VizTMAssociationIF) object)
              .setShouldDisplayScopedAssociationNames(newValue);
      }
    }
  }

  public void setPanelBackgroundColour(Color aColor) {
    tgPanel.setBackColor(aColor);
    tgPanel.repaint();
  }

  public void removeDisconnectedNodes() {  
    TMAbstractNode target = getFocusNode();
    
    // In map view do nothing.
    if (target == null)
      return;

    // Get the connected graph starting from target.
    Graph connectedGraph = getConnectedGraph(target);
    
    // Get all nodes not part of connectedGraph.
    Collection connectedNodes = connectedGraph.getNodes();
    Vector disconnectedNodes = new Vector();
    Iterator viewNodesIt = tgPanel.getAllNodes();
    while (viewNodesIt.hasNext()) {
      TMAbstractNode currentNode = (TMAbstractNode)viewNodesIt.next();
      
      if (!connectedNodes.contains(currentNode))
        disconnectedNodes.add(currentNode);
    }
    
    // Get and filter out any non-TMTopicNode Nodes from connectedNodes.
    Collection connectedObjects = new ArrayList();
    Iterator connectedNodesIt = connectedNodes.iterator();
    while (connectedNodesIt.hasNext()) {
      TMAbstractNode currentNode = (TMAbstractNode)connectedNodesIt.next();
      if (!(currentNode instanceof TMTopicNode)) {
        connectedObjects.add(currentNode);
        connectedNodesIt.remove();
      }
    }
    
    // Get all edges not part of connectedGraph.
    Collection connectedEdges = connectedGraph.getEdges();
    Vector disconnectedEdges = new Vector();
    Iterator viewEdgesIt = tgPanel.getAllEdges();
    if (viewEdgesIt != null) {
      while (viewEdgesIt.hasNext()) {
        TMAbstractEdge currentEdge = (TMAbstractEdge)viewEdgesIt.next();
        
        if (!connectedEdges.contains(currentEdge))
          disconnectedEdges.add(currentEdge);
      }
    }
    
    connectedObjects.addAll(connectedEdges);
    
    // Remove all nodes not part of connectedGraph
    deleteNodes(disconnectedNodes);
    retainNodes(connectedNodes);
    retainObjects(connectedObjects);    
    
    // Remove all edges not part of connectedGraph
    ((Locality) tgPanel.getGES()).removeEdges(disconnectedEdges);
    deleteEdges(disconnectedEdges);
  }
  
  private void deleteNodes(Vector nodes) {
    Iterator nodesIt = nodes.iterator();
    while (nodesIt.hasNext()) {
      TMAbstractNode currentNode = (TMAbstractNode)nodesIt.next();
      deleteNode(currentNode);
    }
    ((Locality) tgPanel.getGES()).deleteNodes(nodes);
  }

  private void deleteNode(TMAbstractNode node) {
    controller.undoManager.addRecovery(node.getRecreator());
  }

  /**
   * @param target an arbitrary node of the connected graph to be returned.
   * @return a Set containing all nodes of the connected graph.
   */
  private Graph getConnectedGraph(TMAbstractNode target) {
    Set graphNodes = new HashSet();
    Set graphEdges = new HashSet();
    Graph retVal = new Graph(graphNodes, graphEdges);
    
    if (target == null)
      return retVal;
    
    graphNodes.add(target);
    
    // Process the edges of the graph, adding edges as new nodes are visited.
    List edgeQueue = target.getVisibleEdgesList();
    while (!edgeQueue.isEmpty()) {
      TMAbstractEdge edge = (TMAbstractEdge)edgeQueue.remove(0);
      graphEdges.add(edge);
      
      TMAbstractNode currentNode = (TMAbstractNode)edge.getFrom();
      
      // At most one of the nodes of edge may not already be in graphNodes.
      if (graphNodes.contains(currentNode))
        currentNode = (TMAbstractNode)edge.getTo();
      
      // If a node of the edge has not yet been visited.
      if (!graphNodes.contains(currentNode)) {
        // Visit the node.
        graphNodes.add(currentNode);
        
        Collection additionalEdges = currentNode.getVisibleEdgesList();
        
        // Don't reprocess the edge just visited.
        additionalEdges.remove(edge);
        
        // Schedule for processing: visible edges incident to currentNode.
        edgeQueue.addAll(additionalEdges);
      }
    }
    
    return retVal;
  }

  public void setTopicTypeExcluded(TopicIF aType, boolean excluded) {
    if (excluded) {
      // aType has been excluded.
      // - Remove nodes of this type from the local index
      // - Re-initialize the topics by there new primary type
      // - If there new primary type is null (i.e. untyped) then
      // index by this type
      List objects = getTopicNodesFor(aType);
      int index = nodeTypeIndex.indexOf(aType);
      nodesByType.set(index, new ArrayList());

      Iterator it = objects.iterator();
      while (it.hasNext()) {
        TMTopicNode node = (TMTopicNode) it.next();
        TopicIF primaryType = getPrimaryTypeFor(node);
        initializeNode(node, primaryType);
        if (primaryType == null)
          indexNode(node, null);
        Key key = new Key(aType, node.getTopic());

        // We need to take a copy of the edges collection since
        // it we may be removing edges below without using the
        // iterator directly.
        ArrayList edgesCopy = new ArrayList(node.edgeCount());
        for (int i = 0; i < node.edgeCount(); i++) {
          edgesCopy.add(node.edgeAt(i));
        }

        for (Iterator edgesIt = edgesCopy.iterator(); edgesIt.hasNext();) {
          TMAbstractEdge edge = (TMAbstractEdge) edgesIt.next();
          if (edge.represents(key)) {
            deleteEdgeUndoable(edge);
          }
        }
      }
    } else {
      // aType has been included.
      // - Check all active objects to see if they are of the included type.
      // if so, re-index by the included type and re-initialize
      // by there primary type (which may have changed).

      ArrayList nodesByTypeCopy = new ArrayList(nodesByType);
      int ignore = nodeTypeIndex.indexOf(aType);
      for (int i = 0; i < nodesByTypeCopy.size(); i++) {
        if (i != ignore) {
          List objects = (List) nodesByType.get(i);
          for (Iterator objectsIt = objects.iterator(); objectsIt.hasNext();) {
            TMTopicNode node = (TMTopicNode) objectsIt.next();
            if (node.getTopic().getTypes().contains(aType)) {
              indexNode(node, aType);
              initializeNode(node);
              makeTypeInstanceEdge(node, assertNode(aType, true));
            }
          }
        }
      }
    }
  }

  public List performSearch(String searchString) {
    // Do not search nodes that are not in the current locality.

    ArrayList results = new ArrayList();
    String pattern = searchString.toLowerCase();
    for (Iterator typesIt = nodesByType.iterator(); typesIt
        .hasNext();) {
      ArrayList collection = (ArrayList) typesIt.next();
      for (Iterator topicsIt = collection.iterator(); topicsIt
          .hasNext();) {
        TMTopicNode node = (TMTopicNode) topicsIt.next();
        if (node.isVisible() && searchTopicFor(pattern, node))
          results.add(node);
      }
    }
    return results;
  }
  
  private static boolean matchesIgnoreCase(String source, String pattern) {
    return source.toLowerCase().indexOf(pattern) != -1;
  }
  
  private boolean searchTopicFor(String pattern, TMTopicNode node) {
    Iterator namesIt = node.getTopic().getTopicNames().iterator();
    while (namesIt.hasNext()) {
      TopicNameIF currentName = (TopicNameIF)namesIt.next();
      if (matchesIgnoreCase(currentName.getValue(), pattern))
        return true;
      
      Iterator variantsIt = currentName.getVariants().iterator();
      while (variantsIt.hasNext()) {
        VariantNameIF currentVariant = (VariantNameIF)variantsIt.next();
        if (matchesIgnoreCase(currentVariant.getValue(), pattern))
          return true;
      }
    }
    return false;
  }

  public int getLocality() {
    return locality;
  }

  public TMTopicNode getStartNode() {
    TopicIF startTopic = controller.getStartTopic(topicmap);
    if (startTopic == null)
      return null;
    return assertNode(startTopic, true);
  }

  public TMAbstractNode getFocusNode() {
    return (TMAbstractNode) tgPanel.getSelect();
  }

  public TGPanel getTGPanel() {
    return tgPanel;
  }

  public void outputDebugInfo(String operation) {
    debug.execute(operation);
  }

  private int getNodeCount() {
    // Get the number of nodes currently loaded
    int count = 0;
    for (Iterator nodesIt = nodesByType.iterator(); nodesIt.hasNext();)
      count = count + ((List) nodesIt.next()).size();

    return count;
  }

  public void setScopingTopic(TopicIF aScope) {
    currentScopingTopic = aScope;

    for (Iterator objectsIt = objectsByType.iterator(); objectsIt.hasNext();) {
      List objects2It = (List) objectsIt.next();
      for (Iterator iterator = objects2It.iterator(); iterator.hasNext();) {
        ((VizTMObjectIF) iterator.next()).setScopingTopic(aScope);
      }
    }

    for (Iterator nodesIt = nodesByType.iterator(); nodesIt.hasNext();) {
      List objects = (List) nodesIt.next();
      for (Iterator objectsIt = objects.iterator(); objectsIt.hasNext();) {
        ((TMTopicNode) objectsIt.next()).setScopingTopic(aScope);
      }
    }

    tgPanel.repaint();
  }

  public void clearModel() {
    tgPanel.clearAll();
    tgPanel.clearSelect();
    tgPanel.setGraphEltSet(new GraphEltSet());
  }

  // -------------- Nested classes and interfaces ------------
  
  private class Graph {
    private Set nodes;
    private Set edges;
    
    public Graph(Set nodes, Set edges) {
      this.nodes = nodes;
      this.edges = edges;
    }
    
    public Set getNodes() {
      return nodes;
    }
    
    public Set getEdges() {
      return edges;
    }
  }

  /**
   * INTERNAL: PRIVATE: Purpose: Output debug information
   */
  protected class Debug {
    private boolean reportAndCrash = false;

    protected void execute(String operation) {
      if ("count".equals(operation))
        counts();
      if ("gc".equals(operation))
        collectGarbage();
      if ("paint".equals(operation))
        paintNodes(true);
      if ("paint-h".equals(operation))
        paintNodes(false);
      if ("paint-all".equals(operation))
        paintNodes();
      if ("assoc-v".equals(operation))
        outputAssociaitonNames(true);
      if ("h-assoc-h".equals(operation))
        outputAssociaitonNames(false);
    }

    private void collectGarbage() {
      Runtime runtime = Runtime.getRuntime();
      long total = runtime.totalMemory();
      long free = runtime.freeMemory();
      System.out.print("Memory - total: ");
      System.out.print(total);
      System.out.print(" free: ");
      System.out.print(free);
      System.out.print(" used: ");
      System.out.println(total - free);
      System.out.println("Performing GC");
      runtime.gc();
      total = runtime.totalMemory();
      free = runtime.freeMemory();
      System.out.print("Memory - total: ");
      System.out.print(total);
      System.out.print(" free: ");
      System.out.print(free);
      System.out.print(" used: ");
      System.out.println(total - free);
    }

    /**
     * 
     */
    private void outputAssociaitonNames(boolean visible) {
      for (Iterator objectsIt = getObjectsOfType(TMAbstractEdge.class, visible)
          .iterator(); objectsIt.hasNext();) {
        VizTMObjectIF object = (VizTMObjectIF) objectsIt.next();
        System.out.println(((TMAbstractEdge) object).getMainHoverHelpText());
      }

      for (Iterator objectsIt = getObjectsOfType(TMAssociationNode.class, 
          visible).iterator(); objectsIt.hasNext();) {
        TMAssociationNode object = (TMAssociationNode) objectsIt.next();
        System.out.println(object.getMainText());
      }
    }

    private void counts() {
      System.out
          .println("--------------------------------------" +
                   "--------------------------------------");

      System.out.print("Total Node Count = (viz) ");
      int totalNodeCount = getNodesOfType(Node.class, false).size()
          + getObjectsOfType(Node.class, false).size();
      System.out.print(totalNodeCount);
      System.out.print(" (");
      int totalNodeCount_distinct = getNodesOfType(Node.class, true).size()
          + getObjectsOfType(Node.class, true).size();
      System.out.print(totalNodeCount_distinct);
      System.out.print(") - (TouchGraph) ");
      int totalNodeCountTG = getTGNodesOfType(Node.class, false).size();
      System.out.print(totalNodeCountTG);
      System.out.print(" (");
      int totalNodeCountTG_distinct = getTGNodesOfType(Node.class, true).size();
      System.out.print(totalNodeCountTG_distinct);
      System.out.println(")");

      System.out.print("Visible Node Count (TouchGraph) = ");
      int visibleNodeCountTG = getTGNodesOfType(Node.class, true, true).size();
      System.out.print(visibleNodeCountTG);
      System.out.print("; Hidden Node Count (TouchGraph) = ");
      int hiddenNodeCountTG = getTGNodesOfType(Node.class, true, false).size();
      System.out.println(hiddenNodeCountTG);
      
      System.out.print("Total Edge Count = (viz) ");
      int totalEdgeCount = getObjectsOfType(Edge.class, false).size();
      System.out.print(totalEdgeCount);
      System.out.print(" (");
      int totalEdgeCount_distinct = getObjectsOfType(Edge.class, true).size();
      System.out.print(totalEdgeCount_distinct);
      System.out.print(") - (TouchGraph) ");
      int totalEdgeCountTG = getTGEdgesOfType(Edge.class, false).size();
      System.out.print(totalEdgeCountTG);
      System.out.print(" (");
      int totalEdgeCountTG_distinct = getTGEdgesOfType(Edge.class, true).size();
      System.out.print(totalEdgeCountTG_distinct);
      System.out.println(")");

      System.out.print("Visible Edge Count (TouchGraph) = ");
      int visibleEdgeCount = getTGEdgesOfType(Edge.class, true, true).size();
      System.out.print(visibleEdgeCount);
      System.out.print("; Hidden Edge Count (TouchGraph) = ");
      int hiddenEdgeCount = getTGEdgesOfType(Edge.class, true, false).size();
      System.out.println(hiddenEdgeCount);
      
      System.out.print("Total Topic Count = (viz) ");
      int totalTopicCount = getNodesOfType(TMTopicNode.class, false).size();
      System.out.print(totalTopicCount);
      System.out.print(" (");
      int totalTopicCount_distinct =
          getNodesOfType(TMTopicNode.class, true).size();
      System.out.print(totalTopicCount_distinct);
      System.out.print(") - (TouchGraph) ");
      int totalTopicCountTG = getTGNodesOfType(TMTopicNode.class, false).size();
      System.out.print(totalTopicCountTG);
      System.out.print(" (");
      int totalTopicCountTG_distinct =
          getTGNodesOfType(TMTopicNode.class, true).size();
      System.out.print(totalTopicCountTG_distinct);
      System.out.println(")");

      System.out.print("Total Association Count = (viz) ");
      int totalAssociationCount =
          getObjectsOfType(TMAssociationEdge.class, false).size() +
          getObjectsOfType(TMAssociationNode.class, false).size(); 
      System.out.print(totalAssociationCount);
      System.out.print(" (");
      int totalAssociationCount_distinct =
          getObjectsOfType(TMAssociationEdge.class, true).size() +
          getObjectsOfType(TMAssociationNode.class, true).size();
      System.out.print(totalAssociationCount_distinct);
      System.out.print(") - (TouchGraph) ");
      int totalAssociationCountTG =
          getTGEdgesOfType(TMAssociationEdge.class, false).size() +
          getTGNodesOfType(TMAssociationNode.class, false).size();
      System.out.print(totalAssociationCountTG);
      System.out.print(" (");
      int totalAssociationCountTG_distinct =
          getTGEdgesOfType(TMAssociationEdge.class, true).size() +
          getTGNodesOfType(TMAssociationNode.class, true).size();
      System.out.print(totalAssociationCountTG_distinct);
      System.out.println(")");

      System.out.print("Total Role Count = (viz) ");
      int totalRoleCount = getObjectsOfType(TMRoleEdge.class, false).size();
      System.out.print(totalRoleCount);
      System.out.print(" (");
      int totalRoleCount_distinct =
          getObjectsOfType(TMRoleEdge.class, true).size();
      System.out.print(totalRoleCount_distinct);
      System.out.print(") - (TouchGraph) ");
      int totalRoleCountTG = getTGEdgesOfType(TMRoleEdge.class, false).size();
      System.out.print(totalRoleCountTG);
      System.out.print(" (");
      int totalRoleCountTG_distinct =
          getTGEdgesOfType(TMRoleEdge.class, true).size();
      System.out.print(totalRoleCountTG_distinct);
      System.out.println(")");
      
      int hiddenNodes = getTGNodesOfType(Node.class, true, false).size();
      if (hiddenNodes != 0)
        System.out.println("WARNING! - There are " + hiddenNodes
            + " - hidden nodes, but no nodes should ever be hidden!");
      int hiddenEdges = getTGEdgesOfType(Edge.class, true, false).size();
      if (hiddenEdges != 0)
        System.out.println("WARNING! - There are " + hiddenEdges
            + " - hidden edges, but no edges should ever be hidden!");
      
      if (VizDebugUtils.isDebugFailMode()) {
        if (totalNodeCount_distinct != totalNodeCountTG_distinct)
          throw new OntopiaRuntimeException("The number of distinct nodes in" +
              " Vizigator (" + totalNodeCount_distinct + ") does not match the" +
              " number of distinct nodes in TouchGraph(" +
              totalNodeCountTG_distinct + ").");
      } else {
        if (totalNodeCount_distinct != totalNodeCountTG_distinct)
          System.out.println("WARNING - The number of distinct nodes in" +
              " Vizigator (" + totalNodeCount_distinct + ") does not match the" +
              " number of distinct nodes in TouchGraph(" +
              totalNodeCountTG_distinct + ").");
      }
    }

    protected Collection getTGNodesOfType(Class aClass, boolean distinct, 
        boolean visible) {
      Collection nodesOfType = getTGNodesOfType(aClass,  distinct );
      ArrayList result = new ArrayList(nodesOfType.size());
      for (Iterator iter = nodesOfType.iterator(); iter.hasNext();) {
        TMAbstractNode element = (TMAbstractNode) iter.next();
        if (element.isVisible() == visible) result.add(element);
      }
      return result;
    }

    protected Collection getTGEdgesOfType(Class aClass, boolean distinct, 
        boolean visible) {
      Collection edgesOfType = getTGEdgesOfType(aClass,  distinct );
      ArrayList result = new ArrayList(edgesOfType.size());
      for (Iterator iter = edgesOfType.iterator(); iter.hasNext();) {
        Edge element = (Edge) iter.next();
        if (element.isVisible() == visible) result.add(element);
      }
      return result;
    }

    protected Collection getTGEdgesOfType(Class aClass, boolean distinct) {
      Collection result;
      if (distinct)
        result = new HashSet();
      else
        result = new ArrayList();

      Iterator edgesIt = tgPanel.getAllEdges();
      if (edgesIt != null) {
        while (edgesIt.hasNext()) {
          Object object = edgesIt.next();
          if (aClass.isInstance(object))
            result.add(object);
        }
      }

      return result;
    }

    protected Collection getTGNodesOfType(Class aClass, boolean distinct) {
      Collection result;
      if (distinct)
        result = new HashSet();
      else
        result = new ArrayList();

      Iterator nodesIt = tgPanel.getAllNodes();
      if (nodesIt != null) {
        while (nodesIt.hasNext()) {
          Object object = nodesIt.next();
          if (aClass.isInstance(object))
            result.add(object);
        }
      }

      return result;
    }

    private void paintNodes(boolean visible) {
      Hashtable distances = new Hashtable();
      if (getFocusNode() != null) {
        distances = GESUtils.calculateDistances((GraphEltSet) tgPanel.getGES(),
            getFocusNode(), 100);
      }

      for (Iterator topicByType = new ArrayList(nodesByType).iterator(); topicByType
          .hasNext();) {
        for (Iterator topics = (new ArrayList((List) topicByType.next()))
            .iterator(); topics.hasNext();) {
          TMTopicNode node = (TMTopicNode) topics.next();
          if (node.isVisible() == visible) {
            Integer distance = (Integer) distances.get(node);
            char value = 'X';
            if (distance != null)
              value = Character.forDigit(distance.intValue(), 10);
            node.paintSmallTag(tgPanel.getGraphics(), tgPanel,
                (int) node.drawx, (int) node.drawy, Color.black, Color.white,
                value);
          }
        }
      }
    }

    private void paintNodes() {
      Hashtable distances = new Hashtable();
      if (getFocusNode() != null) {
        distances = GESUtils.calculateDistances((GraphEltSet) tgPanel.getGES(),
            getFocusNode(), 100);
      }

      for (Iterator topicByType = new ArrayList(nodesByType).iterator(); topicByType
          .hasNext();) {
        for (Iterator topics = (new ArrayList((List) topicByType.next()))
            .iterator(); topics.hasNext();) {
          TMTopicNode node = (TMTopicNode) topics.next();
          Integer distance = (Integer) distances.get(node);
          char value = 'X';
          if (distance != null)
            value = Character.forDigit(distance.intValue(), 10);
          node.paintSmallTag(tgPanel.getGraphics(), tgPanel, (int) node.drawx,
              (int) node.drawy, Color.black, Color.white, value);
        }
      }
    }

    public Collection getObjectsOfType(Class type, boolean distinct) {
      Collection result;
      if (distinct)
        result = new HashSet();
      else
        result = new ArrayList();

      for (Iterator collections = objectsByType.iterator(); collections
          .hasNext();) {
        for (Iterator objects = ((List) collections.next()).iterator(); objects
            .hasNext();) {
          Object object = objects.next();
          if (type.isInstance(object))
            result.add(object);
        }
      }
      return result;
    }
    
    public Collection getNodesOfType(Class type, boolean distinct) {
      Collection result;
      if (distinct)
        result = new HashSet();
      else
        result = new ArrayList();

      for (Iterator collections = nodesByType.iterator(); collections.hasNext();) {
        for (Iterator objects = ((List) collections.next()).iterator(); objects
            .hasNext();) {
          Object object = objects.next();
          if (type.isInstance(object))
            result.add(object);
        }
      }
      return result;
    }
    
    public void integrityCheck() {
      if (!VizDebugUtils.isDebugEnabled())
        return;
      
      VizDebugUtils.debug("-------------- Start of Integrity check.");
      
      // Nodes in Vizigator
      Collection nodesViz = getNodesOfType(Node.class, false);
      Collection objectsViz = getObjectsOfType(Node.class, false);

      // Distinct nodes in Vizigator
      Collection nodesViz_distinct = getNodesOfType(Node.class, true);
      Collection objectsViz_distinct = getObjectsOfType(Node.class, true);
      
      // Nodes in TouchGraph
      Collection nodesTG = getTGNodesOfType(Node.class, false);
      
      // Distinct nodes in TouchGraph
      Collection nodesTG_distinct = getTGNodesOfType(Node.class, true);

      // Visible nodes in TouchGraph
      Collection visibleNodesTG = getTGNodesOfType(Node.class, true, true);
      
      // Hidden nodes in TouchGraph
      Collection hiddenNodesTG = getTGNodesOfType(Node.class, true, false);
      
      // Edges in Vizigator
      Collection edgesViz = getObjectsOfType(Edge.class, false);
      
      // Distinct edges in Vizigator
      Collection edgesViz_distinct = getObjectsOfType(Edge.class, true);
      
      // Edges in TouchGraph
      Collection edgesTG = getTGEdgesOfType(Edge.class, false);
      
      // Distinct edges in TouchGraph
      Collection edgesTG_distinct = getTGEdgesOfType(Edge.class, true);
      
      // Visible edges in TouchGraph
      Collection visibleEdgesTG = getTGEdgesOfType(Edge.class, true, true);
      
      // Hidden edges in TouchGraph
      Collection hiddenEdgesTG = getTGEdgesOfType(Edge.class, true, false);
      
      // Topics in Viz
      Collection topicsViz_distinct = getNodesOfType(TMTopicNode.class,
                                                           true);

      // Topics in TG
      Collection topicsTG = getTGNodesOfType(TMTopicNode.class, false);
      Collection topicsTG_distinct = getTGNodesOfType(TMTopicNode.class,
                                                            true);
      
      // Associations in Viz
      Collection assocEdgesViz = getObjectsOfType(TMAssociationEdge.class, 
                                                        false);
      Collection assocNodesViz = getObjectsOfType(TMAssociationNode.class,
                                                        false); 
      
      // Distinct associations in Viz
      Collection assocEdgesViz_distinct = 
          getObjectsOfType(TMAssociationEdge.class, true);
      Collection assocNodesViz_distinct =
          getObjectsOfType(TMAssociationNode.class, true);

      // Associations in TouchGraph
      Collection assocEdgesTG = getTGEdgesOfType(TMAssociationEdge.class,
                                                       false);
      Collection assocNodesTG = getTGNodesOfType(TMAssociationNode.class,
                                                       false);

      // Distinct associations in TouchGraph
      Collection assocEdgesTG_distinct =
          getTGEdgesOfType(TMAssociationEdge.class, true);
      Collection assocNodesTG_distinct =
          getTGNodesOfType(TMAssociationNode.class, true);

      // Roles in Viz
      Collection rolesViz = getObjectsOfType(TMRoleEdge.class, false);
      
      // Distinct roles in Viz
      Collection rolesViz_distinct = getObjectsOfType(TMRoleEdge.class,
                                                            true);

      Collection rolesTG = getTGEdgesOfType(TMRoleEdge.class, false);
      Collection rolesTG_distinct = getTGEdgesOfType(TMRoleEdge.class, 
                                                           true);

      assertEmpty(hiddenNodesTG, "hidden nodes");
      assertEmpty(hiddenEdgesTG, "hidden edges");
      
      // nodesViz and nodesViz_distinct need not have the same size.
      // nodesViz just double counts nodes whose topics have multiple types.
      // assertSameSize(nodesViz, nodesViz_distinct, 
      //                "nodesViz", "nodesViz_distinct");

      assertSameSize(objectsViz, objectsViz_distinct, 
                     "objectsViz", "objectsViz_distinct");
      
      Collection allDistinctNodes = new ArrayList(nodesViz_distinct);
      allDistinctNodes.addAll(objectsViz_distinct);
      assertSameContents(allDistinctNodes, nodesTG,
          "allDistinctNodes",
          "nodesTG");
      assertEqual(nodesViz_distinct.size() + objectsViz_distinct.size(),
          nodesTG.size(), "the number of distinct nodes in Vizigator",
          "the number of nodes in TouchGraph");

      assertSameSize(nodesTG, nodesTG_distinct, 
          "nodesTG", "nodesTG_distinct");
      assertSameSize(nodesTG, visibleNodesTG, 
          "nodesTG", "visibleNodesTG");
      
      assertSameSize(edgesViz, edgesViz_distinct, 
          "edgesViz", "edgesViz_distinct");
      assertSameSize(edgesTG, edgesTG_distinct, 
          "edgesTG", "edgesTG_distinct");
      assertSameSize(edgesViz, edgesTG, 
          "edgesViz", "edgesTG");
      assertSameSize(edgesTG, visibleEdgesTG, 
          "edgesTG", "visibleEdgesTG");
      
      // topicsViz and topicsViz_distinct need not have the same size.
      // topicsViz just double counts nodes whose topics have multiple types.
      // assertSameSize(topicsViz, topicsViz_distinct, 
      //     "topicsViz", "topicsViz_distinct");
      
      assertSameSize(topicsTG, topicsTG_distinct, 
          "topicsTG", "topicsTG_distinct");
      assertSameSize(topicsTG, topicsViz_distinct, 
          "topicsTG", "topicsViz_distinct");
      
      assertSameSize(assocEdgesViz, assocEdgesViz_distinct, 
          "assocEdgesViz", "assocEdgesViz_distinct");
      assertSameSize(assocEdgesTG, assocEdgesTG_distinct, 
          "assocEdgesTG", "assocEdgesTG_distinct");
      assertSameSize(assocEdgesViz, assocEdgesTG, 
          "assocEdgesViz", "assocEdgesTG");

      assertSameSize(assocNodesViz, assocNodesViz_distinct, 
          "assocNodesViz", "assocNodesViz_distinct");
      assertSameSize(assocNodesTG, assocNodesTG_distinct, 
          "assocNodesTG", "assocNodesTG_distinct");
      assertSameSize(assocNodesViz, assocNodesTG, 
          "assocNodesViz", "assocNodesTG");

      assertSameSize(rolesViz, rolesViz_distinct, 
          "rolesViz", "rolesViz_distinct");
      assertSameSize(rolesTG, rolesTG_distinct, 
          "rolesViz", "rolesViz_distinct");
      assertSameSize(rolesViz, rolesViz_distinct, 
          "rolesViz", "rolesViz_distinct");
      
      Collection allNodes = new HashSet(nodesViz);
      allNodes.addAll(objectsViz);
      assertSameContents(allNodes, nodesTG, 
                         "allNodes", "nodesTG");
      
      VizDebugUtils.debug("-------------- End of Integrity check.");
      if (reportAndCrash)
        reportAndExit();
    }
    
    private void assertEmpty(Collection coll, String whatsThere) {
      assertTrue(coll.isEmpty(), "WARNING! There are " + coll.size() + 
            " " + whatsThere + ".");
    }
    
    private boolean assertSameSize(Collection coll1, Collection coll2,
                                String name1, String name2) {
      return assertTrue(coll1.size() == coll2.size(), "WARNING! " + 
          leadCap(name1) + " has size " + coll1.size() + ", but " + name2 + 
          " has size " + coll2.size() + ". " + 
          "They should have the same size.");
    }

    private boolean assertSameContents(Collection coll1, Collection coll2, 
                                    String name1, String name2) {
      boolean retVal = assertSameSize(coll1, coll2, name1, name2);
      
      Iterator coll1It = coll1.iterator();
      while (coll1It.hasNext()) {
        Object current = coll1It.next();
        String label = current.toString();
        if (current instanceof TMAbstractNode) {
          label = ((TMAbstractNode)current).getLabel();
        }
        if (!assertTrue(coll2.contains(current), 
            "The element " + label + " from the collection " + name1 +
            " could not be found in the collection " + name2 + "."))
          retVal = false;
      }
      
      Iterator coll2It = coll2.iterator();
      while (coll2It.hasNext()) {
        Object current = coll2It.next();
        String label = current.toString();
        if (current instanceof TMAbstractNode) {
          label = ((TMAbstractNode)current).getLabel();
        }
        if (!assertTrue(coll1.contains(current),
            "The element " + label + " from the collection " + name2 +
            " could not be found in the collection " + name1 + "."))
          retVal = false;
      }

      return retVal;
    }

    private boolean assertEqual(int number1, int number2, String name1,
        String name2) {
      return assertTrue(number1 == number2, "WARNING! " + leadCap(name1) + 
          " is " + number1 + ", but " + name2 + " is " + 
          number2 + ". " + "They should be equal.");
    }

    private boolean assertTrue(boolean asserted, String message) {
      if (!asserted) {
        VizDebugUtils.debug(message);
        reportAndCrash = true;
      }
      return asserted;
    }
    
    private void reportAndExit() {
      if (!VizDebugUtils.isDebugEnabled())
        return;
      System.out.println("FOUND INCONSISTENCIES (SEE ABOVE)");
      System.out.println("DISABLING VIZIGATOR USER");
      vizigatorUser.enabled = false;
      // System.out.println("FOUND INCONSISTENCIES (SEE ABOVE)");
      // System.out.println("EXITING VIZIGATOR");
      // System.exit(1);
    }
    
    private String leadCap(String source) {
      String lead = source.substring(0, 1);
      String rest = source.substring(1);
      return lead.toUpperCase() + rest;
    }
  }
  
  /**
   * Sets all nodes to fixed (sticky) or not fixed.
   * @param fixed true(/false) if all nodes should get a (un)fixed position.
   */
  public void setAllNodesFixed(boolean fixed) {
    Iterator nodesByTypeIt = nodesByType.iterator();
    while (nodesByTypeIt.hasNext()) {
      Collection typeNodes = (Collection)nodesByTypeIt.next();
      Iterator typeNodesIt = typeNodes.iterator();
      while (typeNodesIt.hasNext()) {
        TMAbstractNode currentNode = (TMAbstractNode)typeNodesIt.next();
        currentNode.setFixed(fixed);
      }
    }
  }

  public int getMaxTopicNameLength() {
    return maxTopicNameLength;
  }

  public void setMaxTopicNameLength(int length) {
    this.maxTopicNameLength = length;
    Iterator nodesByTypeIt = this.nodesByType.iterator();
    while (nodesByTypeIt.hasNext()) {
      Collection nodesOfCurrentType = (Collection)nodesByTypeIt.next();
      Iterator nodesOfCurrentTypeIt = nodesOfCurrentType.iterator();
      while (nodesOfCurrentTypeIt.hasNext()) {
        TMTopicNode currentNode = (TMTopicNode)nodesOfCurrentTypeIt.next();
        currentNode.updateLabel();
      }
    }
    updateDisplayNoWork();
  }
}
