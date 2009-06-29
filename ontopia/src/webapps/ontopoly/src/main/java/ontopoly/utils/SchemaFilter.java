
// $Id: SchemaFilter.java,v 1.5 2008/12/04 11:29:39 lars.garshol Exp $

package ontopoly.utils;

import java.util.Iterator;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.PSI;
import net.ontopia.utils.DeciderIF;

/**
 * INTERNAL: Filters out Ontopoly constructs from topic maps.
 */
public class SchemaFilter implements DeciderIF {
  
  public boolean ok(Object object) {
    boolean include = true;
    if (object instanceof TopicIF) {
      TopicIF topic = (TopicIF)object;
      
      // check subject identifiers first
      Iterator it = topic.getSubjectIdentifiers().iterator();
      while (it.hasNext()) {
        LocatorIF psi = (LocatorIF) it.next();

        // There are some exceptions
        if (PSI.ON_DESCRIPTION.equals(psi) ||
            PSI.ON_CREATOR.equals(psi) ||
            PSI.ON_VERSION.equals(psi) ||
            PSI.ON_SUPERCLASS_SUBCLASS.equals(psi) ||
            PSI.ON_SUPERCLASS.equals(psi) ||
            PSI.ON_SUBCLASS.equals(psi))
          return true;
        
        // If one PSI is an Ontopoly system PSI, then the topic should be filtered out
        // unless if there is an exception
        if (psi.getAddress().startsWith("http://psi.ontopia.net/ontology/"))
          include = false;
      }
    }
    return include;
  }
    
}
