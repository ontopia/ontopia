/**
 * Copyright (C) 2009 Space Applications Services
 *   <thomas.neidhart@spaceapplications.com>
 *
 * This file is part of the Ontopia project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ontopia.topicmaps.query.toma.parser.ast;

import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;
import net.ontopia.topicmaps.query.toma.util.IndentedStringBuilder;

/**
 * INTERNAL: Common base interface for all elements of the AST. This contains
 * just a common method for printing the AST on screen.  
 */
public interface ASTElementIF {

  /**
   * Validates if this element is syntactically correct.
   * @return true if validation was successful.
   * @throws AntlrWrapException if an syntax error was found.
   */
  public boolean validate() throws AntlrWrapException;

  /**
   * Fills the parse tree with a string representation of this AST element.
   * 
   * @param buf the buffer to use.
   * @param level the current level of within the parse tree.
   */
  public void fillParseTree(IndentedStringBuilder buf, int level);
}
