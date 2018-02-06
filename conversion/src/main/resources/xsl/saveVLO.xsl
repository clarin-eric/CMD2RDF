<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:cmdi="http://www.clarin.eu/cmdi/"
    xmlns:ore="http://www.openarchives.org/ore/terms/"
    xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:dcr="http://www.isocat.org/ns/dcr.rdf#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
    xmlns:cmd="http://www.clarin.eu/cmd/"
    xmlns:vlo="http://www.clarin.eu/vlo/"
    xmlns:oa="http://www.w3.org/ns/oa#"
    xmlns:cmdm="http://www.clarin.eu/cmd/general.rdf#"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    exclude-result-prefixes="xs"
    version="2.0">
    
    <xsl:param name="vloOutputDir" select="'.'"/>
    
    <xsl:template match="/rdf:RDF">
        <xsl:message>DBG: out[<xsl:value-of select="concat($vloOutputDir,'/',replace(@xml:base,'.*/graph/',''))"/>]</xsl:message>
        <xsl:result-document href="{$vloOutputDir}/{replace(@xml:base,'.*/graph/','')}">
            <xsl:copy>
                <xsl:apply-templates/>
            </xsl:copy>
        </xsl:result-document>
        <xsl:copy-of select="."/>
    </xsl:template>
    
    <xsl:template match="node() | @*" mode="#all">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*" mode="#current"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="text()" priority="1"/>

    <xsl:template match="oa:hasBody">
        <xsl:copy>
            <rdf:Description rdf:about="{ancestor::oa:Annotation/@rdf:about}#vlo">
                <xsl:copy-of select="//vlo:*"/>
            </rdf:Description>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="rdf:Description[exists(vlo:*)]"/>
    <xsl:template match="vlo:*"/>
    
</xsl:stylesheet>