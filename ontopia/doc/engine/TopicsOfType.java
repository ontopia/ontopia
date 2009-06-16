
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Collection;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;

public class TopicsOfType {

  public static void main(String[] args) {
    TopicsOfType self = new TopicsOfType();
    try {
      self.printTopicsOfType(new File(args[0]), args[1]);
    }
    catch (IOException e) {
      System.err.println("ERROR reading topic map: " + e);
    }
  }

  public void printTopicsOfType(File tmfile, String typeId)
    throws IOException {
    XTMTopicMapReader reader = new XTMTopicMapReader(tmfile);
    TopicMapIF tm = reader.read();

    TopicIF topicType = getTopicByXMLId(tm, typeId);
    if (topicType == null) {
      System.err.println("ERROR: No such topic: " + typeId);
      return;
    }

    Iterator it = tm.getTopics().iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();
      Collection types = topic.getTypes();
      if (types.contains(topicType))
				System.out.println(topic);
    }
  }

  private TopicIF getTopicByXMLId(TopicMapIF tm, String id) {
    TopicMapStoreIF store = tm.getStore();
    LocatorIF loc = store.getBaseAddress().resolveAbsolute("#" + id);
    return (TopicIF) tm.getObjectByItemIdentifier(loc);
  }
    
}
