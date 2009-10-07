
// $Id$

package net.ontopia.topicmaps.query.parser;

import java.util.Map;
import java.util.HashMap;

import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.impl.utils.Argument;
import net.ontopia.topicmaps.impl.utils.ArgumentValidator;

/**
 * INTERNAL: Represents an UPDATE or DELETE statement, since these are
 * the ones that can have functions. Created so the parser can handle
 * both cases with the same code.
 */
public abstract class ModificationFunctionStatement
  extends ModificationStatement {
  protected String funcname; // name of delete function to be called, if any
  protected static Map<String, ModificationFunctionIF> functions;

  static {
    functions = new HashMap<String, ModificationFunctionIF>();
  }
  
  public ModificationFunctionStatement() {
    super();
  }

  public void setFunction(String name) {
    this.funcname = name;
  }

  public String getFunction() {
    return funcname;
  }

  // --- Internal helpers

  protected static ModificationFunctionIF makeFunction(String name)
    throws InvalidQueryException {
    ModificationFunctionIF function = functions.get(name);
    if (function == null)
      throw new InvalidQueryException("No such function: '" + name + "'");
    return function;
  }

  interface ModificationFunctionIF {
    public String getSignature();
    public void modify(TMObjectIF object, Object value);
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
      if (!reqarg.allows(arg.getClass()))
        throw new InvalidQueryException("Function " + function +
                                        " does not accept " +
                                        arg +
                                        "as parameter no " + no +
                                        ", but requires " +
                                        getClassList(reqarg.getTypes()));
    }
  }
}