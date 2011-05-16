
<!--

 This XSLT stylesheet converts specifications to HTML.
 $Id: dbspec2html.xslt,v 1.1 2001/05/03 15:13:53 larsga Exp $

-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">


  <xsl:import href = "docbook2html.xslt" />


  <xsl:template match = "reqlist">
    <dl>
    <xsl:apply-templates/>
    </dl>
  </xsl:template>

  <xsl:template match = "req">
    <dt><a name="{@id}"><xsl:value-of select = "@id" /></a></dt>
    <dd><xsl:apply-templates/></dd>
  </xsl:template>

  <xsl:template match = "subreqlist">
    <dt><a name="{@id}"><xsl:value-of select = "@id" /></a></dt>
    <dd><xsl:apply-templates/></dd>
  </xsl:template>

  <xsl:template match = "ireq">
    [<span title="{@id}" class="ireq"><xsl:apply-templates/></span>]
  </xsl:template>

  <xsl:template match = "reqref">
    <a href="#{@ref}"><xsl:value-of select="@ref"/></a>
  </xsl:template>

</xsl:stylesheet>
