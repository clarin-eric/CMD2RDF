<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE rdf:RDF [
    <!ENTITY rdf 'http://www.w3.org/1999/02/22-rdf-syntax-ns#'>
    <!ENTITY rdfs 'http://www.w3.org/TR/WD-rdf-schema#'>
    <!ENTITY xsd 'http://www.w3.org/2001/XMLSchema#'>
    <!ENTITY cmdm 'http://www.clarin.eu/cmd/general.rdf#'>
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" xmlns:dcr="http://www.isocat.org/ns/dcr.rdf#" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#" xmlns:cmd="http://www.clarin.eu/cmd/" xmlns:cmdm="http://www.clarin.eu/cmd/general.rdf" xmlns:dcterms="http://purl.org/dc/terms/">

    <xsl:output method="xml" encoding="UTF-8"/>

    <xsl:param name="out" select="'.'"/>

    <xsl:include href="CMD2RDF.xsl"/>

    <!-- let's create some RDF -->
    <xsl:template match="/CMD_ComponentSpec">
        <!-- for the output base replace the xml extension by rdf -->
        <xsl:result-document href="{concat($out,'/',Header/ID,'.rdf')}">
            <rdf:RDF xml:base="{if (@isProfile='true') then (cmd:ppath(Header/ID,'rdf')) else (cmd:cpath(Header/ID,'rdf'))}">
                <xsl:apply-templates>
                    <xsl:with-param name="context" tunnel="yes" select="''"/>
                </xsl:apply-templates>
            </rdf:RDF>
        </xsl:result-document>
    </xsl:template>

    <!-- override default text template -->
    <xsl:template match="text()"/>

    <!-- some attributes appear everywhere -->
    <xsl:template name="generic">
        <xsl:if test="normalize-space(@ConceptLink)!=''">
            <dcr:datcat>
                <xsl:value-of select="normalize-space(@ConceptLink)"/>
            </dcr:datcat>
        </xsl:if>
        <xsl:if test="normalize-space(@Documentation)!=''">
            <rdfs:comment>
                <xsl:value-of select="normalize-space(@Documentation)"/>
            </rdfs:comment>
        </xsl:if>
    </xsl:template>

    <!-- a profile (and component?) contains all the nested components as well,
         but as these are shared we want to also share their RDF representations.
         So restart the traversal when there is reuse. -->
    <xsl:template match="CMD_Component[exists(@ComponentId)]">
        <xsl:if test="empty(preceding::CMD_Component[@ComponentId=current()/@ComponentId])">
            <xsl:result-document href="{concat($out,'/',@ComponentId,'.rdf')}">
                <!-- for the output base replace the xml extension by rdf -->
                <rdf:RDF xml:base="{cmd:cpath(@ComponentId,'rdf')}">
                    <xsl:call-template name="CMD_Component">
                        <xsl:with-param name="context" tunnel="yes" select="''"/>
                    </xsl:call-template>
                </rdf:RDF>
            </xsl:result-document>
        </xsl:if>
    </xsl:template>

    <xsl:template name="CMD_Component">
        <xsl:param name="context" tunnel="yes"/>
        <!-- extend the context path with this component -->
        <xsl:variable name="id" select="cmd:path($context,@name)"/>
        <!-- a component maps to an RDF class -->
        <rdfs:Class rdf:about="#{$id}">
            <xsl:choose>
                <xsl:when test="parent::CMD_ComponentSpec/@isProfile='true'">
                    <rdfs:subClassOf rdf:resource="&cmdm;Profile"/>
                </xsl:when>
                <xsl:otherwise>
                    <rdfs:subClassOf rdf:resource="&cmdm;Component"/>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:if test="exists(preceding-sibling::Header/ID)">
                <dcterms:identifier>
                    <xsl:value-of select="preceding-sibling::Header/ID"/>
                </dcterms:identifier>
            </xsl:if>
            <xsl:if test="exists(@ComponentId)">
                <dcterms:identifier>
                    <xsl:value-of select="@ComponentId"/>
                </dcterms:identifier>
            </xsl:if>
            <xsl:call-template name="generic"/>
        </rdfs:Class>
        <!-- continue with the child component/elements -->
        <xsl:apply-templates>
            <xsl:with-param name="context" tunnel="yes" select="$id"/>
        </xsl:apply-templates>
    </xsl:template>

    <!-- component belonging only to the root profile/component -->
    <xsl:template match="CMD_Component[empty(@ComponentId)]">
        <xsl:param name="context" tunnel="yes"/>
        <xsl:call-template name="CMD_Component">
            <xsl:with-param name="context" tunnel="yes" select="$context"/>
        </xsl:call-template>
    </xsl:template>

    <!-- a CMD element -->
    <xsl:template match="CMD_Element">
        <xsl:param name="context" tunnel="yes"/>
        <!-- extend the context with this element -->
        <xsl:variable name="id" select="cmd:path($context,@name)"/>
        <xsl:variable name="has" select="cmd:path($context,concat('has',@name))"/>
        <!-- element becomes a class to be able to group attributes and value together -->
        <rdfs:Class rdf:about="#{$id}">
            <rdfs:subClassOf rdf:resource="&cmdm;Element"/>
            <xsl:call-template name="generic"/>
        </rdfs:Class>
        <!-- the value property -->
        <rdf:Property rdf:about="{$has}ElementValue">
            <rdfs:subPropertyOf rdf:resource="&cmdm;hasElementValue"/>
            <!-- the domain of the value property is the class corresponding to the CMD element -->
            <rdfs:domain rdf:resource="#{$id}"/>
            <xsl:choose>
                <!-- if the value scheme is an enumeration the range consists of the labels of the values -->
                <xsl:when test="exists(ValueScheme/enumeration)">
                    <rdfs:range rdf:resource="&xsd;string"/>
                </xsl:when>
                <!-- if the value scheme is an XSD datatype the range becomes the equivalent RDF XSD datatype -->
                <xsl:when test="exists(@ValueScheme)">
                    <rdfs:range rdf:resource="&xsd;{cmd:datatype(@ValueScheme)}"/>
                </xsl:when>
                <!-- if the value scheme is different, e.g., a regular expression, fall back to xsd:string -->
                <xsl:otherwise>
                    <rdfs:range rdf:resource="&xsd;string"/>
                </xsl:otherwise>
            </xsl:choose>
        </rdf:Property>
        <!-- if there is an value enumeration also have a hasElementEntity property -->
        <xsl:if test="exists(ValueScheme/enumeration)">
            <rdf:Class rdf:about="#{$id}Entity">
                <rdf:subClassOf rdf:resource="&cmdm;Entity"/>
            </rdf:Class>
            <rdf:Property rdf:about="#{$has}ElementEntity">
                <rdfs:subPropertyOf rdf:resource="&cmdm;hasElementEntity"/>
                <!-- the domain of the value property is the class corresponding to the CMD element -->
                <rdfs:domain rdf:resource="#{$id}"/>
                <!-- the range consists of a superclass for the specific value classes -->
                <rdfs:range rdf:resource="#{$id}Entity"/>
            </rdf:Property>
        </xsl:if>
        <!-- continue with the attributes and values -->
        <xsl:apply-templates>
            <xsl:with-param name="context" tunnel="yes" select="$id"/>
        </xsl:apply-templates>
    </xsl:template>

    <!-- a CDM value -->
    <xsl:template match="item">
        <xsl:param name="context" tunnel="yes"/>
        <xsl:variable name="label" select="."/>
        <!-- id is the label without whitespace
             CHECK: is that enough? -->
        <xsl:variable name="id" select="replace($label,'\s','')"/>
        <!-- a value in a value scheme becomes a subclass of that scheme -->
        <rdf:Class rdf:about="#{$context}Value{$STEP}{$id}">
            <rdf:subClassOf rdf:resource="#{$context}Entity"/>
            <xsl:call-template name="generic"/>
            <rdfs:label>
                <xsl:value-of select="$label"/>
            </rdfs:label>
        </rdf:Class>
    </xsl:template>

    <!-- a CMD attribute -->
    <xsl:template match="Attribute">
        <xsl:param name="context" tunnel="yes"/>
        <!-- extend the context with this attribute -->
        <xsl:variable name="id" select="concat(cmd:path($context,Name),'Attribute')"/>
        <xsl:variable name="has" select="concat(cmd:path($context,concat('has',Name)),'Attribute')"/>
        <!-- element becomes a class to be able to group attributes and value together -->
        <rdfs:Class rdf:about="#{$id}">
            <rdfs:subClassOf rdf:resource="&cmdm;Attribute"/>
            <xsl:call-template name="generic"/>
        </rdfs:Class>
        <!-- an attribute becomes a property -->
        <rdf:Property rdf:about="#{$has}Value">
            <rdfs:subPropertyOf rdf:resource="&cmdm;hasAttributeValue"/>
            <!-- the domain is the CMD element RDF class -->
            <rdfs:domain rdf:resource="#{$id}"/>
            <xsl:choose>
                <!-- if the value scheme is an enumeration the range is the labels -->
                <xsl:when test="exists(ValueScheme/enumeration)">
                    <rdfs:range rdf:resource="&xsd;string"/>
                </xsl:when>
                <!-- if the value scheme is an XSD datatype the range becomes the equivalent RDF XSD datatype -->
                <xsl:when test="exists(@Type)">
                    <rdfs:range rdf:resource="&xsd;{cmd:datatype(Type)}"/>
                </xsl:when>
                <!-- if the value scheme is different, e.g., a regular expression, fall back to xsd:string -->
                <xsl:otherwise>
                    <rdfs:range rdf:resource="&xsd;string"/>
                </xsl:otherwise>
            </xsl:choose>
        </rdf:Property>
        <!-- if there is an value enumeration also have a hasAttributeEntity property -->
        <xsl:if test="exists(ValueScheme/enumeration)">
            <rdf:Class rdf:about="#{$id}Entity">
                <rdf:subClassOf rdf:resource="&cmdm;Entity"/>
            </rdf:Class>
            <rdf:Property rdf:about="#{$has}Entity">
                <rdfs:subPropertyOf rdf:resource="&cmdm;hasAttributeEntity"/>
                <!-- the domain is the CMD Attribute RDF class -->
                <rdfs:domain rdf:resource="#{$id}"/>
                <!-- the range consists of a superclass for the specific value classes -->
                <rdfs:range rdf:resource="{$id}Entity"/>
            </rdf:Property>
        </xsl:if>
        <!-- continue with the values -->
        <xsl:apply-templates>
            <xsl:with-param name="context" tunnel="yes" select="$id"/>
        </xsl:apply-templates>
    </xsl:template>

</xsl:stylesheet>
