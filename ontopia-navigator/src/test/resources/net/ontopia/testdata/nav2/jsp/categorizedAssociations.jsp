<logic:context tmparam="tm" objparam="id" set="topic">
        <!-- ================= Associations ==================== -->
	<logic:set name="roles" comparator="off">
	  <tm:roles of="topic"/>
	</logic:set>
	<logic:set name="assocs" comparator="assocComparator">
	  <tm:associations of="roles" contextFilter="user"/>
	</logic:set>
	<logic:set name="assocTypes"><!-- main loop is running over this collection -->
	  <tm:classesOf of="assocs"/>
	</logic:set>
	<logic:set name="assocTopics" comparator="off">
	  <tm:associations of="topic" contextFilter="user"/>
	</logic:set>
	
        <p><table class="shboxed" width="100%"><tr><td>
    	<logic:if name="assocTypes">
    	  <logic:then>
    	    <h3>Related Subjects</h3>
    	    <ul>
    	      <logic:foreach set="type">
                  <li><b><output:element name="a">
        	    <output:attribute name="href"><output:link template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/></output:attribute>
        	    <output:name/><!-- basenameScope="types" -->
        	  </output:element></b>
  		  <logic:set name="assocs" comparator="assocComparator">
  		    <tm:filter instanceOf="type">
  		      <value:copy of="assocTopics"/>
  		    </tm:filter>
  		  </logic:set>
  		  <ul>
		    <!-- ===== List binary associations ===== -->
                    <logic:set name="roles" comparator="assocRoleComparator">
		      <tm:roles of="assocs" remove="topic" cardinality="binary" />
		    </logic:set>
		    <logic:if name="roles"><logic:then>
		    <logic:foreach name="roles">
		      <logic:set name="player" comparator="off"><tm:topics/></logic:set>
		      <logic:set name="roletype" comparator="off"><tm:classesOf/></logic:set>
		      <li type='circle'><output:element name="a">
        	        <output:attribute name="href"><output:link of="player" template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/></output:attribute>
        	        <output:name of="player"/>
        	      </output:element>
		      (<output:element name="a">
        	        <output:attribute name="href"><output:link of="roletype" template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/></output:attribute>
        	        <output:name of="roletype"/>
        	      </output:element>)</li>
		    </logic:foreach>
		    </logic:then></logic:if>
		    <!-- ===== List n-ary associations ===== -->
		    <logic:foreach name="assocs">
  		      <logic:set name="roles" comparator="off">
  			<tm:roles remove="topic" cardinality="nary" />
  		      </logic:set>
  		      <logic:if name="roles"><logic:then>
		      <span class='nary'>
  		      <logic:foreach name="roles">
  			<logic:set name="player" comparator="off"><tm:topics/></logic:set>
  			<logic:set name="roletype" comparator="off"><tm:classesOf/></logic:set>
  			<li type='circle'><output:element name="a">
  			  <output:attribute name="href"><output:link of="player" template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/></output:attribute>
  			  <output:name of="player"/>
  			</output:element>
  			(<output:element name="a">
  			  <output:attribute name="href"><output:link of="roletype" template="topic_%model%.jsp?tm=%topicmap%&amp;id=%id%" generator="modelLinkGenerator"/></output:attribute>
  			  <output:name of="roletype"/>
  			</output:element>)</li>
  		      </logic:foreach>
		      </span>
  		      </logic:then></logic:if>
  		    </logic:foreach>
  		  </ul>

		</li>
  	      </logic:foreach>
    	    </ul>
    	  </logic:then>
    	  <logic:else>
    	    No associations available for this topic.
    	  </logic:else>
        </logic:if>
	</td></tr></table></p>
</logic:context>
