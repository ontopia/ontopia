
package net.ontopia.topicmaps.classify;

import java.util.StringTokenizer;

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
