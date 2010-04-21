
import sys
from net.ontopia.topicmaps.xml import InvalidTopicMapException
from net.ontopia.topicmaps.utils import ImportExportUtils, DuplicateSuppressionUtils
from net.ontopia.topicmaps.query.utils import QueryUtils, QueryWrapper, RowMapperIF

# TODO
#  - figure out the subtyping problem

prefixes = """
using xtm for i"http://www.topicmaps.org/xtm/1.0/core.xtm#"

overlaps($C, $TT) :-
  association-role($A, $R1),
  type($A, $TO),
  subject-identifier($TO, "http://psi.topicmaps.org/tmcl/overlaps"),
  type($R1, $TAS),
  subject-identifier($TAS, "http://psi.topicmaps.org/tmcl/allows"),
  association-role($A, $R2),
  type($R2, $TAD),
  subject-identifier($TAD, "http://psi.topicmaps.org/tmcl/allowed"),
  role-player($R1, $C),
  role-player($R2, $TT).

constrained-tt($C, $TT) :-
  association-role($A, $R1),
  type($A, $TO),
  subject-identifier($TO, "http://psi.topicmaps.org/tmcl/constrained-topic-type"),
  type($R1, $TAS),
  subject-identifier($TAS, "http://psi.topicmaps.org/tmcl/constraint"),
  association-role($A, $R2),
  type($R2, $TAD),
  subject-identifier($TAD, "http://psi.topicmaps.org/tmcl/constrained"),
  role-player($R1, $C),
  role-player($R2, $TT).

constrained-s($C, $ST) :-
  association-role($A, $R1),
  type($A, $TO),
  subject-identifier($TO, "http://psi.topicmaps.org/tmcl/constrained-statement"),
  type($R1, $TAS),
  subject-identifier($TAS, "http://psi.topicmaps.org/tmcl/constraint"),
  association-role($A, $R2),
  type($R2, $TAD),
  subject-identifier($TAD, "http://psi.topicmaps.org/tmcl/constrained"),
  role-player($R1, $C),
  role-player($R2, $ST).

required-s($C, $S) :-
  association-role($A, $R1),
  type($A, $TO),
  subject-identifier($TO, "http://psi.topicmaps.org/tmcl/required-scope"),
  type($R1, $TAS),
  subject-identifier($TAS, "http://psi.topicmaps.org/tmcl/constraint"),
  association-role($A, $R2),
  type($R2, $TAD),
  subject-identifier($TAD, "http://psi.topicmaps.org/tmcl/constrained"),
  role-player($R1, $C),
  role-player($R2, $S).

card-min($C, $MIN) :-
  occurrence($C, $OCC),
  type($OCC, $OT),
  subject-identifier($OT, "http://psi.topicmaps.org/tmcl/card-min"),
  value($OCC, $MIN).

card-max($C, $MAX) :-
  occurrence($C, $OCC),
  type($OCC, $OT),
  subject-identifier($OT, "http://psi.topicmaps.org/tmcl/card-max"),
  value($OCC, $MAX).

direct-subclass-of($SUB, $SUPER) :-
  association-role($A, $R1),
  type($A, $TO),
  subject-identifier($TO, "http://www.topicmaps.org/xtm/1.0/core.xtm#superclass-subclass"),
  type($R1, $TAS),
  subject-identifier($TAS, "http://www.topicmaps.org/xtm/1.0/core.xtm#subclass"),
  association-role($A, $R2),
  type($R2, $TAD),
  subject-identifier($TAD, "http://www.topicmaps.org/xtm/1.0/core.xtm#superclass"),
  role-player($R1, $SUB),
  role-player($R2, $SUPER).

subclass-of($SUB, $SUPER) :- {
  direct-subclass-of($SUB, $SUPER) |
  direct-subclass-of($SUB, $MID),
  subclass-of($MID, $SUPER)
}.
"""

def noresults(tm, query, msg):
    errors = []
    qw = QueryWrapper(tm)
    qw.setDeclarations(prefixes)
    for map in qw.queryForMaps(query):
        errors.append((map["BAD"], msg % map))
    return errors

def get_list(tm, query, params):
    qw = QueryWrapper(tm)
    qw.setDeclarations(prefixes)
    return qw.queryForMaps(query, params)

def get_constraints(tm, query):
    qw = QueryWrapper(tm)
    qw.setDeclarations(prefixes)
    return qw.queryForList(query, ConstraintBuilder())

class ConstraintBuilder(RowMapperIF):
  def mapRow(self, result, rowno):
      index = result.getIndex("S")
      if index != -1:
          scope = result.getValue(index)
      else:
          scope = None
      return Constraint(result.getValue("TT"),
                        result.getValue("ST"),
                        result.getValue("MIN"),
                        result.getValue("MAX"),
                        scope)

class Constraint:
    def __init__(self, tt, st, min, max, scope):
        self._tt = tt
        self._st = st
        self._min = int(min or "0")
        if max and not max == "*":
            self._max = int(max)
        else:
            self._max = "*" # numbers are smaller than strings in python
        self._scope = scope

def validate_constraints(topicmap, query1, query2, what):
    errors = []
    constraints = get_constraints(topicmap, query1)
    for c in constraints:
        # FIXME: must make it illegal to have max < min
        params = {"TT" : c._tt, "ST" : c._st, "S" : c._scope}
        for i in get_list(topicmap, query2, params):
            count = int(i["OBJ"])
            if count < c._min:
                errors.append((i["T"], ("must have at least %s %s of type " +
                               "%s, but had only %s") %
                               (c._min, what, c._st, count)))
            elif count > c._max:
                errors.append((i["T"], ("must have at most %s %s of type " +
                               "%s, but had %s") %
                               (c._max, what, c._st, count)))
    return errors
            
def validate(topicmap):
    # clause 6.2, gvc
    errors = noresults(topicmap, """
select $BAD from
  instance-of($T, $BAD),
  not(instance-of($BAD, $TTT),
      subject-identifier($TTT, "http://psi.topicmaps.org/tmcl/topic-type"))?
""", "has instances, but is not an instance of tmcl:topic-type")

    # clause 6.3, gvc
    errors += noresults(topicmap, """
select $BAD from
  topic-name($T, $TN), type($TN, $BAD),
  not(instance-of($BAD, $TNT),
      subject-identifier($TNT, "http://psi.topicmaps.org/tmcl/name-type"))?
""", "is used as a name type, but is not an instance of tmcl:name-type")

    # clause 6.4, gvc
    errors += noresults(topicmap, """
select $BAD from
  occurrence($T, $OCC), type($OCC, $BAD),
  not(instance-of($BAD, $TOT),
      subject-identifier($TOT, "http://psi.topicmaps.org/tmcl/occurrence-type"))?
""", "is used as an occurrence type, but is not an instance of tmcl:occurrence-type")

    # clause 6.5, gvc
    errors += noresults(topicmap, """
select $BAD from
  association($A), type($A, $BAD),
  not(instance-of($BAD, $TAT),
      subject-identifier($TAT, "http://psi.topicmaps.org/tmcl/association-type"))?
""", "is used as an association type, but is not an instance of tmcl:association-type")

    # clause 6.6, gvc
    errors += noresults(topicmap, """
select $BAD from
  association-role($A, $AR), type($AR, $BAD),
  not(instance-of($BAD, $TRT),
      subject-identifier($TRT, "http://psi.topicmaps.org/tmcl/role-type"))?
""", "is used as a role type, but is not an instance of tmcl:role-type")

    # clause 6.7, gvc
    errors += noresults(topicmap, """
select $BAD from
  instance-of($BAD, $TT1),
  instance-of($BAD, $TT2),
  $TT1 /= $TT2,
  not(subclass-of($TT1, $TT2)),
  not(subclass-of($TT2, $TT1)),
  not(instance-of($C, $TOD),
      subject-identifier($SI, "http://psi.topicmaps.org/tmcl/overlap-declaration"),
      overlaps($C, $TT1),
      overlaps($C, $TT2))?
""", "has two topic types, which are not declared as overlapping")

    # clause 7.2, cvr
    errors += noresults(topicmap, """
select $BAD from
  instance-of($AC, $TAC),
  subject-identifier($TAC, "http://psi.topicmaps.org/tmcl/abstract-constraint"),
  constrained-tt($AC, $BAD),
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
  instance-of($TNC, $TTNC),
  subject-identifier($TTNC, "http://psi.topicmaps.org/tmcl/topic-name-constraint"),
  constrained-tt($TNC, $TT),
  constrained-s($TNC, $ST),
  { card-max($TNC, $MAX) },
  { card-min($TNC, $MIN) }?
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
        subject-identifier($TT, "http://psi.topicmaps.org/iso13250/model/subject") }, 
      instance-of($TNC, $TTNC),
      subject-identifier($TTNC, "http://psi.topicmaps.org/tmcl/topic-name-constraint"),
      constrained-tt($TNC, $TT),
      constrained-s($TNC, $BAD))?
""", "is used as a name type on topic types where this is not allowed")

    # clause 7.7, cvr
    query1 = """
select $TT, $ST, $S, $MAX, $MIN from
  instance-of($VNC, $TVNC),
  subject-identifier($TVNC, "http://psi.topicmaps.org/tmcl/variant-name-constraint"),
  constrained-tt($VNC, $TT),
  constrained-s($VNC, $ST),
  required-s($VNC, $S),
  { card-max($VNC, $MAX) },
  { card-min($VNC, $MIN) }?
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
  not(instance-of($VNC, $TVNC),
      subject-identifier($TVNC, "http://psi.topicmaps.org/tmcl/variant-name-constraint"),
     constrained-tt($VNC, $TT),
     constrained-s($VNC, $ST),
     required-s($VNC, $S))?
""", "is used as a variant name on topic names where this is not allowed")
    
    # clause 7.8, cvr
    query1 = """
select $TT, $ST, $MAX, $MIN from
  instance-of($TOC, $TTOC),
  subject-identifier($TTOC, "http://psi.topicmaps.org/tmcl/topic-occurrence-constraint"),
  constrained-tt($TOC, $TT),
  constrained-s($TOC, $ST),
  { card-max($TOC, $MAX) },
  { card-min($TOC, $MIN) }?
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
  not(instance-of($TOC, $TTOC),
      subject-identifier($TTOC, "http://psi.topicmaps.org/tmcl/topic-occurrence-constraint"),
      constrained-tt($TOC, $TT),
      constrained-s($TOC, $BAD))?
""", "is used as an occurrence type on topic types where this is not allowed")
    
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
        
if __name__ == "__main__":
    topicmap = load_tm(sys.argv[1 : ])
    errors = validate(topicmap)
    report(errors)
