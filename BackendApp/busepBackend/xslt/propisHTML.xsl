<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:pro="http://www.ftn.uns.ac.rs/propisi" version="2.0">
    
    <xsl:output method="html" />
    
    <xsl:template match="pro:Propis">
    	<html xmlns="http://www.w3.org/1999/xhtml">
    		<head>
    			<meta charset="utf-8" />
    			<title><xsl:value-of select="@Naziv"/></title>
    		</head>
    		<body>
    			<h1><xsl:value-of select="@Naziv"/></h1>
   				<xsl:apply-templates select="pro:Deo"/>
   				<xsl:apply-templates select="pro:Clan"/>
    		</body>
    	</html>
    </xsl:template>
    
    <xsl:template match="pro:Deo">
    	<h2>Deo <xsl:value-of select="@Redni_broj"/> <xsl:value-of select="@Naziv"/></h2>
		<xsl:apply-templates select="pro:Glava" />
    </xsl:template>
    
    <xsl:template match="pro:Glava">
    	<h3> <xsl:value-of select="@Redni_broj"/> <xsl:value-of select="@Naziv"/> </h3>
		<xsl:apply-templates select="pro:Odeljak"/>
    </xsl:template>
    
    <xsl:template match="pro:Odeljak">
    	<h4> <xsl:value-of select="@Redni_broj"/> <xsl:value-of select="@Naziv"/> </h4>
		<xsl:apply-templates select="pro:Pododeljak"/>
		<xsl:apply-templates select="pro:Clan"/>
    </xsl:template>
    
    <xsl:template match="pro:Pododeljak">
    	<h5> <xsl:value-of select="@Slovo"/> <xsl:value-of select="@Naziv"/> </h5>
		<xsl:apply-templates select="pro:Clan"/>
    </xsl:template>
    
    <xsl:template match="pro:Clan">
    	<h6>Član <xsl:value-of select="@Redni_broj"/> <xsl:value-of select="@Naziv"/> </h6>
    	<xsl:apply-templates select="pro:Stav"/>
    </xsl:template>
    
    <xsl:template match="pro:Stav">
	    <xsl:apply-templates select="pro:Tekst"/>
	    <xsl:apply-templates select="pro:Tacka"/>
    </xsl:template>
    
    <xsl:template match="pro:Tacka">
    	<p>
    		<xsl:value-of select="@Redni_broj"/>
    		<xsl:apply-templates select="pro:Tekst"/>
    		<xsl:apply-templates select="pro:Podtacka"/>
    	</p>
    </xsl:template>
    
    <xsl:template match="pro:Podtacka">
    	<p>
    		<xsl:value-of select="@Redni_broj"/>
    		<xsl:apply-templates select="pro:Tekst"/>
    		<xsl:apply-templates select="pro:Alineja"/>
    	</p>
    </xsl:template>
    
    <xsl:template match="pro:Alineja">
    	<p>
    		<xsl:value-of select="."/>
    	</p>
    </xsl:template>
    
    <xsl:template match="Tekst">
    	<p>
    		<xsl:value-of select="."/>
    	</p>
    </xsl:template>
        
</xsl:stylesheet>