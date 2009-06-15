
// $Id: DefaultTokenizer.java,v 1.5 2006/11/15 13:29:01 grove Exp $

package net.ontopia.topicmaps.classify;

import java.util.*;

import net.ontopia.utils.*;

/**
 * INTERNAL: 
 */
public class DefaultTokenizer implements TokenizerIF {

  private StringTokenizer tokenizer;

  public DefaultTokenizer() {
  }

  public void setText(String text) {
    this.tokenizer = new StringTokenizer(text, " \t\n\r\f");
  }
  
  public boolean next() {
    return tokenizer.hasMoreElements();
  }
  
  public String getToken() {
    return (String)tokenizer.nextElement();
  }
  
}
