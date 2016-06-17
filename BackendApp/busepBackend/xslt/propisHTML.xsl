<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:pro="http://www.ftn.uns.ac.rs/propisi" version="2.0">
    
    <xsl:output method="html" />
    
    <xsl:template match="pro:Propis">
    	<md-content layout-align="center">
<!--    	<title><xsl:value-of select="@Naziv"/></title> -->
			<md-content layout="row" layout-align="center">
		 		<h1>
		 			<xsl:value-of select="@Naziv"/> <span>	<md-button class="md-raised md-primary"><xsl:attribute name="ng-click">elementClicked('<xsl:value-of select="@element_path" />')</xsl:attribute>Izmeni</md-button><br/>	 </span> <!-- <span style="display:none"> <xsl:value-of select="@element_path"/> </span> -->
		 		</h1>
	 		</md-content>
			<xsl:apply-templates select="pro:Deo"/>
			<xsl:apply-templates select="pro:Clan"/>
		</md-content>
    </xsl:template>
    
    <xsl:template match="pro:Deo">
    	<h2>
    		Deo <xsl:value-of select="@Redni_broj"/><br/><xsl:value-of select="@Naziv"/>
    		<span>
				<md-button class="md-raised md-primary"><xsl:attribute name="ng-click">elementClicked('<xsl:value-of select="@element_path" />')</xsl:attribute>Izmeni</md-button><br/>
<!-- 				<span style="display:none"> <xsl:value-of select="@element_path"/> </span> -->
			</span>
    	</h2>
		<xsl:apply-templates select="pro:Glava" />
    </xsl:template>
    
    <xsl:template match="pro:Glava">
    	<h3>
    		<xsl:value-of select="@Redni_broj"/><br/><xsl:value-of select="@Naziv"/>
    		<span>
				<md-button class="md-raised md-primary"><xsl:attribute name="ng-click">elementClicked('<xsl:value-of select="@element_path" />')</xsl:attribute>Izmeni</md-button><br/>
<!-- 				<span style="display:none"> <xsl:value-of select="@element_path"/> </span> -->
			</span>
    	</h3>
		<xsl:apply-templates select="pro:Odeljak"/>
    </xsl:template>
    
    <xsl:template match="pro:Odeljak">
    	<h4>
    		<xsl:value-of select="@Redni_broj"/><br/><xsl:value-of select="@Naziv"/>
    		<span>
				<md-button class="md-raised md-primary"><xsl:attribute name="ng-click">elementClicked('<xsl:value-of select="@element_path" />')</xsl:attribute>Izmeni</md-button><br/>
<!-- 				<span style="display:none"> <xsl:value-of select="@element_path"/> </span> -->
			</span>
    	</h4>
		<xsl:apply-templates select="pro:Pododeljak"/>
		<xsl:apply-templates select="pro:Clan"/>
    </xsl:template>
    
    <xsl:template match="pro:Pododeljak">
    	<h5>
    		<xsl:value-of select="@Slovo"/><br/><xsl:value-of select="@Naziv"/>
    		<span>
				<md-button class="md-raised md-primary"><xsl:attribute name="ng-click">elementClicked('<xsl:value-of select="@element_path" />')</xsl:attribute>Izmeni</md-button><br/>
<!-- 				<span style="display:none"> <xsl:value-of select="@element_path"/> </span> -->
			</span>
    	</h5>
		<xsl:apply-templates select="pro:Clan"/>
    </xsl:template>
    
    <xsl:template match="pro:Clan">
    	<h6>
    		Član <xsl:value-of select="@Redni_broj"/><br/><xsl:value-of select="@Naziv"/>
    		<span>
				<md-button class="md-raised md-primary"><xsl:attribute name="ng-click">elementClicked('<xsl:value-of select="@element_path" />')</xsl:attribute>Izmeni</md-button><br/>
<!-- 				<span style="display:none"> <xsl:value-of select="@element_path"/> </span> -->
			</span>
    	</h6>
    	<xsl:apply-templates select="pro:Stav"/>
    </xsl:template>
    
    <xsl:template match="pro:Stav">
   		<span>
			<md-button class="md-raised md-primary"><xsl:attribute name="ng-click">elementClicked('<xsl:value-of select="@element_path" />')</xsl:attribute>Izmeni</md-button><br/>
<!-- 			<span style="display:none"> <xsl:value-of select="@element_path"/> </span> -->
		</span>
	    <xsl:apply-templates select="pro:Tekst"/>
	    <xsl:apply-templates select="pro:Tacka"/>
    </xsl:template>
    
    <xsl:template match="pro:Tacka">
    	<p>
    		<xsl:value-of select="@Redni_broj"/>
    		<span>
				<md-button class="md-raised md-primary"><xsl:attribute name="ng-click">elementClicked('<xsl:value-of select="@element_path" />')</xsl:attribute>Izmeni</md-button><br/>
<!-- 				<span style="display:none"> <xsl:value-of select="@element_path"/> </span> -->
			</span>
    		<xsl:apply-templates select="pro:Tekst"/>
    		<xsl:apply-templates select="pro:Podtacka"/>
    	</p>
    </xsl:template>
    
    <xsl:template match="pro:Podtacka">
    	<p>
    		<xsl:value-of select="@Redni_broj"/>
    		<span>
				<md-button class="md-raised md-primary"><xsl:attribute name="ng-click">elementClicked('<xsl:value-of select="@element_path" />')</xsl:attribute>Izmeni</md-button><br/>
<!-- 				<span style="display:none"> <xsl:value-of select="@element_path"/> </span> -->
			</span>
    		<xsl:apply-templates select="pro:Tekst"/>
    		<xsl:apply-templates select="pro:Alineja"/>
    	</p>
    </xsl:template>
    
    <xsl:template match="pro:Alineja">
    	<p>
    		<xsl:value-of select="."/>
    		<span>
				<md-button class="md-raised md-primary"><xsl:attribute name="ng-click">elementClicked('<xsl:value-of select="@element_path" />')</xsl:attribute>Izmeni</md-button><br/>
<!-- 				<span style="display:none"> <xsl:value-of select="@element_path"/> </span> -->
			</span>
    	</p>
    </xsl:template>
    
    <xsl:template match="Tekst">
    	<p>
    		<xsl:value-of select="."/>
    		<span>
				<md-button class="md-raised md-primary"><xsl:attribute name="ng-click">elementClicked('<xsl:value-of select="@element_path" />')</xsl:attribute>Izmeni</md-button><br/>
<!-- 				<span style="display:none"> <xsl:value-of select="@element_path"/> </span> -->
			</span>
    	</p>
    </xsl:template>
        
</xsl:stylesheet>