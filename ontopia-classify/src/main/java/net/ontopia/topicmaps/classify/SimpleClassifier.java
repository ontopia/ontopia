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
    if (topicmap == null) {
      tcl = new TopicMapClassification();
    } else {
      tcl = new TopicMapClassification(topicmap);
    }

    // read document
    ClassifiableContentIF cc = ClassifyUtils.getClassifiableContent(uri_or_file);

    // classify document
    tcl.classify(cc);

    // return the terms
    return tcl.getTermDatabase();
  }

  /**
   * PUBLIC: Extracts keywords from the given content and returns
   * a TermDatabase representing the results.
   * @since 5.3.0
   */
  public static TermDatabase classify(byte[] content) {
    return classify(content, null);
  }

  /**
   * PUBLIC: Extracts keywords from the given content, using the
   * information in the topic map, and returns a TermDatabase
   * representing the results.
   * @since 5.3.0
   */
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