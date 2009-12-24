
// $Id$

package net.ontopia.topicmaps.query.parser;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.io.Reader;
import java.io.IOException;
import java.io.StringReader;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.ctm.CTMLexer;
import net.ontopia.topicmaps.utils.ctm.CTMParser;
import net.ontopia.topicmaps.utils.ctm.Template;
import net.ontopia.topicmaps.utils.ctm.BuilderEventHandler;
import net.ontopia.topicmaps.utils.ctm.TemplateEventHandler;
import net.ontopia.topicmaps.utils.ctm.ValueGeneratorIF;
import net.ontopia.topicmaps.utils.ctm.ValueGenerator;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;

import antlr.RecognitionException;
import antlr.TokenStreamRecognitionException;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;

/**
 * INTERNAL: Represents a parsed INSERT statement.
 */
public class InsertStatement extends ModificationStatement {
  private Template template;
  private net.ontopia.topicmaps.utils.ctm.ParseContextIF context;
  private List<String> parameters; // the order of the parameters to the CTM part

  public InsertStatement() {
    super();
  }

  public int doStaticUpdates(TopicMapIF topicmap, Map arguments)
    throws InvalidQueryException {
    BuilderEventHandler handler =
      new BuilderEventHandler(topicmap.getBuilder(), context);
    template.invoke(Collections.EMPTY_LIST, handler);
    return 1;
  }

  public int doUpdates(QueryMatches matches)
    throws InvalidQueryException {
    TopicMapIF topicmap = matches.getQueryContext().getTopicMap();
    BuilderEventHandler handler =
      new BuilderEventHandler(topicmap.getBuilder(), context);

    // the argument values to be passed to the template
    List arguments = new ArrayList(matches.colcount);
    for (int ix = 0; ix < matches.colcount; ix++)
      arguments.add(null); // making a slot for value filled in later

    // a mapping from the column indexes in matches to the corresponding
    // parameter indexes in the arguments list
    int[] colix = new int[parameters.size()];
    for (int ix = 0; ix < parameters.size(); ix++) {
      String name = parameters.get(ix).substring(1); // remove the $
      colix[ix] = matches.getVariableIndex(name);
    }
    
    for (int row = 0; row <= matches.last; row++) {
      for (int ix = 0; ix < parameters.size(); ix++)
        arguments.set(ix, makeGenerator(matches.data[row][colix[ix]]));
      template.invoke(arguments, handler);
    }
    
    return matches.last + 1;
  }

  public String toString() {
    String str = "insert ..."; // FIXME: stringfy CTM part
    if (query != null)
      str += "\nfrom" + query.toStringFromPart();
    return str;
  }
  
  public void setCTMPart(String ctm, ParseContextIF context)
    throws InvalidQueryException {

    if (query == null)
      parameters = Collections.EMPTY_LIST;
    else {
      String[] varnames = query.getSelectedVariableNames();
      parameters = new ArrayList<String>(varnames.length);
      for (int ix = 0; ix < varnames.length; ix++)
        parameters.add("$" + varnames[ix]);
    }
    
    try {
      // see ctm.g tolog_insert comment for why we add an "end"
      Reader reader = new StringReader(ctm + " end");
      CTMLexer lexer = new CTMLexer(reader);
      CTMParser parser = new CTMParser(lexer);
      parser.init();
      parser.setBase(context.getTopicMap().getStore().getBaseAddress());
      parser.setTopicMap(context.getTopicMap());
      TemplateEventHandler handler = 
        new TemplateEventHandler(null, parameters, null);
      this.context = new CTMParseContext(context, parser.getContext());
      parser.setHandler(handler, this.context);
      parser.tolog_insert();
      template = handler.getTemplate();
      
    } catch (AntlrWrapException ex) {
      throw new InvalidQueryException("IO exception: " + ex.getException());
    } catch (RecognitionException ex) {
      throw new InvalidQueryException("Lexical error at :" +
                            ex.line + ":" + ex.column + ": "+ ex.getMessage());
    } catch (TokenStreamRecognitionException e) {
      RecognitionException ex = e.recog;
      throw new InvalidQueryException("Lexical error at:" +
                            ex.line + ":" + ex.column + ": "+ ex.getMessage());
    } catch (TokenStreamIOException ex) {
      throw new InvalidQueryException("IO exception: " + ex.io);
    } catch (TokenStreamException ex) {
      throw new InvalidQueryException("Lexical error: " + ex.getMessage());
    }
  }

  private static ValueGeneratorIF makeGenerator(Object value) {
    if (value instanceof TopicIF)
      return new ValueGenerator((TopicIF) value, null, null, null);
    else if (value instanceof String)
      return new ValueGenerator(null, (String) value, DataTypes.TYPE_STRING,
                                null);
    else
      throw new OntopiaRuntimeException("Can't make generator for " + value);
  }

  // --- CTM parse context wrapping tolog parse context

  static class CTMParseContext
    implements net.ontopia.topicmaps.utils.ctm.ParseContextIF {
    private ParseContextIF tologctx;
    private net.ontopia.topicmaps.utils.ctm.ParseContextIF ctmctx;

    private CTMParseContext(ParseContextIF tologctx,
                            net.ontopia.topicmaps.utils.ctm.ParseContextIF ctmctx) {
      this.tologctx = tologctx;
      this.ctmctx = ctmctx;
    }

    public void addPrefix(String prefix, LocatorIF locator) {
      throw new UnsupportedOperationException();
    }

    public void addIncludeUri(LocatorIF uri) {
      throw new UnsupportedOperationException();
    }
  
    public Set getIncludeUris() {
      throw new UnsupportedOperationException();
    }
  
    public LocatorIF resolveQname(String qname) {
      return tologctx.resolveQName(new QName(qname));
    }

    public ValueGeneratorIF getTopicById(String id) {
      return ctmctx.getTopicById(id);
    }
  
    public ValueGeneratorIF getTopicByItemIdentifier(LocatorIF itemid) {
      throw new UnsupportedOperationException();
    }

    public ValueGeneratorIF getTopicBySubjectLocator(LocatorIF subjloc) {
      throw new UnsupportedOperationException();
    }

    public ValueGeneratorIF getTopicBySubjectIdentifier(LocatorIF subjid) {
      return ctmctx.getTopicBySubjectIdentifier(subjid);
    }

    public ValueGeneratorIF getTopicByQname(String qname) {
      throw new UnsupportedOperationException();
    }

    public TopicIF makeTopicById(String id) {
      return ctmctx.makeTopicById(id);
    }
    
    public TopicIF makeTopicByItemIdentifier(LocatorIF itemid) {
      return ctmctx.makeTopicByItemIdentifier(itemid);
    }

    public TopicIF makeTopicBySubjectLocator(LocatorIF subjloc) {
      return ctmctx.makeTopicBySubjectLocator(subjloc);
    }

    public TopicIF makeTopicBySubjectIdentifier(LocatorIF subjid) {
      return ctmctx.makeTopicBySubjectIdentifier(subjid);
    }
  
    public TopicIF makeAnonymousTopic() {
      return ctmctx.makeAnonymousTopic();
    }

    public TopicIF makeAnonymousTopic(String wildcard_name) {
      return ctmctx.makeAnonymousTopic(wildcard_name);
    }

    public void registerTemplate(String name, Template template) {
      throw new UnsupportedOperationException();
    }

    public Template getTemplate(String name) {
      throw new UnsupportedOperationException();
    }

    public Map getTemplates() {
      throw new UnsupportedOperationException();
    }
  }
}