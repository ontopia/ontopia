
// $Id: AssociationClassAnalyzer.java,v 1.3 2008/01/10 11:08:49 geir.gronmo Exp $

package net.ontopia.topicmaps.schema.impl.osl.cmdline;

import java.util.*;

import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.schema.impl.osl.*;

public class AssociationClassAnalyzer extends AbstractSchemaAnalyzer {


  private AssociationClass aclass;
  private TopicIF atype;
  private OSLSchema schema;
  private Collection associations;
  private Map rtypes;
  private Map ptypes;


  /**
   * 
   */
  public AssociationClassAnalyzer(OSLSchema schema, TopicIF assoctype, Collection associations) {
    // init
    this.schema = schema;
    this.atype = assoctype;
    this.associations = associations;
    this.rtypes = new HashMap();
    this.ptypes = new HashMap();

    // setup
    TypeSpecification spec = getTypeSpecification(atype);
    
    if (spec != null) {    
      aclass = new AssociationClass(schema);
      aclass.setTypeSpecification(spec);
      //! aclass.setScopeSpecification(getScopeSpecification(atype)); // WARN: commented out this as it no longer compiles. nobody understands it either, so it it left for those who sometime in the future might care.
      schema.addAssociationClass(aclass);
    }    
  }


  /**
   * Analyzes the topics for this AssociationClassAnalyzerz
   */ 
  public void analyze() {
    // Not setup properly return with nothing.
    if (aclass == null) return;

    Iterator it = associations.iterator();
    while (it.hasNext()) {
      AssociationIF assoc = (AssociationIF)it.next();
      Iterator it2 = assoc.getRoles().iterator();
      while (it2.hasNext()) {
        registerConstraint((AssociationRoleIF)it2.next());
      }
    }
  }
  
  public void registerConstraint(AssociationRoleIF role) {

    AssociationRoleConstraint constraint = getRoleConstraint(role);
    TopicIF player = role.getPlayer();
    
    if (player != null) {
      Collection types = player.getTypes();
      if (types.isEmpty()) {
        // Player has no types.
        String constraint_key = makeKey(role.getAssociation().getType(), role.getType(), null);
        if (!ptypes.containsKey(constraint_key)) {
          TypeSpecification ptspec = new TypeSpecification();
          ptspec.setClassMatcher(new AnyTopicMatcher());
          constraint.addPlayerType(ptspec);
          ptypes.put(constraint_key, ptspec);
        }
      } else {

        // FIXME: Ignore if any player constraint already exists.
        Iterator iter = types.iterator();
        while (iter.hasNext()) {
          TopicIF ptype = (TopicIF)iter.next();
          
          String constraint_key = makeKey(role.getAssociation().getType(), role.getType(), ptype);
          
          if (!ptypes.containsKey(constraint_key)) {
            // Create new player type specification
            TypeSpecification ptspec = getTypeSpecification(ptype);
            ptypes.put(constraint_key, ptspec);
            // Register player constraint with association role constraint
            // FIXME: this still sucks, since the duplicates are not removed.
            constraint.addPlayerType(ptspec);
          }
        }
      }
    }
  }

  public AssociationRoleConstraint getRoleConstraint(AssociationRoleIF role) {

    TopicIF atype = role.getAssociation().getType();
    TopicIF rtype = role.getType();
    
    String constraint_key = makeKey(atype, rtype);
    
    if (rtypes.containsKey(constraint_key))
      return (AssociationRoleConstraint)rtypes.get(constraint_key);
    else {
      AssociationRoleConstraint constraint = new AssociationRoleConstraint(aclass);
      constraint.setTypeSpecification(getTypeSpecification(rtype));
      aclass.addRoleConstraint(constraint);
      rtypes.put(constraint_key, constraint);
      return constraint;
    }
  }
  
}
