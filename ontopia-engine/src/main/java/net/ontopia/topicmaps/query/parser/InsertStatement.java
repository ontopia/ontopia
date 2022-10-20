/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

// $Id$

package net.ontopia.topicmaps.query.parser;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.io.Reader;
import java.io.StringReader;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.utils.ctm.CTMLexer;
import net.ontopia.topicmaps.utils.ctm.CTMParser;
import net.ontopia.topicmaps.utils.ctm.Template;
import net.ontopia.topicmaps.utils.ctm.BuilderEventHandler;
import net.ontopia.topicmaps.utils.ctm.TemplateEventHandler;
import net.ontopia.topicmaps.utils.ctm.ValueGeneratorIF;
import net.ontopia.topicmaps.utils.ctm.ValueGenerator;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;
import net.ontopia.topicmaps.xml.InvalidTopicMapException;

import antlr.RecognitionException;
import antlr.TokenStreamRecognitionException;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;

/**
 * INTERNAL: Represents a parsed INSERT statement.
 */
public class InsertStatement extends ModificationStatement {
  private Template template;
  private CTMParseContext context;
  private List<String> parameters; // the order of the parameters to the CTM part

  @Override
  public int doStaticUpdates(TopicMapIF topicmap, Map arguments)
    throws InvalidQueryException {
    BuilderEventHandler handler =
      new BuilderEventHandler(topicmap.getBuilder(), context);
    template.invoke(Collections.EMPTY_LIST, handler);
    return 1;
  }

  @Override
  public int doUpdates(QueryMatches matches)
    throws InvalidQueryException {
    TopicMapIF topicmap = matches.getQueryContext().getTopicMap();
    BuilderEventHandler handler =
      new BuilderEventHandler(topicmap.getBuilder(), context);

    // the argument values to be passed to the template
    List arguments = new ArrayList(matches.colcount);
    for (int ix = 0; ix < matches.colcount; ix++) {
      arguments.add(null); // making a slot for value filled in later
    }

    // a mapping from the column indexes in matches to the corresponding
    // parameter indexes in the arguments list
    int[] colix = new int[parameters.size()];
    for (int ix = 0; ix < parameters.size(); ix++) {
      String name = parameters.get(ix).substring(1); // remove the $
      colix[ix] = matches.getVariableIndex(name);
    }

    for (int row = 0; row <= matches.last; row++) {
      for (int ix = 0; ix < parameters.size(); ix++) {
        arguments.set(ix, makeGenerator(matches.data[row][colix[ix]]));
      }

      template.invoke(arguments, handler);
      context.endContext();
    }
    
    return matches.last + 1;
  }

  @Override
  public String toString() {
    String str = "insert ..."; // FIXME: stringfy CTM part
    if (query != null) {
      str += "\nfrom" + query.toStringFromPart();
    }
    return str;
  }
  
  public void setCTMPart(String ctm, ParseContextIF context)
    throws InvalidQueryException {

    // this sets parameter list to all parameters used in the query,
    // but these aren't necessarily all used in the INSERT part. still,
    // this list allows the CTM parser to reject unknown parameters and
    // still work the way it usually does. we reset the parameter list
    // once the CTM has been parsed.
    if (query == null) {
      parameters = Collections.EMPTY_LIST;
    } else {
      String[] varnames = query.getSelectedVariableNames();
      parameters = new ArrayList<String>(varnames.length);
      for (int ix = 0; ix < varnames.length; ix++) {
        parameters.add("$" + varnames[ix]);
      }
    }

    // actually do the CTM parsing
    try {
      // see ctm.g tolog_insert comment for why we add an "end"
      Reader reader = new StringReader(ctm + " end");
      CTMLexer lexer = new CTMLexer(reader);
      CTMParser parser = new CTMParser(lexer);
      parser.init();
      parser.setBase(context.getTopicMap().getStore().getBaseAddress());
      parser.setTopicMap(context.getTopicMap(), null);
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
    } catch (InvalidTopicMapException e) {
      throw new InvalidQueryException("Error in CTM part: " + e.getMessage());
    }

    if (query != null) {
      // deducing the set of parameters to the virtual template based on
      // what parameters are actually used in the INSERT part.
      Set<String> used = template.getUsedParameters();
      parameters = new ArrayList<String>(used.size());
      for (String param : used) {
        parameters.add(param);
      }
      template.setParameters(parameters);

      // finally, adjust the SELECT part of the query to match the parameters
      // actually used in the INSERT, to project the query down correctly.
      List<Variable> vars = new ArrayList<Variable>(parameters.size());
      for (String name : parameters) {
        vars.add(new Variable(name));
      }
      query.setSelectedVariables(vars);
    }
  }

  private static ValueGeneratorIF makeGenerator(Object value) {
    if (value instanceof TopicIF) {
      return new ValueGenerator((TopicIF) value, null, null, null);
    } else if (value instanceof String) {
      return new ValueGenerator(null, (String) value, DataTypes.TYPE_STRING,
              null);
    } else {
      throw new OntopiaRuntimeException("Can't make generator for " + value);
    }
  }

  // --- CTM parse context wrapping tolog parse context

  // lifetime for this context is the duration of the update statement
  static class CTMParseContext
    implements net.ontopia.topicmaps.utils.ctm.ParseContextIF {
    private ParseContextIF tologctx;
    private net.ontopia.topicmaps.utils.ctm.ParseContextIF ctmctx;
    private Map<String, TopicIF> wildcards;

    private CTMParseContext(ParseContextIF tologctx,
                            net.ontopia.topicmaps.utils.ctm.ParseContextIF ctmctx) {
      this.tologctx = tologctx;
      this.ctmctx = ctmctx;
      this.wildcards = new HashMap<String, TopicIF>();
    }

    @Override
    public void addPrefix(String prefix, LocatorIF locator) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void addIncludeUri(LocatorIF uri) {
      throw new UnsupportedOperationException();
    }
  
    @Override
    public Set getIncludeUris() {
      throw new UnsupportedOperationException();
    }
  
    @Override
    public LocatorIF resolveQname(String qname) {
      return tologctx.resolveQName(new QName(qname));
    }

    @Override
    public ValueGeneratorIF getTopicById(String id) {
      return ctmctx.getTopicById(id);
    }
  
    @Override
    public ValueGeneratorIF getTopicByItemIdentifier(LocatorIF itemid) {
      return ctmctx.getTopicByItemIdentifier(itemid);
    }

    @Override
    public ValueGeneratorIF getTopicBySubjectLocator(LocatorIF subjloc) {
      return ctmctx.getTopicBySubjectLocator(subjloc);
    }

    @Override
    public ValueGeneratorIF getTopicBySubjectIdentifier(LocatorIF subjid) {
      return ctmctx.getTopicBySubjectIdentifier(subjid);
    }

    @Override
    public ValueGeneratorIF getTopicByQname(String qname) {
      return ctmctx.getTopicBySubjectIdentifier(resolveQname(qname));
    }

    @Override
    public TopicIF makeTopicById(String id) {
      return ctmctx.makeTopicById(id);
    }
    
    @Override
    public TopicIF makeTopicByItemIdentifier(LocatorIF itemid) {
      return ctmctx.makeTopicByItemIdentifier(itemid);
    }

    @Override
    public TopicIF makeTopicBySubjectLocator(LocatorIF subjloc) {
      return ctmctx.makeTopicBySubjectLocator(subjloc);
    }

    @Override
    public TopicIF makeTopicBySubjectIdentifier(LocatorIF subjid) {
      return ctmctx.makeTopicBySubjectIdentifier(subjid);
    }
  
    @Override
    public TopicIF makeAnonymousTopic() {
      TopicMapIF topicmap = tologctx.getTopicMap();
      TopicMapBuilderIF builder = topicmap.getBuilder();
      TopicIF topic = builder.makeTopic();
      return topic;
    }

    @Override
    public TopicIF makeAnonymousTopic(String wildcard_name) {
      TopicIF topic = wildcards.get(wildcard_name);
      if (topic == null) {
        topic = makeAnonymousTopic();
        wildcards.put(wildcard_name, topic);
      }
      return topic;
    }

    @Override
    public void registerTemplate(String name, Template template) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Template getTemplate(String name , int paramcount) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Map getTemplates() {
      throw new UnsupportedOperationException();
    }

    // finished one row; named wildcards must be released
    private void endContext() {
      wildcards.clear();
    }
  }
}