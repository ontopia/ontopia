
// $Id: JunkNormalizer.java,v 1.4 2007/03/22 15:05:06 grove Exp $

package net.ontopia.topicmaps.classify;

import java.util.*;
import java.lang.reflect.Method;

import net.ontopia.utils.*;

/**
 * INTERNAL: 
 */
public class JunkNormalizer implements TermNormalizerIF {
  
  public String normalize(String term) {
    // strip out repeated whitespace characters
    term = StringUtils.normalizeIsWhitespace(term);
    // drop 's endings
    if (term.length() >= 2 && term.endsWith("'s")) {
      term = term.substring(0, term.length()-2);
    }
    return term;
  }
  
}
