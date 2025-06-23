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

import java.net.URISyntaxException;
import java.util.Map;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Represents an UPDATE statement.
 */
public class UpdateStatement extends ModificationFunctionStatement {

  static {
    functions.put("value", new ValueFunction());
    functions.put("resource", new ResourceFunction());
  }

  // doStaticUpdates is inherited from ModificationFunctionStatement

  @Override
  public int doUpdates(QueryMatches matches) throws InvalidQueryException {
    return doFunctionUpdates(matches);
  }

  @Override
  protected int doLitListDeletes(boolean strict, Map arguments)
    throws InvalidQueryException {
    throw new UnsupportedOperationException(); // updates always have a function
  }

  @Override
  public String toString() {
    String str = "update " + toStringFunction();
    if (query != null) {
      str += "\nfrom " + query.toStringFromPart();
    }
    return str;
  }

  // ----- UPDATE FUNCTIONS

  static class ValueFunction implements ModificationFunctionIF {
    @Override
    public String getSignature() {
      return "vbo s";
    }
    @Override
    public void modify(TMObjectIF object, Object v) {
      String value = (String) v;

      if (object instanceof OccurrenceIF) {
        ((OccurrenceIF) object).setValue(value);
      } else if (object instanceof TopicNameIF) {
        ((TopicNameIF) object).setValue(value);
      } else if (object instanceof VariantNameIF) {
        ((VariantNameIF) object).setValue(value);
      } else {
        throw new OntopiaRuntimeException("OUCH!");
      }
    }
  }

  static class ResourceFunction implements ModificationFunctionIF {
    @Override
    public String getSignature() {
      return "vo s";
    }
    @Override
    public void modify(TMObjectIF object, Object v) {
      try {
        LocatorIF loc = new URILocator((String) v);

        if (object instanceof OccurrenceIF) {
          ((OccurrenceIF) object).setLocator(loc);
        } else if (object instanceof VariantNameIF) {
          ((VariantNameIF) object).setLocator(loc);
        } else {
          throw new OntopiaRuntimeException("OUCH!");
        }
      } catch (URISyntaxException e) {
        throw new OntopiaRuntimeException(e);
      }
    }
  }
}
