
package net.ontopia.topicmaps.db2tm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * INTERNAL: Represents a prefix declaration.
 */
public class Prefix {

  public static final int TYPE_ITEM_IDENTIFIER = 1;
  public static final int TYPE_SUBJECT_IDENTIFIER = 2;
  public static final int TYPE_SUBJECT_LOCATOR = 4;

  protected String id;
  protected String locator;
  protected int type;
  
  Prefix(String id, String locator, int type) {
    this.id = id;
    this.locator = locator;
    this.type = type;
  }

  /**
   * INTERNAL:
   */
  public String getId() {
    return id;
  }

  /**
   * INTERNAL:
   */
  public String getLocator() {
    return locator;
  }

  /**
   * INTERNAL:
   */
  public int getType() {
    return type;
  }
  
}
