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

package net.ontopia.topicmaps.impl.utils;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.query.parser.Pair;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Instances of this class represent a signature of some
 * predicate/function/object that takes a positional list of arguments
 * and can validate whether a given list of arguments are valid
 * according to the signature.
 */
public class ArgumentValidator {
  protected List<Argument> arguments;
  
  // --- TYPE MAP

  // used to get nice type names in error messages
  protected static Map<Class<?>, String> typenames;
  static {
    typenames = new HashMap<Class<?>, String>();
    typenames.put(net.ontopia.topicmaps.core.AssociationIF.class,
                  "an association");
    typenames.put(net.ontopia.topicmaps.core.TopicIF.class,
                  "a topic");
    typenames.put(net.ontopia.topicmaps.core.TopicMapIF.class,
                  "a topic map");
    typenames.put(net.ontopia.topicmaps.core.TopicNameIF.class,
                  "a base name");
    typenames.put(net.ontopia.topicmaps.core.VariantNameIF.class,
                  "a variant name");
    typenames.put(net.ontopia.topicmaps.core.OccurrenceIF.class,
                  "an occurrence");
    typenames.put(net.ontopia.topicmaps.core.AssociationRoleIF.class,
                  "an association role");
    typenames.put(net.ontopia.topicmaps.impl.basic.Association.class,
                  "an association");
    typenames.put(net.ontopia.topicmaps.impl.basic.Topic.class,
                  "a topic");
    typenames.put(net.ontopia.topicmaps.impl.basic.TopicMap.class,
                  "a topic map");
    typenames.put(net.ontopia.topicmaps.impl.basic.TopicName.class,
                  "a base name");
    typenames.put(net.ontopia.topicmaps.impl.basic.VariantName.class,
                  "a variant name");
    typenames.put(net.ontopia.topicmaps.impl.basic.Occurrence.class,
                  "an occurrence");
    typenames.put(net.ontopia.topicmaps.impl.basic.AssociationRole.class,
                  "an association role");
    typenames.put(String.class, "a string");

    // add RDBMS classes if we have them
    try {
      typenames.put(net.ontopia.topicmaps.impl.rdbms.Association.class,
                    "an association");
      typenames.put(net.ontopia.topicmaps.impl.rdbms.Topic.class,
                    "a topic");
      typenames.put(net.ontopia.topicmaps.impl.rdbms.TopicMap.class,
                    "a topic map");
      typenames.put(net.ontopia.topicmaps.impl.rdbms.TopicName.class,
                    "a base name");
      typenames.put(net.ontopia.topicmaps.impl.rdbms.VariantName.class,
                    "a variant name");
      typenames.put(net.ontopia.topicmaps.impl.rdbms.Occurrence.class,
                    "an occurrence");
      typenames.put(net.ontopia.topicmaps.impl.rdbms.AssociationRole.class,
                    "an association role");
    } catch (NoClassDefFoundError e) {
    }
  }

  /**
   * INTERNAL: Creates a validator for the signature represented by
   * this string.
   */
  public ArgumentValidator(String signature) {
    arguments = new ArrayList<Argument>();

    if (signature.length() == 0) {
      return; // otherwise we'll add an empty argument...
    }
      
    Argument curarg = new Argument();
    for (int ix = 0; ix < signature.length(); ix++) {
      if (signature.charAt(ix) == ' ') {
        arguments.add(curarg);
        curarg = new Argument();
      } else {
        interpretCharacter(signature.charAt(ix), curarg);
      }
    }
    arguments.add(curarg);
  }

  /**
   * INTERNAL: Interprets the given character and updates the argument
   * to record the information in the character.
   *
   * <pre>
   * x    TMObjectIF (equivalent to mtarbvo)
   * m    TopicMapIF
   * t    TopicIF
   * a    AssociationIF
   * r    AssociationRoleIF
   * b    TopicNameIF
   * v    VariantNameIF
   * o    OccurrenceIF
   * l    LocatorIF
   *
   * p    Pair (tolog-specific)
   * B    Boolean
   * s    String
   * n    Number (float + integer)
   * i    Integer
   * f    Float
   * .    Object
   * z    PredicateOptions (tolog-specific wizardry)
   *
   * ?    optional argument
   * +    repeatable argument
   * &    multiple values
   * !    tolog: argument must be bound; webed: argument must have a value
   * </pre>
   */
  public void interpretCharacter(char ch, Argument curarg) {
    switch(ch) {
    case 'm':
      curarg.addType(TopicMapIF.class); break;
    case 't':
      curarg.addType(TopicIF.class); break;
    case 'a':
      curarg.addType(AssociationIF.class); break;
    case 'r':
      curarg.addType(AssociationRoleIF.class); break;
    case 'b':
      curarg.addType(TopicNameIF.class); break;
    case 'v':
      curarg.addType(VariantNameIF.class); break;
    case 'o':
      curarg.addType(OccurrenceIF.class); break;
    case 'l':
      curarg.addType(LocatorIF.class); break;
    case 's':
      curarg.addType(String.class); break;
    case 'n':
      curarg.addType(Integer.class);
      curarg.addType(Float.class);
      break;
    case 'i':
      curarg.addType(Integer.class); break;
    case 'f':
      curarg.addType(Float.class); break;
    case 'B':
      curarg.addType(Boolean.class); break;
    case '.':
      curarg.addType(Object.class); break;
    case 'x':
      curarg.addType(TopicMapIF.class); 
      curarg.addType(TopicIF.class); 
      curarg.addType(TopicNameIF.class); 
      curarg.addType(VariantNameIF.class); 
      curarg.addType(OccurrenceIF.class); 
      curarg.addType(AssociationIF.class); 
      curarg.addType(AssociationRoleIF.class); 
      break;
    case '?':
      curarg.setOptional(); break;
    case '+':
      curarg.setRepeatable(); break;
    case '!':
      curarg.setMustBeBound(); break;
    case '&':
      curarg.setMultiValue(); break;
    default:
      throw new OntopiaRuntimeException("INTERNAL ERROR: Unknown type signature " +
                                        "character '" + ch + "' passed to " +
                                        getClass().getName());
    }
  }

  public Class[] getTypes(int ix) {
    if (ix >= arguments.size()) {
      ix = arguments.size() - 1;
    }
    Argument arg = arguments.get(ix);
    return arg.getTypes();
  }

  /**
   * INTERNAL: Returns an Argument object representing the argument at
   * this position in the signature.
   */
  public Argument getArgument(int ix) {
    if (ix >= arguments.size()) {
      return null;
    } else {
      return arguments.get(ix);
    }
  }

  /**
   * INTERNAL: Turns a list of class types into a signature string.
   * @param types The list of class types.
   * @return The generated signature string.
   */
  public static String makeSignature(Object[] types) {
    char[] sign = new char[types.length];
    for (int ix = 0; ix < types.length; ix++) {
      Class type = (Class) types[ix];
      
      if (type.equals(TopicMapIF.class)) {
        sign[ix] = 'm';
      } else if (type.equals(TopicIF.class)) {
        sign[ix] = 't';
      } else if (type.equals(TopicNameIF.class)) {
        sign[ix] = 'b';
      } else if (type.equals(VariantNameIF.class)) {
        sign[ix] = 'v';
      } else if (type.equals(OccurrenceIF.class)) {
        sign[ix] = 'o';
      } else if (type.equals(AssociationIF.class)) {
        sign[ix] = 'a';
      } else if (type.equals(AssociationRoleIF.class)) {
        sign[ix] = 'r';
      } else if (type.equals(LocatorIF.class)) {
        sign[ix] = 'l';
      } else if (type.equals(String.class)) {
        sign[ix] = 's';
      } else if (type.equals(Number.class)) {
        sign[ix] = 'n';
      } else if (type.equals(Integer.class)) {
        sign[ix] = 'i';
      } else if (type.equals(Float.class)) {
        sign[ix] = 'f';
      } else if (type.equals(Object.class)) {
        sign[ix] = '.';
      } else if (type.equals(Pair.class)) {
        sign[ix] = 'p';
      } else {
        throw new OntopiaRuntimeException("INTERNAL ERROR: Unknown type " +
                                          type);      
      }      
    }
  
    return new String(sign);
  }

  // --- ERROR MESSAGE METHODS

  /**
   * INTERNAL: Returns a string name for the class.
   */
  public static String getClassName(Class klass) {
    String classname = typenames.get(klass);
    if (classname == null) {
      classname = klass.toString();
    }
    return classname;
  }

  /**
   * INTERNAL: Returns a string name for the class of this object.
   */
  public static String getClassName(Object object) {
    return getClassName(object.getClass());
  }

  /**
   * INTERNAL: Returns a nicely formatted list of class names.
   */
  public static String getClassList(Object[] classes) {
    StringBuilder buf = new StringBuilder();
    for (int ix = 0; ix < classes.length; ix++) {
      buf.append(getClassName((Class) classes[ix]));
      if (ix + 2 < classes.length) {
        buf.append(", ");
      } else if (ix + 1 < classes.length) {
        buf.append(", or ");
      }
    }

    return buf.toString();
  }
}
