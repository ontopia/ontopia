
package net.ontopia.topicmaps.viz;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.net.MalformedURLException;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StringifierIF;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.TopicStringifiers;

import com.touchgraph.graphlayout.Node;

/**
 * INTERNAL: Helper methods.
 */
public class VizUtils {
  public static LocatorIF makeLocator(String url) {
    try {
      return new URILocator(url);
    } catch (MalformedURLException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  public static StringifierIF stringifierFor(TopicIF scope) {
    if (scope == null)
      return TopicStringifiers.getDefaultStringifier();

    return TopicStringifiers.getTopicNameStringifier(Collections
          .singleton(scope));
  }
  
  public static LocatorIF makeLocator(File file) {
    return new URILocator(file);
  }

  public static void debug(TopicIF topic) {
    System.out.println("Object ID: " + topic.getObjectId()); 
    Iterator it = topic.getSubjectLocators().iterator();
    while (it.hasNext())
      System.out.println("Subject: " + it.next()); 
    it = topic.getSubjectIdentifiers().iterator();
    while (it.hasNext())
      System.out.println("Indicator: " + it.next()); 
    it = topic.getItemIdentifiers().iterator();
    while (it.hasNext())
      System.out.println("Source: " + it.next()); 

    it = topic.getRoles().iterator();
    while (it.hasNext()) {
      AssociationIF assoc = ((AssociationRoleIF) it.next()).getAssociation();
      System.out.print("Association " + assoc.getObjectId() + ": ");  
      Iterator it2 = assoc.getRoles().iterator();
      while (it2.hasNext()) {
        AssociationRoleIF role = (AssociationRoleIF) it2.next();
        if (role.getPlayer() != topic)
          System.out.print(" " + role.getPlayer().getObjectId()); 
      }
      System.out.println();
    }
  }

  public static void debug(Node node) {
    System.out.println("Node ID: " + node.getID()); 
    System.out.println("Node: " + node); 

    Iterator it = node.getEdges();
    while (it.hasNext()) {
      TMAssociationEdge ed = (TMAssociationEdge) it.next();
      System.out.println("Edge (" + ed.getID() + "): " +  
                         ((TMTopicNode) ed.getFrom()).getTopic() +
                         " -> " + ((TMTopicNode) ed.getTo()).getTopic() + " " +  
                         ed.getAssociation());
    }
  }
}
