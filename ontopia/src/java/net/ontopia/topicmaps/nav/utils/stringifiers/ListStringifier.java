// $Id: ListStringifier.java,v 1.14 2004/11/12 11:47:19 grove Exp $

package net.ontopia.topicmaps.nav.utils.stringifiers;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import net.ontopia.utils.StringifierIF;

import org.apache.log4j.Logger;

/** 
 * INTERNAL: A stringifier which stringies a collection, given a
 * Stringifier for each item and a comparator.
 */
public class ListStringifier implements StringifierIF {
  
  // Define a logging category.
  static Logger log = Logger.getLogger(ListStringifier.class.getName());

  public final static int DEFAULT_PAGE_SIZE = 1000;  // should make use of applicationConfig: property listMaxSize
  
  protected StringifierIF itemStringifier;
  protected Comparator itemComparator;
  protected String args;
  protected int page = 1;
  protected int pageSize = DEFAULT_PAGE_SIZE;

  /**
   * A required constructor for constructing a list stringifier.
   *
   * @param itemStringifier The StringifierIF which will stringifiy each of the items.
   * @param itemComparator  The Comparator which will be used to compare items.
   * @param args            The separator between items
   */
  public ListStringifier(StringifierIF itemStringifier,
                         Comparator itemComparator,
                         String args) {
    this(itemStringifier, itemComparator, args, 1, DEFAULT_PAGE_SIZE);
  }
  
  /**
   * A required constructor for constructing a list stringifier.
   *
   * @param itemStringifier The StringifierIF which will stringifiy each of the items.
   * @param itemComparator  The Comparator which will be used to compare items.
   * @param args            The separator between items
   * @param page            The pagenumber which should be displayed if paged output
   * @param pageSize        How many items should be displayed on one page.
   */
  public ListStringifier(StringifierIF itemStringifier,
                         Comparator itemComparator,
                         String args, int page, int pageSize) {
    this.itemStringifier = itemStringifier;
    this.itemComparator = itemComparator;
    this.args = args;
    if (args == null) 
      this.args = "";
    this.page = page;
    this.pageSize = pageSize;
  }

  /**
   * Returns a String for a Collection.
   */
  public String toString(Object object) {
    if (object == null) 
      return "null";
        
    // Sort and output list
    Object[] list = ((Collection) object).toArray();    
    if (itemComparator != null)
      Arrays.sort(list, itemComparator);

    // Loop over objects
    if (list.length == 0)
      return "";

    int itemCount = 0;
    int length = 0;
    String[] items = new String[Math.min(list.length, pageSize+1)];

    for (int ix = 0; ix < list.length; ix++) {
      if (itemCount < pageSize) {
        String thisItem = itemStringifier.toString(list[ix]);
        if (thisItem != null && !thisItem.equals("")) {
          items[itemCount++] = thisItem;
          length += thisItem.length();
        }
      } else {
        items[itemCount++] = itemStringifier.toString("list truncated...");
        length += items[itemCount-1].length();
        break;
      }
    }

    if (itemCount == 1) // if only one item there are no args
      return items[0];
    else if (itemCount == 0)
      return "";

    int pos = 0;
    char[] argsch = args.toCharArray();

    if ((length + (itemCount-1) * args.length()) <= 0)
      return "";
        
    char[] out = new char[length + (itemCount-1) * args.length()];
    for (int ix = 0; ix < itemCount; ix++) {
      int len = items[ix].length();
      items[ix].getChars(0, len, out, pos);
      pos += len;

      if (ix+1 < itemCount) {
        System.arraycopy(argsch, 0, out, pos, argsch.length);
        pos += argsch.length;
      }
    }
      
    return new String(out);
  }

}





