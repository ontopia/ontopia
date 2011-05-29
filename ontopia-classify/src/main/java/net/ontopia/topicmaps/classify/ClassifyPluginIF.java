
package net.ontopia.topicmaps.classify;

import java.io.InputStream;
import java.util.Map;
import javax.servlet.http.*;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;

/**
 * INTERNAL: Interface implemented by code that is able to locate
 * classifiable content for topics.
 */
public interface ClassifyPluginIF {

  /**
   * INTERNAL: Returns true if the plug-in is able to locate
   * classifiable content for the given topic.
   */
  public boolean isClassifiable(TopicIF topic);

  /**
   * INTERNAL: Returns the classifiable content for the given topic.
   */
  public ClassifiableContentIF getClassifiableContent(TopicIF topic);
  
}
