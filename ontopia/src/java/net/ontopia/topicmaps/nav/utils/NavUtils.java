// $Id: NavUtils.java,v 1.19 2008/06/13 08:36:27 geir.gronmo Exp $

package net.ontopia.topicmaps.nav.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.StringTokenizer;
import java.net.MalformedURLException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.utils.OntopiaRuntimeException;

import org.apache.log4j.Logger;

/**
 * INTERNAL: A utility class with miscellaneous methods used by the
 * navigator framework.
 */
public class NavUtils {
  
  // Define a logging category.
  static Logger log = Logger.getLogger(NavUtils.class.getName());

  /**
   * INTERNAL: Escapes XML's predefined entities.
   */
  public static String escapeXMLEntities(String s) {
    char[] ch = s.toCharArray();
    StringBuffer content = new StringBuffer(s.length() + 10);

    for (int i = 0; i < ch.length; i++) {
      switch(ch[i]) {
      case '&':
        content.append("&amp;");
        break;
      case '<':
        content.append("&lt;");
        break;
      case '>':
        content.append("&gt;");
        break;
      case '\'':
        content.append("&apos;");
        break;   
      case '\"':
        content.append("&quot;");
        break;           
      default:
        content.append(ch[i]);     
      }
    }
    return content.toString();
  }  

  /**
   * INTERNAL: Escapes HTML's predefined entities.
   */
  public static String escapeHTMLEntities(String s) {
    char[] ch = s.toCharArray();
    StringBuffer content = new StringBuffer(s.length() + 10);
    for (int i = 0; i < ch.length; i++) {
      switch(ch[i]) {
      case '&':
        content.append("&amp;");
        break;
      case '<':
        content.append("&lt;");
        break;
      case '>':
        content.append("&gt;");
        break;  
      case '\"':
        content.append("&quot;");
        break;           
      default:
        content.append(ch[i]);     
      }
    }
    return content.toString();
  }    
  
  /**
   * INTERNAL: Converts FROM a string of subject identities source IDs or
   * object IDs separated by spaces TO a Collection of topics.
   * 
   * @param tm      the topic map
   * @param args    separated list of identities and/or ids
   * @param divider a character which separates the list
   * @return A Collection of matched topics in the order they were listed.
   *         Arguments that do not match will be ignored.
   */
  public static Collection args2Topics(TopicMapIF tm, String args, String divider) {
    ArrayList list = new ArrayList();
    StringTokenizer st;

    if (args == null || tm == null || args.equals(""))
      return list; // empty

    if (divider == null) 
      st = new StringTokenizer(args);
    else
      st = new StringTokenizer(args, divider);

    while (st.hasMoreTokens()) {
      TopicIF t = null;
      String s = st.nextToken();

      // 1. try to match a subject indicator
      try {
        t = (TopicIF) tm.getTopicBySubjectIdentifier(new URILocator(s));
      } catch (MalformedURLException e) {
        // apparently not a URL, so try something else
      }
        
      // 2. try to match a source
      if (t == null) {
        try {
          LocatorIF uri = tm.getStore().getBaseAddress();
          if (uri != null) {
            uri = uri.resolveAbsolute("#" + s);
            t = (TopicIF) tm.getObjectByItemIdentifier(uri);
          }
        } catch (ClassCastException e) {
          // if it's not a topic  we don't want it
        }
      }
        
      // 3.try to match a topic id
      if (t == null) {
        try {
          t = (TopicIF) tm.getObjectById(s);
        } catch (ClassCastException e) {
          // it's ok; we don't want it if it's not a topic
        }
      }

      if (t != null)
        list.add(t);
    } // while
    
    return list;
  }
  
  /** 
   * Returns an object when requested by classname by two means:
   * <ul>
   * <li>if args are null, try to get a valid instance for <code>s</code>
   *     out of <code>m</code> ("quick instantiation")
   * <li>otherwise get all available constructors for this class
   *     and try them one after each other in case of failure
   *     ("slow instantiation")
   * 
   * @param s        string representing the full java class name
   * @param args[]   array of parameter values for construction
   * @param m        Map which contains as keys String of full
   *                 java class names and as values object instances
   * @return Object instance
   */
  public static Object class2Object(String s, Object[] args, Map m)  {
    Object object;
    Class c;
    Constructor constructor;
    if (s==null || s.equals(""))
      // No class passed in
      return null;

    if (args == null && m != null) {
      // No arguments, look in map
      object = m.get(s);
      if (object!=null) {
        return object;
      } else {
        // Not in map, quick instantiation
        try {
          c = Class.forName(s);
          try {
            return c.newInstance();
          } catch (InstantiationException e2){
            throw new OntopiaRuntimeException("NavUtils.class2Object Error: InstantiationException (empty constructor): " + s + ".");
          } catch (IllegalAccessException e3){
            throw new OntopiaRuntimeException("NavUtils.class2Object Error: IllegalAccessException (empty constructor): " + s + ".");
          } catch (IllegalArgumentException e4){
            throw new OntopiaRuntimeException("NavUtils.class2Object Error: IllegalArgumentException (empty constructor): " + s + ".");
          } 
        } catch (ClassNotFoundException e1){
          throw new OntopiaRuntimeException("NavUtils.class2Object Error: ClassNotFoundException (empty constructor): " + s + ".");
        }       
      }
    } else {
      // need to construct
      try {
        c = Class.forName(s);
        try {
          Constructor[] constructors = c.getConstructors();
          // FIXME: This could probably optimized
          for (int i=0; i < constructors.length; i++) {
            constructor = constructors[i];
            try {
              //System.out.println("instantiate: slow: " + s);
              return object = constructor.newInstance(args);
            } catch (InstantiationException e3){
              throw new OntopiaRuntimeException("NavUtils.class2Object Error: InstantiationException: " + s + ".");
            } catch (IllegalAccessException e4){
              throw new OntopiaRuntimeException("NavUtils.class2Object Error: IllegalAccessException: " + s + ".");
            } catch (IllegalArgumentException e5){
              // No warning required because classes with more than one constructor are allowed;
            } catch (InvocationTargetException e6){
              throw new OntopiaRuntimeException(e6.getTargetException());
            }                                    
          }     
        } catch (SecurityException e2){
          throw new OntopiaRuntimeException("NavUtils.class2Object Error: SecurityException: " + s + ".");
        }
      } catch (ClassNotFoundException e1){
        throw new OntopiaRuntimeException("NavUtils.class2Object Error: ClassNotFoundException: " + s + ".");
      }
      return null;
    }
  }
  
}





