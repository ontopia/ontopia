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
import java.awt.Component;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import net.ontopia.Ontopia;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.index.ScopeIndexIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.nav.utils.comparators.TopicComparator;
import net.ontopia.utils.CmdlineOptions;
import net.ontopia.utils.CmdlineUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.SimpleFileFilter;
import net.ontopia.utils.URIUtils;

import com.touchgraph.graphlayout.Node;
import java.util.function.Function;

/**
 * INTERNAL: The top-level class for the VizDesktop. Can be run from the
 * command-line to produce the desktop UI.
 */
public class VizDesktop implements VizFrontEndIF {
  private static final String ONTOPIA_VIZDESKTOP_TITLE = "Ontopia VizDesktop";

  private VizPanel vPanel;
  private VizController controller;
  private JMenu recentTopicMapFilesMenu;
  private JMenu visibleAssocTypesMenu;
  private JMenu visibleTopicTypesMenu;
  private JMenuItem focusStartTopicItem;

  private TypesConfigFrame topicFrame;
  private TypesConfigFrame assocFrame;

  private TopicMapIF currentTopicMap;
  private HashMap topicTypeButtonMap;
  private HashMap associationTypeButtonMap;
  private JMenu styleMenu;
  private JMenu optionsMenu;
  private JMenu viewMenu;
  private GeneralConfigFrame generalFrame;
  private TypesPrecedenceFrame precedenceFrame;
  private JFrame frame;
  private JMenuItem mapViewMenu;
  private JMenuItem clearStartMenu;
  private JMenu scopingTopicsMenu;
  private Function<TopicIF, String> stringifier;
  private TopicIF currentScope;
  private OpenRDBMSDialogBox openBox;
  private static final boolean enableRDBMSImport = true;

  /**
   * Simple main to allow stand-alone startup. Optionally can provide an initial
   * topicmap to load.
   */
  public static void main(String[] argv) {
    // set up logging
    CmdlineUtils.initializeLogging();
    CmdlineUtils.setLoggingPriority("ERROR");
    
    // Initialize command line option parser and listeners
    CmdlineOptions options = new CmdlineOptions("VizDesktop", argv);
    OptionsListener ohandler = new OptionsListener();
    options.addLong(ohandler, "lang", 'l', true);

    // Register logging options
    CmdlineUtils.registerLoggingOptions(options);
      
    // Parse command line options
    try {
      options.parse();
    } catch (CmdlineOptions.OptionsException e) {
      System.err.println("Error: " + e.getMessage());
      System.exit(1);      
    }

    // Set user-interface language
    Messages.setLanguage(ohandler.lang);

    // Get command line arguments
    final String[] args = options.getArguments();    
    if (args.length > 1) {
      System.err.println(Messages.getString("Viz.InvalidParamNum"));
      usage();
      System.exit(1);
    }

    // open main window
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        new VizDesktop(args);
      }
    });
  }

  protected static void usage() {
    System.out.println(Ontopia.getInfo());
    System.out.println("java net.ontopia.topicmaps.viz.VizDesktop [options] " +
        "<file>");
    System.out.println("");
    System.out.println("  " + Messages.getString("Viz.cmdlineUsageTitle"));
    System.out.println("");
    System.out.println("  Options:");
    CmdlineUtils.printLoggingOptionsUsage(System.out);
    System.out.println("");
    System.out.println("  <" + Messages.getString("Viz.cmdlineFile") + ">: " +
                       Messages.getString("Viz.cmdLineFileDescription"));
    
  }

  private static class OptionsListener implements CmdlineOptions.ListenerIF {
    private String lang = "en";
    @Override
    public void processOption(char option, String value) {
      if (option == 'l') lang = value;
    }
  }

  private void disableMenuItems() {
    this.setEnableMenuItems(false);
  }
  
  public TypesConfigFrame getTopicFrame() {
    return topicFrame;
  }

  public TypesConfigFrame getAssocFrame() {
    return assocFrame;
  }

  private void setEnableMenuItems(boolean b) {
    visibleAssocTypesMenu.setEnabled(b);
    visibleTopicTypesMenu.setEnabled(b);
    viewMenu.setEnabled(b);
    styleMenu.setEnabled(b);
    optionsMenu.setEnabled(b);
    scopingTopicsMenu.setEnabled(b);
    AssociationScopeFilterMenu associationScopeFilterMenu = vPanel
        .getAssociationScopeFilterMenu();
    associationScopeFilterMenu.setEnabled(b);
  }

  private void enableMenuItems() {
    setEnableMenuItems(true);
  }

  public VizDesktop(String args[]) {
    frame = new JFrame(ONTOPIA_VIZDESKTOP_TITLE);

    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        try {
          controller.saveGeneralConfiguration();
        } catch (IOException ex) {
          ErrorDialog.showError(vPanel, ex);
        }
        System.exit(0);
      }
    });

    try {
      getContext();
      vPanel = new VizPanel(this);
      controller = vPanel.getController();
    } catch (Exception e) {
      ErrorDialog.showError(vPanel, e);
      return;
    }

    frame.setJMenuBar(getMenuBar());
    frame.getContentPane().add("Center", vPanel);

    frame.pack();
    frame.setSize(800, 800);
    frame.setVisible(true);
    this.disableMenuItems();

    if (args.length == 1) {
      // load topic map from file
      File f = new File(args[0]);
      if (!f.exists()) {
        ErrorDialog.showError(vPanel, Messages.getString(
            "Viz.FileDoesNotExist", args));
      } else {
        setCurrentTopicMapDirectory(f.getParent());
        try {
          loadTopicMap(f);
        } catch (IOException e) {
          ErrorDialog.showError(vPanel,
              Messages.getString("Viz.TMLoadError"), e);
        }
      }
    }
  }

  protected void resetMapViewMenu() {
    mapViewMenu.setEnabled(controller.getFocusNode() != null);
  }

  /**
   * Display recent file menu items.
   */
  private void displayRecentFileMenuItems(JMenu parent) {
    Iterator fileIterator = controller.getRecentFiles().iterator();
    boolean first = true;

    while (fileIterator.hasNext()) {
      File file = (File) fileIterator.next();

      JMenuItem mItem = new JMenuItem(file.getAbsolutePath());
      mItem.addActionListener(new FileOpenMenuListener());
      if (first)
        mItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
      parent.add(mItem);
      first = false;
    }
  }

  /**
   * Returns a menu for controlling the application. Uses the isRemote flag for
   * deciding which menu items are displayed.
   */
  private JMenuBar getMenuBar() {
    // FIXME: This whole menu sucks royally! We need to clean this
    // stuff up.  It's 200 lines now, but should be about 20-50.
    
    JMenuBar mBar;
    JMenu men;
    JMenuItem mItem;

    mBar = new JMenuBar();

    men = new JMenu(Messages.getString("Viz.File"));
    mBar.add(men);

    mItem = new JMenuItem(Messages.getString("Viz.MenuLoadTopicMap"));
    mItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent anE) {
        menuLoadTopicMap();
      }
    });
    mItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
        KeyInputManager.KEY_MASK));
    men.add(mItem);

    if (enableRDBMSImport)
      addRDBMSImportMenuItem(men);
    
    men.addSeparator();

    mItem = new JMenuItem(Messages.getString("Viz.MenuSaveConfiguration"));
    mItem.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent anE) {
        menuSaveConfiguration();
      }
    });
    mItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
        KeyInputManager.KEY_MASK));
    men.add(mItem);

    men.addSeparator();

    mItem = new JMenuItem(Messages.getString("Viz.SetRDFMappingFile"));
    mItem.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent anE) {
        menuSetRdfMappingFile();
      }
    });
    mItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M,
        KeyInputManager.KEY_MASK));
    men.add(mItem);

    men.addSeparator();

    recentTopicMapFilesMenu = new JMenu(Messages
        .getString("Viz.MenuRecentFiles"));
    men.add(recentTopicMapFilesMenu);
    displayRecentFileMenuItems(recentTopicMapFilesMenu);

    men.addSeparator();

    mItem = new JMenuItem(Messages.getString("Viz.MenuExit"));
    mItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
        KeyInputManager.KEY_MASK));
    mItem.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent anE) {

        try {
          controller.saveGeneralConfiguration();
        } catch (IOException e) {
          ErrorDialog.showError(vPanel, e);
        }
        System.exit(0);
      }
    });
    men.add(mItem);

    viewMenu = new JMenu(Messages.getString("Viz.MenuView"));
    mBar.add(viewMenu);

    mapViewMenu = new JMenuItem(Messages.getString("Viz.MenuMapView"));
    mapViewMenu.setEnabled(false);
    mapViewMenu.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent anE) {

        controller.goToMapView();
        resetMapViewMenu();
        resetStartTopicMenu();
      }
    });
    viewMenu.add(mapViewMenu);

    focusStartTopicItem = new JMenuItem(Messages
        .getString("Viz.MenuFocusStartNode"));
    focusStartTopicItem.setEnabled(false);
    focusStartTopicItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent anE) {
        controller.focusStartTopic();
      }
    });
    viewMenu.add(focusStartTopicItem);

    clearStartMenu = new JMenuItem(Messages
        .getString("Viz.MenuClearStartNode"));
    clearStartMenu.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent anE) {
        controller.clearStartTopic();
        resetClearStartMenu();
        resetStartTopicMenu();
      }
    });
    viewMenu.add(clearStartMenu);

    visibleTopicTypesMenu = new JMenu(Messages
        .getString("Viz.TopicTypesTitle"));

    mBar.add(visibleTopicTypesMenu);

    visibleAssocTypesMenu = new JMenu(Messages
        .getString("Viz.AssociationTypesTitle"));

     mBar.add(visibleAssocTypesMenu);

    // Options for topic type visual appearance
    styleMenu = new JMenu(Messages.getString("Viz.StyleTitle"));
    mBar.add(styleMenu);

    mItem = new JMenuItem(Messages.getString("Viz.TopicTypeConfiguration"));
    mItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent anE) {
        menuOpenTopicConfig();
      }
    });
    styleMenu.add(mItem);

    // Options for association type visual appearance
    mItem = new JMenuItem(Messages.getString("Viz.AssociationTypeConfiguration"));
    mItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent anE) {
        menuOpenAssociationConfig();
      }
    });
    styleMenu.add(mItem);

    // General configuration options
    optionsMenu = new JMenu(Messages.getString("Viz.MenuOptions"));
    mBar.add(optionsMenu);

    mItem = new JMenuItem(Messages.getString("Viz.MenuGeneral"));
    mItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent anE) {
        menuOpenGeneralConfig();
      }
    });
    optionsMenu.add(mItem);

    mItem = new JMenuItem(Messages.getString("Viz.MenuPrecedence"));
    mItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent anE) {
        menuOpenPrecedenceConfig();
      }
    });
    optionsMenu.add(mItem);

    scopingTopicsMenu = new JMenu(Messages.getString("Viz.ScopingMenuTitle"));
    mBar.add(scopingTopicsMenu);

    AssociationScopeFilterMenu associationScopeFilterMenu = vPanel
        .getAssociationScopeFilterMenu();
    mBar.add(associationScopeFilterMenu);

    JMenu helpMenu = new JMenu(Messages.getString("Viz.Help"));
    mBar.add(helpMenu);

    mItem = new JMenuItem(Messages.getString("Viz.About", "Ontopia Vizigator"));
    mItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent anE) {
        menuOpenAboutWindow();
      }
    });
    helpMenu.add(mItem);

    return mBar;
  }
  
  protected void addRDBMSImportMenuItem(JMenu containingMenu) {
    JMenuItem mItem = new JMenuItem(Messages
        .getString("Viz.MenuOpenRDBMSTopicMap"));
    mItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent anE) {
        menuOpenRDBMSTopicMap();
      }
    });
    mItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
        KeyInputManager.KEY_MASK));
    containingMenu.add(mItem);
  }

  protected void resetClearStartMenu() {
    clearStartMenu.setEnabled(controller.getStartTopic() != null);
  }

  private void menuSetRdfMappingFile() {
    setRdfMappingFile(getCurrentTopicMapDirectory());
  }

  private void setRdfMappingFile(String suggestion) {
    File defaultFile = null;

    if (controller.getRdfMappingFile() != null) {
      defaultFile = new File(controller.getRdfMappingFile());
    }

    if (defaultFile == null || !defaultFile.exists()) {
      // HACK - I don't like the following code
      String base = System.getProperty("user.dir");
      base = base.substring(0, Math.max(0, base.length() - 4));
      File preset = new File(base + "\\apache-tomcat\\webapps\\omnigator\\" +
          "WEB-INF\\topicmaps\\mapping.rdff");
      if (preset.exists()) {
        defaultFile = preset;
      } else {
        if (suggestion != null) {
          File tmp = new File(suggestion);
          if (tmp.exists()) {
            defaultFile = tmp;
          } else {
            defaultFile = null;
          }
        }
      }
    }

    JFileChooser fc = new JFileChooser(defaultFile);
    fc.setSelectedFile(defaultFile);
    SimpleFileFilter def = new SimpleFileFilter(Messages
        .getString("Viz.RDFMappingFilesDescription"), "rdf", "rdff");
    fc.addChoosableFileFilter(def);
    fc.setDialogTitle(Messages.getString("Viz.RfdMappingFileSelectionTitle"));
    int returnVal = fc.showDialog(vPanel, Messages.getString("Viz.FileSelect"));

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File f = fc.getSelectedFile();
      try {
        controller.setRdfMappingFile(f);
      } catch (IOException e) {
        ErrorDialog.showError(vPanel, e);
      }
    }
  }

  /**
   * Invoked after a topic map and any configuration has been loaded so that the
   * user can configure the display.
   */
  private void configureDynamicMenus() {
    configureFilterMenus();
    configureScopeMenu();
    vPanel.configureDynamicMenus(new DynamicMenuListener());
  }

  private void configureScopeMenu() {
    if (currentTopicMap == null)
      return;

    ScopeIndexIF scopeix = (ScopeIndexIF) currentTopicMap.getIndex(
            "net.ontopia.topicmaps.core.index.ScopeIndexIF");

    BasenameUserThemeFilter filter = new BasenameUserThemeFilter(currentTopicMap);

    HashMap map = new HashMap();
    ArrayList untyped = new ArrayList();
    for (Iterator iter = filter.filterThemes(scopeix.getTopicNameThemes())
        .iterator(); iter.hasNext();) {
      TopicIF theme = (TopicIF) iter.next();
      if (theme.getTypes().isEmpty())
        untyped.add(theme);
      else {
        for (Iterator iterator = theme.getTypes().iterator(); iterator
            .hasNext();) {
          TopicIF type = (TopicIF) iterator.next();
          List list = (List) map.get(type);
          if (list == null) {
            list = new ArrayList();
            map.put(type, list);
          }
          list.add(theme);
        }
      }
    }

    scopingTopicsMenu.removeAll();
    JMenuItem unconstrainedScopeMenu = new JMenuItem(Messages
        .getString("Viz.UnconstrainedScopeMenuTitle"));
    unconstrainedScopeMenu.addActionListener(new ScopeActionListener(null));
    scopingTopicsMenu.add(unconstrainedScopeMenu);
    unconstrainedScopeMenu.setEnabled(currentScope != null);

    if (!untyped.isEmpty()) {
      scopingTopicsMenu.addSeparator();
      Collections.sort(untyped, new TopicComparator());
      for (Iterator iterator = untyped.iterator(); iterator.hasNext();) {
        TopicIF scope = (TopicIF) iterator.next();
        JMenuItem scopeMenu = new JMenuItem(stringifier.apply(scope));
        scopeMenu.addActionListener(new ScopeActionListener(scope));
        scopingTopicsMenu.add(scopeMenu);
        scopeMenu.setEnabled(!scope.equals(currentScope));
      }
    }

    if (!map.isEmpty()) {
      scopingTopicsMenu.addSeparator();
      ArrayList grouping = new ArrayList(map.keySet());
      Collections.sort(grouping, new TopicComparator());

      for (Iterator iter = grouping.iterator(); iter.hasNext();) {
        TopicIF group = (TopicIF) iter.next();
        JMenu groupMenu = new JMenu(stringifier.apply(group));
        scopingTopicsMenu.add(groupMenu);
        List list = (List) map.get(group);
        Collections.sort(list, new TopicComparator());
        for (Iterator iterator = list.iterator(); iterator.hasNext();) {
          TopicIF scope = (TopicIF) iterator.next();
          JMenuItem scopeMenu = new JMenuItem(stringifier.apply(scope));
          scopeMenu.addActionListener(new ScopeActionListener(scope));
          groupMenu.add(scopeMenu);
          scopeMenu.setEnabled(!scope.equals(currentScope));
        }
      }
    }
  }

  protected void scopingTopicChanged(TopicIF scope) {
    setScopingTopic(scope);
    controller.setScopingTopic(scope);
    configureDynamicMenus();
    if (generalFrame != null)
      generalFrame.initializeTopicLists();
    if (assocFrame != null)
      assocFrame.initializeTypeList();
    if (topicFrame != null)
      topicFrame.initializeTypeList();

  }

  public void setScopingTopic(TopicIF topic) {
    currentScope = topic;
    stringifier = VizUtils.stringifierFor(topic);
  }

  @Override
  public void configureFilterMenus() {
    // clear any existing menu items
    visibleTopicTypesMenu.removeAll();
    visibleAssocTypesMenu.removeAll();
    
    // Create default association menu item.
    TopicIF defaultAssociationType = controller.getConfigurationManager()
        .defaultAssociationType; 
    ColouredSquareMenuItem defaultAssociationMenuItem = 
        setupAssociationMenuItem(defaultAssociationType);
    
    // Get and sort association types
    List types = new ArrayList(controller.getAssociationTypes());
    Collections.sort(types, new TopicComparator());
    associationTypeButtonMap = new HashMap(types.size());
    
    // Iterate association types
    Iterator ttypes = types.iterator();
    while (ttypes.hasNext()) {
      TopicIF type = (TopicIF) ttypes.next();

      if (type.equals(defaultAssociationType))
        continue;
      
      ColouredSquareMenuItem mItem = setupAssociationMenuItem(type);
      mItem.addActionListener(new AssocActionListener(type));
      defaultAssociationMenuItem.addActionListener(new DefaultActionListener(
          mItem));
      visibleAssocTypesMenu.add(mItem);
      associationTypeButtonMap.put(type, mItem);
    }
    
    defaultAssociationMenuItem.addActionListener(
        new AssocActionListener(defaultAssociationType));
    visibleAssocTypesMenu.add(defaultAssociationMenuItem);
    associationTypeButtonMap.put(controller.getConfigurationManager()
        .defaultAssociationType, defaultAssociationMenuItem);

    // Create Default topic menu item.
    TopicIF defaultType = controller.getConfigurationManager().defaultType;
    ColouredSquareMenuItem defaultTopicMenuItem
        = setupTopicMenuItem(defaultType);
    
    // Get and sort topic types.
    types = new ArrayList(controller.getAllTopicTypesWithNull());
    Collections.sort(types, new TopicComparator());
    topicTypeButtonMap = new HashMap(types.size());
    
    // Iterate topic types
    ttypes = types.iterator();
    while (ttypes.hasNext()) {
      TopicIF type = (TopicIF) ttypes.next();
      
      // Skip default type (treated separately.
      if (type.equals(defaultType))
        continue;
      
      ColouredSquareMenuItem mItem = setupTopicMenuItem(type);
      mItem.addActionListener(new TopicActionListener(type));
      defaultTopicMenuItem.addActionListener(new DefaultActionListener(mItem));
      visibleTopicTypesMenu.add(mItem);
      topicTypeButtonMap.put(type, mItem);
    }

    defaultTopicMenuItem.addActionListener(
        new TopicActionListener(defaultType));
    visibleTopicTypesMenu.add(defaultTopicMenuItem);
    topicTypeButtonMap.put(controller.getConfigurationManager().defaultType,
        defaultTopicMenuItem);
  }

  private ColouredSquareMenuItem setupAssociationMenuItem(TopicIF type) {
    String name =
        (type == controller.getConfigurationManager().defaultAssociationType)
        ? Messages.getString("Viz.DefaultType")
        : stringifier.apply(type);
    
    ColouredSquareMenuItem mItem = new ColouredSquareMenuItem(name,
        stateOf(type, false));
    mItem.setSquareColor(controller.getAssociationTypeColor(type));
    
    return mItem;
  }
  
  private ColouredSquareMenuItem setupTopicMenuItem(TopicIF type) {
    String name;
    if (type == null)
      name = Messages.getString("Viz.Untyped");
    else if (type == controller.getConfigurationManager()
        .defaultType)
      name = Messages.getString("Viz.DefaultType");
    else
      name = stringifier.apply(type);

    // add new ones based on topicmap ontology.
    ColouredSquareMenuItem mItem = new ColouredSquareMenuItem(name,
        stateOf(type, true));
    mItem.setSquareColor(controller.getTopicTypeColor(type));
    
    return mItem;
  }

  private byte stateOf(TopicIF type, boolean isTopicType) {
    VizTopicMapConfigurationManager confMan = controller
      .getConfigurationManager();

    TopicIF visibleTopic = confMan.getTypeVisibleTopic();
    if (confMan.hasOccurrence(type, visibleTopic)) {
      if (confMan.isAssociationTypeVisible(type))
        return VisibleIndicator.CHECKED;

      return VisibleIndicator.UNCHECKED;
    }
      
    if (isTopicType && confMan.isTopicTypeVisible(type) ||
        (!isTopicType) && confMan.isAssociationTypeVisible(type))
      return VisibleIndicator.DEFAULT_CHECKED;
      
    return VisibleIndicator.DEFAULT_UNCHECKED;
  }

  private void loadTopicMap(File f) throws IOException {
    this.resetFrame(assocFrame);
    assocFrame = null;
    this.resetFrame(topicFrame);
    topicFrame = null;
    this.resetFrame(generalFrame);
    generalFrame = null;
    this.resetFrame(precedenceFrame);
    precedenceFrame = null;
    disableMenuItems();

    if ("rdf".equals(getFileExtension(f)) || "n3".equals(getFileExtension(f))) {
      String mapping = controller.getRdfMappingFile();
      if (mapping == null)
        this.setRdfMappingFile(f.getAbsolutePath());
    }

    TopicMapIF newTopicMap;
    File defaultcfg = getConfigFileFor(f);
    if (defaultcfg.exists()) {
      newTopicMap = controller.loadTopicMap(f, defaultcfg);
      if (newTopicMap == null) {
        frame.setTitle(ONTOPIA_VIZDESKTOP_TITLE);
      } else {
        frame.setTitle(ONTOPIA_VIZDESKTOP_TITLE + " - " + f.getName() + " ("
            + defaultcfg.getName() + ")");
      }
    } else {
      newTopicMap = controller.loadTopicMap(f);
      if (newTopicMap == null) {
        frame.setTitle(ONTOPIA_VIZDESKTOP_TITLE);
      } else {
        frame.setTitle(ONTOPIA_VIZDESKTOP_TITLE + " - " + f.getName());
      }
    }

    if (newTopicMap != null) {
      currentTopicMap = newTopicMap;
      controller.setLocality(vPanel.getLocality());
      controller.updateRecentFiles(f);
      recentTopicMapFilesMenu.removeAll();
      displayRecentFileMenuItems(recentTopicMapFilesMenu);
      controller.saveGeneralConfiguration();

      controller.undoManager.reset();
    }

    if (vPanel != null) {
      configureDynamicMenus();
      enableMenuItems();
    }
  }
  
  public void loadTopicMap(TopicMapReferenceIF tmReference, 
      String configFilePath) throws IOException {
    this.resetFrame(assocFrame);
    assocFrame = null;
    this.resetFrame(topicFrame);
    topicFrame = null;
    this.resetFrame(generalFrame);
    generalFrame = null;
    this.resetFrame(precedenceFrame);
    precedenceFrame = null;
    disableMenuItems();

    TopicMapIF tm; 
    try {
      tm = tmReference.createStore(false)
          .getTopicMap();
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }
    
    String tmString = OpenRDBMSDialogBox.createTMString(tmReference);
    
    File defaultcfg = new File(configFilePath);
    if (defaultcfg.exists()) {
      controller.loadTopicMap(tm, defaultcfg);
      if (tm == null) {
        frame.setTitle(ONTOPIA_VIZDESKTOP_TITLE);
      } else {
        frame.setTitle(ONTOPIA_VIZDESKTOP_TITLE + " - " + tmString + " ("
            + defaultcfg.getName() + ")");
      }
    } else {
      tm = controller.loadTopicMap(tm);
      if (tm == null) {
        frame.setTitle(ONTOPIA_VIZDESKTOP_TITLE);
      } else {
        frame.setTitle(ONTOPIA_VIZDESKTOP_TITLE + " - " + tmString);
      }
    }

    if (tm != null) {
      currentTopicMap = tm;
      controller.setLocality(vPanel.getLocality());
      recentTopicMapFilesMenu.removeAll();
      displayRecentFileMenuItems(recentTopicMapFilesMenu);
      controller.saveGeneralConfiguration();

      if (vPanel != null) {
        configureDynamicMenus();
        enableMenuItems();
      }
    }
  }

  private void resetFrame(JFrame frame) {
    if (frame != null) {
      frame.setVisible(false);
      frame.dispose();
    }
  }

  /**
   * Called from the color configuration menu when the color for a topic or
   * association type is changed.
   */
  @Override
  public void setNewTypeColor(TopicIF type, Color c) {
    // Change in both topics and associations

    ColouredSquareMenuItem menuItem = (ColouredSquareMenuItem)topicTypeButtonMap
        .get(type);
    if (menuItem != null)
      menuItem.setSquareColor(c);

    menuItem = (ColouredSquareMenuItem) associationTypeButtonMap.get(type);
    if (menuItem != null)
      menuItem.setSquareColor(c);

  }

  private File getConfigFileFor(File tmfile) {
    tmfile = tmfile.getAbsoluteFile();
    File cfg = new File(tmfile.getParentFile(), tmfile.getName() + ".viz");

    return cfg;
  }

  // --- Internal classes

  class FileOpenMenuListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent ev) {
      JMenuItem source = (JMenuItem) ev.getSource();
      File f = new File(source.getText());
      try {
        loadTopicMap(f);
      } catch (Exception e) {
        // FIXME: Do we ever want this dialog? It was removed to fix bug
        // #2087, but are there any negative side effects?
        // Presumably, this error message always co-occurs with the error
        // message on (currently) line 546 in VizController (SwingWorker worker)
        //! ErrorDialog.showError(vPanel, Messages
        //!    .getString("Viz.TMLoadError"), e);
      }
    }
  }

  abstract class TypeActionListener implements ActionListener {
    protected TopicIF type;

    protected TypeActionListener(TopicIF type) {
      this.type = type;
    }

    public TopicIF getType() {
      return type;
    }
  }

  /**
   * Class to handle menu events.
   */
  class AssocActionListener extends TypeActionListener {
    protected AssocActionListener(TopicIF type) {
      super(type);
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
      ColouredSquareMenuItem source = (ColouredSquareMenuItem) ev.getSource();
      VisibleIndicator indicator = source.getVisibleIndicator();
      
      indicator.setSelected(indicator.isSelected() ? VisibleIndicator.UNCHECKED
          : VisibleIndicator.CHECKED);
      
      controller.setAssociationTypeVisible(type, indicator.isSelected());
      
      if (assocFrame != null)
        assocFrame.updateSelectedFilter();
    }
  }

  /** Class to handle menu events. */
  class TopicActionListener extends TypeActionListener {
    protected TopicActionListener(TopicIF type) {
      super(type);
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
      ColouredSquareMenuItem source = (ColouredSquareMenuItem) ev.getSource();
      VisibleIndicator indicator = source.getVisibleIndicator();

      indicator.setSelected(indicator.isSelected()
          ? VisibleIndicator.UNCHECKED
          : VisibleIndicator.CHECKED);

      controller.setTopicTypeVisible(type, indicator.isSelected());

      if (topicFrame != null)
        topicFrame.updateSelectedFilter();
    }
  }

  /**
   * Class to handle menu events.
   */
  class DefaultActionListener implements ActionListener {
    private ColouredSquareMenuItem mItem;

    protected DefaultActionListener(ColouredSquareMenuItem mItem) {
      this.mItem = mItem;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
      ColouredSquareMenuItem source = (ColouredSquareMenuItem) ev.getSource();

      byte selectedValue = source.getVisibleIndicator().getSelected();
      mItem.getVisibleIndicator().setDefault(selectedValue);
    }
  }

  private void menuLoadTopicMap() {
    JFileChooser fc = new JFileChooser(getCurrentTopicMapDirectory());

    SimpleFileFilter def = new SimpleFileFilter(Messages
        .getString("Viz.XTMfiles"), "xtm");
    fc.addChoosableFileFilter(def);
    fc.addChoosableFileFilter(new SimpleFileFilter(Messages
        .getString("Viz.CTMfiles"), "ltm"));
    fc.addChoosableFileFilter(new SimpleFileFilter(Messages
        .getString("Viz.LTMfiles"), "ltm"));
    fc.addChoosableFileFilter(new SimpleFileFilter(Messages
        .getString("Viz.RDFfiles"), "rdf", "n3"));
    fc.addChoosableFileFilter(new SimpleFileFilter(Messages
        .getString("Viz.TMXMLfiles"), "tmx"));
    fc.setFileFilter(def);

    int returnVal = fc.showOpenDialog(vPanel);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File f = fc.getSelectedFile();
      setCurrentTopicMapDirectory(f.getParent());
      try {
        loadTopicMap(f);
      } catch (Exception e) {
        // FIXME: Do we ever want this dialog? It was removed to fix bug
        // #2087, but are there any negative side effects?
        // Presumably, this error message always co-occurs with the error
        // message on (currently) line 546 in VizController (SwingWorker worker)
        //! ErrorDialog.showError(vPanel, Messages
        //!    .getString("Viz.TMLoadError"), e);
      }
    }
  }

  private void menuOpenRDBMSTopicMap() {
    if (openBox == null)
      openBox = new OpenRDBMSDialogBox(this);
    openBox.show();
  }
  
  private String getFileExtension(File file) {
    String name = file.getName();
    int pos = name.lastIndexOf('.');
    if (pos == -1) {
      return null;
    }
    return name.substring(pos + 1).toLowerCase();
  }

  private void menuOpenAboutWindow() {
    Window parent = SwingUtilities.windowForComponent(vPanel);
    JDialog aboutframe = new AboutFrame((Frame) parent);
    aboutframe.show();
  }

  protected void menuOpenAssociationConfig() {
    if (!controller.hasTopicMap())
      return;

    if (assocFrame == null) {
      assocFrame = TypesConfigFrame.createAssociationTypeConfigFrame(
          controller, this);
      assocFrame.show();
    } else {
      assocFrame.setVisible(true);
      assocFrame.toFront();
    }
  }

  private void menuOpenGeneralConfig() {
    if (!controller.hasTopicMap())
      return;

    if (generalFrame == null) {
      generalFrame = new GeneralConfigFrame(controller);
      generalFrame.show();
    } else {
      generalFrame.setVisible(true);
      generalFrame.toFront();
    }
  }

  private void menuOpenPrecedenceConfig() {
    if (!controller.hasTopicMap())
      return;

    if (precedenceFrame == null) {
      precedenceFrame = new TypesPrecedenceFrame(controller);
      precedenceFrame.show();
    } else {
      precedenceFrame.setVisible(true);
      precedenceFrame.toFront();
    }
  }

  protected void menuOpenTopicConfig() {
    if (!controller.hasTopicMap())
      return;

    if (topicFrame == null) {
      topicFrame =
        TypesConfigFrame.createTopicTypeConfigFrame(controller, this);
      topicFrame.show();
    } else {
      topicFrame.setVisible(true);
      topicFrame.toFront();
    }
  }

  private void menuSaveConfiguration() {
    if (!controller.hasTopicMap())
      return;

    File existing = getFileFor(controller.getConfigurationManager()
        .getTopicMap());
    if (existing == null) 
      existing = getConfigFileFor(getFileFor(controller.getTopicMap()));

    JFileChooser fc = new JFileChooser();
      fc.setSelectedFile(existing);
    SimpleFileFilter def = new SimpleFileFilter(Messages
        .getString("Viz.ConfigFilesDescription"), "viz");
    fc.addChoosableFileFilter(def);
    int returnVal = fc.showSaveDialog(vPanel);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File f = fc.getSelectedFile();
      try {
        controller.saveTopicMapConfiguration(f);
      } catch (IOException e) {
        ErrorDialog.showError(vPanel, e);
      }
    }
  }

  private File getFileFor(TopicMapIF topicmap) {
    try {
      return URIUtils.getURIFile(topicmap.getStore().getBaseAddress());
    } catch (MalformedURLException e) {
      return null;
    }
  }

  public String getCurrentTopicMapDirectory() {
    String configDir = controller.getCurrentTMDir();
    if (configDir != null)
      return configDir;
    
    if (this.currentTopicMap == null)
      return null;

    // This seems like an overly convoluted way to get to the
    // (String)path of the currently loaded TM, but I have not
    // been able to find a simpler method.

    try {
      return new URL(this.currentTopicMap.getStore().getBaseAddress()
          .getAddress()).getPath();
    } catch (MalformedURLException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  public void setCurrentTopicMapDirectory(String currentTMDir) {
    try {
      controller.setCurrentTMDir(currentTMDir);
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  public String getCurrentRDBMSDir() {
    return controller.getCurrentRDBMSDir();
  }
  
  public void setCurrentRDBMSDir(String dir) {
    try {
      controller.setCurrentRDBMSDir(dir);
    } catch (IOException e) {
      ErrorDialog.showError(vPanel, Messages.getString("Viz.DirError"), e);
    }
  }
  
  public String getCurrentConfigDir() {
    return controller.getCurrentConfigDir();
  }
  
  public void setCurrentConfigDir(String dir) {
    try {
      controller.setCurrentConfigDir(dir);
    } catch (IOException e) {
      ErrorDialog.showError(vPanel, Messages.getString("Viz.DirError"), e);
    }
  }
  
  public TopicMapIF getCurrentTopicMap() {
    return currentTopicMap;
  }

  protected void resetStartTopicMenu() {
    Node focusNode = controller.getFocusNode();
    TopicIF startTopic = controller.getStartTopic();

    // Not very pretty, but it does the job and I am a bit short of time
    focusStartTopicItem
        .setEnabled((startTopic != null && (focusNode == null || !(focusNode instanceof TMTopicNode)))
            || (startTopic != null && (focusNode != null) && !startTopic
                .equals(((TMTopicNode) focusNode).getTopic())));

  }

  public Component getVpanel() {
    return vPanel;
  }

  /**
   * INTERNAL.
   */
  protected class ScopeActionListener implements ActionListener {
    private TopicIF scope;

    protected ScopeActionListener(TopicIF topic) {
      scope = topic;
    }

    @Override
    public void actionPerformed(ActionEvent aE) {
      scopingTopicChanged(scope);
    }
  }
  
  protected class DynamicMenuListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent aE) {
      configureDynamicMenus();
    }
  }

  // --- VizFrontEndIF implementation
  
  @Override
  public ApplicationContextIF getContext() {
    return new DesktopContext(this);
  }

  @Override
  public boolean getDefaultControlsVisible() {
    return true;
  }

  @Override
  public TypesConfigFrame getTypesConfigFrame(VizController controller, boolean isTopicConfig) {
    if(isTopicConfig) {
      return TypesConfigFrame.createTopicTypeConfigFrame(controller, this);
    } else {
      return TypesConfigFrame.createAssociationTypeConfigFrame(controller, this);
    }
  }
  
  @Override
  public boolean mapPreLoaded() {
    return false;
  }

  @Override
  public TopicMapIF getTopicMap() {
    return currentTopicMap;
  }

  @Override
  public String getWallpaper() {
    return null;
  }
  
  @Override
  public String getConfigURL() {
    return null;
  }
  
  @Override
  public boolean useGeneralConfig() {
    return true;
  }
}
