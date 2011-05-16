
import java.io.File;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;

public class Hello {

  public static void main(String[] args) {
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    TopicMapIF tm = store.getTopicMap();
    System.out.println("Hello, world!");
    System.out.println("I have a topic map with " + tm.getTopics().size() + 
                       " topics!");
  }

}
