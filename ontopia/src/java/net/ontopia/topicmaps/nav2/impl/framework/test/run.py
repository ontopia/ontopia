
# $Id: run.py,v 1.1 2001/08/17 08:26:01 hca Exp $

from net.ontopia.topicmaps.nav2.impl.basic.test import TaglibFrameworkTestCaseGenerator
from net.ontopia.topicmaps.nav2.impl.basic.test import FrameworkTest
from java.lang import System
from net.ontopia.utils import IteratorCollection
System.setProperty("net.ontopia.test.root", "/home/hca/ontopia/src/java/test-data")
gen = TaglibFrameworkTestCaseGenerator()
#it1 = gen.generateTests()
## it2 = gen.generateTopicmaps()
#coll1 = IteratorCollection(gen.generateTests())
#coll2 = IteratorCollection(gen.generateTopicMaps())
#
#print "Number of jsp files : ", coll1.size()
#print "Number of topicmaps : ", coll2.size()
#
## Simulate the tests.
#i = 0
#while i < coll1.size():
#    tagtest = it1.next()
#    j = 0
#    it2 = gen.generateTopicMaps()
#    while j < coll2.size():
#        topictest = it2.next()
#        print tagtest.getName(), topictest.getName()
#        j = j + 1
#    i = i + 1

