
grandchild($GF, $GM, $GC) :-
  parenthood($GF : father, $GM : mother, $C : child),
  parenthood($C : father, $M : mother, $GC : child).

/* we get an error here because of a duplicate rule defn */
grandchild($GF, $GM, $GC) :-
  parenthood($GF : father, $GM : mother, $C : child),
  parenthood($C : father, $M : mother, $GC : child).