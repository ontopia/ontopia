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

import java.util.HashMap;
import java.util.Map;

/**
 * INTERNAL: 
 */
public class RegionBooster extends AbstractDocumentAnalyzer {

  private Map<String, Double> regions;
  private double boost = 1.0d;

  public RegionBooster() {
    super(1);
    this.regions = new HashMap<String, Double>();
  }

  public void addBoost(String rname, double boost) {
    regions.put(rname, boost);
  }
  
  @Override
  public void startRegion(Region region) {
    super.startRegion(region);
    String rname = region.getName();
    Double d = regions.get(rname);    
    if (d != null) {
      this.boost = d.doubleValue();
    } else {
      this.boost = 1.0d;
    }      
  }
  
  @Override
  public void analyzeToken(TextBlock parent, Token token, int index) {
    // ignore non variant tokens
    if (token.getType() != Token.TYPE_VARIANT) {
      return;
    }
    
    Term term = ((Variant)token).getTerm();
    double score = term.getScore();
    if (score > 0d) {
      term.multiplyScore(boost, "region boost");
    }
  }

  @Override
  public void endRegion(Region region) {
    super.endRegion(region);
  }
  
}
