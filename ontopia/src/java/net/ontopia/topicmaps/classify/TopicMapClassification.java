
// $Id: TopicMapClassification.java,v 1.13 2007/06/01 10:11:27 geir.gronmo Exp $

package net.ontopia.topicmaps.classify;

import java.util.*;

import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.query.core.*;
import net.ontopia.topicmaps.query.utils.*;

/**
 * INTERNAL: 
 */
public class TopicMapClassification {

  TermDatabase tdb;
  TopicMapAnalyzer ta;
  TermAnalyzerIF customTermAnalyzer;
  
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

    // FIXME: wrap and hand over to compound analyzer instead?
    dc.addTermAnalyzer(language.getStopListAnalyzer());
    if (customTermAnalyzer != null) dc.addTermAnalyzer(customTermAnalyzer); // blacklist

    dc.addTermAnalyzer(ca);

    // run stop list analyzer after compounds have been made    
    dc.addTermAnalyzer(language.getStopListAnalyzer());
    if (customTermAnalyzer != null) dc.addTermAnalyzer(customTermAnalyzer); // blacklist
    
    if (ta != null) dc.addTermAnalyzer(ta);
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
  public Collection getTopics(Variant variant) {
    return ta.getTopics(variant);
  }

  public Collection getCandidateTypes() {
    return ta.getCandidateTypes();
  }

  public Collection getAssociationTypes() {
    return ta.getAssociationTypes();
  }
  
}
