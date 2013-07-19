
package net.ontopia.topicmaps.query.parser;

import java.util.Map;
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
import net.ontopia.topicmaps.query.impl.basic.QueryContext;
import net.ontopia.topicmaps.query.impl.utils.QueryMatchesUtils;

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

  // doStaticUpdates is inherited from ModificationFunctionStatement

  public int doUpdates(QueryMatches matches) throws InvalidQueryException {
    return doFunctionUpdates(matches);
  }

  protected int doLitListDeletes(boolean strict, Map arguments)
    throws InvalidQueryException {
    throw new UnsupportedOperationException(); // updates always have a function
  }

  public String toString() {
    String str = "update " + toStringFunction();
    if (query != null)
      str += "\nfrom " + query.toStringFromPart();
    return str;
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