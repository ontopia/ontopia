// $Id: Layout.java,v 1.6 2004/11/12 11:47:19 grove Exp $

package net.ontopia.topicmaps.nav.utils.structures;

/** 
 * INTERNAL: Holds the selection and presentation info for layout. An instance is used 
 * by topicmap tags to access information on the list stringifiers and comparators
 * to be used when building lists and blocks of lists.
 */     
public class Layout {
        
  // initialise with appropriate defaults
  protected String itemListStringifier;
  protected String itemListStringifierArgs;
  protected String itemComparator;   
  protected String entryListStringifier;
  protected String entryListStringifierArgs;
  protected String entryComparator;
  protected String blockListStringifier;
  protected String blockListStringifierArgs;
  protected String blockComparator;  

  /**
   * Empty constructor used by configuration objects. Ensures that properties are
   * never null, even if designer doesn't complete them on the JSP.
   */
  public Layout() {
    this.itemListStringifier = "";
    this.itemListStringifierArgs = "";
    this.itemComparator = "";
    this.entryListStringifier = "";
    this.entryListStringifierArgs = "";
    this.entryComparator = "";
    this.blockListStringifier = "";
    this.blockListStringifierArgs = "";
    this.blockComparator = ""; 
  }
        
  /**
   * Constructor used by <code>LayoutTag</code>.
   */
  public Layout (String itemListStringifier, 
                 String itemListStringifierArgs, 
                 String itemComparator,
                 String entryListStringifier, 
                 String entryListStringifierArgs, 
                 String entryComparator,
                 String blockListStringifier, 
                 String blockListStringifierArgs,
                 String blockComparator) {
    this.itemListStringifier = itemListStringifier;
    this.itemListStringifierArgs = itemListStringifierArgs;
    this.itemComparator = itemComparator;
    this.entryListStringifier = entryListStringifier;
    this.entryListStringifierArgs = entryListStringifierArgs;
    this.entryComparator = entryComparator;
    this.blockListStringifier = blockListStringifier;
    this.blockListStringifierArgs = blockListStringifierArgs;
    this.blockComparator = blockComparator;      
  }

  
  // ------------------------------------------------------------------------------
  // get methods
  // ------------------------------------------------------------------------------

  public String getItemListStringifier()      { return itemListStringifier; }
  public String getItemListStringifierArgs()  { return itemListStringifierArgs; }
  public String getItemComparator()           { return itemComparator; }

  public String getEntryListStringifier()     { return entryListStringifier; }
  public String getEntryListStringifierArgs() { return entryListStringifierArgs; }
  public String getEntryComparator()          { return entryComparator; }

  public String getBlockListStringifier()     { return blockListStringifier; }
  public String getBlockListStringifierArgs() { return blockListStringifierArgs; }
  public String getBlockComparator()          { return blockComparator; }

  
  // ------------------------------------------------------------------------------
  // set methods
  // ------------------------------------------------------------------------------

  public void setItemListStringifier(String s)      { itemListStringifier = s; }
  public void setItemListStringifierArgs(String s)  { itemListStringifierArgs = s; }
  public void setItemComparator(String s)           { itemComparator = s; }

  public void setEntryListStringifier(String s)     { entryListStringifier = s; }
  public void setEntryListStringifierArgs(String s) { entryListStringifierArgs = s; }
  public void setEntryComparator(String s)          { entryComparator = s; }

  public void setBlockListStringifier(String s)     { blockListStringifier = s; }
  public void setBlockListStringifierArgs(String s) { blockListStringifierArgs = s; }
  public void setBlockComparator(String s)          { blockComparator = s; }
}





