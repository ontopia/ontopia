
import sys
from java.io import FileInputStream
from com.hp.hpl.jena.rdf.model import ModelFactory
from net.ontopia.utils import URIUtils
from net.ontopia.infoset.impl.basic import URILocator
from net.ontopia.topicmaps.utils.rdf import RDFIntroSpector
from net.ontopia.topicmaps.entry import XMLConfigSource
from net.ontopia.topicmaps.nav2.webapps.ontopoly.sysmodel import OntopolyRepository
from net.ontopia.topicmaps.nav2.webapps.ontopoly.model import TopicMap, TopicType, NameType, OccurrenceType

def get_value(subject, property):
    it = subject.listProperties(property)
    while it.hasNext():
        stmt = it.next()
        return stmt.getResource()

def get_string(subject, property):
    it = subject.listProperties(property)
    while it.hasNext():
        stmt = it.next()
        return stmt.getString()

file = "/Users/larsga/Desktop/oks-enterprise-4.0.0/apache-tomcat/common/classes/tm-sources.xml"
rep = XMLConfigSource.getRepository(file)
rep = OntopolyRepository(rep)
tm = rep.getReference("test.xtm")
if not tm:
    tm = rep.getSources()[0].createTopicMap("test.xtm")
tm = TopicMap(tm)
realtm = tm.getTopicMapIF()

RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"
RDFS = "http://www.w3.org/2000/01/rdf-schema#"

model = ModelFactory.createDefaultModel()
model.read(FileInputStream("datasets/foaf.rdf"), "http://bullshit")

RDFS_CLASS = model.createResource(RDFS + "Class")
RDFS_DOMAIN = model.createProperty(RDFS + "domain")
RDFS_RANGE = model.createProperty(RDFS + "range")
RDFS_LABEL = model.createProperty(RDFS + "label")
RDF_TYPE = model.createProperty(RDF + "type")

file = "/Users/larsga/Desktop/oks-enterprise-4.0.0/apache-tomcat/webapps/omnigator/WEB-INF/topicmaps/mapping.rdff"
fileuri = URIUtils.getURI(file).getAddress();
mappings = RDFIntroSpector.getPropertyMappings(fileuri, 0)

it = model.listSubjectsWithProperty(RDF_TYPE, RDFS_CLASS)
while it.hasNext():
    klass = it.nextResource()
    print klass
    typepsi = URILocator(klass.toString())
    ktopic = realtm.getTopicBySubjectIdentifier(typepsi)
    if not ktopic:
        ktopic = tm.createTopicType(get_string(klass, RDFS_LABEL))
        ktopic.addSubjectIdentifier(typepsi)
    ktt = TopicType(ktopic, tm)
    # FIXME: koble opp supertype
    # FIXME: må hoppe over noen klasser

    it2 = model.listSubjectsWithProperty(RDFS_DOMAIN, klass)
    while it2.hasNext():
        property = it2.nextResource()
        print "  ", property
        pname = get_string(property, RDFS_LABEL)
        mapsto = mappings.get(property.toString())
        if mapsto:
            print "    XXX: ", mapsto.getMapsTo()
        range = get_value(property, RDFS_RANGE)
        if range:
            print "    YYY: ", range

        # basename
        if mapsto and mapsto.getMapsTo().endswith("basename"):
            type = mapsto.getType() or property.toString()
            typepsi = URILocator(type)
            ptopic = realtm.getTopicBySubjectIdentifier(typepsi)
            if not ptopic:
                ptopic = tm.createNameType(pname)
                ptopic.addSubjectIdentifier(typepsi)
            nametype = NameType(ptopic, tm)
            ktt.addFieldAssignment(nametype)

        # occurrence
        elif mapsto and mapsto.getMapsTo().endswith("occurrence"):
            type = mapsto.getType() or property.toString()
            typepsi = URILocator(type)
            ptopic = realtm.getTopicBySubjectIdentifier(typepsi)
            if not ptopic:
                ptopic = tm.createOccurrenceType(pname)
                ptopic.addSubjectIdentifier(typepsi)
            occtype = OccurrenceType(ptopic, tm)
            ktt.addFieldAssignment(occtype)

            if range.toString() != RDFS + "Literal":
                psi = "http://www.w3.org/2001/XMLSchema#anyURI"
                datatype = DataType(realtm.getTopicBySubjectIdentifier(psi))
                occtype.setDataType(datatype)
            
        # association
        #   - make sure role types exist
        #   - try to make up names
        #   - set legal types on both sides
        #   - cardinality defaults to 0-*

tm.save()
