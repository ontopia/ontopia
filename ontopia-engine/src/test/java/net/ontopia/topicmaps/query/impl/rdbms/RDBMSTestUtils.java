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

package net.ontopia.topicmaps.query.impl.rdbms;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTestFactory;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapStore;
import net.ontopia.topicmaps.query.core.AbstractQueryTest;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.TestFileUtils;
import org.junit.Ignore;
import org.xml.sax.SAXException;

@Ignore
public class RDBMSTestUtils {

  private final static String testdataDirectory = "query";

  // ===== Helper methods (topic maps)

  public static void load(AbstractQueryTest test, String filename) throws IOException {
    URL file = TestFileUtils.getTestInputURL(testdataDirectory, filename);

    checkDatabasePresence();

    RDBMSTopicMapStore store = new RDBMSTopicMapStore();
    test.topicmap = store.getTopicMap();
    test.builder = store.getTopicMap().getBuilder();
    test.base = new URILocator(file);
    store.setBaseAddress(test.base);

    TopicMapReaderIF importer = ImportExportUtils.getReader(file.toString());
    if (importer instanceof XTMTopicMapReader) {
      ((XTMTopicMapReader) importer).setValidation(false);
    }
    importer.importInto(test.topicmap);

    // NOTE: Query processor must have base set, because of the way
    // the test suite looks up source locators.
    //! test.processor = new QueryProcessor(test.topicmap, test.base);
    test.processor = QueryUtils.createQueryProcessor(test.topicmap, test.base);
  }

  private static void checkDatabasePresence() throws IOException {
    try {
      RDBMSTestFactory.checkDatabasePresence();
    } catch (SQLException e) {
      throw new IOException(e);
    } catch (SAXException e) {
      throw new IOException(e);
    }
  }

  public static void makeEmpty(AbstractQueryTest test) {
    try {
      checkDatabasePresence();
      RDBMSTopicMapStore store = new RDBMSTopicMapStore();
      test.topicmap = store.getTopicMap();
      test.builder = test.topicmap.getBuilder();
      //! test.processor = new QueryProcessor(test.topicmap);
      URL filename = TestFileUtils.getTestInputURL("");
      test.processor = QueryUtils.createQueryProcessor(test.topicmap, new URILocator(filename));
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }
}
