import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.utils.StringifierIF;

public class TopicsOfType2 {

  public static void main(String[] args) {
    TopicsOfType2 self = new TopicsOfType2();
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

    StringifierIF stringifier = TopicStringifiers.getDefaultStringifier();
    ClassInstanceIndexIF index = (ClassInstanceIndexIF) tm.getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");
    
    Iterator it = index.getTopics(topicType).iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();
      System.out.println(stringifier.toString(topic));
    }
  }

  private TopicIF getTopicByXMLId(TopicMapIF tm, String id) {
    TopicMapStoreIF store = tm.getStore();
    LocatorIF loc = store.getBaseAddress().resolveAbsolute("#" + id);
    return (TopicIF) tm.getObjectByItemIdentifier(loc);
  }

}
