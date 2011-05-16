
import java.io.File;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;

public class Hello2 {

  public static void main(String[] args) {
    try {
      XTMTopicMapReader reader = new XTMTopicMapReader(new File(args[0]));
      TopicMapIF tm = reader.read();
      System.out.println("Hello, world!");
      System.out.println("I have a topic map with " + tm.getTopics().size() + 
      			 " topics!");
    }
    catch (java.io.IOException e) {
      System.err.println("Error reading topic map: " + e);
    }
  }

}
