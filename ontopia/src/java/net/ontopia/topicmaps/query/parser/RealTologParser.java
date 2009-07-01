// $ANTLR 2.7.7 (20060906): "tolog.g" -> "RealTologParser.java"$
 package net.ontopia.topicmaps.query.parser; 
import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;

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

/**
 * INTERNAL: Parser for the tolog query language.
 */
public class RealTologParser extends antlr.LLkParser       implements RealTologParserTokenTypes
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

    if (options.getBooleanValue("optimizer.inliner", true))
      optimizer.addOptimizer(new QueryOptimizer.RuleInliner());    
    if (options.getBooleanValue("optimizer.typeconflict", true))
      optimizer.addOptimizer(new QueryOptimizer.TypeConflictResolver());
    if (options.getBooleanValue("optimizer.hierarchy-walker", false))
      optimizer.addOptimizer(new QueryOptimizer.HierarchyWalker());      
    if (options.getBooleanValue("optimizer.recursive-pruner", true))
      optimizer.addOptimizer(new QueryOptimizer.RecursivePruner());

    // disabling this optimizer in rules because the effect is that before
    // we have the rule parameter bindings it just messes up the predicate
    // order. the reorderer running at run-time will then start from the
    // wrong order and make a real mess of things. better to leave the order
    // untouched so that the run-time optimizer produces better results.
    if (options.getBooleanValue("optimizer.reorder", false)) {
      boolean newapproach =
        options.getBooleanValue("optimizer.reorder.predicate-based", true);
      optimizer.addOptimizer(new QueryOptimizer.Reorderer(newapproach)); 
    }   
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

protected RealTologParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public RealTologParser(TokenBuffer tokenBuf) {
  this(tokenBuf,8);
}

protected RealTologParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public RealTologParser(TokenStream lexer) {
  this(lexer,8);
}

public RealTologParser(ParserSharedInputState state) {
  super(state,8);
  tokenNames = _tokenNames;
}

	public final void query() throws RecognitionException, TokenStreamException {
		
		
		{
		_loop3:
		do {
			switch ( LA(1)) {
			case USING:
			{
				prefixdecl();
				break;
			}
			case IMPORT:
			{
				importdecl();
				break;
			}
			default:
			{
				break _loop3;
			}
			}
		} while (true);
		}
		{
		_loop9:
		do {
			boolean synPredMatched8 = false;
			if (((LA(1)==IDENT) && (LA(2)==LPAREN) && (LA(3)==VARIABLE) && (LA(4)==COMMA||LA(4)==RPAREN) && (LA(5)==VARIABLE||LA(5)==CONNECT) && (_tokenSet_0.member(LA(6))) && (_tokenSet_1.member(LA(7))) && (_tokenSet_2.member(LA(8))))) {
				int _m8 = mark();
				synPredMatched8 = true;
				inputState.guessing++;
				try {
					{
					match(IDENT);
					match(LPAREN);
					match(VARIABLE);
					{
					_loop7:
					do {
						if ((LA(1)==COMMA)) {
							match(COMMA);
							match(VARIABLE);
						}
						else {
							break _loop7;
						}
						
					} while (true);
					}
					match(RPAREN);
					match(CONNECT);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched8 = false;
				}
				rewind(_m8);
inputState.guessing--;
			}
			if ( synPredMatched8 ) {
				rule();
			}
			else {
				break _loop9;
			}
			
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			
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
		{
		switch ( LA(1)) {
		case SELECT:
		{
			match(SELECT);
			selectlist();
			match(FROM);
			break;
		}
		case IDENT:
		case VARIABLE:
		case INDICATOR:
		case SOURCELOC:
		case ADDRESS:
		case STRING:
		case NUMBER:
		case LCURLY:
		case NOT:
		case PARAMETER:
		case OBJID:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		clauselist();
		if ( inputState.guessing==0 ) {
			query.setClauseList(prevClauseList);
		}
		{
		switch ( LA(1)) {
		case ORDER:
		{
			order();
			break;
		}
		case QUESTIONM:
		case LIMIT:
		case OFFSET:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		switch ( LA(1)) {
		case LIMIT:
		{
			limit();
			break;
		}
		case QUESTIONM:
		case OFFSET:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		switch ( LA(1)) {
		case OFFSET:
		{
			offset();
			break;
		}
		case QUESTIONM:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(QUESTIONM);
		if ( inputState.guessing==0 ) {
			
			try {
			query.close();
			}
			catch (InvalidQueryException e) {
			throw new AntlrWrapException(e);
			}
			
		}
	}
	
	public final void prefixdecl() throws RecognitionException, TokenStreamException {
		
		
		match(USING);
		match(IDENT);
		if ( inputState.guessing==0 ) {
			notQNameHere(); prevIdent = LT(0).getText();
		}
		match(FOR);
		{
		switch ( LA(1)) {
		case INDICATOR:
		{
			match(INDICATOR);
			if ( inputState.guessing==0 ) {
				prefixType = ParseContextIF.SUBJECT_IDENTIFIER;
			}
			break;
		}
		case SOURCELOC:
		{
			match(SOURCELOC);
			if ( inputState.guessing==0 ) {
				prefixType = ParseContextIF.ITEM_IDENTIFIER;
			}
			break;
		}
		case ADDRESS:
		{
			match(ADDRESS);
			if ( inputState.guessing==0 ) {
				prefixType = ParseContextIF.SUBJECT_LOCATOR;
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			context.addPrefixBinding(prevIdent, LT(0).getText(), prefixType);
		}
	}
	
	public final void importdecl() throws RecognitionException, TokenStreamException {
		
		
		match(IMPORT);
		match(STRING);
		if ( inputState.guessing==0 ) {
			prevValue = LT(0).getText();
		}
		match(AS);
		match(IDENT);
		if ( inputState.guessing==0 ) {
			notQNameHere();
			context.addPrefixBinding(LT(0).getText(), (String) prevValue,
			ParseContextIF.MODULE);
		}
	}
	
	public final void rule() throws RecognitionException, TokenStreamException {
		
		
		match(IDENT);
		if ( inputState.guessing==0 ) {
			notQNameHere();     
			rule = getRule(LT(0).getText());
			rule.init(query);
			context.addPredicate(context.getPredicate(rule));
		}
		match(LPAREN);
		paramlist();
		match(RPAREN);
		match(CONNECT);
		clauselist();
		match(PERIOD);
		if ( inputState.guessing==0 ) {
			
			rule.setClauseList(prevClauseList);
			
		}
	}
	
	public final void selectlist() throws RecognitionException, TokenStreamException {
		
		
		{
		switch ( LA(1)) {
		case VARIABLE:
		{
			match(VARIABLE);
			if ( inputState.guessing==0 ) {
				query.addVariable(new Variable(LT(0).getText()));
			}
			break;
		}
		case COUNT:
		{
			{
			match(COUNT);
			match(LPAREN);
			match(VARIABLE);
			if ( inputState.guessing==0 ) {
				query.addCountVariable(new Variable(LT(0).getText()));
			}
			match(RPAREN);
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		_loop40:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				{
				switch ( LA(1)) {
				case VARIABLE:
				{
					match(VARIABLE);
					if ( inputState.guessing==0 ) {
						query.addVariable(new Variable(LT(0).getText()));
					}
					break;
				}
				case COUNT:
				{
					{
					match(COUNT);
					match(LPAREN);
					match(VARIABLE);
					if ( inputState.guessing==0 ) {
						query.addCountVariable(new Variable(LT(0).getText()));
					}
					match(RPAREN);
					}
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
			}
			else {
				break _loop40;
			}
			
		} while (true);
		}
	}
	
	public final void clauselist() throws RecognitionException, TokenStreamException {
		
		
		if ( inputState.guessing==0 ) {
			openLists.push(clauselist);
			clauselist = new ArrayList();
		}
		clause();
		if ( inputState.guessing==0 ) {
			clauselist.add(prevClause);
		}
		{
		_loop24:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				clause();
				if ( inputState.guessing==0 ) {
					clauselist.add(prevClause);
				}
			}
			else {
				break _loop24;
			}
			
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			prevClauseList = clauselist;
			clauselist = (List) openLists.pop();
		}
	}
	
	public final void order() throws RecognitionException, TokenStreamException {
		
		
		match(ORDER);
		match(BY);
		orderpart();
		{
		_loop43:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				orderpart();
			}
			else {
				break _loop43;
			}
			
		} while (true);
		}
	}
	
	public final void limit() throws RecognitionException, TokenStreamException {
		
		
		match(LIMIT);
		match(NUMBER);
		if ( inputState.guessing==0 ) {
			isIntegerHere();
		}
		if ( inputState.guessing==0 ) {
			query.setLimit(Integer.parseInt(LT(0).getText()));
		}
	}
	
	public final void offset() throws RecognitionException, TokenStreamException {
		
		
		match(OFFSET);
		match(NUMBER);
		if ( inputState.guessing==0 ) {
			isIntegerHere();
		}
		if ( inputState.guessing==0 ) {
			try {
			query.setOffset(Integer.parseInt(LT(0).getText()));
			} catch (InvalidQueryException e) {
			throw new AntlrWrapException(e);
			}
			
		}
	}
	
	public final void declarations() throws RecognitionException, TokenStreamException {
		
		
		{
		_loop16:
		do {
			switch ( LA(1)) {
			case USING:
			{
				prefixdecl();
				break;
			}
			case IMPORT:
			{
				importdecl();
				break;
			}
			default:
			{
				break _loop16;
			}
			}
		} while (true);
		}
		{
		_loop18:
		do {
			if ((LA(1)==IDENT)) {
				rule();
			}
			else {
				break _loop18;
			}
			
		} while (true);
		}
	}
	
	public final void clause() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case LCURLY:
		{
			if ( inputState.guessing==0 ) {
				orClauses.push(new OrClause());
			}
			match(LCURLY);
			clauselist();
			if ( inputState.guessing==0 ) {
				((OrClause) orClauses.peek()).addClauseList(prevClauseList);
			}
			{
			switch ( LA(1)) {
			case DOUBLEPIPE:
			{
				{
				int _cnt55=0;
				_loop55:
				do {
					if ((LA(1)==DOUBLEPIPE)) {
						match(DOUBLEPIPE);
						clauselist();
						if ( inputState.guessing==0 ) {
							((OrClause) orClauses.peek()).setShortCircuit(true);
							((OrClause) orClauses.peek()).addClauseList(prevClauseList);
						}
					}
					else {
						if ( _cnt55>=1 ) { break _loop55; } else {throw new NoViableAltException(LT(1), getFilename());}
					}
					
					_cnt55++;
				} while (true);
				}
				break;
			}
			case PIPE:
			{
				{
				int _cnt57=0;
				_loop57:
				do {
					if ((LA(1)==PIPE)) {
						match(PIPE);
						clauselist();
						if ( inputState.guessing==0 ) {
							((OrClause) orClauses.peek()).addClauseList(prevClauseList);
						}
					}
					else {
						if ( _cnt57>=1 ) { break _loop57; } else {throw new NoViableAltException(LT(1), getFilename());}
					}
					
					_cnt57++;
				} while (true);
				}
				break;
			}
			case RCURLY:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(RCURLY);
			if ( inputState.guessing==0 ) {
				prevClause = (OrClause) orClauses.peek();
				orClauses.pop();
			}
			break;
		}
		case NOT:
		{
			match(NOT);
			if ( inputState.guessing==0 ) {
				notClauses.push(new NotClause());
			}
			match(LPAREN);
			clauselist();
			if ( inputState.guessing==0 ) {
				((NotClause) notClauses.peek()).setClauseList(prevClauseList);
			}
			match(RPAREN);
			if ( inputState.guessing==0 ) {
				prevClause = (NotClause) notClauses.peek();
				notClauses.pop();
			}
			break;
		}
		default:
			if ((_tokenSet_3.member(LA(1))) && (LA(2)==LPAREN)) {
				{
				predicateref();
				if ( inputState.guessing==0 ) {
					clause = new PredicateClause(); // need to see args 1st
					predicate = prevValue;
				}
				match(LPAREN);
				valueorpair();
				if ( inputState.guessing==0 ) {
					clause.addArgument(prevValue);
				}
				{
				_loop51:
				do {
					if ((LA(1)==COMMA)) {
						match(COMMA);
						valueorpair();
						if ( inputState.guessing==0 ) {
							clause.addArgument(prevValue);
						}
					}
					else {
						break _loop51;
					}
					
				} while (true);
				}
				match(RPAREN);
				}
				if ( inputState.guessing==0 ) {
					clause.setPredicate(getPredicate(predicate, clause));
					prevClause = clause;
				}
			}
			else if ((_tokenSet_4.member(LA(1))) && ((LA(2) >= NOTEQUALS && LA(2) <= GREATERTHANEQ))) {
				{
				value();
				if ( inputState.guessing==0 ) {
					prevValue2 = prevValue;
				}
				comparator();
				if ( inputState.guessing==0 ) {
					comp = LT(0).getText();
				}
				value();
				if ( inputState.guessing==0 ) {
					clause = new PredicateClause(context.getPredicate(new QName(comp), false));
					// doesn't matter whether assoc true or not here, as it'll never be used
					clause.addArgument(prevValue2);
					clause.addArgument(prevValue);
					prevClause = clause;
				}
				}
			}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void ruleset() throws RecognitionException, TokenStreamException {
		
		
		{
		_loop27:
		do {
			switch ( LA(1)) {
			case USING:
			{
				prefixdecl();
				break;
			}
			case IMPORT:
			{
				importdecl();
				break;
			}
			default:
			{
				break _loop27;
			}
			}
		} while (true);
		}
		{
		_loop29:
		do {
			if ((LA(1)==IDENT)) {
				rule();
			}
			else {
				break _loop29;
			}
			
		} while (true);
		}
	}
	
	public final void paramlist() throws RecognitionException, TokenStreamException {
		
		
		match(VARIABLE);
		if ( inputState.guessing==0 ) {
			rule.addParameter(new Variable(LT(0).getText()));
		}
		{
		_loop33:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				match(VARIABLE);
				if ( inputState.guessing==0 ) {
					rule.addParameter(new Variable(LT(0).getText()));
				}
			}
			else {
				break _loop33;
			}
			
		} while (true);
		}
	}
	
	public final void orderpart() throws RecognitionException, TokenStreamException {
		
		
		match(VARIABLE);
		if ( inputState.guessing==0 ) {
			var = new Variable(LT(0).getText());
		}
		{
		switch ( LA(1)) {
		case ASC:
		{
			match(ASC);
			break;
		}
		case DESC:
		{
			match(DESC);
			break;
		}
		case COMMA:
		case QUESTIONM:
		case LIMIT:
		case OFFSET:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			query.addOrderBy(var, !LT(0).getText().equalsIgnoreCase("DESC"));
		}
	}
	
	public final void predicateref() throws RecognitionException, TokenStreamException {
		
		
		{
		switch ( LA(1)) {
		case IDENT:
		{
			match(IDENT);
			if ( inputState.guessing==0 ) {
				prevValue = new QName(LT(0).getText());
			}
			break;
		}
		case INDICATOR:
		case SOURCELOC:
		case ADDRESS:
		case OBJID:
		{
			{
			switch ( LA(1)) {
			case INDICATOR:
			{
				match(INDICATOR);
				if ( inputState.guessing==0 ) {
					prevValue = context.getTopicBySubjectIdentifier(LT(0).getText());
				}
				break;
			}
			case ADDRESS:
			{
				match(ADDRESS);
				if ( inputState.guessing==0 ) {
					prevValue = context.getTopicBySubjectLocator(LT(0).getText());
				}
				break;
			}
			case SOURCELOC:
			{
				match(SOURCELOC);
				if ( inputState.guessing==0 ) {
					prevValue = context.getObjectByItemId(LT(0).getText());
				}
				break;
			}
			case OBJID:
			{
				match(OBJID);
				if ( inputState.guessing==0 ) {
					prevValue = context.getObjectByObjectId(LT(0).getText());
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
	}
	
	public final void valueorpair() throws RecognitionException, TokenStreamException {
		
		
		value();
		if ( inputState.guessing==0 ) {
			prevValue2 = prevValue;
		}
		{
		switch ( LA(1)) {
		case COLON:
		{
			match(COLON);
			topicref();
			if ( inputState.guessing==0 ) {
				prevValue = new Pair(prevValue2, prevValue);
			}
			break;
		}
		case COMMA:
		case RPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
	}
	
	public final void value() throws RecognitionException, TokenStreamException {
		
		
		{
		switch ( LA(1)) {
		case VARIABLE:
		{
			match(VARIABLE);
			if ( inputState.guessing==0 ) {
				prevValue = new Variable(LT(0).getText());
			}
			break;
		}
		case IDENT:
		case INDICATOR:
		case SOURCELOC:
		case ADDRESS:
		case OBJID:
		{
			topicref();
			break;
		}
		case STRING:
		{
			match(STRING);
			if ( inputState.guessing==0 ) {
				prevValue = LT(0).getText();
			}
			break;
		}
		case NUMBER:
		{
			match(NUMBER);
			if ( inputState.guessing==0 ) {
				prevValue = parseNumber(LT(0).getText());
			}
			break;
		}
		case PARAMETER:
		{
			match(PARAMETER);
			if ( inputState.guessing==0 ) {
				prevValue = new Parameter(LT(0).getText());
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
	}
	
	public final void comparator() throws RecognitionException, TokenStreamException {
		
		
		{
		switch ( LA(1)) {
		case NOTEQUALS:
		{
			match(NOTEQUALS);
			break;
		}
		case EQUALS:
		{
			match(EQUALS);
			break;
		}
		case LESSTHAN:
		{
			match(LESSTHAN);
			break;
		}
		case GREATERTHAN:
		{
			match(GREATERTHAN);
			break;
		}
		case LESSTHANEQ:
		{
			match(LESSTHANEQ);
			break;
		}
		case GREATERTHANEQ:
		{
			match(GREATERTHANEQ);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
	}
	
	public final void topicref() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case IDENT:
		{
			match(IDENT);
			if ( inputState.guessing==0 ) {
				prevValue = context.getObject(new QName(LT(0).getText()));
			}
			break;
		}
		case INDICATOR:
		{
			match(INDICATOR);
			if ( inputState.guessing==0 ) {
				prevValue = context.getTopicBySubjectIdentifier(LT(0).getText());
			}
			break;
		}
		case ADDRESS:
		{
			match(ADDRESS);
			if ( inputState.guessing==0 ) {
				prevValue = context.getTopicBySubjectLocator(LT(0).getText());
			}
			break;
		}
		case SOURCELOC:
		{
			match(SOURCELOC);
			if ( inputState.guessing==0 ) {
				prevValue = context.getObjectByItemId(LT(0).getText());
			}
			break;
		}
		case OBJID:
		{
			match(OBJID);
			if ( inputState.guessing==0 ) {
				prevValue = context.getObjectByObjectId(LT(0).getText());
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"IDENT",
		"(",
		"VARIABLE",
		",",
		")",
		":-",
		"\"select\"",
		"\"from\"",
		"?",
		"\"using\"",
		"\"for\"",
		"INDICATOR",
		"SOURCELOC",
		"ADDRESS",
		"\"import\"",
		"STRING",
		"\"as\"",
		")",
		"\"count\"",
		"\"order\"",
		"\"by\"",
		"\"asc\"",
		"\"desc\"",
		"\"limit\"",
		"NUMBER",
		"\"offset\"",
		"{",
		"||",
		"|",
		"}",
		"\"not\"",
		"/=",
		"=",
		"<",
		">",
		"<=",
		">=",
		":",
		"PARAMETER",
		"OBJID",
		"WS",
		"COMMENT",
		"INCOMMENT"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 13212662333904L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 15377325851248L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 15377325851120L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 8796093251600L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 13194408722512L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	
	}
