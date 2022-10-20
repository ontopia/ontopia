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

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * INTERNAL: A term analyzer which recognizes certain kinds of terms
 * using regexps and adjusts their scores accordingly. At the moment
 * it only recognizes emails addresses and HTTP URLs. These are scored
 * down dramatically.
 */
public class RegexpTermAnalyzer implements TermAnalyzerIF {
  private List<Rule> rules;
  
  public RegexpTermAnalyzer() {
    this.rules = new ArrayList<Rule>();
    rules.add(new Rule("email address",
                       "[-A-Za-z.0-9]+@([-A-Za-z.0-9]+\\.)+[A-Za-z]+",
                       0.002d));
    rules.add(new Rule("http URL",
                       "http://[-.A-Za-z?+&=0-9#/]+",
                       0.002d));
  }
  
  @Override
  public void startAnalysis(TermDatabase tdb) {
    // no-op
  }
  
  @Override
  public void analyzeTerm(Term term) {
    for (int ix = 0; ix < rules.size(); ix++) {
      Rule rule = rules.get(ix);
      if (rule.matches(term)) {
        term.multiplyScore(rule.getFactor(), 
                           "matched " + rule.getName() + " rule");
      }
    }
  }

  @Override
  public void endAnalysis() {
    // no-op
  }

  // --- Internal

  static class Rule {
    private String name;
    private Pattern pattern;
    private double factor;

    public Rule(String name, String pattern, double factor) {
      this.name = name;
      this.pattern = Pattern.compile(pattern);
      this.factor = factor;
    }

    public boolean matches(Term term) {
      return pattern.matcher(term.getPreferredName()).matches();
    }

    public String getName() {
      return name;
    }
    
    public double getFactor() {
      return factor;
    }
  }
}
