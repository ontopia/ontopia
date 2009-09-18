package net.ontopia.topicmaps.query.toma.impl.basic.path;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;

/**
 * INTERNAL: Data path element in an path expression. Returns the value of 
 * variants/occurrences as string.
 * <p>
 * <b>Allowed Input</b>:
 * <ul>
 * <li>OCCURRENCE
 * <li>VARIANT
 * </ul>
 * </p><p>
 * <b>Output</b>: STRING
 * </p>
 */
public class DataPath extends AbstractBasicPathElement {
  static final Set<TYPE> inputSet;
  
  static {
    inputSet = new HashSet<TYPE>();
    inputSet.add(TYPE.OCCURRENCE);
    inputSet.add(TYPE.VARIANT);
  }
  
  public DataPath() {
    super("DATA");
  }

  protected boolean isLevelAllowed() {
    return false;
  }

  protected boolean isScopeAllowed() {
    return false;
  }
  
  protected boolean isTypeAllowed() {
    return false;
  }

  protected boolean isChildAllowed() {
    return false;
  }
  
  public Set<TYPE> validInput() {
    return inputSet;
  }

  public TYPE output() {
    return TYPE.STRING;
  }
  
  public Collection<?> evaluate(LocalContext context, Object input) {
    Collection<String> coll = new LinkedList<String>();

    if (input instanceof OccurrenceIF) {
      OccurrenceIF oc = (OccurrenceIF) input;
      coll.add(oc.getValue());
    } else if (input instanceof VariantNameIF) {
      VariantNameIF var = (VariantNameIF) input;
      coll.add(var.getValue());
    }
    
    return coll;
  }  
}
