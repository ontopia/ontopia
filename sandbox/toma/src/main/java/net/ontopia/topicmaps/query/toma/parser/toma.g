
header { package net.ontopia.topicmaps.query.toma.parser; }

{
  import net.ontopia.topicmaps.query.toma.parser.ast.*;
}

/**
 * INTERNAL: Parser for the toma query language.
 */
class RealTomaParser extends Parser;

options {
  defaultErrorHandler = false;
  k = 2;
}

{
  private LocalParseContext context;

  public void init(LocalParseContext c) {
  	this.context = c;
  }
   
  /**
   * Tries to convert a string into an integer and throws an AntlrWrapException
   * if it does not work.
   */ 
  protected int getInt(String s) throws AntlrWrapException {
  	try {
	  return Integer.parseInt(s);
  	} catch (NumberFormatException e) {
  	  throw new AntlrWrapException(e);
  	}
  }
  
  /**
   * Create a path expression that is started by a variable
   */
  protected PathExpressionIF createVariable(String varName) throws AntlrWrapException {
  	PathRootIF root = context.createVariable(varName);
  	PathExpressionIF pe = context.createPathExpression();
  	pe.setRoot(root);
  	return pe;
  }
  
  /**
   * Create an association path expression (i.e. without left side input)
   */
  protected PathExpressionIF createAssociationExpression() throws AntlrWrapException {
    PathExpressionIF path = context.createPathExpression();
  	path.setRoot(context.createEmptyRoot());
  	return path;
  }

}

// the TOMA grammar

query returns [TomaQuery q]:
  q=statement   { q.validate(); }
  SEMICOLON;

statement returns [TomaQuery q]:
                { q = new TomaQuery(); SelectStatement stmt;     }
  stmt=select   { q.addStatement(stmt);                          }
  (             { SelectStatement.UNION_TYPE type;               }
    ( UNION     { type = SelectStatement.UNION_TYPE.UNION;       } 
    | INTERSECT { type = SelectStatement.UNION_TYPE.INTERSECT;   }
    | EXCEPT    { type = SelectStatement.UNION_TYPE.EXCEPT;      }
    )
    (ALL        { type = SelectStatement.UNION_TYPE.UNIONALL;    })? 
    stmt=select { stmt.setUnionType(type); q.addStatement(stmt); }
  )*
  (order[q])?
  (limit[q])?
  (offset[q])?;

select returns [SelectStatement stmt]:
                     { stmt = new SelectStatement(); }
  SELECT 
  ( ALL              { stmt.setDistinct(false);      } 
  | DISTINCT         { stmt.setDistinct(true);       }
  )?
  selectlist[stmt]
  (                  { ExpressionIF e;               } 
    WHERE e=orclause { stmt.setClause(e);            }
  );

selectlist [SelectStatement stmt]:
                 { ExpressionIF e;    }
  e=expr         { stmt.addSelect(e); }
  (
    COMMA e=expr { stmt.addSelect(e); }
  )*;
  
orclause returns [ExpressionIF e]:
                       { ExpressionIF left, right;                                }
  e=andclause 
  (
    OR right=andclause { left=e; e = context.createExpression("OR", left, right); }
  )*;

andclause returns [ExpressionIF e]:
                      { ExpressionIF left, right;                                 }
  e=clause 
  (
    AND right=clause  { left=e; e = context.createExpression("AND", left, right); }
  )*;
  
clause returns [ExpressionIF e]:
                               { ExpressionIF left, right; String exprID;         }
  (
                               { exprID = "EXISTS";                               } 
    (NOT                       { exprID = "NOTEXISTS";                            })? 
     EXISTS left=expr           
                               { e = context.createExpression(exprID, left);      }
  |                            
    left=expr                               
    ( 
      e=comparator right=expr 
                               { e.addChild(left); e.addChild(right);             }
    |                          { exprID = "NOTEXISTS";                            } 
      IS (NOT                  { exprID = "EXISTS";                               })? 
      NULL                     { e = context.createExpression(exprID, left);      }
    | IN LPAREN                { e = context.createExpression("IN");
    	                         e.addChild(left);                                }
      ( right=statement        { e.addChild(right);                               }
      | right=expr             { e.addChild(right);                               } 
        (
         COMMA right=expr      { e.addChild(right);                               }
        )*
      )? RPAREN
    )
  );
  
expr returns [ExpressionIF p]:
               { ExpressionIF left, right;                                  }
  p=atomexpr
  (DOUBLEPIPE right=atomexpr 
               { left = p; p = context.createExpression("||", left, right); })*;   
  
atomexpr returns [ExpressionIF p]:
  ( p=topicpathexpr
  |                         { p = createAssociationExpression();       }
    assocpathexpr[(PathExpressionIF) p, null]
  | s:STRING                { p = context.createLiteral(s.getText());  }
  |                         { FunctionIF f; ExpressionIF left;         } 
    f=function              { p = f;                                   }
      LPAREN
      left=atomexpr         { p.addChild(left);                        }
      (
       COMMA functionparam  { f.addParam(LT(0).getText());             }
      )* 
      RPAREN
  );
  
topicpathexpr returns [PathExpressionIF p]:
  p=pathexpr
  (DOT assocleftside[p])?;
  
assocpathexpr [PathExpressionIF p, PathExpressionIF left]:
                               { PathElementIF pe = context.createElement("ASSOC");
                               	 if (left != null) pe.addChild(left);
                               	 p.addPath(pe);
                               	 PathExpressionIF type, scope, right;               } 
  (assocVar:VARIABLE           { pe.bindVariable(
    	                           context.createVariable(assocVar.getText()));     })?
  LPAREN type=pathexpr RPAREN  { pe.setType(type);                                  }                                     
  (ATSCOPE scope=pathexpr      { pe.setScope(scope);                                })?
  RARROW 
  LPAREN right=roleexpr RPAREN { pe.addChild(right);                                }
  // TODO: add support for variable binding in association roles
  (LSQUARE
   roleVar:VARIABLE
   RSQUARE
  )?
  (                            { PathElementIF part;                                }
    DOT part=pathpart          { p.addPath(part);                                   }
  )*
  (DOT assocleftside[p])?;

assocleftside [PathExpressionIF p]:
  { PathExpressionIF left; }
  LPAREN left=roleexpr RPAREN LARROW assocpathexpr[p, left];

roleexpr returns [PathExpressionIF p]:
  (
    ANONYM              { p = context.createPathExpression();
    	                  p.setRoot(context.createAnyRoot());                  }
  | p=pathexpr
  );
  
pathexpr returns [PathExpressionIF path]:
                        { path = context.createPathExpression();               }
  ( 
                        { PathRootIF topic;                                    } 
    topic=topicliteral  { path.setRoot(topic);                                 } 
  | var:VARIABLE        { path.setRoot(context.createVariable(var.getText())); }
  )
  (                     { PathElementIF pe;                                    }
   DOT pe=pathpart      { path.addPath(pe);                                    }
  )*;
  
pathpart returns [PathElementIF p]:
  ( ID | SI | SL | NAME | VAR | OC | REF | DATA | SC | PLAYER | 
    ROLE | REIFIER | TYPE | INSTANCE | SUB | SUPER )
  { p = context.createElement(LT(0).getText()); }
  ( LPAREN
    (                { PathExpressionIF type; } 
      type=pathexpr  { p.setType(type);       }
    |                { Level l;               }           
      l=level        { p.setLevel(l);         }
    )
    RPAREN
  )?
  ( ATSCOPE 
                                { PathExpressionIF pe; PathRootIF r;         }
    ( var:VARIABLE              { pe = createVariable(var.getText());        }
    | r=topicliteral            { pe = context.createPathExpression();       } 
    	                        { pe.setRoot(r);                             }
    | LPAREN pe=pathexpr RPAREN 
    )                           { p.setScope(pe);                            }
  )?
  ( LSQUARE
    bound:VARIABLE              { p.bindVariable(                            }
    	                        {  context.createVariable(bound.getText())); }
    RSQUARE
  )?; 
 
order [TomaQuery q]:  
  ORDER BY orderpart[q] (COMMA orderpart[q])*;

orderpart [TomaQuery q]:
          { QueryOrder.SORT_ORDER order = QueryOrder.SORT_ORDER.ASC; }
  col:INT
  ( ASC   { order = QueryOrder.SORT_ORDER.ASC;                       }
  | DESC  { order = QueryOrder.SORT_ORDER.DESC;                      }
  )?
          { q.addOrderBy(getInt(col.getText()), order);              };

limit [TomaQuery q]: 
  LIMIT i:INT   { q.setLimit(getInt(i.getText()));  };
  
offset [TomaQuery q]: 
  OFFSET o:INT  { q.setOffset(getInt(o.getText())); };

level returns [Level l]:
                { int start = 0; int end = 0;          }
  ( i:INT       { start = end = getInt(i.getText());   }
  | ASTERISK    { start = 0; end = Integer.MAX_VALUE;  }
  | s:INT RANGE { start = getInt(s.getText());         }
    ( e:INT     { end = getInt(e.getText());           } 
    | ASTERISK  { end = Integer.MAX_VALUE;             }
    )
  )             { l = new Level(start, end);           };
  
topicliteral returns [PathRootIF root]:
                { String type;                                       }
  ( ITEMID      { type = "IID";                                      }
  | NAMELITERAL { type = "NAME";                                     }
  | VARLITERAL  { type = "VAR";                                      }
  | SUBJID      { type = "SUBJID";                                   }
  | SUBJLOC     { type = "SUBJLOC";                                  }
  | IDENTIFIER  { type = "IID";                                      }
  )             { root = context.createTopic(type, LT(0).getText()); };
  
comparator returns [ExpressionIF e]:
  ( NOTEQUALS | EQUALS | LESSTHAN | GREATERTHAN | LESSTHANEQ     
  | GREATERTHANEQ | REGEXPCS | REGEXPCI | REGEXPNCS | REGEXPNCI)
  { e = context.createExpression(LT(0).getText()); };
      
function returns [FunctionIF f]:
  ( f=aggregate_function
  | f=simple_function
  );      
  
aggregate_function returns [FunctionIF f]:
  ( COUNT | SUM | MAX | MIN | AVG | CONCAT)
  { f = context.createFunction(LT(0).getText()); };
  
simple_function returns [FunctionIF f]:
  ( LOWERCASE | UPPERCASE | TITLECASE | LENGTH | SUBSTR | TRIM | TO_NUM)
  { f = context.createFunction(LT(0).getText()); };

functionparam:
  ( INT
  | IDENTIFIER
  | STRING
  ); 
  
/**
 * INTERNAL: Lexer for TOMA query language.
 */
class TomaLexer extends Lexer;

options { 
  // can't include U+FFFF in the vocabulary, because antlr 2.7.1 uses it
  // to represent EOF...
  charVocabulary = '\1'..'\uFFFE';
  caseSensitive = true;
  caseSensitiveLiterals = false;
  testLiterals = false;
  k = 4;
}

tokens {
  SELECT   = "select";
  WHERE    = "where";
  ORDER    = "order";
  BY       = "by";
  NOT      = "not";
  AND      = "and";
  OR       = "or";
  EXISTS   = "exists";
  IN       = "in";
  IS       = "is";
  NULL     = "null";
  DESC     = "desc";
  ASC      = "asc";
  LIMIT    = "limit";
  OFFSET   = "offset";
  ID       = "id";
  SI       = "si";
  SL       = "sl";
  NAME     = "name";
  VAR      = "var";
  OC       = "oc";
  REF      = "ref";
  DATA     = "data";
  SC       = "sc";
  PLAYER   = "player";
  ROLE     = "role";
  REIFIER  = "reifier";
  TYPE     = "type";
  INSTANCE = "instance";
  SUPER    = "super";
  SUB      = "sub";
  UNION    = "union";
  INTERSECT = "intersect";
  EXCEPT   = "except";
  ALL      = "all";
  DISTINCT = "distinct";
  
  // AGGREGATE FUNCTIONS
  COUNT     = "count";
  SUM       = "sum";
  MAX       = "max";
  MIN       = "min";
  AVG       = "avg";
  CONCAT    = "concat";
  
  // STRING FUNCTIONS
  LOWERCASE = "lowercase";
  UPPERCASE = "uppercase";
  TITLECASE = "titlecase";
  LENGTH    = "length";
  SUBSTR    = "substr";
  TRIM      = "trim";
  
  // CONVERSION FUNCTIONS
  TO_NUM    = "to_num";
}

IDENTIFIER options { testLiterals = true; }:
  ('A'..'Z' | 'a'..'z') 
  ('A'..'Z' | 'a'..'z' | '0'..'9' | '_' | '-')*
  (
    { !(LA(2) == ' ' || LA(2) == '\t' || LA(2) == '\n' || LA(2) == '\r') }?
    (':' ('A'..'Z' | 'a'..'z' | '0'..'9' | '_' | '-')+)?
  )?;

WS:
  (' ' |	'\t' | '\n'  { newline(); } | '\r')
  { $setType(Token.SKIP); };

ITEMID: 'i' STRING;
NAMELITERAL: 'n' STRING;
VARLITERAL: 'v' STRING;
SUBJID: "si" STRING;
SUBJLOC: "sl" STRING;
 
STRING: 
  '\'' 
  (
    options { generateAmbigWarnings=false; }: 
    ~('\'' | '\n') 
  | '\n' { newline(); } 
  | { LA(1)=='\\' }? '\''
  )* 
  '\''
  { setText(new String(text.getBuffer(), _begin+1, (text.length()-_begin)-2)); };

COMMENT:
  "#" (~'\n')* '\n'
  { $setType(Token.SKIP); newline(); };

VARIABLE:
  '$'
  ('A'..'Z' | 'a'..'z' | '_') 
  ('A'..'Z' | 'a'..'z' | '0'..'9' | '_' )*
  { setText(new String(text.getBuffer(), _begin+1, (text.length()-_begin)-1)); };

INT: ('0'..'9')+;
 
NUMBER: ('0'..'9')+ ( '.' ('0'..'9')+ )?;

ASTERISK   options { paraphrase = "*";  } : '*'  ;
COLON      options { paraphrase = ":";  } : ':'  ;
SEMICOLON  options { paraphrase = ":";  } : ';'  ;
QUESTIONM  options { paraphrase = "?";  } : '?'  ;
SLASH      options { paraphrase = "/";  } : '/'  ;
ATSCOPE    options { paraphrase = "@";  } : '@'  ;
COMMA  	   options { paraphrase = ",";  } : ','  ;
LPAREN 	   options { paraphrase = "(";  } : '('  ;
RPAREN 	   options { paraphrase = ")";  } : ')'  ;
LSQUARE	   options { paraphrase = "[";  } : '['  ;
RSQUARE	   options { paraphrase = "]";  } : ']'  ;
DOT        options { paraphrase = ".";  } : '.'  ;
RARROW     options { paraphrase = "->"; } : "->" ;
LARROW     options { paraphrase = "<-"; } : "<-" ;
ANONYM     options { paraphrase = "$$"; } : "$$" ;
LCURLY     options { paraphrase = "{";  } : '{'  ;
RCURLY     options { paraphrase = "}";  } : '}'  ;
PIPE       options { paraphrase = "|";  } : '|'  ;
DOUBLEPIPE options { paraphrase = "||";  } : "||"  ;
RANGE      options { paraphrase = "..";  } : ".."  ;

NOTEQUALS     options { paraphrase = "!="; } : "!=" ;
EQUALS        options { paraphrase = "="; }  : "="  ;
LESSTHAN      options { paraphrase = "<"; }  : "<"  ;
GREATERTHAN   options { paraphrase = ">"; }  : ">"  ;
LESSTHANEQ    options { paraphrase = "<="; } : "<=" ;
GREATERTHANEQ options { paraphrase = ">="; } : ">=" ;
REGEXPCS      options { paraphrase = "~"; }  : '~' ;
REGEXPCI      options { paraphrase = "~*"; } : "~*" ;
REGEXPNCS     options { paraphrase = "!~"; } : "!~" ;
REGEXPNCI     options { paraphrase = "!~*"; }: "!~*" ;
