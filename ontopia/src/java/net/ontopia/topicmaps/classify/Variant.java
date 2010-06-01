
// $Id: Variant.java,v 1.4 2007/03/07 10:25:32 grove Exp $

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
  
  void setTerm(Term term) {
    if (this.term != null)
      throw new OntopiaRuntimeException("Cannot set parent term on variant more than once." + this + " " + this.term + " " + term);
    this.term = term;
  }

  void replaceTerm(Term term) {
    this.term = term;
  }
  
  public String toString() {
    return '\"' + getValue() + "\"";
  }
  
}
