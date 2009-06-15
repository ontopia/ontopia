// $Id: Entry.java,v 1.5 2004/11/12 11:47:19 grove Exp $

package net.ontopia.topicmaps.nav.utils.structures;

/** 
 * INTERNAL: Holds the selection and presentation info for blocks. An
 * instance is used by topicmap tags to access information on the list
 * stringifiers and comparators to be used when building lists and
 * blocks of lists.
 */     
public class Entry {
        
  // initialise with appropriate defaults
  protected String renderName;     
  protected String renderTemplate;      
  protected String renderStringifier;     


  /**
   * Empty constructor used by configuration objects. Ensures that properties are
   * never null, even if designer doesn't complete them on the JSP.
   */
  public Entry(){
    this.renderName="";      
    this.renderStringifier="";
    this.renderTemplate="";
  }
        
  /**
   * Constructor used by <code>EntryTag</code>.
   */
  public Entry (String renderName, 
                String renderTemplate,
                            String renderStringifier) {
    this.renderName = renderName;
    this.renderTemplate = renderTemplate;
    this.renderStringifier = renderStringifier;
  }
        
  // get methods
  public String getRenderName(){return renderName;} 
  public String getRenderTemplate(){return renderTemplate;}
  public String getRenderStringifier(){return renderStringifier;} 

  // set methods
  public void setRenderName(String s){renderName=s;} 
  public void setRenderTemplate(String s){renderTemplate=s;}
  public void setRenderStringifier(String s){renderStringifier=s;}

}





