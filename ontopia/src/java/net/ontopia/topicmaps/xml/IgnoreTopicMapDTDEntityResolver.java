
// $Id: IgnoreTopicMapDTDEntityResolver.java,v 1.9 2004/11/18 13:12:29 grove Exp $

package net.ontopia.topicmaps.xml;

import net.ontopia.xml.*;
import org.xml.sax.InputSource;

/**
 * INTERNAL: SAX entity resolver that makes sure that doctype
 * declarations referencing the ISO 13250 and XTM 1.0 DTDs using
 * public ids are ignored.</p>
 *
 * The resolver returns an empty input source for each entity with the
 * public ids:</p>
 *
 * <pre>
 *   "-//TopicMaps.Org//DTD XML Topic Map (XTM) 1.0//EN"
 *   "+//IDN ontopia.net//DTD Topic Map Interchange Format (Strict 1.0)//EN"
 * </pre>
 *
 * An empty input source is also used if the system_id ends with '.dtd'.</p>
 */
public class IgnoreTopicMapDTDEntityResolver extends ConfigurableEntityResolver {

  protected InputSourceFactoryIF factory;

  public IgnoreTopicMapDTDEntityResolver() {
    this(new EmptyInputSourceFactory());
  }
  
  public IgnoreTopicMapDTDEntityResolver(InputSourceFactoryIF factory) {
    this.factory = factory;
    addPublicIdSource("-//TopicMaps.Org//DTD XML Topic Map (XTM) 1.0//EN", factory);
    addPublicIdSource("+//IDN ontopia.net//DTD Topic Map Interchange Format (Strict 1.0)//EN", factory);    
  }

  public InputSource resolveEntity (String public_id, String system_id) {
    if (system_id != null && system_id.endsWith(".dtd"))
      return factory.createInputSource();
    return super.resolveEntity(public_id, system_id);
  }
  
}
