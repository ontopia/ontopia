
package net.ontopia.topicmaps.classify;

import java.util.*;

import net.ontopia.utils.*;

/**
 * INTERNAL: 
 */
public class Region {

  private String name;
  private Region parent;
  private List children = new ArrayList(); // list of TextBlock and Region
    
  Region() {
  }

  Region(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
  
  public Region getParent() {
    return parent;
  }
  
  public void setParent(Region parent) {
    this.parent = parent;
  }

  public List getChildren() {
    return children;
  }
  
  public void addText(char[] ch, int start, int length) {
    // check to see if last child is text
    int size = children.size();
    if (size > 0) {
      Object last = children.get(size-1);
      if (last instanceof TextBlock) {
        ((TextBlock)last).addText(ch, start, length);
        return;
      }
    }
    TextBlock tb = new TextBlock();
    tb.addText(ch, start, length);
    children.add(tb);
  }

  public void addRegion(Region child) {
    children.add(child);
  }

  public void dump() {
    dump(-1);
  }
  
  protected void dump(int level) {
    if (name != null) {
      for (int a=0; a < (level*2); a++) {
        System.out.print(' ');
      }
      System.out.println("<" + name + ">");
    }
    
    for (int i=0; i < children.size(); i++) {
      Object o = children.get(i);
      if (o instanceof TextBlock)
        System.out.println(((TextBlock)o).getTokens());
      else {
        level++;
        ((Region)o).dump(level);
        level--;
      }
    }

    if (name != null) {
      for (int a=0; a < (level*2); a++) {
        System.out.print(' ');
      }
      System.out.println("</" + name + ">");
    }
  }

  public void visitTokens(TokenVisitor visitor) {
    for (int i=0; i < children.size(); i++) {
      Object o = children.get(i);
      if (o instanceof TextBlock)
        ((TextBlock)o).visitTokens(visitor);
      else {
        ((Region)o).visitTokens(visitor);
      }
    }
  }
  
}
