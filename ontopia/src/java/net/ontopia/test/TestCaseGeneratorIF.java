// $Id: TestCaseGeneratorIF.java,v 1.3 2002/05/29 13:38:38 hca Exp $

package net.ontopia.test;

import java.util.Iterator;

/**
 * Classes that implement this interface can be listed as test cases.
 * They will then be instantiated and asked to produce all their test
 * cases, which are then added to the test suite by the framework.
 */

public interface TestCaseGeneratorIF {

    /**
     * Generates the test cases.
     * @return A collection of junit.framework.Test instances.
     */
    
    public Iterator generateTests();

}





