package net.ontopia.topicmaps.query.toma.impl.basic.path;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicPathElementIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractPathElement;

public class DataPath extends AbstractPathElement implements BasicPathElementIF {
  
  static final Set<TYPE> inputSet;
  
  static {
    inputSet = new HashSet<TYPE>();
    inputSet.add(TYPE.OCCURRENCE);
    inputSet.add(TYPE.VARIANT);
  }
  
  public DataPath() {
    super("DATA");
  }

  @Override
  protected boolean isLevelAllowed() {
    return false;
  }

  @Override
  protected boolean isScopeAllowed() {
    return false;
  }
  
  @Override
  protected boolean isTypeAllowed() {
    return false;
  }

  @Override
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
  
  public String[] getColumnNames() {
    if (getBoundVariable() != null) {
      return new String[] { getBoundVariable().toString() };
    } else {
      return new String[0];
    }
  }

  public int getResultSize() {
    if (getBoundVariable() != null) {
      return 1;
    } else {
      return 0;
    }
  }
}
