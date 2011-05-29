
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
 * INTERNAL: Interface that holds the identifier and the actual
 * content of a classifiable resource.
 */
public interface ClassifiableContentIF {

  /**
   * INTERNAL: Returns an identifier that identifies the classifiable
   * content. This could e.g. be the absolute filename or an URI of
   * the resource.
   */
  public String getIdentifier();

  /**
   * INTERNAL: Returns the actual bytes in the content of the
   * classiable content.
   */
  public byte[] getContent();
  
}
