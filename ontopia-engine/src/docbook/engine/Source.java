
import java.util.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.entry.*;
import net.ontopia.topicmaps.xml.*;

public class Source {
  public void main(String[] args) throws java.io.IOException {
    // Create and configure the source
    XTMPathTopicMapSource source = new XTMPathTopicMapSource();
    source.setPath("/ontopia/topicmaps");
    source.setSuffix(".xtm");

    // Retrieve all the references
    Collection refs = source.getReferences();
    System.out.println(refs.size() + " topicmap references found.");

    // Resolve the first reference
    TopicMapReferenceIF ref = (TopicMapReferenceIF) refs.iterator().next();

    // Get the topic map
    TopicMapIF topicmap = ref.createStore(false).getTopicMap();
  }
}
