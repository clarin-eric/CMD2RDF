<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" xmlns:dcr="http://www.isocat.org/ns/dcr.rdf#"
	xmlns:cmd="http://www.clarin.eu/cmd/" xmlns:vlo="http://www.clarin.eu/vlo/"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:sx="java:nl.knaw.dans.saxon"
	xmlns:functx="http://www.functx.com"
	exclude-result-prefixes="xsl dcr xsi xs sx functx vlo cmd">

	<xsl:output method="xml" encoding="UTF-8"/>
	
	<xsl:include href="CMD2RDF.xsl"/>
	
	<!-- load the VLO facet mapping -->
	<xsl:param name="vloFacetMapping" select="'https://lux17.mpi.nl/isocat/clarin/vlo/mapping/facetConcepts.xml'"/>
	<xsl:variable name="fm" select="document($vloFacetMapping)"/>
	
	<!-- skip some facets -->
	<xsl:param name="skipVLOFacets" select="('id','_selfLink','text','_componentProfile')"/>
	
	<!-- SIL to ISO 639 -->
	<xsl:variable name="lang-top" select="document('sil_to_iso6393.xml')/languages"/>
	<xsl:key name="iso-lookup" match="lang" use="sil"/>
	
	<!-- helper -->
	<xsl:function name="functx:capitalize-first" as="xs:string?">
		<xsl:param name="arg" as="xs:string?"/>
		
		<xsl:sequence select="
			concat(upper-case(substring($arg,1,1)),
			substring($arg,2))
			"/>
		
	</xsl:function>
	
	<!-- get the profile id -->
	<xsl:variable name="profileId">
		<xsl:choose>
			<xsl:when test="exists(/cmd:CMD/cmd:Header/cmd:MdProfile)">
				<!-- and ignore if there are multiple MdProfile and just take the first!! 
                         although probably this more a case for the schema validation!  -->
				<xsl:sequence select="cmd:id((/cmd:CMD/cmd:Header/cmd:MdProfile)[1])"/>
			</xsl:when>
			<xsl:when test="exists(/cmd:CMD/@xsi:schemaLocation)">
				<xsl:sequence select="cmd:id(/cmd:CMD/@xsi:schemaLocation)"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:message terminate="yes">
					<xsl:text>ERR: the CMDI record doesn't refer to its profile!</xsl:text>
				</xsl:message>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	
	<!-- load the profile -->
	<xsl:variable name="profile" select="cmd:profile($profileId)"/>
	
	<!-- the CMD record -->
	<xsl:variable name="rec" select="/"/>
	
	<!-- namespaces -->
	<xsl:variable name="ns">
		<c:ns xmlns:c="http://www.clarin.eu/cmd/"/>
	</xsl:variable>
        
        <xsl:template match="/">
            <!-- <xsl:message>INF: Welcome to addVLOFacets.xsl</xsl:message> -->
            <xsl:apply-templates/>
        </xsl:template>
	
	<!-- identity copy -->
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:function name="vlo:findConceptPaths">
		<xsl:param name="concepts"/>
		<xsl:param name="multiple" as="xs:boolean"/>
		<xsl:variable name="paths" select="$profile//*[@ConceptLink=$concepts]"/>
		<xsl:if test="exists($paths)">
			<xsl:choose>
				<xsl:when test="$multiple">
					<xpath>
						<xsl:text>distinct-values(/c:CMD/c:Components/(c:</xsl:text>
						<xsl:for-each select="$paths">
							<xsl:value-of select="string-join(ancestor-or-self::*/@name,'/c:')"/>
							<xsl:text>/text()</xsl:text>
							<xsl:if test="position()!=last()">
								<xsl:text>,</xsl:text>
							</xsl:if>
						</xsl:for-each>
						<xsl:text>))</xsl:text>
					</xpath>
				</xsl:when>
				<xsl:otherwise>
					<xsl:for-each select="$paths">
						<xpath>
							<xsl:text>/c:CMD/c:Components/c:</xsl:text>
							<xsl:value-of select="string-join(ancestor-or-self::*/@name,'/c:')"/>
							<xsl:text>/text()</xsl:text>
						</xpath>
					</xsl:for-each>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:function>
	
	<xsl:template match="/cmd:CMD">
		<xsl:copy>
			<xsl:attribute name="xml:base" select="base-uri()"/>
			<xsl:apply-templates select="@*"/>
			<xsl:variable name="vlo">
				<xsl:for-each select="$fm//facetConcept[not(@name=$skipVLOFacets)]">
					<xsl:variable name="facet" select="."/>
					<!-- <xsl:message>DBG: facet[<xsl:value-of select="$facet/@name"/>]</xsl:message> -->
					<xsl:variable name="facetValues">
						<xsl:for-each select="vlo:findConceptPaths(concept,empty(@allowMultipleValues) or (@allowMultipleValues='true'))">
							<xsl:variable name="pos" select="position()"/>
							<xsl:variable name="xp" select="."/>
							<!-- <xsl:message>DBG: facet concept path[<xsl:value-of select="$xp"/>]</xsl:message> -->
							<xsl:for-each select="sx:evaluate($rec,$xp,$ns/*)">
								<value pos="{$pos}">
									<xsl:value-of select="."/>
								</value>
							</xsl:for-each>
						</xsl:for-each>
					</xsl:variable>
					<!-- <xsl:message>DBG: facet values[<xsl:value-of select="count($facetValues/*)"/>]</xsl:message>
					<xsl:for-each select="$facetValues/*">
						<xsl:message>[<xsl:value-of select="position()"/>] <xsl:value-of select="."/></xsl:message>
					</xsl:for-each> -->
					<xsl:choose>
						<xsl:when test="exists($facetValues/*)">
							<xsl:for-each-group select="$facetValues/*" group-by="@pos">
								<xsl:if test="position()=1">
									<xsl:for-each select="current-group()[normalize-space(.)!='']">
										<xsl:element name="vlo:hasFacet{functx:capitalize-first($facet/@name)}">
											<xsl:value-of select="."/>
										</xsl:element>
									</xsl:for-each>
								</xsl:if>
							</xsl:for-each-group>
						</xsl:when>
						<xsl:otherwise>
							<xsl:variable name="facetValues">
								<xsl:for-each select="pattern">
									<xsl:variable name="pos" select="position()"/>
									<xsl:variable name="xp" select="."/>
									<!-- <xsl:message>DBG: facet pattern path[<xsl:value-of select="$xp"/>]</xsl:message> -->
									<xsl:for-each select="sx:evaluate($rec,$xp,$ns/*)">
										<value pos="{$pos}">
											<xsl:value-of select="."/>
										</value>
									</xsl:for-each>
								</xsl:for-each>
							</xsl:variable>
							<!-- <xsl:message>DBG: facet values[<xsl:value-of select="count($facetValues/*)"/>]</xsl:message>
							<xsl:for-each select="$facetValues/*">
								<xsl:message>[<xsl:value-of select="position()"/>] <xsl:value-of select="."/></xsl:message>
							</xsl:for-each> -->
							<xsl:for-each-group select="$facetValues/*" group-by="@pos">
								<xsl:if test="position()=1 or empty($facet/@allowMultipleValues) or ($facet/@allowMultipleValues='true')">
									<xsl:for-each select="current-group()[normalize-space(.)!='']">
										<xsl:element name="vlo:hasFacet{functx:capitalize-first($facet/@name)}">
											<xsl:value-of select="."/>
										</xsl:element>
									</xsl:for-each>
								</xsl:if>
							</xsl:for-each-group>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:for-each>
			</xsl:variable>
			<xsl:apply-templates select="$vlo"/>
			<xsl:apply-templates select="node()"/>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="vlo:hasFacetLanguageCode">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
		<xsl:variable name="language" select="."/>
		<xsl:variable name="codeset" select="replace(substring-before($language,':'),' ','')"/>
		<xsl:variable name="codestr" select="substring-after($language,':')"/>
		<xsl:variable name="code">
			<xsl:choose>
				<xsl:when test="$codeset='ISO639-3'">
					<xsl:choose>
						<xsl:when test="$codestr='xxx'">
							<xsl:message>WRN: language code[<xsl:value-of select="base-uri()"/>]: 'xxx' is a potential valid ISO 639-3 code, but for now mapped to 'und'!</xsl:message>
							<xsl:value-of select="'und'"/>
						</xsl:when>
						<xsl:when test="matches($codestr,'^[a-z]{3}$')">
							<xsl:value-of select="$codestr"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="'und'"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="$codeset='RFC1766'">
					<xsl:choose>
						<xsl:when test="starts-with($codestr,'x-sil-')">
							<xsl:variable name="iso" select="key('iso-lookup', lower-case(replace($codestr, 'x-sil-', '')))/iso"/>
							<xsl:choose>
								<xsl:when test="$iso!='xxx'">
									<xsl:value-of select="$iso"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="'und'"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="'und'"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'und'"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<!-- <xsl:message>DBG: language[<xsl:value-of select="$language"/>] ISO 639-3[<xsl:value-of select="$code"/>]</xsl:message> -->
		<vlo:hasFacetISO6393>
			<xsl:value-of select="$code"/>
		</vlo:hasFacetISO6393>
	</xsl:template>

</xsl:stylesheet>