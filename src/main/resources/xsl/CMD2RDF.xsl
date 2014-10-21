<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE rdf:RDF [
    <!ENTITY rdf 'http://www.w3.org/1999/02/22-rdf-syntax-ns#'>
    <!ENTITY rdfs 'http://www.w3.org/TR/WD-rdf-schema#'>
    <!ENTITY xsd 'http://www.w3.org/2001/XMLSchema#'>
]>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
    xmlns:dcr="http://www.isocat.org/ns/dcr.rdf#"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
    xmlns:cmd="http://www.clarin.eu/cmd/"
>
    <!-- location of the registry -->
    <xsl:param name="registry" select="'http://catalog.clarin.eu/ds/ComponentRegistry'"/>
    
    <!-- separator when traversing the component/element hierarchy --> 
    <xsl:variable name="STEP" select="'.'"/>
    
    <!-- XSD data types allowed in RDF (see http://www.w3.org/TR/2004/REC-rdf-mt-20040210/#DTYPEINTERP) -->
    <xsl:variable name="RDF-XSD-DataTypes" select="(
        'string',
        'boolean',
        'decimal',
        'float',
        'double',
        'dateTime',
        'time',
        'date',
        'gYearMonth',
        'gYear',
        'gMonthDay',
        'gDay',
        'gMonth',
        'hexBinary',
        'base64Binary',
        'anyURI',
        'normalizedString',
        'token',
        'language',
        'NMTOKEN',
        'Name',
        'NCName',
        'integer',
        'nonPositiveInteger',
        'negativeInteger',
        'long',
        'int',
        'short',
        'byte',
        'nonNegativeInteger',
        'unsignedLong',
        'unsignedInt',
        'unsignedShort',
        'unsignedByte',
        'positiveInteger'
    )"/>
    
    <!-- append a step to a path -->
    <xsl:function name="cmd:path">
        <xsl:param name="context"/>
        <xsl:param name="name"/>
        <xsl:sequence select="concat($context,if (normalize-space($context)='') then ('') else ($STEP),$name)"/>
    </xsl:function>

    <!-- turn a CMD XSD datatype into a XSD datatype allowed by RDF -->
    <xsl:function name="cmd:datatype">
        <xsl:param name="datatype"/>
        <xsl:sequence select="if ($datatype=$RDF-XSD-DataTypes) then ($datatype) else ('string')"/>
    </xsl:function>
    
    <!-- make sure we use a clean ID -->
    <xsl:function name="cmd:id">
        <xsl:param name="id"/>
        <xsl:sequence select="replace($id,'.*(clarin.eu:cr[0-9]+:p_[0-9]+).*','$1')"/>
    </xsl:function>
    
    <!-- the registry URL for a profile -->
    <xsl:function name="cmd:ppath">
        <xsl:param name="id"/>
        <xsl:param name="ext"/>
        <xsl:sequence select="concat($registry,'/rest/registry/profiles/',cmd:id($id),'/',$ext)"/>
    </xsl:function>

	<!-- load a profile from the registry -->
	<xsl:function name="cmd:profile">
		<xsl:param name="id"/>
		<xsl:sequence select="doc(cmd:ppath($id,'xml'))"/>
	</xsl:function>
	
	<!-- the registry URL for a component -->
    <xsl:function name="cmd:cpath">
        <xsl:param name="id"/>
        <xsl:param name="ext"/>
        <xsl:sequence select="concat($registry,'/rest/registry/components/',cmd:id($id),'/',$ext)"/>
    </xsl:function>
    
	<!-- load a component from the registry -->
	<xsl:function name="cmd:component">
		<xsl:param name="id"/>
		<xsl:sequence select="doc(cmd:cpath($id,'xml'))"/>
	</xsl:function>
	
</xsl:stylesheet>