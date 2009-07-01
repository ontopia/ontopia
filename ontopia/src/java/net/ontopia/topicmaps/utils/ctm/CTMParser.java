// $ANTLR 2.7.7 (20060906): "ctm.g" -> "CTMParser.java"$
 package net.ontopia.topicmaps.utils.ctm; 
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

  import java.io.File;
  import java.io.Reader;
  import java.io.IOException;
  
  import java.net.MalformedURLException;
  
  import java.util.Set;
  import java.util.Map;
  import java.util.List;
  import java.util.HashMap;
  import java.util.Iterator;
  import java.util.ArrayList;
  import java.util.Collection;
  
  import net.ontopia.utils.CompactHashSet;
  import net.ontopia.utils.OntopiaRuntimeException;
  import net.ontopia.infoset.core.LocatorIF;
  import net.ontopia.infoset.impl.basic.URILocator;
  import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
  import net.ontopia.topicmaps.core.*;
  import net.ontopia.topicmaps.xml.XTMTopicMapReader;
  import net.ontopia.topicmaps.xml.XTMContentHandler;
  import net.ontopia.topicmaps.xml.InvalidTopicMapException;
  import net.ontopia.topicmaps.utils.PSI;
  import net.ontopia.topicmaps.utils.MergeUtils;
  import net.ontopia.topicmaps.utils.ltm.AntlrWrapException;
  import org.xml.sax.InputSource;
  import antlr.TokenStreamException;
  import antlr.SemanticException;

/**
 * INTERNAL: Parser for the CTM syntax.
 */
public class CTMParser extends antlr.LLkParser       implements CTMParserTokenTypes
 {

  private LocatorIF document;
  private TopicMapIF topicmap;
  private TopicMapBuilderIF builder;
  private Set alreadyLoaded;// the set of locators whose resources are loaded
  private boolean subordinate;//whether we were created via #MERGEMAP or #INC
  private ParseContextIF context;
  private ParseEventHandlerIF handler;
  private ParseEventHandlerIF real_handler;

  private TopicGeneratorIF topic_ref;    // topic referenced by topic_ref
  private TopicGeneratorIF type;      // used for role types
  private LiteralGeneratorIF literal;    // last parsed literal
  private BasicLiteralGenerator basic_literal; // reused generator
  private BasicLiteralGenerator datatype_literal; // reused generator
  private BasicLiteralGenerator tmp;              // used for swapping
  private TopicGeneratorIF wildcard;           // reused generator
  private String id;                     // prefix name (used in declarations)
  private Template template;             // template to be invoked (only invoc)

  private String template_name;  // definition only
  private String template_name2; // invocation only
  private List parameters;
  private Map wildcards;          // for top-level named wildcards
  private Map template_wildcards; // for named wildcards in templates
  private Template current_template; // this template inside template definition

  // --- configuration interface

  public void setTopicMap(TopicMapIF topicmap) {
    this.topicmap = topicmap;
    this.builder = topicmap.getBuilder();
    this.context = new GlobalParseContext(topicmap, document);
    this.handler = new BuilderEventHandler(builder, context);
    this.real_handler = handler;
    this.basic_literal = new BasicLiteralGenerator();
    this.datatype_literal = new BasicLiteralGenerator();
    this.wildcard = new WildcardTopicGenerator(context);
    this.wildcards = new HashMap();
    this.template_wildcards = new HashMap();
  }

  public ParseContextIF getContext() {
    return context;
  }

  public void setBase(LocatorIF base) {
    this.document = base;
  }

  public void init() {
    alreadyLoaded = new CompactHashSet();
    alreadyLoaded.add(document); // don't want to read top document again
  }

  private TopicGeneratorIF getWildcard(String name) {
    Map map;

    if (current_template == null) 
      map = wildcards;
    else
      map = template_wildcards;
        
    TopicGeneratorIF gen = (TopicGeneratorIF) map.get(name);
    if (gen == null) {
      gen = new NamedWildcardTopicGenerator(context, name);
      map.put(name, gen);
    }

    if (current_template != null)
      current_template.registerWildcard(name, gen);

    return gen;
  }

  private LocatorIF getAbsoluteLocator() throws antlr.TokenStreamException {
    try {
      return new URILocator(LT(0).getText());
    } catch (MalformedURLException e) {
      throw new OntopiaRuntimeException(e); // FIXME!          
    }
  }

  private LocatorIF getRelativeLocator() throws antlr.TokenStreamException {
    return document.resolveAbsolute(LT(0).getText());
  }

protected CTMParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public CTMParser(TokenBuffer tokenBuf) {
  this(tokenBuf,5);
}

protected CTMParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public CTMParser(TokenStream lexer) {
  this(lexer,5);
}

public CTMParser(ParserSharedInputState state) {
  super(state,5);
  tokenNames = _tokenNames;
}

	public final void topicmap() throws RecognitionException, TokenStreamException {
		
		
		{
		switch ( LA(1)) {
		case ENCODING:
		{
			encoding_decl();
			break;
		}
		case EOF:
		case VERSION:
		case PREFIX:
		case IDENTIFIER:
		case INCLUDE:
		case MERGEMAP:
		case VARIABLE:
		case WILDCARD:
		case NAMED_WILDCARD:
		case HAT:
		case EQUALS:
		case TILDE:
		case LEFTBRACKET:
		case QNAME:
		case IRI:
		case WRAPPED_IRI:
		case DEF:
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
		case VERSION:
		{
			version();
			break;
		}
		case EOF:
		case PREFIX:
		case IDENTIFIER:
		case INCLUDE:
		case MERGEMAP:
		case VARIABLE:
		case WILDCARD:
		case NAMED_WILDCARD:
		case HAT:
		case EQUALS:
		case TILDE:
		case LEFTBRACKET:
		case QNAME:
		case IRI:
		case WRAPPED_IRI:
		case DEF:
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
		case TILDE:
		{
			tm_reifier();
			break;
		}
		case EOF:
		case PREFIX:
		case IDENTIFIER:
		case INCLUDE:
		case MERGEMAP:
		case VARIABLE:
		case WILDCARD:
		case NAMED_WILDCARD:
		case HAT:
		case EQUALS:
		case LEFTBRACKET:
		case QNAME:
		case IRI:
		case WRAPPED_IRI:
		case DEF:
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
		_loop9:
		do {
			switch ( LA(1)) {
			case PREFIX:
			case INCLUDE:
			case MERGEMAP:
			{
				directive();
				break;
			}
			case DEF:
			{
				template_def();
				break;
			}
			default:
				if ((_tokenSet_0.member(LA(1))) && (_tokenSet_1.member(LA(2))) && (_tokenSet_2.member(LA(3))) && (_tokenSet_3.member(LA(4))) && (_tokenSet_4.member(LA(5)))) {
					topic();
				}
				else if ((_tokenSet_5.member(LA(1))) && (_tokenSet_6.member(LA(2))) && (_tokenSet_7.member(LA(3))) && (_tokenSet_8.member(LA(4))) && (_tokenSet_4.member(LA(5)))) {
					{
					boolean synPredMatched8 = false;
					if (((_tokenSet_5.member(LA(1))) && (_tokenSet_6.member(LA(2))) && (_tokenSet_9.member(LA(3))) && (_tokenSet_10.member(LA(4))) && (_tokenSet_11.member(LA(5))))) {
						int _m8 = mark();
						synPredMatched8 = true;
						inputState.guessing++;
						try {
							{
							topic_ref();
							match(LEFTPAREN);
							topic_ref();
							match(COLON);
							}
						}
						catch (RecognitionException pe) {
							synPredMatched8 = false;
						}
						rewind(_m8);
inputState.guessing--;
					}
					if ( synPredMatched8 ) {
						association();
					}
					else if ((LA(1)==IDENTIFIER) && (LA(2)==LEFTPAREN) && (_tokenSet_12.member(LA(3))) && (_tokenSet_13.member(LA(4))) && (_tokenSet_14.member(LA(5)))) {
						template_invocation();
					}
					else {
						throw new NoViableAltException(LT(1), getFilename());
					}
					
					}
				}
			else {
				break _loop9;
			}
			}
		} while (true);
		}
		match(Token.EOF_TYPE);
	}
	
	public final void encoding_decl() throws RecognitionException, TokenStreamException {
		
		
		match(ENCODING);
		string();
	}
	
	public final void version() throws RecognitionException, TokenStreamException {
		
		
		match(VERSION);
		match(ONEOH);
	}
	
	public final void tm_reifier() throws RecognitionException, TokenStreamException {
		
		
		match(TILDE);
		topic_ref();
		if ( inputState.guessing==0 ) {
			
			// if this is an included TM, don't set reifier
			if (context.getIncludeUris().isEmpty())
			handler.addReifier(topic_ref);
			else {
			// the topic is referenced, and so must be created somehow...
			handler.startTopic(topic_ref);
			handler.endTopic();
			}
			
		}
	}
	
	public final void directive() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case PREFIX:
		{
			prefix_decl();
			break;
		}
		case INCLUDE:
		{
			include();
			break;
		}
		case MERGEMAP:
		{
			mergemap();
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void topic() throws RecognitionException, TokenStreamException {
		
		
		topic_identity();
		{
		switch ( LA(1)) {
		case IDENTIFIER:
		case VARIABLE:
		case WILDCARD:
		case NAMED_WILDCARD:
		case HAT:
		case EQUALS:
		case HYPHEN:
		case ISA:
		case AKO:
		case LEFTBRACKET:
		case QNAME:
		case IRI:
		case WRAPPED_IRI:
		{
			property();
			{
			_loop19:
			do {
				if ((LA(1)==SEMICOLON) && (_tokenSet_15.member(LA(2))) && (_tokenSet_16.member(LA(3))) && (_tokenSet_17.member(LA(4))) && (_tokenSet_18.member(LA(5)))) {
					match(SEMICOLON);
					property();
				}
				else {
					break _loop19;
				}
				
			} while (true);
			}
			{
			switch ( LA(1)) {
			case SEMICOLON:
			{
				match(SEMICOLON);
				break;
			}
			case STOP:
			{
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
		case STOP:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(STOP);
		if ( inputState.guessing==0 ) {
			handler.endTopic();
		}
	}
	
	public final void template_def() throws RecognitionException, TokenStreamException {
		
		
		match(DEF);
		match(IDENTIFIER);
		if ( inputState.guessing==0 ) {
			template_name = LT(0).getText(); 
			parameters = new ArrayList();
		}
		match(LEFTPAREN);
		{
		switch ( LA(1)) {
		case VARIABLE:
		{
			match(VARIABLE);
			if ( inputState.guessing==0 ) {
				parameters.add(LT(0).getText());
			}
			{
			_loop69:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					match(VARIABLE);
					if ( inputState.guessing==0 ) {
						parameters.add(LT(0).getText());
					}
				}
				else {
					break _loop69;
				}
				
			} while (true);
			}
			break;
		}
		case RIGHTPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(RIGHTPAREN);
		if ( inputState.guessing==0 ) {
			handler = new TemplateEventHandler(template_name, parameters, real_handler);
			current_template = ((TemplateEventHandler) handler).getTemplate();
		}
		{
		_loop74:
		do {
			if ((_tokenSet_0.member(LA(1))) && (_tokenSet_1.member(LA(2))) && (_tokenSet_19.member(LA(3))) && (_tokenSet_17.member(LA(4))) && (_tokenSet_18.member(LA(5)))) {
				topic();
			}
			else if ((_tokenSet_5.member(LA(1))) && (_tokenSet_6.member(LA(2))) && (_tokenSet_7.member(LA(3))) && (_tokenSet_20.member(LA(4))) && (_tokenSet_18.member(LA(5)))) {
				{
				boolean synPredMatched73 = false;
				if (((_tokenSet_5.member(LA(1))) && (_tokenSet_6.member(LA(2))) && (_tokenSet_9.member(LA(3))) && (_tokenSet_10.member(LA(4))) && (_tokenSet_11.member(LA(5))))) {
					int _m73 = mark();
					synPredMatched73 = true;
					inputState.guessing++;
					try {
						{
						topic_ref();
						match(LEFTPAREN);
						topic_ref();
						match(COLON);
						}
					}
					catch (RecognitionException pe) {
						synPredMatched73 = false;
					}
					rewind(_m73);
inputState.guessing--;
				}
				if ( synPredMatched73 ) {
					association();
				}
				else if ((LA(1)==IDENTIFIER) && (LA(2)==LEFTPAREN) && (_tokenSet_12.member(LA(3))) && (_tokenSet_21.member(LA(4))) && (_tokenSet_22.member(LA(5)))) {
					template_invocation();
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				
				}
			}
			else {
				break _loop74;
			}
			
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			context.registerTemplate(template_name, current_template);
			handler = real_handler;
			current_template = null;
		}
		match(END);
	}
	
	public final void topic_ref() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case IDENTIFIER:
		{
			match(IDENTIFIER);
			if ( inputState.guessing==0 ) {
				
				topic_ref = context.getTopicById(LT(0).getText());
				
			}
			break;
		}
		case LEFTBRACKET:
		{
			embedded_topic();
			break;
		}
		case WILDCARD:
		{
			match(WILDCARD);
			if ( inputState.guessing==0 ) {
				topic_ref = wildcard;
			}
			break;
		}
		case NAMED_WILDCARD:
		{
			match(NAMED_WILDCARD);
			if ( inputState.guessing==0 ) {
				topic_ref = getWildcard(LT(0).getText());
			}
			break;
		}
		case EQUALS:
		{
			subject_locator();
			if ( inputState.guessing==0 ) {
				
				topic_ref = context.getTopicBySubjectLocator(literal.getLocator());
				
			}
			break;
		}
		case HAT:
		{
			match(HAT);
			iri_ref();
			if ( inputState.guessing==0 ) {
				
				topic_ref = context.getTopicByItemIdentifier(literal.getLocator());
				
			}
			break;
		}
		default:
			if ((LA(1)==VARIABLE) && (_tokenSet_23.member(LA(2))) && (_tokenSet_24.member(LA(3))) && (_tokenSet_18.member(LA(4))) && (_tokenSet_18.member(LA(5)))) {
				match(VARIABLE);
				if ( inputState.guessing==0 ) {
					
					topic_ref = current_template.getTopicVariable(LT(0).getText());
					
				}
			}
			else if ((_tokenSet_25.member(LA(1))) && (_tokenSet_23.member(LA(2))) && (_tokenSet_24.member(LA(3))) && (_tokenSet_18.member(LA(4))) && (_tokenSet_18.member(LA(5)))) {
				iri_ref();
				if ( inputState.guessing==0 ) {
					
					topic_ref = context.getTopicBySubjectIdentifier(literal.getLocator());
					
				}
			}
			else if ((LA(1)==QNAME) && (_tokenSet_23.member(LA(2))) && (_tokenSet_24.member(LA(3))) && (_tokenSet_18.member(LA(4))) && (_tokenSet_18.member(LA(5)))) {
				match(QNAME);
				if ( inputState.guessing==0 ) {
					
					topic_ref = context.getTopicByQname(LT(0).getText());
					
				}
			}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void association() throws RecognitionException, TokenStreamException {
		
		
		topic_ref();
		match(LEFTPAREN);
		if ( inputState.guessing==0 ) {
			handler.startAssociation(topic_ref);
		}
		role();
		{
		_loop46:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				role();
			}
			else {
				break _loop46;
			}
			
		} while (true);
		}
		match(RIGHTPAREN);
		{
		switch ( LA(1)) {
		case AT:
		{
			scope();
			break;
		}
		case EOF:
		case PREFIX:
		case IDENTIFIER:
		case INCLUDE:
		case MERGEMAP:
		case VARIABLE:
		case WILDCARD:
		case NAMED_WILDCARD:
		case HAT:
		case EQUALS:
		case TILDE:
		case LEFTBRACKET:
		case QNAME:
		case IRI:
		case WRAPPED_IRI:
		case DEF:
		case END:
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
			handler.endRoles();
		}
		{
		switch ( LA(1)) {
		case TILDE:
		{
			reifier();
			break;
		}
		case EOF:
		case PREFIX:
		case IDENTIFIER:
		case INCLUDE:
		case MERGEMAP:
		case VARIABLE:
		case WILDCARD:
		case NAMED_WILDCARD:
		case HAT:
		case EQUALS:
		case LEFTBRACKET:
		case QNAME:
		case IRI:
		case WRAPPED_IRI:
		case DEF:
		case END:
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
			handler.endAssociation();
		}
	}
	
	public final void template_invocation() throws RecognitionException, TokenStreamException {
		
		
		match(IDENTIFIER);
		if ( inputState.guessing==0 ) {
			template_name2 = LT(0).getText();
			parameters = new ArrayList();
		}
		match(LEFTPAREN);
		{
		switch ( LA(1)) {
		case IDENTIFIER:
		case VARIABLE:
		case WILDCARD:
		case NAMED_WILDCARD:
		case HAT:
		case EQUALS:
		case LEFTBRACKET:
		case QNAME:
		case IRI:
		case WRAPPED_IRI:
		case INTEGER:
		case DECIMAL:
		case DATE:
		case DATETIME:
		case SINGLE_QUOTED_STRING:
		case TRIPLE_QUOTED_STRING:
		{
			argument();
			{
			_loop78:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					argument();
				}
				else {
					break _loop78;
				}
				
			} while (true);
			}
			break;
		}
		case RIGHTPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(RIGHTPAREN);
		if ( inputState.guessing==0 ) {
			handler.templateInvocation(template_name2, parameters);
		}
	}
	
	public final void string() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case SINGLE_QUOTED_STRING:
		{
			match(SINGLE_QUOTED_STRING);
			break;
		}
		case TRIPLE_QUOTED_STRING:
		{
			match(TRIPLE_QUOTED_STRING);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void prefix_decl() throws RecognitionException, TokenStreamException {
		
		
		match(PREFIX);
		match(IDENTIFIER);
		if ( inputState.guessing==0 ) {
			id = LT(0).getText();
		}
		iri_ref();
		if ( inputState.guessing==0 ) {
			context.addPrefix(id, literal.getLocator());
		}
	}
	
	public final void include() throws RecognitionException, TokenStreamException {
		
		
		match(INCLUDE);
		iri_ref();
		if ( inputState.guessing==0 ) {
			
			LocatorIF docuri = literal.getLocator();
			InputSource source = new InputSource(docuri.getExternalForm());
			ParseContextIF othercontext;
			try {
			Reader reader = 
			CTMTopicMapReader.makeReader(source, new CTMEncodingSniffer());
			CTMLexer lexer = new CTMLexer(reader);
			lexer.setDocuri(docuri.getExternalForm());
			CTMParser parser = new CTMParser(lexer);
			parser.setBase(docuri);
			parser.setTopicMap(topicmap);
			parser.init();
			othercontext = parser.getContext();
			Iterator it = context.getIncludeUris().iterator();
			while (it.hasNext())
			othercontext.addIncludeUri((LocatorIF) it.next());
			othercontext.addIncludeUri(document);
			parser.topicmap();
			reader.close();
			} catch (IOException e) {
			throw new AntlrWrapException(e);
			}
			
			// pull over template definitions
			Iterator it = othercontext.getTemplates().values().iterator();
			while (it.hasNext()) {
			Template template = (Template) it.next();
			context.registerTemplate(template.getName(), template);
			}
			
		}
	}
	
	public final void mergemap() throws RecognitionException, TokenStreamException {
		
		
		match(MERGEMAP);
		iri_ref();
		if ( inputState.guessing==0 ) {
			
			try {
			LocatorIF docuri = literal.getLocator();   
			TopicMapIF tm = new CTMTopicMapReader(docuri).read();
			MergeUtils.mergeInto(topicmap, tm);
			} catch (IOException e) {
			throw new AntlrWrapException(e);
			}
			
		}
	}
	
	public final void iri_ref() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case QNAME:
		{
			{
			match(QNAME);
			if ( inputState.guessing==0 ) {
				literal = basic_literal;
				basic_literal.setLocator(context.resolveQname(LT(0).getText()));
			}
			}
			break;
		}
		case IRI:
		{
			match(IRI);
			if ( inputState.guessing==0 ) {
				literal = basic_literal;
				basic_literal.setLocator(getAbsoluteLocator());
			}
			break;
		}
		case WRAPPED_IRI:
		{
			match(WRAPPED_IRI);
			if ( inputState.guessing==0 ) {
				literal = basic_literal;
				basic_literal.setLocator(getRelativeLocator());
			}
			break;
		}
		case VARIABLE:
		{
			match(VARIABLE);
			if ( inputState.guessing==0 ) {
				literal = current_template.getLiteralVariable(LT(0).getText());
			}
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void topic_identity() throws RecognitionException, TokenStreamException {
		
		
		{
		switch ( LA(1)) {
		case IDENTIFIER:
		{
			match(IDENTIFIER);
			if ( inputState.guessing==0 ) {
				
				handler.startTopic(context.getTopicById(LT(0).getText()));
				
			}
			break;
		}
		case HAT:
		{
			item_identifier();
			if ( inputState.guessing==0 ) {
				handler.startTopicItemIdentifier(literal);
			}
			break;
		}
		case EQUALS:
		{
			subject_locator();
			if ( inputState.guessing==0 ) {
				handler.startTopicSubjectLocator(literal);
			}
			break;
		}
		case WILDCARD:
		{
			match(WILDCARD);
			if ( inputState.guessing==0 ) {
				handler.startTopic(wildcard);
			}
			break;
		}
		case NAMED_WILDCARD:
		{
			match(NAMED_WILDCARD);
			if ( inputState.guessing==0 ) {
				handler.startTopic(getWildcard(LT(0).getText()));
			}
			break;
		}
		default:
			if ((LA(1)==VARIABLE) && (_tokenSet_1.member(LA(2))) && (_tokenSet_26.member(LA(3))) && (_tokenSet_17.member(LA(4))) && (_tokenSet_18.member(LA(5)))) {
				match(VARIABLE);
				if ( inputState.guessing==0 ) {
					if (current_template == null)
					throw new InvalidTopicMapException("Variable $" + LT(0).getText() + 
					" referenced outside template");
					topic_ref = current_template.getTopicIdentityVariable(LT(0).getText(), 
					topicmap);
					handler.startTopic(topic_ref);
				}
			}
			else if ((_tokenSet_25.member(LA(1))) && (_tokenSet_1.member(LA(2))) && (_tokenSet_26.member(LA(3))) && (_tokenSet_17.member(LA(4))) && (_tokenSet_18.member(LA(5)))) {
				subject_identifier();
				if ( inputState.guessing==0 ) {
					handler.startTopicSubjectIdentifier(literal);
				}
			}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
	}
	
	public final void property() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case HYPHEN:
		{
			name();
			break;
		}
		case ISA:
		{
			instance_of();
			break;
		}
		case AKO:
		{
			a_kind_of();
			break;
		}
		default:
			if ((_tokenSet_5.member(LA(1))) && (_tokenSet_27.member(LA(2))) && (_tokenSet_28.member(LA(3))) && (_tokenSet_29.member(LA(4))) && (_tokenSet_18.member(LA(5)))) {
				occurrence();
			}
			else if ((LA(1)==HAT) && (_tokenSet_25.member(LA(2))) && (LA(3)==SEMICOLON||LA(3)==STOP||LA(3)==RIGHTBRACKET) && (_tokenSet_30.member(LA(4))) && (_tokenSet_24.member(LA(5)))) {
				item_identifier_add();
			}
			else if ((LA(1)==EQUALS) && (_tokenSet_25.member(LA(2))) && (LA(3)==SEMICOLON||LA(3)==STOP||LA(3)==RIGHTBRACKET) && (_tokenSet_30.member(LA(4))) && (_tokenSet_24.member(LA(5)))) {
				subject_locator_add();
			}
			else if ((_tokenSet_25.member(LA(1))) && (LA(2)==SEMICOLON||LA(2)==STOP||LA(2)==RIGHTBRACKET)) {
				subject_identifier_add();
			}
			else if ((LA(1)==IDENTIFIER) && (LA(2)==LEFTPAREN)) {
				template_invocation();
			}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void item_identifier() throws RecognitionException, TokenStreamException {
		
		
		match(HAT);
		iri_ref();
	}
	
	public final void subject_locator() throws RecognitionException, TokenStreamException {
		
		
		match(EQUALS);
		iri_ref();
	}
	
	public final void subject_identifier() throws RecognitionException, TokenStreamException {
		
		
		iri_ref();
	}
	
	public final void name() throws RecognitionException, TokenStreamException {
		
		
		if ( inputState.guessing==0 ) {
			topic_ref = null;
		}
		match(HYPHEN);
		{
		if ((_tokenSet_5.member(LA(1))) && (_tokenSet_27.member(LA(2)))) {
			topic_ref();
			match(COLON);
		}
		else if ((LA(1)==VARIABLE||LA(1)==SINGLE_QUOTED_STRING||LA(1)==TRIPLE_QUOTED_STRING) && (_tokenSet_31.member(LA(2)))) {
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		if ( inputState.guessing==0 ) {
			if (topic_ref == null)
			topic_ref = context.getTopicBySubjectIdentifier(PSI.getSAMNameType());
		}
		{
		switch ( LA(1)) {
		case SINGLE_QUOTED_STRING:
		case TRIPLE_QUOTED_STRING:
		{
			string();
			if ( inputState.guessing==0 ) {
				basic_literal.setLiteral(LT(0).getText());
				handler.startName(topic_ref, basic_literal);
			}
			break;
		}
		case VARIABLE:
		{
			match(VARIABLE);
			if ( inputState.guessing==0 ) {
				literal = current_template.getLiteralVariable(LT(0).getText());
				handler.startName(topic_ref, literal);
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
		switch ( LA(1)) {
		case AT:
		{
			scope();
			break;
		}
		case LEFTPAREN:
		case SEMICOLON:
		case STOP:
		case TILDE:
		case RIGHTBRACKET:
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
		case TILDE:
		{
			reifier();
			break;
		}
		case LEFTPAREN:
		case SEMICOLON:
		case STOP:
		case RIGHTBRACKET:
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
		_loop33:
		do {
			if ((LA(1)==LEFTPAREN)) {
				variant();
			}
			else {
				break _loop33;
			}
			
		} while (true);
		}
		if ( inputState.guessing==0 ) {
			handler.endName();
		}
	}
	
	public final void occurrence() throws RecognitionException, TokenStreamException {
		
		
		topic_ref();
		match(COLON);
		literal();
		if ( inputState.guessing==0 ) {
			handler.startOccurrence(topic_ref, literal);
		}
		{
		switch ( LA(1)) {
		case AT:
		{
			scope();
			break;
		}
		case SEMICOLON:
		case STOP:
		case TILDE:
		case RIGHTBRACKET:
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
		case TILDE:
		{
			reifier();
			break;
		}
		case SEMICOLON:
		case STOP:
		case RIGHTBRACKET:
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
	
	public final void instance_of() throws RecognitionException, TokenStreamException {
		
		
		match(ISA);
		topic_ref();
		if ( inputState.guessing==0 ) {
			handler.addTopicType(topic_ref);
		}
	}
	
	public final void item_identifier_add() throws RecognitionException, TokenStreamException {
		
		
		match(HAT);
		iri_ref();
		if ( inputState.guessing==0 ) {
			handler.addItemIdentifier(literal);
		}
	}
	
	public final void subject_locator_add() throws RecognitionException, TokenStreamException {
		
		
		match(EQUALS);
		iri_ref();
		if ( inputState.guessing==0 ) {
			handler.addSubjectLocator(literal);
		}
	}
	
	public final void subject_identifier_add() throws RecognitionException, TokenStreamException {
		
		
		iri_ref();
		if ( inputState.guessing==0 ) {
			handler.addSubjectIdentifier(literal);
		}
	}
	
	public final void a_kind_of() throws RecognitionException, TokenStreamException {
		
		
		match(AKO);
		topic_ref();
		if ( inputState.guessing==0 ) {
			handler.addSubtype(topic_ref);
		}
	}
	
	public final void scope() throws RecognitionException, TokenStreamException {
		
		
		match(AT);
		topic_ref();
		if ( inputState.guessing==0 ) {
			handler.addScopingTopic(topic_ref);
		}
		{
		_loop53:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				topic_ref();
				if ( inputState.guessing==0 ) {
					handler.addScopingTopic(topic_ref);
				}
			}
			else {
				break _loop53;
			}
			
		} while (true);
		}
	}
	
	public final void reifier() throws RecognitionException, TokenStreamException {
		
		
		match(TILDE);
		topic_ref();
		if ( inputState.guessing==0 ) {
			handler.addReifier(topic_ref);
		}
	}
	
	public final void variant() throws RecognitionException, TokenStreamException {
		
		
		match(LEFTPAREN);
		literal();
		if ( inputState.guessing==0 ) {
			handler.startVariant(literal);
		}
		scope();
		{
		switch ( LA(1)) {
		case TILDE:
		{
			reifier();
			break;
		}
		case RIGHTPAREN:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(RIGHTPAREN);
	}
	
	public final void literal() throws RecognitionException, TokenStreamException {
		
		
		{
		switch ( LA(1)) {
		case SINGLE_QUOTED_STRING:
		case TRIPLE_QUOTED_STRING:
		{
			string();
			if ( inputState.guessing==0 ) {
				literal = basic_literal;
				basic_literal.setLiteral(LT(0).getText());
				basic_literal.setDatatype(PSI.getXSDString());
			}
			{
			switch ( LA(1)) {
			case HATHAT:
			{
				match(HATHAT);
				if ( inputState.guessing==0 ) {
					tmp = (BasicLiteralGenerator) basic_literal.copyLiteral();
				}
				iri_ref();
				if ( inputState.guessing==0 ) {
					tmp.setDatatype(literal.getLocator()); 
					literal = tmp;
				}
				break;
			}
			case SEMICOLON:
			case STOP:
			case RIGHTPAREN:
			case COMMA:
			case AT:
			case TILDE:
			case RIGHTBRACKET:
			{
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
		case IRI:
		{
			match(IRI);
			if ( inputState.guessing==0 ) {
				literal = basic_literal;
				basic_literal.setLocator(getAbsoluteLocator());
			}
			break;
		}
		case WRAPPED_IRI:
		{
			match(WRAPPED_IRI);
			if ( inputState.guessing==0 ) {
				literal = basic_literal;
				basic_literal.setLocator(getRelativeLocator());
			}
			break;
		}
		case INTEGER:
		{
			match(INTEGER);
			if ( inputState.guessing==0 ) {
				literal = basic_literal;
				basic_literal.setLiteral(LT(0).getText());
				basic_literal.setDatatype(PSI.getXSDInteger());
			}
			break;
		}
		case DECIMAL:
		{
			match(DECIMAL);
			if ( inputState.guessing==0 ) {
				literal = basic_literal;
				basic_literal.setLiteral(LT(0).getText());
				basic_literal.setDatatype(PSI.getXSDDecimal());
			}
			break;
		}
		case DATE:
		{
			match(DATE);
			if ( inputState.guessing==0 ) {
				literal = basic_literal;
				basic_literal.setLiteral(LT(0).getText());
				basic_literal.setDatatype(PSI.getXSDDate());
			}
			break;
		}
		case DATETIME:
		{
			match(DATETIME);
			if ( inputState.guessing==0 ) {
				literal = basic_literal;
				basic_literal.setLiteral(LT(0).getText());
				basic_literal.setDatatype(PSI.getXSDDatetime());
			}
			break;
		}
		case VARIABLE:
		{
			match(VARIABLE);
			if ( inputState.guessing==0 ) {
				
				if (current_template == null)
				throw new InvalidTopicMapException("Variable $" + LT(0).getText() +
				" referenced outside template");
				literal = current_template.getLiteralVariable(LT(0).getText()); 
				
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
	
	public final void role() throws RecognitionException, TokenStreamException {
		
		
		topic_ref();
		if ( inputState.guessing==0 ) {
			type = topic_ref.copyTopic();
		}
		match(COLON);
		topic_ref();
		if ( inputState.guessing==0 ) {
			handler.addRole(type, topic_ref);
		}
		{
		switch ( LA(1)) {
		case TILDE:
		{
			reifier();
			break;
		}
		case RIGHTPAREN:
		case COMMA:
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
	
	public final void embedded_topic() throws RecognitionException, TokenStreamException {
		
		
		match(LEFTBRACKET);
		if ( inputState.guessing==0 ) {
			handler.startEmbeddedTopic();
		}
		property();
		{
		_loop58:
		do {
			if ((LA(1)==SEMICOLON) && (_tokenSet_15.member(LA(2))) && (_tokenSet_9.member(LA(3))) && (_tokenSet_24.member(LA(4))) && (_tokenSet_18.member(LA(5)))) {
				match(SEMICOLON);
				property();
			}
			else {
				break _loop58;
			}
			
		} while (true);
		}
		{
		switch ( LA(1)) {
		case SEMICOLON:
		{
			match(SEMICOLON);
			break;
		}
		case RIGHTBRACKET:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(RIGHTBRACKET);
		if ( inputState.guessing==0 ) {
			topic_ref = handler.endEmbeddedTopic();
		}
	}
	
	public final void argument() throws RecognitionException, TokenStreamException {
		
		
		{
		if ((LA(1)==IRI) && (LA(2)==RIGHTPAREN||LA(2)==COMMA) && (_tokenSet_32.member(LA(3))) && (_tokenSet_33.member(LA(4))) && (_tokenSet_24.member(LA(5)))) {
			match(IRI);
			if ( inputState.guessing==0 ) {
				
				parameters.add(new IRIAsArgumentGenerator(context, getAbsoluteLocator()));
				
			}
		}
		else if ((LA(1)==WRAPPED_IRI) && (LA(2)==RIGHTPAREN||LA(2)==COMMA) && (_tokenSet_32.member(LA(3))) && (_tokenSet_33.member(LA(4))) && (_tokenSet_24.member(LA(5)))) {
			match(WRAPPED_IRI);
			if ( inputState.guessing==0 ) {
				
				parameters.add(new IRIAsArgumentGenerator(context, getRelativeLocator()));
				
			}
		}
		else if ((LA(1)==QNAME) && (LA(2)==RIGHTPAREN||LA(2)==COMMA) && (_tokenSet_32.member(LA(3))) && (_tokenSet_33.member(LA(4))) && (_tokenSet_24.member(LA(5)))) {
			match(QNAME);
			if ( inputState.guessing==0 ) {
				
				parameters.add(new IRIAsArgumentGenerator(context, context.resolveQname(LT(0).getText())));
				
			}
		}
		else if ((_tokenSet_5.member(LA(1))) && (_tokenSet_34.member(LA(2))) && (_tokenSet_22.member(LA(3))) && (_tokenSet_18.member(LA(4))) && (_tokenSet_18.member(LA(5)))) {
			topic_ref();
			if ( inputState.guessing==0 ) {
				parameters.add(topic_ref.copyTopic());
			}
		}
		else if ((_tokenSet_35.member(LA(1))) && (LA(2)==RIGHTPAREN||LA(2)==COMMA||LA(2)==HATHAT) && (_tokenSet_32.member(LA(3))) && (_tokenSet_33.member(LA(4))) && (_tokenSet_24.member(LA(5)))) {
			literal();
			if ( inputState.guessing==0 ) {
				parameters.add(literal.copyLiteral());
			}
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"LEFTPAREN",
		"COLON",
		"ENCODING",
		"VERSION",
		"ONEOH",
		"PREFIX",
		"IDENTIFIER",
		"INCLUDE",
		"MERGEMAP",
		"SEMICOLON",
		"STOP",
		"VARIABLE",
		"WILDCARD",
		"NAMED_WILDCARD",
		"HAT",
		"EQUALS",
		"HYPHEN",
		"RIGHTPAREN",
		"ISA",
		"AKO",
		"COMMA",
		"AT",
		"TILDE",
		"LEFTBRACKET",
		"RIGHTBRACKET",
		"QNAME",
		"HATHAT",
		"IRI",
		"WRAPPED_IRI",
		"INTEGER",
		"DECIMAL",
		"DATE",
		"DATETIME",
		"DEF",
		"END",
		"SINGLE_QUOTED_STRING",
		"TRIPLE_QUOTED_STRING"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 6980338688L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 7128204288L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 1793834614322L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 1923054829106L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 1924145348146L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 7114556416L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { 7128187920L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 1785515189296L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = { 1924145331762L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = { 1656664073264L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	private static final long[] mk_tokenSet_10() {
		long[] data = { 1785615852592L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
	private static final long[] mk_tokenSet_11() {
		long[] data = { 1786706371632L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_11 = new BitSet(mk_tokenSet_11());
	private static final long[] mk_tokenSet_12() {
		long[] data = { 1785233114112L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_12 = new BitSet(mk_tokenSet_12());
	private static final long[] mk_tokenSet_13() {
		long[] data = { 145659764226L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_13 = new BitSet(mk_tokenSet_13());
	private static final long[] mk_tokenSet_14() {
		long[] data = { 1922970943026L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_14 = new BitSet(mk_tokenSet_14());
	private static final long[] mk_tokenSet_15() {
		long[] data = { 7128187904L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_15 = new BitSet(mk_tokenSet_15());
	private static final long[] mk_tokenSet_16() {
		long[] data = { 1656395654192L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_16 = new BitSet(mk_tokenSet_16());
	private static final long[] mk_tokenSet_17() {
		long[] data = { 2197932736050L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_17 = new BitSet(mk_tokenSet_17());
	private static final long[] mk_tokenSet_18() {
		long[] data = { 2199023255090L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_18 = new BitSet(mk_tokenSet_18());
	private static final long[] mk_tokenSet_19() {
		long[] data = { 1931273561136L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_19 = new BitSet(mk_tokenSet_19());
	private static final long[] mk_tokenSet_20() {
		long[] data = { 2061584278576L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_20 = new BitSet(mk_tokenSet_20());
	private static final long[] mk_tokenSet_21() {
		long[] data = { 283098711040L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_21 = new BitSet(mk_tokenSet_21());
	private static final long[] mk_tokenSet_22() {
		long[] data = { 2197848849970L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_22 = new BitSet(mk_tokenSet_22());
	private static final long[] mk_tokenSet_23() {
		long[] data = { 419785866802L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_23 = new BitSet(mk_tokenSet_23());
	private static final long[] mk_tokenSet_24() {
		long[] data = { 2197949513266L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_24 = new BitSet(mk_tokenSet_24());
	private static final long[] mk_tokenSet_25() {
		long[] data = { 6979354624L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_25 = new BitSet(mk_tokenSet_25());
	private static final long[] mk_tokenSet_26() {
		long[] data = { 2068712521266L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_26 = new BitSet(mk_tokenSet_26());
	private static final long[] mk_tokenSet_27() {
		long[] data = { 7128187936L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_27 = new BitSet(mk_tokenSet_27());
	private static final long[] mk_tokenSet_28() {
		long[] data = { 1785513092144L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_28 = new BitSet(mk_tokenSet_28());
	private static final long[] mk_tokenSet_29() {
		long[] data = { 1786689610800L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_29 = new BitSet(mk_tokenSet_29());
	private static final long[] mk_tokenSet_30() {
		long[] data = { 419799498290L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_30 = new BitSet(mk_tokenSet_30());
	private static final long[] mk_tokenSet_31() {
		long[] data = { 369123344L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_31 = new BitSet(mk_tokenSet_31());
	private static final long[] mk_tokenSet_32() {
		long[] data = { 2197816344066L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_32 = new BitSet(mk_tokenSet_32());
	private static final long[] mk_tokenSet_33() {
		long[] data = { 420873240114L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_33 = new BitSet(mk_tokenSet_33());
	private static final long[] mk_tokenSet_34() {
		long[] data = { 7147062272L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_34 = new BitSet(mk_tokenSet_34());
	private static final long[] mk_tokenSet_35() {
		long[] data = { 1784558944256L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_35 = new BitSet(mk_tokenSet_35());
	
	}
