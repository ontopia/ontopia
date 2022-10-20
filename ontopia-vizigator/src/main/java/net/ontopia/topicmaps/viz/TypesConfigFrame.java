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

import com.touchgraph.graphlayout.Node;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.colorchooser.DefaultColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.nav.utils.comparators.TopicComparator;
import net.ontopia.utils.SimpleFileFilter;

/**
 * This class provides a display to allow the user to define which colours are
 * used when displaying associations of this type.
 */
public class TypesConfigFrame extends JFrame implements ListSelectionListener {
  public static final int UNDEFINED_EDGE_SHAPE = -1;
  public static final int UNDEFINED_EDGE_SHAPE_WEIGHT = -1;
  public static final int UNDEFINED_NODE_SHAPE_PADDING = -1;
  public static final int UNDEFINED_NODE_SHAPE = -1;
 
  private static final String[] AVAILABLE_SIZES = { "9", "10", "11", "12", "14", 
      "16", "18", "20", "22", "24", "28", "36" };

  private JColorChooser chooser;

  private VizController controller;
  private ConfigurationModelIF model;
  private TopicIF selectedType;
  private JList<TopicListItem> typeList;
  private boolean ignoreSelection = false;
  private ButtonGroup buttonGroup; // Shape buttons
  private ButtonGroup filterGroup; // Filter buttons
  private JTextField filenameField;
  private JRadioButton[] shapeMap; // Maps buttons to models
  private JRadioButton[] filterMap; // Maps buttons to models
  private JSlider weight;
  private JList fontList;
  private JCheckBox bold;
  private JCheckBox italic;
  private DefaultColorSettingCheckBox defaultColorSettingCheckbox;

  private JList sizeList;
  private String lastIconPath;

  private JButton clearButton;

  private VizDesktop desktop;

  // Topic type shape radio buttons.
  private JRadioButton circle;
  private JRadioButton ellipse;
  private JRadioButton rectangle;
  private JRadioButton round;

  // Association type shape radio buttons.
  private JRadioButton bowtie;
  private JRadioButton line;
  private JRadioButton unselect;
  
  private JRadioButton filterIn;
  private JRadioButton filterOut;
  private JRadioButton filterDefault;
  
  private JButton defaultButton;
  
  public static TypesConfigFrame createAssociationTypeConfigFrame(
      VizController controller, VizDesktop desktop) {
    controller.loadAssociationTypes();
    TypesConfigFrame frame = new TypesConfigFrame(controller, desktop);
    frame.setAssociationTypeModel();
    frame.build();
    return frame;
  }

  public static TypesConfigFrame createTopicTypeConfigFrame(
      VizController controller, VizDesktop desktop) {
    controller.loadTopicTypes();
    TypesConfigFrame frame = new TypesConfigFrame(controller, desktop);
    frame.setTopicTypeModel();
    frame.build();
    return frame;
  }

  private TypesConfigFrame(VizController controller, VizDesktop desktop) {
    super();
    this.controller = controller;
    this.desktop = desktop;

    unselect = new JRadioButton();
    unselect.setVisible(false);
  }

  private void build() {
    setTitle(model.getTitle());

    // populate JList
    typeList = new JList<>();
    initializeTypeList();
    
    // Set the first
    typeList.setSelectedIndex(0);
    selectedType = typeList.getSelectedValue().getTopic();

    // setup UI
    getContentPane().setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    c.fill = GridBagConstraints.BOTH;
    typeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    typeList.addListSelectionListener(this);

    JScrollPane scrlPane = new JScrollPane(typeList);
    getContentPane().add(scrlPane, c);

    JTabbedPane tabbedPane = new JTabbedPane();
    
    tabbedPane
        .addTab(
            Messages.getString("Viz.StylingConfigTitle"), null,
                createGeneralConfigPanel(),
            Messages.getString("Viz.StylingConfigHoverHelp"));
    tabbedPane
        .addTab(
            Messages.getString("Viz.ColourConfigTitle"), null,
                createColorChooserPanel(),
            Messages.getString("Viz.ColourConfigHoverHelp"));
    tabbedPane
        .addTab(
            Messages.getString("Viz.FontConfigTitle"), null,
                createFontSelectionPanel(),
            Messages.getString("Viz.FontConfigHoverHelp"));
    tabbedPane
        .addTab(Messages.getString("Viz.TypeFilter"), null,
                createFilterSelectionPanel(),
            Messages.getString("Viz.FilterConfigHoverHelp"));
    
    
    
    JPanel parent = new JPanel();
    parent.setLayout(new BoxLayout(parent, BoxLayout.Y_AXIS));
    parent.add(tabbedPane);
    parent.add(createDefaultPanel());
    getContentPane().add(parent, c);
    
    pack();
    setResizable(false);
    
    defaultColorSettingCheckbox.update();
  }
  
  private JPanel createDefaultPanel() {
    JPanel retVal = new JPanel();
    retVal.setLayout(new BoxLayout(retVal, BoxLayout.X_AXIS));
    defaultButton = new JButton(Messages
        .getString("Viz.UseDefaultSettings"));
    defaultButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        VizTopicMapConfigurationManager confMan = controller
            .getConfigurationManager();
        if (getUsingTopicModel()) {
          // Remove color autogenerated setting from type.
          confMan.removeOccurence(selectedType,
              confMan.getTopicTypeColorAutogeneratedTopic());

          // Remove color setting from topic type.
          controller.setColorToDefault(selectedType, true);
          
          // Remove font setting from topic type.
          controller.setFontToDefault(selectedType, true);
          setSelectedFont(controller.getTypeFont(selectedType));

          // Remove icon setting from topic type.
          controller.setTypeIconFilename(selectedType, null);

          // Remove shape padding setting from topic type.
          setSelectedWeight(model.getWeight(controller, confMan.defaultType));
          controller.setTopicTypeShapePadding(selectedType,
              UNDEFINED_NODE_SHAPE_PADDING);

          // Remove shape setting from topic type.
          unselectShapeRadioButtons();          
          controller.setTopicTypeShape(selectedType, UNDEFINED_NODE_SHAPE);
        } else {
          // Remove color autogenerated setting from type.
          confMan.removeOccurence(selectedType,
              confMan.getAssociationTypeColorAutogeneratedTopic());

          // Remove color setting from association type.
          controller.setColorToDefault(selectedType, false);

          // Remove font setting from association type.
          controller.setFontToDefault(selectedType, false);
          setSelectedFont(controller.getTypeFont(selectedType));

          // Remove line weight setting from type.
          setSelectedWeight(model.getWeight(controller,
              confMan.defaultAssociationType));
          controller.setAssociationTypeLineWeight(selectedType,
              UNDEFINED_EDGE_SHAPE_WEIGHT);

          // Remove shape setting from type.
          unselectShapeRadioButtons();          
          controller.setAssociationTypeShape(selectedType, UNDEFINED_EDGE_SHAPE);
        }

        // Make the appropriate font selection (or no selection, as the case may be)
        // in the font selection interface.
        updateFontSelection();
      }
    });
    retVal.add(defaultButton);
    
    return retVal;
  }
  
  private boolean getUsingTopicModel() {
    return model instanceof TopicTypeConfigurationModel;
  }
  
  public class DefaultColorSettingCheckBox extends JCheckBox {
    public DefaultColorSettingCheckBox() {
      super(Messages.getString("Viz.OverrideRandomColors"));
      setupCheckBox();
    }
    
    public void setupCheckBox() {
      addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          VizTopicMapConfigurationManager confMan = controller
              .getConfigurationManager();
  
          boolean defaultSelected = selectedType == confMan.defaultType ||
            selectedType == confMan.defaultAssociationType;
          
          if (defaultSelected) {
            handleDefaultSelection();
          } else {
            handleRegularSelection();
          }
        }
      });
    }

    private void handleDefaultSelection() {
      VizTopicMapConfigurationManager confMan = controller
          .getConfigurationManager();
      
      TopicIF type = getUsingTopicModel() ? confMan.defaultType
          : confMan.defaultAssociationType;

      confMan.setOccurenceValue(type, confMan.getOverrideColorsTopic(), 
          isSelected());

      Color c = getModelColor();

      // Now get back the old color to (event will get triggered).
      chooser.setColor(c);
    }
    
    private void handleRegularSelection() {
      VizTopicMapConfigurationManager confMan = controller
          .getConfigurationManager();

      boolean selected = isSelected();
      
      if (!selected) {
        chooser.setColor(chooser.getColor());
        return;
      }
      
      // Override (or not, pending selected) this color with default color.
      confMan.setOccurenceValue(selectedType, getUsingTopicModel() 
          ? confMan.getTopicTypeColorAutogeneratedTopic()
          : confMan.getAssociationTypeColorAutogeneratedTopic(), selected);
      
      // If the default type overrides automatic colours, get rid of old color.
      // This overrides color setting permanently.
      if (confMan.getUsesDefault(selectedType, getUsingTopicModel())) {
        confMan.removeOccurrence(selectedType, 
            confMan.getTopicTypeColorTopic());
      }
      
      // Update selectedType with appropriate (default) color.
      Color color = getUsingTopicModel()
          ? controller.getTopicTypeColor(confMan.defaultType)
          : controller.getAssociationTypeColor(confMan.defaultAssociationType);
      controller.updateViewTypeColor(selectedType, color);
    }
    
    private void update() {
      VizTopicMapConfigurationManager confMan = controller
          .getConfigurationManager();
      
      // Check if this is a default type or a regular (proper) type.
      boolean useDefaultMode = selectedType == confMan.defaultType
          || selectedType == confMan.defaultAssociationType;

      boolean selected;
      if (useDefaultMode) {
        // The (un)checked state of the default type takes it's value from the
        // configuration. With no given value, use unchecked (false) as default.
        // So, by default it doesn't override autogenerated color values.
        selected = confMan.defaultOverrides(getUsingTopicModel());
      } else {
        // The (un)checked state of proper types are checked by default.
        // So, by default they do let the default color value toke precedence
        // when the default overrides autogenerated color values.
        selected = confMan.getUsesDefault(selectedType, getUsingTopicModel());
      }
        
      setText(Messages.getString(useDefaultMode
          ? "Viz.OverrideRandomColors" : "Viz.UseDefaultColor"));
      setSelected(selected);
    }
  }
  
  private Color getModelColor() {
    return model.getColor(controller, selectedType);
  }
  
  /**
   * Makes sure fireStateChanged() is called whenever there's been a color
   * selection. This ensures default color is correctly overridden when an
   * old default color is selected.
   */
  public class VizColorSelectionModel extends DefaultColorSelectionModel {
    @Override
    public void setSelectedColor(Color color) {
      super.setSelectedColor(color);
      if (color != null && getSelectedColor().equals(color)) {
        fireStateChanged();
      }
    }
  }
  
  protected JPanel createColorChooserPanel() {
    JPanel parent = new JPanel(new BorderLayout());
    chooser = new JColorChooser(new VizColorSelectionModel());

    // Setting #setPreviewPanel() to a new empty JPanel should
    // remove the preview panel from use.
    chooser.setPreviewPanel(new JPanel());
    chooser.getSelectionModel().addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        if (typeList.getSelectedIndex() == -1) {
          return;
        }

        Color c = chooser.getColor();
        controller.setTypeColor(selectedType, c);
        if (desktop != null) { // LMG 2008-07-17: why not via controller?
          desktop.setNewTypeColor(selectedType, c);
        }

        // Make default colour setting checkbox take correct (un)checked value.
        defaultColorSettingCheckbox.update();
      }
    });
    
    parent.add(chooser, BorderLayout.NORTH);

    defaultColorSettingCheckbox = new DefaultColorSettingCheckBox();
    parent.add(defaultColorSettingCheckbox, BorderLayout.SOUTH);
    
    return parent;
  }
  
  /**
   * Initialize the list of topic types.
   */
  protected void initializeTypeList() {
    List<TopicListItem> ttypes = new ArrayList<>();
    Collection graphtypes = model.getListItems(controller);
    // sort the topics
    graphtypes = new ArrayList(graphtypes);
    Collections.sort((java.util.List) graphtypes, new TopicComparator());
    Iterator gtypes = graphtypes.iterator();
    while (gtypes.hasNext()) {
      TopicIF t = (TopicIF) gtypes.next();
      
      
      if (t == controller.getConfigurationManager().defaultType) {
        ttypes.add(new TopicListItem(t, Messages.getString("Viz.DefaultType")));
      } else if (t == controller.getConfigurationManager().defaultAssociationType) {
        ttypes.add(new TopicListItem(t, Messages.getString("Viz.DefaultType")));
      } else if (t != null) {
        ttypes.add(new TopicListItem(t, controller.getStringifier()));
      }
    }
    model.addAdditionalItems(ttypes);
    typeList.setListData(ttypes.toArray(new TopicListItem[ttypes.size()]));
  }
  
  /**
   * Convert an array of font names to an array of alphabetically ordered font
   * family names.
   * @param names The font names.
   * @return The font family names (without duplicates) of all the font names.
   */
  private String[] toFamilyNames(String names[]) {
    SortedSet familyNames = new TreeSet();
    
    for (int i = 0; i < names.length; i++) {
      familyNames.add(new Font(names[i], 12, Font.PLAIN).getFamily());
    }
    
    String familyNamesArray[] = new String[familyNames.size()];
    familyNames.toArray(familyNamesArray);
    return familyNamesArray;
  }

  private JPanel createFontSelectionPanel() {
    JPanel parent = new JPanel(new BorderLayout());

    GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
    
    // Note: Despite the name of the method "getAvailableFontFamilyNames", it
    // actually returns the font names, and not the font family names.
    // The method "toFamilyNames" solves this, by generating a list of the
    // corresponding font names.
    String[] fonts = g.getAvailableFontFamilyNames();
    fonts = toFamilyNames(fonts);
    
    JPanel details = new JPanel(new BorderLayout());

    JPanel fontPanel = new JPanel(new BorderLayout());
    fontPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory
        .createEtchedBorder(), Messages.getString("Viz.FontBorderTitle")));

    fontList = new JList(fonts);
    fontList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JScrollPane fontpane = new JScrollPane(fontList);
    fontList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        // Only handle the change in selection when the user has finally made 
        // his selection !

        if (e.getValueIsAdjusting()) {
          return;
        }
        buildAndSetFont();
      }
    });

    fontpane.setBorder(BorderFactory.createEtchedBorder());
    fontPanel.add(fontpane);
    parent.add(fontPanel);

    JPanel sizePanel = new JPanel(new BorderLayout());
    sizePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory
        .createEtchedBorder(), Messages.getString("Viz.FontSizeBorderTitle")));

    sizeList = new JList(AVAILABLE_SIZES);
    sizeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    sizeList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        // Only handle the change in selection when the user has finally made
        // his selection !
        if (e.getValueIsAdjusting() == true) {
          return;
        }
        buildAndSetFont();
      }
    });

    JScrollPane sizePane = new JScrollPane(sizeList);
    sizePane.setBorder(BorderFactory.createEtchedBorder());
    sizePanel.add(sizePane);

    JPanel attributes = new JPanel(new GridLayout(1, 2));
    attributes.setBorder(BorderFactory.createTitledBorder(BorderFactory
        .createEtchedBorder(), Messages
        .getString("Viz.FontAttributesBorderTitle")));
    bold = new JCheckBox(Messages.getString("Viz.FontBold"));
    bold.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        buildAndSetFont();
      }
    });

    italic = new JCheckBox(Messages.getString("Viz.FontItalic"));
    italic.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        buildAndSetFont();
      }
    });

    attributes.add(bold);
    attributes.add(italic);

    details.add(sizePanel, BorderLayout.CENTER);
    details.add(attributes, BorderLayout.SOUTH);
    parent.add(details, BorderLayout.EAST);

    return parent;
  }

  /**
   * Build a new font object from the GUI and assign it
   */
  protected void buildAndSetFont() {
    if (typeList.getSelectedValue() == null) {
      return;
    }
    // We need @ignoreSelection since we cannot set the current
    // font without signalling the font changes callbacks
    if (ignoreSelection) {
      return;
    }

    String name = (String) fontList.getSelectedValue();
    String sizeString = (String) sizeList.getSelectedValue();
    boolean isBold = bold.isSelected();
    boolean isItalic = italic.isSelected();

    if (name == null || sizeString == null) {
      Font fallbackFont = controller.getTypeFont(selectedType);
      
      if (name == null && sizeString == null && !isBold) {
        isBold = fallbackFont.isBold();
      }

      if (name == null && sizeString == null && !isItalic) {
        isItalic = fallbackFont.isItalic();
      }
      
      if (name == null) {
        name = fallbackFont.getFamily();
        fontList.setSelectedValue(name, true);
      }
      
      if (sizeString == null) {
        sizeString = Integer.toString(fallbackFont.getSize());
        sizeList.setSelectedValue(sizeString, true);
      }
    } 
    
    int style = Font.PLAIN;
    if (isBold) {
      style = style | Font.BOLD;
    }
    if (isItalic) {
      style = style | Font.ITALIC;
    }

    controller.setTypeFont(selectedType, new Font(name, style, Integer
        .parseInt(sizeString)));
  }

  public void buildAssociationTypeGeneralConfigPanel(JPanel config) {
    config.add(createAssociationTypeShapePanel());
    config.add(createIconPanel());
  }

  public void buildTopicTypeGeneralConfigPanel(JPanel config) {
    config.add(createTopicTypeShapePanel());
    config.add(createIconPanel());
  }

  private JPanel createAssociationTypeShapePanel() {
    // Make the size of the shapeMap one bigger than the number of radio buttons
    shapeMap = new JRadioButton[3];

    JPanel shapePanel = new JPanel(new GridLayout(0, 1));
    shapePanel.setBorder(BorderFactory
        .createTitledBorder(BorderFactory.createEtchedBorder(), Messages
            .getString("Viz.ObjectShapeBorderTitle")));

    bowtie = new JRadioButton(Messages
        .getString("Viz.ObjectShapeBowtie"));
    bowtie.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setShape(TMRoleEdge.SHAPE_BOWTIE);
      }
    });
    shapePanel.add(bowtie);
    shapeMap[TMRoleEdge.SHAPE_BOWTIE] = bowtie;

    line = new JRadioButton(Messages
        .getString("Viz.ObjectShapeLine"));
    line.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setShape(TMRoleEdge.SHAPE_LINE);
      }
    });
    shapePanel.add(line);
    shapeMap[TMRoleEdge.SHAPE_LINE] = line;

    //Group the radio buttons.
    buttonGroup = new ButtonGroup();
    buttonGroup.add(bowtie);
    buttonGroup.add(line);
    buttonGroup.add(unselect);

    shapePanel.add(createWeightPanel(
        Messages.getString("Viz.ObjectShapeWeight"), 1,
            TMRoleEdge.DEFAULT_LINE_WEIGHT * 3));

    return shapePanel;
  }
  
  private JPanel createAssociationTypeFilterPanel() {
    // Make the size of the shapeMap one bigger than the number of radio buttons
    filterMap = new JRadioButton[4];
    JPanel filterPanel = new JPanel(new GridLayout(0, 1));
    filterPanel.setBorder(BorderFactory
        .createTitledBorder(BorderFactory.createEtchedBorder(),
                            Messages.getString("Viz.TypeFilter")));

    filterIn = new JRadioButton(Messages.getString("Viz.FilterIn"));
    filterIn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setFilter(VizTopicMapConfigurationManager.FILTER_IN);
      }
    });
    filterPanel.add(filterIn);
    filterMap[VizTopicMapConfigurationManager.FILTER_IN] = filterIn;
    
    filterOut = new JRadioButton(Messages.getString("Viz.FilterOut"));
    filterOut.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setFilter(VizTopicMapConfigurationManager.FILTER_OUT);
      }
    });
    filterPanel.add(filterOut);
    filterMap[VizTopicMapConfigurationManager.FILTER_OUT] = filterOut;
    
    filterDefault = new JRadioButton(Messages
        .getString("Viz.UseDefaultFilterSetting"));
    filterDefault.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setFilter(VizTopicMapConfigurationManager.FILTER_DEFAULT);
      }
    });
    filterPanel.add(filterDefault);
    filterMap[VizTopicMapConfigurationManager.FILTER_DEFAULT] = filterDefault;
        
    //Group the radio buttons.
    filterGroup = new ButtonGroup();
    filterGroup.add(filterIn);
    filterGroup.add(filterOut);
    filterGroup.add(filterDefault);
    filterGroup.add(unselect);

    return filterPanel;
  }

  private Box createWeightPanel(String title, int min, int max) {
    Box weightPanel = new Box(BoxLayout.X_AXIS);

    weightPanel.add(Box.createHorizontalStrut(10));
    weightPanel.add(new JLabel(title));
    weightPanel.add(Box.createHorizontalStrut(10));

    weight = new JSlider(JSlider.HORIZONTAL, min, max, 1);
    weight.addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {
        setWeight(((JSlider) e.getSource()).getValue());
      }
    });

    weightPanel.add(weight);
    weightPanel.add(Box.createHorizontalStrut(10));
    return weightPanel;
  }

  private JPanel createGeneralConfigPanel() {
    JPanel config = new JPanel(new GridLayout(0, 1));
    model.buildGeneralConfigPanel(this, config);
    return config;
  }

  private JPanel createFilterSelectionPanel() {
    JPanel config = new JPanel(new GridLayout(0, 1));
    config.add(createAssociationTypeFilterPanel());
    updateSelectedFilter();
    return config;
  }
  
  private JPanel createIconPanel() {
    JPanel iconPanel = new JPanel();
    iconPanel.setLayout(new BoxLayout(iconPanel, BoxLayout.X_AXIS));
    iconPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory
        .createEtchedBorder(), Messages.getString("Viz.IconBorderTitle")));
    iconPanel.add(Box.createHorizontalStrut(10));
    iconPanel.add(new JLabel(Messages.getString("Viz.IconFilename")));
    iconPanel.add(Box.createHorizontalStrut(10));

    filenameField = new JTextField(15);
    // Stupid ... stupid ... stupid ... This is the only way I would get the components to layout correctly !
    filenameField.setMaximumSize(new Dimension((int) (filenameField
        .getMaximumSize().getWidth()), (int) (filenameField.getPreferredSize()
        .getHeight())));
    filenameField.setEditable(false);

    iconPanel.add(filenameField);
    iconPanel.add(Box.createHorizontalStrut(10));

    // Must be final to refer to in inner class.
    final Component thisComponent = this;

    JButton fileButton = new JButton(Messages.getString("Viz.IconBrowseButton"));
    fileButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          String filename = promptForFile();
          if (filename != null) {
            setSelectedIconFilename(filename);
            setIconFilename(filename);
          }
        } catch (java.security.AccessControlException exception) {
          ErrorDialog.showError(thisComponent, Messages.getString(
              "Viz.FileBrowseFailure"));
        }
      }
    });

    iconPanel.add(fileButton);
    iconPanel.add(Box.createHorizontalStrut(10));

    clearButton = new JButton(Messages.getString("Viz.IconClear"));
    clearButton.setEnabled(false);
    clearButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent anE) {
        setSelectedIconFilename(null);
        setIconFilename(null);
      }
    });

    iconPanel.add(clearButton);
    return iconPanel;
  }
  
  private JPanel createTopicTypeShapePanel() {
    // Make the size of the shapeMap one bigger than the number of
    // radio buttons
    shapeMap = new JRadioButton[5];

    JPanel shapePanel = new JPanel(new GridLayout(0, 1));
    shapePanel.setBorder(BorderFactory
        .createTitledBorder(BorderFactory.createEtchedBorder(), Messages
            .getString("Viz.ObjectShapeBorderTitle")));

    circle = new JRadioButton(Messages
        .getString("Viz.ObjectShapeCircle"));
    circle.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setShape(Node.TYPE_CIRCLE);
      }
    });

    shapePanel.add(circle);
    shapeMap[Node.TYPE_CIRCLE] = circle;

    ellipse = new JRadioButton(Messages
        .getString("Viz.ObjectShapeEllipse"));
    ellipse.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        setShape(Node.TYPE_ELLIPSE);
      }
    });
    shapePanel.add(ellipse);
    shapeMap[Node.TYPE_ELLIPSE] = ellipse;

    rectangle = new JRadioButton(Messages
        .getString("Viz.ObjectShapeRectangle"));
    rectangle.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setShape(Node.TYPE_RECTANGLE);
      }
    });
    shapePanel.add(rectangle);
    shapeMap[Node.TYPE_RECTANGLE] = rectangle;

    round = new JRadioButton(Messages
        .getString("Viz.ObjectShapeRoundRectangle"));
    round.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setShape(Node.TYPE_ROUNDRECT);
      }
    });
    shapePanel.add(round);
    shapeMap[Node.TYPE_ROUNDRECT] = round;

    // Group the radio buttons.
    buttonGroup = new ButtonGroup();
    buttonGroup.add(circle);
    buttonGroup.add(ellipse);
    buttonGroup.add(rectangle);
    buttonGroup.add(round);
    buttonGroup.add(unselect);

    shapePanel.add(createWeightPanel(
        Messages.getString("Viz.ObjectShapePadding"), 0, 
            TMTopicNode.MAX_SHAPE_PADDING));

    return shapePanel;
  }

  protected String promptForFile() {
    JFileChooser dialog = new JFileChooser(lastIconPath);

    dialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
    dialog.setAcceptAllFileFilterUsed(false);
    dialog.setDialogTitle(Messages.getString("Viz.SelectIcon"));
    dialog.setSelectedFile(new File(filenameField.getText()));

    SimpleFileFilter filter = new SimpleFileFilter(Messages
        .getString("Viz.ImageFiles"));
    filter.addExtension("JPG");
    filter.addExtension("JPEG");
    filter.addExtension("GIF");
    filter.addExtension("PNG");

    dialog.setFileFilter(filter);

    dialog.showOpenDialog(this);
    File file = dialog.getSelectedFile();
    if (file == null) {
      return null;
    }

    lastIconPath = file.getPath();
    return file.getAbsolutePath();
  }

  private void setAssociationTypeModel() {
    model = new AssociationTypeConfigurationModel();
  }

  protected void setIconFilename(String string) {
    if (typeList.getSelectedValue() == null) {
      return;
    }
    controller.setTypeIconFilename(selectedType, string);
  }

  public void setSelectedIconFilename(String string) {
    filenameField.setText(string);
    if (string == null || string.length() == 0) {
      clearButton.setEnabled(false);
    } else {
      clearButton.setEnabled(true);
    }
  }

  protected void setWeight(int i) {
    if (typeList.getSelectedValue() == null) {
      return;
    }
    model.setWeight(controller, selectedType, i);
  }

  public void setSelectedShape(int i) {
    buttonGroup.setSelected(shapeMap[i].getModel(), true);
    
    Enumeration elements = buttonGroup.getElements();
    while(elements.hasMoreElements()) {
      ((AbstractButton)elements.nextElement()).repaint();
    }
  }

  public void setSelectedFilter(int i) {
    filterGroup.setSelected(filterMap[i].getModel(), true);

    Enumeration elements = filterGroup.getElements();
    while(elements.hasMoreElements()) {
      ((AbstractButton)elements.nextElement()).repaint();
    }
  }
  
  /**
   * When some other class changes the filter selection, this method updates
   * this GUI.
   * If other parts of the configuration, such as colour and shape,
   * become changable in other classes, this method is probably the right place
   * to update the GUI. 
   */
  public void updateSelectedFilter() {
    int i = controller.getTypeVisibility(selectedType);
    setSelectedFilter(i);
  }
  
  // Unselects all the radio topic/association type shape radio buttons.
  private void unselectShapeRadioButtons() {
    buttonGroup.setSelected(unselect.getModel(), true);
    
    Enumeration elements = buttonGroup.getElements();
    while(elements.hasMoreElements()) {
      ((AbstractButton)elements.nextElement()).repaint();
    }
  }

  public void setSelectedWeight(int i) {
    weight.setValue(i);
  }

  protected void setShape(int i) {
    if (typeList.getSelectedValue() == null) {
      return;
    }

    model.setShape(controller, selectedType, i);
  }

  protected void setFilter(int i) {
    if (typeList.getSelectedValue() == null) {
      return;
    }
    model.setFilter(controller, selectedType, i);
    if (desktop != null) {
      desktop.configureFilterMenus();
    }
  }

  private void setTopicTypeModel() {
    model = new TopicTypeConfigurationModel();
  }
  
  @Override
  public void valueChanged(ListSelectionEvent e) {
    // Only handle the change in selection when the user has finally made his
    // selection !

    if (e.getValueIsAdjusting()) {
      return;
    }

    TopicListItem selectedItem = (TopicListItem) typeList.getSelectedValue();
    if (selectedItem == null) {
      selectedType = null;
      return;
    }

    selectedType = selectedItem.getTopic();
    
    VizTopicMapConfigurationManager confMan = controller
        .getConfigurationManager();

    defaultColorSettingCheckbox.update();
    
    // Make the colour chooser show the appropriate selected colour.
    setChooserColor();
    
    // Show the selected shape, or no selection when using default type settings
    if (getUsingTopicModel() ? 
        confMan.getOccurrenceValue(selectedType, 
            confMan.getTopicTypeShapeTopic()) == null : 
        confMan.getOccurrenceValue(selectedType, 
            confMan.getAssociationTypeShapeTopic()) == null) {
      unselectShapeRadioButtons();
    } else {
      setSelectedShape(model.getShape(controller, selectedType));
    }

    // Show the filter selection.
    setSelectedFilter(controller.getTypeVisibility(selectedType));

    // Default selection is not applicable to default type.
    if (getUsingTopicModel() ? selectedType == confMan.defaultType
                         : selectedType == confMan.defaultAssociationType) {
      filterDefault.setEnabled(false);
    } else {
      filterDefault.setEnabled(true);
    }
    
    // Default button is not applicable to default type.
    defaultButton.setEnabled(!(selectedType == confMan.defaultType
        || selectedType == confMan.defaultAssociationType));
    
    // WORKAROUND (HORRIBLE HACK): When the slider is updated, the underlying
    // value is automatically changed. This corrupts the behavior of
    // Default Type, which should apply to all types with no explicit setting.
    // Hence the need to check if it was already set and then remove it after
    // setSelectedWeight has changed it.
    TopicIF paddingTopic = confMan.getTopicTypeShapePaddingTopic();
    TopicIF configType = confMan.getConfigTopic(selectedType);
    boolean hadShapePadding = confMan.getOccurrence(configType, paddingTopic)
        != null;
    setSelectedWeight(model.getWeight(controller, selectedType));
    if (!hadShapePadding) {
      confMan.removeOccurrence(configType, paddingTopic);
    }
    
    setSelectedIconFilename(controller.getTypeIconFilename(selectedType));
    
    // Make the appropriate font selection (or no selection, as the case may be)
    // in the font selection interface.
    updateFontSelection();
  }
  
  /**
   * Make the appropriate font selection (or no selection, as the case may be)
   * in the font selection interface.
   */
  private void updateFontSelection() {
    VizTopicMapConfigurationManager confMan = controller
        .getConfigurationManager();
    
    String fontString = confMan.getOccurrenceValue(selectedType,
        confMan.getTopicTypeFontTopic());
    if (fontString == null) {
      unselectFont();
    } else {
      Font font = confMan.parseFont(fontString);
      setSelectedFont(font);
    }
  }
  
  private void setChooserColor() {
    // Needed by the config manager to avoid updating view with new color.
    controller.setIgnoreStateChangedEvent(true);

    Color c = model.getColor(controller, selectedType);
    if (c != null) {
      chooser.setColor(c);
    }

    // Needed by the config manager to avoid updating view with new color.
    controller.setIgnoreStateChangedEvent(false);
  }

  private void setSelectedFont(Font typeFont) {
    //  We need @ignoreSelection since we cannot set the current
    // font without signalling the font changes callbacks
    ignoreSelection = true;
    fontList.setSelectedValue(typeFont.getFamily(), true);
    sizeList.setSelectedValue(Integer.toString(typeFont.getSize()), true);
    bold.setSelected(typeFont.isBold());
    italic.setSelected(typeFont.isItalic());
    ignoreSelection = false;
  }

  // Remove all font selection (glyph, size, ?bold and ?italics) from the font
  // selection interface. This will typically happen for fonts that use default
  // font settings.
  private void unselectFont() {
    //  We need @ignoreSelection since we cannot set the current
    // font without signalling the font changes callbacks
    ignoreSelection = true;
    int[] noInts = new int[0];
    fontList.setSelectedIndices(noInts);
    sizeList.setSelectedIndices(noInts);
    bold.setSelected(false);
    italic.setSelected(false);
    ignoreSelection = false;
  }

  /**
   * INTERNAL: Model object to represent what is being configured.
   */
  private interface ConfigurationModelIF {

    void addAdditionalItems(List ttypes);

    void buildGeneralConfigPanel(TypesConfigFrame frame, JPanel config);

    int getWeight(VizController controller, TopicIF selectedType);

    Collection getListItems(VizController controller);

    int getShape(VizController controller, TopicIF selectedType);

    String getTitle();

    void setWeight(VizController controller, TopicIF selectedType,
                   int i);

    void setFilter(VizController controller, TopicIF selectedType, int i);

    void setShape(VizController controller, TopicIF selectedType, int i);

    Color getColor(VizController controller, TopicIF selectedType);

  }
  
  /**
   * INTERNAL: PRIVATE: Description: Model object to represent TopicTypes
   * Examples:
   */
  private class AssociationTypeConfigurationModel implements
      ConfigurationModelIF {

    @Override
    public void addAdditionalItems(List ttypes) {
      // For association types do nothing
    }

    @Override
    public void buildGeneralConfigPanel(TypesConfigFrame frame, JPanel config) {
      frame.buildAssociationTypeGeneralConfigPanel(config);
    }

    @Override
    public Color getColor(VizController controller, TopicIF selectedType) {
      return controller.getAssociationTypeColor(selectedType);
    }

    @Override
    public int getWeight(VizController controller, TopicIF selectedType) {
      return controller.getAssoicationTypeLineWeight(selectedType);
    }

    @Override
    public Collection getListItems(VizController controller) {
      return controller.getAssociationTypes();
    }

    @Override
    public int getShape(VizController controller, TopicIF selectedType) {
      return controller.getAssoicationTypeShape(selectedType);
    }

    @Override
    public String getTitle() {
      return Messages.getString("Viz.AssociationTypeConfiguration");
    }

    @Override
    public void setWeight(VizController controller, TopicIF selectedType,
        int i) {
      controller.setAssociationTypeLineWeight(selectedType, i);
    }

    @Override
    public void setFilter(VizController controller, TopicIF selectedType,
        int i) {
      controller.setAssociationTypeVisibility(selectedType, i);
    }

    @Override
    public void setShape(VizController controller, TopicIF selectedType,
        int i) {
      controller.setAssociationTypeShape(selectedType, i);
    }
  }

  /**
   * INTERNAL: PRIVATE: Description: Model object to represent TopicTypes 
   * Examples:
   */
  private class TopicTypeConfigurationModel implements ConfigurationModelIF {

    @Override
    public void addAdditionalItems(List ttypes) {
      ttypes.add(new TopicListItem(Messages.getString("Viz.Untyped"))); 
     }

    @Override
    public void buildGeneralConfigPanel(TypesConfigFrame frame, JPanel config) {
      frame.buildTopicTypeGeneralConfigPanel(config);
    }

    @Override
    public Color getColor(VizController controller, TopicIF selectedType) {
      return controller.getTopicTypeColor(selectedType);
    }

    @Override
    public int getWeight(VizController controller, TopicIF selectedType) {
      return controller.getTopicTypeShapePadding(selectedType);
    }

    @Override
    public Collection getListItems(VizController controller) {
      return controller.getAllTopicTypesWithNull();
    }

    @Override
    public int getShape(VizController controller, TopicIF selectedType) {
      return controller.getTopicTypeShape(selectedType);
    }

    @Override
    public String getTitle() {
      return Messages.getString("Viz.TopicTypeConfiguration");
    }

    @Override
    public void setWeight(VizController controller, TopicIF selectedType,
                          int i) {
      controller.setTopicTypeShapePadding(selectedType, i);
    }

    @Override
    public void setFilter(VizController controller, TopicIF selectedType,
                         int i) {
      controller.setTopicTypeVisibility(selectedType, i);
    }

    @Override
    public void setShape(VizController controller, TopicIF selectedType,
                         int i) {
      controller.setTopicTypeShape(selectedType, i);
    }
  }
}
