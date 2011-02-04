
// $Id: ObjectUtils.java,v 1.15 2008/09/29 08:52:41 geir.gronmo Exp $

package net.ontopia.utils;

import java.util.*;
import java.lang.reflect.*;

/**
 * INTERNAL: Class that contains useful methods.
 */
public class ObjectUtils {

  private ObjectUtils() {
  }

  /**
   * INTERNAL: Returns true if the two objects are different. The
   * method handle null values without throwing a
   * NullPointerException. Null equals null.
   */
  public static boolean different(Object o1, Object o2) {
    return !(o1 == null ? o2 == null : o1.equals(o2));
  }

  /**
   * INTERNAL: Returns true if the two objects are equals. The method
   * handle null values without throwing a NullPointerException. Null
   * equals null.
   */
  public static boolean equals(Object o1, Object o2) {
    return (o1 == null ? o2 == null : o1.equals(o2));
  }

  /**
   * INTERNAL: Compares two int values.
   */
  public static int compare(int i1, int i2) {
      return (i1 > i2 ? 1 : (i2 > i1 ?  -1 : 0));
  }


  /**
   * INTERNAL: Compares two double values. Note that this method is
   * included in Java 1.4, but we've included it here because we have
   * to support Java 1.3.
   */
  public static int compare(double d1, double d2) {
    if (d1 < d2) return -1;
    if (d1 > d2) return 1;    
    long d1Bits = Double.doubleToLongBits(d1);
    long d2Bits = Double.doubleToLongBits(d2);    
    return (d1Bits == d2Bits ?  0 : (d1Bits < d2Bits ? -1 : 1));
  }

  /**
   * INTERNAL: Compares two string or null values in a consistent way
   * ignoring the case. Will not throw a NullPointerException if any
   * of the arguments are null.
   *
   * @since 3.4
   */
  public static int compareIgnoreCase(String s1, String s2) {
    if (s1 == null)
      return (s2 == null ? 0 : -1);
    else if (s2 == null)
      return 1;
    else
      return s1.compareToIgnoreCase(s2);
  }

  /**
   * INTERNAL: Compares two objects or null values in a consistent
   * way. Will not throw a NullPointerException if any of the
   * arguments are null.
   *
   * @since 3.4
   */
  public static int compare(Comparable o1, Comparable o2) {
    if (o1 == null)
      return (o2 == null ? 0 : -1);
    else if (o2 == null)
      return 1;
    else
      return o1.compareTo(o2);
  }

  /**
   * INTERNAL: Compares two objects or null values in a consistent way
   * using the given comparator. Will not throw a NullPointerException
   * if any of the arguments are null.
   *
   * @since 3.4
   */
  public static int compare(Object o1, Object o2, Comparator c) {
    if (o1 == null)
      return (o2 == null ? 0 : -1);
    else if (o2 == null)
      return 1;
    else
      return c.compare(o1, o2);
  }
  
  /**
   * INTERNAL: Get bean property value from object.
   */
  public static Object getProperty(Object bean, String propertyName) {    
    try {
      // Lookup getter method
      String methodName = "get" + propertyName.substring(0,1).toUpperCase() + propertyName.substring(1);
      Method method = bean.getClass().getMethod(methodName, new Class[] {});
      // Invoke getter method
      return method.invoke(bean, new Object[] {});
    }
    catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }
  
  /**
   * INTERNAL: Get bean property value from object. Returns null if property does not exist or fails.
   */
  public static Object getProperty(Object bean, String propertyName, Object _default) {
    try {
      // Lookup getter method
      String methodName = "get" + propertyName.substring(0,1).toUpperCase() + propertyName.substring(1);
      Method method = bean.getClass().getMethod(methodName, new Class[] {});
      // Invoke getter method
      return method.invoke(bean, new Object[] {});
    } catch (Exception e) {
      return _default;
    }
  }

  /**
   * INTERNAL: Returns boolean value if Boolean object. If not returns false.
   */
  public static boolean isTrue(Object value) {
    if (value instanceof Boolean)
      return ((Boolean)value).booleanValue();
    else
      return false;
  }

  /**
   * INTERNAL: Casts string to int value, returns default if it fails.
   */
  public static int toInteger(String value, int _default) {
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      return _default;
    }
  }

  /**
   * INTERNAL: Create new instance of given class. Class must have a
   * default constructor.
   */
  public static Object newInstance(String className) {    
    try {
      Class klass = Class.forName(className);
      return klass.newInstance();
    }
    catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  /**
   * INTERNAL: Extract real exception from wrapper exception.
   */
  public static Throwable getRealCause(Throwable t) {
    Throwable cause;
    if (t instanceof OntopiaRuntimeException)
      cause = ((OntopiaRuntimeException)t).getCause();
    else if (t instanceof org.xml.sax.SAXException)
      cause = ((org.xml.sax.SAXException)t).getException();
    else
      cause = null; // may want to support 1.4 getCause method
    
    if (cause != null)
      return getRealCause(cause);
    else
      return t;
  }

  /**
   * INTERNAL: Extract real exception from wrapper exception and
   * rethrow as a RuntimeException.
   */
  public static void throwRuntimeException(Throwable t) {
    Throwable x = getRealCause(t);
    if (x instanceof RuntimeException)
      throw ((RuntimeException)x);
    else
      throw new OntopiaRuntimeException(x);
  }
  
  /* --- Dual arrays: insertion sort */

  public static void sortParallel(Object[] x, Object[] y, Comparator c) {
    // Sorts x and shifts the items in y accordingly
    sortParallel(x, 0, x.length, y, 0, c);
  }
  
  private static void sortParallel(Object[] x, int xoff, int xlen, Object[] y, int yoff, Comparator c) {
    // Insertion sort; fast when arrays are small.
    int yrel = xoff-yoff;
    for (int i=xoff; i < xlen+xoff; i++) {
      for (int j=i; j > xoff && c.compare(x[j-1], x[j]) > 0; j--) {
        swapParallel(x, y, j, j-1, yrel);
      }
    }
  }
  
  private static void swapParallel(Object[] x, Object[] y, int a, int b, int yrel) {
    // Swaps x[a] with x[b] and y[a] with y[b].
    Object tx = x[a], ty = y[a+yrel];
    x[a] = x[b]; y[a+yrel] = y[b+yrel];
    x[b] = tx; y[b+yrel] = ty;
  }


  /**
   * INTERNAL: Compare array size, then each element in sequence using
   * a comparator.
   */
  public static int compareArrays(Object[] a1, Object[] a2, Comparator c) {
    int r = compare(a1.length, a2.length);
    if (r != 0) return r;
    for (int i=0; i < a1.length; i++) {
      r = c.compare(a1[i], a2[i]);
      if (r != 0) return r;
    }
    return 0;
  }

  public static String toString(Object o) {
    return (o == null ? "null" : o.toString());
  }
}
