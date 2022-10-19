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
package net.ontopia.topicmaps.impl.tmapi2;

import java.util.Set;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.tmapi.core.Locator;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapExistsException;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

@Ignore
public class RDBMSTopicMapSystemTest {

  private TopicMapSystemFactory tmsf;
  private TopicMapSystem tms;

  private Locator locFirst; 

  @Before
  protected void setUp() throws Exception {
    tmsf = TopicMapSystemFactory.newInstance();
    
    tmsf.setProperty("net.ontopia.topicmaps.store", "rdbms");
    tmsf.setProperty("net.ontopia.topicmaps.impl.rdbms.Database", "mysql");
    tmsf.setProperty("net.ontopia.topicmaps.impl.rdbms.ConnectionString", "jdbc:mysql://localhost/ontopia");
    tmsf.setProperty("net.ontopia.topicmaps.impl.rdbms.DriverClass", "com.mysql.jdbc.Driver");
    tmsf.setProperty("net.ontopia.topicmaps.impl.rdbms.UserName", "ontopia");
    tmsf.setProperty("net.ontopia.topicmaps.impl.rdbms.Password", "ontopia");
    tmsf.setProperty("net.ontopia.topicmaps.impl.rdbms.ConnectionPool", "true");

    tms = tmsf.newTopicMapSystem();

    locFirst = tms.createLocator("http://ontopia.net/first");
  }

  @After
  protected void tearDown() throws Exception {
    tms.close();
  }

  @Test
  public void testAll() {
    
    // first clean all existing topic maps
    Set<Locator> locators = tms.getLocators();
    for (Locator loc : locators) {
      TopicMap tm = tms.getTopicMap(loc);
      tm.remove();
    }
    
    TopicMap tm;

    try {
      tm = tms.createTopicMap(locFirst);
      Assert.assertNotNull("could not create new TopicMap", tm);

    } catch (TopicMapExistsException e) {
      Assert.fail("failed to create new TopicMap in empty TopicMapSystem");
    }

    TopicMap tm2 = tms.getTopicMap(locFirst);
    Assert.assertNotNull("could not get newly created TopicMap", tm2);

    tm2.remove();

    tm = tms.getTopicMap(locFirst);
    Assert.assertNull("TopicMap has not been removed from TopicMapSystem after remove operation", tm);
  }
}
