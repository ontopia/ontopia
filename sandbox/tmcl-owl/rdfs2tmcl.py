
# TODO
#  - check for owl:inverseOf
#  - how to handle owl:AnnotationProperties
#  - owl:disjointWith, owl:complementOf
#  - plays-role

import sys
from java.util import ArrayList
from java.io import FileInputStream
from com.hp.hpl.jena.rdf.model import ModelFactory
from net.ontopia.utils import URIUtils
from net.ontopia.infoset.impl.basic import URILocator
from net.ontopia.topicmaps.utils.rdf import RDFIntroSpector

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

def qname(uri):
    if uri is None:
        return None
    
    if type(uri) != type(""):
        uri = uri.toString()

    for (prefix, ns) in prefixes.items():
        if uri.startswith(ns):
            return prefix + ":" + uri[len(ns) : ]

    return uri

def verify(subject, property, object):
    it = subject.listProperties(property)
    while it.hasNext():
        o = it.next().getResource()
        if object == o:
            return 1
    return 0

def iterator2list(it):
    list = []
    while it.hasNext():
        list.append(it.next())
    return list

def get_all_subprops(prop):
    subs = [prop]
    it = model.listSubjectsWithProperty(RDFS_SUBPROPERTYOF, prop)
    while it.hasNext():
        subs += get_all_subprops(it.next())
    return subs

def inherit_mappings(mappings):
    for prop in ArrayList(mappings.keySet()):
        mapsto = mappings.get(prop)
        prop = model.createProperty(prop)
        for subprop in get_all_subprops(prop):
            if not mappings.get(subprop.toString()):
                mappings.put(subprop.toString(), mapsto)

RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"
RDFS = "http://www.w3.org/2000/01/rdf-schema#"
OWL = "http://www.w3.org/2002/07/owl#"

prefixes = {
    "rdf" : RDF,
    "rdfs" : RDFS,
    "owl" : OWL,
    "foaf" : "http://xmlns.com/foaf/0.1/",
    "rtm" : "http://psi.ontopia.net/rdf2tm/#",
    "tmcl" : "http://psi.topicmaps.org/tmcl/",
    "skos" : "http://www.w3.org/2008/05/skos#",
    #"http://www.w3.org/2004/02/skos/core#"
    "dc" : "http://purl.org/dc/elements/1.1/",
    "doap" : "http://usefulinc.com/ns/doap#",
    }

model = ModelFactory.createDefaultModel()
model.read(FileInputStream(sys.argv[1]), "http://bullshit")

RDFS_CLASS = model.createResource(RDFS + "Class")
RDFS_DOMAIN = model.createProperty(RDFS + "domain")
RDFS_SUBCLASSOF = model.createProperty(RDFS + "subClassOf")
RDFS_SUBPROPERTYOF = model.createProperty(RDFS + "subPropertyOf")
RDFS_RANGE = model.createProperty(RDFS + "range")
RDFS_LABEL = model.createProperty(RDFS + "label")
RDF_TYPE = model.createProperty(RDF + "type")
RDF_PROPERTY = model.createProperty(RDF + "Property")
OWL_FUNCTIONALPROPERTY = model.createProperty(OWL + "FunctionalProperty")
OWL_INVERSEFP = model.createProperty(OWL + "InverseFunctionalProperty")
OWL_CLASS = model.createProperty(OWL + "Class")
OWL_OBJECTPROPERTY = model.createProperty(OWL + "ObjectProperty")
OWL_DATATYPEPROPERTY = model.createProperty(OWL + "DatatypeProperty")
OWL_ANNOTATIONPROPERTY = model.createProperty(OWL + "AnnotationProperty")
OWL_SYMMETRICPROPERTY = model.createProperty(OWL + "SymmetricProperty")

file = "/Users/larsga/Desktop/oks-enterprise-4.0.0/apache-tomcat/webapps/omnigator/WEB-INF/topicmaps/mapping.rdff"
fileuri = URIUtils.getURI(file).getAddress();
mappings = RDFIntroSpector.getPropertyMappings(fileuri, 0)
inherit_mappings(mappings)

outf = open(sys.argv[2], "w")
for (prefix, ns) in prefixes.items():
    outf.write("%%prefix %s %s\n" % (prefix, ns))
outf.write("\n")

outf.write("""def unique-occurrence($c, $o)
  ?c isa tmcl:uniqueoccurrence-constraint .
  tmcl:applies-to(tmcl:constraint-role : ?c, tmcl:topictype-role : $c)
  tmcl:applies-to(tmcl:constraint-role : ?c, tmcl:occurrencetype-role : $o)
end

def binary-association($at, $rt1, $rt2)
  $at isa associationtype .
  $rt1 isa roletype .
  $rt2 isa roletype .

  ?c isa associationrole-constraint 
    card-min: 1
    card-max: 1 .
  applies-to(constraint-role : ?c, associationtype-role : $at)
  applies-to(constraint-role : ?c, roletype-role : $rt1) 

  ?c2 isa associationrole-constraint 
    card-min: 1
    card-max: 1 .
  applies-to(constraint-role : ?c2, associationtype-role : $at)
  applies-to(constraint-role : ?c2, roletype-role : $rt2) 
end

def symmetric-association($at, $rt)
  $at isa associationtype .
  $rt isa roletype .

  ?c isa associationrole-constraint 
    card-min: 2
    card-max: 2 .
  applies-to(constraint-role : ?c, associationtype-role : $at)
  applies-to(constraint-role : ?c, roletype-role : $rt) 
end

""")

it = model.listSubjectsWithProperty(RDF_TYPE, OWL_SYMMETRICPROPERTY)
while it.hasNext():
    prop = it.next()
    mapsto = mappings.get(prop.toString())

    if not mapsto:
        print "NO MAPPING FOR", prop
        continue

    if not mapsto.getMapsTo().endswith("association"):
        print "SYMMETRIC PROPERTY NOT ASSOCIATION:", prop

    if mapsto.getSubjectRole() != mapsto.getObjectRole():
        print "SYMMETRIC PROPERTY NOT MAPPED SYMMETRICALLY:", prop

klasses = (iterator2list(model.listSubjectsWithProperty(RDF_TYPE, RDFS_CLASS)) +
           iterator2list(model.listSubjectsWithProperty(RDF_TYPE, OWL_CLASS)))
for klass in klasses:
    outf.write("%s isa tmcl:topic-type;\n" % qname(klass))

    it2 = klass.listProperties(RDFS_SUBCLASSOF)
    while it2.hasNext():
        stmt = it2.next()
        super = stmt.getResource()
        outf.write("  ako %s;\n" % qname(super))
    
    it2 = model.listSubjectsWithProperty(RDFS_DOMAIN, klass)
    while it2.hasNext():
        property = it2.nextResource()
        mapsto = mappings.get(property.toString())
        if not mapsto:
            print "NO MAPPING FOR", qname(property)
            continue

        for property in get_all_subprops(property):
            range = get_value(property, RDFS_RANGE)
            min_card = "0"
            max_card = "MAX_INT"
            if verify(property, RDF_TYPE, OWL_FUNCTIONALPROPERTY):
                max_card = "1"

            # basename
            if mapsto and mapsto.getMapsTo().endswith("basename"):
                ptype = mapsto.getType() or property.toString()
                outf.write('  has-name(%s, %s, %s, ".*");\n' %
                           (qname(ptype), min_card, max_card))

            # occurrence
            elif mapsto and mapsto.getMapsTo().endswith("occurrence"):
                ptype = mapsto.getType() or property.toString()
                outf.write('  has-occurrence(%s, %s, %s, ".*");\n' %
                           (qname(ptype), min_card, max_card))

            # association
            elif mapsto and mapsto.getMapsTo().endswith("association"):
                ptype = mapsto.getType() or property.toString()
                outf.write('  plays-role(%s, %s, %s); # %s\n' %
                           (qname(mapsto.getSubjectRole()),
                            min_card, max_card, qname(ptype)))
            

    it2 = model.listSubjectsWithProperty(RDFS_RANGE, klass)
    while it2.hasNext():
        property = it2.nextResource()
        mapsto = mappings.get(property.toString())
        if not mapsto or not mapsto.getMapsTo().endswith("association"):
            continue

        min_card = "0"
        max_card = "MAX_INT"
        if verify(property, RDF_TYPE, OWL_FUNCTIONALPROPERTY):
            max_card = "1"
        
        for property in get_all_subprops(property):
            ptype = mapsto.getType() or property.toString()
            outf.write('  plays-role(%s, %s, %s); # %s\n' %
                       (qname(mapsto.getObjectRole()), min_card, max_card,
                        qname(ptype)))
    
    outf.write(".\n\n")

it = model.listSubjectsWithProperty(RDF_TYPE, OWL_INVERSEFP)
while it.hasNext():
    property = it.nextResource()
    mapsto = mappings.get(property.toString())
    domain = get_value(property, RDFS_DOMAIN)

    if not mapsto or not mapsto.getMapsTo().endswith("occurrence"):
        continue

    outf.write("unique-occurrence(%s, %s)\n" % (qname(domain), qname(property)))

props = (iterator2list(model.listSubjectsWithProperty(RDF_TYPE, RDF_PROPERTY)) +
         iterator2list(model.listSubjectsWithProperty(RDF_TYPE, OWL_OBJECTPROPERTY)) +
         iterator2list(model.listSubjectsWithProperty(RDF_TYPE, OWL_DATATYPEPROPERTY)) +
         iterator2list(model.listSubjectsWithProperty(RDF_TYPE, OWL_ANNOTATIONPROPERTY)))
for property in props:
    mapsto = mappings.get(property.toString())
    if not mapsto:
        print "NO MAPPING FOR", property
        continue

    if mapsto.getMapsTo().endswith("association"):
        if mapsto.getSubjectRole() != mapsto.getObjectRole():
            outf.write("binary-association(%s, %s, %s)\n" %
                       (qname(property),
                        qname(mapsto.getSubjectRole()),
                        qname(mapsto.getObjectRole())))
        else:
            outf.write("symmetric-association(%s, %s)\n" %
                       (qname(property),
                        qname(mapsto.getSubjectRole())))
    elif mapsto.getMapsTo().endswith("occurrence"):
        outf.write("%s isa tmcl:occurrencetype .\n" % qname(property))
    elif mapsto.getMapsTo().endswith("basename"):
        outf.write("%s isa tmcl:topicnametype .\n" % qname(property))

outf.close()
