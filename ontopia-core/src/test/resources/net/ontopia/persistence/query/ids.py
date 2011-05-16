from net.ontopia.topicmaps.impl.rdbms import RDBMSTopicMapStore
from net.ontopia.infoset.impl.basic import URILocator

store = RDBMSTopicMapStore("/ontopia/src/java/net/ontopia/topicmaps/impl/rdbms/config/grove.postgresql.props", 67401)
tm = store.getTopicMap()

def get_id(id):
    object = tm.getObjectBySourceLocator(URILocator("file:/ontopia/topicmaps/opera/opera.xtm#%s" % (id)))    
    print id, object.getObjectId()[1:]
    
def get_tid(id):
    object = tm.getObjectBySourceLocator(URILocator("file:/ontopia/topicmaps/opera/opera-template.xtmp#%s" % (id)))    
    print id, object.getObjectId()[1:]

get_id("shakespeare")
get_tid("based-on")
get_tid("result")
get_tid("source")
get_tid("written-by")
get_tid("work")
get_tid("writer")

print tm.getObjectById("T69019"), tm.getObjectById("T68642")
print tm.getObjectById("T69149"), tm.getObjectById("T68650")
print tm.getObjectById("T69162"), tm.getObjectById("T68652")
print tm.getObjectById("T69162"), tm.getObjectById("T68655")

ci = tm.getTransaction().getIndexManager().getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF")

#store.close()
