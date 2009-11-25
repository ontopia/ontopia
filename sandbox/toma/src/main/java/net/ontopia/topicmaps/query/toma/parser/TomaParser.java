/**
 * Copyright (C) 2009 Space Applications Services
 *   <thomas.neidhart@spaceapplications.com>
 *
 * This file is part of the Ontopia project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ontopia.topicmaps.query.toma.parser;

import java.io.Reader;
import java.io.StringReader;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.parser.ast.TomaQuery;
import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.TokenStreamRecognitionException;

/**
 * INTERNAL: The TOMA query language parser. This is a convenient class and acts
 * as a wrapper to the auto-generated parser and lexer classes from the antlr
 * grammar.
 */
public class TomaParser {
  /**
   * INTERNAL: Parses a TOMA query. Depending on the parse context, a different
   * AST will be created.
   * 
   * @param query the TOMA query to be parsed.
   * @param context the parse context to be used.
   * @return the parsed query.
   * @throws InvalidQueryException if the query is syntactically wrong.
   */
  public static TomaQuery parse(String query, LocalParseContext context)
      throws InvalidQueryException {
    return parse(new StringReader(query), context);
  }

  private static TomaQuery parse(Reader queryReader, LocalParseContext context)
      throws InvalidQueryException {
    try {
      RealTomaParser parser = makeParser(queryReader, context);
      return parser.query();
    } catch (AntlrWrapException ex) {
      Exception e = ex.getException();
      if (e instanceof InvalidQueryException)
        throw (InvalidQueryException) e;
      else
        throw new InvalidQueryException(e);
    } catch (RecognitionException ex) {
      throw new InvalidQueryException("Lexical error at "
      /* + getBaseAddress().getAddress() + ":" */+ ex.line + ":" + ex.column
          + ": " + ex.getMessage());
    } catch (TokenStreamRecognitionException e) {
      RecognitionException ex = e.recog;
      throw new InvalidQueryException("Lexical error at "
      /* + getBaseAddress().getAddress() + ":" */+ ex.line + ":" + ex.column
          + ": " + ex.getMessage());
    } catch (TokenStreamIOException ex) {
      throw new InvalidQueryException(ex.io.toString());
    } catch (TokenStreamException ex) {
      throw new InvalidQueryException("Lexical error: " + ex.getMessage());
    }
  }

  // --- Internal methods

  /**
   * INTERNAL: create a new TOMA parser that internally contains the
   * auto-generated lexer and parser from the antlr grammar.
   */
  private static RealTomaParser makeParser(Reader reader,
      LocalParseContext context) throws InvalidQueryException {
    TomaLexer lexer = new TomaLexer(reader);
    RealTomaParser parser = new RealTomaParser(lexer);
    parser.init(context);
    return parser;
  }
}
