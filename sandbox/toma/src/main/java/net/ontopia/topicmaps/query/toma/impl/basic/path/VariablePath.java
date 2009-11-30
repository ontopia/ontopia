/**
 * Copyright (C) 2009 Space Applications Services
 *   <thomas.neidhart@spaceapplications.com>
 *
 * This file is part of the Ontopia project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ontopia.topicmaps.query.toma.impl.basic.path;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicPathElementIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;
import net.ontopia.topicmaps.query.toma.impl.basic.Row;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractVariable;
import net.ontopia.topicmaps.query.toma.parser.ast.VariableDecl;
import net.ontopia.utils.CompactHashSet;

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

  @SuppressWarnings("unchecked")
  public Collection<?> evaluate(LocalContext context, Object input)
      throws InvalidQueryException {
    // try to get a ResultSet that already bound the variable
    ResultSet rs = context.getResultSet(toString());

    String varName = toString();
    if (rs != null) {
      if (getResultSize() > 1) {
        List<String> vars = rs.getBoundVariables();
        // move current var to the end of the list
        vars.remove(varName);
        vars.add(varName);
        
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
        return rs.getValues(varName);
      }
    } else {
      TopicMapIF topicmap = context.getTopicMap();
      
      switch (output()) {
      case ASSOCIATION:
        return topicmap.getAssociations();
        
      case TOPIC:
        return topicmap.getTopics();
        
      case NAME:
      {
        Collection names = new CompactHashSet();
        for (Object topic : topicmap.getTopics()) {
          names.addAll(((TopicIF) topic).getTopicNames());
        }
        return names;
      }

      case OCCURRENCE:
      {
        Collection ocs = new CompactHashSet();
        for (Object topic : topicmap.getTopics()) {
          ocs.addAll(((TopicIF) topic).getOccurrences());
        }
        return ocs;
      }
      
      case VARIANT:
      {
        Collection vars = new CompactHashSet();
        for (Object topic : topicmap.getTopics()) {
          for (Object name : ((TopicIF) topic).getTopicNames()) {
            vars.addAll(((TopicNameIF) name).getVariants());
          }
        }
        return vars;
      }
          
      default:
        throw new InvalidQueryException("Variable type '" + output() + "' not yet supported.");
      }
    }
  }
}
