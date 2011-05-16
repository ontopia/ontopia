package ontopoly.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.version.undo.Change;

public class AjaxRadioGroupPanel extends Panel {

  private String suffix = "<br/>\n"; 
  private List<Component> ajaxTargets = new ArrayList<Component>();
  
  @SuppressWarnings("unchecked")
  public AjaxRadioGroupPanel(String id, final Form<?> form, List choices, IModel model) {
    super(id);
    final RadioGroup rg = new RadioGroup("radiochoicegroup", model);  
    rg.setRenderBodyOnly(false);  
    rg.add(new ListView("radiochoices", choices)  {   
      @Override   
      protected void populateItem(ListItem item)   {    
        final Serializable radioitem = (Serializable) item.getModelObject();    
        final Radio rc = new Radio("radiochoice", new Model(radioitem));    
        // Must use AjaxFormSubmitBehavior for this type of component    
        rc.add(new AjaxFormSubmitBehavior(form, "onclick")    {     
          protected void onSubmit(AjaxRequestTarget target)     {      
            if(target != null) {
              int size = ajaxTargets.size();
              for (int i = 0; i < size; i++) {       
                target.addComponent(ajaxTargets.get(i));      
              }   
            }
          }
          @Override
          protected void onError(AjaxRequestTarget target) {
          }    
        });    
        // Add label for radio button    
        String label = radioitem.toString();    
        String display = label;    
        if (localizeDisplayValues())    {     
          display = getLocalizer().getString(label, this, label);    
        }    
        item.add(rc);    
        item.add(new Label("radiolabel", display));    
        item.add(new Label("suffix", getSuffix()).setRenderBodyOnly(true).setEscapeModelStrings(false));   
      }  
    });  
    add(rg);
  }

  /**  * Borrowed from RadioChoice  *   
   * * @return Separator to use between radio options  
   **/ 
  public final String getSuffix() {  
    return suffix; 
  } 
  
  /**  * Borrowed from RadioChoice  *   
   * * @param suffix  *            
   * Separator to use between radio options  */ 
  public final void setSuffix(String suffix) {  
    // Tell the page that this component's suffix was changed  
    final Page page = findPage();  
    if (page != null)  {   
      addStateChange(new SuffixChange(this.suffix));  
    }  
    this.suffix = suffix; 
  } 
  
  /**  * Borrowed from RadioChoice suffix change record.  */ 
  private class SuffixChange extends Change {  
    private static final long serialVersionUID = 3344L;  
    final String prevSuffix;  SuffixChange(String prevSuffix)  {   
      this.prevSuffix = prevSuffix;  
    }  
    public void undo()  {   
      setSuffix(prevSuffix);  
    }  
    
    public String toString()  {   
      return "SuffixChange[component: " + getPath() + ", suffix: "     + prevSuffix + "]";  
    } 
  } 
  
  /**  * Borrowed from AbstractChoice  *   
   * * Override this method if you want to localize the display values of the  
   * * generated options. By default false is returned so that the display  
   * * values of options are not tested if they have a i18n key.  *   
   * * @return true If you want to localize the display values, default == false  */ 
  protected boolean localizeDisplayValues() {  
    return false; 
  }  
  
  /**  * @param c A component to update when a radio choice is clicked  */ 
  public void addAjaxTarget(Component c) {  
    ajaxTargets.add(c); 
  }
}
