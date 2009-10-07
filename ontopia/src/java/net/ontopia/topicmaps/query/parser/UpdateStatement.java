
// $Id$

package net.ontopia.topicmaps.query.parser;

import java.util.Collections;
import java.net.MalformedURLException;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;

/**
 * INTERNAL: Represents an UPDATE statement.
 */
public class UpdateStatement extends ModificationFunctionStatement {

  static {
    functions.put("value", new ValueFunction());
    functions.put("resource", new ResourceFunction());
  }

  public UpdateStatement() {
    super();
  }

  public int doStaticUpdates() throws InvalidQueryException {
    // in order to avoid duplicating code we produce a "fake" matches
    // object here, so that in effect we're simulating a one-row zero-column
    // result set
    QueryMatches matches = new QueryMatches(Collections.EMPTY_SET, null);
    matches.last++; // make an empty row
    return doUpdates(matches);
  }

  public int doUpdates(QueryMatches matches)
    throws InvalidQueryException {
    int updates = 0;

    ModificationFunctionIF function = makeFunction(funcname);
    FunctionSignature signature = FunctionSignature.getSignature(function);
    Object arg1 = litlist.get(0);
    int varix1 = getIndex(arg1, matches);
    Object arg2 = litlist.get(1);
    int varix2 = getIndex(arg2, matches);
    
    for (int row = 0; row <= matches.last; row++) {
      if (varix1 != -1)
        arg1 = matches.data[row][varix1];

      if (varix2 != -1)
        arg2 = matches.data[row][varix2];

      signature.validateArguments(arg1, arg2, funcname);
      function.modify((TMObjectIF) arg1, arg2);
      updates++;
    }

    return updates;
  }

  // ----- UPDATE FUNCTIONS

  static class ValueFunction implements ModificationFunctionIF {
    public String getSignature() {
      return "vbo s";
    }
    public void modify(TMObjectIF object, Object v) {
      String value = (String) v;

      if (object instanceof OccurrenceIF)
        ((OccurrenceIF) object).setValue(value);
      else if (object instanceof TopicNameIF)
        ((TopicNameIF) object).setValue(value);
      else if (object instanceof VariantNameIF)
        ((VariantNameIF) object).setValue(value);
      else
        throw new OntopiaRuntimeException("OUCH!");
    }
  }

  static class ResourceFunction implements ModificationFunctionIF {
    public String getSignature() {
      return "vo s";
    }
    public void modify(TMObjectIF object, Object v) {
      try {
        LocatorIF loc = new URILocator((String) v);

        if (object instanceof OccurrenceIF)
          ((OccurrenceIF) object).setLocator(loc);
        else if (object instanceof VariantNameIF)
          ((VariantNameIF) object).setLocator(loc);
        else
          throw new OntopiaRuntimeException("OUCH!");
      } catch (MalformedURLException e) {
        throw new OntopiaRuntimeException(e);
      }
    }
  }
}