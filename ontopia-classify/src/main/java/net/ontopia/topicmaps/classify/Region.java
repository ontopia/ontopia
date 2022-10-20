/*
 * #!
 * Ontopia Classify
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.topicmaps.classify;

import java.util.ArrayList;
import java.util.List;

/**
 * INTERNAL: 
 */
public class Region {

  private String name;
  private Region parent;
  private List<Object> children = new ArrayList<Object>(); // list of TextBlock and Region
    
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

  public List<Object> getChildren() {
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
      if (o instanceof TextBlock) {
        System.out.println(((TextBlock)o).getTokens());
      } else {
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
      if (o instanceof TextBlock) {
        ((TextBlock)o).visitTokens(visitor);
      } else {
        ((Region)o).visitTokens(visitor);
      }
    }
  }
  
}
