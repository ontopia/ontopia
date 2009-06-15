// $Id: Display.java,v 1.15 2008/06/12 14:37:18 geir.gronmo Exp $

package net.ontopia.topicmaps.nav.utils.structures;

import java.util.Map;
import java.util.HashMap;

/** 
 * INTERNAL: Holds the selection and presentation info collected from <code>DisplayTag</code>.
 */     
public class Display implements DisplayIF {
        
  // initialise
  protected String object;      
  protected String args;  
  protected String renderName;     
  protected String renderTemplate;      
  protected String renderStringifier;     
  protected String baseNameContext;
  protected String baseNameGrabber;
  protected String baseNameDecider;
  protected String variantNameContext;
  protected String variantNameGrabber;
  protected String variantNameDecider;  
  protected String displayTitle;
  protected String displayHref;
  protected String displayBehaviour;  
  /** Map with key: String,
      value: Collection (of TopicIF objects used to define scope themes) */
  protected Map context;
 
  /**
   * Constructor used when Set methods to be called during configuration.
   */
  public Display() {
    this.renderName = "";      
    this.object = "";
    this.renderStringifier = "";
    this.renderTemplate = "";
    this.args = "";
    this.baseNameContext = "";
    this.baseNameGrabber = "";
    this.baseNameDecider = "";
    this.variantNameContext = "";
    this.variantNameGrabber = "";
    this.variantNameDecider = "";      
    this.context = new HashMap(); 
  }
        
  /**
   * Constructor used by <code>DisplayTag</code> to complete every property.
   *
   * @param name          the name of the <code>Display</code>
   * @param object        the name of the topic map object which this object related to:
   *                      generally generic names are used such as "primary", "secondary" and "id"
   * @param stringifier   the full java path to a <code>StringifierIF</code> class
   * @param template      the <code>RenderDisplay</code> template to be used
   * @param args          a space separated list of arguments to be used by the application 
   * @param baseNameContext     a space separated list of identities or ids which provide context for the baseName
   * @param baseNameGrabber     the full java path to a baseName <code>GrabberIF</code> 
   * @param baseNameArgs        a space separated list of args used by the baseName grabber
   * @param variantNameContext  a space separated list of identities or ids which provide context for the baseName
   * @param variantNameGrabber  the full java path to a variantName <code>GrabberIF</code> 
   * @param variantNameArgs     a space separated list of args used by the variantName grabber
   */
  public Display(String object, 
                 String args, 
                 String renderName, 
                 String renderTemplate, 
                 String renderStringifier, 
                 String baseNameContext, 
                 String baseNameGrabber, 
                 String baseNameDecider, 
                 String variantNameContext, 
                 String variantNameGrabber, 
                 String variantNameDecider, 
                 String displayTitle,
                 String displayHref,
                 String displayBehaviour,
                 Map context) {
    this.object = object;
    this.args = args;
    this.renderName = renderName;
    this.renderTemplate = renderTemplate;
    this.renderStringifier = renderStringifier;
    this.baseNameContext = baseNameContext;
    this.baseNameGrabber = baseNameGrabber;
    this.baseNameDecider = baseNameDecider;
    this.variantNameContext = variantNameContext;
    this.variantNameGrabber = variantNameGrabber;
    this.variantNameDecider = variantNameDecider;       
    this.displayTitle = displayTitle;
    this.displayHref = displayHref;
    this.displayBehaviour = displayBehaviour;         
    this.context = context;
  }

  /**
   * INTERNAL: for easier debugging.
   */
  public String toString() {
    return "Object: " + object + ", Title: " + displayTitle + ", RenderName: " + renderName; 
  }
  
  // -------------------------------------------------------------------------------
  // set methods
  // used by configuration defaults
  // nb, Context and Display Title, Href and Behaviour cannot be set in the defaults
  // -------------------------------------------------------------------------------

  public void setObject(String s) { object = s; }
  public void setArgs(String s) { args = s; }

  public void setRenderName(String s) { renderName = s; } 
  public void setRenderTemplate(String s) { renderTemplate = s; }
  public void setRenderStringifier(String s) { renderStringifier = s; }

  public void setTopicNameContext(String s) { baseNameContext = s; }
  public void setTopicNameGrabber(String s) { baseNameGrabber = s; }
  public void setTopicNameDecider(String s) { baseNameDecider = s; }

  public void setVariantNameContext(String s) { variantNameContext = s; }
  public void setVariantNameGrabber(String s) { variantNameGrabber = s; }
  public void setVariantNameDecider(String s) { variantNameDecider = s; }

  
  // --------------------------------------------------------------------------
  // implementation of DisplayIF
  // --------------------------------------------------------------------------

  public String getObject() { return object; }  
  public String getArgs() { return args; }

  public String getRenderName() { return renderName; } 
  public String getRenderTemplate() { return renderTemplate; }
  public String getRenderStringifier() { return renderStringifier; } 

  public String getTopicNameContext() { return baseNameContext; }
  public String getTopicNameGrabber() { return baseNameGrabber; }
  public String getTopicNameDecider() { return baseNameDecider; }

  public String getVariantNameContext() { return variantNameContext; }
  public String getVariantNameGrabber() { return variantNameGrabber; }
  public String getVariantNameDecider() { return variantNameDecider; } 

  public String getDisplayTitle() { return displayTitle; }
  public String getDisplayHref() { return displayHref; }
  public String getDisplayBehaviour() { return displayBehaviour; }  

  public Map getContext() { return context; } 

}





