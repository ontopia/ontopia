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

package net.ontopia.topicmaps.impl.basic.index;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.index.NameIndexIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapReader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NameTest {
  protected NameIndexIF index;
  protected TopicMapBuilderIF builder;
  protected TopicMapIF topicmap;
    
  @Before
  public void setUp() {
    topicmap = makeTopicMap();
    index = (NameIndexIF) topicmap.getIndex("net.ontopia.topicmaps.core.index.NameIndexIF");
  }

  // intended to be overridden
  protected TopicMapIF makeTopicMap() {
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    builder = store.getTopicMap().getBuilder();
    return store.getTopicMap();
  }
    
  // --- Test cases

  @Test
  public void testTopicNames() {
    // STATE 1: empty topic map
    // Assert.assertTrue("index finds spurious base names",
    //        index.getTopicNameValues().size() == 0);

    Assert.assertTrue("index finds base names it shouldn't",
               index.getTopicNames("akka bakka").size() == 0);

        
    // STATE 2: topic map has some topics in it
    TopicIF t1 = builder.makeTopic();
    TopicNameIF bn1 = builder.makeTopicName(t1, "bonka rakka");
    TopicNameIF bn2 = builder.makeTopicName(t1, "");

    Assert.assertTrue("couldn't find base name via string",
               index.getTopicNames("bonka rakka").size() == 1);
    Assert.assertTrue("wrong base name found via string",
               index.getTopicNames("bonka rakka").iterator().next().equals(bn1));

    // Assert.assertTrue("value string missing from value string collection",
    //        index.getTopicNameValues().size() == 2);
    // Assert.assertTrue("value string missing from value string collection",
    //        index.getTopicNameValues().contains("bonka rakka"));
    // Assert.assertTrue("null missing from value string collection",
    //        index.getTopicNameValues().contains(null));

    Assert.assertTrue("couldn't find base name via \"\"",
               index.getTopicNames("").size() == 1);
    Assert.assertTrue("wrong base name found via \"\"",
               index.getTopicNames("").iterator().next().equals(bn2));
        
    // STATE 3: topic map with duplicates
    builder.makeTopicName(t1, "bonka rakka");
        
    // Assert.assertTrue("duplicate base name string not filtered out",
    //        index.getTopicNameValues().size() == 2);
    Assert.assertTrue("second base name not found via string",
               index.getTopicNames("bonka rakka").size() == 2);


    // STATE 4: base names with difficult characters
    TopicNameIF bn4 = builder.makeTopicName(t1, "Erlend \u00d8verby");
    TopicNameIF bn5 = builder.makeTopicName(t1, "Kana: \uFF76\uFF85"); // half-width katakana

    Assert.assertTrue("couldn't find base name via latin1 string",
               index.getTopicNames("Erlend \u00d8verby").size() == 1);
    Assert.assertTrue("wrong base name found via latin1 string",
               index.getTopicNames("Erlend \u00d8verby").iterator().next().equals(bn4));

    Assert.assertTrue("couldn't find base name via hw-kana string",
               index.getTopicNames("Kana: \uFF76\uFF85").size() == 1);
    Assert.assertTrue("wrong base name found via hw-kana string",
               index.getTopicNames("Kana: \uFF76\uFF85").iterator().next().equals(bn5));
        
  }

  @Test
  public void testVariants() {
    // STATE 1: empty topic map
    Assert.assertTrue("index finds spurious variant names",
               index.getVariants("akka bakka").size() == 0);

    // Assert.assertTrue("index finds variant vakyes it shouldn't",
    //        index.getVariantValues().size() == 0);

        
    // STATE 2: topic map has some topics in it
    TopicIF t1 = builder.makeTopic();
    TopicNameIF bn1 = builder.makeTopicName(t1, "");
    VariantNameIF v1 = builder.makeVariantName(bn1, "bonka rakka", Collections.<TopicIF>emptySet());
    VariantNameIF v2 = builder.makeVariantName(bn1, "", Collections.<TopicIF>emptySet());

    Assert.assertTrue("couldn't find variant name via string",
               index.getVariants("bonka rakka").size() == 1);
    Assert.assertTrue("wrong variant name found via string",
               index.getVariants("bonka rakka").iterator().next().equals(v1));

    // Assert.assertTrue("value string missing from value string collection",
    //        index.getVariantValues().size() == 2);
    // Assert.assertTrue("value string missing from value string collection",
    //        index.getVariantValues().contains("bonka rakka"));
    // Assert.assertTrue("null missing from value string collection",
    //        index.getVariantValues().contains(null));

    Assert.assertTrue("couldn't find variant name via \"\"",
               index.getVariants("").size() == 1);
    Assert.assertTrue("wrong base name found via \"\"",
               index.getVariants("").iterator().next().equals(v2));
        
    // STATE 3: topic map with duplicates
    builder.makeVariantName(bn1, "bonka rakka", Collections.<TopicIF>emptySet());
        
    Assert.assertTrue("duplicate variant name string not filtered out",
               index.getVariants("bonka rakka").size() == 2);
    // Assert.assertTrue("second variant name not found via string",
    //        index.getVariantValues().size() == 2);


    // STATE 4: variant names with difficult characters
    VariantNameIF v4 = builder.makeVariantName(bn1, "Erlend \u00d8verby", Collections.<TopicIF>emptySet());
    VariantNameIF v5 = builder.makeVariantName(bn1, "Kana: \uFF76\uFF85", Collections.<TopicIF>emptySet()); // half-width katakana

    Assert.assertTrue("couldn't find variant name via latin1 string",
               index.getVariants("Erlend \u00d8verby").size() == 1);
    Assert.assertTrue("wrong variant name found via latin1 string",
               index.getVariants("Erlend \u00d8verby").iterator().next().equals(v4));

    Assert.assertTrue("couldn't find variant name via hw-kana string",
               index.getVariants("Kana: \uFF76\uFF85").size() == 1);
    Assert.assertTrue("wrong variant name found via hw-kana string",
               index.getVariants("Kana: \uFF76\uFF85").iterator().next().equals(v5));
        
  }

  @Test
  public void testLTMImport() throws IOException {
    String ltm = "    [random-id : user = \"Karl Popper\" = \"popper\" / username] " +
    "ansatt-ved(ontopia-uni : arbeidsgiver, random-id : ansatt)";

    LocatorIF base = URILocator.create("http://www.example.com");
    LTMTopicMapReader reader = new LTMTopicMapReader(new StringReader(ltm), base);
    reader.importInto(topicmap);
    topicmap.getStore().commit();

    Assert.assertTrue("couldn't find base name via string value",
               index.getTopicNames("popper").size() == 1);

    TopicNameIF bn = (TopicNameIF) index.getTopicNames("popper").iterator().next();
    bn.setValue("popper");

    topicmap.getStore().commit();
    
    Assert.assertTrue("couldn't find base name via string value after modification",
               index.getTopicNames("popper").size() == 1);
  }
  
}
