/*
 * #!
 * Ontopoly Editor
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */
package ontopoly.conversion;

import ontopoly.model.PSI;
import ontopoly.model.TopicMap;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

public class Upgrade_2_0 extends UpgradeBase {
  
  Upgrade_2_0(TopicMap topicmap) throws InvalidQueryException {
    super(topicmap);
  }
  
  @Override
  protected void importLTM(StringBuilder sb) {
    // add height field to name-field
    sb.append("on:has-field(on:name-field : on:field-owner, on:height : on:field-definition)\n");
    sb.append("on:field-in-view(on:name-field-embedded-view : on:fields-view, on:height : on:field-definition)\n");
    // add width field to name-field
    sb.append("on:has-field(on:name-field : on:field-owner, on:width : on:field-definition)\n");
    sb.append("on:field-in-view(on:name-field-embedded-view : on:fields-view, on:width : on:field-definition)\n");
    // add width field to identity-field
    sb.append("on:has-field(on:identity-field : on:field-owner, on:width : on:field-definition)\n");
    sb.append("on:field-in-view(on:identity-field-embedded-view : on:fields-view, on:width : on:field-definition)\n");

    // see issue 3
    sb.append("[on:untyped-name @\"http://psi.topicmaps.org/iso13250/model/topic-name\"]\n");
  }
  
  @Override
  protected void transform() throws InvalidQueryException {
    // rename http://psi.ontopia.net/ontology/role-type to "Role type"
    doUpdate("update value($TN, \"Role type\") " +
             "from topic-name(on:role-type, $TN)");

    // FIXME
    // http://psi.ontopia.net/ontology/rf-role-type_has-role-type
    // should be part of Advanced view only

    // FIXME
    // http://psi.ontopia.net/ontology/rf-association-field_has-association-type
    // should have cardinality 1:1 or 1:M
    //   - same applies to fields for name type, identity type, and
    //     occurrence type

    // FIXME    
    // http://psi.ontopia.net/ontology/rf-association-field_has-association-field
    // should have cardinality 1:M

    // FIXME (can't be bothered myself)
    // rename superclass to supertype and subclass to subtype

    // change PSI of default name type from own to TMDM PSI
    TopicMapIF tm = topicMap.getTopicMapIF();
    TopicIF defnametype =
      tm.getTopicBySubjectIdentifier(PSI.ON_DEPRECATED_UNTYPED_NAME);
    defnametype.removeSubjectIdentifier(PSI.ON_DEPRECATED_UNTYPED_NAME);
    defnametype.addSubjectIdentifier(PSI.TMDM_TOPIC_NAME);
  }

}
