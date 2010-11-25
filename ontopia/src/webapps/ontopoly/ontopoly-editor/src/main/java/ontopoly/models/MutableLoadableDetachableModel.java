package ontopoly.models;

import org.apache.wicket.model.IModel;

public abstract class MutableLoadableDetachableModel<T> implements IModel<T> {

  /** keeps track of whether this model is attached or detached */
  private transient boolean attached = false;

  /** temporary, transient object. */
  private transient T transientModelObject;

  public MutableLoadableDetachableModel() {
  }

  public MutableLoadableDetachableModel(T object) {
    this.transientModelObject = object;
    attached = true;
  }

  public void detach() {
    if (attached) {
      attached = false;
      transientModelObject = null;
      onDetach();
    }
  }
  
  public T getObject() {
    if (!attached) {
      attached = true;
      transientModelObject = load();
      onAttach();
    }
    return transientModelObject;
  }

  public void setObject(T object) {
    this.transientModelObject = object;
    attached = true;
  }

  public final boolean isAttached() {
    return attached;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer("Model:classname=[");
    sb.append(getClass().getName()).append("]");
    sb.append(":attached=").append(attached).append(":tempModelObject=[").append(
        this.transientModelObject).append("]");
    return sb.toString();
  }

  protected abstract T load();

  protected void onAttach() {
  }

  protected void onDetach() {
  }
  
}
