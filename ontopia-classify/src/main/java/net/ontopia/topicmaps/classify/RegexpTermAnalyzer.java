
package net.ontopia.topicmaps.classify;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

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
  
  public void startAnalysis(TermDatabase tdb) {
  }
  
  public void analyzeTerm(Term term) {
    for (int ix = 0; ix < rules.size(); ix++) {
      Rule rule = rules.get(ix);
      if (rule.matches(term))
        term.multiplyScore(rule.getFactor(), 
                           "matched " + rule.getName() + " rule");
    }
  }

  public void endAnalysis() {
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
