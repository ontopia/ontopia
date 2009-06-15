
// $Id: ctm.flex,v 1.2 2009/04/27 11:04:17 lars.garshol Exp $

package net.ontopia.topicmaps.utils.ctm;

import antlr.*;
import net.ontopia.topicmaps.xml.InvalidTopicMapException;

// To get ANTLR token types generated for parser.

/**
 * A CTM lexer, to be used with the Antlr-generated parser.
 */
%%
%public
%class CTMLexer
%implements TokenStream
%type Token
%function nextToken2
%unicode            
%line
%column

%{
  private StringBuffer string = new StringBuffer(); // used to gather strings
  
  private String docuri;
  public void setDocuri(String docuri) {
    this.docuri = docuri;
  }
  
  public Token nextToken() {
    try {
      return nextToken2();
    } catch (java.io.IOException e) {
      // FIXME: is this OK?
      return new CommonToken(Token.EOF_TYPE, "END OF FUCKING FILE, OK?");
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

  private char unhex(int length) {
    int number = 0;
    for (int ix = 0; ix < length; ix++) {
      int digit;
      char ch = yycharat(2 + ix);
      if (ch >= '0' && ch <= '9')
        digit = ch - '0';
      else if (ch >= 'a' && ch <= 'f')
        digit = (ch - 'a') + 10;
      else if (ch >= 'A' && ch <= 'F')
        digit = (ch - 'A') + 10;
      else
        throw new InvalidTopicMapException("Invalid escape sequence: '" +
                                           yytext() + "'");
    
      number = number * 16 + digit;
    }
    return (char) number;
  }  
%}
            
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = {LineTerminator} | [ \t\f]

/* comments */
Comment = {EndOfLineComment} | {MultilineComment}
EndOfLineComment     = "#" {InputCharacter}* {LineTerminator}
MultilineComment     = "#(" ([^)]* [^#])*  ")#"

/* identifiers */
QName = {Identifier} ":" ([0-9]+ {NamePart}* | {Identifier})
Identifier = {NameStart} ("."* {NamePart}+)*
NameStart = [A-Za-z_]
NamePart = {NameStart} | "-" | [0-9]

Variable = "$" {Identifier}

/* IRIs */
IRI = [a-z]+ "://" (("." | ";")* [^ \r\n\f\t;.(\]]+)*
WrappedIRI = "<" [^>]+ ">"

/* string 
   SimpleString = "\"" [^\"]* "\""*/
TripleString = "\"\"\"" ("\""? "\""? [^\"])* "\"\"\""
Hexdigit = [A-Fa-f0-9]
                       
/* number */
Digit = [0-9]
Sign = "+" | "-"                                             
Integer = {Sign}? {Digit}+
Decimal = {Sign}? {Digit}+ "." {Digit}+

/* dates and times */
Date = {Digit}{Digit}{Digit}{Digit}"-"{Digit}{Digit}"-"{Digit}{Digit}
DateTime = {Date} "T" {Digit}{Digit}":"{Digit}{Digit}":"{Digit}{Digit}

%state STRING
%%
<YYINITIAL> {
  "1.0"            { return newToken(CTMParser.ONEOH); }
  "%encoding"      { return newToken(CTMParser.ENCODING); }
  "%version"       { return newToken(CTMParser.VERSION); }
  "%prefix"        { return newToken(CTMParser.PREFIX); }
  "%include"       { return newToken(CTMParser.INCLUDE); }
  "%mergemap"      { return newToken(CTMParser.MERGEMAP); }
  "def"            { return newToken(CTMParser.DEF); }
  "end"            { return newToken(CTMParser.END); }
  "isa"            { return newToken(CTMParser.ISA); }
  "ako"            { return newToken(CTMParser.AKO); }
  {QName}          { return newToken(CTMParser.QNAME); }
  {Identifier}     { return newToken(CTMParser.IDENTIFIER); }
  {Variable}       { return newToken(CTMParser.VARIABLE); }
  "-"              { return newToken(CTMParser.HYPHEN); }
  "."              { return newToken(CTMParser.STOP); }
  ";"              { return newToken(CTMParser.SEMICOLON); }
  ","              { return newToken(CTMParser.COMMA); }
  "@"              { return newToken(CTMParser.AT); }
  ":"              { return newToken(CTMParser.COLON); }
  "="              { return newToken(CTMParser.EQUALS); }
  "("              { return newToken(CTMParser.LEFTPAREN); }
  ")"              { return newToken(CTMParser.RIGHTPAREN); }
  "~"              { return newToken(CTMParser.TILDE); }
  "["              { return newToken(CTMParser.LEFTBRACKET); }
  "]"              { return newToken(CTMParser.RIGHTBRACKET); }
  "^"              { return newToken(CTMParser.HAT); }
  "^^"             { return newToken(CTMParser.HATHAT); }
  "?"              { return newToken(CTMParser.WILDCARD); }
  "?" {Identifier} { return newToken(CTMParser.NAMED_WILDCARD,
                                     yytext().substring(1)); }
  {Integer}        { return newToken(CTMParser.INTEGER); }
  {Decimal}        { return newToken(CTMParser.DECIMAL); }
  {Date}           { return newToken(CTMParser.DATE); }
  {DateTime}       { return newToken(CTMParser.DATETIME); }
  /*  {SimpleString}   { return newToken(CTMParser.SINGLE_QUOTED_STRING,
      yytext().substring(1, yylength() - 1)); }*/
  \"               { string.setLength(0); // empty string buffer
                     yybegin(STRING); }
  {TripleString}   { return newToken(CTMParser.TRIPLE_QUOTED_STRING,
                                     yytext().substring(3, yylength() - 3)); }
  {IRI}            { return newToken(CTMParser.IRI); }
  {WrappedIRI}     { return newToken(CTMParser.WRAPPED_IRI,
                                     yytext().substring(1, yylength() - 1)); }
  {Comment}        { /* ignore */ }
  {WhiteSpace}     { /* ignore */ }
}

<STRING> {
  \" { yybegin(YYINITIAL);
       return newToken(CTMParser.SINGLE_QUOTED_STRING, string.toString()); }
  [^\"\\] { string.append(yycharat(0)); }
  \\\"    { string.append('"'); }
  \\\\    { string.append('\\'); }
  \\n     { string.append((char) 10); }
  \\r     { string.append((char) 13); }
  \\t     { string.append((char) 9); }
  \\u{Hexdigit}{Hexdigit}{Hexdigit}{Hexdigit}
          { string.append(unhex(4)); }
  \\U{Hexdigit}{Hexdigit}{Hexdigit}{Hexdigit}{Hexdigit}{Hexdigit}
          { string.append(unhex(6)); }
}

/* error fallback */
.|\n {
  throw new InvalidTopicMapException("Illegal character <"+yytext()+"> at " +
                                     docuri + ":" + (yyline+1) + ":" + yycolumn); }
<<EOF>> { return newToken(CTMParser.EOF); }                                   