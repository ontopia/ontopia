// $ANTLR : "toma.g" -> "RealTomaParser.java"$
 package net.ontopia.topicmaps.query.toma.parser; 
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

  import net.ontopia.topicmaps.query.toma.parser.ast.*;

/**
 * INTERNAL: Parser for the toma query language.
 */
public class RealTomaParser extends antlr.LLkParser       implements RealTomaParserTokenTypes
 {

  private LocalParseContext context;

  public void init(LocalParseContext c)
  {
  	this.context = c;
  }
   
  /**
   * Tries to convert a string into an integer and throws an AntlrWrapException
   * if it does not work.
   */ 
  protected int getInt(String s) throws AntlrWrapException
  {
  	try
  	{
	  return Integer.parseInt(s);
  	} catch (NumberFormatException e) {
  	  throw new AntlrWrapException(e);
  	}
  }  

protected RealTomaParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public RealTomaParser(TokenBuffer tokenBuf) {
  this(tokenBuf,4);
}

protected RealTomaParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public RealTomaParser(TokenStream lexer) {
  this(lexer,4);
}

public RealTomaParser(ParserSharedInputState state) {
  super(state,4);
  tokenNames = _tokenNames;
}

	public final TomaQuery  query() throws RecognitionException, TokenStreamException {
		TomaQuery q;
		
		
		q=statement();
		match(SEMICOLON);
		return q;
	}
	
	public final TomaQuery  statement() throws RecognitionException, TokenStreamException {
		TomaQuery q;
		
		
		q = new TomaQuery(); SelectStatement stmt;
		stmt=select();
		q.addStatement(stmt);
		{
		_loop1323:
		do {
			if (((LA(1) >= UNION && LA(1) <= EXCEPT))) {
				SelectStatement.UNION_TYPE type;
				{
				switch ( LA(1)) {
				case UNION:
				{
					match(UNION);
					type = SelectStatement.UNION_TYPE.UNION;
					break;
				}
				case INTERSECT:
				{
					match(INTERSECT);
					type = SelectStatement.UNION_TYPE.INTERSECT;
					break;
				}
				case EXCEPT:
				{
					match(EXCEPT);
					type = SelectStatement.UNION_TYPE.EXCEPT;
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
				case ALL:
				{
					match(ALL);
					type = SelectStatement.UNION_TYPE.UNIONALL;
					break;
				}
				case SELECT:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				stmt=select();
				stmt.setUnionType(type); q.addStatement(stmt);
			}
			else {
				break _loop1323;
			}
			
		} while (true);
		}
		{
		switch ( LA(1)) {
		case ORDER:
		{
			order(q);
			break;
		}
		case SEMICOLON:
		case RPAREN:
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
			limit(q);
			break;
		}
		case SEMICOLON:
		case RPAREN:
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
			offset(q);
			break;
		}
		case SEMICOLON:
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
		return q;
	}
	
	public final SelectStatement  select() throws RecognitionException, TokenStreamException {
		SelectStatement stmt;
		
		
		stmt = new SelectStatement();
		match(SELECT);
		{
		switch ( LA(1)) {
		case ALL:
		{
			match(ALL);
			stmt.setDistinct(false);
			break;
		}
		case DISTINCT:
		{
			match(DISTINCT);
			stmt.setDistinct(true);
			break;
		}
		case LPAREN:
		case STRING:
		case VARIABLE:
		case ITEMID:
		case NAMELITERAL:
		case VARLITERAL:
		case SUBJID:
		case SUBJLOC:
		case IDENT:
		case LOWERCASE:
		case UPPERCASE:
		case TITLECASE:
		case LENGTH:
		case SUBSTR:
		case TRIM:
		case TO_NUM:
		case TO_UNIT:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		selectlist(stmt);
		{
		switch ( LA(1)) {
		case WHERE:
		{
			ExpressionIF e;
			match(WHERE);
			e=orclause();
			stmt.setClause(e);
			break;
		}
		case SEMICOLON:
		case UNION:
		case INTERSECT:
		case EXCEPT:
		case RPAREN:
		case ORDER:
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
		return stmt;
	}
	
	public final void order(
		TomaQuery q
	) throws RecognitionException, TokenStreamException {
		
		
		match(ORDER);
		match(BY);
		orderpart(q);
		{
		_loop1376:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				orderpart(q);
			}
			else {
				break _loop1376;
			}
			
		} while (true);
		}
	}
	
	public final void limit(
		TomaQuery q
	) throws RecognitionException, TokenStreamException {
		
		Token  i = null;
		
		match(LIMIT);
		i = LT(1);
		match(INT);
		q.setLimit(getInt(i.getText()));
	}
	
	public final void offset(
		TomaQuery q
	) throws RecognitionException, TokenStreamException {
		
		Token  o = null;
		
		match(OFFSET);
		o = LT(1);
		match(INT);
		q.setOffset(getInt(o.getText()));
	}
	
	public final void selectlist(
		SelectStatement stmt
	) throws RecognitionException, TokenStreamException {
		
		
		ExpressionIF e;
		e=expr();
		stmt.addSelect(e);
		{
		_loop1332:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				e=expr();
				stmt.addSelect(e);
			}
			else {
				break _loop1332;
			}
			
		} while (true);
		}
	}
	
	public final ExpressionIF  orclause() throws RecognitionException, TokenStreamException {
		ExpressionIF e;
		
		
		ExpressionIF left, right;
		e=andclause();
		{
		_loop1335:
		do {
			if ((LA(1)==OR)) {
				match(OR);
				right=andclause();
				left=e; e = context.createExpression("OR", left, right);
			}
			else {
				break _loop1335;
			}
			
		} while (true);
		}
		return e;
	}
	
	public final ExpressionIF  expr() throws RecognitionException, TokenStreamException {
		ExpressionIF p;
		
		
		ExpressionIF left, right;
		p=atomexpr();
		{
		_loop1346:
		do {
			if ((LA(1)==DOUBLEPIPE)) {
				match(DOUBLEPIPE);
				right=atomexpr();
				left = p; p = context.createExpression("||", left, right);
			}
			else {
				break _loop1346;
			}
			
		} while (true);
		}
		return p;
	}
	
	public final ExpressionIF  andclause() throws RecognitionException, TokenStreamException {
		ExpressionIF e;
		
		
		ExpressionIF left, right;
		e=clause();
		{
		_loop1338:
		do {
			if ((LA(1)==AND)) {
				match(AND);
				right=clause();
				left=e; e = context.createExpression("AND", left, right);
			}
			else {
				break _loop1338;
			}
			
		} while (true);
		}
		return e;
	}
	
	public final ExpressionIF  clause() throws RecognitionException, TokenStreamException {
		ExpressionIF e;
		
		
		ExpressionIF left, right;
		{
		switch ( LA(1)) {
		case NOT:
		{
			match(NOT);
			match(EXISTS);
			left=expr();
			e = context.createExpression("NOTEXISTS", left);
			break;
		}
		case EXISTS:
		{
			match(EXISTS);
			left=expr();
			e = context.createExpression("EXISTS", left);
			break;
		}
		default:
			if ((_tokenSet_0.member(LA(1))) && (_tokenSet_1.member(LA(2))) && (_tokenSet_2.member(LA(3))) && (_tokenSet_3.member(LA(4)))) {
				left=expr();
				e=comparator();
				right=expr();
				e.addChild(left); e.addChild(right);
			}
			else if ((_tokenSet_0.member(LA(1))) && (_tokenSet_4.member(LA(2))) && (_tokenSet_5.member(LA(3))) && (_tokenSet_6.member(LA(4)))) {
				left=expr();
				match(IS);
				match(NULL);
				e = context.createExpression("NOTEXISTS", left);
			}
			else if ((_tokenSet_0.member(LA(1))) && (_tokenSet_4.member(LA(2))) && (_tokenSet_7.member(LA(3))) && (_tokenSet_8.member(LA(4)))) {
				left=expr();
				match(IS);
				match(NOT);
				match(NULL);
				e = context.createExpression("EXISTS", left);
			}
			else if ((_tokenSet_0.member(LA(1))) && (_tokenSet_9.member(LA(2))) && (_tokenSet_2.member(LA(3))) && (_tokenSet_10.member(LA(4)))) {
				left=expr();
				match(IN);
				match(LPAREN);
				e = context.createExpression("IN");
				{
				switch ( LA(1)) {
				case SELECT:
				{
					left=statement();
					e.addChild(left);
					break;
				}
				case LPAREN:
				case STRING:
				case VARIABLE:
				case ITEMID:
				case NAMELITERAL:
				case VARLITERAL:
				case SUBJID:
				case SUBJLOC:
				case IDENT:
				case LOWERCASE:
				case UPPERCASE:
				case TITLECASE:
				case LENGTH:
				case SUBSTR:
				case TRIM:
				case TO_NUM:
				case TO_UNIT:
				{
					left=expr();
					e.addChild(left);
					{
					_loop1343:
					do {
						if ((LA(1)==COMMA)) {
							match(COMMA);
							right=expr();
							e.addChild(right);
						}
						else {
							break _loop1343;
						}
						
					} while (true);
					}
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				match(RPAREN);
			}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		return e;
	}
	
	public final ExpressionIF  comparator() throws RecognitionException, TokenStreamException {
		ExpressionIF e;
		
		
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
		case REGEXPCS:
		{
			match(REGEXPCS);
			break;
		}
		case REGEXPCI:
		{
			match(REGEXPCI);
			break;
		}
		case REGEXPNCS:
		{
			match(REGEXPNCS);
			break;
		}
		case REGEXPNCI:
		{
			match(REGEXPNCI);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		e = context.createExpression(LT(0).getText());
		return e;
	}
	
	public final ExpressionIF  atomexpr() throws RecognitionException, TokenStreamException {
		ExpressionIF p;
		
		Token  s = null;
		
		{
		switch ( LA(1)) {
		case STRING:
		{
			s = LT(1);
			match(STRING);
			p = context.createLiteral(s.getText());
			break;
		}
		case LOWERCASE:
		case UPPERCASE:
		case TITLECASE:
		case LENGTH:
		case SUBSTR:
		case TRIM:
		case TO_NUM:
		case TO_UNIT:
		{
			FunctionIF f; ExpressionIF left;
			f=function();
			p = f;
			match(LPAREN);
			left=atomexpr();
			p.addChild(left);
			{
			_loop1350:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					functionparam();
					f.addParam(LT(0).getText());
				}
				else {
					break _loop1350;
				}
				
			} while (true);
			}
			match(RPAREN);
			break;
		}
		default:
			if ((_tokenSet_11.member(LA(1))) && (_tokenSet_12.member(LA(2)))) {
				p=topicpathexpr();
			}
			else if ((LA(1)==LPAREN||LA(1)==VARIABLE) && (_tokenSet_13.member(LA(2)))) {
				PathExpressionIF path = 
					                             context.createPathExpression();
					                           path.setRoot(context.createEmptyRoot());
				p = path;
				assocpathexpr(path, null);
			}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		return p;
	}
	
	public final PathExpressionIF  topicpathexpr() throws RecognitionException, TokenStreamException {
		PathExpressionIF p;
		
		
		p=pathexpr();
		{
		switch ( LA(1)) {
		case DOT:
		{
			match(DOT);
			assocleftside(p);
			break;
		}
		case SEMICOLON:
		case UNION:
		case INTERSECT:
		case EXCEPT:
		case WHERE:
		case COMMA:
		case OR:
		case AND:
		case IS:
		case IN:
		case RPAREN:
		case DOUBLEPIPE:
		case ORDER:
		case LIMIT:
		case OFFSET:
		case NOTEQUALS:
		case EQUALS:
		case LESSTHAN:
		case GREATERTHAN:
		case LESSTHANEQ:
		case GREATERTHANEQ:
		case REGEXPCS:
		case REGEXPCI:
		case REGEXPNCS:
		case REGEXPNCI:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		return p;
	}
	
	public final void assocpathexpr(
		PathExpressionIF p, PathExpressionIF left
	) throws RecognitionException, TokenStreamException {
		
		Token  assocVar = null;
		
		PathElementIF pe = context.createElement("ASSOC");
			 if (left != null) {
			 	pe.addChild(left);
			 }
			 PathExpressionIF type, scope, right; 
			 p.addPath(pe);
		{
		switch ( LA(1)) {
		case VARIABLE:
		{
			assocVar = LT(1);
			match(VARIABLE);
			pe.bindVariable(
				                           context.createVariable(assocVar.getText()));
			break;
		}
		case LPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(LPAREN);
		type=pathexpr();
		match(RPAREN);
		pe.setType(type);
		{
		switch ( LA(1)) {
		case ATSCOPE:
		{
			match(ATSCOPE);
			scope=pathexpr();
			pe.setScope(scope);
			break;
		}
		case RARROW:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(RARROW);
		match(LPAREN);
		right=roleexpr();
		match(RPAREN);
		pe.addChild(right);
		{
		switch ( LA(1)) {
		case LSQUARE:
		{
			match(LSQUARE);
			match(VARIABLE);
			match(RSQUARE);
			break;
		}
		case SEMICOLON:
		case UNION:
		case INTERSECT:
		case EXCEPT:
		case WHERE:
		case COMMA:
		case OR:
		case AND:
		case IS:
		case IN:
		case RPAREN:
		case DOUBLEPIPE:
		case DOT:
		case ORDER:
		case LIMIT:
		case OFFSET:
		case NOTEQUALS:
		case EQUALS:
		case LESSTHAN:
		case GREATERTHAN:
		case LESSTHANEQ:
		case GREATERTHANEQ:
		case REGEXPCS:
		case REGEXPCI:
		case REGEXPNCS:
		case REGEXPNCI:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		PathElementIF part;
		{
		_loop1358:
		do {
			if ((LA(1)==DOT) && ((LA(2) >= ID && LA(2) <= SUPER))) {
				match(DOT);
				part=pathpart();
				p.addPath(part);
			}
			else {
				break _loop1358;
			}
			
		} while (true);
		}
		{
		switch ( LA(1)) {
		case DOT:
		{
			match(DOT);
			assocleftside(p);
			break;
		}
		case SEMICOLON:
		case UNION:
		case INTERSECT:
		case EXCEPT:
		case WHERE:
		case COMMA:
		case OR:
		case AND:
		case IS:
		case IN:
		case RPAREN:
		case DOUBLEPIPE:
		case ORDER:
		case LIMIT:
		case OFFSET:
		case NOTEQUALS:
		case EQUALS:
		case LESSTHAN:
		case GREATERTHAN:
		case LESSTHANEQ:
		case GREATERTHANEQ:
		case REGEXPCS:
		case REGEXPCI:
		case REGEXPNCS:
		case REGEXPNCI:
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
	
	public final FunctionIF  function() throws RecognitionException, TokenStreamException {
		FunctionIF f;
		
		
		{
		switch ( LA(1)) {
		case LOWERCASE:
		{
			match(LOWERCASE);
			break;
		}
		case UPPERCASE:
		{
			match(UPPERCASE);
			break;
		}
		case TITLECASE:
		{
			match(TITLECASE);
			break;
		}
		case LENGTH:
		{
			match(LENGTH);
			break;
		}
		case SUBSTR:
		{
			match(SUBSTR);
			break;
		}
		case TRIM:
		{
			match(TRIM);
			break;
		}
		case TO_NUM:
		{
			match(TO_NUM);
			break;
		}
		case TO_UNIT:
		{
			match(TO_UNIT);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		f = context.createFunction(LT(0).getText());
		return f;
	}
	
	public final void functionparam() throws RecognitionException, TokenStreamException {
		
		
		{
		switch ( LA(1)) {
		case INT:
		{
			match(INT);
			break;
		}
		case IDENT:
		{
			match(IDENT);
			break;
		}
		case STRING:
		{
			match(STRING);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
	}
	
	public final PathExpressionIF  pathexpr() throws RecognitionException, TokenStreamException {
		PathExpressionIF path;
		
		Token  var = null;
		
		path = context.createPathExpression();
		{
		switch ( LA(1)) {
		case ITEMID:
		case NAMELITERAL:
		case VARLITERAL:
		case SUBJID:
		case SUBJLOC:
		case IDENT:
		{
			PathRootIF topic;
			topic=topicliteral();
			path.setRoot(topic);
			break;
		}
		case VARIABLE:
		{
			var = LT(1);
			match(VARIABLE);
			path.setRoot(context.createVariable(var.getText()));
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		PathElementIF pe;
		{
		_loop1366:
		do {
			if ((LA(1)==DOT) && ((LA(2) >= ID && LA(2) <= SUPER))) {
				match(DOT);
				pe=pathpart();
				path.addPath(pe);
			}
			else {
				break _loop1366;
			}
			
		} while (true);
		}
		return path;
	}
	
	public final void assocleftside(
		PathExpressionIF p
	) throws RecognitionException, TokenStreamException {
		
		
		PathExpressionIF left;
		match(LPAREN);
		left=roleexpr();
		match(RPAREN);
		match(LARROW);
		assocpathexpr(p, left);
	}
	
	public final PathExpressionIF  roleexpr() throws RecognitionException, TokenStreamException {
		PathExpressionIF p;
		
		
		{
		switch ( LA(1)) {
		case ANONYM:
		{
			match(ANONYM);
			p = context.createPathExpression();
				                  p.setRoot(context.createAnyRoot());
			break;
		}
		case VARIABLE:
		case ITEMID:
		case NAMELITERAL:
		case VARLITERAL:
		case SUBJID:
		case SUBJLOC:
		case IDENT:
		{
			p=pathexpr();
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		return p;
	}
	
	public final PathElementIF  pathpart() throws RecognitionException, TokenStreamException {
		PathElementIF p;
		
		Token  var = null;
		Token  bound = null;
		
		{
		switch ( LA(1)) {
		case ID:
		{
			match(ID);
			break;
		}
		case SI:
		{
			match(SI);
			break;
		}
		case SL:
		{
			match(SL);
			break;
		}
		case NAME:
		{
			match(NAME);
			break;
		}
		case VAR:
		{
			match(VAR);
			break;
		}
		case OC:
		{
			match(OC);
			break;
		}
		case REF:
		{
			match(REF);
			break;
		}
		case DATA:
		{
			match(DATA);
			break;
		}
		case SC:
		{
			match(SC);
			break;
		}
		case PLAYER:
		{
			match(PLAYER);
			break;
		}
		case ROLE:
		{
			match(ROLE);
			break;
		}
		case REIFIER:
		{
			match(REIFIER);
			break;
		}
		case TYPE:
		{
			match(TYPE);
			break;
		}
		case INSTANCE:
		{
			match(INSTANCE);
			break;
		}
		case SUB:
		{
			match(SUB);
			break;
		}
		case SUPER:
		{
			match(SUPER);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		p = context.createElement(LT(0).getText());
		{
		switch ( LA(1)) {
		case LPAREN:
		{
			match(LPAREN);
			{
			switch ( LA(1)) {
			case VARIABLE:
			case ITEMID:
			case NAMELITERAL:
			case VARLITERAL:
			case SUBJID:
			case SUBJLOC:
			case IDENT:
			{
				PathExpressionIF path;
				path=pathexpr();
				p.setType(path);
				break;
			}
			case INT:
			case ASTERISK:
			{
				Level l;
				l=level();
				p.setLevel(l);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(RPAREN);
			break;
		}
		case SEMICOLON:
		case UNION:
		case INTERSECT:
		case EXCEPT:
		case WHERE:
		case COMMA:
		case OR:
		case AND:
		case IS:
		case IN:
		case RPAREN:
		case DOUBLEPIPE:
		case DOT:
		case ATSCOPE:
		case RARROW:
		case LSQUARE:
		case ORDER:
		case LIMIT:
		case OFFSET:
		case NOTEQUALS:
		case EQUALS:
		case LESSTHAN:
		case GREATERTHAN:
		case LESSTHANEQ:
		case GREATERTHANEQ:
		case REGEXPCS:
		case REGEXPCI:
		case REGEXPNCS:
		case REGEXPNCI:
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
		case ATSCOPE:
		{
			match(ATSCOPE);
			PathExpressionIF pe; PathRootIF r;
			{
			switch ( LA(1)) {
			case VARIABLE:
			{
				var = LT(1);
				match(VARIABLE);
				r = context.createVariable(var.getText());
					                          pe = context.createPathExpression();
					                          pe.setRoot(r);
				break;
			}
			case ITEMID:
			case NAMELITERAL:
			case VARLITERAL:
			case SUBJID:
			case SUBJLOC:
			case IDENT:
			{
				r=topicliteral();
				pe = context.createPathExpression(); 
					                          pe.setRoot(r);
				break;
			}
			case LPAREN:
			{
				match(LPAREN);
				pe=pathexpr();
				match(RPAREN);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			p.setScope(pe);
			break;
		}
		case SEMICOLON:
		case UNION:
		case INTERSECT:
		case EXCEPT:
		case WHERE:
		case COMMA:
		case OR:
		case AND:
		case IS:
		case IN:
		case RPAREN:
		case DOUBLEPIPE:
		case DOT:
		case RARROW:
		case LSQUARE:
		case ORDER:
		case LIMIT:
		case OFFSET:
		case NOTEQUALS:
		case EQUALS:
		case LESSTHAN:
		case GREATERTHAN:
		case LESSTHANEQ:
		case GREATERTHANEQ:
		case REGEXPCS:
		case REGEXPCI:
		case REGEXPNCS:
		case REGEXPNCI:
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
		case LSQUARE:
		{
			match(LSQUARE);
			bound = LT(1);
			match(VARIABLE);
			p.bindVariable(
				                           context.createVariable(bound.getText()));
			match(RSQUARE);
			break;
		}
		case SEMICOLON:
		case UNION:
		case INTERSECT:
		case EXCEPT:
		case WHERE:
		case COMMA:
		case OR:
		case AND:
		case IS:
		case IN:
		case RPAREN:
		case DOUBLEPIPE:
		case DOT:
		case RARROW:
		case ORDER:
		case LIMIT:
		case OFFSET:
		case NOTEQUALS:
		case EQUALS:
		case LESSTHAN:
		case GREATERTHAN:
		case LESSTHANEQ:
		case GREATERTHANEQ:
		case REGEXPCS:
		case REGEXPCI:
		case REGEXPNCS:
		case REGEXPNCI:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		return p;
	}
	
	public final PathRootIF  topicliteral() throws RecognitionException, TokenStreamException {
		PathRootIF root;
		
		
		String type;
		{
		switch ( LA(1)) {
		case ITEMID:
		{
			match(ITEMID);
			type = "IID";
			break;
		}
		case NAMELITERAL:
		{
			match(NAMELITERAL);
			type = "NAME";
			break;
		}
		case VARLITERAL:
		{
			match(VARLITERAL);
			type = "VAR";
			break;
		}
		case SUBJID:
		{
			match(SUBJID);
			type = "SUBJID";
			break;
		}
		case SUBJLOC:
		{
			match(SUBJLOC);
			type = "SUBJLOC";
			break;
		}
		case IDENT:
		{
			match(IDENT);
			type = "IID";
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		root = context.createTopic(type, LT(0).getText());
		return root;
	}
	
	public final Level  level() throws RecognitionException, TokenStreamException {
		Level l;
		
		Token  i = null;
		Token  s = null;
		Token  e = null;
		
		{
		if ((LA(1)==INT) && (LA(2)==RPAREN)) {
			i = LT(1);
			match(INT);
			l = new Level(getInt(i.getText()));
		}
		else if ((LA(1)==ASTERISK)) {
			match(ASTERISK);
			l = new Level(0, Integer.MAX_VALUE);
		}
		else if ((LA(1)==INT) && (LA(2)==RANGE)) {
			s = LT(1);
			match(INT);
			match(RANGE);
			int start = 0; int end = 0;
			{
			switch ( LA(1)) {
			case INT:
			{
				e = LT(1);
				match(INT);
				end = getInt(e.getText());
				break;
			}
			case ASTERISK:
			{
				match(ASTERISK);
				end = Integer.MAX_VALUE;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			l = new Level(getInt(s.getText()), end);
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		return l;
	}
	
	public final void orderpart(
		TomaQuery q
	) throws RecognitionException, TokenStreamException {
		
		Token  col = null;
		
		QueryOrder.SORT_ORDER order = QueryOrder.SORT_ORDER.ASC;
		col = LT(1);
		match(INT);
		{
		switch ( LA(1)) {
		case ASC:
		{
			match(ASC);
			order = QueryOrder.SORT_ORDER.ASC;
			break;
		}
		case DESC:
		{
			match(DESC);
			order = QueryOrder.SORT_ORDER.DESC;
			break;
		}
		case SEMICOLON:
		case COMMA:
		case RPAREN:
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
		q.addOrderBy(getInt(col.getText()), order);
	}
	
	public final FunctionIF  aggregate_function() throws RecognitionException, TokenStreamException {
		FunctionIF f;
		
		
		{
		switch ( LA(1)) {
		case COUNT:
		{
			match(COUNT);
			break;
		}
		case SUM:
		{
			match(SUM);
			break;
		}
		case MAX:
		{
			match(MAX);
			break;
		}
		case MIN:
		{
			match(MIN);
			break;
		}
		case AVG:
		{
			match(AVG);
			break;
		}
		case CONCAT:
		{
			match(CONCAT);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		f = context.createFunction(LT(0).getText());
		return f;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		":",
		"\"union\"",
		"\"intersect\"",
		"\"except\"",
		"\"all\"",
		"\"select\"",
		"\"distinct\"",
		"\"where\"",
		",",
		"\"or\"",
		"\"and\"",
		"\"not\"",
		"\"exists\"",
		"\"is\"",
		"\"null\"",
		"\"in\"",
		"(",
		")",
		"||",
		"STRING",
		".",
		"VARIABLE",
		"@",
		"->",
		"[",
		"]",
		"<-",
		"$$",
		"\"id\"",
		"\"si\"",
		"\"sl\"",
		"\"name\"",
		"\"var\"",
		"\"oc\"",
		"\"ref\"",
		"\"data\"",
		"\"sc\"",
		"\"player\"",
		"\"role\"",
		"\"reifier\"",
		"\"type\"",
		"\"instance\"",
		"\"sub\"",
		"\"super\"",
		"\"order\"",
		"\"by\"",
		"INT",
		"\"asc\"",
		"\"desc\"",
		"\"limit\"",
		"\"offset\"",
		"*",
		"..",
		"ITEMID",
		"NAMELITERAL",
		"VARLITERAL",
		"SUBJID",
		"SUBJLOC",
		"IDENT",
		"!=",
		"=",
		"<",
		">",
		"<=",
		">=",
		"~",
		"~*",
		"!~",
		"!~*",
		"\"count\"",
		"\"sum\"",
		"\"max\"",
		"\"min\"",
		"\"avg\"",
		"\"concat\"",
		"\"lowercase\"",
		"\"uppercase\"",
		"\"titlecase\"",
		"\"length\"",
		"\"substr\"",
		"\"trim\"",
		"\"to_num\"",
		"\"to_unit\"",
		"WS",
		"COMMENT",
		"NUMBER",
		":",
		"?",
		"/",
		"{",
		"}",
		"|"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 9079256848821911552L, 8355840L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { -144115188020281344L, 511L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 9079538319522529280L, 8355840L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { -116530641978232592L, 511L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 9079256848834625536L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 9079538319522791424L, 8355840L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { 9106841394876674288L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 9079538319522562048L, 8355840L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = { 9079538322135977984L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = { 9079256848835018752L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	private static final long[] mk_tokenSet_10() {
		long[] data = { 9079538322144498176L, 8355840L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
	private static final long[] mk_tokenSet_11() {
		long[] data = { 9079256848812474368L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_11 = new BitSet(mk_tokenSet_11());
	private static final long[] mk_tokenSet_12() {
		long[] data = { -9196068964090087184L, 511L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_12 = new BitSet(mk_tokenSet_12());
	private static final long[] mk_tokenSet_13() {
		long[] data = { 9079256848813522944L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_13 = new BitSet(mk_tokenSet_13());
	
	}
