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

import java.io.IOException;
import java.io.Reader;

/**
 * INTERNAL: JSON parser for topic maps stored in JTM 1.0 notation.
 */
final class JSONParser {

    private final JSONLexer _lexer;
    private int _current = -1;

    public JSONParser(final Reader reader) {
        _lexer = new JSONLexer(reader);
    }

    /**
     * Returns the next token.
     * 
     * Note: Colons and commas are omitted.
     *
     * @return The next token.
     * @throws IOException In case of an error.
     */
    public int nextToken() throws IOException {
        _current = _lexer.token();
        if (_current == JSONToken.COLON || _current == JSONToken.COMMA) {
            _current = _lexer.token();
        }
        return _current;
    }

    /**
     * Returns the current token.
     *
     * @return The current token.
     */
    public int getCurrentToken() {
        return _current;
    }

    /**
     * Returns the text associated to the current token.
     * 
     * The text is unescaped (if necessary) automatically.
     *
     * @return The unescaped text.
     */
    public String getText() {
        final String value = _lexer.value();
        int backSlash = value.indexOf('\\');
        if (backSlash == -1) {
            return value;
        }
        final StringBuilder sb = new StringBuilder();
        int pos = 0;
        int length = value.length();
        while (backSlash != -1) {
            sb.append(value.substring(pos, backSlash));
            if (backSlash + 1 >= length) {
                throw new IllegalArgumentException("Invalid escape syntax: " + value);
            }
            char c = value.charAt(backSlash + 1);
            if (c == 't') {
                sb.append('\t');
                pos = backSlash + 2;
            }
            else if (c == 'r') {
                sb.append('\r');
                pos = backSlash + 2;
            }
            else if (c == '/') {
                sb.append('/');
                pos = backSlash + 2;
            }
            else if (c == 'n') {
                sb.append('\n');
                pos = backSlash + 2;
            }
            else if (c == '"') {
                sb.append('"');
                pos = backSlash + 2;
            }
            else if (c == '\\') {
                sb.append('\\');
                pos = backSlash + 2;
            }
            else if (c == 'u') {
                // \\uxxxx
                if (backSlash + 5 >= length) {
                    throw new IllegalArgumentException(
                            "Incomplete Unicode escape sequence in: " + value);
                }
                String xx = value.substring(backSlash + 2, backSlash + 6);
                try {
                    c = (char)Integer.parseInt(xx, 16);
                    sb.append(c);
                    pos = backSlash + 6;
                }
                catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "Illegal Unicode escape sequence '\\u" + xx + "' in: " + value);
                }
            }
            else {
                throw new IllegalArgumentException("Unescaped backslash in: " + value);
            }
            backSlash = value.indexOf('\\', pos);
        }
        sb.append(value.substring(pos));
        return sb.toString();
    }

}
