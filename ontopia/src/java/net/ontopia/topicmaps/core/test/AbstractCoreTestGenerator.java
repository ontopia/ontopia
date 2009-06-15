
// $Id: AbstractCoreTestGenerator.java,v 1.10 2006/03/22 09:45:52 grove Exp $

package net.ontopia.topicmaps.core.test;

import java.util.*;
import java.lang.reflect.*;
import junit.framework.*;
import net.ontopia.utils.*;
import net.ontopia.test.TestCaseGeneratorIF;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.entry.*;

public abstract class AbstractCoreTestGenerator implements TestCaseGeneratorIF {

  protected List tests = new ArrayList();

  public abstract FactoryIF getFactory();
  
  public Iterator generateTests() {
    String generator_name = getClass().getName();
    String property = System.getProperty(generator_name);
    String[] names = StringUtils.split(property, " ");
      
    // System.out.println("Generator: " + generator_name);

    for (int i = 0; i < names.length; i++) {
      String name = names[i];

      try {
        tests.add(new TestSuite(Class.forName(name)));
      } catch (ClassNotFoundException e) {
        System.err.println("ERROR: Couldn't find class " + name);
      }
    }

    Iterator iter = tests.iterator();
    while (iter.hasNext()) {
      TestSuite suite = (TestSuite)iter.next();
      Enumeration enumeration = suite.tests();
      while (enumeration.hasMoreElements()) {
        try {
          AbstractTopicMapTest test = (AbstractTopicMapTest)enumeration.nextElement();
          // Set the TopicMapIF object factory to use.
          test.setFactory(getFactory());
        } catch (ClassCastException e) {
          // means there were no test methods in the test case, which
          // is acceptable; so, we say nothing. JUnit will warn in any case.
        }
      }
    }
    return tests.iterator();
  }

  public static interface FactoryIF {

    public TopicMapStoreIF makeStandaloneTopicMapStore();
    
    public TopicMapReferenceIF makeTopicMapReference();

    public void releaseTopicMapReference(TopicMapReferenceIF topicmapRef);

  }
  
}
