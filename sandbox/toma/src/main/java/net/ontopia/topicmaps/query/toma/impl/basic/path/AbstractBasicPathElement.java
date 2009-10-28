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
    String[] names = new String[getResultSize(context)];

    int idx = 0;
    if (getBoundInputVariable() != null) {
      names[idx++] = getBoundInputVariable().toString();
    }
    if (getBoundVariable() != null) {
      names[idx++] = getBoundVariable().toString();
    }
    return names;
  }

  public int getResultSize(LocalContext context) {
    int cnt = 0;
    if (getBoundInputVariable() != null) cnt++;
    if (getBoundVariable() != null) cnt++;
    return cnt;
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
