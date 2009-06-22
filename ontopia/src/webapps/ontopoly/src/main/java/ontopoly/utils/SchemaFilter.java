
// $Id: SchemaFilter.java,v 1.5 2008/12/04 11:29:39 lars.garshol Exp $

package ontopoly.utils;

import java.util.Collection;
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
    boolean isOntopolyTopic = false;
    if (object instanceof TopicIF) {
      TopicIF topic = (TopicIF)object;

      // check system topic types first
      Collection types = topic.getTypes();      
      TopicIF systemTopic = topic.getTopicMap().getTopicBySubjectIdentifier(PSI.ON_SYSTEM_TOPIC);
      if (systemTopic != null && types.contains(systemTopic)) return false;
      TopicIF publicSystemTopic = topic.getTopicMap().getTopicBySubjectIdentifier(PSI.ON_PUBLIC_SYSTEM_TOPIC);
      if (publicSystemTopic != null && types.contains(publicSystemTopic)) return false;
      
      // check subject identifiers first
      Iterator it = topic.getSubjectIdentifiers().iterator();
      while (it.hasNext()) {
        LocatorIF psi = (LocatorIF) it.next();
        
        // If one PSI is an Ontopoly system PSI, then the topic should be filtered out
        // unless it's the supertype/subtype PSIs
        if (!isOntopolyTopic)
          isOntopolyTopic = psi.getAddress().startsWith("http://psi.ontopia.net/ontopoly/");
        
        if (isOntopolyTopic) {
           if (PSI.XTM_SUPERCLASS_SUBCLASS.equals(psi) ||
               PSI.XTM_SUPERCLASS.equals(psi) ||
               PSI.XTM_SUBCLASS.equals(psi))
             isOntopolyTopic = false;
        }
      }
   }
   return !isOntopolyTopic;
  }
    
}
