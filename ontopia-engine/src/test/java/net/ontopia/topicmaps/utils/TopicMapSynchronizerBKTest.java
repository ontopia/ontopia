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
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TypedIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.xml.CanonicalXTMWriter;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TopicMapSynchronizerBKTest {
  private String ttopicq;
  private String stopicq;
  private Predicate tchard;
  private Predicate schard;
  private String base;
  
  private final static String testdataDirectory = "tmsync";

  @Before
  public void setUp() throws URISyntaxException {
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
    tchard = new TypePSIDecider(psis);

    psis.clear();
    schard = (o) -> true;

    String root = TestFileUtils.getTestdataOutputDirectory();
    base = root + File.separator + testdataDirectory + File.separator;

    TestFileUtils.verifyDirectory(base, "out");
  }

  @Test
  public void testEmptyTM() throws InvalidQueryException, IOException {
    TopicMapIF target = load("bk-empty.ltm");
    TopicMapIF source = load("livsit-1.ltm");

    TopicMapSynchronizer.update(target, ttopicq, tchard,
                                source, stopicq, schard);

    canonicalize("bk-empty.cxtm", target);
    assertEqualsToBaseline("bk-empty.cxtm");
  }

  @Test
  public void testStaticTM() throws InvalidQueryException, IOException {
    TopicMapIF target = load("bk-static.ltm");
    TopicMapIF source = load("livsit-1.ltm");

    TopicMapSynchronizer.update(target, ttopicq, tchard,
                                source, stopicq, schard);

    canonicalize("bk-static.cxtm", target);
    assertEqualsToBaseline("bk-static.cxtm");
  }

  @Test
  public void testAddEmneord() throws InvalidQueryException, IOException {
    TopicMapIF target = load("bk-static.ltm");
    TopicMapIF source = load("livsit-2.ltm");

    TopicMapSynchronizer.update(target, ttopicq, tchard,
                                source, stopicq, schard);

    canonicalize("bk-add-emneord.cxtm", target);
    assertEqualsToBaseline("bk-add-emneord.cxtm");
  }  

  @Test
  public void testRemoveEmneord() throws InvalidQueryException, IOException {
    TopicMapIF target = load("bk-remove-emneord.ltm");
    TopicMapIF source = load("livsit-1.ltm");

    TopicMapSynchronizer.update(target, ttopicq, tchard,
                                source, stopicq, schard);

    canonicalize("bk-remove-emneord.cxtm", target);
    assertEqualsToBaseline("bk-remove-emneord.cxtm");
  }

  @Test
  public void testBKEmneord() throws InvalidQueryException, IOException {
    TopicMapIF target = load("bk-private-emneord.ltm");
    TopicMapIF source = load("livsit-1.ltm");

    TopicMapSynchronizer.update(target, ttopicq, tchard,
                                source, stopicq, schard);

    canonicalize("bk-private-emneord.cxtm", target);
    assertEqualsToBaseline("bk-private-emneord.cxtm");
  }

  @Test
  public void testSameAssociation()
    throws InvalidQueryException, IOException, URISyntaxException {
    // Instead of having one relevant-for in LivsIT, and another
    // association type locally (livsit-relevant-for) we have just one
    // association type; and associations of this type are
    // synchronized. This should work because associations to
    // non-synchronized topics should not be touched.

    // Adjust deciders to match new ontology
    List psis = new ArrayList();
    psis.add("http://psi.bergen.kommune.no/portal/forelder-barn");
    psis.add("http://psi.bergen.kommune.no/portal/relevant-for");
    tchard = new TypePSIDecider(psis);

    // Do actual test
    TopicMapIF target = load("bk-same-association.ltm");
    TopicMapIF source = load("livsit-3.ltm");

    TopicMapSynchronizer.update(target, ttopicq, tchard,
                                source, stopicq, schard);

    canonicalize("bk-same-association.cxtm", target);
    assertEqualsToBaseline("bk-same-association.cxtm");
  }

  @Test
  public void testSingleTopicTwoFilter() throws IOException, URISyntaxException {
    // Set up deciders
    tchard = (o) -> true;

    List psis = new ArrayList();
    psis.add("http://psi.example.org/type-one");
    schard = new TypePSIDecider(psis);

    // Do actual test
    String psi = "http://psi.example.org/topic";
    TopicMapIF target = load("target-simple.ltm");
    TopicMapIF source = load("source-simple.ltm");
    TopicIF sourcet = source.getTopicBySubjectIdentifier(new URILocator(psi));
    
    TopicMapSynchronizer.update(target, sourcet, tchard, schard);

    canonicalize("single-topic-two.filter.cxtm", target);
    assertEqualsToBaseline("single-topic-two.filter.cxtm");
  }

  @Test
  public void testReifiedAssociation() throws IOException, InvalidQueryException {
    // Set up deciders
    tchard = (o) -> true;
    schard = tchard;

    // Load topic maps
    TopicMapIF target = load("target-reify-assoc.ltm");
    TopicMapIF source = load("source-reify-assoc.ltm");

    // Sync
    TopicMapSynchronizer.update(target, "topic($T)?", tchard,
                                source, "topic($T)?", schard);

    // Test
    canonicalize("reify-assoc.cxtm", target);
    assertEqualsToBaseline("reify-assoc.cxtm");
  }
  
  // ===== INTERNAL

  private TopicMapIF load(String filename) throws IOException {
    return ImportExportUtils.getReader(TestFileUtils.getTestInputFile(testdataDirectory, "bk", filename)).read();
  }

  private void canonicalize(String filename, TopicMapIF tm) throws IOException {
    String out = base + File.separator + "out" + File.separator + filename;
    new CanonicalXTMWriter(new File(out)).write(tm);

    /*out += ".ltm";
    ImportExportUtils.getWriter(out).write(tm);*/
  }

  private void assertEqualsToBaseline(String filename) throws IOException {
    String out = base + File.separator + "out" + File.separator + filename;
    String baseline = TestFileUtils.getTestInputFile(testdataDirectory, "baseline", filename);
    Assert.assertTrue("test file " + filename + " canonicalized wrongly",
               TestFileUtils.compareFileToResource(out, baseline));
  }

  static class TypePSIDecider implements Predicate {
    private Collection okpsis;
    
    public TypePSIDecider(Collection okpsis) throws URISyntaxException {
      this.okpsis = new HashSet();
      Iterator it = okpsis.iterator();
      while (it.hasNext()) {
        Object obj = it.next();
        LocatorIF psi;
        if (obj instanceof LocatorIF) {
          psi = (LocatorIF) obj;
        } else {
          psi = new URILocator((String) obj);
        }
        this.okpsis.add(psi);
      }
    }
    
    @Override
    public boolean test(Object object) {
      if (object instanceof TypedIF) {
        TopicIF type = ((TypedIF) object).getType();
        if (type == null) {
          return false;
        }
        
        Iterator it = type.getSubjectIdentifiers().iterator();
        while (it.hasNext()) {
          if (okpsis.contains(it.next())) {
            return true;
          }
        }
      } 
      return false;
    }
  }  
}
