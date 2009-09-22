package net.ontopia.topicmaps.query.toma.impl.basic.root;

import java.util.Collection;
import java.util.LinkedList;

import net.ontopia.topicmaps.query.toma.impl.basic.BasicRootIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractEmptyRoot;

public class EmptyRoot extends AbstractEmptyRoot implements BasicRootIF {

  public EmptyRoot() {
  }

  public Collection<?> evaluate(LocalContext context) {
    LinkedList<String> result = new LinkedList<String>();
    result.add("empty");
    return result;
  }
}
