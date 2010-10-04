package ontopoly.conversion;

import ontopoly.model.OntopolyTopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

public class Upgrade_1_3 extends UpgradeBase {
  
  Upgrade_1_3(OntopolyTopicMapIF topicmap) throws InvalidQueryException {
    super(topicmap);
  }
  
  @Override
  protected void importLTM(StringBuffer sb) {
    sb.append("[on:datatype-html   : on:datatype = \"HTML\"]\n");
    sb.append("[on:datatype-image   : on:datatype = \"Image\"]\n");
    sb.append("[on:ontology-topic-type : on:topic-type]\n");
  }
  
  @Override
  protected void transform() throws InvalidQueryException {
    
    removeObjects(topicmap, dc,
        "select $A from association-role($A, $R1), type($A, xtm:superclass-subclass), " +
        "type($R1, xtm:superclass), role-player($R1, on:topic-type), " +
        "association-role($A, $R2), " +
        "type($R2, xtm:subclass), role-player($R2, on:ontology-topic-type)?");
  }
  
}
