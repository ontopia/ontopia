// $Id: IncludeTopicMapDTDEntityResolver.java,v 1.6 2008/01/09 10:07:35 geir.gronmo Exp $

package net.ontopia.topicmaps.xml;

import java.io.StringReader;
import net.ontopia.xml.*;
import org.xml.sax.InputSource;

/**
 * INTERNAL: SAX entity resolver that makes sure that doctype
 * declarations referencing the XTM 1.0 DTD using public ids are given
 * the correct DTD.</p>
 *
 * The resolver returns an input source refering to the correct DTDs
 * for each entity with the public ids:</p>
 *
 * <pre>
 *   "-//TopicMaps.Org//DTD XML Topic Map (XTM) 1.0//EN"
 *   "+//IDN ontopia.net//DTD Topic Map Interchange Format (Strict 1.0)//EN"
 * </pre>
 */

public class IncludeTopicMapDTDEntityResolver extends ConfigurableEntityResolver {

  public IncludeTopicMapDTDEntityResolver() {
    InputSourceFactoryIF xtm_factory = new InputSourceFactoryIF() {
        public org.xml.sax.InputSource createInputSource() {
          return new InputSource(new StringReader(DTD.getXTMDocumentType()));
        }
      };
    addPublicIdSource("-//TopicMaps.Org//DTD XML Topic Map (XTM) 1.0//EN", xtm_factory);
  }
    
}




