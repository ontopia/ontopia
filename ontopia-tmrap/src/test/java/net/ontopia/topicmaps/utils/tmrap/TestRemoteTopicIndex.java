/*
 * #!
 * Ontopia TMRAP
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

package net.ontopia.topicmaps.utils.tmrap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import javax.servlet.ServletException;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.InputSource;

/**
 * RemoteTopicIndex is used by the Vizlet to retrieve information from
 * the server.
 */
public class TestRemoteTopicIndex extends TestTMRAPOperation {

  @Test
  public void testTopicsFromTwoMaps() throws ServletException, IOException {
    // Init
    File outfile = TestFileUtils.getTestOutputFile(testdataDirectory, "out", "get-topic-page_remote.xtm");
    String outurl = outfile.toURL().toString();
    
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
    Assert.assertTrue("Wrong number of pages: " + pages.size(), pages.size() == 1);

    TopicPage page = (TopicPage) pages.iterator().next();
    Assert.assertTrue("Bad view URL in topic page: " + page.getURL(),
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
    
    @Override
    protected InputSource getInputSource(String method, String params,
                                         boolean compress)
      throws IOException {
      return new InputSource(viewBaseuri);
    }
  }
}
