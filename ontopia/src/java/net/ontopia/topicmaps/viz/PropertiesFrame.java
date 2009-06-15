
//$Id: PropertiesFrame.java,v 1.8 2007/05/02 15:15:13 eirik.opland Exp $

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
       .getStringifier().toString(topic)); 
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
