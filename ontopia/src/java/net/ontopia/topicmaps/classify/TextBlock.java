
// $Id: TextBlock.java,v 1.6 2007/03/07 10:25:32 grove Exp $

package net.ontopia.topicmaps.classify;

import java.util.*;

import net.ontopia.utils.*;

/**
 * INTERNAL: 
 */
public class TextBlock {

  StringBuffer sb;
  List tokens = new ArrayList();

  public String getText() {
    return (sb == null ? null : sb.toString());
  }

  public List getTokens() {
    return tokens;
  }
  
  public void addToken(Token token) {
    tokens.add(token);
  }
  
  public void addText(char[] ch, int start, int length) {
    if (sb == null)
      sb = new StringBuffer(length);
    sb.append(ch, start, length);
  }

  public void visitTokens(TokenVisitor visitor) {
    for (int i=0; i < tokens.size(); i++) {
      Token token = (Token)tokens.get(i);
      visitor.visit(token);
    }
  }
  
}
