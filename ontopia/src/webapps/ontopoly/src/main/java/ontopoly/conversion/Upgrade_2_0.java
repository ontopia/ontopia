package ontopoly.conversion;

import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.TopicMap;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

public class Upgrade_2_0 extends UpgradeBase {
  
  Upgrade_2_0(TopicMap topicmap) throws InvalidQueryException {
    super(topicmap);
  }
  
  @Override
  protected void importLTM(StringBuffer sb) {
    // add height field to name-field
    sb.append("on:has-field(on:name-field : on:field-owner, on:height : on:field-definition)\n");
    sb.append("on:field-in-view(on:name-field-embedded-view : on:fields-view, on:height : on:field-definition)\n");
    // add width field to name-field
    sb.append("on:has-field(on:name-field : on:field-owner, on:width : on:field-definition)\n");
    sb.append("on:field-in-view(on:name-field-embedded-view : on:fields-view, on:width : on:field-definition)\n");
    // add width field to identity-field
    sb.append("on:has-field(on:identity-field : on:field-owner, on:width : on:field-definition)\n");
    sb.append("on:field-in-view(on:identity-field-embedded-view : on:fields-view, on:width : on:field-definition)\n");

  }
  
  @Override
  protected void transform() throws InvalidQueryException {
    // rename http://psi.ontopia.net/ontology/role-type to "Role type"
    // http://psi.ontopia.net/ontology/rf-role-type_has-role-type should be part of Advanced view only
    // http://psi.ontopia.net/ontology/rf-association-field_has-association-type should have cardinality 1:1 or 1:M
    //   - same applies to fields for name type, identity type, and occurrence type
    // http://psi.ontopia.net/ontology/rf-association-field_has-association-field should have cardinality 1:M
    
    // rename superclass to supertype and subclass to subtype
    // rename subject identifier to PSI?
  }

}
