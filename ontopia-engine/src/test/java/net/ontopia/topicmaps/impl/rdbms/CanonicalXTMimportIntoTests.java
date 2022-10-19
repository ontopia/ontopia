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

package net.ontopia.topicmaps.impl.rdbms;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.xml.CanonicalTopicMapWriter;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import org.junit.BeforeClass;

public class CanonicalXTMimportIntoTests extends net.ontopia.topicmaps.xml.CanonicalXTMimportIntoTests {

  @BeforeClass
  public static void checkDatabasePresence() throws Exception {
    RDBMSTestFactory.checkDatabasePresence();
  }

  public CanonicalXTMimportIntoTests(URL inputFile, String filename) {
    super(inputFile, filename);
  }

  @Override
  protected void canonicalize(URL infile, File outfile) throws IOException {
    // Import document
    TopicMapStoreIF store1 = new RDBMSTopicMapStore();
    TopicMapIF source1 = store1.getTopicMap();

    // Get hold of topic map id
    long topicmap_id = Long.parseLong(source1.getObjectId().substring(1));
    
    XTMTopicMapReader reader = new XTMTopicMapReader(infile);
    reader.setValidation(false);
    reader.importInto(source1);
    
    store1.commit();
    store1.close();

    // Canonicalize document
    TopicMapStoreIF store2 = new RDBMSTopicMapStore(topicmap_id);
    TopicMapIF source2 = store2.getTopicMap();

    CanonicalTopicMapWriter cwriter = new CanonicalTopicMapWriter(outfile);
    cwriter.setBaseLocator(new URILocator(infile));
    cwriter.write(source2);

    store2.delete(true);
    //! store2.close();
  }
  
}
