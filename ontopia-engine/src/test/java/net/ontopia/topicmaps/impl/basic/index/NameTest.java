
package net.ontopia.topicmaps.impl.basic.index;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AbstractTopicMapTest;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.index.NameIndexIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapReader;
import junit.framework.TestCase;

public class NameTest extends TestCase {
  protected NameIndexIF index;
  protected TopicMapBuilderIF builder;
  protected TopicMapIF topicmap;
    
  public NameTest(String name) {
    super(name);
  }

  protected void setUp() {
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

  public void testTopicNames() {
    // STATE 1: empty topic map
    // assertTrue("index finds spurious base names",
    //        index.getTopicNameValues().size() == 0);

    assertTrue("index finds base names it shouldn't",
               index.getTopicNames("akka bakka").size() == 0);

        
    // STATE 2: topic map has some topics in it
    TopicIF t1 = builder.makeTopic();
    TopicNameIF bn1 = builder.makeTopicName(t1, "bonka rakka");
    TopicNameIF bn2 = builder.makeTopicName(t1, "");

    assertTrue("couldn't find base name via string",
               index.getTopicNames("bonka rakka").size() == 1);
    assertTrue("wrong base name found via string",
               index.getTopicNames("bonka rakka").iterator().next().equals(bn1));

    // assertTrue("value string missing from value string collection",
    //        index.getTopicNameValues().size() == 2);
    // assertTrue("value string missing from value string collection",
    //        index.getTopicNameValues().contains("bonka rakka"));
    // assertTrue("null missing from value string collection",
    //        index.getTopicNameValues().contains(null));

    assertTrue("couldn't find base name via \"\"",
               index.getTopicNames("").size() == 1);
    assertTrue("wrong base name found via \"\"",
               index.getTopicNames("").iterator().next().equals(bn2));
        
    // STATE 3: topic map with duplicates
    TopicNameIF bn3 = builder.makeTopicName(t1, "bonka rakka");
        
    // assertTrue("duplicate base name string not filtered out",
    //        index.getTopicNameValues().size() == 2);
    assertTrue("second base name not found via string",
               index.getTopicNames("bonka rakka").size() == 2);


    // STATE 4: base names with difficult characters
    TopicNameIF bn4 = builder.makeTopicName(t1, "Erlend \u00d8verby");
    TopicNameIF bn5 = builder.makeTopicName(t1, "Kana: \uFF76\uFF85"); // half-width katakana

    assertTrue("couldn't find base name via latin1 string",
               index.getTopicNames("Erlend \u00d8verby").size() == 1);
    assertTrue("wrong base name found via latin1 string",
               index.getTopicNames("Erlend \u00d8verby").iterator().next().equals(bn4));

    assertTrue("couldn't find base name via hw-kana string",
               index.getTopicNames("Kana: \uFF76\uFF85").size() == 1);
    assertTrue("wrong base name found via hw-kana string",
               index.getTopicNames("Kana: \uFF76\uFF85").iterator().next().equals(bn5));
        
  }

  public void testVariants() {
    // STATE 1: empty topic map
    assertTrue("index finds spurious variant names",
               index.getVariants("akka bakka").size() == 0);

    // assertTrue("index finds variant vakyes it shouldn't",
    //        index.getVariantValues().size() == 0);

        
    // STATE 2: topic map has some topics in it
    TopicIF t1 = builder.makeTopic();
    TopicNameIF bn1 = builder.makeTopicName(t1, "");
    VariantNameIF v1 = builder.makeVariantName(bn1, "bonka rakka");
    VariantNameIF v2 = builder.makeVariantName(bn1, "");

    assertTrue("couldn't find variant name via string",
               index.getVariants("bonka rakka").size() == 1);
    assertTrue("wrong variant name found via string",
               index.getVariants("bonka rakka").iterator().next().equals(v1));

    // assertTrue("value string missing from value string collection",
    //        index.getVariantValues().size() == 2);
    // assertTrue("value string missing from value string collection",
    //        index.getVariantValues().contains("bonka rakka"));
    // assertTrue("null missing from value string collection",
    //        index.getVariantValues().contains(null));

    assertTrue("couldn't find variant name via \"\"",
               index.getVariants("").size() == 1);
    assertTrue("wrong base name found via \"\"",
               index.getVariants("").iterator().next().equals(v2));
        
    // STATE 3: topic map with duplicates
    VariantNameIF v3 = builder.makeVariantName(bn1, "bonka rakka");
        
    assertTrue("duplicate variant name string not filtered out",
               index.getVariants("bonka rakka").size() == 2);
    // assertTrue("second variant name not found via string",
    //        index.getVariantValues().size() == 2);


    // STATE 4: variant names with difficult characters
    VariantNameIF v4 = builder.makeVariantName(bn1, "Erlend \u00d8verby");
    VariantNameIF v5 = builder.makeVariantName(bn1, "Kana: \uFF76\uFF85"); // half-width katakana

    assertTrue("couldn't find variant name via latin1 string",
               index.getVariants("Erlend \u00d8verby").size() == 1);
    assertTrue("wrong variant name found via latin1 string",
               index.getVariants("Erlend \u00d8verby").iterator().next().equals(v4));

    assertTrue("couldn't find variant name via hw-kana string",
               index.getVariants("Kana: \uFF76\uFF85").size() == 1);
    assertTrue("wrong variant name found via hw-kana string",
               index.getVariants("Kana: \uFF76\uFF85").iterator().next().equals(v5));
        
  }

  public void testLTMImport() throws IOException, MalformedURLException {
    String ltm = "    [random-id : user = \"Karl Popper\" = \"popper\" / username] " +
    "ansatt-ved(ontopia-uni : arbeidsgiver, random-id : ansatt)";

    LocatorIF base = new URILocator("http://www.example.com");
    LTMTopicMapReader reader = new LTMTopicMapReader(new StringReader(ltm), base);
    reader.importInto(topicmap);
    topicmap.getStore().commit();

    assertTrue("couldn't find base name via string value",
               index.getTopicNames("popper").size() == 1);

    TopicNameIF bn = (TopicNameIF) index.getTopicNames("popper").iterator().next();
    bn.setValue("popper");

    topicmap.getStore().commit();
    
    assertTrue("couldn't find base name via string value after modification",
               index.getTopicNames("popper").size() == 1);
  }
  
}
