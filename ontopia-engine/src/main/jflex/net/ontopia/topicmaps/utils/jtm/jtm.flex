//#! Ignore-License
/*
 * Copyright 2007 - 2009 Lars Heuer (heuer[at]semagia.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ontopia.topicmaps.utils.jtm;

/**
 * INTERNAL: JSON lexer for topic maps stored in JTM 1.0 notation.
 */
@SuppressWarnings("unused")
%%

%class JSONLexer

%unicode

%line 
%column

%pack

%int

%function token

%{

    public static final int EOF = YYEOF;

    private int _leftOffset;
    private int _rightOffset;

    public int getLine() {
        return yyline+1;
    }

    public int getColumn() {
        return yycolumn;
    }

    public String value() {
        return new String(zzBuffer, zzStartRead+_leftOffset, yylength()-_leftOffset-_rightOffset);
    }

    private int _token(final int type) {
        return _token(type, 0, 0);
    }

    private int _token(final int type, final int leftOffset, final int rightOffset) {
        _leftOffset = leftOffset;
        _rightOffset = rightOffset;
        return type;
    }
%}

LineTerminator  = \r|\n|\r\n
Whitespace      = {LineTerminator} | [ \t\f]

String          = \"([^\\\"]|(\\[\\\"rntu/]))*\"

%%

{Whitespace}        { /* noop */ }

<YYINITIAL> {
    "{"                 { return _token(JSONToken.START_OBJECT); }
    "}"                 { return _token(JSONToken.END_OBJECT); }
    "["                 { return _token(JSONToken.START_ARRAY); }
    "]"                 { return _token(JSONToken.END_ARRAY); }
    \"version\"         { return _token(JSONToken.KW_VERSION); }
    \"item_type\"       { return _token(JSONToken.KW_ITEM_TYPE); }
    \"topics\"          { return _token(JSONToken.KW_TOPICS); }
    \"associations\"    { return _token(JSONToken.KW_ASSOCIATIONS); }
    \"roles\"           { return _token(JSONToken.KW_ROLES); }
    \"occurrences\"     { return _token(JSONToken.KW_OCCURRENCES); }
    \"names\"           { return _token(JSONToken.KW_NAMES); }
    \"variants\"        { return _token(JSONToken.KW_VARIANTS); }
    \"scope\"           { return _token(JSONToken.KW_SCOPE); }
    \"type\"            { return _token(JSONToken.KW_TYPE); }
    \"player\"          { return _token(JSONToken.KW_PLAYER); }
    \"value\"           { return _token(JSONToken.KW_VALUE); }
    \"datatype\"        { return _token(JSONToken.KW_DATATYPE); }
    \"reifier\"         { return _token(JSONToken.KW_REIFIER); }
    \"parent\"          { return _token(JSONToken.KW_PARENT); }
    \"item_identifiers\" { return _token(JSONToken.KW_IIDS); }
    \"subject_identifiers\" { return _token(JSONToken.KW_SIDS); }
    \"subject_locators\" { return _token(JSONToken.KW_SLOS); }
    "null"              { return _token(JSONToken.VALUE_NULL); }
    {String}            { return _token(JSONToken.VALUE_STRING, 1, 1); }
    ","                 { return _token(JSONToken.COMMA); }
    ":"                 { return _token(JSONToken.COLON); }
}

.|[^]                    { throw new Error("Illegal character <" + yytext() + "> at line " + getLine() + " column: " + getColumn()); }

