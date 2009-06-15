
using fam for s"#"

fam:grandchild($GF, $GM, $GC) :-
  parenthood($GF : father, $GM : mother, $C : child),
  parenthood($C : father, $M : mother, $GC : child).