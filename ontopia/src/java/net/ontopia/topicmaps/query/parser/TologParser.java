
// $Id: TologParser.java,v 1.16 2005/12/14 13:08:35 grove Exp $

package net.ontopia.topicmaps.query.parser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.TokenStreamRecognitionException;

/**
 * INTERNAL: The tolog query parser.
 */
public class TologParser {
  protected ParseContextIF context;
  protected TologOptions options;

  public TologParser(ParseContextIF context, TologOptions options) {
    this.context = context;
    this.options = options;
  }

  public TologQuery parse(String query) throws InvalidQueryException {
    return parse(new StringReader(query));
  }
  
  public TologQuery parse(Reader queryReader) throws InvalidQueryException {
    try {
      RealTologParser parser = makeParser(queryReader);
      parser.setContext(new LocalParseContext(context));
      parser.query();
      return parser.getQuery();
    }
    catch (AntlrWrapException ex) {
      Exception e = ex.getException();
      if (e instanceof InvalidQueryException)
        throw (InvalidQueryException)e;
      else 
        throw new InvalidQueryException(e);
    }
    catch (RecognitionException ex) {
      throw new InvalidQueryException("Lexical error at " /*+ getBaseAddress().getAddress() + ":"*/ + ex.line + ":" + ex.column + ": "+ ex.getMessage());
    }
    catch (TokenStreamRecognitionException e) {
      RecognitionException ex = e.recog;
      throw new InvalidQueryException("Lexical error at " /*+ getBaseAddress().getAddress() + ":"*/ + ex.line + ":" + ex.column + ": "+ ex.getMessage());
    }
    catch (TokenStreamIOException ex) {
      throw new InvalidQueryException(ex.io.toString());
    }
    catch (TokenStreamException ex) {
      throw new InvalidQueryException("Lexical error: " + ex.getMessage());
    }
  }

  public ParseContextIF parseDeclarations(String decls) throws InvalidQueryException {
    try {
      ParseContextIF ctxt = new LocalParseContext(context);
      RealTologParser parser = makeParser(new StringReader(decls));
      parser.setContext(ctxt);
      parser.declarations();
      return ctxt;
    }
    catch (AntlrWrapException ex) {
      Exception e = ex.getException();
      if (e instanceof InvalidQueryException)
        throw (InvalidQueryException)e;
      else 
        throw new InvalidQueryException(e);
    }
    catch (RecognitionException ex) {
      throw new InvalidQueryException("Lexical error at " /*+ getBaseAddress().getAddress() + ":"*/ + ex.line + ":" + ex.column + ": "+ ex.getMessage());
    }
    catch (TokenStreamRecognitionException e) {
      RecognitionException ex = e.recog;
      throw new InvalidQueryException("Lexical error at " /*+ getBaseAddress().getAddress() + ":"*/ + ex.line + ":" + ex.column + ": "+ ex.getMessage());
    }
    catch (TokenStreamIOException ex) {
      throw new InvalidQueryException(ex.io.toString());
    }
    catch (TokenStreamException ex) {
      throw new InvalidQueryException("Lexical error: " + ex.getMessage());
    }
  }  

  public void load(String ruleset) throws InvalidQueryException {
    load(new StringReader(ruleset));
  }

  public void load(Reader reader) throws InvalidQueryException {
    try {
      RealTologParser parser = makeParser(reader);
      parser.setContext(context);
      parser.ruleset();
    }
    catch (AntlrWrapException ex) {
      Exception e = ex.getException();
      if (e instanceof InvalidQueryException)
        throw (InvalidQueryException)e;
      else 
        throw new InvalidQueryException(e);
    }
    catch (RecognitionException ex) {
      throw new InvalidQueryException("Lexical error at " /*+ getBaseAddress().getAddress() + ":"*/ + ex.line + ":" + ex.column + ": "+ ex.getMessage());
    }
    catch (TokenStreamRecognitionException e) {
      RecognitionException ex = e.recog;
      throw new InvalidQueryException("Lexical error at " /*+ getBaseAddress().getAddress() + ":"*/ + ex.line + ":" + ex.column + ": "+ ex.getMessage());
    }
    catch (TokenStreamIOException ex) {
      throw new InvalidQueryException(ex.io.toString());
    }
    catch (TokenStreamException ex) {
      throw new InvalidQueryException("Lexical error: " + ex.getMessage());
    }
  }

  public ParseContextIF getContext() {
    return context;
  }
  
  // --- Internal methods

  private RealTologParser makeParser(Reader reader) throws InvalidQueryException {
    TologLexer lexer = new TologLexer(reader, options);
    RealTologParser parser = new RealTologParser(lexer);
    parser.init(lexer);
    return parser;
  }
}
