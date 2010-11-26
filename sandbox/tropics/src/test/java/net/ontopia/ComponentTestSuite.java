package net.ontopia;

import net.ontopia.tropics.resources.GroupTest;
import net.ontopia.tropics.resources.QueryParamTest;
import net.ontopia.tropics.resources.SearchTest;
import net.ontopia.tropics.resources.TopicMapTest;
import net.ontopia.tropics.resources.TopicMapsTest;
import net.ontopia.tropics.resources.TopicTest;
import net.ontopia.tropics.resources.TopicsTest;
import net.ontopia.tropics.resources.TracerTest;
import net.ontopia.tropics.utils.URIUtilsTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({  
  URIUtilsTest.class,
  
  QueryParamTest.class,  
  TracerTest.class,
  TopicMapsTest.class,  
  TopicMapTest.class,
  GroupTest.class,
  TopicsTest.class,
  TopicTest.class,
  SearchTest.class
})

public class ComponentTestSuite {

}
