
// $Id: TologRulePlugin.java,v 1.4 2007/04/30 09:16:09 grove Exp $

package net.ontopia.topicmaps.classify;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.net.URL;
import javax.servlet.http.*;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: 
 */
public class TologRulePlugin implements ClassifyPluginIF {

  public boolean isClassifiable(TopicIF topic) {
    if (topic == null) return false;
    TopicMapIF tm = topic.getTopicMap();
    if (tm == null) return false;
    QueryProcessorIF qp = QueryUtils.getQueryProcessor(tm);
    try {
      QueryResultIF qr = qp.execute("import \"classify.tl\" as classify classify:is-classifiable(%topic%)?", Collections.singletonMap("topic", topic));
      try {
        return qr.next();
      } finally {
        qr.close();
      }    
    } catch (Throwable e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  public ClassifiableContentIF getClassifiableContent(TopicIF topic) {
    if (topic == null) return null;
    TopicMapIF tm = topic.getTopicMap();
    if (tm == null) return null;
    QueryProcessorIF qp = QueryUtils.getQueryProcessor(tm);
    try {
      QueryResultIF qr = qp.execute("import \"classify.tl\" as classify cl:get-classifiable-content(%topic%, $DOCURI)?", Collections.singletonMap("topic", topic));
      try {
        if (qr.next()) {
          String identifier = (String)qr.getValue(0);
          return ClassifyUtils.getClassifiableContent(identifier);
        } else {
          return null;
        }
      } finally {
        qr.close();
      }
    } catch (Throwable e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
}
