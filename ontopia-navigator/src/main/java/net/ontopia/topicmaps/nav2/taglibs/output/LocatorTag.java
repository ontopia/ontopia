
// $Id: LocatorTag.java,v 1.18 2008/04/10 08:10:03 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.taglibs.output;

import java.io.IOException;
import java.util.Iterator;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspTagException;

import net.ontopia.utils.StringUtils;
import net.ontopia.utils.StringifierIF;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.utils.ObjectUtils;

import net.ontopia.topicmaps.nav2.core.NavigatorConfigurationIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;

/**
 * INTERNAL: Output Producing Tag for writing out the URI of the
 * resource that is an occurrence, a variant name, or a locator.<p>
 *
 * Note: Only puts out first entry retrieved by iterator.
 */
public class LocatorTag extends BaseOutputProducingTag
  implements StringifierIF {

  // tag attributes
  private boolean relativeToTopicmap = false;
  private String strifyCN;
  
  public final void generateOutput(JspWriter out, Iterator iter)
    throws JspTagException, IOException {

    LocatorIF locator = null;
    Object elem = iter.next();
    
    // --- first try if object is instance of OccurrenceIF
    try {
      OccurrenceIF occ = (OccurrenceIF) elem;
      // could be also an internal occurrence
      if (ObjectUtils.equals(DataTypes.TYPE_URI, occ.getDataType()))
        locator = occ.getLocator();
    }
    // --- otherwise try other instances 
    catch (ClassCastException e) {
      // --- LocatorIF
      if (elem instanceof LocatorIF) {
        locator = (LocatorIF) elem;
      }
      // --- VariantNameIF
      else if (elem instanceof VariantNameIF) {
        VariantNameIF variant = (VariantNameIF) elem;
        locator = variant.getLocator();
      }
      // --- otherwise signal error
      else {
        String msg = "LocatorTag: expected collection which contains " +
          "occurrences, locators or variant names as elements, " +
          "but got instance of " + elem.getClass().getName() + ".";
        throw new NavigatorRuntimeException(msg);
      }
    }

    // Get and write address belonging to locator
    StringifierIF strify = this;
    if (strifyCN != null) 
      strify = (StringifierIF)
        contextTag.getNavigatorApplication().getInstanceOf(strifyCN);
    out.print(strify.toString(locator));
  }


  // ----------------------------------------------
  // additional attributes
  // ----------------------------------------------

  /**
   * INTERNAL: Set the behaviour of the output, whether or not the
   * base address of the topicmap should be not be in generated
   * string. Default is to generate full locator address. Allowed
   * values are: "yes|true|no|false". (FIXME: not implemented yet)
   */
  public void setRelative(String relativeToTopicmap) {
    this.relativeToTopicmap = (relativeToTopicmap.equalsIgnoreCase("true")
                               || relativeToTopicmap.equalsIgnoreCase("yes"));
  }

  /**
   * Tag attribute for setting the stringifier to be used when
   * producing the string from the selected object.
   * @since 2.0
   */
  public final void setStringifier(String strifyCN) {
    this.strifyCN = strifyCN;
  }

  // --- StringifierIF interface

  public String toString(Object object) {
    if (object != null && !(object instanceof LocatorIF))
      throw new OntopiaRuntimeException("Stringifiying " + object + " which is"
                                        + " not a locator!");
    LocatorIF locator = (LocatorIF) object;
    
    String address = null;
    if (locator != null)
      address = locator.getExternalForm();

    if (relativeToTopicmap) {
      // TODO: implement when clear how to access base locator.
      throw new OntopiaRuntimeException("UNIMPLEMENTED FEATURE!!!!");
    }
        
    // special handling for null and empty locators
    if (locator == null || address == null || address.equals("")) {
      NavigatorConfigurationIF navConf = contextTag.getNavigatorConfiguration();
      if (navConf != null) {
        if (locator == null)
          address = navConf.getProperty(NavigatorConfigurationIF.OCCURRENCE_NULLLOCATOR,
                                        NavigatorConfigurationIF.DEFVAL_OCC_NULLLOC);
        else
          address = navConf.getProperty(NavigatorConfigurationIF.OCCURRENCE_EMPTYLOCATOR,
                                        NavigatorConfigurationIF.DEFVAL_OCC_EMPTYLOC);
      }
    }

    return StringUtils.escapeHTMLEntities(address);
  }
 
}
