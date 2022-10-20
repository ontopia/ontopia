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
 * PUBLIC: Represents a form of a term as it occurred in classified
 * content.
 */
public class Variant extends Token {
  protected Term term;
  
  Variant(String value) {
    super(value, Token.TYPE_VARIANT);
  }

  /**
   * PUBLIC: Returns the term of which this is a variant.
   */
  public Term getTerm() {
    return term;
  }

  /**
   * PUBLIC: Returns the number of times this particular variant
   * occurred in the classified content.
   */
  public int getOccurrences() {
    return term.getOccurrences(this);
  }
  
  protected void setTerm(Term term) {
    if (this.term != null) {
      throw new OntopiaRuntimeException("Cannot set parent term on variant more than once." + this + " " + this.term + " " + term);
    }
    this.term = term;
  }

  protected void replaceTerm(Term term) {
    this.term = term;
  }
  
  @Override
  public String toString() {
    return '\"' + getValue() + "\"";
  }
  
}
