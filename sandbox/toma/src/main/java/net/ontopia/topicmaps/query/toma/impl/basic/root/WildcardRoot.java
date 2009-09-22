package net.ontopia.topicmaps.query.toma.impl.basic.root;

import java.util.Collection;
import java.util.LinkedList;

import net.ontopia.topicmaps.query.toma.impl.basic.BasicRootIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractAnyRoot;

public class WildcardRoot extends AbstractAnyRoot implements BasicRootIF {

  public WildcardRoot() {}

  public Collection<?> evaluate(LocalContext context) {
    LinkedList<String> result = new LinkedList<String>();
    result.add("any");
    return result;
  }
}
