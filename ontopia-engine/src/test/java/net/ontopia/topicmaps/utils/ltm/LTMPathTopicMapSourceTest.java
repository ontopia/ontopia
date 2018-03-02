/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2017 The Ontopia Project
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
package net.ontopia.topicmaps.utils.ltm;

import java.io.File;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Test;

public class LTMPathTopicMapSourceTest {

  @Test
  public void testIssue493() {
    TestFileUtils.verifyDirectory(TestFileUtils.getTestdataOutputDirectory(), "issue493");
    LTMPathTopicMapSource source = new LTMPathTopicMapSource(TestFileUtils.getTestdataOutputDirectory() + File.separator + "issue493", ".ltm");
    source.setSupportsCreate(true);
    LTMTopicMapReference created = (LTMTopicMapReference) source.createTopicMap("foo", "foo:bar");
    Assert.assertTrue(created.getURL().toString().endsWith(".ltm"));
  }
}
