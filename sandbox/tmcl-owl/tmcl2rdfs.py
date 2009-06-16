
# FIXME:
#  - remove fixmes below
#  - convert cardinalities!
#  - inverse functional property -> hmmmm
#  - hva med kollisjoner på rdfs:range & rdfs:domain?
#  - other role constraint -> owl:allValuesFrom ?
#  - datatype constraint -> ?
#  - associations -> must handle symmetrics, n-aries

import sys, random, string
from java.util import HashMap
from net.ontopia.topicmaps.utils import ImportExportUtils
from net.ontopia.topicmaps.query.utils import QueryUtils

def getvalue(query, params = {}):
    params = map2hash(params)
    result = qp.execute(query, params)
    while result.next():
        return result.getValue(0)

def getlist(query, params = {}):
    list = []
    params = map2hash(params)
    result = qp.execute(query, params)
    while result.next():
        list.append(result.getValue(0))
    return list

def map2hash(map):
    hash = HashMap()
    for (key, value) in map.items():
        hash.put(key, value)
    return hash

def find_types(at, rt):
    params = {"AT" : at, "RT" : rt}
    return getlist(PREFIXES + """
    select $TT from
    instance-of($RPC, tmcl:roleplayer-constraint),
    tmcl:applies-to($RPC : tmcl:constraint-role, %AT% : tmcl:assoctype-role),
    tmcl:applies-to($RPC : tmcl:constraint-role, %RT% : tmcl:roletype-role),
    tmcl:applies-to($RPC : tmcl:constraint-role, $TT : tmcl:topictype-role)?""",
                   params)

def get_min_card(at, rt):
    params = {"AT" : at, "RT" : rt}
    return int(getvalue(PREFIXES + """
    select $CARD from
    instance-of($ARC, tmcl:associationrole-constraint),
    tmcl:card-min($ARC, $CARD),
    tmcl:applies-to($ARC : tmcl:constraint-role, 
                    %AT% : tmcl:assoctype-role), 
    tmcl:applies-to($ARC : tmcl:constraint-role, 
                    %RT% : tmcl:roletype-role)?""", params))

def get_max_card(at, rt):
    params = {"AT" : at, "RT" : rt}
    return int(getvalue(PREFIXES + """
    select $CARD from
    instance-of($ARC, tmcl:associationrole-constraint),
    tmcl:card-max($ARC, $CARD),
    tmcl:applies-to($ARC : tmcl:constraint-role, 
                    %AT% : tmcl:assoctype-role) ,
    tmcl:applies-to($ARC : tmcl:constraint-role, 
                    %RT% : tmcl:roletype-role)?""", params))

prefixes = {"ph" : "http://psi.garshol.priv.no/photo/",
            "dc" : "http://purl.org/dc/elements/1.1/",
            "tm" : "http://psi.topicmaps.org/iso13250/model/",
            "foaf" : "http://xmlns.com/foaf/0.1/",
            "rdfs" : "http://www.w3.org/2000/01/rdf-schema#",
            "thes" : "http://www.techquila.com/psi/thesaurus/#",
            "xsd" : "http://www.w3.org/2001/XMLSchema#",
            "rdf" : "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
            "owl" : "http://www.w3.org/2002/07/owl#"}
def qname(topic):
    for psi in topic.getSubjectIdentifiers():
        str = psi.getAddress()
        for (prefix, ns) in prefixes.items():
            if str.startswith(ns):
                rest = str[len(ns) : ]
                return "%s:%s" % (prefix, rest)

schema = ImportExportUtils.getReader(sys.argv[1]).read()
qp = QueryUtils.getQueryProcessor(schema)

PREFIXES = """
using tmcl for i"http://psi.topicmaps.org/tmcl/"
using tm for i"http://psi.topicmaps.org/iso13250/model/"
using tmr for i"http://psi.ontopia.net/tm2rdf/#"
"""

default_name_type = getvalue(PREFIXES + "$T = tm:topic-name?")

for (prefix, ns) in prefixes.items():
    print "@prefix %s: <%s> ." % (prefix, ns)

# ----- CLASSES

print

result = qp.execute(PREFIXES + """
instance-of($TT, tmcl:topictype)?""")
while result.next():
    tt = qname(result.getValue("TT"))
    print "%s rdf:type owl:Class ." % tt

# ----- SUBCLASSING

print

result = qp.execute(PREFIXES +
            "tm:superclass-subclass($SUP : tm:superclass, $SUB : tm:subclass)?")
while result.next():
    sup = result.getValue("SUP")
    sub = result.getValue("SUB")
    print "%s rdfs:subClassOf %s ." % (qname(sup), qname(sub))

# ----- ABSTRACT CLASSES

print

result = qp.execute(PREFIXES + """
select $CLASS from
instance-of($C, tmcl:abstract-topictype-constraint),
tmcl:applies-to($C : tmcl:constraint-role, $CLASS : tmcl:topictype-role)
?""")
while result.next():
    k = result.getValue("CLASS")

    subs = getlist(PREFIXES +
            "tm:superclass-subclass(%SUP% : tm:superclass, $SUB : tm:subclass)?",
            {"SUP" : k})

    print "%s owl:equivalentClass [ rdf:type owl:Class;" % qname(k)
    print "  owl:unionOf (%s) ]." % string.join(map(qname, subs), ", ")
    
# ----- NAME PROPERTIES

print

preferred_for = {}
result = qp.execute(PREFIXES +
                    "tmr:name-property($TT : tmr:type, $NP : tmr:property)?")
while result.next():
    tt = result.getValue("TT")
    np = result.getValue("NP")

    list = preferred_for.get(np)
    if not list:
        list = []
        preferred_for[np] = list
    list.append(tt)

result = qp.execute(PREFIXES + "instance-of($NT, tmcl:nametype)?")
while result.next():
    nt = result.getValue("NT")

    params = {"NT" : nt}
    tts = getlist(PREFIXES + """
    select $TT from
    instance-of($TNC, tmcl:topicname-constraint),
    tmcl:applies-to($TNC : tmcl:constraint-role, $TT : tmcl:topictype-role),
    tmcl:applies-to($TNC : tmcl:constraint-role, %NT% : tmcl:nametype-role)?""",
                  params)

    if nt == default_name_type:
        continue # we process these separately

    print "%s rdf:type owl:DatatypeProperty;" % qname(nt)
    if len(tts) == 1:
        print "  rdfs:domain %s;" % qname(tts[0])
    elif len(tts) > 1:
        print "  # domain conflict, %s different types" % (len(tts))
    else:
        print "  # domain unknown"
    print "  rdfs:range rdfs:Literal ."

for (np, tts) in preferred_for.items():
    if qname(np).startswith("rdfs:"):
        continue # we can't redefine RDFS vocabulary
    
    print "%s rdf:type owl:DatatypeProperty;" % qname(np)
    if len(tts) == 1:
        print "  rdfs:domain %s;" % qname(tts[0])
    elif len(tts) > 1:
        print "  # domain conflict, %s different types" % (len(tts))
    else:
        print "  # domain unknown"
    print "  rdfs:range rdfs:Literal ."

    for tt in tts:
        print "%s rdfs:subClassOf [ rdf:type owl:Restriction;" % qname(tt)
        print "  owl:onProperty %s;" % qname(np)
        print "  owl:cardinality \"1\";"
        print "]."

# ----- OCCURRENCE PROPERTIES

print

result = qp.execute(PREFIXES + "instance-of($OT, tmcl:occurrencetype)?")
while result.next():
    ot = result.getValue("OT")

    params = {"OT" : ot}
    tts = getlist(PREFIXES + """
    select $TT from
    instance-of($TOC, tmcl:topicoccurrence-constraint),
    tmcl:applies-to($TOC : tmcl:constraint-role, $TT : tmcl:topictype-role),
    tmcl:applies-to($TOC : tmcl:constraint-role,
                    %OT% : tmcl:occurrencetype-role)?""", params)

    print "%s rdf:type owl:DatatypeProperty;" % qname(ot)
    if len(tts) == 1:
        print "  rdfs:domain %s;" % qname(tts[0])
    elif len(tts) > 1:
        print "  # domain conflict, %s different types" % (len(tts))
    else:
        print "  # domain unknown"

    datatype = getvalue(PREFIXES + """
    select $DT from
    instance-of($ODTC, tmcl:occurrencedatatype-constraint),
    tmcl:applies-to($ODTC : tmcl:constraint-role, $DT : tmcl:datatype-role), 
    tmcl:applies-to($ODTC : tmcl:constraint-role, %OT% : tmcl:occurrencetype-role) 
    ?""", params)
    print "  rdfs:range %s ." % (qname(datatype) or "rdfs:Literal")

print

result = qp.execute(PREFIXES + "instance-of($AT, tmcl:associationtype)?")
while result.next():
    at = result.getValue("AT")

    params = {"AT" : at}
    roletypes = getlist(PREFIXES + """
    select $RT from
    instance-of($ARC, tmcl:associationrole-constraint),
    tmcl:applies-to($ARC : tmcl:constraint-role, 
                    %AT% : tmcl:assoctype-role), 
    tmcl:applies-to($ARC : tmcl:constraint-role, 
                    $RT : tmcl:roletype-role)?""",
                        params)

    if len(roletypes) == 0:
        print "%s rdf:type owl:ObjectProperty . " % qname(at)
        print "  # no specified role types"
    elif len(roletypes) == 1:
        print "%s rdf:type owl:ObjectProperty;  " % qname(at)
        params["RT"] = roletypes[0]
        mincard = get_min_card(at, roletypes[0])
        maxcard = get_max_card(at, roletypes[0])

        if mincard == 2 and maxcard == 2:
            print "  rdf:type owl:SymmetricProperty;"

            types = find_types(at, roletypes[0])
            if len(types) == 0:
                print "  # domain&range unknown"
            elif len(types) == 1:
                print "  rdfs:domain %s;" % qname(types[0])
                print "  rdfs:range %s;" % qname(types[0])
            else:
                print "  # %s types as domain/range" % len(types)
        else:
            print "  # appears to be unary, but not symmetric"
        print "."
    elif len(roletypes) == 2:
        print "%s rdf:type owl:ObjectProperty; " % qname(at)
        preferred = getvalue(PREFIXES +
         "tmr:preferred-role(%AT% : tmr:association-type, $RT : tmr:role-type)?",
                             params)
        if not preferred:
            print "  # had to guess preferred role type"
            preferred = random.choice(roletypes)

        params["RT"] = preferred
        if roletypes[0] == preferred:
            other = roletypes[1]
        else:
            other = roletypes[0]

        types = find_types(at, preferred)
        if len(types) == 0:
            print "  # no domain given"
        elif len(types) == 1:
            print "  rdfs:domain %s;" % qname(types[0])
        else:
            print "  # domain has %s types" % len(types)

        types = find_types(at, other)
        if len(types) == 0:
            print "  # no range given"
        elif len(types) == 1:
            print "  rdfs:range %s;" % qname(types[0])
        else:
            print "  # range has %s types" % len(types)
            
        print "."
    else:
        print "%s rdf:type rdf:Class . " % qname(at)
        print "  # n-ary; turn into a resource"
        print roletypes

# ----- DISJOINTNESS
        
print
    
result = qp.execute(PREFIXES + """
instance-of($ETC, tmcl:exclusive-instance),
tmcl:applies-to($ETC : tmcl:constraint-role, $TT1 : tmcl:topictype-role),
tmcl:applies-to($ETC : tmcl:constraint-role, $TT2 : tmcl:topictype-role),
$TT1 /= $TT2 ?""")
while result.next():
    tt1 = result.getValue("TT1")
    tt2 = result.getValue("TT2")
    print "%s owl:disjointWith %s ." % (qname(tt1), qname(tt2))
