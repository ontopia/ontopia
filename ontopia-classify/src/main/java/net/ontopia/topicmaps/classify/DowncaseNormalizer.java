
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
