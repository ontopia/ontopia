
package net.ontopia.topicmaps.webed.impl.actions;

import java.util.*;
import net.ontopia.utils.ontojsp.FakeServletRequest;
import net.ontopia.utils.ontojsp.FakeServletResponse;
import net.ontopia.topicmaps.webed.core.*;
import net.ontopia.topicmaps.webed.impl.basic.*;
import net.ontopia.topicmaps.webed.impl.actions.*;
import net.ontopia.topicmaps.webed.impl.actions.association.*;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.*;
import net.ontopia.infoset.core.*;

import net.ontopia.topicmaps.query.core.*;
import net.ontopia.topicmaps.query.utils.*;

public class TestAssignRolePlayer extends AbstractWebedTestCase {
  
  public TestAssignRolePlayer(String name) {
    super(name);
  }

  
  /*
    1. Good, Normal use, with player
    2. Good, Normal use, player as req.
  */

  


  public void testNormalOperationMakeNewAssoc() throws java.io.IOException, InvalidQueryException{
    AssociationIF assoc = (AssociationIF) tm.getAssociations().iterator().next();
    TopicIF topicRole = getTopicById(tm, "player");
    TopicIF topicRole2 = getTopicById(tm, "team");
    TopicIF topicPlayer = getTopicById(tm, "gamst");
    TopicIF topicPlayer2 = getTopicById(tm, "rosenborg");
    TopicIF topicType = getTopicById(tm, "plays-for");
    
    //make action
    ActionIF action = new AssignRolePlayer();

    List list = new ArrayList(6);
    list.add(Collections.EMPTY_LIST);
    list.add(Collections.singleton(topicType));
    list.add(Collections.singleton(topicPlayer));
    list.add(Collections.singleton(topicRole));
    list.add(Collections.singleton(topicRole2));
    list.add(Collections.singleton(topicPlayer2));
    

    //build parms
    ActionParametersIF params = makeParameters(list, "abc");
    ActionResponseIF response = makeResponse();
    try{
      //execute    
      action.perform(params, response);
      
      //test      
      String query = 
	"select $ASSOC from role-player($ROLE1, gamst), type($ROLE1, player), role-player($ROLE2, rosenborg), type($ROLE2, team), association-role($ASSOC, $ROLE1), association-role($ASSOC, $ROLE2)?";
      
      QueryResultIF result = runQuery(query);
      boolean next = result.next();
      AssociationIF assocRes = (AssociationIF) result.getValue("ASSOC");
      //dbPrint(assocRes);
      assertFalse("assosiation not made", assocRes == null);
      Collection res = (Collection) assocRes.getRoles();
      //dbPrint(res);
    }catch (ActionRuntimeException e){
      fail("Good everything, should work");
    }
  }

  public void testNormalOperationMakeNewAssocWreq() throws java.io.IOException{
    AssociationIF assoc = (AssociationIF) tm.getAssociations().iterator().next();
    TopicIF topicRole = getTopicById(tm, "player");
    TopicIF topicRole2 = getTopicById(tm, "team");
    TopicIF topicPlayer = getTopicById(tm, "gamst");
    TopicIF topicPlayer2 = getTopicById(tm, "rosenborg");
    TopicIF topicType = getTopicById(tm, "plays-for");
    String topicId = topicPlayer2.getObjectId();
    
    //make action
    ActionIF action = new AssignRolePlayer();
    
    List list = new ArrayList(6);
    list.add(Collections.EMPTY_LIST);
    list.add(Collections.singleton(topicType));
    list.add(Collections.singleton(topicPlayer));
    list.add(Collections.singleton(topicRole));
    list.add(Collections.singleton(topicRole2));
    list.add(Collections.singleton(null));
    
    
    //build parms
    ActionParametersIF params = makeParameters(list, topicId);
    ActionResponseIF response = makeResponse();
    //try{
      //execute    
    action.perform(params, response);
      
      //test      
      //}
    
      //catch (ActionRuntimeException e){
      //fail("Good everything, should work");
      //}
    
    
  }
}


