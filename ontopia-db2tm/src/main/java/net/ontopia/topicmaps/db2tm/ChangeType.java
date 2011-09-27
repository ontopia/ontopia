
package net.ontopia.topicmaps.db2tm;

/**
 * INTERNAL: We used to have five change types (declared as static
 * ints in ChangelogReaderIF), but reduced that to the current two.
 * However, I don't want to use a boolean for this, and so decided to
 * change over to an enum. 
 */
public enum ChangeType {
  /**
   * Used for both insert and update. The code works out from the data
   * what to do.
   */
  UPDATE,

  /**
   * Used for delete. The code detects this case itself from the data.
   */
  DELETE

  // there used to also be UNKNOWN (not needed any more) and IGNORE
  // (not needed any more). we deliberately omit both.
}