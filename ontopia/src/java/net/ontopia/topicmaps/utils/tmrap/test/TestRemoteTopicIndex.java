
// $Id: TestRemoteTopicIndex.java,v 1.6 2008/07/18 13:26:05 lars.garshol Exp $

package net.ontopia.topicmaps.utils.tmrap.test;

import java.io.Writer;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.ServletException;

import org.xml.sax.InputSource;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.utils.URIUtils;
import net.ontopia.topicmaps.utils.tmrap.RAPServlet;
import net.ontopia.topicmaps.utils.tmrap.RemoteTopicIndex;
import net.ontopia.topicmaps.utils.tmrap.TopicPage;

/**
 * RemoteTopicIndex is used by the Vizlet to retrieve information from
 * the server.
 */
public class TestRemoteTopicIndex extends TestTMRAPOperation {

  public TestRemoteTopicIndex() {
    super("TestRemoteTopicIndex");
  }
 
  public void testTopicsFromTwoMaps() throws ServletException, IOException {
    // Init
    verifyDirectory(getBaseDir(), "out");    
    String outfile = getOutDir() + "get-topic-page_remote.xtm";
    String outurl = URIUtils.toURL(new File(outfile)).toString();
    
    // Write get-topic-page result to a file
    Writer out = new FileWriter(outfile);
    doGet(uriPrefix + "get-topic-page",
          "topicmap=opera.xtm&" +
          "identifier=http://www.topicmaps.org/xtm/1.0/country.xtm%23IT",
          paramsTable, rapServlet, out, 200);
    out.close();

    // Read response with RemoteTopicIndex
    RemoteTopicIndex ix = new FileTopicIndex(null, outurl);
    Collection pages = ix.getTopicPages(Collections.EMPTY_SET,
                                        Collections.EMPTY_SET,
                                        Collections.EMPTY_SET);
    assertTrue("Wrong number of pages: " + pages.size(), pages.size() == 1);

    TopicPage page = (TopicPage) pages.iterator().next();
    assertTrue("Bad view URL in topic page: " + page.getURL(),
               "http://localhost:8080/omnigator/plugins/viz/get-topic/models/topic_complete.jsp?tm=opera.xtm&id=432".equals(page.getURL()));
  }

  // --- Internal classes

  /**
   * We need to override getInputSource in order to make the index
   * read correctly from file.
   */
  class FileTopicIndex extends RemoteTopicIndex {
    public FileTopicIndex(String edit, String view) {
      super(edit, view);
    }
    
    protected InputSource getInputSource(String method, String params,
                                         boolean compress)
      throws IOException {
      return new InputSource(viewBaseuri);
    }
  }
}
