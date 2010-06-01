
package net.ontopia.topicmaps.classify;

import net.ontopia.topicmaps.core.TopicMapIF;

/**
 * PUBLIC: A simple top-level API for classifying content.
 * @since %NEXT%
 */
public class SimpleClassifier {

  /**
   * PUBLIC: Extracts keywords from the given URI or file and returns
   * a TermDatabase representing the results.
   */
  public static TermDatabase classify(String uri_or_file) {
    return classify(uri_or_file, null);
  }

  /**
   * PUBLIC: Extracts keywords from the given URI or file, using the
   * information in the topic map, and returns a TermDatabase
   * representing the results.
   */
  public static TermDatabase classify(String uri_or_file, TopicMapIF topicmap) {
    // create classifier
    TopicMapClassification tcl;
    if (topicmap == null)
      tcl = new TopicMapClassification();
    else
      tcl = new TopicMapClassification(topicmap);

    // read document
    ClassifiableContentIF cc = ClassifyUtils.getClassifiableContent(uri_or_file);

    // classify document
    tcl.classify(cc);

    // return the terms
    return tcl.getTermDatabase();
  }
  
}