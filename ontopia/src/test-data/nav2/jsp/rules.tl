
inspired-by($AUTHOR, $COMPOSER) :-
  composed-by($OPERA : opera, $COMPOSER : composer), 
  based-on($OPERA : result, $WORK : source), 
  written-by($WORK : work, $AUTHOR : writer).
