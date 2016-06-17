<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:pro="http://www.ftn.uns.ac.rs/propisi" version="2.0">
    
    <xsl:output method="html" />
    
    <xsl:template match="Propis">
    	<html xmlns="http://www.w3.org/1999/xhtml">
    		<head>
    			<meta charset="utf-8" />
    			<title><xsl:value-of select="@Naziv"/></title>
    		</head>
    		<body>
    			<h1><xsl:value-of select="@Naziv"/></h1>
    			<xsl:apply-templates select="Deo"/>
    			<xsl:apply-templates select="Clan"/>
    		</body>
    	</html>
    </xsl:template>
    
    <xsl:template match="Deo">
    	<h2>Deo <xsl:value-of select="@Redni_broj"/> <xsl:value-of select="@Naziv"/></h2>
    	<xsl:apply-templates select="Glava"/>
    </xsl:template>
    
    <xsl:template match="Glava">
    	<h3> <xsl:value-of select="@Redni_broj"/> <xsl:value-of select="@Naziv"/> </h3>
    	<xsl:apply-templates select="Odeljak"/>
    </xsl:template>
    
    <xsl:template match="Odeljak">
    	<h4> <xsl:value-of select="@Redni_broj"/> <xsl:value-of select="@Naziv"/> </h4>
    	<xsl:apply-templates select="Pododeljak"/>
    </xsl:template>
    
    <xsl:template match="Pododeljak">
    	<h5> <xsl:value-of select="@Slovo"/> <xsl:value-of select="@Naziv"/> </h5>
    	<xsl:apply-templates select="Clan"/>
    </xsl:template>
    
    <xsl:template match="Clan">
    	<h6>Član <xsl:value-of select="@Redni_broj"/> <xsl:value-of select="@Naziv"/> </h6>
    	<xsl:apply-templates select="Stav"/>
    </xsl:template>
    
    <xsl:template match="Stav">
	    <xsl:choose>
	        <xsl:when test="boolean(Tekst)">	<!-- process Tekst -->
	        	<p><xsl:apply-templates/></p>
	        </xsl:when>
	        <xsl:otherwise>						<!-- process Tacke -->
	        	<p>
	        		<ul style="list-style-type:disc">
	        			<xsl:apply-templates/>
	        		</ul>
	        	</p>
	        </xsl:otherwise>
		</xsl:choose>
    </xsl:template>
    
    <xsl:template match="Tacka">
    	<xsl:choose>
	        <xsl:when test="boolean(Tekst)">	<!-- process Tekst -->
	        	<p><xsl:apply-templates/></p>
	        </xsl:when>
	        <xsl:otherwise>						<!-- process Podtacke -->
	        	<p>
	        		<ul style="list-style-type:circle">
	        			<xsl:apply-templates/>
	        		</ul>
	        	</p>
	        </xsl:otherwise>
		</xsl:choose>
    </xsl:template>
    
    <xsl:template match="Podtacka">
    	<xsl:choose>
	        <xsl:when test="boolean(Tekst)">	<!-- process Tekst -->
	        	<p><xsl:apply-templates/></p>
	        </xsl:when>
	        <xsl:otherwise>						<!-- process Podtacke -->
	        	<p>
	        		<ul style="list-style-type:none">
	        			<xsl:apply-templates/>
	        		</ul>
	        	</p>
	        </xsl:otherwise>
		</xsl:choose>
    </xsl:template>
    
    <xsl:template match="Alineja">
    	<xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="Tekst">
    	<xsl:apply-templates/>
    </xsl:template>
        
</xsl:stylesheet>