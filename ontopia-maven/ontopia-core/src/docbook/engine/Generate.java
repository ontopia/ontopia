
import java.io.File;
import java.io.IOException;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.xml.XTMTopicMapWriter;

public class Generate {

  public static void main(String[] args) throws IOException {
    TopicMapStoreIF store = new InMemoryTopicMapStore();
    TopicMapIF topicmap = store.getTopicMap();

    TopicMapBuilderIF builder = topicmap.getBuilder();
    for (int ix = 1; ix < 11; ix++) {
      TopicIF topic = builder.makeTopic(); // adds topic to topic map
      builder.makeTopicName(topic, Integer.toString(ix));
      // builder adds base name to topic and sets name string
    }

    // having created the topic map we are now ready to export it
    new XTMTopicMapWriter("numbers.xtm").write(topicmap);
  }
    
}
