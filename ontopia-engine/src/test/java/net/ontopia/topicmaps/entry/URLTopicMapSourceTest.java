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
import org.junit.Assert;
import org.junit.Test;

public class URLTopicMapSourceTest extends AbstractTopicMapSourceTest {

  // --- Test cases (XTM)

  @Test
  public void testXTM() {
    URLTopicMapSource source = new URLTopicMapSource("file:/tmp/foobar.xtm");
    source.setId("fooid");
    source.setTitle("footitle");
    source.setSyntax("XTM");

    // run abstract topic map source tests
    assertCompliesToAbstractTopicMapSource(source);
  }

  @Test
  public void testXTM1() {
    URLTopicMapSource source = new URLTopicMapSource("file:/tmp/foobar.xtm");
    source.setId("fooid");
    source.setTitle("footitle");
    source.setSyntax("XTM");
    assertXTMSource(source);
  }

  @Test
  public void testXTM2() {
    URLTopicMapSource source = new URLTopicMapSource();
    source.setUrl("file:/tmp/foobar.xtm");
    source.setId("fooid");
    source.setTitle("footitle");
    assertXTMSource(source);
  }

  protected void assertXTMSource(URLTopicMapSource source) {
    Collection refs = source.getReferences();
    Assert.assertTrue("URLTopicMapSource.getReferences().size() != 1", refs.size() == 1);
    TopicMapReferenceIF ref = (TopicMapReferenceIF)refs.iterator().next();
    Assert.assertTrue("!TopicMapReference.getId().equals('foobar')", "fooid".equals(ref.getId()));
    Assert.assertTrue("!TopicMapReference.getTitle().equals('foobar')", "footitle".equals(ref.getTitle()));    
    Assert.assertTrue("!(TopicMapReferenceIF instanceof XTMTopicMapReference)", ref instanceof XTMTopicMapReference);
  }

  // --- Test cases (LTM)

  @Test
  public void testLTM1() {
    URLTopicMapSource source = new URLTopicMapSource("file:/tmp/foobar.ltm");
    source.setId("fooid");
    source.setTitle("footitle");
    source.setSyntax("LTM");
    assertLTMSource(source);
  }

  @Test
  public void testLTM2() {
    URLTopicMapSource source = new URLTopicMapSource();
    source.setUrl("file:/tmp/foobar.ltm");
    source.setId("fooid");
    source.setTitle("footitle");
    assertLTMSource(source);
  }

  protected void assertLTMSource(URLTopicMapSource source) {
    Collection refs = source.getReferences();
    Assert.assertTrue("URLTopicMapSource.getReferences().size() != 1", refs.size() == 1);
    TopicMapReferenceIF ref = (TopicMapReferenceIF)refs.iterator().next();
    Assert.assertTrue("!TopicMapReference.getId().equals('foobar')", "fooid".equals(ref.getId()));
    Assert.assertTrue("!TopicMapReference.getTitle().equals('foobar')", "footitle".equals(ref.getTitle()));    
    Assert.assertTrue("!(TopicMapReferenceIF instanceof LTMTopicMapReference)", ref instanceof LTMTopicMapReference);
  }
}
