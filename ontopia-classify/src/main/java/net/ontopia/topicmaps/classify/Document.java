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
  
  @Override
  public void startRegion(String regionName) {
    Region region = new Region(regionName);
    region.setParent(current);
    current = region;
  }
  
  @Override
  public void text(char[] ch, int start, int length) {
    current.addText(ch, start, length);
  }

  @Override
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
    if (this.tokenized) {
      throw new OntopiaRuntimeException("Cannot tokenize document more than once.");
    }
    this.tokenized = tokenized;
  }
  
}
