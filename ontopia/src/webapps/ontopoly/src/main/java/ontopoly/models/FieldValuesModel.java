package ontopoly.models;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.FieldInstance;

import org.apache.wicket.model.LoadableDetachableModel;

public class FieldValuesModel extends LoadableDetachableModel implements Comparator {

  private FieldInstanceModel fieldInstanceModel;
  private Comparator comparator;
  private boolean showExtraField;
  
  public FieldValuesModel(FieldInstanceModel fieldInstanceModel) {
    this(fieldInstanceModel, null);
  }
  
  public FieldValuesModel(FieldInstanceModel fieldInstanceModel, Comparator comparator) {
    if (fieldInstanceModel == null)
      throw new NullPointerException("fieldInstanceModel parameter cannot be null.");
    this.fieldInstanceModel = fieldInstanceModel;
    this.comparator = comparator;
  }
  
  public void setComparator(Comparator comparator) {
    this.comparator = comparator;
  }
  
  public FieldInstanceModel getFieldInstanceModel() {
    return fieldInstanceModel;    
  }
  
  public boolean getShowExtraField() {
    return showExtraField;
  }
  
  public void setShowExtraField(boolean showExtraField) {
    this.showExtraField = showExtraField;
  }
  
  /**
   * Returns the number of values in the model. Note that the first
   * one might not represent an actual value. Use getNumberOfValues()
   * to get the real number of actual values in the model.
   * @return the size of the model.
   */
  public int size() {
    Collection values = (Collection)getObject();
    return values.size();
  }
  
  /**
   * Get the number of actual existing values. Number is either the same 
   * as size() or one less if the showExtraField is enabled.
   * @return the number of actual values
   */
  public int getNumberOfValues() {
    int size = size();
    if (getShowExtraField())
      size--;
    return size;
  }
  
  public boolean containsExisting() {
    Collection values = (Collection)super.getObject();
    return !values.isEmpty();
  }

  protected Collection getValues(FieldInstance fieldInstance) {
    return fieldInstance.getValues();
  }
  
  @Override
  public Object getObject() {
    Collection values = (Collection)super.getObject();
    if (values.isEmpty())
      setShowExtraField(true);
    if (getShowExtraField()) {
      List result = new ArrayList(values);
      FieldValueModel fieldValueModel = FieldValueModel.createModel(fieldInstanceModel, null, false); 
      result.add(0, fieldValueModel);
      return result;
    } else {
      return values;
    }
  }
  
  @Override
  protected Object load() {
    FieldInstance fieldInstance = fieldInstanceModel.getFieldInstance();
    Collection values = getValues(fieldInstance);
    if (values.isEmpty()) {
      return values;
    } else {
      List result = new ArrayList(values.size());
      Iterator iter = values.iterator();
      while (iter.hasNext()) {
        Object value = iter.next();
        result.add(FieldValueModel.createModel(fieldInstanceModel,  value, true));
      }
      // sort field value models
      if (comparator != null)
        Collections.sort(result, this);
      return result;
    }
  }

  public int compare(Object o1, Object o2) {
    return comparator.compare(((FieldValueModel)o1).getFieldValue(), ((FieldValueModel)o2).getFieldValue());
  }
 
}
