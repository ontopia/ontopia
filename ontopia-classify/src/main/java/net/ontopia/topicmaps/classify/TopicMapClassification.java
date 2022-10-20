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

import java.util.Collection;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;

/**
 * INTERNAL: 
 */
public class TopicMapClassification {
  private TermDatabase tdb;
  private TopicMapAnalyzer ta;
  private TermAnalyzerIF customTermAnalyzer;
  
  public TopicMapClassification() {
    this.tdb = new TermDatabase();    
  }
  
  public TopicMapClassification(TopicMapIF topicmap) {
    this.tdb = new TermDatabase();    
    this.ta = new TopicMapAnalyzer(topicmap);
  }

  public void setCustomTermAnalyzer(TermAnalyzerIF customTermAnalyzer) {
    this.customTermAnalyzer = customTermAnalyzer;
  }
  
  public void classify(ClassifiableContentIF cc) {
    // detect document format and read document
    Document doc = new Document();
    new FormatModule().readContent(cc, doc);

    // tokenize document
    DocumentTokenizer dt = new DocumentTokenizer(tdb);
    dt.setTokenizer(new DefaultTokenizer());
    
    SpecialCharNormalizer specialChars = new SpecialCharNormalizer();
    dt.setDelimiterTrimmer(specialChars);
    
    dt.addTermNormalizer(new JunkNormalizer());
    dt.addTermNormalizer(specialChars);

    dt.tokenize(doc);

    // detect language
    Language language = Language.detectLanguage(doc);
    
    // set up document classifier and term database
    DocumentClassifier dc = new DocumentClassifier(tdb);   
    
    TermStemmerIF stemmer = language.getStemmer();
    dc.setTermStemmer(stemmer);
    
    dc.addDocumentAnalyzer(new DistanceAnalyzer());
    CompoundAnalyzer ca = new CompoundAnalyzer();
    ca.setTermStemmer(stemmer);
    dc.addDocumentAnalyzer(ca);
    
    RegionBooster rb = new RegionBooster();
    rb.addBoost("title", 1.15d);
    //! rb.addBoost("abstract", 1.05d);
    //! rb.addBoost("keyword", 1.10d);
    //! rb.addBoost("para", 1.01d);
    //! dc.addDocumentAnalyzer(rb);
    
    dc.addTermAnalyzer(CharacterAnalyzer.getInstance());
    dc.addTermAnalyzer(language.getFrequencyAnalyzer());
    dc.addTermAnalyzer(new RegexpTermAnalyzer());

    // FIXME: wrap and hand over to compound analyzer instead?
    dc.addTermAnalyzer(language.getStopListAnalyzer());
    if (customTermAnalyzer != null) {
      dc.addTermAnalyzer(customTermAnalyzer); // blacklist
    }

    dc.addTermAnalyzer(ca);

    // run stop list analyzer after compounds have been made    
    dc.addTermAnalyzer(language.getStopListAnalyzer());
    if (customTermAnalyzer != null) {
      dc.addTermAnalyzer(customTermAnalyzer); // blacklist
    }
    
    if (ta != null) {
      dc.addTermAnalyzer(ta);
    }
    dc.addTermAnalyzer(new RelativeScore());
    
    // analyze document
    dc.analyzeDocument(doc);
    dc.analyzeTerms();    
  }
  
  public TermDatabase getTermDatabase() {
    return tdb;
  }

  /**
   * INTERNAL: Returns the topics that matches the given variant.
   */
  public Collection<TopicIF> getTopics(Variant variant) {
    return ta.getTopics(variant);
  }

  public Collection<TopicIF> getCandidateTypes() {
    return ta.getCandidateTypes();
  }

  public Collection<TopicMapAnalyzer.AssociationType> getAssociationTypes() {
    return ta.getAssociationTypes();
  }
  
}
