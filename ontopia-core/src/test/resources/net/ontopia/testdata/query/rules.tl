parent($P, $C, $UNUSED) :-
  { parenthood($P : father, $M : mother, $C : child) |
    parenthood($F : father, $P : mother, $C : child) },
  $UNUSED /= @50.
etterkommer($A, $D, $UNUSED) :-
  { parent($A, $C, $UNUSED), etterkommer($C, $D, $UNUSED) |
    parent($A, $D, $UNUSED) }.
