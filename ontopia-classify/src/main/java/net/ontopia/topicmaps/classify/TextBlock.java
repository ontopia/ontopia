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
public class TextBlock {

  private StringBuilder sb;
  private List<Token> tokens = new ArrayList<Token>();

  public String getText() {
    return (sb == null ? null : sb.toString());
  }

  public List<Token> getTokens() {
    return tokens;
  }
  
  public void addToken(Token token) {
    tokens.add(token);
  }
  
  public void addText(char[] ch, int start, int length) {
    if (sb == null) {
      sb = new StringBuilder(length);
    }
    sb.append(ch, start, length);
  }

  public void visitTokens(TokenVisitor visitor) {
    for (int i=0; i < tokens.size(); i++) {
      Token token = tokens.get(i);
      visitor.visit(token);
    }
  }
  
}
