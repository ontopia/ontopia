package net.ontopia.topicmaps.query.toma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;

@SuppressWarnings("unchecked")
public class ScopeTest extends AbstractTomaQueryTestCase {
  
  public ScopeTest(String name) {
    super(name);
  }

  /// context management

  public void setUp() {
    QueryMatches.initialSize = 1;
  }

  public void tearDown() {
    closeStore();
  }

  /// association tests
  
  public void testScopeUsingPath() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T", getTopicById("topic-maps"));
    
    verifyQuery(matches, "select $t where exists $t.oc(specification)@english;");
  }  

  public void testAssignScope() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    fillMatchesWithVariantsAndScopes(matches, getTopicById("xtm"));
    fillMatchesWithVariantsAndScopes(matches, getTopicById("ltm"));
    
    verifyQuery(matches, "select $t, $t.name.var@$scope, $scope where $t.type = format;");
  }
  
  private void fillMatchesWithVariantsAndScopes(List matches, TopicIF topic) {
    Collection names = topic.getTopicNames();
    for (Object name : names) {
      TopicNameIF n = (TopicNameIF) name;
      for (Object o : n.getVariants()) {
        VariantNameIF var = (VariantNameIF) o;
        Collection scopes = var.getScope();
        if (scopes == null || scopes.isEmpty()) {
          addMatch(matches, "$T", topic, "$T.NAME.VAR@$SCOPE", o, "$SCOPE", null);
        } else {
          for (Object scope : scopes) {
            addMatch(matches, "$T", topic, "$T.NAME.VAR@$SCOPE", o, "$SCOPE", scope);
          }
        }
      }
    }
  }
}
