
package net.ontopia.utils;

import java.util.Comparator;
import java.text.Collator;

/**
 * INTERNAL: Comparator that performs a lexical comparison. It calls the
 * toString method on the objects and compares the result. It can be
 * configured to be case insensitive. It is case sensitive by default.</p>
 */

public class LexicalComparator implements Comparator {

  public final static LexicalComparator CASE_SENSITIVE = new LexicalComparator(true);
  public final static LexicalComparator CASE_INSENSITIVE = new LexicalComparator(false);

  protected Collator collator;

  private LexicalComparator(boolean casesensitive) {
    collator = Collator.getInstance();
    if (!casesensitive)
      collator.setStrength(Collator.SECONDARY);
  }
  
  private LexicalComparator(Collator collator) {
    this.collator = collator;
  }

  public int compare(Object obj1, Object obj2) {
    return collator.compare(obj1.toString(), obj2.toString());
  }
  
}




