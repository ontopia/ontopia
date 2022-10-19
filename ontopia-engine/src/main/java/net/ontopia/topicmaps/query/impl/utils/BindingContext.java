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

package net.ontopia.topicmaps.query.impl.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.parser.Pair;
import net.ontopia.topicmaps.query.parser.Parameter;
import net.ontopia.topicmaps.query.parser.PredicateIF;
import net.ontopia.topicmaps.query.parser.Variable;
import net.ontopia.utils.CompactHashSet;

/**
 * INTERNAL.
 */
public class BindingContext {

  // whether types are checked for correctness
  protected boolean typecheck;
  
  // Variable types: varname : Class[]
  protected Map vtypes;

  // Variable type theories: varname : Class[]
  protected Map vtypetheory; // see assymetric merging below
  
  // Parameter types: parname : Class[]
  protected Map ptypes;

  // Parameter type theories: parname : Class[]
  protected Map ptypetheory; // see assymetric merging below
  
  public BindingContext(boolean typecheck) {
    this.typecheck = typecheck;
    this.vtypes = new HashMap();
    this.ptypes = new HashMap();
    this.vtypetheory = new HashMap();
    this.ptypetheory = new HashMap();
  }

  public boolean getCheckingTypes() {
    return typecheck;
  }

  public Map getVariableTypes() {
    return mergeMaps(vtypes, vtypetheory);
  }

  public Map getParameterTypes() {
    return mergeMaps(ptypes, ptypetheory);
  }

  private Map mergeMaps(Map main, Map theory) {
    Map merged = new HashMap(theory);
    Iterator it = main.keySet().iterator();
    while (it.hasNext()) {
      Object k = it.next();
      merged.put(k, main.get(k));
    }
    return merged;
  }
  
  // PredicateIF callbacks
  
  public void addArgumentTypes(Object argument, Class[] types, PredicateIF predicate)
    throws InvalidQueryException {

    if (argument instanceof Pair)
      argument = ((Pair) argument).getFirst();

    if (argument instanceof Variable)
      _addVariableTypes(((Variable) argument).getName(), types, predicate);
    else if (argument instanceof Parameter)
      _addParameterTypes(((Parameter) argument).getName(), types, predicate);
    // else literal, so we don't care
  }

  // FIXME:
  // would be nice to merge _addVariableTypes and _addParameterTypes;
  // this makes for slight difficulties with the error messages, however.
  // solution: common superclass for parameters and variables (or even
  // merge them into one class with a flag to tell you what it is)
  
  private void _addVariableTypes(String varname, Object[] types,
                                 PredicateIF predicate)
    throws InvalidQueryException {
    
    // Register variable types in this context
    Object[] newtypes = types;
    Object[] etypes = (Object[]) vtypes.get(varname);
    if (etypes != null) {
      newtypes = intersect(etypes, types);
      checkForTypeConflict("$" + varname, etypes, types, newtypes, predicate);
    }
    vtypes.put(varname, newtypes);
  }

  private void _addParameterTypes(String parname, Object[] types,
                                 PredicateIF predicate)
    throws InvalidQueryException {
    
    // Register parameter types in this context
    Object[] newtypes = types;
    Object[] etypes = (Object[]) ptypes.get(parname);
    if (etypes != null) {
      newtypes = intersect(etypes, types);
      checkForTypeConflict("%" + parname + "%", etypes, types, newtypes, predicate);
    }
    ptypes.put(parname, newtypes);
  }

  private void checkForTypeConflict(String name, Object[] types1, Object[] types2,
                                    Object[] newtypes, PredicateIF predicate)
    throws InvalidQueryException {

    if (newtypes.length == 0 && typecheck)
      throw new InvalidQueryException("Type conflict on " + name + ": cannot " +
                                      "be both " +
                                      PredicateSignature.getClassList(types1) +
                                      " and, as required by " +
                                      "predicate '" + predicate.getName() + "', " +
                                      PredicateSignature.getClassList(types2));
  }

  // --- Modifiers

  // no need to take theories into account here; child theories get discarded anyway
  public void mergeIntersect(BindingContext bc)
    throws InvalidQueryException {
    this.vtypes = mergeTypeMapsIntersect(this.vtypes, bc.vtypes, true);
    this.ptypes = mergeTypeMapsIntersect(this.ptypes, bc.ptypes, false);    
    this.vtypetheory = mergeTypeMapsIntersect(this.vtypetheory, bc.vtypetheory, true);
    this.ptypetheory = mergeTypeMapsIntersect(this.ptypetheory, bc.ptypetheory, false);
  }

  protected Map mergeTypeMapsIntersect(Map map1, Map map2, boolean variables)
    throws InvalidQueryException {
    Map result = new HashMap(map1);
    
    Iterator iter = map2.keySet().iterator();
    while (iter.hasNext()) {
      Object key = iter.next();
      if (map1.containsKey(key)) {
        // Merge type lists
        Object[] types1 = (Object[])map1.get(key);
        Object[] types2 = (Object[])map2.get(key);

        Object[] newtypes = intersect(types1, types2);
        if (newtypes.length == 0 && typecheck) {
          String name = "$" + key;
          if (!variables)
            name = "%" + key + "%";
          
          throw new InvalidQueryException("Type conflict on " + name + ": cannot " +
                                          "be both " +
                                          PredicateSignature.getClassList(types1) +
                                          " and " +
                                          PredicateSignature.getClassList(types2));
        }
        result.put(key, newtypes);
      } else
        result.put(key, (Object[]) map2.get(key));
    }
    return result;
  }

  // no need to take theories into account here; child theories get discarded anyway
  public void mergeUnion(BindingContext bc) {
    this.vtypes = mergeTypeMapsUnion(this.vtypes, bc.vtypes);
    this.ptypes = mergeTypeMapsUnion(this.ptypes, bc.ptypes);
    this.vtypetheory = mergeTypeMapsUnion(this.vtypetheory, bc.vtypetheory);
    this.ptypetheory = mergeTypeMapsUnion(this.ptypetheory, bc.ptypetheory);
  }

  protected Map mergeTypeMapsUnion(Map map1, Map map2) {
    Map result = new HashMap(map1);
    
    Iterator iter = map2.keySet().iterator();
    while (iter.hasNext()) {
      Object key = iter.next();
      if (map1.containsKey(key)) {
        // Merge type lists
        Object[] types1 = (Object[]) map1.get(key);
        Object[] types2 = (Object[]) map2.get(key);
        result.put(key, union(types1, types2));          
      } else
        result.put(key, (Object[])map2.get(key));
    }
    return result;
  }

  // type information in this bc is certain, whereas type information
  // from the other context is just a theory. information from the
  // other bc therefore has to yield to information from this one
  // whenever there is any, whereas when there is none in this one
  // information is retained as a theory. information from different
  // theories is unioned.
  public void mergeAssymetric(BindingContext bc) {
    mergeTypeMapsAssymetric(vtypes, bc.vtypes, vtypetheory, bc.vtypetheory);
    mergeTypeMapsAssymetric(ptypes, bc.ptypes, ptypetheory, bc.ptypetheory);
  }

  // updates map1 and theory1
  protected void mergeTypeMapsAssymetric(Map map1, Map map2,
                                         Map theory1, Map theory2) {
    // do the certain stuff first
    Iterator iter = map2.keySet().iterator();
    while (iter.hasNext()) {
      Object key = iter.next();
      Object[] types2 = (Object[]) map2.get(key);
      if (!map1.containsKey(key)) {
        Object[] existing = (Object[]) theory1.get(key);
        if (existing != null)
          types2 = union(existing, types2);
        theory1.put(key, types2);
      }
    }

    // then deal with the theory (bug #1606)
    iter = theory2.keySet().iterator();
    while (iter.hasNext()) {
      Object key = iter.next();
      // if nothing's known about this variable, record it as a theory
      if (!map1.containsKey(key) && !theory1.containsKey(key))
        theory1.put(key, theory2.get(key));
    }
  }

  // --- Set operations

  // RULES:
  //  - all types exclude each other, except Object, which excludes itself
  //  - Object will only ever appear alone
  
  protected static Object[] intersect(Object[] array1, Object[] array2) {
    if (array1.length == 1 && array1[0].equals(Object.class))
      return array2;
    if (array2.length == 1 && array2[0].equals(Object.class))
      return array1;
    
    int matches = 0;
    // Result array cannot be bigger than the shortest input array
    Object[] tresult = new Object[Math.min(array1.length, array2.length)];
    
    // Look up array2 objects
    for (int i = 0; i < array2.length; i++) {
      boolean found = false;
      for (int ix = 0; !found && ix < array1.length; ix++)
        found = array2[i].equals(array1[ix]);
        
      if (found) {
        tresult[matches] = array2[i];
        matches++;        
      }
    }    
    Object[] result = new Object[matches];
    System.arraycopy(tresult, 0, result, 0, matches);
    return result;
  }

  protected static Object[] union(Object[] array1, Object[] array2) {
    Set result = new CompactHashSet();

    boolean seenobject = false;    
    for (int i=0; i < array1.length; i++) {
      seenobject |= array1[i].equals(Object.class);
      result.add(array1[i]);
    }
    for (int i=0; i < array2.length; i++) {
      seenobject |= array2[i].equals(Object.class);
      result.add(array2[i]);
    }

    if (seenobject)
      result = Collections.singleton(Object.class); // least specific info wins

    return result.toArray();
  }
  
  // --- Misc
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append('{');
    if (vtypes != null) {
      // Variable types
      sb.append("V[");
      Iterator i = vtypes.entrySet().iterator();
      while (i.hasNext()) {
        Map.Entry entry = (Map.Entry)i.next();
        sb.append(entry.getKey());
        sb.append('=');
        Object value = entry.getValue();
        if (value != null && value.getClass().isArray())
          sb.append(Arrays.asList((Object[])value));
        else
          sb.append(value);
      }
      sb.append(']');
    }
    if (ptypes != null) {
      // Parameter types
      sb.append("P[");
      Iterator i = ptypes.entrySet().iterator();
      while (i.hasNext()) {
        Map.Entry entry = (Map.Entry)i.next();
        sb.append(entry.getKey());
        sb.append('=');
        Object value = entry.getValue();
        if (value != null && value.getClass().isArray())
          sb.append(Arrays.asList((Object[])value));
        else
          sb.append(value);
      }
      sb.append(']');
    }
    sb.append('}');
    return sb.toString();
  }
}
