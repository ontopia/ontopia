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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapSource;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.SimpleFileFilter;

/**
 * INTERNAL: A General Configuration frame for the VizDesktop.
 */
public class OpenRDBMSDialogBox extends JFrame {
  private VizDesktop desktop;
  private TopicMapReferenceIF tmReference;
  private Vector topicMaps;
  private Map idToTMReference;
  private JComboBox topicMapChooser;
  private JTextField propertiesFileField;
  private JTextField configurationFileField;
  private String oldPropertiesFilePath;
    
  public OpenRDBMSDialogBox(VizDesktop desktop) {
    super(Messages.getString("Viz.GeneralConfigWindowTitle"));

    this.desktop = desktop;
    tmReference = null;
    topicMaps = new Vector();
    idToTMReference = new HashMap();
    topicMapChooser = new JComboBox(topicMaps);
    propertiesFileField = new JTextField(30);
    configurationFileField = new JTextField(30);
    oldPropertiesFilePath = null;
    build();
  }

  private void build() {
    getContentPane().add(createOpenRDBMSPanel());
    pack();
    setResizable(false);
  }
    
  private JPanel createOpenRDBMSPanel() {
    // Create the main panel to hold all the other components.
    JPanel mainPanel = new JPanel();
    // Lay out the main components vertically.
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory
        .createEtchedBorder(), Messages.getString("Viz.OpenRDBMSBoxTitle")));

    // Create a panel for specifying the properties file.
    JPanel propertiesFilePanel = new JPanel();
    propertiesFilePanel.setLayout(new BoxLayout(propertiesFilePanel,
                                                BoxLayout.X_AXIS));
      
    // Label the properties file field (which will follow).
    JLabel propertiesFileLabel = new JLabel(Messages
        .getString("Viz.PropertiesFileLabel"));
    propertiesFilePanel.add(propertiesFileLabel);

    // Create a listener that will listen for and act upon changes to the
    // file chooser button.
    CaretListener propertiesFileTextFieldListener = new CaretListener() {
        @Override
        public void caretUpdate(CaretEvent e) {
          String text = propertiesFileField.getText();
          
          // Get the properties file..
          File propertiesFile = new File(text);

          // Read the properties file and update the topic maps accordingly.
          setPropertiesFile(propertiesFile);
        }
      };
      
    // Create a text box for displaying the name of the properties file.
    propertiesFileField.setEditable(true);
    propertiesFileField.addCaretListener(propertiesFileTextFieldListener);
      
    propertiesFilePanel.add(propertiesFileField);
      
    // Create a listener that will listen for and act upon changes to the
    // file chooser button.
    ActionListener propertiesFileListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent action) {
          File newPropertiesFile;
          SimpleFileFilter filter = new SimpleFileFilter(Messages
              .getString("Viz.FileFilter_props"), "props");
          
          String dir = desktop.getCurrentRDBMSDir();
          newPropertiesFile = browseForFile(dir, filter);
          
          if (newPropertiesFile != null) {
            // Remember the property file directory for the future.
            desktop.setCurrentRDBMSDir(newPropertiesFile.getParent());

            // Set this to be the selected properties file.
            // This will also trigger file update (see CaretListener)
            propertiesFileField.setText(newPropertiesFile.getAbsolutePath());
          }
        }
      };
      
    // Create a button for browsing to the properties file.
    JButton propertiesFileBrowseButton = new JButton(Messages
        .getString("Viz.IconBrowseButton"));
    propertiesFileBrowseButton.addActionListener(propertiesFileListener);
    propertiesFilePanel.add(propertiesFileBrowseButton);

    mainPanel.add(propertiesFilePanel);

    // Create a listener that will listen for and act upon changes to the
    // topic map choice.
    ActionListener topicMapChooserListener = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent action) {
        tmReference = (TopicMapReferenceIF)idToTMReference.get(topicMapChooser
            .getSelectedItem());
      }
    };
      
    // Create a panel for selecting the topic map.
    JPanel chooserPanel = new JPanel();
    chooserPanel.setLayout(new BoxLayout(chooserPanel,
                                         BoxLayout.X_AXIS));

    // Label the properties file field (which will follow).
    JLabel chooserLabel = new JLabel(Messages.getString("Viz.TopicMap"));
    chooserPanel.add(chooserLabel);

    // Let the user choose a topic map from a list
    topicMapChooser.setEnabled(false);
    topicMapChooser.addActionListener(topicMapChooserListener);
    chooserPanel.add(topicMapChooser);
      
    mainPanel.add(chooserPanel);
      
    // Create a panel for specifying the configuration file.
    JPanel configurationFilePanel = new JPanel();
    configurationFilePanel.setLayout(new BoxLayout(configurationFilePanel,
                                                   BoxLayout.X_AXIS));
      
    // Label the properties file field (which will follow).
    JLabel configurationFileLabel = new JLabel(Messages
        .getString("Viz.ConfigurationFileLabel"));
    configurationFilePanel.add(configurationFileLabel);
      
    // Create a text field for specifying the name of the configuration file.
    configurationFileField = new JTextField(30);

    configurationFilePanel.add(configurationFileField);
      
    // Create a listener that will listen for and act upon changes to the
    // configuration file chooser button.
    ActionListener configurationFileListener = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent action) {
        File newConfigurationFile;
        SimpleFileFilter filter = new SimpleFileFilter(Messages
            .getString("Viz.FileFilter_viz"), "viz");
        
        String dir = desktop.getCurrentConfigDir();
        newConfigurationFile = browseForFile(dir, filter);

        if (newConfigurationFile != null) {
          desktop.setCurrentConfigDir(newConfigurationFile.getParent());

          // Set this to be the selected configuration file.
          configurationFileField.setText(newConfigurationFile
                                         .getAbsolutePath());
          repaint();
        }
      }
    };
      
    // Create a button for browsing to the configuration file.
    JButton configurationFileBrowseButton = new JButton(Messages
        .getString("Viz.IconBrowseButton"));
    configurationFileBrowseButton
      .addActionListener(configurationFileListener);
    configurationFilePanel.add(configurationFileBrowseButton);

    mainPanel.add(configurationFilePanel);
      
    // Create a panel for the ok and cancel buttons.
    JPanel confirmationPanel = new JPanel();
    confirmationPanel.setLayout(new BoxLayout(confirmationPanel,
                                              BoxLayout.X_AXIS));
      
    // Create a listener that will listen for and act upon changes to the
    // Cancel button.
    ActionListener cancelListener = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent action) {
        hide();
      }
    };    
      
    // Create a Cancel button.
    JButton cancelButton = new JButton(Messages.getString("Viz.Cancel"));
    cancelButton.addActionListener(cancelListener);
    confirmationPanel.add(cancelButton);

    // Create a listener that will listen for and act upon changes to the
    // OK button.
    ActionListener okListener = new OpenRDBMSTopicMapListener();
      
    // Create an Ok button.
    JButton okButton = new JButton(Messages.getString("Viz.OK"));
    okButton.addActionListener(okListener);
    confirmationPanel.add(okButton);
      
    mainPanel.add(confirmationPanel);
      
    return mainPanel;
  }

  private void setPropertiesFile(File newPropertiesFile) {
    // Get all the topic maps from the database.
    topicMaps.clear();
    idToTMReference.clear();
    tmReference = null;
    topicMapChooser.setSelectedIndex(-1);
      
    if (!newPropertiesFile.exists()) {
      topicMapChooser.setEnabled(false);
      return;
    }

    if (oldPropertiesFilePath != null && !oldPropertiesFilePath
        .equals(newPropertiesFile.getAbsolutePath())) {
      oldPropertiesFilePath = null;
    }
        
    // Get the properties file.
    Collection propertiesReferences;
    try {
      propertiesReferences = new RDBMSTopicMapSource(newPropertiesFile
          .getAbsolutePath()).getReferences();
    } catch (OntopiaRuntimeException e) {
      if (e.getCause() instanceof FileNotFoundException) {
        topicMapChooser.setEnabled(false);
        return;
      }
        
      if (e.getCause() instanceof IllegalArgumentException) {
        if (oldPropertiesFilePath != null) {
          return;
        }
          
        oldPropertiesFilePath = newPropertiesFile.getAbsolutePath();
        WarningBox warningBox = new WarningBox(Messages
            .getString("Viz.WarningInvalidPropertiesFile"));
        warningBox.setVisible(true);
          
        topicMapChooser.setEnabled(false);
        return;
      }

      if (e.getCause().getMessage().startsWith("Connection refused")) {
        WarningBox warningBox = new WarningBox(Messages
            .getString("Viz.WarningConnectException"));
        warningBox.setVisible(true);
          
        topicMapChooser.setEnabled(false);
        return;
      }

      throw (e);
    }
      
    // Read the RDBMS properties file.
    Iterator tmReferenceIt = propertiesReferences.iterator();
    while (tmReferenceIt.hasNext()) {
      TopicMapReferenceIF currentReference
        = (TopicMapReferenceIF)tmReferenceIt.next();
        
      String tmString = createTMString(currentReference);
        
      topicMaps.add(tmString);
      idToTMReference.put(tmString, currentReference);
    }
    if (topicMapChooser.getItemCount() != 0) {
      topicMapChooser.setSelectedIndex(0);
      tmReference = (TopicMapReferenceIF)idToTMReference.get(topicMapChooser
          .getSelectedItem());
    }
      
    topicMapChooser.setEnabled(true);
      
    repaint();
  }
    
  private File browseForFile(String currentPath, SimpleFileFilter filter) {
    JFileChooser fc = new JFileChooser(currentPath);

    fc.addChoosableFileFilter(filter);
    fc.setFileFilter(filter);

    int returnVal = fc.showOpenDialog(this);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      return fc.getSelectedFile();
    }
      
    return null;
  }
    
  public static String createTMString(TopicMapReferenceIF currentReference) {
    return currentReference.getId() + " # " + currentReference.getSource();
  }
    
  /**
   * Generates a box with a warning message and an Ok button.
   */
  private class WarningBox extends JFrame {
    public WarningBox(String warning) {
      // Create the warning panel to hold all the other components.
      JPanel warningPanel = new JPanel();
      // Lay out the components vertically.
      warningPanel.setLayout(new BoxLayout(warningPanel, BoxLayout.Y_AXIS));
      warningPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory
          .createEtchedBorder(), Messages.getString("Viz.Warning")));

      // Create the warning message.
      JTextArea warningTextArea = new JTextArea(warning);
      warningTextArea.setLineWrap(true);
      warningTextArea.setWrapStyleWord(true);
      warningTextArea.setEditable(false);
      warningTextArea.setFont(warningTextArea.getFont().deriveFont(13f));
      warningTextArea.setBackground(warningPanel.getBackground());
      warningPanel.add(warningTextArea);
        
      // Will listen for and act upon changes to the OK button.
      ActionListener okListener = new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent action) {
            setVisible(false);
          }
        };
        
      // Create an Ok button.
      JButton okButton = new JButton(Messages.getString("Viz.OK"));
      okButton.addActionListener(okListener);
      warningPanel.add(okButton);
        
      getContentPane().add(warningPanel);
      setSize(400, 150);
    }      
  }

  /**
   * This listener receives the action event when the "OK" button is
   * clicked.
   */
  class OpenRDBMSTopicMapListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent action) {
      try {
        _actionPerformed(action);
      } catch (Exception e) {
        ErrorDialog.showError(desktop.getVpanel(), Messages
            .getString("Viz.TMLoadError"), e);
      }
    }

    private void _actionPerformed(ActionEvent action) throws IOException {
      String filename = configurationFileField.getText();
      File conffile = null;
      if (filename != null && filename.length() > 0) {
        conffile = new File(filename);
      }
      
      if (!topicMapChooser.isEnabled()) {
        String propertiesText = propertiesFileField.getText();
        File propertiesFile = new File(propertiesText);
        
        if (propertiesFile.isDirectory()) {
          warn("Viz.WarningPropertiesFileIsDirectory");
        } else if (!propertiesFile.isFile()) {
          warn("Viz.WarningWrongPathPropertiesFile");
        } else {
          warn("Viz.InvalidPropertiesFile");
        }
      } if (tmReference == null) {
        warn("Viz.MissingTopicMapReference");
      } else if (conffile != null && conffile.isDirectory()) {
        warn("Viz.WarningConfigFileIsDirectory");
      } else if (conffile != null && !conffile.isFile()) {
        warn("Viz.WarningWrongPathConfigFile");
      } else {
        hide();
        desktop.loadTopicMap(tmReference, filename);
      }
    }    
  }

  private void warn(String property) {
    (new WarningBox(Messages.getString(property))).setVisible(true);
  }
}
