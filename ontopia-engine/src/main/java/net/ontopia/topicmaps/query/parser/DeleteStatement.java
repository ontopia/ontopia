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

package net.ontopia.topicmaps.query.parser;

import java.util.Map;
import java.net.URISyntaxException;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;
/**
 * INTERNAL: Represents a parsed DELETE statement.
 */
public class DeleteStatement extends ModificationFunctionStatement {

  static {
    functions.put("item-identifier", new ItemIdentifierFunction());
    functions.put("subject-identifier", new SubjectIdentifierFunction());
    functions.put("subject-locator", new SubjectLocatorFunction());
    functions.put("direct-instance-of", new DirectInstanceOfFunction());
    functions.put("scope", new ScopeFunction());
    functions.put("reifies", new ReifiesFunction());
  }
  
  // doStaticUpdates is inherited from ModificationFunctionStatement

  @Override
  public int doUpdates(QueryMatches matches) throws InvalidQueryException {
    if (funcname == null) {
      return doNormalDeletes(matches);
    } else {
      return doFunctionUpdates(matches);
    }
  }

  // --- Internal methods
  
  @Override
  public String toString() {
    String str = "delete ";
    if (funcname != null) {
      str += toStringFunction();
    } else {
      str += toStringLitlist();
    }
    if (query != null) {
      str += "\nfrom " + query.toStringFromPart();
    }
    return str;
  }

  @Override
  protected int doLitListDeletes(boolean strict, Map arguments)
    throws InvalidQueryException {
    int deletes = 0;
    for (int ix = 0; ix < litlist.size(); ix++) {
      Object lit = litlist.get(ix);

      if (lit instanceof Parameter) {
        lit = arguments.get(((Parameter) lit).getName());
      }
      
      if (lit instanceof TMObjectIF) {
        ((TMObjectIF) lit).remove();
        deletes++;
      } else if (strict) {
        throw new InvalidQueryException("Invalid reference in litlist: " +
                                        lit);
      }
    }
    return deletes;
  }

  private int doNormalDeletes(QueryMatches matches) throws InvalidQueryException{
    int deletes = doLitListDeletes(false,
                                   matches.getQueryContext().getParameters());

    // INV: the final QueryMatches object contains only variables actually
    // used in the litlist, so we can go through and just delete everything

    for (int row = 0; row <= matches.last; row++) {
      for (int col = 0; col < matches.colcount; col++) {
        Object o = matches.data[row][col];
        if (o instanceof TMObjectIF) {
          ((TMObjectIF) o).remove();
          deletes++;
        } else {
          throw new InvalidQueryException("Deleting non-topic map object: " +
                                          o);
        }
      }
    }
    
    return deletes;
  }
    
  // ----- DELETE FUNCTIONS

  static class ItemIdentifierFunction implements ModificationFunctionIF {
    @Override
    public String getSignature() {
      return "x s";
    }
    @Override
    public void modify(TMObjectIF object, Object v) {
      String value = (String) v;
      try {
        object.removeItemIdentifier(new URILocator(value));
      } catch (URISyntaxException e) {
        throw new OntopiaRuntimeException("Invalid URI: " + value);
      }
    }
  }

  static class SubjectIdentifierFunction implements ModificationFunctionIF {
    @Override
    public String getSignature() {
      return "t s";
    }
    @Override
    public void modify(TMObjectIF object, Object v) {
      if (!(object instanceof TopicIF)) {
        return;
      }

      TopicIF topic = (TopicIF) object;
      String value = (String) v;
      try {
        topic.removeSubjectIdentifier(new URILocator(value));
      } catch (URISyntaxException e) {
        throw new OntopiaRuntimeException("Invalid URI: " + value);
      }
    }
  }

  static class SubjectLocatorFunction implements ModificationFunctionIF {
    @Override
    public String getSignature() {
      return "t s";
    }
    @Override
    public void modify(TMObjectIF object, Object v) {
      if (!(object instanceof TopicIF)) {
        return;
      }

      TopicIF topic = (TopicIF) object;
      String value = (String) v;
      try {
        topic.removeSubjectLocator(new URILocator(value));
      } catch (URISyntaxException e) {
        throw new OntopiaRuntimeException("Invalid URI: " + value);
      }
    }
  }

  static class DirectInstanceOfFunction implements ModificationFunctionIF {
    @Override
    public String getSignature() {
      return "t t";
    }
    @Override
    public void modify(TMObjectIF object, Object v) {
      if (!(object instanceof TopicIF)) {
        return;
      }

      TopicIF topic = (TopicIF) object;
      TopicIF type = (TopicIF) v;
      topic.removeType(type);
    }
  }

  static class ScopeFunction implements ModificationFunctionIF {
    @Override
    public String getSignature() {
      return "bvoa t";
    }
    @Override
    public void modify(TMObjectIF object, Object v) {
      ScopedIF scoped = (ScopedIF) object;
      TopicIF theme = (TopicIF) v;
      scoped.removeTheme(theme);
    }
  }

  static class ReifiesFunction implements ModificationFunctionIF {
    @Override
    public String getSignature() {
      return "t bvoar";
    }
    @Override
    public void modify(TMObjectIF object, Object v) {
      TopicIF reifier = (TopicIF) object;
      ReifiableIF reified = (ReifiableIF) v;

      TopicIF realreifier = reified.getReifier();
      if (realreifier != null && reifier.equals(realreifier)) {
        reified.setReifier(null);
      }
    }
  }
}