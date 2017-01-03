/*
 * #!
 * Ontopia Engine
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

package net.ontopia.persistence.query.sql;

import java.util.List;
import net.ontopia.utils.OntopiaRuntimeException;
import org.apache.commons.lang3.StringUtils;

/**
 * INTERNAL: SQL set operation.
 */

public class SQLSetOperation implements SQLExpressionIF {

  public static final int UNION = 10;
  public static final int UNION_ALL = 11;
  public static final int INTERSECT = 20;
  public static final int INTERSECT_ALL = 21;
  public static final int EXCEPT = 30;
  public static final int EXCEPT_ALL = 31;

  protected List sets;
  protected int operator;
  
  public SQLSetOperation(List sets, int operator) {
    // A set contain either JDOQuery or JDOSetOperation instances.
    // FIXME: Sets must have same width and compatible types.
    this.sets = sets;
    this.operator = operator;
  }

  public int getOperator() {
    return operator;
  }
  
  public List getSets() {
    return sets;
  }

  public int getType() {
    return SET_OPERATION;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    String op;
    switch (operator) {
    case UNION:
      op = ") union (";
      break;
    case UNION_ALL:
      op = ") union all (";
      break;
    case INTERSECT:
      op = ") intersect (";
      break;
    case INTERSECT_ALL:
      op = ") intersect all (";
      break;
    case EXCEPT:
      op = ") except (";
      break;
    case EXCEPT_ALL:
      op = ") except all (";
      break;
    default:
      throw new OntopiaRuntimeException("Unsupported set operator: '" + operator + "'");
    }
    sb.append('(');
    sb.append(StringUtils.join(sets, op));
    sb.append(')');
    return sb.toString();
  }
  
}
