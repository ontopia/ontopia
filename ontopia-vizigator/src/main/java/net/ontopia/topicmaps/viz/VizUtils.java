/*
 * #!
 * Ontopia Vizigator
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.topicmaps.viz;

import com.touchgraph.graphlayout.Node;
import java.io.File;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Function;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Helper methods.
 */
public class VizUtils {
  public static LocatorIF makeLocator(String url) {
    try {
      return new URILocator(url);
    } catch (URISyntaxException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  public static Function<TopicIF, String> stringifierFor(TopicIF scope) {
    if (scope == null) {
      return TopicStringifiers.getDefaultStringifier();
    }

    return TopicStringifiers.getTopicNameStringifier(Collections
          .singleton(scope));
  }
  
  public static LocatorIF makeLocator(File file) {
    return new URILocator(file);
  }

  public static void debug(TopicIF topic) {
    System.out.println("Object ID: " + topic.getObjectId()); 
    Iterator it = topic.getSubjectLocators().iterator();
    while (it.hasNext()) {
      System.out.println("Subject: " + it.next());
    } 
    it = topic.getSubjectIdentifiers().iterator();
    while (it.hasNext()) {
      System.out.println("Indicator: " + it.next());
    } 
    it = topic.getItemIdentifiers().iterator();
    while (it.hasNext()) {
      System.out.println("Source: " + it.next());
    } 

    it = topic.getRoles().iterator();
    while (it.hasNext()) {
      AssociationIF assoc = ((AssociationRoleIF) it.next()).getAssociation();
      System.out.print("Association " + assoc.getObjectId() + ": ");  
      Iterator it2 = assoc.getRoles().iterator();
      while (it2.hasNext()) {
        AssociationRoleIF role = (AssociationRoleIF) it2.next();
        if (role.getPlayer() != topic) {
          System.out.print(" " + role.getPlayer().getObjectId());
        } 
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
