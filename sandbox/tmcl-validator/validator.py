
import sys
from net.ontopia.topicmaps.xml import InvalidTopicMapException
from net.ontopia.topicmaps.utils import ImportExportUtils, DuplicateSuppressionUtils
from net.ontopia.topicmaps.query.utils import QueryUtils, QueryWrapper, RowMapperIF

prefixes = """
using tmcl for i"http://psi.topicmaps.org/tmcl/"
using tmdm for i"http://psi.topicmaps.org/iso13250/model/"
using xtm for i"http://www.topicmaps.org/xtm/1.0/core.xtm#"
using dt for i"http://psi.garshol.priv.no/datatypes/"
using xsd for i"http://www.w3.org/2001/XMLSchema#"

subclass-of($SUB, $SUPER) :- {
  xtm:superclass-subclass($SUB : xtm:subclass, $SUPER : xtm:superclass) |
  xtm:superclass-subclass($SUB : xtm:subclass, $MID : xtm:superclass),
  subclass-of($MID, $SUPER)
}.

subdatatype-of($SUB, $SUPER) :- {
  dt:subdatatype-of($SUB : dt:subtype, $SUPER : dt:supertype) |
  dt:subdatatype-of($SUB : dt:subtype, $MID : dt:supertype),
  subdatatype-of($MID, $SUPER)
}.

validly-substitutable-for($SUB, $SUPER) :- {
  $SUB = $SUPER |
  subdatatype-of($SUB, $SUPER)
}.
 
"""

def noresults(tm, query, msg):
    errors = []
    qw = QueryWrapper(tm)
    qw.setDeclarations(prefixes)
    for map in qw.queryForMaps(query):
        errors.append((map["BAD"], msg % map))
    return errors

def display(tm, query):
    qw = QueryWrapper(tm)
    qw.setDeclarations(prefixes)
    for map in qw.queryForMaps(query):
        print map

def get_list(tm, query, params):
    qw = QueryWrapper(tm)
    qw.setDeclarations(prefixes)
    return qw.queryForMaps(query, params)

def get_constraints(tm, query):
    qw = QueryWrapper(tm)
    qw.setDeclarations(prefixes)
    return qw.queryForList(query, ConstraintBuilder())

def getOptional(result, name):
    index = result.getIndex(name)
    if index != -1:
        return result.getValue(index)
    else:
        return None

class ConstraintBuilder(RowMapperIF):
    def mapRow(self, result, rowno):
        return Constraint(getOptional(result, "TT"),
                          result.getValue("ST"),
                          result.getValue("MIN"),
                          result.getValue("MAX"),
                          getOptional(result, "S"),
                          getOptional(result, "RT"))
  
class Constraint:
    def __init__(self, tt, st, min, max, scope, roletype):
        self._tt = tt
        self._st = st
        self._min = int(min or "0")
        if max and not max == "*":
            self._max = int(max)
        else:
            self._max = "*" # numbers are smaller than strings in python
        self._scope = scope
        self._rt = roletype

def validate_constraints(topicmap, query1, query2, error):
    if type(error) == type(""):
        what = error
        error = lambda env: "%s of type %s" % (env["what"], env["c"]._st)
    
    errors = []
    constraints = get_constraints(topicmap, query1)
    for c in constraints:
        # FIXME: must make it illegal to have max < min
        params = {"TT" : c._tt, "ST" : c._st, "S" : c._scope,
                  "RT" : c._rt}

        for i in get_list(topicmap, query2, params):
            count = int(i["OBJ"])
            if count < c._min:
                errors.append((i["T"], ("must have at least %s %s" +
                                        ", but had only %s") %
                               (c._min, error(locals()), count)))
            elif count > c._max:
                errors.append((i["T"], ("must have at most %s %s" +
                                        ", but had %s") %
                               (c._max, error(locals()), count)))
    return errors

def validate(topicmap):
    # clause 6.2, gvc
    errors = noresults(topicmap, """
select $BAD from
  instance-of($T, $BAD),
  not(instance-of($BAD, tmcl:topic-type))?
""", "has instances, but is not an instance of tmcl:topic-type")

    # clause 6.3, gvc
    errors += noresults(topicmap, """
select $BAD from
  topic-name($T, $TN), type($TN, $BAD),
  not(instance-of($BAD, tmcl:name-type))?
""", "is used as a name type, but is not an instance of tmcl:name-type")

    # clause 6.4, gvc
    errors += noresults(topicmap, """
select $BAD from
  occurrence($T, $OCC), type($OCC, $BAD),
  not(instance-of($BAD, tmcl:occurrence-type))?
""", "is used as an occurrence type, but is not an instance of tmcl:occurrence-type")

    # clause 6.5, gvc
    errors += noresults(topicmap, """
select $BAD from
  association($A), type($A, $BAD),
  not(instance-of($BAD, tmcl:association-type))?
""", "is used as an association type, but is not an instance of tmcl:association-type")

    # clause 6.6, gvc
    errors += noresults(topicmap, """
select $BAD from
  association-role($A, $AR), type($AR, $BAD),
  not(instance-of($BAD, tmcl:role-type))?
""", "is used as a role type, but is not an instance of tmcl:role-type")

    # clause 6.7, gvc
    errors += noresults(topicmap, """
select $BAD from
  instance-of($BAD, $TT1),
  instance-of($BAD, $TT2),
  $TT1 /= $TT2,
  not(subclass-of($TT1, $TT2)),
  not(subclass-of($TT2, $TT1)),
  not(instance-of($C, tmcl:overlap-declaration),
      tmcl:overlaps($C : tmcl:allows, $TT1 : tmcl:allowed),
      tmcl:overlaps($C : tmcl:allows, $TT2 : tmcl:allowed))?
""", "has two topic types, which are not declared as overlapping")

    # clause 7.2, cvr
    errors += noresults(topicmap, """
select $BAD from
  instance-of($AC, tmcl:abstract-constraint),
  tmcl:constrained-topic-type($AC : tmcl:constraint, $BAD : tmcl:constrained),
  direct-instance-of($T, $BAD)?
""", "has direct instances, but is declared as abstract")

    # clause 7.3, cvr
    # FIXME: cannot implement; must have regexp library
    # clause 7.4, cvr
    # FIXME: cannot implement; must have regexp library
    # clause 7.5, cvr
    # FIXME: cannot implement; must have regexp library

    # clause 7.6, cvr
    query1 = """
select $TT, $ST, $MAX, $MIN from
  instance-of($C, tmcl:topic-name-constraint),
  tmcl:constrained-topic-type($C : tmcl:constraint, $TT : tmcl:constrained),
  tmcl:constrained-statement($C : tmcl:constraint, $ST : tmcl:constrained),
  { tmcl:card-max($C, $MAX) },
  { tmcl:card-min($C, $MIN) }?
"""
    query2 = """
select $T, count($OBJ) from
  instance-of($T, %TT%),
  { topic-name($T, $OBJ),
    type($OBJ, %ST%) }?
        """
    errors += validate_constraints(topicmap, query1, query2, "names")

    # clause 7.6, gvr
    errors += noresults(topicmap, """
select $BAD from
  topic-name($T, $TN),
  type($TN, $BAD),
  not({ instance-of($T, $TT) |
        not(direct-instance-of($T, $TT)),
        $TT = tmdm:subject }, 
      instance-of($C, tmcl:topic-name-constraint),
      tmcl:constrained-topic-type($C : tmcl:constraint, $TT : tmcl:constrained),
      tmcl:constrained-statement($C : tmcl:constraint, $BAD : tmcl:constrained))?
""", "is used as a name type on topic types where this is not allowed")

    # clause 7.7, cvr
    query1 = """
select $TT, $ST, $S, $MAX, $MIN from
  instance-of($C, tmcl:variant-name-constraint),
  tmcl:constrained-topic-type($C : tmcl:constraint, $TT : tmcl:constrained),
  tmcl:constrained-statement($C : tmcl:constraint, $ST : tmcl:constrained),
  tmcl:constrained-scope-topic($C : tmcl:constraint, $S : tmcl:constrained),
  { tmcl:card-max($C, $MAX) },
  { tmcl:card-min($C, $MIN) }?
"""
    query2 = """
select $TN, count($OBJ) from
  instance-of($T, %TT%),
  { topic-name($T, $TN),
    type($TN, $ST),
    variant($TN, $OBJ),
    scope($OBJ, %S%) }?
        """
    errors += validate_constraints(topicmap, query1, query2, "variant names")

    # clause 7.7, gvr
    errors += noresults(topicmap, """
select $BAD from
  instance-of($BAD, $TT),
  topic-name($BAD, $TN),
  type($TN, $ST),
  variant($TN, $VN),
  scope($VN, $S),  
  not(instance-of($C, tmcl:variant-name-constraint),
      tmcl:constrained-topic-type($C : tmcl:constraint, $TT : tmcl:constrained),
      tmcl:constrained-statement($C : tmcl:constraint, $ST : tmcl:constrained),
      tmcl:constrained-scope-topic($C : tmcl:constraint, $S : tmcl:constrained))?
""", "is used as a variant name on topic names where this is not allowed")
    
    # clause 7.8, cvr
    query1 = """
select $TT, $ST, $MAX, $MIN from
  instance-of($C, tmcl:topic-occurrence-constraint),
  tmcl:constrained-topic-type($C : tmcl:constraint, $TT : tmcl:constrained),
  tmcl:constrained-statement($C : tmcl:constraint, $ST : tmcl:constrained),
  { tmcl:card-max($C, $MAX) },
  { tmcl:card-min($C, $MIN) }?
"""
    query2 = """
select $T, count($OBJ) from
  instance-of($T, %TT%),
  { occurrence($T, $OBJ),
    type($OBJ, %ST%) }?
        """
    errors += validate_constraints(topicmap, query1, query2, "occurrences")

    # clause 7.8, gvr
    # FIXME: tmdm:subject
    # FIXME: must handle subtyping here!
    # FIXME: no errors if $T has no type
    errors += noresults(topicmap, """
select $BAD from
  direct-instance-of($T, $TT),
  occurrence($T, $OCC),
  type($OCC, $BAD),
  not(instance-of($C, tmcl:topic-occurrence-constraint),
      tmcl:constrained-topic-type($C : tmcl:constraint, $TT : tmcl:constrained),
      tmcl:constrained-statement($C : tmcl:constraint, $BAD : tmcl:constrained))?
""", "is used as an occurrence type on topic types where this is not allowed")

    # clause 7.9, cvr
    query1 = """
select $TT, $ST, $RT, $MAX, $MIN from
  instance-of($C, tmcl:topic-role-constraint),
  tmcl:constrained-topic-type($C : tmcl:constraint, $TT : tmcl:constrained),
  tmcl:constrained-statement($C : tmcl:constraint, $ST : tmcl:constrained),
  tmcl:constrained-role($C : tmcl:constraint, $RT : tmcl:constrained),
  { tmcl:card-max($C, $MAX) },
  { tmcl:card-min($C, $MIN) }?
"""
    query2 = """
select $T, count($OBJ) from
  instance-of($T, %TT%),
  { role-player($OBJ, $T),
    type($OBJ, %RT%),
    association-role($ASSOC, $OBJ),
    type($ASSOC, %ST%) }?
        """
    errors += validate_constraints(topicmap, query1, query2, "roles")

    # clause 7.9, gvr
    # FIXME: subclassing
    errors += noresults(topicmap, """
select $BAD from  
  role-player($ROLE, $T),
  type($ROLE, $RT),
  association-role($ASSOC, $ROLE),
  type($ASSOC, $BAD),
  not({ instance-of($T, $TT) |
        not(instance-of($T, $TT)),
        $TT = tmdm:subject },
      instance-of($C, tmcl:topic-role-constraint),
      tmcl:constrained-topic-type($C : tmcl:constraint, $TT : tmcl:constrained),
      tmcl:constrained-statement($C : tmcl:constraint, $BAD : tmcl:constrained),
      tmcl:constrained-role($C : tmcl:constraint, $RT : tmcl:constrained))?
""", "is used as an association type on topic types where this is not allowed")

    # clause 7.10, cvr
    query1 = """
select $ST, $S, $MAX, $MIN from
  instance-of($C, tmcl:scope-constraint),
  tmcl:constrained-statement($C : tmcl:constraint, $ST : tmcl:constrained),
  tmcl:constrained-scope($C : tmcl:constraint, $S : tmcl:constrained),
  { tmcl:card-max($C, $MAX) },
  { tmcl:card-min($C, $MIN) }?
"""
    query2 = """
select $T, count($OBJ) from
  type($T, %ST%),
  { scope($T, $OBJ),
    { instance-of($OBJ, %S%) | %S% = tmdm:subject} }?
        """
    errors += validate_constraints(topicmap, query1, query2, "scopes")
    
    # clause 7.10, gvr
    # FIXME: subclassing
    errors += noresults(topicmap, """
select $BAD from  
  type($OBJ, $ST),
  scope($OBJ, $BAD),
  not({ instance-of($BAD, $TT) |
        not(instance-of($BAD, $TT)),
        $TT = tmdm:subject },
      instance-of($C, tmcl:scope-constraint),
      tmcl:constrained-statement($C : tmcl:constraint, $ST : tmcl:constrained),
      tmcl:constrained-scope($C : tmcl:constraint, $TT : tmcl:constrained))?
""", "is used as a scope on statements where this is not allowed")
    
    # clause 7.11, cvr
    query1 = """
select $TT, $ST, $S, $MAX, $MIN from
  instance-of($C, tmcl:scope-required-constraint),
  tmcl:constrained-topic-type($C : tmcl:constraint, $TT : tmcl:constrained),
  tmcl:constrained-statement($C : tmcl:constraint, $ST : tmcl:constrained),
  tmcl:constrained-scope-topic($C : tmcl:constraint, $S : tmcl:constrained),
  { tmcl:card-max($C, $MAX) },
  { tmcl:card-min($C, $MIN) }?
"""
    query2 = """
select $T, count($OBJ) from
  instance-of($T, %TT%),
  { { topic-name($T, $OBJ) |
      occurrence($T, $OBJ) |
      role-player($ROLE, $T),
      association-role($OBJ, $ROLE) },
    type($OBJ, %ST%),
    scope($OBJ, %S%) }?
        """
    error = (lambda env: "statements of type %s in scope %s" %
             (env["c"]._st, env["c"]._scope))
    errors += validate_constraints(topicmap, query1, query2, error)

    # clause 7.12, cvr
    # FIXME: tmdm:subject
    # FIXME: subtyping
    query1 = """
select $TT, $ST, $MAX, $MIN from
  instance-of($C, tmcl:reifier-constraint),
  tmcl:constrained-statement($C : tmcl:constraint, $ST : tmcl:constrained),
  tmcl:allowed-reifier($C : tmcl:allows, $TT : tmcl:allowed),
  { tmcl:card-max($C, $MAX) },
  { tmcl:card-min($C, $MIN) }?
"""
    query2 = """
select $T, count($OBJ) from
  type($T, %ST%),
  { reifies($OBJ, $T),
    instance-of($OBJ, %TT%) }?
        """
    error = (lambda env: "reifiers of type %s, since it is of type %s" %
             (env["c"]._tt, env["c"]._st))
    errors += validate_constraints(topicmap, query1, query2, error)

    # clause 7.13, cvr
    # FIXME: tmdm:subject
    # FIXME: subtyping
    query1 = """
select $TT, $ST, $MAX, $MIN from
  instance-of($C, tmcl:topic-reifies-constraint),
  tmcl:constrained-topic-type($C : tmcl:constraint, $TT : tmcl:constrained),
  tmcl:constrained-statement($C : tmcl:constraint, $ST : tmcl:constrained),
  { tmcl:card-max($C, $MAX) },
  { tmcl:card-min($C, $MIN) }?
"""
    query2 = """
select $T, count($OBJ) from
  instance-of($T, %TT%),
  { reifies($T, $OBJ),
    type($OBJ, %ST%) }?
        """
    error = (lambda env: "statements of type %s that it reifies" %
             (env["c"]._st))
    errors += validate_constraints(topicmap, query1, query2, error)

    # clause 7.14, cvr
    query1 = """
select $ST, $RT, $MAX, $MIN from
  instance-of($C, tmcl:association-role-constraint),
  tmcl:constrained-statement($C : tmcl:constraint, $ST : tmcl:constrained),
  tmcl:constrained-role($C : tmcl:constraint, $RT : tmcl:constrained),
  { tmcl:card-max($C, $MAX) },
  { tmcl:card-min($C, $MIN) }?
"""
    query2 = """
select $T, count($OBJ) from
  type($T, %ST%),
  { association-role($T, $OBJ),
    type($OBJ, %RT%) }?
        """
#     error = (lambda env: "statements of type %s that it reifies" %
#              (env["c"]._st))
    errors += validate_constraints(topicmap, query1, query2, "roles")

    # clause 7.14, gvr
    errors += noresults(topicmap, """
select $BAD, $ST from  
  type($OBJ, $ST),
  association-role($OBJ, $ROLE),
  type($ROLE, $BAD),
  not(instance-of($C, tmcl:association-role-constraint),
      tmcl:constrained-statement($C : tmcl:constraint, $ST : tmcl:constrained),
      tmcl:constrained-role($C : tmcl:constraint, $BAD : tmcl:constrained))?
""", "is used as a role type in associations of type %(ST)s, where this is not allowed")
 
    # clause 7.15, gvr
    errors += noresults(topicmap, """
select $BAD, $RT1, $TT1, $RT2, $TT2 from
  instance-of($SOMEC, tmcl:role-combination-constraint),
  tmcl:constrained-statement($SOMEC : tmcl:constraint, $BAD : tmcl:constrained),
  type($ASSOC, $BAD),
  association-role($ASSOC, $R1),
  association-role($ASSOC, $R2),
  $R1 /= $R2,
  role-player($R1, $T1),
  role-player($R2, $T2),
  type($R1, $RT1),
  type($R2, $RT2),
  direct-instance-of($T1, $TT1),
  direct-instance-of($T2, $TT2),
  not(instance-of($C, tmcl:role-combination-constraint),
      tmcl:constrained-statement($C : tmcl:constraint, $BAD : tmcl:constrained),
      { tmcl:constrained-role($C : tmcl:constraint, $RT1 : tmcl:constrained),
        tmcl:constrained-topic-type($C : tmcl:constraint, $TT1 : tmcl:constrained),
        tmcl:other-constrained-role($C : tmcl:constraint, $RT2 : tmcl:constrained),
        tmcl:other-constrained-topic-type($C : tmcl:constraint, $TT2 : tmcl:constrained)
      | tmcl:constrained-role($C : tmcl:constraint, $RT2 : tmcl:constrained),
        tmcl:constrained-topic-type($C : tmcl:constraint, $TT2 : tmcl:constrained),
        tmcl:other-constrained-role($C : tmcl:constraint, $RT1 : tmcl:constrained),
        tmcl:other-constrained-topic-type($C : tmcl:constraint, $TT1 : tmcl:constrained) }),
  /* trick to remove duplicate error messages */
  object-id($RT1, $ID1),
  object-id($RT2, $ID2),
  $ID1 < $ID2?
""", "has illegal role player combination (%(RT1)s, %(TT1)s) and (%(RT2)s, %(TT2)s)")

    # clause 7.16, cvr
    errors += noresults(topicmap, """
select $BAD, $RDTPSI, $CDTPSI from
  instance-of($C, tmcl:occurrence-datatype-constraint),
  tmcl:constrained-statement($C : tmcl:constraint, $ST : tmcl:constrained),
  tmcl:datatype($C, $CDTPSI),
  type($BAD, $ST),
  datatype($BAD, $RDTPSI),
  not({ subject-identifier($CDT, $CDTPSI),
        subject-identifier($RDT, $RDTPSI),
        validly-substitutable-for($RDT, $CDT) |
        $CDTPSI = $RDTPSI })?
""", "has illegal datatype %(RDTPSI)s, should be %(CDTPSI)s")

    # clause 7.17, cvr
    errors += noresults(topicmap, """
select $BAD, $OCC2 from
  instance-of($C, tmcl:unique-value-constraint),
  tmcl:constrained-statement($C : tmcl:constraint, $ST : tmcl:constrained),
  occurrence($TOPIC1, $BAD),
  type($BAD, $ST),
  occurrence($TOPIC2, $OCC2),
  type($OCC2, $ST),
  $BAD /= $OCC2,
  value($BAD, $VALUE),
  value($OCC2, $VALUE),
  object-id($BAD, $ID1),
  object-id($OCC2, $ID2),
  $ID1 < $ID2?
""", "has same value as %(OCC2)s, but should be unique")

    # clause 7.18, cvr
    # FIXME: cannot implement without regexp
    
    return errors

def report(errors):
    for (topic, msg) in errors:
        print topic
        print "  ", msg
    
    print "%s errors" % len(errors)
    if not errors:
        print "Topic map is valid"
    else:
        print "Topic map is not valid"

def load_tm(files):
    SEED_TM = "/Users/larsga/cvs-co/iso-13250/tmcl/specification/schema.ctm"
    files = files + ["datatypes.ctm"]
    try:
        topicmap = ImportExportUtils.getReader(SEED_TM).read()
        for file in files:
            ImportExportUtils.getImporter(file).importInto(topicmap)
    except InvalidTopicMapException, e:
        print "Invalid topic map", file
        print "  ", e.getMessage()
        sys.exit(1)
    DuplicateSuppressionUtils.removeDuplicates(topicmap)
    return topicmap

def run():
    if len(sys.argv) == 1:
        print """
jython validator.py tmfile [tmfile tmfile tmfile ...]

  Loads all the given files, merges them together, and then runs TMCL
  validation to ensure that the resulting topic map is valid.
"""
        sys.exit(1)
        
    topicmap = load_tm(sys.argv[1 : ])
    errors = validate(topicmap)
    report(errors)

if __name__ == "__main__":
    run()
