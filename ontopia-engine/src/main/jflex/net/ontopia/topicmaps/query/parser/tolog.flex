
package net.ontopia.topicmaps.query.parser;

import java.io.Reader;
import antlr.*;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

// To get ANTLR token types generated for parser.

/**
 * A tolog lexer, to be used with the Antlr-generated parser.
 */
%%
%public
%class TologLexer
%implements TokenStream
%type Token
%function nextToken2
%unicode            
%line
%column
%caseless
            //%debug

%{
  private TologOptions options;
  int commentNest = 0; // used to track comment nesting
  StringBuilder comment = new StringBuilder(); // collect comment text for parsing

  public TologLexer(Reader reader, TologOptions options) {
    this(reader);
    this.options = new TologOptions(options);
  }
  
  public TologOptions getOptions() {
    return options;
  }

  public Token nextToken() {
    try {
      return nextToken2();
    } catch (java.io.IOException e) {
      // FIXME: is this OK?
      return new CommonToken(Token.EOF_TYPE, "END OF FILE");
    }
  }
  
  private Token newToken(int type, String text) {
    CommonToken token = new CommonToken(type, text);
    token.setLine(yyline + 1);
    token.setColumn(yycolumn);
    return token;
  }

  private Token newToken(int type) {
    return newToken(type, yytext());
  }

  public int getStartOfToken() {
    return zzStartRead;
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
%}
            
LineTerminator = \r|\n|\r\n
WhiteSpace     = {LineTerminator} | [ \t\f]

ObjectId = "@" [A-Za-z0-9]*

Name = {NameStart} {NameChar}*
NameStart = [A-Za-z_]
NameChar = [-A-Za-z0-9_]| [\u00C0-\u00D6] | [\u00D8-\u00F6] 
                        | [\u00F8-\u02FF] | [\u0370-\u037D] 
                        | [\u037F-\u1FFF] | [\u200C-\u200D] 
                        | [\u2070-\u218F] | [\u2C00-\u2FEF] 
                        | [\u3001-\uD7FF] | [\uF900-\uFDCF] 
                        | [\uFDF0-\uFFFD] | \u00B7 
                        | [\u0300-\u036F] | [\u203F-\u2040]
Identifier = {Name} ( ":" {NameChar}+)?
Variable = "$" {Name}
Parameter = "%" {Name} "%"

UriString = "\"" [^\"]* "\""
SourceLoc = "s" {UriString}
Address   = "a" {UriString}
Indicator = "i" {UriString}
  
Number = (\-)? [0-9]+ ( "." [0-9]+ )?
PositiveInteger = [0-9]+

String = "\"" ([^\"] | "\"\"" )* "\""

%state YYINITIAL COMMENT
%%
<YYINITIAL> {
  "count"            { return newToken(RealTologParser.COUNT); }
  "order"            { return newToken(RealTologParser.ORDER); }
  "by"               { return newToken(RealTologParser.BY); }
  "select"           { return newToken(RealTologParser.SELECT); }
  "from"             { return newToken(RealTologParser.FROM); }
  "not"              { return newToken(RealTologParser.NOT); }
  "desc"             { return newToken(RealTologParser.DESC); }
  "asc"              { return newToken(RealTologParser.ASC); }
  "limit"            { return newToken(RealTologParser.LIMIT); }
  "offset"           { return newToken(RealTologParser.OFFSET); }
  "using"            { return newToken(RealTologParser.USING); }
  "for"              { return newToken(RealTologParser.FOR); }
  "as"               { return newToken(RealTologParser.AS); }
  "import"           { return newToken(RealTologParser.IMPORT); }

  "delete"           { return newToken(RealTologParser.DELETE); }
  "merge"            { return newToken(RealTologParser.MERGE); }
  "update"           { return newToken(RealTologParser.UPDATE); }
  "insert"           { return newToken(RealTologParser.INSERT); }

  ":"                { return newToken(RealTologParser.COLON); }
  "?"                { return newToken(RealTologParser.QUESTIONM); }
  ","                { return newToken(RealTologParser.COMMA); }
  "("                { return newToken(RealTologParser.LPAREN); }
  ")"                { return newToken(RealTologParser.RPAREN); }
  "."                { return newToken(RealTologParser.PERIOD); }
  ":-"               { return newToken(RealTologParser.CONNECT); }
  "{"                { return newToken(RealTologParser.LCURLY); }
  "}"                { return newToken(RealTologParser.RCURLY); }
  "|"                { return newToken(RealTologParser.PIPE); }
  "||"               { return newToken(RealTologParser.DOUBLEPIPE); }
  "/="               { return newToken(RealTologParser.NOTEQUALS); }
  "="                { return newToken(RealTologParser.EQUALS); }
  "<"                { return newToken(RealTologParser.LESSTHAN); }
  ">"                { return newToken(RealTologParser.GREATERTHAN); }
  "<="               { return newToken(RealTologParser.LESSTHANEQ); }
  ">="               { return newToken(RealTologParser.GREATERTHANEQ); }
  
  {PositiveInteger}  { return newToken(RealTologParser.POSITIVEINTEGER); }
  {ObjectId}         {
    return newToken(RealTologParser.OBJID, yytext().substring(1));
  }
  {Identifier}       { return newToken(RealTologParser.IDENT); }
  {Variable}         { return newToken(RealTologParser.VARIABLE); }
  {Parameter}        {
    int len = yytext().length();
    return newToken(RealTologParser.PARAMETER, yytext().substring(1, len - 1));
  }
  {Number}           { return newToken(RealTologParser.NUMBER); }
  {SourceLoc}        {
    int len = yytext().length();
    return newToken(RealTologParser.SOURCELOC, yytext().substring(2, len - 1));
  }
  {Indicator}        {
    int len = yytext().length();
    return newToken(RealTologParser.INDICATOR, yytext().substring(2, len - 1));
  }
  {Address}          {
    int len = yytext().length();
    return newToken(RealTologParser.ADDRESS, yytext().substring(2, len - 1));
  }
  {String}           {
    String str = yytext();
    str = str.substring(1, str.length() - 1);
    str = org.apache.commons.lang3.StringUtils.replace(str, "\"\"", "\"");
    return newToken(RealTologParser.STRING, str);
  }
  
  {WhiteSpace}       { /* ignore */ }

  "/*" {
    commentNest++;
    yybegin(COMMENT);
  }
}

<COMMENT> {
  "/*"   { commentNest++; }
  "*/"   {
    commentNest--;
    if (commentNest == 0) {
      yybegin(YYINITIAL);
      parse(comment.toString());
      comment.setLength(0);
    }
  }
  .      { comment.append(yytext()); }
  "\n"   { comment.append(yytext()); }
}

/* error fallback */
.|[^] {
  throw new JFlexWrapException(
    new InvalidQueryException("Illegal character <"+yytext()+"> at " +
                              (yyline+1) + ":" + yycolumn)); }
<<EOF>> { return newToken(RealTologParser.EOF); }
