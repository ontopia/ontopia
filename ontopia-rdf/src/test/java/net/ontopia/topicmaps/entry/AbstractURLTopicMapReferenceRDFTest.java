/*
 * #!
 * Ontopia RDF
 * #-
 * Copyright (C) 2001 - 2014 The Ontopia Project
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
package net.ontopia.topicmaps.entry;

import java.net.URL;
import static junit.framework.Assert.assertTrue;
import net.ontopia.topicmaps.utils.rdf.RDFTopicMapReference;
import net.ontopia.utils.TestFileUtils;
import net.ontopia.utils.URIUtils;

public class AbstractURLTopicMapReferenceRDFTest extends AbstractURLTopicMapReferenceTest {

  public AbstractURLTopicMapReferenceRDFTest(String name) {
    super(name);
  }

  // --- Test cases (RDF)
  public void testRDFRef() throws java.net.MalformedURLException, java.io.IOException {
    String id = "instance-of.rdf";
    String title = "RDFTM";
    String file = TestFileUtils.getTestInputFile("rdf", "in", id);
    RDFTopicMapReference ref = new RDFTopicMapReference(new URL(URIUtils.getURI(file).getAddress()), id, title);

    // test mapping file
    String mf = ref.getMappingFile();
    assertTrue("Mappingfile default is set", ref.getMappingFile() == null);
    ref.setMappingFile("foo");
    assertTrue("Mappingfile not equals 'foo'", "foo".equals(ref.getMappingFile()));
    ref.setMappingFile(mf);
    assertTrue("Mappingfile != " + mf, mf == ref.getMappingFile());

    // test syntax
    String sx = ref.getSyntax();
    assertTrue("Syntax default is set", ref.getSyntax() == null);
    ref.setSyntax("foo");
    assertTrue("Syntax not equals 'foo'", "foo".equals(ref.getSyntax()));
    ref.setSyntax(sx);
    assertTrue("Syntax != " + sx, sx == ref.getSyntax());

    // test generate names
    boolean gg = ref.getGenerateNames();
    assertTrue("GenerateNames default is not false", !gg);
    ref.setGenerateNames(true);
    assertTrue("GenerateNames is not true", ref.getGenerateNames());
    ref.setGenerateNames(gg);
    assertTrue("GenerateNames is not " + gg, gg == ref.getGenerateNames());

    // run abstract url topic map reference tests
    doAbstractURLTopicMapReferenceTests(ref);

  }
}
