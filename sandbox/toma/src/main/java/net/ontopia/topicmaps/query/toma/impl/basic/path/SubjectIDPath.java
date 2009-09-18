package net.ontopia.topicmaps.query.toma.impl.basic.path;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;

/**
 * INTERNAL: Subject Identifier path element in an path expression. Returns all 
 * subject identifiers for a given input topic. 
 * <p>
 * <b>Allowed Input</b>:
 * <ul>
 * <li>TOPIC
 * </ul>
 * </p><p>
 * <b>Output</b>: LOCATOR
 * </p>
 */
public class SubjectIDPath extends AbstractBasicPathElement {
  static final Set<TYPE> inputSet;
  
  static {
    inputSet = new HashSet<TYPE>();
    inputSet.add(TYPE.TOPIC);
  }
  
  public SubjectIDPath() {
    super("SI");
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
    TopicIF topic = (TopicIF) input;
    return topic.getSubjectIdentifiers();
  }
}
