<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:pro="http://www.ftn.uns.ac.rs/propisi" version="2.0">
    
    <xsl:output method="html" />
    
    <xsl:template match="/">
    	<html xmlns="http://www.w3.org/1999/xhtml">
    		<head>
    			<meta charset="utf-8" />
    			<title><xsl:value-of select="@Naziv"/></title>
    		</head>
    		<body>
    			<h1><xsl:value-of select="/Propis/@Naziv"/></h1>
   				<xsl:apply-templates select="/Propis/Deo"/>
   				<xsl:apply-templates select="/Propis/Clan"/>
    		</body>
    	</html>
    </xsl:template>
    
    <xsl:template match="Deo">
    	<h2>Deo <xsl:value-of select="./@Redni_broj"/> <xsl:value-of select="./@Naziv"/></h2>
		<xsl:apply-templates select="./Glava" />
    </xsl:template>
    
    <xsl:template match="Glava">
    	<h3> <xsl:value-of select="./@Redni_broj"/> <xsl:value-of select="./@Naziv"/> </h3>
		<xsl:apply-templates select="./Odeljak"/>
    </xsl:template>
    
    <xsl:template match="Odeljak">
    	<h4> <xsl:value-of select="./@Redni_broj"/> <xsl:value-of select="./@Naziv"/> </h4>
		<xsl:apply-templates select="./Pododeljak"/>
		<xsl:apply-templates select="./Clan"/>
    </xsl:template>
    
    <xsl:template match="Pododeljak">
    	<h5> <xsl:value-of select="./@Slovo"/> <xsl:value-of select="./@Naziv"/> </h5>
		<xsl:apply-templates select="./Clan"/>
    </xsl:template>
    
    <xsl:template match="Clan">
    	<h6>Član <xsl:value-of select="./@Redni_broj"/> <xsl:value-of select="./@Naziv"/> </h6>
    	<xsl:apply-templates select="./Stav"/>
    </xsl:template>
    
    <xsl:template match="Stav">
	    <xsl:apply-templates select="./Tekst"/>
	    <xsl:apply-templates select="./Tacka"/>
    </xsl:template>
    
    <xsl:template match="Tacka">
    	<p>
    		<xsl:value-of select="./@Redni_broj"/>
    		<xsl:apply-templates select="./Tekst"/>
    		<xsl:apply-templates select="./Podtacka"/>
    	</p>
    </xsl:template>
    
    <xsl:template match="Podtacka">
    	<p>
    		<xsl:value-of select="./@Redni_broj"/>
    		<xsl:apply-templates select="./Tekst"/>
    		<xsl:apply-templates select="./Alineja"/>
    	</p>
    </xsl:template>
    
    <xsl:template match="Alineja">
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