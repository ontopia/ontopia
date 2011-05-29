
package net.ontopia.topicmaps.impl.basic.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.impl.utils.IndexManagerIF;
import net.ontopia.topicmaps.core.index.ScopeIndexIF;
import net.ontopia.topicmaps.impl.utils.BasicIndex;
import net.ontopia.topicmaps.impl.utils.EventManagerIF;
import net.ontopia.topicmaps.impl.utils.ObjectTreeManager;
import net.ontopia.utils.CollectionMap;

/**
 * INTERNAL: The basic dynamic scope index implementation.
 */

public class ScopeIndex extends BasicIndex implements ScopeIndexIF {
  
  protected CollectionMap basenames;
  protected CollectionMap variants;
  protected CollectionMap occurs;
  protected CollectionMap assocs;

  ScopeIndex(IndexManagerIF imanager, EventManagerIF emanager, ObjectTreeManager otree) {

    // Initialize index maps
    basenames = new CollectionMap();
    variants = new CollectionMap();
    occurs = new CollectionMap();
    assocs = new CollectionMap();

    // Initialize object tree event handlers [objects added or removed]    
    otree.addListener(new ScopedIF_added(basenames, "TopicNameIF.addTheme"), "TopicNameIF.added");
    otree.addListener(new ScopedIF_removed(basenames, "TopicNameIF.removeTheme"), "TopicNameIF.removed");
                          
    otree.addListener(new ScopedIF_added(variants, "VariantNameIF.addTheme"), "VariantNameIF.added");
    otree.addListener(new ScopedIF_removed(variants, "VariantNameIF.removeTheme"), "VariantNameIF.removed");
                          
    otree.addListener(new ScopedIF_added(occurs, "OccurrenceIF.addTheme"), "OccurrenceIF.added");
    otree.addListener(new ScopedIF_removed(occurs, "OccurrenceIF.removeTheme"), "OccurrenceIF.removed");
                          
    otree.addListener(new ScopedIF_added(assocs, "AssociationIF.addTheme"), "AssociationIF.added");
    otree.addListener(new ScopedIF_removed(assocs, "AssociationIF.removeTheme"), "AssociationIF.removed");
        
    // Initialize object property event handlers
    handlers.put("TopicNameIF.addTheme", new ScopedIF_addTheme(basenames));
    handlers.put("TopicNameIF.removeTheme", new ScopedIF_removeTheme(basenames));

    handlers.put("VariantNameIF.addTheme", new ScopedIF_addTheme(variants));
    handlers.put("VariantNameIF.removeTheme", new ScopedIF_removeTheme(variants));

    handlers.put("OccurrenceIF.addTheme", new ScopedIF_addTheme(occurs));
    handlers.put("OccurrenceIF.removeTheme", new ScopedIF_removeTheme(occurs));

    handlers.put("AssociationIF.addTheme", new ScopedIF_addTheme(assocs));
    handlers.put("AssociationIF.removeTheme", new ScopedIF_removeTheme(assocs));
    
    // Register dynamic index as event listener
    Iterator iter = handlers.keySet().iterator();
    while (iter.hasNext()) {
      emanager.addListener(this, (String)iter.next());
    }
  }

  // -----------------------------------------------------------------------------
  // ScopeIndexIF
  // -----------------------------------------------------------------------------
    
  public Collection getTopicNames(TopicIF theme) {
    Collection result = (Collection)basenames.get(theme);
    if (result == null) return Collections.EMPTY_SET;
    // Create new collection
    return new ArrayList(result);    
  }
  
  public Collection getVariants(TopicIF theme) {
    Collection result = (Collection)variants.get(theme);
    if (result == null) return Collections.EMPTY_SET;
    // Create new collection
    return new ArrayList(result);    
  }
  
  public Collection getOccurrences(TopicIF theme) {
    Collection result = (Collection)occurs.get(theme);
    if (result == null) return Collections.EMPTY_SET;
    // Create new collection
    return new ArrayList(result);    
  }
  
  public Collection getAssociations(TopicIF theme) {
    Collection result = (Collection)assocs.get(theme);
    if (result == null) return Collections.EMPTY_SET;
    // Create new collection
    return new ArrayList(result);    
  }
    
  public Collection getTopicNameThemes() {
    // Create new collection
    Collection result = new ArrayList(basenames.keySet());
    result.remove(null);
    return result;
  }

  public Collection getVariantThemes() {
    // Create new collection
    Collection result = new ArrayList(variants.keySet()); 
    result.remove(null);
    return result;
 }

  public Collection getOccurrenceThemes() {
    // Create new collection
    Collection result = new ArrayList(occurs.keySet());
    result.remove(null);
    return result;
  }
  
  public Collection getAssociationThemes() {
    // Create new collection
    Collection result = new ArrayList(assocs.keySet());
    result.remove(null);
    return result;
  }

  public boolean usedAsTopicNameTheme(TopicIF topic) {
    return basenames.containsKey(topic);
  }

  public boolean usedAsVariantTheme(TopicIF topic) {
    return variants.containsKey(topic);
  }

  public boolean usedAsOccurrenceTheme(TopicIF topic) {
    return occurs.containsKey(topic);
  }

  public boolean usedAsAssociationTheme(TopicIF topic) {
    return assocs.containsKey(topic);
  }
  
  public boolean usedAsTheme(TopicIF topic) {
    return (basenames.containsKey(topic) ||
            variants.containsKey(topic) ||
            occurs.containsKey(topic) ||
            assocs.containsKey(topic));
  }

  // -----------------------------------------------------------------------------
  // Event handlers
  // -----------------------------------------------------------------------------

  /**
   * EventHandler: ScopedIF.addTheme
   */
  class ScopedIF_addTheme extends EventHandler {
    protected CollectionMap objects;
    ScopedIF_addTheme(CollectionMap objects) {
      this.objects = objects;
    }
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      ScopedIF scoped = (ScopedIF)object;

      // Register scope
      Collection scope = scoped.getScope();
      if (scope.isEmpty())
        // Unregister null theme
        objects.remove(null, scoped);

      // Register theme
      objects.add(new_value, scoped);
    }
  }
  /**
   * EventHandler: ScopedIF.removeTheme
   */
  class ScopedIF_removeTheme extends EventHandler {
    protected CollectionMap objects;
    ScopedIF_removeTheme(CollectionMap objects) {
      this.objects = objects;
    }
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      ScopedIF scoped = (ScopedIF)object;

      // Register themes
      Collection scope = scoped.getScope();
      if (scope.size() == 1 && scope.contains(old_value))
        // Unregister null theme
        objects.add(null, scoped);

      // Unregister theme
      objects.remove(old_value, scoped);
    }
  }

  /**
   * EventHandler: ScopedIF.added
   */
  class ScopedIF_added extends EventHandler {
    protected CollectionMap objects;
    protected String child_event;
    ScopedIF_added(CollectionMap objects, String child_event) {
      this.objects = objects;
      this.child_event = child_event;
    }
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      ScopedIF added = (ScopedIF)new_value;
      // Register themes
      Collection scope = added.getScope();
      if (scope.isEmpty()) {
        // Register the null theme
        objects.add(null, added);       
      } else {
        Object[] _scope = scope.toArray();
        for (int i=0; i < _scope.length; i++)
          addEvent(added, child_event, _scope[i]);
      }
    }
  }
  /**
   * EventHandler: ScopedIF.removed
   */
  class ScopedIF_removed extends EventHandler {
    protected CollectionMap objects;
    protected String child_event;
    ScopedIF_removed(CollectionMap objects, String child_event) {
      this.objects = objects;
      this.child_event = child_event;
    }
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      ScopedIF removed = (ScopedIF)old_value;
      // Unregister themes
      Collection scope = removed.getScope();
      if (!scope.isEmpty()) {
        Object[] _scope = scope.toArray();
        for (int i=0; i < _scope.length; i++)     
          removeEvent(removed, child_event, _scope[i]);
      }
      // Unregister null theme
      objects.remove(null, removed);
    }
  }
  
}





