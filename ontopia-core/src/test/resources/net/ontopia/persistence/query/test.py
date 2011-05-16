from net.ontopia.persistence.query.jdo import JDOQuery, JDOAnd, JDOOr, JDONot, JDONotEqual, JDOEqual, JDOContains, JDOIsEmpty, JDOStartsWith, JDOEndsWith, JDOField, JDOObject, JDOCollection, JDOParameter, JDOVariable, JDOPrimitive, JDONull, JDOString
from net.ontopia.persistence.query.sql import SQLBuilder, GenericSQLGenerator, OracleSQLGenerator
from net.ontopia.topicmaps.core import TopicIF, AssociationIF, AssociationRoleIF
from net.ontopia.topicmaps.impl.rdbms import TopicMap, Topic, Association, AssociationRole, BaseName, Occurrence, SourceLocator
from net.ontopia.persistence.proxy import RDBMSStorage
from java.util import Collection, HashSet, Arrays
from java.lang import System, String

from net.ontopia.persistence.query.sql import SQLStatement, RDBMSCollectionQuery, RDBMSMatrixQuery
from net.ontopia.topicmaps.impl.rdbms import RDBMSTopicMapStore
from net.ontopia.infoset.impl.basic import URILocator

dbtype = 2

if dbtype == 1:
    tmid = 67401
    topicid = 68055
    storage = RDBMSStorage("/ontopia/src/java/net/ontopia/topicmaps/impl/rdbms/config/grove.postgresql.props")
    #storage = RDBMSStorage("/ontopia/src/java/net/ontopia/topicmaps/impl/rdbms/config/grove.jxpostgresql.props")
elif dbtype == 2:
    tmid = 18205001 # opera.xtm
    topicid = 18205002 # city
    storage = RDBMSStorage("/ontopia/src/java/net/ontopia/topicmaps/impl/rdbms/config/grove.oracle.props")
elif dbtype == 3:
    tmid = 80001
    topicid = 80041
    storage = RDBMSStorage("/ontopia/src/java/net/ontopia/topicmaps/impl/rdbms/config/grove.oracle.laptop.props")
elif dbtype == 4:
    tmid = 930001
    topicid = 930041
    storage = RDBMSStorage("/ontopia/src/java/net/ontopia/topicmaps/impl/rdbms/config/grove.oracle.laptop.props")
    
mapping = storage.getMapping();
print mapping

if 1:
    store = RDBMSTopicMapStore(storage, tmid)
    #store = RDBMSTopicMapStore("/ontopia/src/java/net/ontopia/topicmaps/impl/rdbms/config/grove.spydriver.props", 67401)
    tm = store.getTopicMap()
    ci = tm.getTransaction().getIndexManager().getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");

debug = 1
sqlbuilder = SQLBuilder(mapping, debug)

# JDOQL: (vR1.player = pT && vR1.type = pRT1 && vR1.assoc = vA && vA.type = pAT && vA.roles.contains(vR2) && vR2.type = pRT2 && vR2.player = vT)
# JDOQL: (vR1.player.in(pTlist) = pT && vR1.type = pRT1 && vR1.assoc = vA && vA.type = pAT && vA.roles.contains(vR2) && vR2.type = pRT2 && vR2.player = vT)

query0 = JDOQuery()

query0.addSelect(JDOVariable("vT"))

query0.addParameter("pT", Topic)
query0.addParameter("pRT1", Topic)
query0.addParameter("pAT", Topic)
query0.addParameter("pRT2", Topic)

query0.addVariable("vR1", AssociationRole)
query0.addVariable("vR2", AssociationRole)
query0.addVariable("vA", Association)
query0.addVariable("vT", Topic)

query0.addAscending(JDOVariable("vT"))

crit0 = JDOAnd([ JDOEqual(JDOField(JDOVariable("vR1"), "player"), JDOParameter("pT")),
                 JDOEqual(JDOField(JDOVariable("vR1"), "type"), JDOParameter("pRT1")),
                 JDOEqual(JDOField(JDOVariable("vR1"), "assoc"), JDOVariable("vA")),
                 JDOEqual(JDOField(JDOVariable("vA"), "type"), JDOParameter("pAT")),
                 JDOContains(JDOField(JDOVariable("vA"), "roles"), JDOVariable("vR2")),
                 JDOEqual(JDOField(JDOVariable("vR2"), "type"), JDOParameter("pRT2")),
                 JDOEqual(JDOField(JDOVariable("vR2"), "player"), JDOVariable("vT"))
               ])
query0.setFilter(crit0)

# JDOQL:"r1.player = shakespeare && r1.type = writer && r1.assoc = a1 && a1.type = written_by && a1.roles.contains(r2) && r2.type = work && r2.player = B && B.roles.contains(r3) && r3.type = source && r3.assoc = a2 && a2.type = based_on && a2.roles.contains(r4) && r4.type = result && r4.player = A"

# SQL: select * from TM_TOPIC A, TM_ASSOCIATION a2, TM_ASSOCIATION_ROLE r3, TM_ASSOCIATION_ROLE r2, TM_ASSOCIATION a1, TM_ASSOCIATION_ROLE r1, TM_ASSOCIATION_ROLE r4, TM_TOPIC B where (r1.player_id = ? and r1.type_id = ? and r1.assoc_id = a1.id and a1.type_id = ? and a1.id = r2.assoc_id and r2.type_id = ? and r2.player_id = B.id and B.id = r3.player_id and r3.type_id = ? and r3.assoc_id = a2.id and a2.type_id = ? and a2.id = r4.assoc_id and r4.type_id = ? and r4.player_id = A.id)

query1 = JDOQuery()

query1.addSelect(JDOVariable("A"))
query1.addSelect(JDOVariable("B"))

query1.addParameter("shakespeare", Topic)
query1.addParameter("based_on", Topic)
query1.addParameter("result", Topic)
query1.addParameter("source", Topic)
query1.addParameter("written_by", Topic)
query1.addParameter("work", Topic)
query1.addParameter("writer", Topic)

query1.addVariable("a1", Association)
query1.addVariable("a2", Association)
query1.addVariable("r1", AssociationRole)
query1.addVariable("r2", AssociationRole)
query1.addVariable("r3", AssociationRole)
query1.addVariable("r4", AssociationRole)
query1.addVariable("A", Topic)
query1.addVariable("B", Topic)

query1.addAscending(JDOVariable("A"))

crit1 = JDOAnd([ JDOEqual(JDOField(JDOVariable("r1"), "player"), JDOParameter("shakespeare")),
                 JDOEqual(JDOField(JDOVariable("r1"), "type"), JDOParameter("writer")),
                 JDOEqual(JDOField(JDOVariable("r1"), "assoc"), JDOVariable("a1")),
                 JDOEqual(JDOField(JDOVariable("a1"), "type"), JDOParameter("written_by")),
                 JDOContains(JDOField(JDOVariable("a1"), "roles"), JDOVariable("r2")),
                 JDOEqual(JDOField(JDOVariable("r2"), "type"), JDOParameter("work")),
                 JDOEqual(JDOField(JDOVariable("r2"), "player"), JDOVariable("B")),
                 JDOContains(JDOField(JDOVariable("B"), "roles"), JDOVariable("r3")),
                 JDOEqual(JDOField(JDOVariable("r3"), "type"), JDOParameter("source")),
                 JDOEqual(JDOField(JDOVariable("r3"), "assoc"), JDOVariable("a2")),
                 JDOEqual(JDOField(JDOVariable("a2"), "type"), JDOParameter("based_on")),
                 JDOContains(JDOField(JDOVariable("a2"), "roles"), JDOVariable("r4")),
                 JDOEqual(JDOField(JDOVariable("r4"), "type"), JDOParameter("result")),
                 JDOEqual(JDOField(JDOVariable("r4"), "player"), JDOVariable("A"))
               ])
query1.setFilter(crit1)

# JDOQL: [T2] T1.types.contains(T2) && T1.topicmap = M1
# SQL: select distinct T2.id from TM_TOPIC_TYPES MM0, TM_TOPIC T2, TM_TOPIC T1 where (T1.topicmap_id = 67401 and (T1.id = MM0.topic_id and MM0.type_id = T2.id));

# explain select distinct t2.id from TM_TOPIC t2, TM_TOPIC_TYPES MM0  where t2.topicmap_id = 67401 and t2.id = MM0.type_id;
# Y: T1.types.contains(T2) && T2.topicmap = M1 -> (MM.theme_id = t2.id and t2.topicmap_id = ?)
# N: T1.types.contains(T2) && T1.topicmap = M1 -> (T1.topicmap_id = ? and (T1.id = MM0.topic_id and MM0.type_id = T2.id))

query2 = JDOQuery()

query2.addSelect(JDOVariable("T2"))

query2.addParameter("M1", TopicMap)

query2.addVariable("T1", Topic)
query2.addVariable("T2", Topic)

query2.setFilter(JDOAnd([ JDOEqual(JDOField(JDOVariable("T2"), "topicmap"), JDOParameter("M1")),
                          JDOContains(JDOField(JDOVariable("T1"), "types"), JDOVariable("T2"))
                         ]))

# JDOQL: [T1] T1.types.isEmpty() && T1.topicmap = M1
# JDOQL: [T1] T1.types.isEmpty() && T1.topicmap = M1 && T1.roles.isEmpty()

# select t1.id from TM_TOPIC t1 left outer join TM_TOPIC_TYPES t2 on t1.id = t2.topic_id where t1.topicmap_id = 67401 and t2.type_id is null;
# select t1.id from TM_TOPIC t1 where t1.topicmap_id = ? and t1.id not in (select t2.topic_id from TM_TOPIC_TYPES t2)

# select t1.id from TM_TOPIC t1, TM_TOPIC_TYPES t2 where t1.id  = t2.topic_id(+) and t1.topicmap_id = 7962101 and t2.type_id is null; 

query3 = JDOQuery()

query3.addSelect(JDOVariable("T1"))

query3.addParameter("M1", TopicMap)

query3.addVariable("T1", Topic)

query3.setFilter(JDOAnd([ #JDOIsEmpty(JDOField(JDOVariable("T1"), "types")),
                          JDOEqual(JDOField(JDOVariable("T1"), "topicmap"), JDOParameter("M1")),
                          JDOIsEmpty(JDOField(JDOVariable("T1"), "roles")),
                          JDOIsEmpty(JDOField(JDOVariable("T1"), "types"))
                         ]))

# Get topics that have the same source locator
# 1. JDOQL: [T1, T2] T1.sources.contains(S1) && T1.sources.contains(S2) && S1 = S2
# 2. JDOQL: [T1, T2] T1.sources.contains(S1) && T1.indicators.contains(S2) && S1 = S2
# 3. JDOQL: [T1, T2] T1.sources.contains(T2.sources)

# Get topics with a given source locator
# JDOQL: [T1] T1.sources.contains(S1) && T1.topicmap = M1 && S1.address = "file:/ontopia/topicmaps/opera/opera.xtm#turandot-beijing"


# result: 7967988, file:/ontopia/topicmaps/opera/opera.xtm#turandot-beijing
query4 = JDOQuery()
query4.setDistinct(1)

query4.addSelect(JDOVariable("T1"))
query4.addSelect(JDOVariable("S1"))

query4.addParameter("M1", TopicMap)
query4.addParameter("SUFFIX", String)
#query4.addParameter(SourceLocator, "S2")

query4.addVariable("T1", Topic)
query4.addVariable("S1", SourceLocator)

query4.setFilter(JDOAnd([ JDOEqual(JDOField(JDOVariable("T1"), "topicmap"), JDOParameter("M1")),
                          JDOContains(JDOField(JDOVariable("T1"), "sources"), JDOVariable("S1")),
                          #JDOEqual(JDOField(JDOVariable("S1"), "address"),
                          #         JDOString("file:/ontopia/topicmaps/opera/opera.xtm#turandot-beijing"))
                          JDONot(JDOEndsWith(JDOField(JDOVariable("S1"), "address"), JDOString("beijing"))),
                          JDONot(JDOEndsWith(JDOField(JDOVariable("S1"), "address"), JDOParameter("SUFFIX"))),
                          #JDONot(JDOEqual(JDOVariable("S1"), JDOParameter("S2"))),
                          JDOStartsWith(JDOField(JDOVariable("S1"), "address"),
                                        JDOString("file:/ontopia/topicmaps/opera/opera.xtm#tur"))
                         ]))

# JDOQL: [O1] O1.type == null && O1.topicmap = M1

query5 = JDOQuery()

query5.addSelect(JDOVariable("O1"))

query5.addParameter("M1", TopicMap)

query5.addVariable("O1", Occurrence)

query5.setFilter(JDOAnd([ JDOEqual(JDOField(JDOVariable("O1"), "topicmap"), JDOParameter("M1")),
                          JDOEqual(JDONull(), JDOField(JDOVariable("O1"), "type")),
                          JDOEqual(JDONull(), JDONull())
                         ]))

# JDOQL: [T1] T1.types.contains(P1) || (T1.types.contains(T2) && T2.types.contains(P2))

query6 = JDOQuery()

query6.addSelect(JDOVariable("T1"))

query6.addParameter("P1", Topic)
query6.addParameter("P2", Topic)

query6.addVariable("T1", Topic)
query6.addVariable("T2", Topic)

query6.setFilter(JDOOr([ JDOContains(JDOField(JDOVariable("T1"), "types"), JDOParameter("P1")),
                         JDOAnd([
                            JDOContains(JDOField(JDOVariable("T1"), "types"), JDOVariable("T2")),
                            JDOContains(JDOField(JDOVariable("T2"), "types"), JDOParameter("P2"))
                         ])
                       ]))
# JDOQL: [T1, T2] P.contains(T1) && T2.types.contains(T1)

query7 = JDOQuery()

query7.addSelect(JDOVariable("T2"))

query7.addParameter("P", Collection)

query7.addVariable("T1", Topic)
query7.addVariable("T2", Topic)

query7.setFilter(JDOAnd([ JDOContains(JDOParameter("P"), JDOVariable("T1")),
                          JDOContains(JDOField(JDOVariable("T2"), "types"), JDOVariable("T1"))
                        ]))
# JDOQL: [T1, T2] P.contains(T1) && T2.types.contains(T1)

query8 = JDOQuery()
query8.addSelect(JDOVariable("T"))
query8.addVariable("T", Topic)
query8.setFilter(JDOAnd([ JDOEndsWith(JDOField(JDOVariable("T"), ["subject", "address"]),
                                      JDOString("#foo"))
                        ]))

query9 = JDOQuery()
query9.addSelect(JDOVariable("O"))
query9.addVariable("O", Occurrence)
query9.setFilter(JDOAnd([ JDOEndsWith(JDOField(JDOVariable("O"), ["type", "subject", "address"]),
                                      JDOString("#foo")),
                          JDOEqual(JDOField(JDOVariable("O"), "topicmap"), JDOObject(tm))
                          ]))
# JDOQL: [T1, T2] T1.types.contains(P)

query10 = JDOQuery()

query10.addSelect(JDOVariable("T1"))

query10.addParameter("P", Topic)

query10.addVariable("T1", Topic)

query10.setFilter(JDOAnd([ JDOContains(JDOField(JDOVariable("T1"), "types"), JDOParameter("P"))
                        ]))

# JDOQL: [O] O.topicmap = M & O.type.types.isEmpty()

query11 = JDOQuery()

query11.addSelect(JDOVariable("O"))

query11.addParameter("M", TopicMap)

query11.addVariable("O", Occurrence)

query11.setFilter(JDOAnd([
                          JDOEqual(JDOField(JDOVariable("O"), "topicmap"), JDOParameter("M")),
                          JDOIsEmpty(JDOField(JDOVariable("O"), ["type", "types"]))
                         ]))

# JDOQL: [T] T.topicmap = M & T.types.isEmpty()

query12 = JDOQuery()

query12.addSelect(JDOVariable("T"))

query12.addParameter("M", TopicMap)

query12.addVariable("T", Topic)

query12.setFilter(JDOAnd([
                          JDOEqual(JDOField(JDOVariable("T"), "topicmap"), JDOParameter("M")),
                          JDOIsEmpty(JDOField(JDOVariable("T"), "types"))
                         ]))

# JDOQL: select T from T.types.contains(O)

query13 = JDOQuery()

query13.addSelect(JDOVariable("T"))

query13.addVariable("T", Topic)

query13.setFilter(JDOAnd([
                          JDOContains(JDOField(JDOVariable("T"), "types"),
                                      JDOObject(tm.getObjectById("T%d" % (topicid))))
                         ]))

# JDOQL: select O from O.type = T & <coll>.contains(T)

query14 = JDOQuery()

query14.addSelect(JDOVariable("O"))

query14.addVariable("O", Occurrence)
query14.addVariable("T", Topic)

otypes = HashSet()
otypes.add(tm.getObjectById("T8094852"))
otypes = ci.getOccurrenceTypes()
query14.setFilter(JDOAnd([
                          JDOEqual(JDOField(JDOVariable("O"), "type"), JDOVariable("T")),
                          JDOContains(JDOCollection(otypes, Topic), JDOVariable("T")),
                         ]))

# JDOQL: select O from O.type = T & Pcoll.contains(T)

query15 = JDOQuery()

query15.addSelect(JDOVariable("O"))
query15.addSelect(JDOVariable("V"))

query15.addParameter("Pcoll", Collection)

query15.addVariable("O", Occurrence)
query15.addVariable("T", Topic)
query15.addVariable("V", Topic)

query15.setFilter(JDOAnd([
                          JDOEqual(JDOField(JDOVariable("O"), "type"), JDOVariable("T")),
                          JDOContains(JDOField(JDOVariable("T"), "types"), JDOVariable("V")),
                          JDOContains(JDOParameter("Pcoll"), JDOVariable("T")),
                         ]))

# JDOQL: select O from O.type = T & Pcoll.contains(T)

query16 = JDOQuery()

query16.addSelect(JDOVariable("O"))
query16.addSelect(JDOField(JDOVariable("T"), "types"))

query16.addParameter("Pcoll", Collection)

query16.addVariable("O", Occurrence)
query16.addVariable("T", Topic)

query16.setFilter(JDOAnd([
                          JDOEqual(JDOField(JDOVariable("O"), "type"), JDOVariable("T")),
                          JDOContains(JDOParameter("Pcoll"), JDOVariable("T")),
                         ]))

# JDOQL: select R from ?ASSOCIATIONS.contains(R)

query17 = JDOQuery()

#query17.addSelect(JDOField(JDOVariable("A"), "roles"))
query17.addSelect(JDOVariable("R"))
query17.addSelect(JDOField(JDOVariable("R"), "player"))
query17.addSelect(JDOField(JDOVariable("R"), ["type", "subject"]))

query17.addParameter("ASSOCIATIONS", Collection)

query17.addVariable("A", Association)
query17.addVariable("R", AssociationRole)

query17.setFilter(JDOAnd([
                          JDOContains(JDOParameter("ASSOCIATIONS"), JDOVariable("A")),
                          JDOContains(JDOField(JDOVariable("A"), "roles"), JDOVariable("R")),
                         ]))

# JDOQL: select T, T.scope from ?TOPICS.contains(T) order by T.scope, T

query18 = JDOQuery()

query18.addSelect(JDOVariable("T"))
query18.addSelect(JDOField(JDOVariable("T"), "scope"))

query18.addParameter("TOPICS", Collection)
query18.addParameter("THEME", Topic)

query18.addVariable("T", Topic)

query18.setFilter(JDOAnd([
                          JDOContains(JDOParameter("TOPICS"), JDOVariable("T")),
                          JDOContains(JDOField(JDOVariable("T"), "scope"), JDOParameter("THEME")),
                         ]))
query18.addAscending(JDOField(JDOVariable("T"), "scope"))
query18.addAscending(JDOVariable("T"))

# JDOQL: select T order by T.subject.address, T

query19 = JDOQuery()

query19.addSelect(JDOVariable("T"))

query19.addParameter("M", TopicMap)

query19.addVariable("T", Topic)

# query19.setFilter(JDOAnd([
#                           JDOContains(JDOParameter("TOPICS"), JDOVariable("T")),
#                          ]))
query19.addAscending(JDOField(JDOVariable("T"), ["subject", "address"]))
query19.addAscending(JDOVariable("T"))

# JDOQL: select O where O.topicmap = M order by O.type.subject.address

query20 = JDOQuery()

query20.addSelect(JDOVariable("O"))

query20.addParameter("M", TopicMap)

query20.addVariable("O", Occurrence)

query20.setFilter(JDOAnd([
                          JDOEqual(JDOField(JDOVariable("O"), "topicmap"), JDOParameter("M"))
                         ]))
query20.addAscending(JDOField(JDOVariable("O"), ["type", "subject", "address"]))

# JDOQL: select T, B.value where T.types.contains(P) & T.basenames.contains(B) order by B.value

query21 = JDOQuery()

query21.addSelect(JDOVariable("T"))
query21.addSelect(JDOField(JDOVariable("B"), "value"))

query21.addParameter("P", Topic)
query21.addVariable("T", Topic)
query21.addVariable("B", BaseName)

query21.setFilter(JDOAnd([
                          JDOContains(JDOField(JDOVariable("T"), "types"), JDOParameter("P")),
                          JDOContains(JDOField(JDOVariable("T"), "basenames"), JDOVariable("B"))
                         ]))
query21.addAscending(JDOField(JDOVariable("B"), ["value"]))

# JDOQL: select O.type.subject.address where O.type.subject != null

query22 = JDOQuery()

query22.addSelect(JDOVariable("O"))

query22.addParameter("M", TopicMap)
query22.addVariable("O", Occurrence)

query22.setFilter(JDOAnd([
                          JDONotEqual(JDOField(JDOVariable("O"), ["type", "subject"]), JDONull())
                         ]))
query22.addAscending(JDOField(JDOVariable("O"), ["type", "subject", "address"]))

# JDOQL: select T, T.basenames where TOPICS.contains(T)

query23 = JDOQuery()

query23.addSelect(JDOVariable("T"))
query23.addSelect(JDOField(JDOVariable("T"), "basenames"))

query23.addParameter("TOPICS", Collection)
query23.addVariable("T", Topic)
query23.addVariable("B", BaseName)

query23.setFilter(JDOAnd([
                          JDOContains(JDOParameter("TOPICS"), JDOVariable("T")),
                          JDOContains(JDOField(JDOVariable("T"), "basenames"), JDOVariable("B")),
                          JDOEqual(JDOVariable("B"), JDONull())
                          ]))

# JDOQL: select T, T.basenames where TOPICS.contains(T)

query24 = JDOQuery()

query24.addSelect(JDOVariable("T"))
#query24.addSelect(JDOField(JDOVariable("T"), "sources"))
query24.addSelect(JDOField(JDOVariable("T"), ["topicmap"]))
query24.addSelect(JDOField(JDOVariable("T"), ["subject", "address"]))

query24.addParameter("TOPICS", Collection)
query24.addVariable("T", Topic)
query24.addVariable("S", SourceLocator)

query24.setFilter(JDOAnd([
                          JDOContains(JDOParameter("TOPICS"), JDOVariable("T")),
                          JDOContains(JDOField(JDOVariable("T"), "sources"), JDOVariable("S")),
                          ]))

def dump(query):
    print "JDO->", query
    start = System.currentTimeMillis();
    sqlquery = sqlbuilder.makeQuery(query)
    generic_stm = GenericSQLGenerator().createSQLStatement(sqlquery)
    print "Time:", (System.currentTimeMillis() - start)
    oracle_stm = OracleSQLGenerator().createSQLStatement(sqlquery)
    print "SQL1->", sqlquery
    print "SQL2->", generic_stm.getSQL()
    print
    #print "==> GENERIC: %s;" % (generic_stm.getSQL([]))
    #print "==> ORACLE:  %s;" % (oracle_stm.getSQL([]))
    #print "--> ST: %s" % (generic_stm.getSelectTypes())
    #print "--> PT: %s" % (generic_stm.getParameterTypes())


if 1:

    def showresult(tm, jdoquery):
        rdump(get_query(tm, jdoquery).executeQuery())

    def get_stm(tm, jdoquery):
        stm = GenericSQLGenerator().createSQLStatement(sqlbuilder.makeQuery(jdoquery))
        stm.setTransaction(tm.getTransaction().getTransaction())
        #stm.setAccessRegistrar(...)
        #print stm.getSQL([])
        return stm
    
    def get_query(tm, jdoquery):
        return RDBMSMatrixQuery(get_stm(tm, jdoquery))
    
    def rdump(result):
        while result.next():
            for i in range(0, result.getWidth()):
                print result.getValue(i),
            print

    
    # rdbquery1 = get_query(tm, query1)
    # params = [tm.getObjectBySourceLocator(URILocator("file:/ontopia/topicmaps/opera/opera.xtm#%s" % ("shakespeare"))),
    #           tm.getObjectBySourceLocator(URILocator("file:/ontopia/topicmaps/opera/opera-template.xtmp#%s" % ("writer"))),
    #           tm.getObjectBySourceLocator(URILocator("file:/ontopia/topicmaps/opera/opera-template.xtmp#%s" % ("written-by"))),
    #           tm.getObjectBySourceLocator(URILocator("file:/ontopia/topicmaps/opera/opera-template.xtmp#%s" % ("work"))),
    #           tm.getObjectBySourceLocator(URILocator("file:/ontopia/topicmaps/opera/opera-template.xtmp#%s" % ("source"))),
    #           tm.getObjectBySourceLocator(URILocator("file:/ontopia/topicmaps/opera/opera-template.xtmp#%s" % ("based-on"))),
    #           tm.getObjectBySourceLocator(URILocator("file:/ontopia/topicmaps/opera/opera-template.xtmp#%s" % ("result")))
    #           ]
    # print params
    # result = rdbquery1.executeQuery(params)
    # rdump(result)

    # rdbquery6 = get_query(tm, query6)
    # ttype = tm.getObjectBySourceLocator(URILocator("file:/ontopia/topicmaps/opera/opera-template.xtmp#%s" % ("composer")))
    # params = [ttype, ttype]
    # print params
    # result = rdbquery6.executeQuery(params)
    # dump(result)
    
    # print get_query(tm, query2).executeQuery([tm])    
    # print get_query(tm, query3).executeQuery([tm])    
    # print get_query(tm, query4).executeQuery([tm])    
    # print get_query(tm, query5).executeQuery([tm])

    def get_subclasses(query, params, level):
        # execute query
        # print "Getting subclasses for %s" % (params[0])
        result = query.executeQuery(params)
        # loop over result
        while result.next():
            for i in range(0, result.getWidth()):
                subclass = result.getValue(i)
                print "%s%s SUBCLASS: %s" % (level, (" " * level), subclass)
                # replace first parameter
                params[0] = subclass
                get_subclasses(query, params, level + 1)

if 1:
    rdbquery0 = get_query(tm, query0)
    dump(query0)
    params = [ None,
               tm.getObjectBySourceLocator(URILocator("file:/ontopia/topicmaps/opera/ontopsi.xtmm#superclass")),
               tm.getObjectBySourceLocator(URILocator("file:/ontopia/topicmaps/opera/ontopsi.xtmm#superclass-subclass")),
               tm.getObjectBySourceLocator(URILocator("file:/ontopia/topicmaps/opera/ontopsi.xtmm#subclass")),
              ]
    print "===>", params
    
    supertype = tm.getObjectBySourceLocator(URILocator("file:/ontopia/topicmaps/opera/opera-template.xtmp#%s" % ("person")))
    params[0] = supertype
    print params
    print supertype
    get_subclasses(rdbquery0, params, 1)
    
    supertype = tm.getObjectBySourceLocator(URILocator("file:/ontopia/topicmaps/opera/opera-template.xtmp#%s" % ("work")))        
    params[0] = supertype
    print supertype
    get_subclasses(rdbquery0, params, 1)
    
# select t.id, s.address from tm_topic t, tm_source_locators s where t.topicmap_id = 67401 and s.tmobject_id = t.id and s.address like '%composer'; // postgresql

# conn = tm.getTransaction().getTransaction().getStorageAccess().getConnection()
# stm = conn.prepareStatement("select T1.id from TM_TOPIC T1 where T1.id in (?)")
#print
#print query9.getFilter()

# print get_query(tm, query9)
# print get_query(tm, query2)
# print get_query(tm, query10)
# print get_query(tm, query4)

if 1:
    dump(query1)
    dump(query5)
    dump(query7)
    storage.close()
    
    dump(query3)
    dump(query11)
    dump(query12)
    dump(query0)
    dump(query13)
    dump(query1)
    
    dump(query14)
    dump(query15)
    dump(query16)
    
    dump(query8)
    dump(query9)
    dump(query17)
    dump(query2)
    dump(query19)
    dump(query20)
    dump(query21)
    dump(query4)
    dump(query1)
    dump(query22)
dump(query18)
dump(query23)
dump(query24)
dump(query6)

#q = get_query(tm, query15)
#r = q.executeQuery([otypes])
