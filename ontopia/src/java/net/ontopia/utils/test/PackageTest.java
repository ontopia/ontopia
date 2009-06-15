// $Id: PackageTest.java,v 1.10 2002/05/29 13:38:46 hca Exp $

package net.ontopia.utils.test;

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




