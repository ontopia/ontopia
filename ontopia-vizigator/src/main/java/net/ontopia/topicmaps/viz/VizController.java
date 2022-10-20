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
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.impl.remote.RemoteTopicMapStore;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.topicmaps.utils.tmrap.RemoteTopicIndex;
import net.ontopia.utils.OntopiaRuntimeException;

import com.touchgraph.graphlayout.Node;
import com.touchgraph.graphlayout.TGPaintListener;
import com.touchgraph.graphlayout.TGPanel;
import com.touchgraph.graphlayout.graphelements.Locality;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * INTERNAL: The VizController manages the interaction between the
 * gui, model and configuration manager.
 */
public class VizController {
  private TopicMapView view; // null if no TM loaded

  private ApplicationContextIF appContext;
  private VizTopicMapConfigurationManager tmConfig;
  private VizGeneralConfigurationManager generalConfig;
  private VizPanel vpanel;
  private PropertiesFrame propertiesFrame;

  private boolean ignoreStateChangedEvent = false;
  private boolean assocTypesLoaded = false;
  private boolean topicTypesLoaded = false;
  
  private static final String SHORT_NAME = "http://psi.ontopia.net/basename/" +
      "#short-name";

  private Function<TopicIF, String> stringifier;

  private VizHoverHelpManager hoverHelpManager;
  private HighlightNode highlightNode;

  // This KeyInputManager is never referenced, but it handles keyboard inputs.
  private KeyInputManager keyMan;
  
  protected UndoManager undoManager;
  
  protected boolean showNeighbouringCircle = false;
  protected boolean showNeighboursOnMouseover = false;

  public VizController(VizPanel vpanel, VizFrontEndIF vizFrontEnd, TGPanel aTgPanel) {
    undoManager = new UndoManager(this);
    this.vpanel = vpanel;
    hoverHelpManager = new VizHoverHelpManager(aTgPanel);
    appContext = vizFrontEnd.getContext();
    appContext.setVizPanel(vpanel);

    if (vizFrontEnd.useGeneralConfig()) {
      File configFile = getGeneralConfigurationFile();
      if (!configFile.exists()) {
        generalConfig = new VizGeneralConfigurationManager();
      } else {
        try {
          generalConfig = new VizGeneralConfigurationManager(configFile);
        } catch (IOException e) {
          ErrorDialog.showError(vpanel, Messages.getString("Viz.ErrorLoadingConfig"), e);
          generalConfig = new VizGeneralConfigurationManager();
        }
      }
    }

    String configurl = null;
    try {
      configurl = vizFrontEnd.getConfigURL();
      if (configurl != null) {
        tmConfig = new VizTopicMapConfigurationManager(new URL(configurl));
      }
    } catch (MalformedURLException mue) {
      ErrorDialog.showError(vpanel, Messages.getString("Viz.ErrorLoadingConfig"), mue);
    } catch (IOException ioe) {
      ErrorDialog.showError(vpanel, Messages.getString("Viz.ErrorLoadingConfig"), ioe);
    } finally {
      // if an error occurred, or there is no config parameter, then create a default one
      if(tmConfig == null) {
        tmConfig = new VizTopicMapConfigurationManager();
      }
    }

    appContext.setTmConfig(tmConfig);
    if (vizFrontEnd.mapPreLoaded()) {
      view = new TopicMapView(this, vizFrontEnd.getTopicMap(), aTgPanel, tmConfig);
      appContext.setView(view);
    }
    highlightNode = new HighlightNode(this);
    keyMan = new KeyInputManager(this);
    if (vizFrontEnd.mapPreLoaded()) {
      focusStartTopicInternal();
    }
  }

  
  public VizPanel getVizPanel() {
    return vpanel;
  }

  public TopicMapView getView() {
    return view;
  }

  private File getGeneralConfigurationFile() {
    String dir = System.getProperty(
        "net.ontopia.topicmaps.viz.home", System.getProperty("user.home", "."));
    return new File(dir + File.separator + "vizconf.xtm");
  }

  public TopicIF getDefaultScopingTopic(TopicMapIF topicmap) {
    TopicIF scope = appContext.getDefaultScopingTopic(topicmap);
    if (scope == null) {
      scope = appContext.getTopicForLocator(VizUtils.makeLocator(SHORT_NAME),
          topicmap);
    }

    return scope;
  }

  public boolean hasTopicMap() {
    return view != null;
  }

  public boolean isApplet() {
    return appContext.isApplet();
  }

  // --- model introspection -----------------------------------------------

  public Collection getAssociationTypes() {
    return view.getAssociationTypes();
  }

  public Collection getAllTopicTypesWithNull() {
    return view.getAllTopicTypesWithNull();
  }

  public Collection getAllTopicTypes() {
    return view.getAllTopicTypes();
  }

  // --- view introspection ------------------------------------------------

  public boolean isAssocTypeVisible(TopicIF type) {
    return tmConfig.isAssociationTypeVisible(type);
  }

  public boolean isTopicTypeVisible(TopicIF type) {
    return tmConfig.isTopicTypeVisible(type);
  }

  public Color getTopicTypeColor(TopicIF type) {
    return tmConfig.getTopicTypeColor(type);
  }
  
  public TopicMapIF getTopicMap() {
    return view.getTopicMap();
  }

  public int getTopicTypeShape(TopicIF type) {
    return tmConfig.getTopicTypeShape(type);
  }

  public Color getAssociationTypeColor(TopicIF type) {
    return tmConfig.getAssociationTypeColor(type);
  }

  public TopicIF getStartTopic() {
    return getStartTopic(view.getTopicMap());
  }

  public TopicIF getStartTopic(TopicMapIF aTopicmap) {
    return appContext.getStartTopic(aTopicmap);
  }

  // --- view modifications ------------------------------------------------

  public void setLocality(int locality) {
    headedDebug("setLocality ------------- before ------- locality: "
        + locality, null);
    
    undoManager.startOperation(new DoSetLocality(this, locality));
    
    // IDM 13-09-04
    // At startup, when there is no TopicMap loaded, the variable
    // "view" is null causing a NullPointerException error in the
    // original the code below. I have inserted a null check just
    // to get around this. However maybe this should be resolved in
    // another way, such as disabling the spinner when there is no
    // TopicMap loaded ?

    if (view == null) {
      return;
    }

    view.setLocality(locality);
    updateDisplayLazily();
    
    undoManager.completeOperation();

    headedDebug("setLocality ------------- after ------- locality: "
        + locality, null);
  }

  /**
   * Required because the colour chooser will trigger the setTypeColor method
   * when the selected is colour is changed by Vizigator (no user input).
   * In this case the colour view and configuration should not be changed.
   */
  public boolean getIgnoreStateChangedEvent() {
    return ignoreStateChangedEvent;
  }
  
  public void setIgnoreStateChangedEvent(boolean ignoreStateChangedEvent) {
    this.ignoreStateChangedEvent = ignoreStateChangedEvent;
  }

  public void updateViewTypeColor(TopicIF type, Color color) {
    view.setTypeColor(type, color);
    repaint();
  }
  
  public void updateViewType(TopicIF type) {
    view.updateType(type);
  }
  
  public void setColorToDefault(TopicIF type, boolean topicType) {
    tmConfig.setColorToDefault(type, topicType, view);
  }
  
  public void setTypeColor(TopicIF type, Color c) {
    if (!getIgnoreStateChangedEvent()) {
      tmConfig.setTypeColor(type, c, view);
      repaint();
    }
  }

  /**
   * Set the given node to be the focus node. This method should only be called
   * from the user interface, and should not be used to implement other
   * operations.
   * To implement other operations, use focusNodeInternal(TMAbstractNode).
   * @param node The new focus node.
   */
  public void focusNode(TMAbstractNode node) {
    headedDebug("focusNode - before", node);
    view.stat.init();
    view.stat1.init();

    undoManager.startOperation(new DoFocusNode(this, 
        (NodeRecoveryObjectIF)node.getRecreator()));
    
    focusNodeInternal(node);
    
    updateDisplay();

    view.stat.report();
    view.stat1.report();
    
    undoManager.completeOperation();

    headedDebug("focusNode - after", node);
  }

  /**
   * Set the given node to be the focus node. This method should only be used
   * for the internal working of other operations, like 
   * focusNode(TMAbstractNode), and should not be called directly from the user
   * interface.
   * @param node The new focus node.
   */
  public void focusNodeInternal(TMAbstractNode node) {
    TMAbstractNode focusNode = getFocusNode();
    
    if (focusNode != null) {
      SetFocusNode setFocusNode = new SetFocusNode((NodeRecoveryObjectIF)
          focusNode.getRecreator());
      undoManager.addRecovery(setFocusNode);
    }
    
    // It is possible to get here before the view has been completely
    // generated and assigned to its instance variable.
    if (view != null) {
      vpanel.clearSearchResults();
      appContext.focusNode(node);
    }
    
    updateDisplay();
  }

  /**
   * Sets all nodes to fixed (sticky) or not fixed.
   * @param fixed true(/false) if all nodes should get a (un)fixed position.
   */
  public void setAllNodesFixed(boolean fixed) {
    view.setAllNodesFixed(fixed);
    
    // Note: resetDamper is sufficient. No call to updateDisplay is needed since
    // no Nodes or edges were added, filtered or removed.
    view.getTGPanel().resetDamper();
  }

  public TMAbstractNode getFocusNode() {
    return view.getFocusNode();
  }

  public void setStartTopic(TopicIF topic) {
    if (view == null) {
      return;
    }

    appContext.setStartTopic(topic);
  }

  public void clearStartTopic() {
    if (view != null) {
      tmConfig.clearStartTopic();
    }
  }

  // --- environment actions -----------------------------------------------

  /**
   * Opens the supplied url string in a browser window. Which window is used is
   * defined by the 'propTarget' applet parameter
   * 
   * @param url String representing the target url
   */
  public void openPropertiesURL(String url) {
    appContext.openPropertiesURL(url);
  }

  public void goToTopic(TopicIF topic) {
    appContext.goToTopic(topic);
  }

  public void saveTopicMapConfiguration(File file) throws IOException {
    tmConfig.save(file);
  }

  // --- old code -----------------------------------------------------

  public void loadConfig(File f) throws IOException {
    try {
      vpanel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
      tmConfig = new VizTopicMapConfigurationManager(f);
      view.setConfigManager(tmConfig);
    } finally {
      vpanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
  }

  public TopicMapIF loadTopicMap(File f) throws IOException {
    return loadTopicMap(f, null);
  }

  // this method exists because it allows us to build a correctly
  // configured view directly
  public TopicMapIF loadTopicMap(File tmfile, File cfgfile) throws IOException {
    TopicMapIF topicmap;
    try {
      vpanel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
      vpanel.clearSearchResults();
      TopicMapReaderIF reader = ImportExportUtils.getReader(tmfile);

      Map<String, Object> properties = new HashMap<String, Object>(3);
      properties.put("lenient", true);
      properties.put("generateNames", true);
      String mappingFile = generalConfig.getRDFMappingFile();
      if (mappingFile != null) {
        properties.put("mappingFile", new File(mappingFile));
      }
      reader.setAdditionalProperties(properties);

      topicmap = importTopicMap(reader, tmfile.getName());
      if (topicmap != null) {
        if (cfgfile == null) {
          tmConfig = new VizTopicMapConfigurationManager();
        } else {
          tmConfig = new VizTopicMapConfigurationManager(cfgfile);
        }

        // Remove all paintListeners
        hoverHelpManager.resetPainters();
        view = new TopicMapView(this, topicmap, vpanel.getTGPanel(), tmConfig);
        appContext.setView(view);
        setScopingTopic(getDefaultScopingTopic(topicmap));
        int locality = getDefaultLocality();
        VizDebugUtils.debug("loadTopicMap(tmfile, cfgfile) - " +
            "setting locality: " + locality);
        vpanel.setLocality(locality);
        VizDebugUtils.debug("loadTopicMap(tmfile, cfgfile) - " +
            "have set locality: " + locality);
        view.build();

        initializeMotionKillerEnabled();
      }
    } finally {
      vpanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    headedDebug("loadTopicMap - after (only)", null);

    return topicmap;
  }

  public TopicMapIF loadTopicMap(TopicMapIF topicMap) throws IOException {
    return loadTopicMap(topicMap, null);
  }

  // this method exists because it allows us to build a correctly
  // configured view directly
  public TopicMapIF loadTopicMap(TopicMapIF topicmap,
      File cfgfile) throws IOException {
    try {
      vpanel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
      vpanel.clearSearchResults();
      if (topicmap != null) {
        if (cfgfile == null) {
          tmConfig = new VizTopicMapConfigurationManager();
        } else {
          tmConfig = new VizTopicMapConfigurationManager(cfgfile);
        }

        // Remove all paintListeners
        hoverHelpManager.resetPainters();
        view = new TopicMapView(this, topicmap, vpanel.getTGPanel(), tmConfig);
        appContext.setView(view);
        setScopingTopic(getDefaultScopingTopic(topicmap));
        int locality = getDefaultLocality();
        VizDebugUtils.debug("loadTopicMap(tm, cfgfile) - setting  locality: " +
            locality);
        vpanel.setLocality(locality);
        view.build();

        initializeMotionKillerEnabled();
      }
    } finally {
      vpanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    return topicmap;
  }

  public int getDefaultLocality() {
    return appContext.getDefaultLocality();
  }

  public int getMaxLocality() {
    return appContext.getMaxLocality();
  }

  protected void setHighlightNode(TMAbstractNode node, Graphics g) {
    if (highlightNode != null) {
      highlightNode.setNode(node, g);
    }
  }

  private TopicMapIF importTopicMap(TopicMapReaderIF reader, String name) {
    final JOptionPane pane = new JOptionPane(new Object[] { Messages
        .getString("Viz.LoadingTopicMap") + name },
        JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null,
        new String[] {}, null);

    Frame frame = JOptionPane.getFrameForComponent(vpanel);
    final JDialog dialog = new JDialog(frame, Messages
        .getString("Viz.Information"), true);

    dialog.setContentPane(pane);
    dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    dialog.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent we) {
        JOptionPane
            .showMessageDialog(pane,
                Messages.getString("Viz.CannotCancelOperation"), 
                Messages.getString("Viz.Information"),
                JOptionPane.INFORMATION_MESSAGE);
      }
    });

    dialog.pack();
    dialog.setLocationRelativeTo(frame);

    TopicMapIF tm;
    final TopicMapReaderIF r = reader;

    final SwingWorker worker = new SwingWorker() {
      @Override
      public Object construct() {
        TopicMapIF result = null;
        try {
          result = r.read();
        } catch (IOException e) {
          dialog.setVisible(false);
          ErrorDialog.showError(vpanel, e.getMessage());
        }
        return result;
      }

      @Override
      public void finished() {
        dialog.setVisible(false);
      }
    };

    worker.start();
    dialog.setVisible(true);
    tm = (TopicMapIF) worker.getValue();
    return tm;
  }

  public VizTopicMapConfigurationManager getConfigurationManager() {
    return tmConfig;
  }

  public void setTopicTypeVisibility(TopicIF type, int visibility) {
    headedDebug("setTopicTypeVisible - before, type: " + type + ", visible: "
        + visibility, type);

    undoManager.startOperation(new DoSetTTVisibility(type, visibility));
    DoSetTTVisibilityState recovery =
      new DoSetTTVisibilityState(type, tmConfig.getTypeVisibility(type));
    undoManager.addRecovery(recovery);
    
    tmConfig.setTypeVisibility(type, visibility, view);    
    updateDisplay();
    repaint();
    
    undoManager.completeOperation();
    
    headedDebug("setTopicTypeVisible - after", type);
  }


  public void setAssociationTypeVisibility(TopicIF type, int visibility) {
    headedDebug("setAssociationTypeVisible - before, visible: "
        + visibility, type);

    undoManager.startOperation(new DoSetATVisibility(type, visibility));
    DoSetATVisibilityState recovery =
      new DoSetATVisibilityState(type, tmConfig.getTypeVisibility(type));
    undoManager.addRecovery(recovery);

    tmConfig.setAssociationTypeVisible(type, visibility, view);    
    updateDisplay();
    repaint();
    
    undoManager.completeOperation();
    
    headedDebug("setAssociationTypeVisible - after, visible: "
        + visibility, type);
  }

  // FIXME: SHOULD BECOME REDUNDANT. THEN REMOVE.
  public void setTopicTypeVisible(TopicIF type, boolean visible) {
    headedDebug("setTopicTypeVisible - before, type: " + type + ", visible: "
        + visible, type);

    tmConfig.setTypeVisible(type, visible, view);    
    updateDisplay();
    repaint();
    
    headedDebug("setTopicTypeVisible - after", type);
  }

  // FIXME: SHOULD BECOME REDUNDANT. THEN REMOVE.
  public void setAssociationTypeVisible(TopicIF type, boolean visible) {
    headedDebug("setAssociationTypeVisible - before, visible: "
        + visible, type);

    tmConfig.setAssociationTypeVisible(type, visible, view);    
    if (visible) {
      view.loadNodesInLocality(view.getFocusNode(), true, false);
    }
    repaint();
    
    updateDisplay();

    headedDebug("setAssociationTypeVisible - after, visible: "
        + visible, type);
  }

  public void goToMapView() {
    headedDebug("goToMapView - before", null);
    view.clearFocusNode();
    
    // This will unmark all nodes that were scheduled to have their hidden
    // association recounted. That's o.k. since all these numbers reset to 0
    // in 'view.clearFocusNode()'.
    view.updateDisplayNoWork();
    headedDebug("goToMapView - after", null);
  }

  // --- Internals

  private void repaint() {
    vpanel.repaint();
  }

  public void setTopicTypeShape(TopicIF type, int i) {
    tmConfig.setTopicTypeShape(type, i, view);
    repaint();
  }

  public void setAssociationTypeShape(TopicIF type, int i) {
    tmConfig.setAssociationTypeShape(type, i, view);
    repaint();
  }

  public void setFontToDefault(TopicIF type, boolean topicType) {
    tmConfig.setFontToDefault(type, topicType, view);
  }
  
  public void setTypeFont(TopicIF type, Font font) {
    tmConfig.setTypeFont(type, font, view);
    repaint();
  }

  public void setAssociationTypeLineWeight(TopicIF type, int i) {
    tmConfig.setAssociationTypeLineWeight(type, i, view);
    repaint();
  }

  public void setTopicTypeShapePadding(TopicIF type, int i) {
    tmConfig.setTopicTypeShapePadding(type, i, view);
    repaint();
  }

  public int getTypeVisibility(TopicIF selectedType) {
    return tmConfig.getTypeVisibility(selectedType);
  }

  public int getAssoicationTypeShape(TopicIF selectedType) {
    return tmConfig.getAssociationTypeShape(selectedType);
  }

  public int getAssoicationTypeLineWeight(TopicIF selectedType) {
    return tmConfig.getAssociationTypeLineWeight(selectedType);
  }

  public int getTopicTypeShapePadding(TopicIF selectedType) {
    return tmConfig.getTopicTypeShapePadding(selectedType);
  }

  public String getTypeIconFilename(TopicIF selectedType) {
    return tmConfig.getTypeIconFilename(selectedType);
  }

  public Icon getTypeIcon(TopicIF selectedType) {
    return tmConfig.getTypeIcon(selectedType);
  }

  public Font getTypeFont(TopicIF selectedType) {
    return tmConfig.getTypeFont(selectedType);
  }

  public void setTypeIconFilename(TopicIF type, String string) {
    tmConfig.setTypeIconFilename(type, string, view);        
    repaint();
  }

  public void openProperties(TMTopicNode node) {
    if (propertiesFrame == null) {
      propertiesFrame = new PropertiesFrame(this);
      propertiesFrame.setVisible(true);
    }

    propertiesFrame.setTarget(node.getTopic());
    propertiesFrame.setVisible(true);
    propertiesFrame.toFront();
  }

  public void shouldDisplayRoleHoverHelp(boolean newValue) {
    tmConfig.shouldDisplayRoleHoverHelp(newValue);
    view.shouldDisplayRoleHoverHelp(newValue);
  }
  
  public void initializeMotionKillerEnabled() {
    setMotionKillerEnabled(getConfigurationManager().isMotionKillerEnabled());
    vpanel.enableDisableMotionKillerMenuItem(getConfigurationManager()
        .isMotionKillerEnabled());
  }

  public void setMotionKillerEnabled(boolean newValue) {
    tmConfig.setMotionKillerEnabled(newValue);
    view.setMotionKillerEnabled(newValue);
    vpanel.updateEnableMotionKillerMenuItem();
  }

  public void shouldDisplayScopedAssociationNames(boolean newValue) {
    tmConfig.shouldDisplayScopedAssociationNames(newValue);
    view.shouldDisplayScopedAssociationNames(newValue);
  }

  public void setPanelBackgroundColour(Color aColor) {
    tmConfig.setPanelBackgroundColour(aColor);
    view.setPanelBackgroundColour(aColor);
  }

  public void setGeneralSingleClick(int anAction) {
    tmConfig.setGeneralSingleClick(anAction);
  }

  public void setGeneralLocalityAlgorithm(int anAction) {
    tmConfig.setGeneralLocalityAlgorithm(anAction);
    focusNode(getFocusNode());
  }

  public void setMotionKillerDelay(int seconds) {
    tmConfig.setMotionKillerDelay(seconds);
    view.motionKiller.setMaxCycle(seconds);
  }

  public void setGeneralDoubleClick(int anAction) {
    tmConfig.setGeneralDoubleClick(anAction);
  }

  public void setMaxTopicNameLength(int length) {
    tmConfig.setMaxTopicNameLength(length);
    view.setMaxTopicNameLength(length);
  }

  public void setTypeIncluded(TopicIF type) {
    tmConfig.setTypeIncluded(type, view);
    repaint();
  }

  public void setTypeExcluded(TopicIF type) {
    headedDebug("setTypeExcluded - before", type);
    tmConfig.setTypeExcluded(type, view);
    repaint();
    headedDebug("setTypeExcluded - after", type);
  }

  public List performSearch(String searchString) {
    if (view != null) {
      return view.performSearch(searchString);
    }
    return Collections.EMPTY_LIST;
  }

  public void loadNode(TMAbstractNode node) {
    view.stat.startOp();
    view.createAssociations(node);
    view.stat.stopOp();
  }

  public void outputDebugInfo(String operation) {
    if (view == null) {
      return;
    }
    view.outputDebugInfo(operation);
  }

  public void expandNode(TMAbstractNode node) {
    headedDebug("expandNode - before", node);
    
    undoManager.startOperation(new DoExpandNode(this, 
        (NodeRecoveryObjectIF)node.getRecreator()));

    // HACK: make sure that we load expanded nodes and related. This code should
    // possibly go somewhere else.
    if (node instanceof TMTopicNode) {
      TopicIF topic = ((TMTopicNode)node).getTopic();
      if (topic instanceof net.ontopia.topicmaps.impl.remote.RemoteTopic) {
        RemoteTopicMapStore store = (RemoteTopicMapStore)topic.getTopicMap().getStore();
        RemoteTopicIndex tindex = store.getTopicIndex();
        tindex.loadRelatedTopics(topic.getSubjectIdentifiers(),
                                 topic.getItemIdentifiers(),
                                 topic.getSubjectLocators(),
                                 false); // only 1 step out, not 2
      }
    }
    loadNode(node);

    view.getTGPanel().expandNode(node); 
    updateDisplay();
    
    undoManager.completeOperation();

    headedDebug("expandNode - after", node);
  }

  public Function<TopicIF, String> getStringifier() {
    return (stringifier == null ? TopicStringifiers.getDefaultStringifier() 
        : stringifier);
  }

  public void saveGeneralConfiguration() throws IOException {
    generalConfig.save(getGeneralConfigurationFile());
  }

  public void updateRecentFiles(File f) {
    generalConfig.updateRecentFiles(f);
  }

  public List getRecentFiles() {
    return generalConfig.getRecentFiles();
  }

  public String getRdfMappingFile() {
    return generalConfig.getRDFMappingFile();
  }

  public void setRdfMappingFile(File file) throws IOException {
    generalConfig.setRdfMappingFile(file);
    saveGeneralConfiguration();
  }

  public String getCurrentTMDir() {
    return generalConfig.getCurrentTMDir();
  }

  public void setCurrentTMDir(String currentTMDir) throws IOException {
    generalConfig.setCurrentTMDir(currentTMDir);
    saveGeneralConfiguration();
  }

  public String getCurrentRDBMSDir() {
    return generalConfig.getCurrentRDBMSDir();
  }

  public void setCurrentRDBMSDir(String dir) throws IOException {
    generalConfig.setCurrentRDBMSDir(dir);
    saveGeneralConfiguration();
  }

  public String getCurrentConfigDir() {
    return generalConfig.getCurrentConfigDir();
  }

  public void setCurrentConfigDir(String dir) throws IOException {
    generalConfig.setCurrentConfigDir(dir);
    saveGeneralConfiguration();
  }

  public void loadTopic(TopicIF aTopic) {
    appContext.loadTopic(aTopic);
  }

  public void collapseNode(TMAbstractNode node) {
    TGPanel panel = view.getTGPanel();

    undoManager.startOperation(new DoCollapseNode(this, 
        (NodeRecoveryObjectIF)node.getRecreator()));

    TMAbstractNode target = (TMAbstractNode) panel.getSelect();
    
    if (node.equals(target)) {
      view.hideNode(node);
    } else if (target == null) {
      // If we are in Map view, use default behaviour
      panel.collapseNode(node);
    } else {
      // If there is a focus node, collapse all links that are not
      // connected to the focus node.
      Vector hidden = getOrphanedNodes(node, target);
      ((Locality) panel.getGES()).removeNodes(hidden);
      updateDisplay();
    }

    undoManager.completeOperation();
  }

  private Vector getOrphanedNodes(TMAbstractNode node, TMAbstractNode target) {
    Vector hidden = new Vector();
    for (Iterator edges = node.getVisibleEdges(); edges.hasNext();) {
      TMAbstractEdge edge = (TMAbstractEdge) edges.next();
      TMAbstractNode neighbour = (TMAbstractNode) edge.getOtherEndpt(node);
      HashSet visited = new HashSet();
      visited.add(node);
      if (!neighbour.hasPathTo(target, visited)) {
        addToHidden(neighbour, node, hidden);
      }
    }
    return hidden;
  }
  
  protected void addToHidden(TMAbstractNode target, Node source, Vector hidden) {
    if (hidden.contains(target)) {
      return;
    }
    hidden.add(target);

    for (Iterator edges = target.getVisibleEdges(); edges.hasNext();) {
      TMAbstractEdge edge = (TMAbstractEdge) edges.next();
      TMAbstractNode neighbour = (TMAbstractNode) edge.getOtherEndpt(target);
      if (!neighbour.equals(source)) {
        addToHidden(neighbour, source, hidden);
      }
    }
  }

  public void focusStartTopic() {
    if (view == null) {
      return;
    }
    focusNode(view.getStartNode());
  }

  public void focusStartTopicInternal() {
    if (view == null) {
      return;
    }
    focusNodeInternal(view.getStartNode());
  }

  protected VizHoverHelpManager getHoverHelpManager() {
    return hoverHelpManager;
  }

  public void setScopingTopic(TopicIF aScope) {
    stringifier = VizUtils.stringifierFor(aScope);
    tmConfig.setScopingTopic(aScope);
    view.setScopingTopic(aScope);
    appContext.setScopingTopic(aScope);
  }

  /** 
   * Configure the given AssociationScopeFilterMenu
   * @param menu The menu to configure.
   * @param topicmap The topicmap filtered by the filter of the menu. 
   * @param parentListener Listens for actions on the items in the menu.
   */
  public void configure(AssociationScopeFilterMenu menu, TopicMapIF topicmap,
      ActionListener parentListener) {
    menu.configure(topicmap, parentListener, this);
  }

  public void setInAssociationScopeFilter(TopicIF scope, boolean useInFilter) {
    headedDebug("setInAssociationScopeFilter - before - useInFilter: " +
        useInFilter, scope);

    undoManager.startOperation(new DoSetInASFilter(scope, useInFilter));
    DoSetInASFilterState recovery =
      new DoSetInASFilterState(scope,
                               tmConfig.isInAssociationScopeFilter(scope));
    undoManager.addRecovery(recovery);

    tmConfig.setInAssociationScopeFilter(scope, useInFilter);
    if (useInFilter) {
      view.addAssociationScopeFilterTopic(scope);
    } else {
      view.removeAssociationScopeFilterTopic(scope);
    }
    updateDisplay();
    repaint();
    
    undoManager.completeOperation();

    headedDebug("setInAssociationScopeFilter - after - useInFilter: " + 
        useInFilter, scope);
  }
  
  /**
   *  Output debug info with a header for the method, provided debug is enabled.
   */
  private void headedDebug(String header, Object o) {
    if (view == null) {
      return;
    }
    view.headedDebug(header, o);
    view.debug.integrityCheck();
  }
  
  public void setAssociationScopeFilterStrictness(int strictness) {
    headedDebug("setAssociationScopeFilterStrictness - before - strictness: " + 
        strictness, null);

    undoManager.startOperation(new DoSetASStrictness(strictness));
    DoSetASStrictnessState recovery =
      new DoSetASStrictnessState(tmConfig
          .getAssociationScopeFilterStrictness());
    undoManager.addRecovery(recovery);

    tmConfig.setAssociationScopeFilterStrictness(strictness);
    view.setAssociationScopeFilterStrictness(strictness);
    updateDisplay();
    
    undoManager.completeOperation();

    headedDebug("setAssociationScopeFilterStrictness - after - strictness: " + 
        strictness, null);
  }

  public int getAssociationScopeFilterStrictness() {
    return tmConfig.getAssociationScopeFilterStrictness();
  }

  public boolean isInAssociationScopeFilter(TopicIF scope) {
    return tmConfig.isInAssociationScopeFilter(scope);
  }
  
  public TypesConfigFrame getTopicFrame() {
    return appContext.getTopicFrame();
  }
  
  public TypesConfigFrame getAssocFrame() {
    return appContext.getAssocFrame();
  }
  
  public void hideEdge(TMAbstractEdge edge) {
    headedDebug("hideEdge - before", edge);

    undoManager.startOperation(new DoHideEdge(this, 
        (EdgeRecoveryObjectIF)edge.getRecreator()));
    
    view.deleteSingleEdge(edge);
    updateDisplayLazily();

    undoManager.completeOperation();
    
    headedDebug("hideEdge - after", edge);
  }
  
  public void undo() {
    headedDebug("undo - before", null);

    undoManager.undo();
    
    // FIXME: Consider marking nodes, so display can be updated lazily.
    updateDisplay();
    
    headedDebug("undo - after", null);
  }
  
  public boolean canUndo() {
    return undoManager.canUndo();
  }
  
  public boolean canRedo() {
    return undoManager.canRedo();
  }
  
  public void redo() {
    headedDebug("redo - before", null);

    undoManager.redo();
    
    // FIXME: Consider marking nodes, so display can be updated lazily.
    updateDisplay();
    
    headedDebug("redo - after", null);
  }
  
  private void updateDisplay() {
    view.updateDisplay();
  }
    
  private void updateDisplayLazily() {
    view.updateDisplayLazily();
  }
    
  /**
   * Delete a node, all incident edges and all nodes and edges that no longer
   * have a path to the focus node as a consequence of this.
   * @param node The base node to delete.
   */
  public void hideNode(TMAbstractNode node) {
    headedDebug("hideNode - before", node);
    
    undoManager.startOperation(new DoHideNode(this, 
        (NodeRecoveryObjectIF)node.getRecreator()));

    view.hideNode(node);
    
    // Lazy update is o.k. in both map view and topic view.
    // For details, see view.hideNode(node).
    updateDisplayLazily();

    undoManager.completeOperation();

    headedDebug("hideNode - after", node);
  }
  

  /**
   * Stops the motion of all nodes completely.
   */
  public void stopMovingNodes() {
    TGPanel panel = view.getTGPanel();
    panel.stopMotion();
    panel.stopMotion();
  }
  
  /**
   * Enables/disables the motion killer.
   * Note: VizPanel uses the value of enabled to build menus, so this method
   *     should only be changed (indirectly) from there.
   */
  public void enableMotionKiller(boolean enable) {
    view.motionKiller.setEnabled(enable);
  }

  public boolean isMotionKillerEnabled() {
    return view.motionKiller.getEnabled();
  }

  /**
   * Enables/disables animation.
   * Note: VizPanel uses the value of enabled to build menus, so this method
   *     should only be changed (indirectly) from there.
   */
  public void enableAnimation(boolean enable) {
    view.vizigatorUser.setEnabled(enable);
  }

  public boolean isAnimationEnabled() {
    return view.vizigatorUser.getEnabled();
  }

  public ParsedMenuFile getEnabledItemIds() {
    ParsedMenuFile enabledItemIds = appContext.getEnabledItemIds();
    VizDebugUtils.debug("VizController.getEnabledItemIds() - enabledItemIds" + 
        enabledItemIds);
    return enabledItemIds;
  }

  public void loadAssociationTypes() {
    if (!appContext.isApplet()) {
      return; // no loading to do on desktop
    }

    if (assocTypesLoaded) {
      return;
    }

    try {
      TopicMapIF topicmap = view.getTopicMap();
      RemoteTopicMapStore store = (RemoteTopicMapStore) topicmap.getStore();
      RemoteTopicIndex tindex = store.getTopicIndex();
      tindex.loadAssociationTypes(topicmap);
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }

    assocTypesLoaded = true;
  }

  public void loadTopicTypes() {
    if (!appContext.isApplet()) {
      return; // no loading to do on desktop
    }
    
    if (topicTypesLoaded) {
      return;
    }

    try {
      TopicMapIF topicmap = view.getTopicMap();
      RemoteTopicMapStore store = (RemoteTopicMapStore) topicmap.getStore();
      RemoteTopicIndex tindex = store.getTopicIndex();
      tindex.loadTopicTypes(topicmap);
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }
    
    topicTypesLoaded = true;
  }

  // ----------- Nested classes and interfaces -------------

  /**
   * INTERNAL: Hover Help Manager
   */
  protected class VizHoverHelpManager implements TGPaintListener {

    protected ArrayList painters;

    protected VizHoverHelpManager(TGPanel panel) {
      panel.addPaintListener(this);
      resetPainters();
    }

    protected void resetPainters() {
      painters = new ArrayList();

    }

    @Override
    public void paintFirst(Graphics g) {
      // Do it this way so that we do not get concurrent modification
      // errors when loading large topic maps
      for (int i = 0; i < painters.size(); i++) {
        ((TGPaintListener) painters.get(i)).paintFirst(g);
      }
    }

    public void addPaintListener(TGPaintListener aListener) {
      painters.add(aListener);
    }

    @Override
    public void paintAfterEdges(Graphics g) {
      // Do it this way so that we do not get concurrent modification
      // errors when loading large topic maps
      for (int i = 0; i < painters.size(); i++) {
        ((TGPaintListener) painters.get(i)).paintAfterEdges(g);
      }
    }

    @Override
    public void paintLast(Graphics g) {
      // Do it this way so that we do not get concurrent modification
      // errors when loading large topic maps
      for (int i = 0; i < painters.size(); i++) {
        ((TGPaintListener) painters.get(i)).paintLast(g);
      }
    }

    public void removePaintListener(TGPaintListener aListener) {
      painters.remove(aListener);
    }
  }
}
