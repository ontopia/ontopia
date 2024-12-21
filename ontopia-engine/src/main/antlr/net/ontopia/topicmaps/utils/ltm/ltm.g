
header { package net.ontopia.topicmaps.utils.ltm; }

{
  import java.io.Reader;
  import java.io.IOException;
  import java.io.FileNotFoundException;
  import java.net.MalformedURLException;
  import java.net.URI;
  import java.net.URISyntaxException;
  import java.net.URL;
  import java.util.Arrays;
  import java.util.ArrayList;
  import java.util.Collection;
  import java.util.Collections;
  import java.util.HashMap;
  import java.util.HashSet;
  import java.util.List;
  import java.util.Map;
  import java.util.Objects;
  import java.util.Set;
  import java.util.Iterator;
  import net.ontopia.infoset.core.LocatorIF;
  import net.ontopia.topicmaps.core.AssociationIF;
  import net.ontopia.topicmaps.core.AssociationRoleIF;
  import net.ontopia.topicmaps.core.DuplicateReificationException;
  import net.ontopia.topicmaps.core.OccurrenceIF;
  import net.ontopia.topicmaps.core.ReifiableIF;
  import net.ontopia.topicmaps.core.TMObjectIF;
  import net.ontopia.topicmaps.core.TopicIF;
  import net.ontopia.topicmaps.core.TopicMapBuilderIF;
  import net.ontopia.topicmaps.core.TopicMapIF;
  import net.ontopia.topicmaps.core.TopicMapReaderIF;
  import net.ontopia.topicmaps.core.TopicNameIF;
  import net.ontopia.topicmaps.core.VariantNameIF;
  import net.ontopia.topicmaps.xml.XTMTopicMapReader;
  import net.ontopia.topicmaps.xml.XTMContentHandler;
  import net.ontopia.topicmaps.utils.PSI;
  import net.ontopia.topicmaps.utils.MergeUtils;
  import net.ontopia.topicmaps.impl.utils.AbstractTopicMapReader;
  import antlr.TokenStreamException;
  import antlr.SemanticException;
}


/**
 * INTERNAL: Parser for the LTM syntax.
 */

class LTMParser extends Parser;

options {
  defaultErrorHandler = false;
  k = 3;
}

{
  private LocatorIF document;
  private LocatorIF base;
  private URL url;
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

  public void setURL(URL url) {
    this.url = url;
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
    LocatorIF locator = makeURI(subject);
  
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
    LocatorIF locator = makeURI(subject);
  
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
      if (roleType != null && !Objects.equals(nullTopic, roleType)) continue;
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
    TopicIF reifier = getTopicByName(id);
    try {
      reifiable.setReifier(reifier);
    } catch (DuplicateReificationException e) {
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

    // get rid of object with lowest id
    if (topic.getObjectId().compareTo(other.getObjectId()) > 0) 
      return merge_(other, topic);
    else
      return merge_(topic, other);
  }

  private TopicIF merge_(TopicIF keeping, TopicIF losing) {
    // losing may be the sort or display topic, in which case we're
    // about to screw up that topic. we check for this and update the
    // references.
    if (losing == sort)
      sort = keeping;
    else if (losing == display)
      display = keeping;

    MergeUtils.mergeInto(keeping, losing);
    return keeping;
  }

  private void mergeInLTM(LocatorIF extloc, List includedFrom)
    throws RecognitionException, TokenStreamException {

    Reader reader = null;
    try {
      reader = AbstractTopicMapReader.makeReader(url, extloc, new LTMEncodingSniffer());
      LTMParser parser = new LTMParser(new LTMLexer(reader));
      parser.setBase(extloc);
      parser.setTopicMap(topicmap);
      parser.setSubordinate(true);
      parser.init();
      parser.setAlreadyLoaded(alreadyLoaded); // or init() will override
      parser.setIncludedFrom(includedFrom);   // ditto
      parser.topicmap();
      createdRoles.addAll(parser.getCreatedRoles());
    } catch (FileNotFoundException e) {
      throw new AntlrWrapException(new IOException("Could not find included file '" + extloc.getAddress() + "', referenced from " + (url != null ? url : base)));
    } catch (IOException e) {
      throw new AntlrWrapException(e);
    } catch (RecognitionException ex) {
      // need to catch the error here and rethrow in order to ensure that
      // error message points to correct file. see issue #143 on google code
      // https://github.com/ontopia/ontopia/issues/143
      throw new AntlrWrapException(new IOException(
          "Lexical error at " + extloc.getAddress() 
          + ":" + ex.line + ":" + ex.column + ": "+ ex.getMessage()));
    } finally {
      try {
        if (reader != null)
          reader.close();
      } catch (IOException e) {
        throw new AntlrWrapException(e);
      }
    }
  }

  private void mergeInOther(LocatorIF extloc, String syntax)
    throws AntlrWrapException {
    try {
      TopicMapReaderIF reader = null;
      if (syntax.equalsIgnoreCase("xtm"))
        reader = new XTMTopicMapReader(new URL(url, extloc.getAddress()));

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
}

topicmap :
  (encodingDecl)?
  directives
  (topic | occurrence | association)*
  EOF!
  { postProcess(); }
  ;

directives :
  (version)? (directive)*
  ;
  
directive : 
  mergemap | baseuri | topicmapid | include | prefix
  ;
 
prefix : 
  PREFIX { String prefixString; }
  (NAME { prefixString = LT(0).getText(); 
          notQname(prefixString);})
  (AT STRING { 
     addIndicatorPrefix(prefixString, LT(0).getText()); } 
  | PERCENT STRING { 
     addLocatorPrefix(prefixString, LT(0).getText()); })
  ;

mergemap :
  MERGEMAP STRING {
    LocatorIF extloc = makeURI(LT(0).getText());
    String syntax = "ltm";
  
    if (LT(1).getType() == LTMParserTokenTypes.NAME &&
        LT(2).getType() != LTMParserTokenTypes.LPAREN)
      throw new AntlrWrapException(new IOException(
        "#MERGEMAP requires a STRING, not a NAME as syntax name"));
  }
  (STRING {
    syntax = LT(0).getText();
  })?
  {
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
  };
    
version :
  VERSION STRING;

include :
  INCLUDE STRING {
    LocatorIF extloc = makeURI(LT(0).getText());

    if (alreadyLoaded.contains(extloc))
      return; // we already did this one
    alreadyLoaded.add(extloc);

    List extlist = new ArrayList(includedFrom);
    extlist.add(document);
    mergeInLTM(extloc, extlist);
  };

baseuri :
  BASEURI
  STRING {
    if (seenBaseUri)
      throw new AntlrWrapException(new IOException(
        "Two BASEURI directives in a single file."));

    base = makeURI(LT(0).getText());
    seenBaseUri = true;
  };

topicmapid :
  TOPICMAPID (NAME { notQname(LT(0).getText());
    if (!subordinate)
      topicmap.addItemIdentifier(makeURI('#' + LT(0).getText()));
  } | (TILDE NAME { reify(topicmap, LT(0).getText()); } ))
  ;

encodingDecl :
  AT
  STRING;
  
topic :
  LBRACKET
  NAME { topic = getTopicByName(LT(0).getText()); }
  (COLON (NAME { topic.addType(getTopicByName(LT(0).getText()));  } )+)?
  (topname)*
  (PERCENT (STRING { topic = addSubjectLocator(topic, makeURI(LT(0).getText()));}))?
  (AT (STRING { topic = addSubjectIdentifier(topic, makeURI(LT(0).getText())); }) )*
  RBRACKET
  ;

topname :
  EQUALS
  STRING 
    { TopicNameIF bname = builder.makeTopicName(topic, LT(0).getText()); }
  (SEMICOL (STRING
    { VariantNameIF vn = builder.makeVariantName(bname, LT(0).getText(), Arrays.asList(getSortTopic()));
      } )?
    (SEMICOL STRING
      { VariantNameIF vn = builder.makeVariantName(bname, LT(0).getText(), Arrays.asList(getDisplayTopic()));
         } )?
  )?
  (SLASH (NAME { bname.addTheme(getTopicByName(LT(0).getText())); } )+ )?
  (TILDE NAME { reify(bname, LT(0).getText()); } )?
  ((LPAREN STRING { VariantNameIF vname = builder.makeVariantName(bname, LT(0).getText(), Arrays.asList()); }
    SLASH (NAME { vname.addTheme(getTopicByName(LT(0).getText())); } )+
    (TILDE NAME { reify(vname, LT(0).getText()); } )?
    RPAREN)+ )?
  ;
 
occurrence :
  LCURLY
  NAME { topic = getTopicByName(LT(0).getText()); }
  COMMA
  NAME { TopicIF type = getTopicByName(LT(0).getText());
         OccurrenceIF occ = builder.makeOccurrence(topic, type, ""); }
  COMMA
  (STRING { occ.setLocator(makeURI(LT(0).getText())); } |
    DATA   { occ.setValue(LT(0).getText()); } )
  RCURLY
  (SLASH (NAME { occ.addTheme(getTopicByName(LT(0).getText())); } )+ )?
  (TILDE NAME { reify(occ, LT(0).getText()); } )?
  ;

association :
  NAME { topic = getTopicByName(LT(0).getText()); }
  { association = builder.makeAssociation(topic); }
  LPAREN
  assocrole ( COMMA assocrole )*
  RPAREN
  (SLASH (NAME { association.addTheme(
                         getTopicByName(LT(0).getText())); } )+ )?
  (TILDE NAME { reify(association, LT(0).getText()); } )?
  ;

assocrole :
  { TopicIF player = null;
    TopicIF roletype = null; }
  ( topic { player = topic; } 
  | NAME { player = getTopicByName(LT(0).getText()); } )
  ( COLON NAME { roletype = getTopicByName(LT(0).getText()); } )?
  { if (roletype == null) roletype = XTMContentHandler.getNullTopic(topicmap);
    AssociationRoleIF role = builder.makeAssociationRole(association, roletype, player);
    createdRoles.add(role); } 
    (TILDE NAME { reify(role, LT(0).getText()); } )?
  ;

/**
 * INTERNAL: Lexer for LTM syntax.
 */


class LTMLexer extends Lexer;

options {
  // can't include U+FFFF in the vocabulary, because antlr 2.7.1 uses it
  // to represent EOF...
  charVocabulary = '\1'..'\uFFFE';
  k = 2;
}

{  
  /**
    * Unescapes a given source string for unicode characters. 
    * Converts sequences of the form [BACKSLASH]uXXXX(XX) where the Xs are
    * hexadecimal digits and [BACKSLASH] is the actual character '\',
    * and the last two digits (in brackets) are optional.
    * E.g. converts \0041 to 'A' and \005c to '\'.
    * If the character is outside the range 0hex..FFFFhex,
    * it is converted to a surrogate pair, that represents the 'big' character.
    */
  private static String unescapeUnicode(String source) {
    String retVal = "";
    int charactersProcessed = 0;
    
    int i = source.indexOf("\\u");
    // For each potential unicode escape sequence:
    while (i != -1) {
      // Characters upto i contain no escaping. Add these as they are.
      retVal += source.substring(charactersProcessed, i);
      charactersProcessed = i;
      
      int unicodeValue = 0; // Used for String to hexadecimal conversion.
      int j;
      // Match up-to six hexadecimal digits, calculating the unicode char value.
      for (j = i + 2; j < i + 8 && j < source.length(); j++) {
        int charValue = hexValue(source.charAt(j));
        if (charValue == -1) break;
        unicodeValue = unicodeValue * 16 + charValue;
      } 
      
      // j is the index position in source of the first non-hexadecimal-digit.
      // If escape sequence is valid (contains at least 4 hexadecimal digits.
      if (j - i >= 6) {
        // Create the appropriate unicode character.
        if (unicodeValue < 65536) {// 65536 == 10000hex
          // Add the unicode character as it is.
          retVal += (char)unicodeValue;
        } else {
          // Create two surrogates to represent one big character.
          // Necessary for characters >= 10000hex (65536).
          
          // Calculation of the surrogates as follows: 
          // Let u = unicodeValue, x = highSurrogate, y = lowSurrogate, then:
          // u == 10000hex + 400hex(x - D800hex) + y - DC00hx
          // i.e. u == 65536 + 1024(x - 55296) + y - 56320
          // i.e. u == 1024x + y - 56613888
          // and range(y) is (DC00hex, DFFFhex), i.e. (56320, 57343)
          // ymin - ymax == 1023 < 1024
          // Therefore x == rounddown((u - ymin + 56613888) / 1024)
          // I.e. x == rounddown((u + 56557568) / 1024)
          // And hence y == u + 56613888 - 1024x
          int highSurrogate = (unicodeValue + 56557568) / 1024;
          int lowSurrogate = unicodeValue + 56613888 - 1024 * highSurrogate;
          retVal += (char)highSurrogate;
          retVal += (char)lowSurrogate;
        }
      } else {
        // Move past the string, which was not a valid unicode sequence.
        retVal += source.substring(charactersProcessed, j);
      }
      charactersProcessed = j;
      i = source.indexOf("\\u", charactersProcessed);
    }
    retVal += source.substring(charactersProcessed);
    return retVal;
  }
  
  /** 
    * @return the integer value of a hexadecimal character (0-9 | A-F | a-f).
    */
  private static int hexValue(char source) {
    if (in('a', source, 'f'))
      return 10 + source - 'a';
    else if (in('A', source, 'F'))
      return 10 + source - 'A';
    else if (in('0', source, '9'))
      return source - '0';
    else
      return -1;
  }
  
  /**
    * @return true iff source is in the interval min..max (inclusive).
    */
  private static boolean in(int min, int source, int max) {
    return min <= source && source <= max;
  }
  
  private static boolean isNameStart(char c) {
    return ('A' <= c && c <= 'Z')
            || ('a' <= c && c <= 'z')
            || c == '_';
  }
}

NAME :
 ('A'..'Z' | 'a'..'z' | '_')
 ('A'..'Z' | 'a'..'z' | '0'..'9' | '_' | '.' | '-')*
 ( { isNameStart(LA(2)) }? COLON 
 ('A'..'Z' | 'a'..'z' | '_')
 ('A'..'Z' | 'a'..'z' | '0'..'9' | '_' | '.' | '-')*)?
 ;

WS :
 (' ' | '\t' | '\n'  { newline(); } | '\r')
 { $setType(Token.SKIP); }
 ;

STRING :
 { boolean quoted = false; }
 '"' (~('"' | '\n') | '\n' { newline(); } | '"' '"' { quoted = true; } )* '"'
 {
   if (quoted)
     setText(org.apache.commons.lang3.StringUtils.replace(unescapeUnicode(new 
             String(text.getBuffer(), _begin+1, (text.length()-_begin)-2)), 
                     "\"\"", "\""));
   else
     setText(unescapeUnicode(new String(text.getBuffer(), _begin+1, (text.length()-_begin)-2))); 
 }
 ;
   
COMMENT :
 "/*" (~('*' | '\n') | { LA(2)!='/' }? '*' | '\n' { newline(); } )*  "*/"
 { $setType(Token.SKIP);  }
 ;

DATA
 options { paraphrase = "inline occurrence data"; } :
 "[[" (']' (~(']'|'\n') | '\n' {newline();}) |
       (~(']'|'\n') | '\n' {newline();}))* "]]"
 { setText(unescapeUnicode(new String(text.getBuffer(), _begin+2,
         (text.length()-_begin)-4))); }
 ;

TOPICMAPID
 options { paraphrase = "topic map directive"; } :
    "#" "TOPICMAP";

PREFIX
 options { paraphrase = "prefix directive"; } :
    "#" "PREFIX";

MERGEMAP
 options { paraphrase = "mergemap directive"; } :
    "#" "MERGEMAP";

VERSION
 options { paraphrase = "version directive"; } :
    "#" "VERSION";

INCLUDE
 options { paraphrase = "include directive"; } :
    "#" "INCLUDE";

BASEURI
 options { paraphrase = "base URI directive"; } :
    "#" "BASEURI";

LBRACKET options { paraphrase = "["; } : '[' ;
RBRACKET options { paraphrase = "]"; } : ']' ;
COLON    options { paraphrase = ":"; } : ':' ;
PERCENT  options { paraphrase = "%"; } : '%' ;
AT       options { paraphrase = "@"; } : '@' ;
EQUALS   options { paraphrase = "="; } : '=' ;
TILDE    options { paraphrase = "~"; } : '~' ;
SEMICOL  options { paraphrase = ";"; } : ';' ;
LCURLY   options { paraphrase = "{"; } : '{' ;
RCURLY   options { paraphrase = "}"; } : '}' ;
COMMA    options { paraphrase = ","; } : ',' ;
LPAREN   options { paraphrase = "("; } : '(' ;
RPAREN   options { paraphrase = ")"; } : ')' ;
SLASH    options { paraphrase = "/"; } : '/' ;
