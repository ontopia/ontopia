package ontopoly.models;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.model.LoadableDetachableModel;

public abstract class ListModel<T,V> extends LoadableDetachableModel<List<T>> {

  private List<V> values;

  public ListModel(List<V> values) {
    if (values == null)
      throw new NullPointerException("values parameter cannot be null.");
    this.values = values;
  }
  
  @Override
  protected List<T> load() {
    List<T> result = makeCollection(values.size());
    Iterator<V> iter = values.iterator();
    while (iter.hasNext()) {
      V value = iter.next();
      result.add(getObjectFor(value));
    }
    return result;
  }

  /**
   * Make new collection instance.
   * @param size the size of the collection to create.
   * @return return new collection
   */
  protected List<T> makeCollection(int size) {
    return new ArrayList<T>(size);
  }
  
  /**
   * This method will be called for each value in the containing collection. The result will be part of the final model collection.
   * @param object the object to wrap
   * @return the wrapper object
   */
  protected abstract T getObjectFor(V object);
  
}
