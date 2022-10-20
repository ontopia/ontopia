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

import gnu.trove.set.hash.TIntHashSet;

/**
 * INTERNAL: 
 */
public class SpecialCharNormalizer implements TermNormalizerIF, DelimiterTrimmerIF {

  private TIntHashSet prechars = new TIntHashSet();
  private TIntHashSet postchars = new TIntHashSet();
    
  public SpecialCharNormalizer() {
    this("<')(\"[ {\u00B7-%\u201c\u2018/$.,",
         ">')(.,\"':;!]? |}*\u00B7-%\u201d\u2019");
  }

  public SpecialCharNormalizer(String _prechars, String _postchars) {
    this((_prechars == null ? null : _prechars.toCharArray()),
         (_postchars == null ? null : _postchars.toCharArray()));
  }
  
  public SpecialCharNormalizer(char[] _prechars, char[] _postchars) {
    if (_prechars != null) {
      for (int i=0; i < _prechars.length; i++) {
        prechars.add(_prechars[i]);
      }
    }
    if (_postchars != null) {
      for (int i=0; i < _postchars.length; i++) {
        postchars.add(_postchars[i]);
      }
    }
  }
  
  @Override
  public String normalize(String term) {
    int length = term.length();
    int start = 0;
    int end = length-1;
    for (int i=start; i < end; i++) {
      if (!prechars.contains(term.charAt(i))) {
        start = i;
        break;
      }
    }
    for (int i=end; i >= start; i--) {
      if (!postchars.contains(term.charAt(i))) {
        end = i;
        break;
      }      
    }
    if (start == end) {
      return null;
    } else if (start == 0 && end == length) {
      return term;
    } else {
      return term.substring(start, end+1);
    }
  }
  
  @Override
  public int trimStart(String token) {
    int start = 0;
    int end = token.length()-1;
    for (int i=start; i < end+1; i++) {
      if (!prechars.contains(token.charAt(i))) {
        start = i;
        break;
      }
    }
    return start;
  }
  
  @Override
  public int trimEnd(String token) {
    int end = token.length()-1;
    for (int i=end; i >= 0; i--) {
      if (!postchars.contains(token.charAt(i))) {
        end = i;
        break;
      }      
    }
    return end;
  }
  
}
