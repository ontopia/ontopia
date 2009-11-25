package net.ontopia.topicmaps.query.toma.impl.basic.path;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicPathElementIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;
import net.ontopia.topicmaps.query.toma.impl.basic.Row;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractVariable;
import net.ontopia.topicmaps.query.toma.parser.ast.VariableDecl;

/**
 * INTERNAL: Represents a variable within a TOMA query.
 */
public class VariablePath extends AbstractVariable implements
    BasicPathElementIF {
  static final Set<TYPE> inputSet;

  static {
    inputSet = new HashSet<TYPE>();
    inputSet.add(TYPE.NONE);
  }

  private String[] columns;
  private int resultSize;
  
  public VariablePath(VariableDecl decl) {
    super(decl);
  }

  public void initResultSet(LocalContext context) {
    ResultSet rs = context.getResultSet(toString());
    if (rs == null) {
      resultSize = 1;
      columns = new String[] { toString() };      
    } else {
      resultSize = rs.getBoundVariables().size();
      List<String> boundVariables = rs.getBoundVariables();
      boundVariables.remove(toString());
      boundVariables.add(toString());
      columns = boundVariables.toArray(new String[0]);
    }
  }

  public String[] getColumnNames() {
    return columns;
  }

  public int getResultSize() {
    return resultSize;
  }

  @Override
  protected boolean isChildAllowed() {
    return false;
  }

  @Override
  protected boolean isLevelAllowed() {
    return false;
  }

  @Override
  protected boolean isScopeAllowed() {
    return false;
  }

  @Override
  protected boolean isTypeAllowed() {
    return false;
  }

  public TYPE output() {
    Set<TYPE> types = getValidTypes();
    if (types.size() != 1) {
      return TYPE.UNKNOWN;
    } else {
      return types.iterator().next();
    }
  }

  public Set<TYPE> validInput() {
    return inputSet;
  }

  public Collection<?> evaluate(LocalContext context, Object input) {
    // try to get a ResultSet that already bound the variable
    ResultSet rs = context.getResultSet(toString());

    // TODO: Variables can be of any type, not just topics

    if (rs != null) {
      if (getResultSize() > 1) {
        List<String> vars = rs.getBoundVariables();
        // FIXME: this is a hack to move the current bound variable to the end
        // of the list.
        vars.remove(toString());
        vars.add(toString());
        int[] indices = new int[vars.size()];
        int idx = 0;
        for (String var : vars) {
          indices[idx++] = rs.getColumnIndex(var);
        }

        Collection<Object[]> result = new LinkedList<Object[]>();
        for (Row r : rs) {
          Object[] obj = new Object[indices.length];
          idx = 0;
          for (int i : indices) {
            obj[idx++] = r.getValue(i);
          }
          result.add(obj);
        }
        return result;
      } else {
        return rs.getValues(toString());
      }
    } else {
      TopicMapIF topicmap = context.getTopicMap();
      
      switch (output()) {
      case ASSOCIATION:
        return topicmap.getAssociations();
        
      default:
        // TODO: fix for other types
        return topicmap.getTopics();
          
      }
    }
  }
}
