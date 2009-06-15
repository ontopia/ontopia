package ontopoly.conversion;

import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.TopicMap;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

public class Upgrade_1_2 extends UpgradeBase {
  
  Upgrade_1_2(TopicMap topicmap) throws InvalidQueryException {
    super(topicmap);
  }
  
  @Override
  protected void importLTM(StringBuffer sb) {
    // #2005 bug fix
    sb.append("[xtm:superclass-subclass : tech:hierarchical-relation-type]\n");
    sb.append("[xtm:superclass : tech:superordinate-role-type]\n");
    sb.append("[xtm:subclass : tech:subordinate-role-type]\n");

    // patterns support on datatypes (including date and datetime)
    sb.append("[on:pattern : on:occurrence-type on:system-topic = \"Pattern\"]\n");
    sb.append("on:is-hidden(on:pattern : on:field-type)\n");
    sb.append("{xsd:date, on:pattern, [[YYYY-MM-DD]]}\n");
    sb.append("{xsd:dateTime, on:pattern, [[YYYY-MM-DD HH:MM:SS]]}\n");

    // browse dialog as new interface control
    sb.append("[on:browse-dialog  : on:interface-control = \"Browse dialog\"]\n");

    // identity field should be instance of ontology topic type
    // ???
  }
  
  @Override
  protected void transform() throws InvalidQueryException {
  }
  
}
