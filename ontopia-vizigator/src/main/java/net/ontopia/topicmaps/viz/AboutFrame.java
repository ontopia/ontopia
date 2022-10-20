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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.ontopia.Ontopia;
import org.apache.commons.io.IOUtils;

public class AboutFrame extends JDialog {
  public AboutFrame(Frame parent) {
    super(parent, Messages.getString("Viz.About", "Ontopia Vizigator"), true);

    JPanel mainPanel = new JPanel();
    mainPanel.setBackground(Color.white);
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    mainPanel.add(createImagePanel());
    mainPanel.add(createAboutTextPanel());

    this.getContentPane().add(mainPanel);
    this.pack();
    this.setResizable(false);
    //    Center the dialog box above its parent
    this.setLocation(
        (parent.getX() + (parent.getWidth() - this.getWidth()) / 2), parent
            .getY()
            + (parent.getHeight() - this.getHeight()) / 2);
  }

  private Component createImagePanel() {
    Box main = new Box(BoxLayout.X_AXIS);
    Icon aboutImage = this.getAboutImage();
    JLabel imageLabel;
    if(aboutImage == null) {
      imageLabel = new JLabel("Ontopia AS - The TopicMap People");
    } else {
      imageLabel = new JLabel(aboutImage);
    }
    main.add(imageLabel);
    return main;
  }

  private Icon getAboutImage() {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    InputStream input = ClassLoader
        .getSystemResourceAsStream("net/ontopia/topicmaps/viz/logo.gif");

    if (input == null) {
      return null;
    }
    
    try {
      IOUtils.copy(input, output);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }

    return new ImageIcon(output.toByteArray());
  }

  private JEditorPane createAboutTextPanel() {

    JEditorPane about = new JEditorPane(
        "text/html",
        "<html>"
            + "<body><center>"
            + "<h1>Vizigator&#8482;: VizDesktop&#8482;</h1>"
            + "<h3>Version: "
            + Ontopia.getInfo()
            + "</h3>"
            + "<p>Topic Map visualization and configuration tool based on the graphic visualization product, TouchGraph </p>"
            + "<p>Copyright &#0169; 2004-2007 Ontopia AS</p>"
            + "<p>The Ontopians wish you Happy Vizigating</p><br></center></body></html>");

    about.setEditable(false);
    return about;
  }
}
