package ontopoly.utils;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.wicket.model.IModel;

public class ModelComparator implements Comparator, Serializable {

  protected Comparator comparator;
  
  public ModelComparator(Comparator comparator) {
    this.comparator = comparator;
  }
  
  public int compare(Object o1, Object o2) {
    Object mo1 = ((IModel)o1).getObject();
    Object mo2 = ((IModel)o2).getObject();
    return comparator.compare(mo1, mo2);
  }

}
