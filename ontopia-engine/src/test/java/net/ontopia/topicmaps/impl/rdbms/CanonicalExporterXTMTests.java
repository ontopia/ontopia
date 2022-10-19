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
import net.ontopia.topicmaps.impl.utils.AbstractTopicMapStore;
import net.ontopia.topicmaps.xml.CanonicalTopicMapWriter;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.topicmaps.xml.XTMTopicMapWriter;
import org.junit.BeforeClass;

public class CanonicalExporterXTMTests extends net.ontopia.topicmaps.xml.CanonicalExporterXTMTests {

  @BeforeClass
  public static void checkDatabasePresence() throws Exception {
    RDBMSTestFactory.checkDatabasePresence();
  }

  public CanonicalExporterXTMTests(URL inputFile, String filename) {
    super(inputFile, filename);
  }

  protected boolean getExportReadOnly() {
    return false;
  }

  @Override
  protected void canonicalize(URL infile, File tmpfile, File outfile) throws IOException {    
    // Import document
    TopicMapStoreIF store1 = new RDBMSTopicMapStore();
    TopicMapIF source1 = store1.getTopicMap();

    // Get hold of topic map id
    long topicmap_id1 = Long.parseLong(source1.getObjectId().substring(1));
    
    XTMTopicMapReader reader = new XTMTopicMapReader(infile);
    reader.setValidation(false);
    reader.importInto(source1);
    store1.commit();
    store1.close();

    // Export document
    TopicMapStoreIF store2 = new RDBMSTopicMapStore(topicmap_id1);
    ((AbstractTopicMapStore)store2).setReadOnly(getExportReadOnly());
    TopicMapIF source2 = store2.getTopicMap();
    new XTMTopicMapWriter(tmpfile).write(source2);
    store2.close();    
    TopicMapStoreIF store2_ = new RDBMSTopicMapStore(topicmap_id1);
    store2_.delete(true);

    // Read exported document
    TopicMapStoreIF store3 = new RDBMSTopicMapStore();
    TopicMapIF source3 = store3.getTopicMap();    
    reader = new XTMTopicMapReader(tmpfile);
    reader.setValidation(false);
    reader.importInto(source3);
    store3.commit();
    store3.close();

    // Get hold of topic map id
    long topicmap_id3 = Long.parseLong(source3.getObjectId().substring(1));

    // Canonicalize document
    TopicMapStoreIF store4 = new RDBMSTopicMapStore(topicmap_id3);
    ((AbstractTopicMapStore)store4).setReadOnly(getExportReadOnly());
    TopicMapIF source4 = store4.getTopicMap();

    CanonicalTopicMapWriter cwriter = new CanonicalTopicMapWriter(outfile);
    cwriter.setBaseLocator(new URILocator(tmpfile));
    cwriter.write(source4);

    // Make sure topic map goes away
    store4.close();    
    TopicMapStoreIF store4_ = new RDBMSTopicMapStore(topicmap_id3);
    store4_.delete(true);
  }
  
}
