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

import net.ontopia.topicmaps.core.TestFactoryIF;
import org.junit.Assert;
import org.junit.Test;

public class OccurrenceTest extends net.ontopia.topicmaps.core.OccurrenceTest {

  @Override
  protected TestFactoryIF getFactory() throws Exception {
    return new RDBMSTestFactory();
  }

  @Test
  public void testIssue494() throws Exception {
    // bug is only triggered if the value is committed
    topicmap.getStore().commit();
    Assert.assertNotNull("Bug 494: empty occurrence value returned as null", occurrence.getValue());
  }
}
