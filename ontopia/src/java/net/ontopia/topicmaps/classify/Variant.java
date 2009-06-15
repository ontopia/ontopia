
// $Id: Variant.java,v 1.4 2007/03/07 10:25:32 grove Exp $

package net.ontopia.topicmaps.classify;

import java.util.*;

import net.ontopia.utils.*;
import gnu.trove.TObjectIntHashMap;
  
/**
 * INTERNAL: 
 */
public class Variant extends Token {

  protected Term term;
  
  Variant(String value) {
    super(value, Token.TYPE_VARIANT);
  }

  public Term getTerm() {
    return term;
  }

  void setTerm(Term term) {
    if (this.term != null) throw new OntopiaRuntimeException("Cannot set parent term on variant more than once." + this + " " + this.term + " " + term);
    this.term = term;
  }

  void replaceTerm(Term term) {
    this.term = term;
  }
  
  public String toString() {
    return '\"' + getValue() + "\"";
  }
  
}
