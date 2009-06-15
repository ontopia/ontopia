/*
 * 1.0 code.
 */

import java.applet.Applet;
import java.awt.Graphics;

import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.topicmaps.impl.basic.*;


public class TestApplet extends Applet {

  StringBuffer buffer;
  public void init() {
    buffer = new StringBuffer();
    addItem("initializing... ");
  }

  public void start() {
    addItem("starting... ");

    System.out.println("Starting...");

    try {
      // Initialize logging
      CmdlineUtils.initializeLogging();
      
      TopicMapStoreIF store = new InMemoryTopicMapStore();
      TopicMapIF tm = store.getRootTransaction().getTopicMap();
    
      TopicMapImporterIF reader = new ISO13250TopicMapReader(getParameter("tmfile"));
      reader.importInto(tm);
      System.out.println("Imported.");
      
      store.commit();
      System.out.println("Done.");
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("error.");
    }
  }

  public void stop() {
    addItem("stopping... ");
  }

  public void destroy() {
    addItem("preparing for unloading...");
  }

  void addItem(String newWord) {
    System.out.println(newWord);
    buffer.append(newWord);
    repaint();
  }

  public void paint(Graphics g) {
    //Draw a Rectangle around the applet's display area.
    g.drawRect(0, 0, size().width - 1, size().height - 1); //Draw the current string inside the rectangle.
    g.drawString(buffer.toString(), 5, 15);
  }
}
