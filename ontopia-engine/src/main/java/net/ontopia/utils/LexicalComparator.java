/*
 * #!
 * Ontopia Engine
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

package net.ontopia.utils;

import java.util.Comparator;
import java.text.Collator;

/**
 * INTERNAL: Comparator that performs a lexical comparison. It calls the
 * toString method on the objects and compares the result. It can be
 * configured to be case insensitive. It is case sensitive by default.</p>
 */

public class LexicalComparator implements Comparator<Object> {

  public final static LexicalComparator CASE_SENSITIVE = new LexicalComparator(true);
  public final static LexicalComparator CASE_INSENSITIVE = new LexicalComparator(false);

  protected Collator collator;

  private LexicalComparator(boolean casesensitive) {
    collator = Collator.getInstance();
    if (!casesensitive)
      collator.setStrength(Collator.SECONDARY);
  }
  
  @Override
  public int compare(Object obj1, Object obj2) {
    return collator.compare(obj1.toString(), obj2.toString());
  }
  
}




