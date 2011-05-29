<!--
     Definition of functions that are used inside the topicmap page
     for the 'complete' model.
     ...........................................................................
-->

<module>

  <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
       function "view_master": show a list of all topics available in the
       topic map.
       ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
  <function name="view_master" params="topicmap">
      <!-- ============== List of all topics ================ -->
      <tolog:set query='using on for i"http://psi.ontopia.net/ontology/"
        select $individuals from
        topic($individuals),
        $individuals /= on:system-topic,
        not({
          instance-of($individuals, on:system-topic) |
          instance-of($individuals, on:cardinality) |
          instance-of($individuals, on:datatype) |
          instance-of($individuals, on:identity-field) |
          instance-of($individuals, on:interface-control)
        }) order by $individuals?'/>
      <tolog:set query='instance-of(%individuals%, $types)?'/>
      <tolog:set query='using xtm for i"http://www.topicmaps.org/xtm/1.0/core.xtm#"
         xtm:superclass-subclass(%types% : xtm:superclass, $subtypes : xtm:subclass)?'/>
      <tolog:set query='using xtm for i"http://www.topicmaps.org/xtm/1.0/core.xtm#"
         xtm:superclass-subclass($supertypes : xtm:superclass, %types% : xtm:subclass)?'/>
      <logic:set name="topics">
        <value:union>
          <value:copy of="subtypes"/>
          <value:copy of="supertypes"/>
          <value:copy of="individuals"/>
          <value:copy of="types"/>
        </value:union>
      </logic:set>
      <table class="shboxed" width="100%"><tr><td>
      <h3>All Topics (<output:count of="topics"/>)</h3>
      <ul>
        <logic:foreach name="topics">
          <li><output:element name="a"><output:attribute name="href"><output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/></output:attribute><output:name /></output:element></li>
        </logic:foreach>
      </ul>
      </td></tr></table>
  </function>


  <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
       function "view_individuals": show a list of all topics except those
       that define the ontology
       ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
  <function name="view_individuals" params="topicmap">
      <!-- ============== List of all topics ================ -->

      <tolog:set query='using on for i"http://psi.ontopia.net/ontology/"
        select $individuals from
          topic($individuals),
          $individuals /= on:system-topic,
          not(instance-of($individuals, $system-topic),
              instance-of($system-topic, on:system-topic)),
          not(instance-of($individuals, on:system-topic))
          order by $individuals?'/>
      <table class="shboxed" width="100%"><tr><td>
      <h3>Non-Typing Topics (<output:count of="individuals"/>)</h3>
      <ul>
        <logic:foreach name="individuals">
          <li><output:element name="a"><output:attribute name="href"><output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/></output:attribute><output:name /></output:element></li>
        </logic:foreach>
      </ul>
      </td></tr></table>
  </function>


  <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
       function "view_nonames": show a list of all topics that have no base name
       ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
  <function name="view_nonames" params="topicmap">
      <!-- ============== List of all unnamed topics ================ -->
      <logic:set name="nonames">
        <tm:tolog query="select $T, $C from
          topic($T),
          not(topic-name($T, $N)),
        { instance-of($T, $C) }?"/>
      </logic:set>
      <logic:if name="nonames"><logic:then>
        <table class="shboxed" width="100%">
        <tr><td>
        <h3><span title="Topics with no base names (types in parentheses)">Unnamed Topics (<output:count/>)</span></h3>
        <ul>
          <logic:foreach name="nonames"><logic:bind>
            <li><output:element name="a"><output:attribute name="href"><output:link of="T"
            template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%"
            generator="modelLinkGenerator"/></output:attribute>{<output:id of="T"/>}</output:element><logic:if
            name="C"><logic:then> (<output:element name="a"><output:attribute name="href"><output:link
            of="C" template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%"
            generator="modelLinkGenerator"/></output:attribute><output:name
            of="C"/></output:element>)</logic:then></logic:if></li>
          </logic:bind></logic:foreach>
        </ul>
        </td></tr>
        </table>
      </logic:then>
      <logic:else>
        <table class="shboxed" width="100%"><tr><td>
        No unnamed topics in this topic map.</td></tr></table>
      </logic:else>
      </logic:if>
  </function>


  <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
       function "view_themes": showing the topics used to define themes
       sorted by topic (HyTM), name, occurrence and association themes.
       ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
  <function name="view_themes" params="topicmap">
      <!-- ============== List of basename themes ================ -->
      <logic:set name="basenameThemes"><tm:themes of="basename"/></logic:set>
      <logic:if name="basenameThemes"><logic:then>
      <table class="shboxed" width="100%"><tr><td>
      <h3>Base Name Themes (<output:count/>)</h3>
      <ul>
        <logic:foreach>
          <li><output:element name="a"><output:attribute name="href"><output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/></output:attribute><output:name /></output:element></li>
        </logic:foreach>
      </ul>
      </td></tr></table>
      </logic:then></logic:if>

      <!-- ============== List of variant themes ================ -->
      <logic:set name="variantThemes"><tm:themes of="variant"/></logic:set>
      <logic:if name="variantThemes"><logic:then>
      <table class="shboxed" width="100%"><tr><td>
      <h3>Variant Name Themes (<output:count/>)</h3>
      <ul>
        <logic:foreach>
          <li><output:element name="a"><output:attribute name="href"><output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/></output:attribute><output:name /></output:element></li>
        </logic:foreach>
      </ul>
      </td></tr></table>
      </logic:then></logic:if>

      <!-- ============== List of occurrence themes ================ -->
      <logic:set name="occurrenceThemes"><tm:themes of="occurrence"/></logic:set>
      <logic:if name="occurrenceThemes"><logic:then>
      <table class="shboxed" width="100%"><tr><td>
      <h3>Occurrence Themes (<output:count/>)</h3>
      <ul>
        <logic:foreach>
          <li><output:element name="a"><output:attribute name="href"><output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/></output:attribute><output:name /></output:element></li>
        </logic:foreach>
      </ul>
      </td></tr></table>
      </logic:then></logic:if>

      <!-- ============== List of association themes ================ -->
      <logic:set name="associationThemes"><tm:themes of="association"/></logic:set>
      <logic:if name="associationThemes"><logic:then>
      <table class="shboxed" width="100%"><tr><td>
      <h3>Association Themes (<output:count/>)</h3>
      <ul>
        <logic:foreach>
          <li><output:element name="a"><output:attribute name="href"><output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/></output:attribute><output:name /></output:element></li>
        </logic:foreach>
      </ul>
      </td></tr></table>
      </logic:then></logic:if>
  </function>


  <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
       function "view_ontology": show links to topics that are used to
       define the ontology of the current topic map.
       ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
  <function name="view_ontology" params="topicmap">

      <!-- ============== List of topic types =============== -->
      <tolog:set query='using on for i"http://psi.ontopia.net/ontology/"
        select $types from topic($T),
        instance-of($T, $types),
        $types /= on:system-topic,
        not(instance-of($types, on:system-topic))
        order by $types?'/>
      <table class="shboxed" width="100%"><tr><td>
      <h3><span title="Topics that type other topics">Topic Types (<output:count of="types"/>)</span></h3>
      <ul>
        <logic:foreach name="types" start="0">
          <li><output:element name="a"><output:attribute name="href"><output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/></output:attribute><output:name /></output:element></li>
        </logic:foreach>
      </ul>
      </td></tr></table>

      <!-- ============== List of association types ========= -->
      <tolog:set query='using on for i"http://psi.ontopia.net/ontology/"
        using xtm for i"http://www.topicmaps.org/xtm/1.0/core.xtm#"
        select $types from association($T),
        type($T, $types),
        not(instance-of($types, on:system-topic), $types /= xtm:superclass-subclass)
        order by $types?'/>
      <logic:if name="types"><logic:then>
          <table class="shboxed" width="100%"><tr><td>
          <h3><span title="Topics that type associations">Association Types (<output:count/>)</span></h3>
          <ul>
            <logic:foreach>
              <li><output:element name="a"><output:attribute name="href"><output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/></output:attribute><output:name /></output:element></li>
            </logic:foreach>
          </ul>
          </td></tr></table>
      </logic:then></logic:if>

      <!-- ============== List of role types ========= -->
      <tolog:set query='using on for i"http://psi.ontopia.net/ontology/"
        select $types from association-role($A, $T),
        type($T, $types),
        not(instance-of($types, on:system-topic))
        order by $types?'/>
      <logic:if name="types"><logic:then>
        <table class="shboxed" width="100%"><tr><td>
        <h3><span title="Topics that type association roles">Association Role Types (<output:count/>)</span></h3>
        <ul>
          <logic:foreach>
            <li><output:element name="a"><output:attribute name="href"><output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/></output:attribute><output:name /></output:element></li>
          </logic:foreach>
        </ul>
        </td></tr></table>
      </logic:then></logic:if>

      <!-- ============== List of occurrence types ========= -->
      <tolog:set query='using on for i"http://psi.ontopia.net/ontology/"
        select $types from occurrence($A, $T),
        type($T, $types),
        not(instance-of($types, on:system-topic))
        order by $types?'/>
      <logic:if name="types"><logic:then>
        <table class="shboxed" width="100%"><tr><td>
        <h3><span title="Topics that type occurrences">Occurrence Types (<output:count/>)</span></h3>
        <ul>
          <logic:foreach>
            <li><output:element name="a"><output:attribute name="href"><output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/></output:attribute><output:name /></output:element></li>
          </logic:foreach>
        </ul>
        </td></tr></table>
      </logic:then></logic:if>
  </function>


  <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
       function "show_subclasses": show the subtype tree of a type as a
       nested list
        superclass: class to find subclasses of
        supers:     classes already displayed higher up (avoid loops)
       ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
  <function name="show_subclasses" params="superclass supers">
    <logic:set name="subclasses">
      <value:difference>
        <tm:subclasses of="superclass" level="1"/>
        <value:copy of="supers"/>
      </value:difference>
    </logic:set>
    <logic:if name="subclasses"><logic:then>
    <ul>
    <logic:foreach name="subclasses">
      <li><output:element name="a"><output:attribute name="href"><output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/></output:attribute><output:name /></output:element>

        <logic:call name="show_subclasses">
          <logic:set name="superclass"><value:copy/></logic:set>
          <logic:set name="supers">
            <value:union>
              <value:copy/>
              <value:copy of="supers"/>
            </value:union>
          </logic:set>
        </logic:call>
      </li>
    </logic:foreach>
    </ul>
    </logic:then></logic:if>
  </function>

  <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
       function "show_metadata": shows topic map metadata
       ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
  <function name="show_metadata" params="topicmap">
    <logic:set name="tm_topic"><tm:reifier of="topicmap" /></logic:set>
    <logic:set name="metadata">
      <tm:classesOf>
        <value:union>
          <tm:associations of="tm_topic"/>
          <tm:occurrences of="tm_topic"/>
        </value:union>
      </tm:classesOf>
    </logic:set>
    <logic:if name="metadata">
      <logic:then>
      <logic:set name="occs"><tm:occurrences of="tm_topic" type="internal"/></logic:set>
      <table class="shboxed" width="100%"><tr><td>
      <h3><span title="Associations and occurrences of the topic that reifies this topic map">Topic Map Metadata</span></h3>
      <ul>
      <logic:foreach name="metadata" set="prop">
        <logic:set name="occ">
          <tm:filter instanceOf="prop">
            <tm:occurrences of="tm_topic" type="internal"/>
          </tm:filter>
        </logic:set>
        <logic:if name="occ">
        <logic:then>
          <li><b><output:element name="a"><output:attribute name="href"><output:link
          of="prop" template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%"
          generator="modelLinkGenerator"/></output:attribute><output:name of="prop"/></output:element>:</b>
          <logic:foreach name="occ" separator="; "><output:content/></logic:foreach>
          </li>
        </logic:then>
        <logic:else>
        <logic:set name="assoc">
          <tm:associated from="tm_topic" type="prop"/>
        </logic:set>
        <logic:if name="assoc">
        <logic:then>
          <li><b><output:element name="a"><output:attribute name="href"><output:link
          of="prop" template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%"
          generator="modelLinkGenerator"/></output:attribute><output:name of="prop"/></output:element>:</b>
          <logic:if sizeEquals="1">
          <logic:then>
             <output:element name="a">
               <output:attribute name="href">
                 <output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/>
               </output:attribute>
               <output:name/>
             </output:element>
          </logic:then>
          <logic:else><ul>
          <logic:foreach>
             <li type="circle"><output:element name="a">
               <output:attribute name="href">
                 <output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/>
               </output:attribute>
               <output:name/>
             </output:element></li>
          </logic:foreach>
          </ul></logic:else>
          </logic:if>
          </li>
        </logic:then>
        </logic:if>
        </logic:else>
        </logic:if>
      </logic:foreach>
      </ul>
      </td></tr></table>
      </logic:then>
    </logic:if>
  </function>


  <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
       function "show-hierarchies": shows hierarchical association types
   ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
  <function name="show-hierarchies">
    <logic:set name="assoc-types">
      <tm:instances>
        <tm:lookup indicator="http://www.techquila.com/psi/hierarchy/#hierarchical-relation-type"/>
      </tm:instances>
    </logic:set>

    <logic:if name="assoc-types">
      <logic:then>

      <table class="shboxed" width="100%"><tr><td>
      <h3><span title="Association types that form hierarchies">Hierarchies</span></h3>
      <ul>
      <logic:foreach name="assoc-types" set="assoctype">
        <li><output:element name="a"><output:attribute name="href"><output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/></output:attribute><output:name /></output:element></li>
      </logic:foreach>
      </ul>
      </td></tr></table>
      </logic:then>
    </logic:if>
  </function>

</module>
