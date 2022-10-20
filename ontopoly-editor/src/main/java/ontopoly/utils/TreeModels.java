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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.core.DeclarationContextIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.utils.OntopiaRuntimeException;
import ontopoly.model.PSI;
import ontopoly.model.TopicMap;
import ontopoly.model.TopicType;
import ontopoly.models.TopicMapModel;
import ontopoly.pojos.TopicNode;

public class TreeModels {

  public static TreeModel createEmptyTreeModel() {
    return new DefaultTreeModel(new DefaultMutableTreeNode("<root>"));
  }
  
  public static TreeModel createTopicTypesTreeModel(TopicMap tm, boolean isAnnotationEnabled, boolean isAdminEnabled) {
    StringBuilder sb = new StringBuilder(); 
    sb.append("using on for i\"http://psi.ontopia.net/ontology/\" ");
    sb.append("using xtm for i\"http://www.topicmaps.org/xtm/1.0/core.xtm#\" ");
    sb.append("select $P, $C from ");
    if (isAnnotationEnabled) {
      sb.append("{ instance-of($C, on:ontology-type) | $C = on:topic-map, topic($C) | ");
    }
    sb.append("instance-of($C, on:topic-type), ");
    if (!isAdminEnabled) {
      sb.append("not(direct-instance-of($C, on:system-topic)), ");
    }
    sb.append("{ xtm:superclass-subclass($C : xtm:subclass, $P : xtm:superclass), instance-of($P, on:topic-type)");
    if (!isAdminEnabled) {
      sb.append(", not(direct-instance-of($P, on:system-topic))");
    }
    sb.append(" }");    
    if (isAnnotationEnabled) {
      sb.append("}");
    }
    sb.append(" order by $P, $C?");
    
    final String topicMapId = tm.getId();
    Map<String,?> params = Collections.emptyMap();
    return new QueryTreeModel(tm, sb.toString(), params) {
      @Override
      protected DefaultMutableTreeNode createTreeNode(Object o) {
        return new DefaultMutableTreeNode(new TopicNode(topicMapId, ((TopicIF)o).getObjectId()));
      }
    };
  }

  public static TreeModel createOccurrenceTypesTreeModel(TopicMap tm, boolean isAdminEnabled) {
    return createTypesTreeModel(tm, "on:occurrence-type", isAdminEnabled);
  }

  public static TreeModel createAssociationTypesTreeModel(TopicMap tm, boolean isAdminEnabled) {
    return createTypesTreeModel(tm, "on:association-type", isAdminEnabled);
  }

  public static TreeModel createRoleTypesTreeModel(TopicMap tm, boolean isAdminEnabled) {
    return createTypesTreeModel(tm, "on:role-type", isAdminEnabled);
  }

  public static TreeModel createNameTypesTreeModel(TopicMap tm, boolean isAdminEnabled) {
    return createTypesTreeModel(tm, "on:name-type", isAdminEnabled);
  }
  
  protected static TreeModel createTypesTreeModel(TopicMap tm, String typePSI, boolean isAdminEnabled) {
    StringBuilder sb = new StringBuilder(); 
    sb.append("using on for i\"http://psi.ontopia.net/ontology/\" ");
    sb.append("using xtm for i\"http://www.topicmaps.org/xtm/1.0/core.xtm#\" ");
    sb.append("select $P, $C from ");
    sb.append("instance-of($C, ").append(typePSI).append("), ");
    if (!isAdminEnabled) {
      sb.append("not(direct-instance-of($C, on:system-topic)), ");
    }
    sb.append("{ xtm:superclass-subclass($C : xtm:subclass, $P : xtm:superclass), instance-of($P, ").append(typePSI).append(")");
    if (!isAdminEnabled) {
      sb.append(", not(direct-instance-of($P, on:system-topic))");
    }
    sb.append(" }");    
    sb.append(" order by $P, $C?");
    
    final String topicMapId = tm.getId();
    Map<String,?> params = Collections.emptyMap();
    return new QueryTreeModel(tm, sb.toString(), params) {
      @Override
      protected DefaultMutableTreeNode createTreeNode(Object o) {
        return new DefaultMutableTreeNode(new TopicNode(topicMapId, ((TopicIF)o).getObjectId()));
      }
    };
  }
  
  private static class HierarchyDefinition {

    private TopicIF atype;
    private TopicIF prtype;
    private TopicIF crtype;
    private Collection<TopicIF> ptypes = new HashSet<TopicIF>();
    private Collection<TopicIF> ctypes = new HashSet<TopicIF>();
    
    HierarchyDefinition(TopicIF atype, TopicIF prtype, TopicIF crtype) {
      this.atype = atype;
      this.prtype = prtype;
      this.crtype = crtype;
    }
      
    @Override
    public boolean equals(Object other) {
      HierarchyDefinition o = (HierarchyDefinition)other;
      return atype.equals(o.atype) && prtype.equals(o.prtype) && crtype.equals(o.crtype);
    }
    
    @Override
    public int hashCode() {
      return atype.hashCode() + prtype.hashCode() + crtype.hashCode();
    }
    
  }
  
  public static TreeModel createInstancesTreeModel(TopicType topicType, final boolean isAdminEnabled) {
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("<root>");    
    if (topicType != null) {
      TopicMap topicMap = topicType.getTopicMap();
      QueryProcessorIF qp = topicMap.getQueryProcessor();
      DeclarationContextIF dc = topicMap.getDeclarationContext();
    
      String query = "using on for i\"http://psi.ontopia.net/ontology/\" " 
        + "using hierarchy for i\"http://www.techquila.com/psi/hierarchy/#\" " 
        
        + "select $ATYPE, $CRT, $CRPT, $PRT, $PRPT from "
        + "on:forms-hierarchy-for($TTYPE : on:topic-type, $ATYPE : on:association-type), " 
        + "on:has-association-type($ATYPE : on:association-type, $AF : on:association-field), "
        + "on:has-association-field($AF : on:association-field, $RF1 : on:role-field), " 
        + "on:has-role-type($RF1 : on:role-field, $PRT : on:role-type), " 
        + "instance-of($PRT, hierarchy:superordinate-role-type), "
        + "on:has-field($RF1 : on:field-definition, $PRPT : on:field-owner), "
        + "on:has-association-field($AF : on:association-field, $RF2 : on:role-field), " 
        + "on:has-role-type($RF2 : on:role-field, $CRT : on:role-type), " 
        + "instance-of($CRT, hierarchy:subordinate-role-type), "
        + "on:has-field($RF2 : on:field-definition, $CRPT : on:field-owner) "
        + "order by $ATYPE, $PRT, $CRT"
        + "?";
      
      // retrieve hierarchical definitions
      Map<HierarchyDefinition,HierarchyDefinition> hds = new HashMap<HierarchyDefinition,HierarchyDefinition>();
      Map<TopicIF,Set<HierarchyDefinition>> hd_ctypes = new HashMap<TopicIF,Set<HierarchyDefinition>>();
      
      try {
        Map<String,?> params = Collections.emptyMap();
        QueryResultIF qr = qp.execute(query, params, dc);
        try {
          while (qr.next()) {
            TopicIF atype = (TopicIF)qr.getValue(0);
            TopicIF crtype = (TopicIF)qr.getValue(1);
            TopicIF prtype = (TopicIF)qr.getValue(3);
            HierarchyDefinition hd = new HierarchyDefinition(atype, prtype, crtype);
            if (hds.containsKey(hd)) {
              hd = hds.get(hd);
            } else {
              hds.put(hd, hd);
            }
            TopicIF crpt = (TopicIF)qr.getValue(2);
            TopicIF prpt = (TopicIF)qr.getValue(4);
            hd.ctypes.add(crpt);
            hd.ptypes.add(prpt);
            if (!hd_ctypes.containsKey(crpt)) {
              hd_ctypes.put(crpt, new HashSet<HierarchyDefinition>());
            }
            hd_ctypes.get(crpt).add(hd);
          }        
        } finally {
          qr.close();
        }
           
        // generate hierarchical query
        Map<TopicIF,StringBuilder> existingRules = new LinkedHashMap<TopicIF,StringBuilder>();
        TopicIF topicTypeIf = topicType.getTopicIF();
        
        StringBuilder sb = new StringBuilder();      
        sb.append("select $P, $C from\n");
        sb.append(createHierarchyRuleFor(topicTypeIf, "P", "C", "B", existingRules, hd_ctypes));
//        if (!isAdminEnabled)
//          sb.append(", not({direct-instance-of($P, on:system-topic) || direct-instance-of($C, on:system-topic)})");        
        sb.append("\norder by $P, $C?");
        
        Iterator<StringBuilder> riter = existingRules.values().iterator();
        while (riter.hasNext()) {
          sb.insert(0, riter.next());
        }
        
        String hquery = sb.toString();

        final String topicMapId = topicMap.getId();
        final TopicMapModel topicMapModel = new TopicMapModel(topicMap);
        Map<String,TopicIF> hqparams = Collections.singletonMap("topicType", topicTypeIf);
        
        return new QueryTreeModel(topicType.getTopicMap(), hquery, hqparams) {
          @Override
          protected boolean filter(Object p, Object c) {
            if (isAdminEnabled) {
              return true;
            }
            // filter out system topics
            TopicIF systemTopic = topicMapModel.getTopicMap().getTopicMapIF().getTopicBySubjectIdentifier(PSI.ON_SYSTEM_TOPIC);
            if (p != null) {
              TopicIF pt = (TopicIF)p;
              if (pt.getTypes().contains(systemTopic)) {
                return false;
              }
            }
            if (c != null) {
              TopicIF ct = (TopicIF)c;
              if (ct.getTypes().contains(systemTopic)) {
                return false;
              }
            }
            return true;
          }
          @Override
          protected DefaultMutableTreeNode createTreeNode(Object o) {
            return new DefaultMutableTreeNode(new TopicNode(topicMapId, ((TopicIF)o).getObjectId()));
          }
        };
      } catch (InvalidQueryException e) {
        throw new OntopiaRuntimeException(e);
      }
    }
    return new DefaultTreeModel(root);
  }

  private static StringBuilder createHierarchyRuleFor(TopicIF topicType, String pVar, String cVar, String bVar, Map<TopicIF,StringBuilder> existingRules, Map<TopicIF,Set<HierarchyDefinition>> hd_ctypes) {
    if (!existingRules.containsKey(topicType)) {
      StringBuilder sb = new StringBuilder();
      existingRules.put(topicType, sb);
      // create rule for type
      sb.append("/* ").append(TopicStringifiers.toString(topicType)).append(" */\n");
      sb.append("hierarchy-for-").append(topicType.getObjectId()).append("($P, $C, $B) :- \n");
      sb.append("instance-of($B, @").append(topicType.getObjectId()).append("),\n");
      Collection<HierarchyDefinition> hds = hd_ctypes.get(topicType);
      if (hds != null && !hds.isEmpty()) {
        sb.append(createStepPredicates(hds, existingRules, hd_ctypes));
      } else {
        sb.append("$B = $C, { not($P = $B) }"); // do weird logic to get $P bound
      }
      sb.append(".\n");
    }
    // call rule
    return new StringBuilder().append("hierarchy-for-").append(topicType.getObjectId())
    .append("($").append(pVar).append(", $").append(cVar).append(", $").append(bVar).append(")");
  }

  private static StringBuilder createStepPredicates(Collection<HierarchyDefinition> hds, Map<TopicIF,StringBuilder> existingRules, Map<TopicIF,Set<HierarchyDefinition>> hd_ctypes) {
    StringBuilder sb = new StringBuilder();
    // call parent rules
    sb.append("{ ");
    Iterator<HierarchyDefinition> hiter = hds.iterator();
    while (hiter.hasNext()) {
      HierarchyDefinition hd = hiter.next();
      sb.append("$B = $C, ");
      sb.append("{ ").append("@").append(hd.atype.getObjectId())
      .append("($C").append(" : @").append(hd.crtype.getObjectId())
      .append(", $P").append(" : @").append(hd.prtype.getObjectId()).append(")").append(" } |\n");
      sb.append("@").append(hd.atype.getObjectId())
      .append("($B").append(" : @").append(hd.crtype.getObjectId())
      .append(", $X").append(" : @").append(hd.prtype.getObjectId()).append("), ");
      sb.append(createStepPredicates(hd, "P", "C", "X", existingRules, hd_ctypes));
      if (hiter.hasNext()) {
        sb.append(" |\n");
      }
    }
    sb.append(" }\n");
    return sb;
  }
  
  private static StringBuilder createStepPredicates(HierarchyDefinition hd, String pVar, String cVar, String bVar, Map<TopicIF,StringBuilder> existingRules, Map<TopicIF,Set<HierarchyDefinition>> hd_ctypes) {
    StringBuilder sb = new StringBuilder();
    
    if (hd.ptypes.size() > 1) {
      sb.append("{");
    }
    Iterator<TopicIF> piter = hd.ptypes.iterator();
    while (piter.hasNext()) {
      TopicIF ptype = piter.next();
      sb.append(createHierarchyRuleFor(ptype, pVar, cVar, bVar, existingRules, hd_ctypes));
      if (piter.hasNext()) {
        sb.append(" | \n");
      }            
    }
    if (hd.ptypes.size() > 1) {
      sb.append("}");
    }
    return sb;    
  }
  
  public static TreeModel createInstancesTreeModel2(TopicType topicType, boolean isAdminEnabled) {
    if (topicType == null) {
      return new DefaultTreeModel(new DefaultMutableTreeNode("<root>"));
    }      

    TopicIF tt = topicType.getTopicIF();

    String query = 
      "using on for i\"http://psi.ontopia.net/ontology/\" " +
      "using hierarchy for i\"http://www.techquila.com/psi/hierarchy/#\" " + 
  
      "hierarchical-parent($P, $C) :- " + 
      "on:forms-hierarchy-for($TTYPE : on:topic-type, $ATYPE : on:association-type), " +
      "on:has-association-type($ATYPE : on:association-type, $AF : on:association-field), " +
      "on:has-association-field($AF : on:association-field, $RF1 : on:role-field), " +
      "on:has-role-type($RF1 : on:role-field, $PRT : on:role-type), " + 
      "instance-of($PRT, hierarchy:superordinate-role-type), " + 
      "on:has-association-field($AF : on:association-field, $RF2 : on:role-field), " +
      "on:has-role-type($RF2 : on:role-field, $CRT : on:role-type), " + 
      "instance-of($CRT, hierarchy:subordinate-role-type), " +
      "type($A, $ATYPE), " +
      "association-role($A, $PR), type($PR, $PRT), " +
      "association-role($A, $CR), type($CR, $CRT), $PR /= $CR, " +
      "role-player($PR, $P), role-player($CR, $C). " + 
  
      "select $P, $C from " +
      "  instance-of($C, %topicType%), " + 
      (!isAdminEnabled ? "not(direct-instance-of($C, on:system-topic)), " : "" ) +
      "  { hierarchical-parent($P, $C) " +
      (!isAdminEnabled ? ", not(direct-instance-of($P, on:system-topic))" : "" ) +
      "} " +
      "order by $P, $C?";
    Map<String,TopicIF> params = Collections.singletonMap("topicType", tt);
    
//    System.out.println("TT: " + tt);
//    System.out.println("HQ: " + query);
    final String topicMapId = topicType.getTopicMap().getId();

    return new QueryTreeModel(topicType.getTopicMap(), query, params) {
      @Override
      protected DefaultMutableTreeNode createTreeNode(Object o) {
        return new DefaultMutableTreeNode(new TopicNode(topicMapId, ((TopicIF)o).getObjectId()));
      }
    };
  }
  
  public static TreeModel createQueryTreeModel(TopicMap topicMap, String query, Map<String,?> params) {

    final String topicMapId = topicMap.getId();
    
    return new QueryTreeModel(topicMap, query, params) {
      @Override
      protected DefaultMutableTreeNode createTreeNode(Object o) {
        return new DefaultMutableTreeNode(new TopicNode(topicMapId, ((TopicIF)o).getObjectId()));
      }
    };    
  }
  
}
