package net.ontopia.topicmaps.utils.jtm;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.jtm.JTMTopicMapReader;

import org.junit.Assert;
import org.junit.Test;

public class JTMTopicMapWriterTest {

  /**
   * TODO: rewrite tests, this is just a proof-of-concept test.
   */
  @Test
  @SuppressWarnings("unchecked")
  public void testImportExport() throws IOException {
    String json = "{\"version\":\"1.0\", \"item_type\":\"topicmap\", " +
    "\"topics\":[ {\"subject_identifiers\":[\"http://psi.topincs.com/movies/dear-wendy\"], \"names\":[ {\"value\":\"Dear Wendy\", \"type\":\"si:http://psi.topincs.com/title\", \"scope\":[ \"si:http://www.topicmaps.org/xtm/1.0/country.xtm#US\", \"si:http://www.topicmaps.org/xtm/1.0/country.xtm#DE\"]}], \"occurrences\":[ {\"value\":\"2005\", \"type\":\"si:http://psi.topincs.com/publication-year\", \"datatype\":\"http://www.w3.org/2001/XMLSchema#gYear\"}]}], \"associations\":[ {\"type\":\"si:http://psi.topicmaps.org/iso13250/model/type-instance\", \"roles\":[ {\"player\":\"si:http://psi.topincs.com/movies/dear-wendy\", \"type\":\"si:http://psi.topicmaps.org/iso13250/model/instance\"}, {\"player\":\"si:http://psi.topincs.com/movie\", \"type\":\"si:http://psi.topicmaps.org/iso13250/model/type\"}]}]}";

    Reader r = new StringReader(json);

    LocatorIF base = new URILocator("http://www.test.org");
    JTMTopicMapReader reader = new JTMTopicMapReader(r, base);
    TopicMapIF tm = reader.read();

    TopicIF wendy = tm.getTopicBySubjectIdentifier(new URILocator("http://psi.topincs.com/movies/dear-wendy"));
    TopicIF movie = tm.getTopicBySubjectIdentifier(new URILocator("http://psi.topincs.com/movie"));
    
    Collection<TopicIF> types = wendy.getTypes();
    Assert.assertEquals(1, types.size());
    Assert.assertEquals(movie, types.iterator().next());

    //JTMTopicMapWriter writer = new JTMTopicMapWriter(System.out);
    // LocatorIF base = tm.getStore().getBaseAddress();
    // TMObjectIF obj =
    // tm.getObjectByItemIdentifier(base.resolveAbsolute("#mother"));
    //writer.write((TopicNameIF) wendy.getTopicNames().iterator().next());
  }
}
