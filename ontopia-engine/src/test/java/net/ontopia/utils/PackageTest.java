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

package net.ontopia.utils;

import junit.framework.*;

public class PackageTest {
    
  public static Test suite() {
      
    // Collections
    TestSuite suite = new TestSuite(IteratorCollectionTest.class);
    suite.addTest(new TestSuite(GrabberCollectionTest.class));
    suite.addTest(new TestSuite(CollectionCollectionTest.class));

    // Grabbers
    suite.addTest(new TestSuite(LowerCaseGrabberTest.class));
    suite.addTest(new TestSuite(UpperCaseGrabberTest.class));
    suite.addTest(new TestSuite(StringifierGrabberTest.class));
    suite.addTest(new TestSuite(SubstringGrabberTest.class));
    suite.addTest(new TestSuite(GrabberGrabberTest.class));
    suite.addTest(new TestSuite(CachedGrabberTest.class));

    // Stringifiers
    suite.addTest(new TestSuite(CollectionStringifierTest.class));

    // Comparators
    suite.addTest(new TestSuite(StringifierComparatorTest.class));
    suite.addTest(new TestSuite(GrabberComparatorTest.class));
    suite.addTest(new TestSuite(LexicalComparatorTest.class));


    return suite;
  }
  
}




