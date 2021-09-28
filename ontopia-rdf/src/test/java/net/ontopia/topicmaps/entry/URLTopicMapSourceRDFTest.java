/*
 * #!
 * Ontopia RDF
 * #-
 * Copyright (C) 2001 - 2014 The Ontopia Project
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
package net.ontopia.topicmaps.entry;

import java.util.Collection;
import net.ontopia.topicmaps.utils.rdf.RDFTopicMapReference;

public class URLTopicMapSourceRDFTest extends AbstractTopicMapSourceTest {

  public URLTopicMapSourceRDFTest(String name) {
    super(name);
  }

  // --- Test cases (RDF)
  public void testRDF1() {
    URLTopicMapSource source = new URLTopicMapSource("file:/tmp/foobar.rdf");
    source.setId("fooid");
    source.setTitle("footitle");
    source.setSyntax("RDF");
    verifyRDFSource(source);
  }

  public void testRDF2() {
    URLTopicMapSource source = new URLTopicMapSource();
    source.setUrl("file:/tmp/foobar.rdf");
    source.setId("fooid");
    source.setTitle("footitle");
    verifyRDFSource(source);
  }

  protected void verifyRDFSource(URLTopicMapSource source) {
    Collection refs = source.getReferences();
    assertTrue("URLTopicMapSource.getReferences().size() != 1", refs.size() == 1);
    TopicMapReferenceIF ref = (TopicMapReferenceIF) refs.iterator().next();
    assertTrue("!TopicMapReference.getId().equals('foobar')", "fooid".equals(ref.getId()));
    assertTrue("!TopicMapReference.getTitle().equals('foobar')", "footitle".equals(ref.getTitle()));
    assertTrue("!(TopicMapReferenceIF instanceof RDFTopicMapReference)", ref instanceof RDFTopicMapReference);
  }
}
