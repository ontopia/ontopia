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

import javax.swing.JFrame;

import net.ontopia.topicmaps.core.TopicIF;


/**
 * INTERNAL:
 * PRIVATE:
 * 
 * Description: A properties frame for topics
 */
public class PropertiesFrame extends JFrame {
  private PropertiesPanel propertiesPanel;
  private VizController controller;
  
  public PropertiesFrame(VizController aController) {
    super();
    controller = aController;
    propertiesPanel = new PropertiesPanel(aController);
    this.getContentPane().add(propertiesPanel);
  }
  
  public void setTarget(TopicIF topic) {
   this.setTitle(Messages.getString("Viz.PropertiesTitle") + controller
       .getStringifier().apply(topic)); 
   propertiesPanel.setTarget(topic);
   this.resize();
  }

  private void resize() {
    // Calling pack() resizes the panel to fit the current contents, however
    // a minimum size is necessary in some cases
    this.pack();
    this.setSize(Math.max(this.getWidth(), 500),
                 Math.max(this.getHeight(), 500));
  }
}
