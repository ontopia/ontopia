<logic:context tmparam="tm" settm="topicmap">

  <logic:set name="basenameThemes"><tm:themes of="basename"/></logic:set>
  <logic:set name="variantThemes"><tm:themes of="variant"/></logic:set>
  <logic:set name="occurrenceThemes"><tm:themes of="occurrence"/></logic:set>
  <logic:set name="associationThemes"><tm:themes of="association"/></logic:set>

  Themes :
-------------------------------------------------------------
  Basename Themes (<output:count of="basenameThemes"/>):
  <logic:foreach name="basenameThemes">
    <output:name />
  </logic:foreach>

  Variant Themes (<output:count of="variantThemes"/>):
  <logic:foreach name="variantThemes">
    <output:name />
  </logic:foreach>

  Occurrence Themes (<output:count of="occurrenceThemes"/>):
  <logic:foreach name="occurrenceThemes">
    <output:name />
  </logic:foreach>

  Association Themes (<output:count of="associationThemes"/>):
  <logic:foreach name="associationThemes">
    <output:name />
  </logic:foreach>

  <logic:set name="themes"><tm:themes/></logic:set>

  All themes (<output:count of="themes"/>): 
  <logic:foreach name="themes">
    <output:name />
  </logic:foreach>
 
</logic:context>
