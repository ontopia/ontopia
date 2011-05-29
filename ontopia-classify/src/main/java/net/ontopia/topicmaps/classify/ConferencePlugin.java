
package net.ontopia.topicmaps.classify;

import java.io.*;
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
public class ConferencePlugin implements ClassifyPluginIF, HttpServletRequestAwareIF {

  private HttpServletRequest request;
  
  public void setRequest(HttpServletRequest request) {
    this.request = request;
  }
  
  public boolean isClassifiable(TopicIF topic) {
    if (topic == null) return false;
    TopicMapIF tm = topic.getTopicMap();
    if (tm == null) return false;
    QueryProcessorIF qp = QueryUtils.getQueryProcessor(tm);
    try {
      QueryResultIF qr = qp.execute("subject-identifier($PAPERTYPE, \"http://psi.example.org/paper\"), instance-of(%topic%, $PAPERTYPE)?", Collections.singletonMap("topic", topic));
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
      QueryResultIF qr = qp.execute("import \"http://psi.ontopia.net/tolog/string/\" as str subject-identifier(%topic%, $SUBIND), str:starts-with($SUBIND, \"http://psi.example.org/paper/\")?", Collections.singletonMap("topic", topic));
      try {
        if (qr.next()) {
          String _identifier = (String)qr.getValue(0);
          String identifier = _identifier.substring("http://psi.example.org/paper/".length());
          
          String path = getResolvedPaperPath();
          String infile = path + File.separator + identifier.toLowerCase() + ".xml";
          // read the content into a byte array
          ClassifiableContentIF cc = ClassifyUtils.getClassifiableContent(infile);
          if (cc != null) return cc;
        }
        return null;
      } finally {
        qr.close();
      }
    } catch (Throwable e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  protected String getResolvedPaperPath() {
    try {
      return new File(request.getRealPath("/") + "../conference/WEB-INF/papers/").getCanonicalPath();
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
}
