from net.ontopia.persistence.rdbms import DatabaseProjectReader, GenericSQLProducer
from java.lang import System
from java.io import OutputStreamWriter

dbreader = DatabaseProjectReader()
project = dbreader.loadProject("file:/ontopia/src/java/net/ontopia/topicmaps/impl/proxy/schema/TopicMaps.xml")

producer = GenericSQLProducer(project)
writer = OutputStreamWriter(System.out)
producer.produceCreate(writer);
producer.produceDrop(writer);
writer.close()

