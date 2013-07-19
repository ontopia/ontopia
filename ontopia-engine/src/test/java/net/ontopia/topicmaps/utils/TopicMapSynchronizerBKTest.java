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

package net.ontopia.topicmaps.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.ArrayList;
import junit.framework.TestCase;

import net.ontopia.utils.DeciderIF;
import net.ontopia.utils.DeciderUtils;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.xml.CanonicalXTMWriter;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.TestFileUtils;

public class TopicMapSynchronizerBKTest extends TestCase {
  private String ttopicq;
  private String stopicq;
  private DeciderIF tchard;
  private DeciderIF schard;
  private String base;
  
  private final static String testdataDirectory = "tmsync";

  public TopicMapSynchronizerBKTest(String name) {
    super(name);
  }

  public void setUp() throws MalformedURLException {
    String DECL = "using bk for i\"http://psi.bergen.kommune.no/portal/\" ";
    ttopicq = DECL + "select $T from  "+
      "instance-of($T, $TT), { " +
      "  $TT = bk:kategori | " +
      "  $TT = bk:emneord | " +
      "  $TT = bk:ressurs " +
      "}, not(bk:er-privat($T : bk:privat))?";
    stopicq = "select $T from instance-of($T, $TT)?";
    
    List psis = new ArrayList();
    psis.add("http://psi.bergen.kommune.no/portal/forelder-barn");
    psis.add("http://psi.bergen.kommune.no/portal/livsit-relevant-for");
    tchard = TMDeciderUtils.getTypePSIDecider(psis);

    psis.clear();
    schard = DeciderUtils.getTrueDecider();

    String root = TestFileUtils.getTestdataOutputDirectory();
    base = root + File.separator + testdataDirectory + File.separator;

    TestFileUtils.verifyDirectory(base, "out");
  }

  public void testEmptyTM() throws InvalidQueryException, IOException {
    TopicMapIF target = load("bk-empty.ltm");
    TopicMapIF source = load("livsit-1.ltm");

    TopicMapSynchronizer.update(target, ttopicq, tchard,
                                source, stopicq, schard);

    canonicalize("bk-empty.cxtm", target);
    compare("bk-empty.cxtm");
  }

  public void testStaticTM() throws InvalidQueryException, IOException {
    TopicMapIF target = load("bk-static.ltm");
    TopicMapIF source = load("livsit-1.ltm");

    TopicMapSynchronizer.update(target, ttopicq, tchard,
                                source, stopicq, schard);

    canonicalize("bk-static.cxtm", target);
    compare("bk-static.cxtm");
  }

  public void testAddEmneord() throws InvalidQueryException, IOException {
    TopicMapIF target = load("bk-static.ltm");
    TopicMapIF source = load("livsit-2.ltm");

    TopicMapSynchronizer.update(target, ttopicq, tchard,
                                source, stopicq, schard);

    canonicalize("bk-add-emneord.cxtm", target);
    compare("bk-add-emneord.cxtm");
  }  

  public void testRemoveEmneord() throws InvalidQueryException, IOException {
    TopicMapIF target = load("bk-remove-emneord.ltm");
    TopicMapIF source = load("livsit-1.ltm");

    TopicMapSynchronizer.update(target, ttopicq, tchard,
                                source, stopicq, schard);

    canonicalize("bk-remove-emneord.cxtm", target);
    compare("bk-remove-emneord.cxtm");
  }

  public void testBKEmneord() throws InvalidQueryException, IOException {
    TopicMapIF target = load("bk-private-emneord.ltm");
    TopicMapIF source = load("livsit-1.ltm");

    TopicMapSynchronizer.update(target, ttopicq, tchard,
                                source, stopicq, schard);

    canonicalize("bk-private-emneord.cxtm", target);
    compare("bk-private-emneord.cxtm");
  }

  public void testSameAssociation()
    throws InvalidQueryException, IOException {
    // Instead of having one relevant-for in LivsIT, and another
    // association type locally (livsit-relevant-for) we have just one
    // association type; and associations of this type are
    // synchronized. This should work because associations to
    // non-synchronized topics should not be touched.

    // Adjust deciders to match new ontology
    List psis = new ArrayList();
    psis.add("http://psi.bergen.kommune.no/portal/forelder-barn");
    psis.add("http://psi.bergen.kommune.no/portal/relevant-for");
    tchard = TMDeciderUtils.getTypePSIDecider(psis);

    // Do actual test
    TopicMapIF target = load("bk-same-association.ltm");
    TopicMapIF source = load("livsit-3.ltm");

    TopicMapSynchronizer.update(target, ttopicq, tchard,
                                source, stopicq, schard);

    canonicalize("bk-same-association.cxtm", target);
    compare("bk-same-association.cxtm");
  }

  public void testSingleTopicTwoFilter() throws IOException {
    // Set up deciders
    tchard = DeciderUtils.getTrueDecider();

    List psis = new ArrayList();
    psis.add("http://psi.example.org/type-one");
    schard = TMDeciderUtils.getTypePSIDecider(psis);

    // Do actual test
    String psi = "http://psi.example.org/topic";
    TopicMapIF target = load("target-simple.ltm");
    TopicMapIF source = load("source-simple.ltm");
    TopicIF sourcet = source.getTopicBySubjectIdentifier(new URILocator(psi));
    
    TopicMapSynchronizer.update(target, sourcet, tchard, schard);

    canonicalize("single-topic-two.filter.cxtm", target);
    compare("single-topic-two.filter.cxtm");
  }

  public void testReifiedAssociation() throws IOException, InvalidQueryException {
    // Set up deciders
    tchard = DeciderUtils.getTrueDecider();
    schard = tchard;

    // Load topic maps
    TopicMapIF target = load("target-reify-assoc.ltm");
    TopicMapIF source = load("source-reify-assoc.ltm");

    // Sync
    TopicMapSynchronizer.update(target, "topic($T)?", tchard,
                                source, "topic($T)?", schard);

    // Test
    canonicalize("reify-assoc.cxtm", target);
    compare("reify-assoc.cxtm");
  }
  
  // ===== INTERNAL

  private TopicMapIF load(String filename) throws IOException {
    return ImportExportUtils.getReader(TestFileUtils.getTestInputFile(testdataDirectory, "bk", filename)).read();
  }

  private void canonicalize(String filename, TopicMapIF tm) throws IOException {
    String out = base + File.separator + "out" + File.separator + filename;
    FileOutputStream str = new FileOutputStream(new File(out));
    new CanonicalXTMWriter(str).write(tm);
    str.close();

    /*out += ".ltm";
    ImportExportUtils.getWriter(out).write(tm);*/
  }

  private void compare(String filename) throws IOException {
    String out = base + File.separator + "out" + File.separator + filename;
    String baseline = TestFileUtils.getTestInputFile(testdataDirectory, "baseline", filename);
    assertTrue("test file " + filename + " canonicalized wrongly",
               FileUtils.compareFileToResource(out, baseline));
  }
}
