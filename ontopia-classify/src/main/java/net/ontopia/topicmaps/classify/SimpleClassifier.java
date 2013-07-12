
package net.ontopia.topicmaps.classify;

import net.ontopia.topicmaps.core.TopicMapIF;

/**
 * PUBLIC: A simple top-level API for classifying content.
 * @since 5.1.0
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

  public static TermDatabase classify(byte[] content) {
    return classify(content, null);
  }

  public static TermDatabase classify(byte[] content, TopicMapIF topicmap) {
    // create classifier
    TopicMapClassification tcl = (topicmap == null)
      ? new TopicMapClassification()
      : new TopicMapClassification(topicmap);

    // read content
    ClassifiableContentIF cc = ClassifyUtils.getClassifiableContent(content);

    // classify content
    tcl.classify(cc);

    // return the terms
    return tcl.getTermDatabase();
  }
  
}