package ontopoly.models;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.wicket.model.LoadableDetachableModel;

public abstract class CollectionModel extends LoadableDetachableModel {

  private Collection values;

  public CollectionModel(Collection values) {
    if (values == null)
      throw new NullPointerException("values parameter cannot be null.");
    this.values = values;
  }
  
  @Override
  protected Object load() {
    Collection result = makeCollection(values.size());
    Iterator iter = values.iterator();
    while (iter.hasNext()) {
      Object value = iter.next();
      result.add(getObjectFor(value));
    }
    return result;
  }

  /**
   * Make new collection instance.
   * @param size the size of the collection to create.
   * @return return new collection
   */
  protected Collection makeCollection(int size) {
    return new ArrayList(size);
  }
  
  /**
   * This method will be called for each value in the containing collection. The result will be part of the final model collection.
   * @param object the object to wrap
   * @return the wrapper object
   */
  protected abstract Object getObjectFor(Object object);
  
}
