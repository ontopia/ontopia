
import sys
from net.ontopia.topicmaps.utils import ImportExportUtils
from net.ontopia.topicmaps.query.utils import QueryUtils, QueryWrapper, RowMapperIF

prefixes = """
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
  subject-identifier($TAS, "http://psi.topicmaps.org/tmcl/constrains"),
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
  subject-identifier($TAS, "http://psi.topicmaps.org/tmcl/constrains"),
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
"""

def noresults(qp, query, msg):
    errors = []
    result = qp.execute(prefixes + query)
    while result.next():
        errors.append((result.getValue(0), msg))
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
        if max:
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
    qp = QueryUtils.getQueryProcessor(topicmap)

    # clause 6.2, gvc
    errors = noresults(qp, """
select $TT from
  instance-of($T, $TT),
  not({ instance-of($TT, $TTT),
        subject-identifier($TTT, "http://psi.topicmaps.org/tmcl/topic-type") |
        subject-identifier($TT, $SI), {
          $SI = "http://psi.topicmaps.org/tmcl/topic-type" |
          $SI = "http://psi.topicmaps.org/tmcl/name-type" |
          $SI = "http://psi.topicmaps.org/tmcl/occurrence-type" |
          $SI = "http://psi.topicmaps.org/tmcl/association-type" |
          $SI = "http://psi.topicmaps.org/tmcl/role-type" |
          $SI = "http://psi.topicmaps.org/tmcl/overlap-declaration" |
          $SI = "http://psi.topicmaps.org/tmcl/topic-name-constraint" |
          $SI = "http://psi.topicmaps.org/tmcl/variant-name-constraint" |
          $SI = "http://psi.topicmaps.org/tmcl/topic-occurrence-constraint" |
          $SI = "http://psi.topicmaps.org/tmcl/abstract-constraint" }
        })?
""", "has instances, but is not an instance of tmcl:topic-type")

    # clause 6.3, gvc
    errors += noresults(qp, """
select $NT from
  topic-name($T, $TN), type($TN, $NT),
  not({ instance-of($NT, $TNT),
        subject-identifier($TNT, "http://psi.topicmaps.org/tmcl/name-type") |
        subject-identifier($TT, $SI),
        $SI = "http://psi.topicmaps.org/iso13250/model/topic-name"
        })?
""", "is used as a name type, but is not an instance of tmcl:name-type")

    # clause 6.4, gvc
    errors += noresults(qp, """
select $OT from
  occurrence($T, $OCC), type($OCC, $OT),
  not({ instance-of($OT, $TOT),
        subject-identifier($TOT, "http://psi.topicmaps.org/tmcl/occurrence-type") |
        subject-identifier($TT, $SI), {
          $SI = "http://psi.topicmaps.org/tmcl/card-min" |
          $SI = "http://psi.topicmaps.org/tmcl/card-max"
        }
})?
""", "is used as an occurrence type, but is not an instance of tmcl:occurrence-type")

    # clause 6.5, gvc
    errors += noresults(qp, """
select $AT from
  association($A), type($A, $AT),
  not({ instance-of($AT, $TAT),
        subject-identifier($TAT, "http://psi.topicmaps.org/tmcl/association-type") |
        subject-identifier($AT, $SI), {
          $SI = "http://psi.topicmaps.org/iso13250/model/supertype-subtype" |
          $SI = "http://psi.topicmaps.org/iso13250/model/type-instance" |
          $SI = "http://psi.topicmaps.org/tmcl/constrained-topic-type" |
          $SI = "http://psi.topicmaps.org/tmcl/constrained-statement" |
          $SI = "http://psi.topicmaps.org/tmcl/required-scope" |
          $SI = "http://psi.topicmaps.org/tmcl/overlaps"
        }
      })?
""", "is used as an association type, but is not an instance of tmcl:association-type")

    # clause 6.6, gvc
    errors += noresults(qp, """
select $RT from
  association-role($A, $AR), type($AR, $RT),
  not({ instance-of($RT, $TRT),
        subject-identifier($TRT, "http://psi.topicmaps.org/tmcl/role-type") |
        subject-identifier($RT, $SI), {
          $SI = "http://psi.topicmaps.org/iso13250/model/supertype" |
          $SI = "http://psi.topicmaps.org/iso13250/model/subtype" |
          $SI = "http://psi.topicmaps.org/iso13250/model/type" |
          $SI = "http://psi.topicmaps.org/iso13250/model/instance" |
          $SI = "http://psi.topicmaps.org/tmcl/constrains" |
          $SI = "http://psi.topicmaps.org/tmcl/constrained" |
          $SI = "http://psi.topicmaps.org/tmcl/constraint" |
          $SI = "http://psi.topicmaps.org/tmcl/allowed" |
          $SI = "http://psi.topicmaps.org/tmcl/allows"
        }
      })?
""", "is used as a role type, but is not an instance of tmcl:role-type")

    # clause 6.7, gvc
    errors += noresults(qp, """
select $T from
  instance-of($T, $TT1),
  instance-of($T, $TT2),
  $TT1 /= $TT2,
  not(instance-of($C, $TOD),
      subject-identifier($SI, "http://psi.topicmaps.org/tmcl/overlap-declaration"),
      overlaps($C, $TT1),
      overlaps($C, $TT2))?
""", "has two topic types, which are not declared as overlapping")

    # clause 7.2, cvr
    errors += noresults(qp, """
select $TT from
  instance-of($AC, $TAC),
  subject-identifier($SI, "http://psi.topicmaps.org/tmcl/abstract-constraint"),
  constrained-tt($AC, $TT),
  direct-instance-of($T, $TT)?
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
    errors += noresults(qp, """
select $NT from
  instance-of($T, $TT),
  topic-name($T, $TN),
  type($TN, $NT),
  not(instance-of($TNC, $TTNC),
      subject-identifier($TTNC, "http://psi.topicmaps.org/tmcl/topic-name-constraint"),
      constrained-tt($TNC, $TT),
      constrained-s($TNC, $ST))?
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
    errors += noresults(qp, """
select $T from
  instance-of($T, $TT),
  topic-name($T, $TN),
  type($TN, $NT),
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
    errors += noresults(qp, """
select $OT from
  instance-of($T, $TT),
  occurrence($T, $OCC),
  type($OCC, $OT),
  not(instance-of($TOC, $TTOC),
      subject-identifier($TTNC, "http://psi.topicmaps.org/tmcl/topic-occurrence-constraint"),
      constrained-tt($TOC, $TT),
      constrained-s($TOC, $ST))?
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

if __name__ == "__main__":
    topicmap = ImportExportUtils.getReader("seed.ctm").read()
    for file in sys.argv[1 : ]:
        ImportExportUtils.getImporter(file).importInto(topicmap)
    errors = validate(topicmap)
    report(errors)
