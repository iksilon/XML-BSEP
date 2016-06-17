<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:pro="http://www.ftn.uns.ac.rs/propisi" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="2.0">
    
    <xsl:output method="html" />
    
    <xsl:template match="pro:Propis">
    	<fo:root>
    		<fo:layout-master-set>
                <fo:simple-page-master master-name="propis-page">
                    <fo:region-body margin="1in"/>
                </fo:simple-page-master>
            </fo:layout-master-set>
    		
    		<fo:page-sequence master-reference="propis-page">
    			<fo:flow flow-name="xsl-region-body">
			 		<fo:block font-family="sans-serif" font-size="32px" font-weight="bold" padding="30px">
			 			<xsl:value-of select="@Naziv"/>
			 		</fo:block>
					<xsl:apply-templates select="pro:Deo"/>
					<xsl:apply-templates select="pro:Clan"/>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
    </xsl:template>
    
    <xsl:template match="pro:Deo">
    	<fo:block font-family="sans-serif" font-size="28px" font-weight="bold" padding="30px">
    		Deo <xsl:value-of select="@Redni_broj"/><br/><xsl:value-of select="@Naziv"/>
    	</fo:block>
		<xsl:apply-templates select="pro:Glava" />
    </xsl:template>
    
    <xsl:template match="pro:Glava">
    	<fo:block font-family="sans-serif" font-size="26px" font-weight="bold" padding="30px">
    		<xsl:value-of select="@Redni_broj"/><br/><xsl:value-of select="@Naziv"/>
    	</fo:block>
		<xsl:apply-templates select="pro:Odeljak"/>
    </xsl:template>
    
    <xsl:template match="pro:Odeljak">
    	<fo:block font-family="sans-serif" font-size="24px" font-weight="bold" padding="30px">
    		<xsl:value-of select="@Redni_broj"/><br/><xsl:value-of select="@Naziv"/>
		</fo:block>
		<xsl:apply-templates select="pro:Pododeljak"/>
		<xsl:apply-templates select="pro:Clan"/>
    </xsl:template>
    
    <xsl:template match="pro:Pododeljak">
    	<fo:block font-family="sans-serif" font-size="22px" font-weight="bold" padding="30px">
    		<xsl:value-of select="@Slovo"/><br/><xsl:value-of select="@Naziv"/>
    	</fo:block>
		<xsl:apply-templates select="pro:Clan"/>
    </xsl:template>
    
    <xsl:template match="pro:Clan">
    	<fo:block font-family="sans-serif" font-size="20px" font-weight="bold" padding="30px">
    		Član <xsl:value-of select="@Redni_broj"/><br/><xsl:value-of select="@Naziv"/>
    	</fo:block>
    	<xsl:apply-templates select="pro:Stav"/>
    </xsl:template>
    
    <xsl:template match="pro:Stav">
	    <xsl:apply-templates select="pro:Tekst"/>
	    <xsl:apply-templates select="pro:Tacka"/>
    </xsl:template>
    
    <xsl:template match="pro:Tacka">
    	<fo:block font-family="sans-serif" font-size="14px" padding="30px">
    		<xsl:value-of select="@Redni_broj"/>
    		<xsl:apply-templates select="pro:Tekst"/>
    		<xsl:apply-templates select="pro:Podtacka"/>
    	</fo:block>
    </xsl:template>
    
    <xsl:template match="pro:Podtacka">
    	<fo:block font-family="sans-serif" font-size="12px" padding="30px">
    		<xsl:value-of select="@Redni_broj"/>
    		<xsl:apply-templates select="pro:Tekst"/>
    		<xsl:apply-templates select="pro:Alineja"/>
    	</fo:block>
    </xsl:template>
    
    <xsl:template match="pro:Alineja">
    	<fo:block font-family="sans-serif" font-size="10px" padding="30px">
    		<xsl:value-of select="."/>
    	</fo:block>
    </xsl:template>
    
    <xsl:template match="Tekst">
    	<fo:block font-family="sans-serif" font-size="12px" padding="30px">
    		<xsl:value-of select="."/>
    	</fo:block>
    </xsl:template>
        
</xsl:stylesheet>