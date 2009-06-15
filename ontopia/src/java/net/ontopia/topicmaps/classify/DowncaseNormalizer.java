
// $Id: DowncaseNormalizer.java,v 1.2 2007/03/22 15:05:06 grove Exp $

package net.ontopia.topicmaps.classify;

import java.util.*;
import java.lang.reflect.Method;

import net.ontopia.utils.*;

/**
 * INTERNAL: 
 */
public class DowncaseNormalizer implements TermNormalizerIF {
  
  public String normalize(String term) {
    return term.toLowerCase();
  }
  
}
