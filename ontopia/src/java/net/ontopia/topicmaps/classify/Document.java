
// $Id: Document.java,v 1.4 2007/03/07 10:25:32 grove Exp $

package net.ontopia.topicmaps.classify;

import java.util.*;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: 
 */
public class Document implements TextHandlerIF {

  private Region root;
  private Region current;

  private boolean tokenized;
  
  Document() {
    this.root = new Region();
    this.current = root;
  }
  
  public Region getRoot() {
    return root;
  }
  
  public void startRegion(String regionName) {
    Region region = new Region(regionName);
    region.setParent(current);
    current = region;
  }
  
  public void text(char[] ch, int start, int length) {
    current.addText(ch, start, length);
  }

  public void endRegion() {
    Region parent = current.getParent();
    parent.addRegion(current);
    current = parent;
  }

  public void dump() {
    root.dump();
  }

  public void visitTokens(TokenVisitor visitor) {
    root.visitTokens(visitor);
  }

  public void setTokenized(boolean tokenized) {
    if (this.tokenized) throw new OntopiaRuntimeException("Cannot tokenize document more than once.");
    this.tokenized = tokenized;
  }
  
}
