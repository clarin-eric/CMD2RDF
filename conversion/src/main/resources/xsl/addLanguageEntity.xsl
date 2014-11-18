<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:skos="http://www.w3.org/2004/02/skos/core#"
    xmlns:vlo="http://www.clarin.eu/vlo/"
    exclude-result-prefixes="xs"
    version="2.0">
    
    <!-- identity copy -->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
	<xsl:template match="vlo:hasFacetISO6393ElementValue" priority="1">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
        <xsl:if test="not(normalize-space()=('','und'))">
            <!-- found a ISO 639-3 code -->
        	<!-- link to dbpedia -->
        	<vlo:hasFacetISO6393ElementEntity rdf:resource="http://dbpedia.org/resource/ISO_639:{normalize-space(.)}"/>
        	<!-- link to lexvo -->
        	<vlo:hasFacetISO6393ElementEntity rdf:resource="http://lexvo.org/id/iso639-3/{normalize-space(.)}"/>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>