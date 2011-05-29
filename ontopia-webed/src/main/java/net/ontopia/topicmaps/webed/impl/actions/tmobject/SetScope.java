
package net.ontopia.topicmaps.webed.impl.actions.tmobject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;


/**
 * PUBLIC: Action for setting the scope of a tmobject. Takes the object to be scoped as first param, 
 * the second is a collection of TopicIFs which the object should be scoped with. If the tmobject has
 * other scopes then the ones contained in the collection these will be removed. If the second 
 * param is not given the action will check if there is a list of topics defined by the request
 * params.
 *
 * If when building an array of requestparams a -1 value is send in with other topicobjectids
 * the -1 will be ignored. Do do a delete of all scopes the -1 needs to be alone.
 */

public class SetScope implements ActionIF {
  
  public void perform(ActionParametersIF params, ActionResponseIF response) {
    //test params
    ActionSignature paramsType = ActionSignature.getSignature("boav t&?");
    paramsType.validateArguments(params, this);
    
    ScopedIF tmobject = (ScopedIF) params.get(0);
    TopicMapIF tm = tmobject.getTopicMap();
    Collection objScopes = tmobject.getScope();
    Collection newScopes = params.getCollection(1);
    
    if (newScopes == null){
      newScopes = new ArrayList();
      String[] reqScoped = params.getStringValues();
      
      if (reqScoped != null){
        int i = 0;
        while (i < reqScoped.length ){
          if (!reqScoped[i].equals("-1"))
            newScopes.add(tm.getObjectById(reqScoped[i]));
          
          i++;
        }
      }
    }
    Iterator newScopesIt = newScopes.iterator();
    
    //set new scopes
    while (newScopesIt.hasNext()){
      TopicIF scope = (TopicIF) newScopesIt.next();
      if (!objScopes.contains(scope))
        tmobject.addTheme(scope);
    }
    Iterator objScopesIt = new ArrayList(tmobject.getScope()).iterator();
    //remove old scopes
    while (objScopesIt.hasNext()){
      TopicIF scope = (TopicIF) objScopesIt.next();
      
      if (!newScopes.contains(scope))
        tmobject.removeTheme(scope);
    }
  }
  
}
