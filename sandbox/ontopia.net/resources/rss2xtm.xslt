<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">

<xsl:template match="channel">
  <topicMap version="2.0" xmlns="http://www.topicmaps.org/xtm/">

    <topic id="the-blog">
      <subjectLocator href="{link}"/>
      <instanceOf>
        <topicRef href="#blog"/>
      </instanceOf>
      <name>
        <value><xsl:value-of select="title"/></value>
      </name>
      <occurrence>
        <type>
          <topicRef href="#description"/>
        </type>
        <resourceData><xsl:value-of select="description"/></resourceData>
      </occurrence>
    </topic>

    <xsl:apply-templates/>
    
    <!-- static ontology -->

    <topic id="blog">
      <subjectIdentifier href="http://psi.ontopedia.net/Blogging/Blog"/>
    </topic>
    <topic id="post">
      <subjectIdentifier href="http://psi.ontopedia.net/Blogging/Post"/>
    </topic>
    <topic id="has-post">
      <subjectIdentifier href="http://psi.ontopedia.net/Blogging/Blog_has_post"/>
    </topic>
    <topic id="description">
      <subjectIdentifier href="http://purl.org/dc/elements/1.1/description"/>
    </topic>
    <topic id="date">
      <subjectIdentifier href="http://purl.org/dc/elements/1.1/date"/>
    </topic>
  </topicMap>
</xsl:template>

<xsl:template match="item">
  <xsl:variable name="month" select="substring(pubDate, 9, 3)"/>
  <topic id="{generate-id(.)}" xmlns="http://www.topicmaps.org/xtm/">
    <subjectLocator href="{link}"/>
    <instanceOf>
      <topicRef href="#post"/>
    </instanceOf>
    <name>
      <value><xsl:value-of select="title"/></value>
    </name>
    <occurrence>
      <type>
        <topicRef href="#description"/>
      </type>
      <resourceData><xsl:value-of select="description"/></resourceData>
    </occurrence>
    <occurrence>
      <type>
        <topicRef href="#date"/>
      </type>
      <!-- Wed, 03 Mar 2010 13:11:24 +0000 -->
      <resourceData>
        <xsl:value-of select="substring(pubDate, 13, 4)"/>
        <xsl:text>-</xsl:text>
        <xsl:choose>
          <xsl:when test="$month = 'Jan'">01</xsl:when>
          <xsl:when test="$month = 'Feb'">02</xsl:when>
          <xsl:when test="$month = 'Mar'">03</xsl:when>
          <xsl:when test="$month = 'Apr'">04</xsl:when>
          <xsl:when test="$month = 'May'">05</xsl:when>
          <xsl:when test="$month = 'Jun'">06</xsl:when>
          <xsl:when test="$month = 'Jul'">07</xsl:when>
          <xsl:when test="$month = 'Aug'">08</xsl:when>
          <xsl:when test="$month = 'Sep'">09</xsl:when>
          <xsl:when test="$month = 'Oct'">10</xsl:when>
          <xsl:when test="$month = 'Nov'">11</xsl:when>
          <xsl:when test="$month = 'Dec'">12</xsl:when>
          <xsl:otherwise>
            <xsl:message terminate="yes">Unknown month:
            <xsl:value-of select="$month"/>!</xsl:message>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:text>-</xsl:text>
        <xsl:value-of select="substring(pubDate, 6, 2)"/>
        <xsl:text> </xsl:text>
        <xsl:value-of select="substring(pubDate, 18, 8)"/>
      </resourceData>
    </occurrence>
  </topic>

  <association xmlns="http://www.topicmaps.org/xtm/">
    <type>
      <topicRef href="#has-post"/>
    </type>
    <role>
      <type>
        <topicRef href="#post"/>
      </type>
      <topicRef href="#{generate-id(.)}"/>
    </role>
    <role>
      <type>
        <topicRef href="#blog"/>
      </type>
      <topicRef href="#the-blog"/>
    </role>
  </association>
</xsl:template>

<xsl:template match="text()"/>

</xsl:stylesheet>