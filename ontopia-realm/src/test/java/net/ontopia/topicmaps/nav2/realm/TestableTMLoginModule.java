/*
 * #!
 * Ontopia Realm
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

package net.ontopia.topicmaps.nav2.realm;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.TestFileUtils;
import org.junit.Ignore;

/**
 * INTERNAL: Class extending TMLoginModule so that it can be properly
 * tested outside of a J2EE environment.
 */

@Ignore
public class TestableTMLoginModule extends TMLoginModule {

  private final static String testdataDirectory = "realm";
  
  /**
   * INTERNAL: Return a topicmap via a hard-coded file-path.
   */
  @Override
  protected TopicMapIF getTopicMap() {
    try {
      String topicmapFile = TestFileUtils.getTestInputFile(testdataDirectory, "tmloginmodule.ltm");
      return ImportExportUtils.getReader(topicmapFile).read();
    } catch (java.io.IOException e) {
      throw new OntopiaRuntimeException(e);
    } 
  }

}
