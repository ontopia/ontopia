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

import java.util.HashMap;
import java.util.Map;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.impl.utils.Argument;
import net.ontopia.topicmaps.impl.utils.ArgumentValidator;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;
import net.ontopia.topicmaps.query.parser.Pair;
import net.ontopia.topicmaps.query.parser.Parameter;
import net.ontopia.topicmaps.query.parser.PredicateIF;
import net.ontopia.topicmaps.query.parser.Variable;

public class PredicateSignature extends ArgumentValidator {
  private static Map cache = new HashMap(); // used to avoid having to reparse

  public static PredicateSignature getSignature(PredicateIF predicate)
    throws InvalidQueryException {

    String sign = predicate.getSignature();
    PredicateSignature signature = (PredicateSignature) cache.get(sign);

    if (signature == null) {
      signature = new PredicateSignature(sign);
      cache.put(sign, signature);
    }
    
    return signature;
  }
  
  private PredicateSignature(String signature) {
    super(signature);
  }

  @Override
  public void interpretCharacter(char ch, Argument curarg) {
    if (ch == 'p') {
      curarg.addType(Pair.class);
    } else if (ch == 'z') {
      curarg.addType(PredicateOptions.class);
    } else {
      super.interpretCharacter(ch, curarg);
    }
  }

  /**
   * Validates the arguments to this predicate. If strict the types of
   * arguments are also checked.
   */
  public void validateArguments(Object[] args, String predicate, boolean strict)
    throws InvalidQueryException {
    if (args.length == 0 && arguments.size() == 0) {
      return; // special case
    }
      
    int arg = 0;
    boolean repeated = false;
            
    for (int ix = 0; ix < args.length; ix++) {
      Argument curarg = getArgument(arg);
      if (curarg == null) {
        throw new InvalidQueryException("Too many arguments to predicate " +
                                        predicate + ", got " + (ix+1) +
                                        ", wanted " + arg);
      }

      if (!(args[ix] instanceof Pair) && curarg.requires(Pair.class)) {
        throw new InvalidQueryException("Predicate " + predicate + " requires " +
                                        "pair as argument, got " + args[ix]);
      }
      if (args[ix] instanceof Pair && curarg.allows(Pair.class)) {
        // the first member of the pair must be a topic
        Object value = ((Pair) args[ix]).getFirst();
        if (strict &&
            !(value instanceof Variable || value instanceof TopicIF ||
              value instanceof Parameter)) {
          throw new InvalidQueryException("First member of pair argument to " +
                                          predicate + " was not a topic");
        }
      }
      if (strict &&
          !(args[ix] instanceof Variable || args[ix] instanceof Parameter) &&
          !curarg.allows(args[ix].getClass())) {
        throw new InvalidQueryException("Predicate " + predicate + " received " +
                                        getClassName(args[ix]) + " as argument " +
                                        (ix+1) + ", but requires " +
                                        getClassList(curarg.getTypes()));
      }
        
      if (!curarg.isRepeatable()) {
        arg++;
      } else {
        repeated = true;
      }
    }

    Argument curarg = getArgument(arg);
    if (curarg != null && !curarg.isOptional() && !repeated) {
      throw new InvalidQueryException("Not enough arguments to predicate " +
                                      predicate);
    }
  }

  /**
   * INTERNAL: Verifies that arguments which are required to be bound
   * actually are bound.
   */
  public void verifyBound(QueryMatches matches, Object[] arguments,
                          PredicateIF predicate)
    throws InvalidQueryException {

    Argument lastArg = null;
    for (int ix = 0; ix < arguments.length; ix++) {
      Argument arg = getArgument(ix);
      // NOTE: arg can be null if last argument is repeatable
      if ((arg != null && arg.mustBeBound()) ||
          (arg == null && lastArg != null && lastArg.isRepeatable() && lastArg.mustBeBound())) {
        int pos = matches.getIndex(arguments[ix]);
        if (!matches.bound(pos)) {
          throw new InvalidQueryException("Variable " + matches.columnDefinitions[pos] +
                                          " not bound in predicate " +
                                          predicate.getName());
        }
      }
      if (arg != null) {
        lastArg = arg;
      }
    }
  }
}
