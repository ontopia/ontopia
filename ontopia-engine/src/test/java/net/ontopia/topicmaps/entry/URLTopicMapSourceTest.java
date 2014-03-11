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

package net.ontopia.topicmaps.entry;

import java.util.Collection;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapReference;
import net.ontopia.topicmaps.xml.XTMTopicMapReference;

public class URLTopicMapSourceTest extends AbstractTopicMapSourceTest {

  public URLTopicMapSourceTest(String name) {
    super(name);
  }

  // --- Test cases (XTM)

  public void testXTM() {
    URLTopicMapSource source = new URLTopicMapSource("file:/tmp/foobar.xtm");
    source.setId("fooid");
    source.setTitle("footitle");
    source.setSyntax("XTM");

    // run abstract topic map source tests
    doAbstractTopicMapSourceTests(source);
  }

  public void testXTM1() {
    URLTopicMapSource source = new URLTopicMapSource("file:/tmp/foobar.xtm");
    source.setId("fooid");
    source.setTitle("footitle");
    source.setSyntax("XTM");
    verifyXTMSource(source);
  }

  public void testXTM2() {
    URLTopicMapSource source = new URLTopicMapSource();
    source.setUrl("file:/tmp/foobar.xtm");
    source.setId("fooid");
    source.setTitle("footitle");
    verifyXTMSource(source);
  }

  protected void verifyXTMSource(URLTopicMapSource source) {
    Collection refs = source.getReferences();
    assertTrue("URLTopicMapSource.getReferences().size() != 1", refs.size() == 1);
    TopicMapReferenceIF ref = (TopicMapReferenceIF)refs.iterator().next();
    assertTrue("!TopicMapReference.getId().equals('foobar')", "fooid".equals(ref.getId()));
    assertTrue("!TopicMapReference.getTitle().equals('foobar')", "footitle".equals(ref.getTitle()));    
    assertTrue("!(TopicMapReferenceIF instanceof XTMTopicMapReference)", ref instanceof XTMTopicMapReference);
  }

  // --- Test cases (LTM)

  public void testLTM1() {
    URLTopicMapSource source = new URLTopicMapSource("file:/tmp/foobar.ltm");
    source.setId("fooid");
    source.setTitle("footitle");
    source.setSyntax("LTM");
    verifyLTMSource(source);
  }

  public void testLTM2() {
    URLTopicMapSource source = new URLTopicMapSource();
    source.setUrl("file:/tmp/foobar.ltm");
    source.setId("fooid");
    source.setTitle("footitle");
    verifyLTMSource(source);
  }

  protected void verifyLTMSource(URLTopicMapSource source) {
    Collection refs = source.getReferences();
    assertTrue("URLTopicMapSource.getReferences().size() != 1", refs.size() == 1);
    TopicMapReferenceIF ref = (TopicMapReferenceIF)refs.iterator().next();
    assertTrue("!TopicMapReference.getId().equals('foobar')", "fooid".equals(ref.getId()));
    assertTrue("!TopicMapReference.getTitle().equals('foobar')", "footitle".equals(ref.getTitle()));    
    assertTrue("!(TopicMapReferenceIF instanceof LTMTopicMapReference)", ref instanceof LTMTopicMapReference);
  }
}
