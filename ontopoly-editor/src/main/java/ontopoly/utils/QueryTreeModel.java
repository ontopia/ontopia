/*
 * #!
 * Ontopoly Editor
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
package ontopoly.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import ontopoly.model.TopicMap;

import net.ontopia.topicmaps.query.core.DeclarationContextIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.utils.OntopiaRuntimeException;

public abstract class QueryTreeModel extends DefaultTreeModel {
  
  public QueryTreeModel(TopicMap topicMap, String query, Map<String, ?> params) {
    super(new DefaultMutableTreeNode("<root>"));    
    
    QueryProcessorIF qp = topicMap.getQueryProcessor();
    DeclarationContextIF dc = topicMap.getDeclarationContext();

    DefaultMutableTreeNode root = (DefaultMutableTreeNode)getRoot();
    Map<Object,DefaultMutableTreeNode> nodes = new HashMap<Object,DefaultMutableTreeNode>();
    
    try {
      QueryResultIF qr = qp.execute(query, params, dc);

      // ignore query if it contains less than two columns
      if (qr.getWidth() < 2) {
        return;
      }
      
      try {
        while (qr.next()) {
          Object p = qr.getValue(0);
          Object c = qr.getValue(1);
          if (!filter(p, c)) {
            continue;
          }
          
          DefaultMutableTreeNode parent;
          DefaultMutableTreeNode child;
          
          if (p == null) {
            parent = (DefaultMutableTreeNode)root; // orphans are children of root
          } else {
            parent = (DefaultMutableTreeNode)nodes.get(p);
            if (parent == null) {
              parent = createTreeNode(p);
              nodes.put(p, parent);
            }
          }
          
          if (c == null) {
            continue; // ignore children that are null
          } else {
            child = (DefaultMutableTreeNode)nodes.get(c);
            if (child == null) {
              child = createTreeNode(c);
              nodes.put(c, child);
            }
          }
          parent.add(child);
        }
      } finally {
        qr.close();
      }
      // add rootless nodes to root
      Iterator<DefaultMutableTreeNode> iter = nodes.values().iterator();
      while (iter.hasNext()) {
        DefaultMutableTreeNode node = iter.next();
        if (node.getParent() == null) {
          root.add(node);
        }
      }
      
    } catch (InvalidQueryException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  /**
   * Filter method that should return true if objects should be part of result set.p
   * @param p Parent object.
   * @param c Child object.
   */
  protected boolean filter(Object p, Object c) {
    return true;
  }

  protected abstract DefaultMutableTreeNode createTreeNode(Object o);
  
}
