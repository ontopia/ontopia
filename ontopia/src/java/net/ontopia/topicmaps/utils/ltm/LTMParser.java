// $ANTLR 2.7.7 (20060906): "ltm.g" -> "LTMParser.java"$
 package net.ontopia.topicmaps.utils.ltm; 
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
  
  import java.util.ArrayList;
  import java.util.Collection;
  import java.util.Collections;
  import java.util.HashMap;
  import java.util.HashSet;
  import java.util.List;
  import java.util.Map;
  import java.util.Set;
  import java.util.Iterator;
  import java.util.NoSuchElementException;
  
  import net.ontopia.infoset.core.LocatorIF;
  import net.ontopia.infoset.impl.basic.URILocator;
  import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
  import net.ontopia.topicmaps.core.*;
  import net.ontopia.topicmaps.xml.XTMTopicMapReader;
  import net.ontopia.topicmaps.xml.XTMContentHandler;
  import net.ontopia.topicmaps.xml.InvalidTopicMapException;
  import net.ontopia.topicmaps.utils.ltm.LTMTopicMapReader;
  import net.ontopia.topicmaps.utils.PSI;
  import net.ontopia.topicmaps.utils.MergeUtils;
  import net.ontopia.topicmaps.impl.utils.AbstractTopicMapReader;
  import net.ontopia.topicmaps.impl.utils.ReificationUtils;
  import net.ontopia.utils.ObjectUtils;
  import org.xml.sax.InputSource;
  import antlr.TokenStreamException;
  import antlr.SemanticException;

/**
 * INTERNAL: Parser for the LTM syntax.
 */
public class LTMParser extends antlr.LLkParser       implements LTMParserTokenTypes
 {

  private LocatorIF document;
  private LocatorIF base;
  private Map indicatorPrefixes;
  private Map locatorPrefixes;
  private TopicMapIF topicmap;
  private TopicMapBuilderIF builder;
  private TopicIF topic;
  private AssociationIF association;
  private List createdRoles;
  private Set alreadyLoaded;// the set of locators whose resources are loaded
  private boolean subordinate;//whether we were created via #MERGEMAP or #INC
  private List includedFrom; // the set of base locators to add srclocs for

  private TopicIF sort;
  private TopicIF display;
  private boolean seenBaseUri = false;

  // --- configuration interface

  public void setTopicMap(TopicMapIF topicmap) {
    this.topicmap = topicmap;
    builder = topicmap.getBuilder();
  }

  public void setBase(LocatorIF base) {
    this.document = base;
    this.base = base;
  }

  public void init() {
    createdRoles = new ArrayList();
    alreadyLoaded = new HashSet();
    alreadyLoaded.add(document); // don't want to read top document again
    includedFrom = new ArrayList();
    indicatorPrefixes = new HashMap();
    locatorPrefixes = new HashMap();
  }

  public Collection getCreatedRoles() {
    return createdRoles;
  }

  // only used for #MERGEMAP and #INCLUDE

  public void setAlreadyLoaded(Set alreadyLoaded) {
    this.alreadyLoaded = alreadyLoaded;
  }

  private void setIncludedFrom(List includedFrom) {
    this.includedFrom = includedFrom;
  }

  private void setSubordinate(boolean subordinate) {
    this.subordinate = subordinate;
  }

  // --- internal methods

  /**
   * Given the id (fragment identifier) of a topic, gets the
   * source-locator of that id, and looks for a topic with that SL.
   * If no such topic exists, one is created and given that SL.
   * @return the looked-up/created topic.
   * @param String id the fragment for which to find/create a topic.
   */
  private TopicIF getTopicById(String id) throws SemanticException {
    LocatorIF srcloc = document.resolveAbsolute('#' + id);
    TMObjectIF object = topicmap.getObjectByItemIdentifier(srcloc);
    if (object != null && !(object instanceof TopicIF))
      throw new SemanticException("Topic ID collides with ID of " + object);

    TopicIF topic = (TopicIF) object;
    if (topic == null) {
      LocatorIF[] locs = new LocatorIF[includedFrom.size()];

      // logic to add extra source locators for #INCLUDE
      for (int ix = 0; ix < locs.length; ix++) {
        LocatorIF incbase = (LocatorIF) includedFrom.get(ix);
        locs[ix] = incbase.resolveAbsolute('#' + id);

        TopicIF other = topicmap.getTopicBySubjectIdentifier(locs[ix]);
        if (other == null) {
          object = topicmap.getObjectByItemIdentifier(locs[ix]);
          if (object != null && !(object instanceof TopicIF))
            throw new SemanticException("Topic ID '" + id + 
            "' collides with ID of " + object);
          topic = (TopicIF) object;
        }
  
        if (other != null) {
          if (topic != null)
            topic = merge(topic, other);
          else
            topic = other;
        }
      }
  
      if (topic == null)
        topic = builder.makeTopic();
  
      topic.addItemIdentifier(srcloc);
      
      for (int ix = 0; ix < locs.length; ix++)
        topic.addItemIdentifier(locs[ix]);
    }
    return topic;
  }

  /** 
    * Given a subject indicator (SI), lookup topic with that SI.
    * If no such topic is found, create topic and add SI to it.
    * @ return the looked-up/created topic.
    */
  private TopicIF getTopicBySubjectIndicator(String subject) 
          throws SemanticException {
    LocatorIF locator;
    
    locator = makeURI(subject);
  
    TopicIF topic = topicmap.getTopicBySubjectIdentifier(locator);
    TMObjectIF obj = topicmap.getObjectByItemIdentifier(locator);
    if (topic != null && obj != null) {
      if (obj instanceof TopicIF && !obj.equals(topic))
        MergeUtils.mergeInto(topic, (TopicIF)obj);
    }
    if (topic == null && obj != null)
      topic = (TopicIF)obj;
    if (topic == null)
      topic = builder.makeTopic();
    topic.addSubjectIdentifier(locator);
    return topic;  
  }

  /** 
    * Given a subject locator (SL), lookup topic with that SL.
    * If no such topic is found, create topic and add SL.
    * @ return the looked-up/created topic.
    */
  private TopicIF getTopicBySubjectLocator(String subject) 
          throws SemanticException {
    LocatorIF locator;
     
    locator = makeURI(subject);
  
    TopicIF topic = topicmap.getTopicBySubjectLocator(locator);
    if (topic == null) {
      topic = builder.makeTopic();
      topic.addSubjectLocator(locator);
    }
    return topic;  
  }
  
  private LocatorIF makeURI(String uri) {
    String localURI = uri;
    
    if (localURI.startsWith("#")) //ISSUE: localURI.charAt(0) == '#' instead?
      return document.resolveAbsolute(localURI);
    else
      return base.resolveAbsolute(localURI);
  }

  private TopicIF getTopic(LocatorIF indicator) {
    TopicIF topic = topicmap.getTopicBySubjectIdentifier(indicator);
    if (topic == null) {
      TMObjectIF obj = topicmap.getObjectByItemIdentifier(indicator);
      if (obj instanceof TopicIF)
        topic = (TopicIF) obj;
    }
    if (topic == null) {
      topic = builder.makeTopic();
      topic.addSubjectIdentifier(indicator);
    }
    return topic;
  }
  
  private TopicIF getDisplayTopic() {
    if (display == null)
      display = getTopic(PSI.getXTMDisplay());
    return display;
  }

  private TopicIF getSortTopic() {
    if (sort == null)
      sort = getTopic(PSI.getXTMSort());
    return sort;
  }

  private void postProcess() {
    if (subordinate)
      return; // let the top-level parser do the post-processing

    TopicIF nullTopic = topicmap.getTopicBySubjectIdentifier(XTMContentHandler.nullPSI);

    Iterator it = createdRoles.iterator();
    while (it.hasNext()) {
      AssociationRoleIF role = (AssociationRoleIF) it.next();
      TopicIF roleType = role.getType();
      if (roleType != null && ObjectUtils.different(nullTopic, roleType)) continue;
      Iterator it2 = role.getPlayer().getTypes().iterator();
      if (it2.hasNext())
        role.setType((TopicIF) it2.next());
    }
    createdRoles = null;
    // remove null-topic if possible
    XTMContentHandler.removeNullTopic(topicmap);
  }
  
  /**
    * Takes a prefix:suffix combination and looks up the topic with
    * the subject locator or  subject indicator for that combination.
    * If the input is not on the form prefix:suffix, the topic is looked up
    * by id.
    * @param String the prefix:suffix or topic id to search for.
    */
  private TopicIF getTopicByName(String name) throws SemanticException {
    int colonIndex = name.indexOf(':');
    if (colonIndex == -1) 
      return getTopicById(name);
    
    String prefix = name.substring(0, colonIndex);
    String suffix = name.substring(colonIndex + 1);
    String mappedPrefix = (String) indicatorPrefixes.get(prefix);
    if (mappedPrefix == null) {
      mappedPrefix = (String)locatorPrefixes.get(prefix);
      if (mappedPrefix == null)
        throw new SemanticException("Unrecognized URI prefix: " + prefix);
      else // Found a locator prefix
        return getTopicBySubjectLocator(mappedPrefix + suffix);
    } else // Found a indicator prefix
      return getTopicBySubjectIndicator(mappedPrefix + suffix);
  }
  
  /**
    * Given a topicmap object and the name of a topic, reifies the topic map
    * with the given topic. 
    * @param TMObject reified - the object to reify.
    * @id The id (or other means of looking up) the reifying topic.
    */
  private void reify(ReifiableIF reifiable, String id) 
          throws SemanticException {
    notQname(id);
    TopicIF reifier = getTopicById(id);
    try {
      ReificationUtils.reify(reifiable, reifier);
    } catch (InvalidTopicMapException e) {
      throw new SemanticException(e.getMessage());
    }
  }
    

  private TopicIF addSubjectIdentifier(TopicIF topic, LocatorIF loc) {
    TopicIF other = topicmap.getTopicBySubjectIdentifier(loc);
    if (other == null) {
      TMObjectIF obj = topicmap.getObjectByItemIdentifier(loc);
      if (obj instanceof TopicIF)
        other = (TopicIF) obj;
    }

    if (other != null && topic == other)
      // the subject indicator of this topic points to the same topic
      // we solve this by doing nothing
      return topic;
    else if (other != null)
      return merge(topic, other);
    else {
      topic.addSubjectIdentifier(loc);
      return topic;
    }
  }

  private TopicIF addSubjectLocator(TopicIF topic, LocatorIF loc) {
    TopicIF other = topicmap.getTopicBySubjectLocator(loc);

    if (other == null)
      topic.addSubjectLocator(loc);
    else
      topic = merge(topic, other);

    return topic;
  }

  private TopicIF merge(TopicIF topic, TopicIF other) {
    if (topic == other)
      return topic;

    // other may be the sort or display topic, in which case we're
    // about to screw up that topic. we check for this and update the
    // references.
    if (other == sort)
      sort = topic;
    else if (other == display)
      display = topic;

    // get rid of object with lowest id
    if (topic.getObjectId().compareTo(other.getObjectId()) > 0) {
      MergeUtils.mergeInto(other, topic); // topic is now lost...
      return other;
    } else {
      MergeUtils.mergeInto(topic, other); // other is now lost...
      return topic;
    }
  }

  private void mergeInLTM(LocatorIF extloc, List includedFrom)
    throws RecognitionException, TokenStreamException {

    try {
      Reader reader = AbstractTopicMapReader.makeReader(
        new InputSource(extloc.getExternalForm()),
        new LTMEncodingSniffer());
      LTMParser parser = new LTMParser(new LTMLexer(reader));
      parser.setBase(extloc);
      parser.setTopicMap(topicmap);
      parser.setSubordinate(true);
      parser.init();
      parser.setAlreadyLoaded(alreadyLoaded); // or init() will override
      parser.setIncludedFrom(includedFrom);   // ditto
      parser.topicmap();
      createdRoles.addAll(parser.getCreatedRoles());
    } catch (IOException e) {
      throw new AntlrWrapException(e);
    }
  }

  private void mergeInOther(LocatorIF extloc, String syntax)
    throws AntlrWrapException {
    try {
      TopicMapReaderIF reader = null;
      if (syntax.equalsIgnoreCase("xtm"))
        reader = new XTMTopicMapReader(extloc.getAddress());

      MergeUtils.mergeInto(topicmap, reader.read());
    } catch (MalformedURLException e) {
      throw new AntlrWrapException(new IOException(
          "Invalid URI in MERGEMAP directive: " + e.getMessage()));
    } catch (IOException e) {
      throw new AntlrWrapException(e);
    }
  }
  
  private void addIndicatorPrefix(String key, String value) 
          throws SemanticException {
    if (indicatorPrefixes.containsKey(key) 
            || locatorPrefixes.containsKey(key))
      throw new SemanticException("The PREFIX: " + key 
              + " is already used.");
    indicatorPrefixes.put(key, value);
  }
  
  private void addLocatorPrefix(String key, String value) 
          throws SemanticException {
    if (indicatorPrefixes.containsKey(key) 
            || locatorPrefixes.containsKey(key))
      throw new SemanticException("The PREFIX: " + key 
              + " is already used.");
    locatorPrefixes.put(key, value);
  }
  
  private void notQname(String name) throws SemanticException {
    if (name.indexOf(':') != -1)
      throw new SemanticException("A colon was found in " + name
              + ". A colon may only be used in names to refer to prefixes"
              + " defined by #PREFIX, and such a reference is not allowed"
              + " here.");
  }

protected LTMParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public LTMParser(TokenBuffer tokenBuf) {
  this(tokenBuf,3);
}

protected LTMParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public LTMParser(TokenStream lexer) {
  this(lexer,3);
}

public LTMParser(ParserSharedInputState state) {
  super(state,3);
  tokenNames = _tokenNames;
}

	public final void topicmap() throws RecognitionException, TokenStreamException {
		
		
		{
		switch ( LA(1)) {
		case AT:
		{
			encodingDecl();
			break;
		}
		case EOF:
		case PREFIX:
		case NAME:
		case MERGEMAP:
		case VERSION:
		case INCLUDE:
		case BASEURI:
		case TOPICMAPID:
		case LBRACKET:
		case LCURLY:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		directives();
		{
		_loop4:
		do {
			switch ( LA(1)) {
			case LBRACKET:
			{
				topic();
				break;
			}
			case LCURLY:
			{
				occurrence();
				break;
			}
			case NAME:
			{
				association();
				break;
			}
			default:
			{
				break _loop4;
			}
			}
		} while (true);
		}
		match(Token.EOF_TYPE);
		postProcess();
	}
	
	public final void encodingDecl() throws RecognitionException, TokenStreamException {
		
		
		match(AT);
		match(STRING);
	}
	
	public final void directives() throws RecognitionException, TokenStreamException {
		
		
		{
		switch ( LA(1)) {
		case VERSION:
		{
			version();
			break;
		}
		case EOF:
		case PREFIX:
		case NAME:
		case MERGEMAP:
		case INCLUDE:
		case BASEURI:
		case TOPICMAPID:
		case LBRACKET:
		case LCURLY:
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
		_loop8:
		do {
			if ((_tokenSet_0.member(LA(1)))) {
				directive();
			}
			else {
				break _loop8;
			}
			
		} while (true);
		}
	}
	
	public final void topic() throws RecognitionException, TokenStreamException {
		
		
		match(LBRACKET);
		match(NAME);
		topic = getTopicByName(LT(0).getText());
		{
		switch ( LA(1)) {
		case COLON:
		{
			match(COLON);
			{
			int _cnt25=0;
			_loop25:
			do {
				if ((LA(1)==NAME)) {
					match(NAME);
					topic.addType(getTopicByName(LT(0).getText()));
				}
				else {
					if ( _cnt25>=1 ) { break _loop25; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt25++;
			} while (true);
			}
			break;
		}
		case AT:
		case PERCENT:
		case RBRACKET:
		case EQUALS:
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
		_loop27:
		do {
			if ((LA(1)==EQUALS)) {
				topname();
			}
			else {
				break _loop27;
			}
			
		} while (true);
		}
		{
		switch ( LA(1)) {
		case PERCENT:
		{
			match(PERCENT);
			{
			match(STRING);
			topic = addSubjectLocator(topic, makeURI(LT(0).getText()));
			}
			break;
		}
		case AT:
		case RBRACKET:
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
		_loop32:
		do {
			if ((LA(1)==AT)) {
				match(AT);
				{
				match(STRING);
				topic = addSubjectIdentifier(topic, makeURI(LT(0).getText()));
				}
			}
			else {
				break _loop32;
			}
			
		} while (true);
		}
		match(RBRACKET);
	}
	
	public final void occurrence() throws RecognitionException, TokenStreamException {
		
		
		match(LCURLY);
		match(NAME);
		topic = getTopicByName(LT(0).getText());
		match(COMMA);
		match(NAME);
		TopicIF type = getTopicByName(LT(0).getText());
		OccurrenceIF occ = builder.makeOccurrence(topic, type, "");
		match(COMMA);
		{
		switch ( LA(1)) {
		case STRING:
		{
			match(STRING);
			occ.setLocator(makeURI(LT(0).getText()));
			break;
		}
		case DATA:
		{
			match(DATA);
			occ.setValue(LT(0).getText());
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(RCURLY);
		{
		switch ( LA(1)) {
		case SLASH:
		{
			match(SLASH);
			{
			int _cnt51=0;
			_loop51:
			do {
				if ((LA(1)==NAME) && (_tokenSet_1.member(LA(2))) && (_tokenSet_2.member(LA(3)))) {
					match(NAME);
					occ.addTheme(getTopicByName(LT(0).getText()));
				}
				else {
					if ( _cnt51>=1 ) { break _loop51; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt51++;
			} while (true);
			}
			break;
		}
		case EOF:
		case NAME:
		case TILDE:
		case LBRACKET:
		case LCURLY:
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
			match(TILDE);
			match(NAME);
			reify(occ, LT(0).getText());
			break;
		}
		case EOF:
		case NAME:
		case LBRACKET:
		case LCURLY:
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
	
	public final void association() throws RecognitionException, TokenStreamException {
		
		
		match(NAME);
		topic = getTopicByName(LT(0).getText());
		association = builder.makeAssociation(topic);
		match(LPAREN);
		assocrole();
		{
		_loop55:
		do {
			if ((LA(1)==COMMA)) {
				match(COMMA);
				assocrole();
			}
			else {
				break _loop55;
			}
			
		} while (true);
		}
		match(RPAREN);
		{
		switch ( LA(1)) {
		case SLASH:
		{
			match(SLASH);
			{
			int _cnt58=0;
			_loop58:
			do {
				if ((LA(1)==NAME) && (_tokenSet_1.member(LA(2))) && (_tokenSet_2.member(LA(3)))) {
					match(NAME);
					association.addTheme(
					getTopicByName(LT(0).getText()));
				}
				else {
					if ( _cnt58>=1 ) { break _loop58; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt58++;
			} while (true);
			}
			break;
		}
		case EOF:
		case NAME:
		case TILDE:
		case LBRACKET:
		case LCURLY:
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
			match(TILDE);
			match(NAME);
			reify(association, LT(0).getText());
			break;
		}
		case EOF:
		case NAME:
		case LBRACKET:
		case LCURLY:
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
	
	public final void version() throws RecognitionException, TokenStreamException {
		
		
		match(VERSION);
		match(STRING);
	}
	
	public final void directive() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case MERGEMAP:
		{
			mergemap();
			break;
		}
		case BASEURI:
		{
			baseuri();
			break;
		}
		case TOPICMAPID:
		{
			topicmapid();
			break;
		}
		case INCLUDE:
		{
			include();
			break;
		}
		case PREFIX:
		{
			prefix();
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void mergemap() throws RecognitionException, TokenStreamException {
		
		
		match(MERGEMAP);
		match(STRING);
		
		LocatorIF extloc = makeURI(LT(0).getText());
		String syntax = "ltm";
		
		if (LT(1).getType() == LTMParserTokenTypes.NAME &&
		LT(2).getType() != LTMParserTokenTypes.LPAREN)
		throw new AntlrWrapException(new IOException(
		"#MERGEMAP requires a STRING, not a NAME as syntax name"));
		
		{
		switch ( LA(1)) {
		case STRING:
		{
			match(STRING);
			
			syntax = LT(0).getText();
			
			break;
		}
		case EOF:
		case PREFIX:
		case NAME:
		case MERGEMAP:
		case INCLUDE:
		case BASEURI:
		case TOPICMAPID:
		case LBRACKET:
		case LCURLY:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		
		if (alreadyLoaded.contains(extloc))
		return; // we already did this one
		alreadyLoaded.add(extloc);
		
		if (syntax.equalsIgnoreCase("ltm"))
		mergeInLTM(extloc, Collections.EMPTY_LIST);
		else if (syntax.equalsIgnoreCase("xtm"))
		mergeInOther(extloc, syntax);
		else
		throw new AntlrWrapException(new IOException(
		"Unsupported syntax: '" + syntax + "'"));
		
	}
	
	public final void baseuri() throws RecognitionException, TokenStreamException {
		
		
		match(BASEURI);
		match(STRING);
		
		if (seenBaseUri)
		throw new AntlrWrapException(new IOException(
		"Two BASEURI directives in a single file."));
		
		base = makeURI(LT(0).getText());
		seenBaseUri = true;
		
	}
	
	public final void topicmapid() throws RecognitionException, TokenStreamException {
		
		
		match(TOPICMAPID);
		{
		switch ( LA(1)) {
		case NAME:
		{
			match(NAME);
			notQname(LT(0).getText());
			if (!subordinate)
			topicmap.addItemIdentifier(makeURI('#' + LT(0).getText()));
			
			break;
		}
		case TILDE:
		{
			{
			match(TILDE);
			match(NAME);
			notQname(LT(0).getText());
			reify(topicmap, LT(0).getText());
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
	
	public final void include() throws RecognitionException, TokenStreamException {
		
		
		match(INCLUDE);
		match(STRING);
		
		LocatorIF extloc = makeURI(LT(0).getText());
		
		if (alreadyLoaded.contains(extloc))
		return; // we already did this one
		alreadyLoaded.add(extloc);
		
		List extlist = new ArrayList(includedFrom);
		extlist.add(document);
		mergeInLTM(extloc, extlist);
		
	}
	
	public final void prefix() throws RecognitionException, TokenStreamException {
		
		
		match(PREFIX);
		String prefixString;
		{
		match(NAME);
		prefixString = LT(0).getText(); 
		notQname(prefixString);
		}
		{
		switch ( LA(1)) {
		case AT:
		{
			match(AT);
			match(STRING);
			
			addIndicatorPrefix(prefixString, LT(0).getText());
			break;
		}
		case PERCENT:
		{
			match(PERCENT);
			match(STRING);
			
			addLocatorPrefix(prefixString, LT(0).getText());
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
	}
	
	public final void topname() throws RecognitionException, TokenStreamException {
		
		
		match(EQUALS);
		match(STRING);
		TopicNameIF bname = builder.makeTopicName(topic, LT(0).getText());
		{
		switch ( LA(1)) {
		case SEMICOL:
		{
			match(SEMICOL);
			{
			switch ( LA(1)) {
			case STRING:
			{
				match(STRING);
				VariantNameIF vn = builder.makeVariantName(bname, LT(0).getText());
				vn.addTheme(getSortTopic());
				break;
			}
			case AT:
			case PERCENT:
			case TILDE:
			case RBRACKET:
			case EQUALS:
			case SEMICOL:
			case SLASH:
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
			{
			switch ( LA(1)) {
			case SEMICOL:
			{
				match(SEMICOL);
				match(STRING);
				VariantNameIF vn = builder.makeVariantName(bname, LT(0).getText());
				vn.addTheme(getDisplayTopic());
				break;
			}
			case AT:
			case PERCENT:
			case TILDE:
			case RBRACKET:
			case EQUALS:
			case SLASH:
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
			break;
		}
		case AT:
		case PERCENT:
		case TILDE:
		case RBRACKET:
		case EQUALS:
		case SLASH:
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
		{
		switch ( LA(1)) {
		case SLASH:
		{
			match(SLASH);
			{
			int _cnt39=0;
			_loop39:
			do {
				if ((LA(1)==NAME)) {
					match(NAME);
					bname.addTheme(getTopicByName(LT(0).getText()));
				}
				else {
					if ( _cnt39>=1 ) { break _loop39; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt39++;
			} while (true);
			}
			break;
		}
		case AT:
		case PERCENT:
		case TILDE:
		case RBRACKET:
		case EQUALS:
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
		{
		switch ( LA(1)) {
		case TILDE:
		{
			match(TILDE);
			match(NAME);
			reify(bname, LT(0).getText());
			break;
		}
		case AT:
		case PERCENT:
		case RBRACKET:
		case EQUALS:
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
		{
		switch ( LA(1)) {
		case LPAREN:
		{
			{
			int _cnt46=0;
			_loop46:
			do {
				if ((LA(1)==LPAREN)) {
					match(LPAREN);
					match(STRING);
					VariantNameIF vname = builder.makeVariantName(bname, LT(0).getText());
					match(SLASH);
					{
					int _cnt44=0;
					_loop44:
					do {
						if ((LA(1)==NAME)) {
							match(NAME);
							vname.addTheme(getTopicByName(LT(0).getText()));
						}
						else {
							if ( _cnt44>=1 ) { break _loop44; } else {throw new NoViableAltException(LT(1), getFilename());}
						}
						
						_cnt44++;
					} while (true);
					}
					{
					switch ( LA(1)) {
					case TILDE:
					{
						match(TILDE);
						match(NAME);
						reify(vname, LT(0).getText());
						break;
					}
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
					match(RPAREN);
				}
				else {
					if ( _cnt46>=1 ) { break _loop46; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt46++;
			} while (true);
			}
			break;
		}
		case AT:
		case PERCENT:
		case RBRACKET:
		case EQUALS:
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
	
	public final void assocrole() throws RecognitionException, TokenStreamException {
		
		
		TopicIF player = null;
		TopicIF roletype = null;
		{
		switch ( LA(1)) {
		case LBRACKET:
		{
			topic();
			player = topic;
			break;
		}
		case NAME:
		{
			match(NAME);
			player = getTopicByName(LT(0).getText());
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
		case COLON:
		{
			match(COLON);
			match(NAME);
			roletype = getTopicByName(LT(0).getText());
			break;
		}
		case TILDE:
		case RPAREN:
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
		if (roletype == null) roletype = XTMContentHandler.getNullTopic(topicmap);
		AssociationRoleIF role = builder.makeAssociationRole(association, roletype, player);
		createdRoles.add(role);
		{
		switch ( LA(1)) {
		case TILDE:
		{
			match(TILDE);
			match(NAME);
			reify(role, LT(0).getText());
			break;
		}
		case RPAREN:
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
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"prefix directive",
		"NAME",
		"@",
		"STRING",
		"%",
		"mergemap directive",
		"version directive",
		"include directive",
		"base URI directive",
		"topic map directive",
		"~",
		"[",
		":",
		"]",
		"=",
		";",
		"/",
		"(",
		")",
		"{",
		",",
		"inline occurrence data",
		"}",
		"WS",
		"COMMENT"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 14864L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 8437794L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 10534946L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	
	}
