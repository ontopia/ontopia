
// $Id: RDBMSTopicMapReader.java,v 1.12 2004/11/12 09:41:48 grove Exp $

package net.ontopia.topicmaps.impl.rdbms;

import java.util.*;
import net.ontopia.topicmaps.core.*;

/**
 * INTERNAL: Topic map reader that reads topic maps from the RDBMS
 * backend connector.
 *
 * @since: 1.2.5
 */

public class RDBMSTopicMapReader implements TopicMapReaderIF {

  protected String propfile;
  protected Map properties;
  protected long topicmap_id;

  public RDBMSTopicMapReader(long topicmap_id) {
    this.topicmap_id = topicmap_id;
  }

  public RDBMSTopicMapReader(String propfile, long topicmap_id) {
    this.propfile = propfile;
    this.topicmap_id = topicmap_id;
  }
        
  public RDBMSTopicMapReader(Map properties, long topicmap_id) {
    this.properties = properties;
    this.topicmap_id = topicmap_id;
  }
  
  public TopicMapIF read() throws java.io.IOException {
    if (properties != null)
      return new RDBMSTopicMapStore(properties, topicmap_id).getTopicMap();
    else if (propfile != null)
      return new RDBMSTopicMapStore(propfile, topicmap_id).getTopicMap();
    else
      return new RDBMSTopicMapStore(topicmap_id).getTopicMap();
  }

  public Collection readAll() throws java.io.IOException {
    return Collections.singleton(read());
  }
        
}
