
// $Id: tolog.g,v 1.48 2009/04/08 11:33:22 geir.gronmo Exp $

header { package net.ontopia.topicmaps.query.parser; }

{
 import java.util.Collection;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Stack;
 import java.util.Map;
 import java.util.HashMap;
 import java.net.MalformedURLException;
 import net.ontopia.utils.StringUtils;
 import net.ontopia.infoset.core.LocatorIF;
 import net.ontopia.infoset.impl.basic.URILocator;
 import net.ontopia.topicmaps.core.*;
 import net.ontopia.topicmaps.query.core.InvalidQueryException;
 import net.ontopia.topicmaps.query.impl.basic.RulePredicate;
 import net.ontopia.topicmaps.query.impl.utils.QueryOptimizer;
}

/**
 * INTERNAL: Parser for the tolog query language.
 */
class RealTologParser extends Parser;

options {
  defaultErrorHandler = false;
  k = 8;
}

{
  /// references
  private TologQuery query;
  private QueryOptimizer optimizer;
  private ParseContextIF context;
  private TologLexer lexer;

  /// state tracking
  private Map rules = new HashMap();
  private ParsedRule rule;
  private Stack openLists;
  private List clauselist;
  private List prevClauseList;
  private Object prevValue;
  private Object prevValue2;
  private String prevIdent;
  private AbstractClause prevClause;
  private PredicateClause clause;
  private int prefixType;
  private Object predicate; // used to delay figuring out what the predicate is

  // these required because of syntactic predicate (YUCK!)
  private Stack orClauses;
  private Stack notClauses;
  private String comp;
  private Variable var;
  
  public void init(TologLexer lexer) {
    this.lexer = lexer;
    query = new TologQuery();
    openLists = new Stack();
    orClauses = new Stack();
    notClauses = new Stack();
    
    // setting it now so we have the options when we parse rules
    query.setOptions(lexer.getOptions());
  }

  public void setContext(ParseContextIF context) {
    this.context = context;
  }
  
  public TologQuery getQuery() {
    return query;
  }

  /// lookup methods

  protected void notQNameHere() throws AntlrWrapException, TokenStreamException {
    if (LT(0).getText().indexOf(':') != -1)
      throw new AntlrWrapException(
              new InvalidQueryException("Qualified names not allowed here"));
  }

  protected void isIntegerHere() throws AntlrWrapException, TokenStreamException {
    try {
      Integer.parseInt(LT(0).getText()); 
    } catch (NumberFormatException e) {
      throw new AntlrWrapException(
              new InvalidQueryException("Non-integers not allowed here."));
    }
  }

  protected Number parseNumber(String number) {
    try {
      return new Integer(Integer.parseInt(number)); 
    } catch (NumberFormatException e) {
      return new Float(Float.parseFloat(number));
    }
  }

  private void configureOptimizer() {
    TologOptions options = query.getOptions();
    optimizer = new QueryOptimizer();

    if (options.getBooleanValue("optimizer.inliner"))
      optimizer.addOptimizer(new QueryOptimizer.RuleInliner());    
    if (options.getBooleanValue("optimizer.typeconflict"))
      optimizer.addOptimizer(new QueryOptimizer.TypeConflictResolver());
    if (options.getBooleanValue("optimizer.hierarchy-walker"))
      optimizer.addOptimizer(new QueryOptimizer.HierarchyWalker());      
    if (options.getBooleanValue("optimizer.recursive-pruner"))
      optimizer.addOptimizer(new QueryOptimizer.RecursivePruner());

    // the reordering optimizer is disabled in rules because the
    // effect is that before we have the rule parameter bindings it
    // just messes up the predicate order. the reorderer running at
    // run-time will then start from the wrong order and make a real
    // mess of things. better to leave the order untouched so that the
    // run-time optimizer produces better results.
  }

  // see comments below
  private boolean isRuleHead() throws TokenStreamException {
    int ix;
    for (ix = 3; ix <= 10; ix++)
      if (LT(ix).getText().equals(")"))
        break;

    if (ix == 11)
      return true; // we don't really know, and so blithely assume

    return LT(ix + 1).getText().equals(":-"); // this works for certain
  }

  private PredicateIF getPredicate(Object predhint, PredicateClause clause)
    throws AntlrWrapException {
    // do we know the predicate already?
    if (predhint instanceof PredicateIF)
      return (PredicateIF) predhint;

    // if not, figure out from arguments in clause
    List args = clause.getArguments();
    if (args.isEmpty())
      throw new AntlrWrapException(new InvalidQueryException("Dynamic predicate " +
        predhint + " must have at least one argument"));
    boolean assoc = args.get(0) instanceof Pair;

    // now find the predicate
    PredicateIF pred;
    if (predhint instanceof TopicIF)
      pred = context.getPredicate((TopicIF) predhint, assoc);
    else
      pred = context.getPredicate((QName) predhint, assoc);

    if (pred == null && rule != null && predhint instanceof QName) {
      ParsedRule deferredRule = getRule(predhint.toString());
      return context.getPredicate(deferredRule);
    }

    if (pred == null)
      throw new AntlrWrapException(
        new InvalidQueryException("No such predicate: " + predhint));

    return pred;
  }

  private ParsedRule getRule(String ruleName) {
    ParsedRule deferredRule = (ParsedRule)rules.get(ruleName);
    if (deferredRule == null) {
      deferredRule = new ParsedRule(ruleName);
      rules.put(ruleName, deferredRule);
    }
    return deferredRule;
  }
}

// the grammar

query:  
  (prefixdecl | importdecl)*
  (// we solve #1143 by using what antlr calls a "syntactic
   // predicate" to verify that this really is a rule
   (IDENT LPAREN VARIABLE (COMMA VARIABLE)* RPAREN CONNECT) => 
   rule)* 
  { 
    if (optimizer == null)
      configureOptimizer();
    // validate rules
    Iterator riter = rules.values().iterator();
    while (riter.hasNext()) {
      ParsedRule prule = (ParsedRule)riter.next();
      if (!prule.initialized()) {
        throw new AntlrWrapException(
                new InvalidQueryException("Unknown predicate: " + prule.getName()));
      }
    }
    // close and optimize rules
    riter = rules.values().iterator();
    while (riter.hasNext()) {
      ParsedRule prule = (ParsedRule)riter.next();
      try {
        prule.close(query);
        prule = optimizer.optimize(prule);
      } catch (InvalidQueryException e) {
        throw new AntlrWrapException(e);
      }
    }
    rules.clear();
  }
  
  (SELECT selectlist FROM )?
    clauselist { query.setClauseList(prevClauseList); }
  (order)?
  (limit)?
  (offset)?
  QUESTIONM { 
    try {
      query.close();
    }
    catch (InvalidQueryException e) {
      throw new AntlrWrapException(e);
    }
  };

// needed because it is referenced from outside; duplicated above in
// order to be able to use a syntactic predicate
declarations:
  (prefixdecl | importdecl)*
  (rule)*;

prefixdecl:
  USING IDENT { notQNameHere(); prevIdent = LT(0).getText(); }
    FOR
    (INDICATOR { prefixType = ParseContextIF.SUBJECT_IDENTIFIER;     } |
     SOURCELOC { prefixType = ParseContextIF.ITEM_IDENTIFIER;        } |
     ADDRESS   { prefixType = ParseContextIF.SUBJECT_LOCATOR;        } )
    
    { context.addPrefixBinding(prevIdent, LT(0).getText(), prefixType); };

importdecl:
  IMPORT STRING { prevValue = LT(0).getText();                                  }
  AS IDENT      { notQNameHere();
                  context.addPrefixBinding(LT(0).getText(), (String) prevValue,
                                           ParseContextIF.MODULE); };

clauselist:
  { openLists.push(clauselist);
    clauselist = new ArrayList(); }
  clause { clauselist.add(prevClause); }
  (COMMA clause { clauselist.add(prevClause); })* 
  { prevClauseList = clauselist;
    clauselist = (List) openLists.pop(); };

ruleset:
  (prefixdecl | importdecl)*
  (rule)*;

rule:  
  IDENT
    { notQNameHere();     
      rule = getRule(LT(0).getText());
      rule.init(query);
      context.addPredicate(context.getPredicate(rule)); }
  LPAREN paramlist RPAREN 
  CONNECT 
  clauselist
  PERIOD
    {
      rule.setClauseList(prevClauseList);
    };

paramlist:
  VARIABLE 
    { rule.addParameter(new Variable(LT(0).getText())); }
  (COMMA VARIABLE
    { rule.addParameter(new Variable(LT(0).getText())); }
  )*;

selectlist:
  (VARIABLE { query.addVariable(new Variable(LT(0).getText())); } |
   (COUNT LPAREN VARIABLE   
    { query.addCountVariable(new Variable(LT(0).getText())); }
    RPAREN))
  (COMMA 
   (VARIABLE { query.addVariable(new Variable(LT(0).getText())); } |
   (COUNT LPAREN VARIABLE   
    { query.addCountVariable(new Variable(LT(0).getText())); }
    RPAREN)))*
  ;

order:
  ORDER BY orderpart
  (COMMA orderpart)*
  ;

orderpart:
  VARIABLE { var = new Variable(LT(0).getText()); }
  (ASC | DESC)?
  { query.addOrderBy(var, !LT(0).getText().equalsIgnoreCase("DESC")); }
  ;

limit:
  LIMIT NUMBER { isIntegerHere(); }
  { query.setLimit(Integer.parseInt(LT(0).getText())); }
  ;

offset:
  OFFSET NUMBER { isIntegerHere(); }
  { try {
      query.setOffset(Integer.parseInt(LT(0).getText()));
    } catch (InvalidQueryException e) {
      throw new AntlrWrapException(e);
    }
  };

clause:
  // ordinary predicate clause
  (predicateref         { clause = new PredicateClause(); // need to see args 1st
                          predicate = prevValue; } // PredicateIF, QName, or topic
   LPAREN valueorpair   { clause.addArgument(prevValue);                         }
   (COMMA valueorpair   { clause.addArgument(prevValue);                         } )*
   RPAREN)              { clause.setPredicate(getPredicate(predicate, clause));
                          prevClause = clause;                                   }

  | // $A /= $B
    ( value { prevValue2 = prevValue; } 
      comparator { comp = LT(0).getText(); }
      value { clause = new PredicateClause(context.getPredicate(new QName(comp), false));
              // doesn't matter whether assoc true or not here, as it'll never be used
              clause.addArgument(prevValue2);
              clause.addArgument(prevValue);
              prevClause = clause; } )
    
  | // or clause
    { orClauses.push(new OrClause());  }
    LCURLY clauselist 
      { ((OrClause) orClauses.peek()).addClauseList(prevClauseList); }
    (( 
      DOUBLEPIPE clauselist 
      { ((OrClause) orClauses.peek()).setShortCircuit(true);
        ((OrClause) orClauses.peek()).addClauseList(prevClauseList); } 
    )+ |
    ( 
      PIPE clauselist 
      { ((OrClause) orClauses.peek()).addClauseList(prevClauseList); } 
    )+)?
    RCURLY
    { prevClause = (OrClause) orClauses.peek();
      orClauses.pop(); }

  | // not clause
    NOT { notClauses.push(new NotClause()); }
    LPAREN clauselist { ((NotClause) notClauses.peek()).setClauseList(prevClauseList); }
    RPAREN { prevClause = (NotClause) notClauses.peek();
             notClauses.pop(); };

comparator:
  ( NOTEQUALS | EQUALS | LESSTHAN | GREATERTHAN | LESSTHANEQ | GREATERTHANEQ);
      
valueorpair:
  value             { prevValue2 = prevValue;                             } 
   (COLON topicref  { prevValue = new Pair(prevValue2, prevValue);        })?;
                                                                          
value:                                                                    
 (VARIABLE          { prevValue = new Variable(LT(0).getText());     } |
  topicref                                                             |
  STRING            { prevValue = LT(0).getText();                   } |
  NUMBER            { prevValue = parseNumber(LT(0).getText());      } |
  PARAMETER         { prevValue = new Parameter(LT(0).getText());    });

topicref:
  IDENT     { prevValue = context.getObject(new QName(LT(0).getText()));       }|
  INDICATOR { prevValue = context.getTopicBySubjectIdentifier(LT(0).getText());}|
  ADDRESS   { prevValue = context.getTopicBySubjectLocator(LT(0).getText());   }|
  SOURCELOC { prevValue = context.getObjectByItemId(LT(0).getText());          }|
  OBJID     { prevValue = context.getObjectByObjectId(LT(0).getText());        };

predicateref:
  (IDENT      { prevValue = new QName(LT(0).getText());                       }|
   (INDICATOR { prevValue = context.getTopicBySubjectIdentifier(LT(0).getText()); }|
    ADDRESS   { prevValue = context.getTopicBySubjectLocator(LT(0).getText()); }|
    SOURCELOC { prevValue = context.getObjectByItemId(LT(0).getText());        }|
    OBJID     { prevValue = context.getObjectByObjectId(LT(0).getText());     }));

/**
 * INTERNAL: Lexer for LTM syntax.
 */

class TologLexer extends Lexer;

options { 
  // can't include U+FFFF in the vocabulary, because antlr 2.7.1 uses it
  // to represent EOF...
  charVocabulary = '\1'..'\uFFFE'; 
  caseSensitive = false;
  caseSensitiveLiterals = false;
  testLiterals = false;
  k = 4;
}

tokens {
  COUNT  = "count";
  ORDER  = "order";
  BY     = "by";
  SELECT = "select";
  FROM   = "from";
  NOT    = "not";
  DESC   = "desc";
  ASC    = "asc";
  LIMIT  = "limit";
  OFFSET = "offset";
  USING  = "using";
  FOR    = "for";
  AS     = "as";
  IMPORT = "import";
}

{
  private TologOptions options;

  public TologLexer(Reader reader, TologOptions options) {
    this(reader);
    this.options = new TologOptions(options);
  }

  public TologOptions getOptions() {
    return options;
  }

  // --- Option parsing
  
  // "#OPTION: name.of.option: value"
  private void parse(String comment) {
    int pos = ignoreWS(comment, 0);
    pos = check(comment, pos, "#OPTION:");
    if (pos == -1)
      return; // syntax error -> no options here

    pos = ignoreWS(comment, pos);
    if (pos == -1)
      return; // syntax error -> no options here

    String name = getName(comment, pos);
    if (name == null)
      return; // syntax error -> no options here
    pos += name.length();

    pos = ignoreWS(comment, pos);
    if (pos == -1)
      return; // syntax error -> no options here

    pos = check(comment, pos, "=");
    if (pos == -1)
      return; // syntax error -> no options here

    pos = ignoreWS(comment, pos);
    if (pos == -1)
      return; // syntax error -> no options here

    String value = getValue(comment, pos);
    if (value == null)
      return; // syntax error -> no options here

    options.setOption(name, value); // there was a value
  }

  private int ignoreWS(String data, int pos) {
    if (pos == data.length())
      return -1;
    
    char ch = ' ';
    while (pos < data.length() &&
           (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r'))
      ch = data.charAt(pos++);
    return pos - 1;
  }

  private int check(String data, int pos, String expected) {
    int start = pos;
    while (pos < data.length() &&
           pos - start < expected.length() &&
           data.charAt(pos) == expected.charAt(pos - start))
      pos++;
    if (pos == start)
      return -1;
    else
      return pos;
  }

  private String getName(String data, int pos) {
    int start = pos;
    char ch = 'a';
    while (pos < data.length() &&
           ((ch >= 'a' && ch <= 'z') ||
            (ch >= 'A' && ch <= 'Z') ||
            (ch >= '0' && ch <= '9') ||
            ch == '.' || ch == '-'))
      ch = data.charAt(pos++);

    if (pos == start)
      return null;
    
    return data.substring(start, pos - 1);
  }
  
  private String getValue(String data, int pos) {
    return getName(data, pos); // FIXME: too primitive, obviously
  }
}

OBJID:
 '@'
 ('A'..'Z' | 'a'..'z' | '0' .. '9')*
 { setText(new String(text.getBuffer(), _begin+1, (text.length()-_begin)-1)); }
 ;

IDENT options { testLiterals = true; }: 
 ('A'..'Z' | 'a'..'z' | '_') 
 ('A'..'Z' | 'a'..'z' | '0'..'9' | '_' | '-')*
 ({ !(LA(2) == ' ' || LA(2) == '\t' || LA(2) == '\n' || LA(2) == '\r') }?
  (':'
   ('A'..'Z' | 'a'..'z' | '0'..'9' | '_' | '-')+)?)?;

WS :
 (' ' |	'\t' | '\n'  { newline(); } | '\r')
 { $setType(Token.SKIP); }
 ;

STRING:
 { boolean quoted = false; }
 '"' (~('"' | '\n') | '\n' { newline(); } | '"' '"' { quoted = true; } )* '"'
 {
   if (quoted)
     setText(net.ontopia.utils.StringUtils.replace(new String(text.getBuffer(), _begin+1, (text.length()-_begin)-2), "\"\"", "\""));
   else
     setText(new String(text.getBuffer(), _begin+1, (text.length()-_begin)-2)); }
 ;

COMMENT : 
 "/*" INCOMMENT "*/" 
 { $setType(Token.SKIP);
   parse(new String(text.getBuffer(), _begin+2, (text.length()-_begin)-3)); }
 ;

INCOMMENT:
 (~('*' | '\n')           |
    { LA(2)!='/' }? '*'   |
      '\n' { newline(); } |
    COMMENT )*;

VARIABLE:
 '$'
 ('A'..'Z' | 'a'..'z' | '_') 
 ('A'..'Z' | 'a'..'z' | '0'..'9' | '_' | '-')*
 ;

PARAMETER :
 '%'
 ('A'..'Z' | 'a'..'z' | '_') 
 ('A'..'Z' | 'a'..'z' | '0'..'9' | '_' | '-')*
 '%'
 { setText(new String(text.getBuffer(), _begin+1, (text.length()-_begin)-2)); }
 ;

INDICATOR :
 'i'
 '"' (~('"' | '\n') | '\n' { newline(); } )* '"'
 { setText(new String(text.getBuffer(), _begin+2, (text.length()-_begin)-3)); }
 ;

ADDRESS :
 'a'
 '"' (~('"' | '\n') | '\n' { newline(); } )* '"'
 { setText(new String(text.getBuffer(), _begin+2, (text.length()-_begin)-3)); }
 ;

SOURCELOC :
 's'
 '"' (~('"' | '\n') | '\n' { newline(); } )* '"'
 { setText(new String(text.getBuffer(), _begin+2, (text.length()-_begin)-3)); }
 ;
  
NUMBER:
 ('0'..'9')+
 ( '.' 
   ('0'..'9')+ )?
 ;


COLON     options { paraphrase = ":";  } : ':'  ;
QUESTIONM options { paraphrase = "?";  } : '?'  ;
COMMA  	  options { paraphrase = ",";  } : ','  ;
LPAREN 	  options { paraphrase = "(";  } : '('  ;
RPAREN 	  options { paraphrase = ")";  } : ')'  ;
PERIOD    options { paraphrase = ")";  } : '.'  ;
CONNECT   options { paraphrase = ":-"; } : ":-" ;
LCURLY    options { paraphrase = "{";  } : '{'  ;
RCURLY    options { paraphrase = "}";  } : '}'  ;
PIPE      options { paraphrase = "|";  } : '|'  ;
DOUBLEPIPE      options { paraphrase = "||";  } : "||"  ;

NOTEQUALS     options { paraphrase = "/="; } : "/=" ;
EQUALS        options { paraphrase = "="; }  : "="  ;
LESSTHAN      options { paraphrase = "<"; }  : "<"  ;
GREATERTHAN   options { paraphrase = ">"; }  : ">"  ;
LESSTHANEQ    options { paraphrase = "<="; } : "<=" ;
GREATERTHANEQ options { paraphrase = ">="; } : ">=" ;

