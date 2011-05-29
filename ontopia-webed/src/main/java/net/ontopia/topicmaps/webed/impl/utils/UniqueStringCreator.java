
package net.ontopia.topicmaps.webed.impl.utils;

/**
 * INTERNAL: Systematically generates strings that are guaranteed not to be
 * repeated.
 */
public class UniqueStringCreator {
    long basicCounter;
    UniqueStringCreator parent;
  
  public UniqueStringCreator() {
    basicCounter = 0;
    parent = null;
  }
  
  /**
   * Each time this method is called, it will return a String that was not
   * returned on any previous call, and thus will not be returned by any
   * subsequent call.
   */
  public String getNextUniqueString() {
    String retVal = getNextUniqueStringRecursively();
    increment();
    return retVal;
  }
  
  private String getNextUniqueStringRecursively() {
    String retVal = "";
    if (parent != null) {
      retVal += parent.getNextUniqueStringRecursively() + ".";
    }
    
    retVal += basicCounter;
    return retVal;
  }
  
  private void increment() {
    if (basicCounter == Long.MAX_VALUE) {
      basicCounter = 0;
      
      if (parent == null)
        parent = new UniqueStringCreator();
      else
        parent.increment();
    } else
      basicCounter++;
  }
  
  private static void evaluate() {
    UniqueStringCreator tested = new UniqueStringCreator();
    
    for (int j = 0; j < 6; j++) {
      for (int i = 0; i < 6; i++)
        System.out.println(tested.getNextUniqueString());
      System.out.println();
      tested.basicCounter = Long.MAX_VALUE - 1;
    }

    tested.parent.basicCounter = Long.MAX_VALUE;
    for (int i = 0; i < 6; i++)
      System.out.println(tested.getNextUniqueString());
    System.out.println();

    tested.basicCounter = Long.MAX_VALUE;
    for (int i = 0; i < 6; i++)
      System.out.println(tested.getNextUniqueString());
    System.out.println();
  }
}
