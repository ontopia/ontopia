
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
  private TologStatement statement;
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
    openLists = new Stack();
    orClauses = new Stack();
    notClauses = new Stack();
  }

  public void setContext(ParseContextIF context) {
    this.context = context;
  }
  
  public TologStatement getStatement() {
    return statement;
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
    TologOptions options = lexer.getOptions();
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

  /**
   * Processing of rules once they are all parsed. Verifies that all
   * rules we have seen references to are actually defined, passes the
   * close() event to rules, and then optimizes them.
   */
  private void optimizeRules() throws AntlrWrapException {
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
      ParsedRule prule = (ParsedRule) riter.next();
      try {
        prule.close();
        prule = optimizer.optimize(prule);
      } catch (InvalidQueryException e) {
        throw new AntlrWrapException(e);
      }
    }
    rules.clear();
  }
}

// the grammar

query: // entry point for SELECT queries
  {
    statement = new TologQuery();
    ((TologQuery) statement).setOptions(lexer.getOptions());
  }
  (prefixdecl | importdecl)*
  (// we solve #1143 by using what antlr calls a "syntactic
   // predicate" to verify that this really is a rule
   (IDENT LPAREN VARIABLE (COMMA VARIABLE)* RPAREN CONNECT) => 
   rule)* 
  { 
    optimizeRules();
  }
  
  (SELECT selectlist FROM )?
    clauselist { ((TologQuery) statement).setClauseList(prevClauseList); }
  (order)?
  (limit)?
  (offset)?
  QUESTIONM { 
    try {
      statement.close();
    }
    catch (InvalidQueryException e) {
      throw new AntlrWrapException(e);
    }
  };

// needed because it is referenced from outside; duplicated above in
// order to be able to use a syntactic predicate
declarations:
  (prefixdecl | importdecl)*
  (rule)* 
  {
    optimizeRules();
  }
  ;

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

// invoked from outside when loading rule declarations and modules. never
// called when parsing a query.
ruleset:
  { // we need a query object. this is probably not the best way, though.
    statement = new TologQuery(); }
  (prefixdecl | importdecl)*
  (rule)*;

rule:  
  IDENT
    { notQNameHere();     
      rule = getRule(LT(0).getText());
      rule.init(lexer.getOptions());
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
  (VARIABLE { ((TologQuery) statement).addVariable(new Variable(LT(0).getText())); } |
   (COUNT LPAREN VARIABLE   
    { ((TologQuery) statement).addCountVariable(new Variable(LT(0).getText())); }
    RPAREN))
  (COMMA 
   (VARIABLE { ((TologQuery) statement).addVariable(new Variable(LT(0).getText())); } |
   (COUNT LPAREN VARIABLE   
    { ((TologQuery) statement).addCountVariable(new Variable(LT(0).getText())); }
    RPAREN)))*
  ;

order:
  ORDER BY orderpart
  (COMMA orderpart)*
  ;

orderpart:
  VARIABLE { var = new Variable(LT(0).getText()); }
  (ASC | DESC)?
  { ((TologQuery) statement).addOrderBy(var, !LT(0).getText().equalsIgnoreCase("DESC")); }
  ;

limit:
  LIMIT NUMBER { isIntegerHere(); }
  { ((TologQuery) statement).setLimit(Integer.parseInt(LT(0).getText())); }
  ;

offset:
  OFFSET NUMBER { isIntegerHere(); }
  { try {
      ((TologQuery) statement).setOffset(Integer.parseInt(LT(0).getText()));
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

/// THE UPDATE LANGUAGE

updatestatement: 
  (prefixdecl | importdecl)*
  (delete | merge | update | insert);

delete:
  DELETE 
    { statement = new DeleteStatement();
      statement.setOptions(lexer.getOptions()); }
  (litlist | funccall)
  (FROM clauselist 
    { ((DeleteStatement) statement)
       .setClauseList(prevClauseList, lexer.getOptions()); } 
  )? EOF! { 
    try {
      statement.close();
    }
    catch (InvalidQueryException e) {
      throw new AntlrWrapException(e);
    }
  };

litlist:
  lit (COMMA lit)*;

lit:
  (VARIABLE  { ((ModificationStatement) statement).addLit(new Variable(LT(0).getText())); }
  | PARAMETER { ((ModificationStatement) statement).addLit(new Parameter(LT(0).getText())); }
  | topicref { ((ModificationStatement) statement).addLit(prevValue); }
  );

funccall:
  IDENT 
  { ((ModificationFunctionStatement) statement).setFunction(LT(0).getText()); }
  LPAREN param
  COMMA param  
  RPAREN;

param: 
  lit |
  STRING { ((ModificationStatement) statement).addLit(LT(0).getText()); } ;

merge:
  MERGE
    { statement = new MergeStatement();
      statement.setOptions(lexer.getOptions()); }
  lit COMMA lit
  (FROM clauselist 
    { ((ModificationStatement) statement)
       .setClauseList(prevClauseList, lexer.getOptions()); } 
  )? EOF! { 
    try {
      statement.close();
    }
    catch (InvalidQueryException e) {
      throw new AntlrWrapException(e);
    }
  };

update:
  UPDATE 
    { statement = new UpdateStatement();
      statement.setOptions(lexer.getOptions()); }
  funccall
  (FROM clauselist 
    { ((UpdateStatement) statement)
       .setClauseList(prevClauseList, lexer.getOptions()); } 
  )? EOF! { 
    try {
      statement.close();
    }
    catch (InvalidQueryException e) {
      throw new AntlrWrapException(e);
    }
  };

insert:
  INSERT
    { statement = new InsertStatement();
      statement.setOptions(lexer.getOptions()); }
  (FROM clauselist
    { ((ModificationStatement) statement)
       .setClauseList(prevClauseList, lexer.getOptions()); } 
  )? EOF! { 
    try {
      statement.close();
    }
    catch (InvalidQueryException e) {
      throw new AntlrWrapException(e);
    }
  };
