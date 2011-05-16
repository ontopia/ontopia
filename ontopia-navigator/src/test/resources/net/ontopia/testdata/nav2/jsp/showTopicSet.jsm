<module>

  <!-- This function displays a set of topics. It assumes the caller has
       already displayed a nice header. -->

  <function name="show-topic-set" params="topics">
    <logic:foreach name="topics">
      * <output:name/>
    </logic:foreach>-----------------------------------------------------------
  </function>
</module>
