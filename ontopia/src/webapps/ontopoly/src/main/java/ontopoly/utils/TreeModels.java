package ontopoly.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.PSI;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.TopicMap;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.TopicType;
import net.ontopia.topicmaps.query.core.DeclarationContextIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryWrapper;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.utils.OntopiaRuntimeException;
import ontopoly.models.TopicMapModel;
import ontopoly.pojos.TopicNode;

public class TreeModels {

  public static TreeModel createEmptyTreeModel() {
    return new DefaultTreeModel(new DefaultMutableTreeNode("<root>"));
  }
  
  public static TreeModel createTopicTypesTreeModel(TopicMap tm, boolean isAnnotationEnabled, boolean isAdminEnabled) {
    StringBuffer sb = new StringBuffer(); 
    sb.append("using on for i\"http://psi.ontopia.net/ontology/\" ");
    sb.append("using xtm for i\"http://www.topicmaps.org/xtm/1.0/core.xtm#\" ");
    sb.append("select $P, $C from ");
    if (isAnnotationEnabled)
      sb.append("{ instance-of($C, on:ontology-type) | $C = on:topic-map, topic($C) | ");
    sb.append("instance-of($C, on:topic-type), ");
    if (!isAdminEnabled)
      sb.append("not(direct-instance-of($C, on:system-topic)), ");
    sb.append("{ xtm:superclass-subclass($C : xtm:subclass, $P : xtm:superclass), instance-of($P, on:topic-type)");
    if (!isAdminEnabled)
      sb.append(", not(direct-instance-of($P, on:system-topic))");
    sb.append(" }");    
    if (isAnnotationEnabled)
      sb.append("}");
    sb.append(" order by $P, $C?");
    
    final String topicMapId = tm.getId();
    return new QueryTreeModel(tm, sb.toString(), Collections.EMPTY_MAP) {
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
    StringBuffer sb = new StringBuffer(); 
    sb.append("using on for i\"http://psi.ontopia.net/ontology/\" ");
    sb.append("using xtm for i\"http://www.topicmaps.org/xtm/1.0/core.xtm#\" ");
    sb.append("select $P, $C from ");
    sb.append("instance-of($C, ").append(typePSI).append("), ");
    if (!isAdminEnabled)
      sb.append("not(direct-instance-of($C, on:system-topic)), ");
    sb.append("{ xtm:superclass-subclass($C : xtm:subclass, $P : xtm:superclass), instance-of($P, ").append(typePSI).append(")");
    if (!isAdminEnabled)
      sb.append(", not(direct-instance-of($P, on:system-topic))");
    sb.append(" }");    
    sb.append(" order by $P, $C?");
    
    final String topicMapId = tm.getId();
    return new QueryTreeModel(tm, sb.toString(), Collections.EMPTY_MAP) {
      @Override
      protected DefaultMutableTreeNode createTreeNode(Object o) {
        return new DefaultMutableTreeNode(new TopicNode(topicMapId, ((TopicIF)o).getObjectId()));
      }
    };
  }
  
  private static class HierarchyDefinition {

    TopicIF atype;
    TopicIF prtype;
    TopicIF crtype;
    Collection ptypes = new HashSet();
    Collection ctypes = new HashSet();
    
    HierarchyDefinition(TopicIF atype, TopicIF prtype, TopicIF crtype) {
      this.atype = atype;
      this.prtype = prtype;
      this.crtype = crtype;
    }
      
    public boolean equals(Object other) {
      HierarchyDefinition o = (HierarchyDefinition)other;
      return atype.equals(o.atype) && prtype.equals(o.prtype) && crtype.equals(o.crtype);
    }
    
    public int hashCode() {
      return atype.hashCode() + prtype.hashCode() + crtype.hashCode();
    }
    
  }
  
  public static TreeModel createInstancesTreeModel(TopicType topicType, final boolean isAdminEnabled) {    
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("<root>");    
    if (topicType != null) {
      QueryWrapper qw = topicType.getTopicMap().getQueryWrapper();
      QueryProcessorIF qp = qw.getQueryProcessor();
      DeclarationContextIF dc = qw.getDeclarationContext();
    
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
      Map hds = new HashMap();
      Map hd_ctypes = new HashMap();
      
      try {
        QueryResultIF qr = qp.execute(query, Collections.EMPTY_MAP, dc);
        try {
          while (qr.next()) {
            TopicIF atype = (TopicIF)qr.getValue(0);
            TopicIF crtype = (TopicIF)qr.getValue(1);
            TopicIF prtype = (TopicIF)qr.getValue(3);
            HierarchyDefinition hd = new HierarchyDefinition(atype, prtype, crtype);
            if (hds.containsKey(hd))
              hd = (HierarchyDefinition)hds.get(hd);
            else
              hds.put(hd, hd);
            TopicIF crpt = (TopicIF)qr.getValue(2);
            TopicIF prpt = (TopicIF)qr.getValue(4);
            hd.ctypes.add(crpt);
            hd.ptypes.add(prpt);
            if (!hd_ctypes.containsKey(crpt))
              hd_ctypes.put(crpt, new HashSet());
            ((Collection)hd_ctypes.get(crpt)).add(hd);
          }        
        } finally {
          qr.close();
        }
           
        // generate hierarchical query
        Map existingRules = new LinkedHashMap();
        TopicIF topicTypeIf = topicType.getTopicIF();
        
        StringBuffer sb = new StringBuffer();      
        sb.append("select $P, $C from\n");
        sb.append(createHierarchyRuleFor(topicTypeIf, "P", "C", "B", existingRules, hd_ctypes));
//        if (!isAdminEnabled)
//          sb.append(", not({direct-instance-of($P, on:system-topic) || direct-instance-of($C, on:system-topic)})");        
        sb.append("\norder by $P, $C?");
        
        Iterator riter = existingRules.values().iterator();
        while (riter.hasNext()) {
          sb.insert(0, (StringBuffer)riter.next());
        }
        
        String hquery = sb.toString();
//        System.out.println("QQ: " + hquery);

        TopicMap topicMap = topicType.getTopicMap();
        final String topicMapId = topicMap.getId();
        final TopicMapModel topicMapModel = new TopicMapModel(topicMap);
        Map params = Collections.singletonMap("topicType", topicTypeIf);
        
        return new QueryTreeModel(topicType.getTopicMap(), hquery, params) {
          @Override
          protected boolean filter(Object p, Object c) {
            if (isAdminEnabled) return true;
            // filter out system topics
            TopicIF systemTopic = topicMapModel.getTopicMap().getTopicMapIF().getTopicBySubjectIdentifier(PSI.ON_SYSTEM_TOPIC);
            if (p != null) {
              TopicIF pt = (TopicIF)p;
              if (pt.getTypes().contains(systemTopic)) return false;
            }
            if (c != null) {
              TopicIF ct = (TopicIF)c;
              if (ct.getTypes().contains(systemTopic)) return false;
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

  private static StringBuffer createHierarchyRuleFor(TopicIF topicType, String pVar, String cVar, String bVar, Map existingRules, Map hd_ctypes) {
    if (!existingRules.containsKey(topicType)) {
      StringBuffer sb = new StringBuffer();
      existingRules.put(topicType, sb);
      // create rule for type
      sb.append("/* ").append(TopicStringifiers.toString(topicType)).append(" */\n");
      sb.append("hierarchy-for-").append(topicType.getObjectId()).append("($P, $C, $B) :- \n");
      sb.append("instance-of($B, @").append(topicType.getObjectId()).append("),\n");
      Collection hds = (Collection)hd_ctypes.get(topicType);
      if (hds != null && !hds.isEmpty()) {
        sb.append(createStepPredicates(hds, existingRules, hd_ctypes));
      } else {
        sb.append("$B = $C, { not($P = $B) }"); // do weird logic to get $P bound
      }
      sb.append(".\n");
    }
    // call rule
    return new StringBuffer().append("hierarchy-for-").append(topicType.getObjectId())
    .append("($").append(pVar).append(", $").append(cVar).append(", $").append(bVar).append(")");
  }

  private static StringBuffer createStepPredicates(Collection hds, Map existingRules, Map hd_ctypes) {
    StringBuffer sb = new StringBuffer();
    // call parent rules
    sb.append("{ ");
    Iterator hiter = hds.iterator();
    while (hiter.hasNext()) {
      HierarchyDefinition hd = (HierarchyDefinition)hiter.next();
      sb.append("$B = $C, ");
      sb.append("{ ").append("@").append(hd.atype.getObjectId())
      .append("($C").append(" : @").append(hd.crtype.getObjectId())
      .append(", $P").append(" : @").append(hd.prtype.getObjectId()).append(")").append(" } |\n");
      sb.append("@").append(hd.atype.getObjectId())
      .append("($B").append(" : @").append(hd.crtype.getObjectId())
      .append(", $X").append(" : @").append(hd.prtype.getObjectId()).append("), ");
      sb.append(createStepPredicates(hd, "P", "C", "X", existingRules, hd_ctypes));
      if (hiter.hasNext()) sb.append(" |\n");
    }
    sb.append(" }\n");
    return sb;
  }
  
  private static StringBuffer createStepPredicates(HierarchyDefinition hd, String pVar, String cVar, String bVar, Map existingRules, Map hd_ctypes) {
    StringBuffer sb = new StringBuffer();
    
    if (hd.ptypes.size() > 1) sb.append("{");
    Iterator piter = hd.ptypes.iterator();
    while (piter.hasNext()) {
      TopicIF ptype = (TopicIF)piter.next();
      sb.append(createHierarchyRuleFor(ptype, pVar, cVar, bVar, existingRules, hd_ctypes));
      if (piter.hasNext()) sb.append(" | \n");            
    }
    if (hd.ptypes.size() > 1) sb.append("}");
    return sb;    
  }
  
  public static TreeModel createInstancesTreeModel2(TopicType topicType, boolean isAdminEnabled) {
    if (topicType == null)
      return new DefaultTreeModel(new DefaultMutableTreeNode("<root>"));      

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
    Map params = Collections.singletonMap("topicType", tt);
    
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
  
  public static TreeModel createQueryTreeModel(TopicMap topicMap, String query, Map params) {

    final String topicMapId = topicMap.getId();
    
    return new QueryTreeModel(topicMap, query, params) {
      @Override
      protected DefaultMutableTreeNode createTreeNode(Object o) {
        return new DefaultMutableTreeNode(new TopicNode(topicMapId, ((TopicIF)o).getObjectId()));
      }
    };    
  }
  
}
