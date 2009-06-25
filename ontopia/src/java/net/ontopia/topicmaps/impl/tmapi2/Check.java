// $Id:$

package net.ontopia.topicmaps.impl.tmapi2;

import java.util.Collection;

import org.tmapi.core.Construct;
import org.tmapi.core.Locator;
import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Topic;

/**
 * INTERNAL: Provides various argument constraint checks.
 */
public final class Check {

  private Check() {
    // noop.
  }

  /**
   * Throws a {@link ModelConstraintException} with the specified
   * <tt>sender</tt> and <tt>msg</tt>
   * 
   * @param sender
   *          The sender
   * @param msg
   *          The error message
   */
  private static void reportError(Construct sender, String msg) {
    throw new ModelConstraintException(sender, msg);
  }

  /**
   * Throws a {@link ModelConstraintException} iff the <tt>scope</tt> is
   * <tt>null</tt>.
   * 
   * @param sender
   *          The sender.
   * @param scope
   *          The scope.
   */
  public static void scopeNotNull(Construct sender, Topic[] scope) {
    if (scope == null) {
      reportError(sender, "The scope must not be null");
    }
  }

  /**
   * Throws a {@link ModelConstraintException} iff the <tt>theme</tt> is
   * <tt>null</tt>.
   * 
   * @param sender
   *          The sender.
   * @param theme
   *          The theme.
   */
  public static void themeNotNull(Construct sender, Topic theme) {
    if (theme == null) {
      throw new ModelConstraintException(sender, "The theme must not be null");
    }
  }

  /**
   * Throws a {@link ModelConstraintException} iff the <tt>scope</tt> is
   * <tt>null</tt>.
   * 
   * @param sender
   *          The sender.
   * @param scope
   *          The scope.
   */
  public static void scopeNotNull(Construct sender, Collection<Topic> scope) {
    if (scope == null) {
      reportError(sender, "The scope must not be null");
    }
  }

  /**
   * Throws a {@link ModelConstraintException} iff the <tt>type</tt> is
   * <tt>null</tt>.
   * 
   * @param sender
   *          The sender.
   * @param type
   *          The type.
   */
  public static void typeNotNull(Construct sender, Topic type) {
    if (type == null) {
      reportError(sender, "The type must not be null");
    }
  }

  /**
   * Throws a {@link ModelConstraintException} iff the <tt>value</tt> is
   * <tt>null</tt>.
   * 
   * @param sender
   *          The sender.
   * @param value
   *          The value.
   */
  public static void valueNotNull(Construct sender, Object value) {
    if (value == null) {
      reportError(sender, "The value must not be null");
    }
  }

  /**
   * Throws a {@link ModelConstraintException} iff the <tt>value</tt> or the
   * <tt>datatype</tt> is <tt>null</tt>.
   * 
   * @param sender
   *          The sender.
   * @param value
   *          The value.
   * @param datatype
   *          The datatype.
   */
  public static void valueNotNull(Construct sender, Object value,
      Locator datatype) {
    valueNotNull(sender, value);
    if (datatype == null) {
      reportError(sender, "The datatype must not be null");
    }
  }

  
  
  /**
   * Throws a {@link ModelConstraintException} iff the <tt>player</tt> is
   * <tt>null</tt>.
   * 
   * @param sender
   *          The sender.
   * @param player
   *          The player.
   */
  public static void playerNotNull(Construct sender, Topic player) {
    if (player == null) {
      reportError(sender, "The role player must not be null");
    }
  }

  public static void subjectIdentifierNotNull(Construct sender, Locator loc) {
    if (loc == null) {
      reportError(sender, "The subject identifier must not be null");
    }
  }

  public static void subjectLocatorNotNull(Construct sender, Locator loc) {
    if (loc == null) {
      reportError(sender, "The subject locator must not be null");
    }
  }

  public static void itemIdentifierNotNull(Construct sender, Locator loc) {
    if (loc == null) {
      reportError(sender, "The item identifier must not be null");
    }
  }

  /**
   * Throws a IllegalArgumentException if the subject locator is <code>null</code>
   * @param loc the locator to check
   */
  public static void subjectLocatorNotNull(Locator loc) {
    if (loc == null) {
      throw new IllegalArgumentException("The subject locator must not be null");
    }
  }
  
  /**
   * Throws a IllegalArgumentException if the subject identifier is <code>null</code>
   * @param sid the identifier to check
   */
  public static void subjectIdentifierNotNull(Locator sid) {
    if (sid == null) {
      throw new IllegalArgumentException("The subject locator must not be null");
    }
  }
  
  /**
   * Throws a IllegalArgumentException if the value is <code>null</code>
   * @param value the value to check
   */
  public static void valueNotNull(Object value) {
    if (value == null) {
      throw new IllegalArgumentException("value is null");
    }
  }
  
  /**
   * Throws a IllegalArgumentException if the  locator is <code>null</code>
   * @param loc the locator to check
   */
  public static void locatorNotNull(Locator loc) {
    if (loc == null) {
      throw new IllegalArgumentException("The locator must not be null");
    }
  }
  
  /**
   * Throws a IllegalArgumentException if the  datatype is <code>null</code>
   * @param datatype the locator to check
   */
  public static void datatypeNotNull(Locator loc) {
    if (loc == null) {
      throw new IllegalArgumentException("The datatype must not be null");
    }
  }
  
  /**
   * Throws a {@link IllegalArgumentException} iff the <tt>theme</tt> is
   * <tt>null</tt>.
   * 
   * @param theme
   *          The theme.
   */
  public static void themeNotNull(Topic theme) {
    if (theme == null) {
      throw new IllegalArgumentException("The theme must not be null");
    }
  }
  
  /**
   * Throws a {@link IllegalArgumentException} iff the <tt>theme</tt> is
   * <tt>null</tt>.
   * 
   * @param themes
   *          The array of themes.
   */
  public static void themeNotNull(Topic... themes) {
    if (themes == null) {
      throw new IllegalArgumentException("The theme must not be null");
    }
  }
  
  /**
   * Throws a {@link IllegalArgumentException} iff the <tt>type</tt> is
   * <tt>null</tt>.
   * 
   * @param type
   *          The type.
   */
  public static void typeNotNull(Topic type) {
    if (type == null) {
      throw new IllegalArgumentException("The type must not be null");
    }
  }

}
