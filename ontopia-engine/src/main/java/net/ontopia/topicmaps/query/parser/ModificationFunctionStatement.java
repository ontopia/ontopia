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

package net.ontopia.topicmaps.query.parser;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Collections;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.impl.utils.Argument;
import net.ontopia.topicmaps.impl.utils.ArgumentValidator;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;
import net.ontopia.topicmaps.query.impl.basic.QueryContext;
import net.ontopia.topicmaps.query.impl.utils.QueryMatchesUtils;

/**
 * INTERNAL: Represents an UPDATE or DELETE statement, since these are
 * the ones that can have functions. Created so the parser can handle
 * both cases with the same code.
 */
public abstract class ModificationFunctionStatement
  extends ModificationStatement {
  protected String funcname; // name of delete function to be called, if any
  protected static Map<String, ModificationFunctionIF> functions = new HashMap<String, ModificationFunctionIF>();

  public void setFunction(String name) {
    this.funcname = name;
  }

  public String getFunction() {
    return funcname;
  }

  // --- Internal helpers

  protected String toStringFunction() {
    return funcname + "(" + toStringLitlist() + ")";
  }

  @Override
  public int doStaticUpdates(TopicMapIF topicmap, Map arguments)
    throws InvalidQueryException {
    if (funcname == null) {
      return doLitListDeletes(true, arguments);
    } else {
      // in order to avoid duplicating code we produce a "fake" matches
      // object here, so that in effect we're simulating a one-row zero-column
      // result set
      QueryContext context = new QueryContext(null, null, arguments, null);
      Collection columns = arguments == null ? Collections.EMPTY_SET :
                                               arguments.values();
      QueryMatches matches =
        QueryMatchesUtils.createInitialMatches(context, columns);
      return doFunctionUpdates(matches);
    }
  }

  protected abstract int doLitListDeletes(boolean strict, Map arguments)
    throws InvalidQueryException;
  
  // generic method for traversing result set and calling functions
  protected int doFunctionUpdates(QueryMatches matches) 
    throws InvalidQueryException {
    int rows = 0;

    ModificationFunctionIF function = makeFunction(funcname);
    FunctionSignature signature = FunctionSignature.getSignature(function);
    QueryContext context = matches.getQueryContext();
    Map parameters = Collections.EMPTY_MAP;
    if (context != null) {
      parameters = context.getParameters();
    }
    Object arg1 = getValue(litlist.get(0), parameters);
    int varix1 = getIndex(arg1, matches);
    Object arg2 = getValue(litlist.get(1), parameters);
    int varix2 = getIndex(arg2, matches);
    
    for (int row = 0; row <= matches.last; row++) {
      if (varix1 != -1) {
        arg1 = matches.data[row][varix1];
      }

      if (varix2 != -1) {
        arg2 = matches.data[row][varix2];
      }

      signature.validateArguments(arg1, arg2, funcname);
      function.modify((TMObjectIF) arg1, arg2);
      rows++;
    }

    return rows;
  }

  // --- Functions and signatures
  
  protected static ModificationFunctionIF makeFunction(String name)
    throws InvalidQueryException {
    ModificationFunctionIF function = functions.get(name);
    if (function == null) {
      throw new InvalidQueryException("No such function: '" + name + "'");
    }
    return function;
  }

  interface ModificationFunctionIF {
    String getSignature();
    void modify(TMObjectIF object, Object value);
  }
  
  static class FunctionSignature extends ArgumentValidator {
    private static Map cache = new HashMap(); // used to avoid having to reparse

    public static FunctionSignature getSignature(ModificationFunctionIF function)
    throws InvalidQueryException {

      String sign = function.getSignature();
      FunctionSignature signature = (FunctionSignature) cache.get(sign);

      if (signature == null) {
        signature = new FunctionSignature(sign);
        cache.put(sign, signature);
      }
    
      return signature;
    }
  
    private FunctionSignature(String signature) {
      super(signature);
    }

    public void validateArguments(Object arg1, Object arg2, String function)
      throws InvalidQueryException {
      check(arg1, getArgument(0), function, 1);
      check(arg2, getArgument(1), function, 2);
    }

    public void check(Object arg, Argument reqarg, String function, int no)
      throws InvalidQueryException {
      if (!reqarg.allows(arg.getClass())) {
        throw new InvalidQueryException("Function " + function +
                                        " does not accept " +
                                        arg +
                                        " as parameter no " + no +
                                        ", but requires " +
                                        getClassList(reqarg.getTypes()));
      }
    }
  }
}