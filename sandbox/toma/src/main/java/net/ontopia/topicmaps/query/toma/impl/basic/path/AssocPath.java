package net.ontopia.topicmaps.query.toma.impl.basic.path;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.core.index.NameIndexIF;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicPathElementIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.PathExpression;
import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;
import net.ontopia.topicmaps.query.toma.impl.basic.WildcardRoot;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractPathElement;

public class AssocPath extends AbstractPathElement 
  implements BasicPathElementIF {
  
  static final Set<TYPE> inputSet;
  
  static {
    inputSet = new HashSet<TYPE>();
    inputSet.add(TYPE.TOPIC);
  }

  public AssocPath() {
    super("ASSOC");
  }

  @Override
  protected boolean isLevelAllowed() {
    return false;
  }

  @Override
  protected boolean isScopeAllowed() {
    return true;
  }
  
  @Override
  protected boolean isTypeAllowed() {
    return true;
  }

  @Override
  protected boolean isChildAllowed() {
    return true;
  }
  
  public Set<TYPE> validInput() {
    return inputSet;
  }
  
  public TYPE output() {
    return TYPE.TOPIC;
  }
  
  public Collection<?> evaluate(LocalContext context, Object input) 
  {
    PathExpression type = (PathExpression) getType();
    ResultSet types = type.evaluate(context);
    Collection<?> validTypes = types.getValues(types.getColumnCount() - 1);
    
    ClassInstanceIndexIF index = 
      (ClassInstanceIndexIF) context.getTopicMap().getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");

    LinkedList<AssociationIF> assocs = new LinkedList<AssociationIF>();
    for (Object t : validTypes) {
      Collection<AssociationIF> a = index.getAssociations((TopicIF) t);
      assocs.addAll(a);
    }
    
    Collection<?> validScopes = null;
    if (getScope() != null) {
      PathExpression scope = (PathExpression) getScope();
      ResultSet scopes = scope.evaluate(context);
      validScopes = scopes.getValues(scopes.getColumnCount() - 1);
    }

    TopicIF topic = null;
    if (input instanceof TopicIF) {
      topic = (TopicIF) input;
    }

    PathExpression leftRole = null, rightRole = null;
    
    switch (getChildCount()) {
    case 1:
      rightRole = (PathExpression) getChild(0);
      break;
    case 2:
      leftRole = (PathExpression) getChild(0);
      rightRole = (PathExpression) getChild(1);
      break;
    default:
      // TODO: improve error handling
      System.err.println("invalid query, missing roles");
      break;
    }
    
    LinkedList<AssociationIF> finalAssocs;
    if (leftRole != null) {
      finalAssocs = getValidAssociations(context, assocs, leftRole, topic);
    } else {
      finalAssocs = assocs;
    }    
    
    if (rightRole.getRoot() instanceof WildcardRoot) 
    {
      LinkedList<LinkedList> result = new LinkedList<LinkedList>();
      for (AssociationIF assoc : finalAssocs) 
      {
        Collection<AssociationRoleIF> assocRoles = assoc.getRoles();
        for (AssociationRoleIF role : assocRoles) 
        {
          LinkedList row = new LinkedList();
          row.add(assoc);
          row.add(role.getPlayer());
          result.add(row);
        }
      }
      return result;
    } else {
      ResultSet roles = rightRole.evaluate(context);
      Collection<?> validRoles = roles.getValues(roles.getColumnCount() - 1);
      
      LinkedList<LinkedList> result = new LinkedList<LinkedList>();
      
      for (AssociationIF assoc : finalAssocs) 
      {
        for (Object o : validRoles) 
        {
          Collection<AssociationRoleIF> assocRoles = assoc.getRolesByType((TopicIF) o);
          for (AssociationRoleIF role : assocRoles) 
          {
            LinkedList row = new LinkedList();
            row.add(assoc);
            row.add(role.getPlayer());
            result.add(row);
          }
        }
      }
      
      return result;
    }
  }
  
  private LinkedList<AssociationIF> getValidAssociations(LocalContext context, LinkedList<AssociationIF> assocs, PathExpression roleType, TopicIF input) 
  {
    if (roleType.getRoot() instanceof WildcardRoot) 
    {
      if (input == null) return assocs;
      LinkedList<AssociationIF> validAssocs = new LinkedList<AssociationIF>();
      for (AssociationIF assoc : assocs) 
      {
        Collection<AssociationRoleIF> assocRoles = assoc.getRoles();
        for (AssociationRoleIF role : assocRoles) 
        {
          if (role.getPlayer().getObjectId().equals(input.getObjectId())) {
            validAssocs.add(assoc);
            break;
          }
        }
      }
      return validAssocs;
    } else {
      ResultSet roles = roleType.evaluate(context);
      Collection<?> validRoles = roles.getValues(roles.getColumnCount() - 1);
      
      LinkedList<AssociationIF> validAssocs = new LinkedList<AssociationIF>();
      
      for (AssociationIF assoc : assocs) 
      {
        for (Object o : validRoles) 
        {
          Collection<AssociationRoleIF> assocRoles = assoc.getRolesByType((TopicIF) o);
          for (AssociationRoleIF role : assocRoles) 
          {
            if (input == null || role.getPlayer().getObjectId().equals(input.getObjectId())) {
              validAssocs.add(assoc);
              break;
            }
          }
        }
      }
      
      return validAssocs;
    }
  }
  
  private boolean containsScope(Collection scopes, Collection valid) {
    for (Object scope : valid) {
      if (scopes.contains(scope)) {
        return true;
      }
    }
    return false;
  }
  
  public String[] getColumnNames() {
    if (getBoundVariable() != null) {
      return new String[] { getBoundVariable().toString() };
    } else {
      return new String[0];
    }
  }

  public int getResultSize() {
    if (getBoundVariable() != null) {
      return 1;
    } else {
      return 0;
    }
  }
}
