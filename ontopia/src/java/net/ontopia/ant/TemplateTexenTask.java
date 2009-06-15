// $Id: TemplateTexenTask.java,v 1.4 2002/05/29 13:38:35 hca Exp $

package net.ontopia.ant;

import java.util.*;

import org.apache.velocity.context.Context;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.texen.ant.TexenTask;

/**
 * An extended Texen task used for generating HTML from
 * an template and have access to System properties.
 */
public class TemplateTexenTask extends TexenTask
{
  private String productName;
  public String getProductName() { return productName; }
  public void setProductName(String productName) { this.productName = productName; }

  private String productVersion;
  public String getProductVersion() { return productVersion; }
  public void setProductVersion(String productVersion) { this.productVersion = productVersion; }

  /**
   * Set up the initialial context for generating the
   * HTML from the HTML template.
   */
  public Context initControlContext()
  {
    // Create a new Velocity context.
    Context context = new VelocityContext();
       
    context.put("productName", productName);
    context.put("productVersion", productVersion);
    
    return context;
  }

}






