package ontopoly.conversion;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import ontopoly.model.TopicMap;

public class Upgrade_2_1 extends UpgradeBase {
  
  Upgrade_2_1(TopicMap topicmap) throws InvalidQueryException {
    super(topicmap);
  }
  
  @Override
  protected void importLTM(StringBuffer sb) {
    // Add support for query fields
    sb.append("[on:query-field : on:topic-type on:system-topic @\"http://psi.ontopia.net/ontology/query-field\" = \"Query field\"]\n");
    sb.append("xtm:superclass-subclass(on:field-definition : xtm:superclass, on:query-field : xtm:subclass)\n");

    // Add view mode "Popup"
    sb.append("[on:view-mode-popup : on:view-mode on:system-topic = \"Popup\"]");

    // Mark on:topic-type as being on:field-set
    sb.append("[on:field-set : on:topic-type on:system-topic @\"http://psi.ontopia.net/ontology/field-set\" = \"Field set\"]\n");
    sb.append("[on:topic-type : on:field-set]\n");
    
    // FIXME: add xsd:boolean?
    
  }
  
  @Override
  protected void transform() throws InvalidQueryException {
  }

}
