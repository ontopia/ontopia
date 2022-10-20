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

package net.ontopia.persistence.query.jdo;

import java.util.Collection;

import net.ontopia.persistence.proxy.ClassInfoIF;
import net.ontopia.persistence.proxy.FieldInfoIF;
import net.ontopia.persistence.proxy.ObjectRelationalMappingIF;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Methods for reducing JDOExpressionIF, so that expressions
 * that require no input data can be removed from the query.<p>
 *
 * Return values [type int] from evaluate methods: 1 is true, -1 is
 * false, 0 means cannot be evaluated.<p>
 * 
 */
public class JDOEvaluator {

  public static int evaluateExpression(JDOExpressionIF jdoexpr, ObjectRelationalMappingIF mapping, boolean reduce) {
    switch (jdoexpr.getType()) {
      // method calls
    case JDOExpressionIF.EQUALS: {
      return evaluateEquals((JDOEquals)jdoexpr, mapping);
    }
    case JDOExpressionIF.NOT_EQUALS: {
      return evaluateNotEquals((JDONotEquals)jdoexpr, mapping);
    }
    case JDOExpressionIF.CONTAINS: {      
      return evaluateContains((JDOContains)jdoexpr, mapping);
    }
    case JDOExpressionIF.IS_EMPTY: {
      return evaluateIsEmpty((JDOIsEmpty)jdoexpr, mapping);
    }
    case JDOExpressionIF.STARTS_WITH: {
      return evaluateStartsWith((JDOStartsWith)jdoexpr, mapping);
    }
    case JDOExpressionIF.ENDS_WITH: {
      return evaluateEndsWith((JDOEndsWith)jdoexpr, mapping);
    }
    case JDOExpressionIF.LIKE: {
      return evaluateLike((JDOLike)jdoexpr, mapping);
    }
      // logical operators
    case JDOExpressionIF.AND: {
      return evaluateAnd((JDOAnd)jdoexpr, mapping, reduce);
    }
    case JDOExpressionIF.OR: {
      return evaluateOr((JDOOr)jdoexpr, mapping, reduce);
    }
    case JDOExpressionIF.NOT: {
      return evaluateNot((JDONot)jdoexpr, mapping, reduce);
    }
    case JDOExpressionIF.BOOLEAN: {
      return evaluateBoolean((JDOBoolean)jdoexpr, mapping);
    }
    case JDOExpressionIF.VALUE_EXPRESSION: {
      return evaluateValueExpression((JDOValueExpression)jdoexpr, mapping);
    }
      // set operators
    case JDOExpressionIF.SET_OPERATION: {
      // FIXME:
      return 0;
    }
    default:
      throw new OntopiaRuntimeException("Expression is of unknown type: '" + jdoexpr + "'");
    }
  }

  public static int evaluateBoolean(JDOBoolean jdoexpr, ObjectRelationalMappingIF mapping) {
    return (jdoexpr.getValue() ? 1 : -1); 
  }

  public static int evaluateValueExpression(JDOValueExpression jdoexpr, ObjectRelationalMappingIF mapping) {
    return 0; 
  }

  public static int evaluateEquals(JDOEquals jdoexpr, ObjectRelationalMappingIF mapping) {
    JDOValueIF left = jdoexpr.getLeft();
    JDOValueIF right = jdoexpr.getRight();

    // Decisive only if equals
    if (left.equals(right)) {
      return 1;
    }
      
    // If both are objects compare them
    if (isEvaluatable(left, mapping) &&
        isEvaluatable(right, mapping)) {
      Object lval = evaluateJDOValue(left, mapping);
      Object rval = evaluateJDOValue(right, mapping);
      return (lval == null ? (rval == null ? 1 : -1) : (lval.equals(rval) ? 1 : -1));
      //! return (lval.equals(rval) ? 1 : -1);
    }

    // FIXME: object vs parameter can only be validated at execution.
    return 0;
  }

  public static int evaluateNotEquals(JDONotEquals jdoexpr, ObjectRelationalMappingIF mapping) {
    JDOValueIF left = jdoexpr.getLeft();
    JDOValueIF right = jdoexpr.getRight();

    // Decisive only if equals
    if (left.equals(right)) {
      return -1;
    }
      
    // If both are objects compare them
    if (isEvaluatable(left, mapping) &&
        isEvaluatable(right, mapping)) {
      Object lval = evaluateJDOValue(left, mapping);
      Object rval = evaluateJDOValue(right, mapping);
      return (lval == null ? (rval == null ? -1 : 1) : (lval.equals(rval) ? -1 : 1));
      //! return (lval.equals(rval) ? -1 : 1);    
    }

    return 0;
  }

  public static int evaluateContains(JDOContains jdoexpr, ObjectRelationalMappingIF mapping) {
    JDOValueIF left = jdoexpr.getLeft();
    JDOValueIF right = jdoexpr.getRight();

    //! System.out.println("El: " + isEvaluatable(left, mapping) + left);
    //! System.out.println("Er: " + isEvaluatable(left, mapping) + right);
    if (isEvaluatable(left, mapping) &&
        isEvaluatable(right, mapping)) {
      Collection lval = (Collection)evaluateJDOValue(left, mapping);
      Object rval = evaluateJDOValue(right, mapping);
      // NOTE: Using containsAll if rval is a collection
      if (rval instanceof Collection) {
        return (lval == null ? -1 : (lval.containsAll((Collection)rval) ? 1 : -1));
      } else {
        return (lval == null ? -1 : (lval.contains(rval) ? 1 : -1));
      }
    }
    return 0;
  }
  
  public static int evaluateIsEmpty(JDOIsEmpty jdoexpr, ObjectRelationalMappingIF mapping) {
    JDOValueIF value = jdoexpr.getValue();
    if (isEvaluatable(value, mapping)) {
      Collection coll = (Collection)evaluateJDOValue(value, mapping);
      return ((coll != null && coll.isEmpty()) ? 1 : -1);
    }
    return 0;
  }

  public static int evaluateStartsWith(JDOStartsWith jdoexpr, ObjectRelationalMappingIF mapping) {
    JDOValueIF left = jdoexpr.getLeft();
    JDOValueIF right = jdoexpr.getRight();
      
    // If both are string, perform string operation.
    if (isEvaluatable(left, mapping) &&
        isEvaluatable(right, mapping)) {
      String lval = (String)evaluateJDOValue(left, mapping);
      String rval = (String)evaluateJDOValue(right, mapping);
      return (lval == null ? -1 : (lval.startsWith(rval) ? 1 : -1));
      //! return (lval.startsWith(rval) ? 1 : -1);
    }
    return 0;
  }

  public static int evaluateEndsWith(JDOEndsWith jdoexpr, ObjectRelationalMappingIF mapping) {
    JDOValueIF left = jdoexpr.getLeft();
    JDOValueIF right = jdoexpr.getRight();
      
    // If both are string, perform string operation.
    if (isEvaluatable(left, mapping) &&
        isEvaluatable(right, mapping)) {
      String lval = (String)evaluateJDOValue(left, mapping);
      String rval = (String)evaluateJDOValue(right, mapping);
      return (lval == null ? -1 : (lval.endsWith(rval) ? 1 : -1));
      //! return (lval.endsWith(rval) ? 1 : -1);
    }
    return 0;
  }

  public static int evaluateLike(JDOLike jdoexpr, ObjectRelationalMappingIF mapping) {
    // TODO: Do some clever stuff here.
    return 0;
  }

  public static int evaluateAnd(JDOAnd jdoexpr, ObjectRelationalMappingIF mapping, boolean reduce) {
    // 1. remove all evaluatable expressions    
    // 2. AND - if item TRUE -> remove item
    //    AND - if item FALSE -> expression FALSE

    int removed = 0;
    
    JDOExpressionIF[] exprs = jdoexpr.getExpressions();
    for (int i=0; i < exprs.length; i++) {
      int result = evaluateExpression(exprs[i], mapping, reduce);
      switch (result) {
      case 0:
        // not evaluatable
        continue; // skip to next
      case 1:
        // true
        //! System.out.println("==> Removing: " + exprs[i]);
        exprs[i] = null; // remove item
        removed++;
        continue;
      case -1:
        // false
        return -1; // expression evaluates to false
      default:
        throw new OntopiaRuntimeException("Unknown result value: '" + result + "'");
      }
    }

    // If an item has been removed truncate the array.
    if (removed > 0) {
      JDOExpressionIF[] _exprs = new JDOExpressionIF[exprs.length - removed];
      int offset = 0;
      for (int i=0; i < exprs.length; i++) {
        if (exprs[i] != null) {
          _exprs[offset] = exprs[i];
          offset++;
        }
      }      
      
      // If no expressions remain, return true
      if (_exprs.length == 0) {
        return 1;
      }

      // Update AND-expression
      jdoexpr.setExpressions(_exprs);      
    }
    return 0;
  }

  public static int evaluateOr(JDOOr jdoexpr, ObjectRelationalMappingIF mapping, boolean reduce) {
    // 1. remove all evaluatable expressions
    // 2. OR - if item TRUE -> expression TRUE
    //    OR - if item FALSE -> remove item

    int removed = 0;
    
    JDOExpressionIF[] exprs = jdoexpr.getExpressions();
    for (int i=0; i < exprs.length; i++) {
      int result = evaluateExpression(exprs[i], mapping, reduce);
      switch (result) {
      case 0:
        // not evaluatable
        continue; // skip to next
      case -1:
        // false
        //! System.out.println("==> Removing: " + exprs[i]);
        exprs[i] = null; // remove item
        removed++;
        continue;
      case 1:
        // true
        return 1; // expression evaluates to true
      default:
        throw new OntopiaRuntimeException("Unknown result value: '" + result + "'");
      }
    }

    // If an item has been removed truncate the array.
    if (removed > 0) {
      JDOExpressionIF[] _exprs = new JDOExpressionIF[exprs.length - removed];
      int offset = 0;
      for (int i=0; i < exprs.length; i++) {
        if (exprs[i] != null) {
          _exprs[offset] = exprs[i];
          offset++;
        }
      }
      
      // If no expressions remain, return false
      if (_exprs.length == 0) {
        return -1;
      }
        
      // Update OR-expression
      jdoexpr.setExpressions(_exprs);
    }
    return 0;
  }

  public static int evaluateNot(JDONot jdoexpr, ObjectRelationalMappingIF mapping, boolean reduce) {
    int result = evaluateExpression(jdoexpr.getExpression(), mapping, reduce);
    switch (result) {
    case 0:
      // not evaluatable
      return 0;
    case -1:
      // false
      return 1;
    case 1:
      // true
      return -1;
    default:
      throw new OntopiaRuntimeException("Unknown result value: '" + result + "'");
    }
  }

  public static boolean isEvaluatable(JDOValueIF jdovalue, ObjectRelationalMappingIF mapping) {
    switch (jdovalue.getType()) {
    case JDOValueIF.OBJECT:
    case JDOValueIF.STRING:
    case JDOValueIF.COLLECTION:
      return true;
    case JDOValueIF.FIELD: {
      JDOField field = (JDOField)jdovalue;
      return field.getEvaluatable() && isEvaluatable(field.getRoot(), mapping);
    }
    }
    return false;
  }
  
  public static Object evaluateJDOValue(JDOValueIF jdovalue, ObjectRelationalMappingIF mapping) {
    switch (jdovalue.getType()) {
    case JDOValueIF.OBJECT:
      return ((JDOObject)jdovalue).getValue();
    case JDOValueIF.STRING:
      return ((JDOString)jdovalue).getValue();
    case JDOValueIF.COLLECTION:
      return ((JDOCollection)jdovalue).getValue();
    case JDOValueIF.FIELD: {

      JDOField field = (JDOField)jdovalue;
      JDOObject obj = (JDOObject)field.getRoot();

      // TODO: Only 1:1 fields supported.
      
      // Get object field value
      Object value = obj.getValue();
      Class ctype = value.getClass();
      //! System.out.println("OVALUE: " + value);
      FieldInfoIF finfo = null;
      
      String[] path = field.getPath();
      for (int i=0; i < path.length; i++) {
        // Make sure that field parent is of declared type
        if (mapping.isDeclared(ctype)) {
          ClassInfoIF cinfo = mapping.getClassInfo(ctype);
          finfo = cinfo.getFieldInfoByName(path[i]);
          
          if (finfo == null) {
            throw new OntopiaRuntimeException("Parent '" + ctype + "' do not have field called '" +
                                              path[i] + "'");
          }
          try {
            value = finfo.getValue(value);
          } catch (Exception e) {
            throw new OntopiaRuntimeException(e);
          }

          // We'll have to stop here if the returned value is null.
          if (value == null) {
            break;
          }
          
          ctype = finfo.getValueClass();
        }
        else {
          throw new OntopiaRuntimeException("Parent of field  '" + path[i] +
                                            "' of undeclared type: '" + ctype + "'");
        }
      }
      //! System.out.println("FVALUE: " + value);
      return value;
    }
    default:
      throw new OntopiaRuntimeException("Unsupported JDOValueIF: '" + jdovalue + "'");
    }
  }
  
}
