/*
 * #!
 * Ontopia Webed
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

package net.ontopia.topicmaps.webed.impl.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.ontopia.topicmaps.impl.utils.Argument;
import net.ontopia.topicmaps.impl.utils.ArgumentValidator;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;

/**
 * INTERNAL.
 */
public class ActionSignature extends ArgumentValidator {
  private static Map cache = new HashMap(); // used to avoid having to reparse
    
  // static method to get signature from cache
  public static ActionSignature getSignature(String sign)
    throws ActionRuntimeException {
    
    ActionSignature signature = (ActionSignature) cache.get(sign);
    
    if (signature == null) {
      signature = new ActionSignature(sign);
      cache.put(sign, signature);
    }
    
    return signature;
  }
  
  // constructor
  private ActionSignature(String signature) {
    super(signature);
  }


  public void validateArguments(ActionParametersIF params, ActionIF action)
    throws ActionRuntimeException {

    String acname = action.getClass().getName();
    int paramNum = 0;
    boolean repeated = false;
    
    for (int i = 0; i < params.getParameterCount(); i ++) {
      Argument currarg = getArgument(paramNum);
      if (currarg == null)
        throw new ActionRuntimeException("Too many arguments to  " + acname
                                         + ", got " + (i+1) 
                                         + ", wanted " + paramNum);

      // get values
      Collection mcurrparam = params.getCollection(i);

      // check if it has to have a value
      if (currarg.mustBeBound() && mcurrparam.isEmpty())
        throw new ActionRuntimeException("Empty collection used argument " + i +
                                         " to action " + acname);
      
      // check if multivalue is allowed
      if (!currarg.isMultiValue() && mcurrparam.size() > 1)
        throw new ActionRuntimeException("Got collection as parameter to " +
                                         "action " + acname);

      // check each value to see if it's OK
      Iterator it = mcurrparam.iterator();
      while (it.hasNext()) {
        Object currparam = it.next();
        if (currparam == null && !(currarg.isOptional()))
          throw new ActionRuntimeException("Argument " + i + " to " +
                                           acname +
                                           " was null, but is not optional");
        
        if (currparam != null && !currarg.allows(currparam.getClass()))
          throw new ActionRuntimeException(getClassName(currparam) +
                                           " used as argument " + i + " to " +
                                           acname + " which requires " +
                                           getClassList(currarg.getTypes()));
      }
    
      if (!currarg.isRepeatable())
        paramNum++;
      else
        repeated = true;
    }
    
    Argument curarg = getArgument(paramNum);
    if (curarg != null && !curarg.isOptional() && !repeated)
      throw new ActionRuntimeException("Not enough arguments to action " +
                                      acname);
  }

  public Class[] getTypes(int ix) {
    if (ix >= arguments.size())
      ix = arguments.size() - 1;
    Argument arg = (Argument) arguments.get(ix);
    return arg.getTypes();
  }
  
  public Argument getArgument(int ix) {
    if (ix >= arguments.size())
      return null;
    else
      return (Argument) arguments.get(ix);
  }

}
