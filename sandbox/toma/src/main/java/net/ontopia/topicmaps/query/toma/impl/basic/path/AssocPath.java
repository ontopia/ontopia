package net.ontopia.topicmaps.query.toma.impl.basic.path;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;
import net.ontopia.topicmaps.query.toma.impl.basic.expression.PathExpression;

/**
 * INTERNAL: Association path element in an path expression. Returns all
 * topics that take part in the specified association.
 * <p>
 * <b>Allowed Input</b>:
 * <ul>
 * <li>TOPIC
 * </ul>
 * </p><p>
 * <b>Output</b>: TOPIC
 * </p>
 */
public class AssocPath extends AbstractBasicPathElement { 
  static final Set<TYPE> inputSet;
  
  static {
    inputSet = new HashSet<TYPE>();
    inputSet.add(TYPE.TOPIC);
    inputSet.add(TYPE.NONE);
  }

  public AssocPath() {
    super("ASSOC");
  }

  protected boolean isLevelAllowed() {
    return false;
  }

  protected boolean isScopeAllowed() {
    return true;
  }
  
  protected boolean isTypeAllowed() {
    return true;
  }

  protected boolean isChildAllowed() {
    return true;
  }
  
  public Set<TYPE> validInput() {
    return inputSet;
  }
  
  public TYPE output() {
    return TYPE.TOPIC;
  }
  
  @SuppressWarnings("unchecked")
  public Collection<?> evaluate(LocalContext context, Object input)
      throws InvalidQueryException  {
    PathExpression type = (PathExpression) getType();
    ResultSet types = type.evaluate(context);
    Collection<?> validTypes = types.getValues(types.getLastIndex());
    
    ClassInstanceIndexIF index = 
      (ClassInstanceIndexIF) context.getTopicMap().getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");

    LinkedList<AssociationIF> assocs = new LinkedList<AssociationIF>();
    for (Object t : validTypes) {
      Collection<AssociationIF> a = index.getAssociations((TopicIF) t);
      assocs.addAll(a);
    }
    
    // TODO: implement scoped associations
//    Collection<?> validScopes = null;
//    if (getScope() != null) {
//      PathExpression scope = (PathExpression) getScope();
//      ResultSet scopes = scope.evaluate(context);
//      validScopes = scopes.getValues(scopes.getLastIndex());
//    }

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
      throw new InvalidQueryException(
          "missing roles in association path element");
    }
    
    LinkedList<AssociationIF> finalAssocs;
    if (leftRole != null) {
      finalAssocs = getValidAssociations(context, assocs, leftRole, topic);
    } else {
      finalAssocs = assocs;
    }    
    
    if (rightRole.isEmpty()) 
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
      Collection<?> validRoles = roles.getValues(roles.getLastIndex());
      
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
  
  @SuppressWarnings("unchecked")
  private LinkedList<AssociationIF> getValidAssociations(LocalContext context, LinkedList<AssociationIF> assocs, PathExpression roleType, TopicIF input) 
  {
    if (roleType.isEmpty()) 
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
}
