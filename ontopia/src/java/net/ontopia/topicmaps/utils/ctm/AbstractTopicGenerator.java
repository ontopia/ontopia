
package net.ontopia.topicmaps.utils.ctm;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.LocatorIF;

/**
 * Abstract topic generator.
 */
public abstract class AbstractTopicGenerator implements ValueGeneratorIF {

  public boolean isTopic() {
    return true;
  }
  
  public String getLiteral() {
    throw new OntopiaRuntimeException("FIXME");
  }
  
  public LocatorIF getDatatype() {
    throw new OntopiaRuntimeException("FIXME");
  }

  public LocatorIF getLocator() {
    throw new OntopiaRuntimeException("FIXME");
  }  

}
