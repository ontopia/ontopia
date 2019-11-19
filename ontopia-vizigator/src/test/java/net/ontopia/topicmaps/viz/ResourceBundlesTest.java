/*
 * #!
 * Ontopia Vizigator
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

package net.ontopia.topicmaps.viz;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class ResourceBundlesTest {

  /**
   * Checks all properties files in a given directory for consistency
   * against the master file. Note that the properties are loaded from
   * the classpath.
   */
  @Test
  public void testTranslationsAreConsistent() throws IOException {
    String languages[] = { "de", "no", "ja" };
    
    Properties master = loadProperties("messages.properties");
    for (int ix = 0; ix < languages.length; ix++) {
      String file = "messages_" + languages[ix] + ".properties";
      Properties trans = loadProperties(file);
      List missing = new ArrayList();
      List extra = new ArrayList();
      
      for (Object prop : trans.keySet()) {
        if (!master.containsKey(prop))
          extra.add(prop);
      }
      
      for (Object prop : master.keySet()) {
        if (!trans.containsKey(prop))
          missing.add(prop);
      }

      Assert.assertTrue(buildReport(file, missing, extra),
                 missing.isEmpty() && extra.isEmpty());
    }
  }

  private Properties loadProperties(String file) throws IOException {
    Properties props = new Properties();
    InputStream inputStream = VizController.class.getResourceAsStream(file);
    props.load(inputStream);
    inputStream.close();
    return props;
  }

  /**
   * Builds a readable error message listing everything that's wrong.
   */
  private String buildReport(String file, List missing, List extra) {
    String msg = file;
    if (!missing.isEmpty())
      msg += " is missing: " + StringUtils.join(missing, ", ");
    if (!extra.isEmpty())
      msg += " has extra: " + StringUtils.join(extra, ", ");
    return msg;
  }
}
