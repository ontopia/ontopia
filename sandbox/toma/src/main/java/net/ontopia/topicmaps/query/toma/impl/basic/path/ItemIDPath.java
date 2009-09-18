package net.ontopia.topicmaps.query.toma.impl.basic.path;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;

/**
 * INTERNAL: ID path element in an path expression. Returns the item identifiers
 * of a given topic map construct as a locator.
 * <p>
 * <b>Allowed Input</b>:
 * <ul>
 * <li>TOPIC
 * <li>NAME
 * <li>VARIANT
 * <li>OCCURRENCE
 * <li>ASSOCIATION
 * </ul>
 * </p><p>
 * <b>Output</b>: LOCATOR
 * </p>
 */
public class ItemIDPath extends AbstractBasicPathElement {
  
  static final Set<TYPE> inputSet;
  
  static {
    inputSet = new HashSet<TYPE>();
    inputSet.add(TYPE.TOPIC);
    inputSet.add(TYPE.NAME);
    inputSet.add(TYPE.VARIANT);
    inputSet.add(TYPE.OCCURRENCE);
    inputSet.add(TYPE.ASSOCIATION);
  }
  
  public ItemIDPath() {
    super("ID");
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
    return TYPE.LOCATOR;
  }

  @SuppressWarnings("unchecked")
  public Collection<LocatorIF> evaluate(LocalContext context, Object input) {
    TMObjectIF tm = (TMObjectIF) input;
    return tm.getItemIdentifiers();
  }
}
