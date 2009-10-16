package net.ontopia.topicmaps.query.toma.impl.basic.path;

import java.util.Collection;

import net.ontopia.topicmaps.query.toma.impl.basic.BasicPathElementIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractPathElement;

public abstract class AbstractBasicPathElement extends AbstractPathElement
    implements BasicPathElementIF {
  protected AbstractBasicPathElement(String name) {
    super(name);
  }

  public String[] getColumnNames(LocalContext context) {
    if (getBoundVariable() != null) {
      return new String[] { getBoundVariable().toString() };
    } else {
      return new String[0];
    }
  }

  public int getResultSize(LocalContext context) {
    if (getBoundVariable() != null) {
      return 1;
    } else {
      return 0;
    }
  }
  
  /**
   * Checks whether collection a contains at least one item from collection b.
   * 
   * @return true if collection a contains any item from collection b, false
   *         otherwise.
   */
  protected boolean containsAny(Collection<?> a, Collection<?> b) {
    for (Object obj : b) {
      if (a.contains(obj)) {
        return true;
      }
    }
    return false;
  }
}
