<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:pro="http://www.ftn.uns.ac.rs/propisi" version="2.0">
    
    <xsl:output method="html" />
    
    <xsl:template match="pro:Propis">
    	<md-content layout="row" layout-align="center" flex="95">
    	<md-content layout="column" flex="95">
			<md-card flex="99">
		 		<h1>
		 			<xsl:value-of select="@Naziv"/>
					<md-button type="button" class="md-raised md-primary">
						<xsl:attribute name="ng-click">elementEdit('<xsl:value-of select="@element_path" />')</xsl:attribute>
						<xsl:attribute name="ng-if">user.role &amp;&amp; user.role != 'Web Admin'</xsl:attribute>
						Izmeni
					</md-button>
		 		</h1><br/><br/>
				<xsl:apply-templates select="pro:Deo"/>
				<xsl:apply-templates select="pro:Clan"/>
	 		</md-card>
		</md-content>
		</md-content>
    </xsl:template>
    
    <xsl:template match="pro:Deo">
    	<h2>
    		Deo <xsl:value-of select="@Redni_broj"/><br/><xsl:value-of select="@Naziv"/>
			<md-button type="button" class="md-raised md-primary">
				<xsl:attribute name="ng-click">
					elementEdit('<xsl:value-of select="@element_path" />')
				</xsl:attribute>
				<xsl:attribute name="ng-if">
					user.role &amp;&amp; user.role != 'Web Admin'
				</xsl:attribute>
				Izmeni
			</md-button>
    	</h2><br/><br/>
		<xsl:apply-templates select="pro:Glava" />
    </xsl:template>
    
    <xsl:template match="pro:Glava">
    	<h3>
    		<xsl:value-of select="@Redni_broj"/><br/><xsl:value-of select="@Naziv"/>
			<md-button type="button" class="md-raised md-primary">
				<xsl:attribute name="ng-click">
					elementEdit('<xsl:value-of select="@element_path" />')
				</xsl:attribute>
				<xsl:attribute name="ng-if">
					user.role &amp;&amp; user.role != 'Web Admin'
				</xsl:attribute>
				Izmeni
			</md-button>
    	</h3><br/><br/>
		<xsl:apply-templates select="pro:Odeljak"/>
    </xsl:template>
    
    <xsl:template match="pro:Odeljak">
    	<h4>
    		<xsl:value-of select="@Redni_broj"/><br/><xsl:value-of select="@Naziv"/>
    		<md-button type="button" class="md-raised md-primary">
				<xsl:attribute name="ng-click">
					elementEdit('<xsl:value-of select="@element_path" />')
				</xsl:attribute>
				<xsl:attribute name="ng-if">
					user.role &amp;&amp; user.role != 'Web Admin'
				</xsl:attribute>
				Izmeni
			</md-button>
    	</h4><br/><br/>
		<xsl:apply-templates select="pro:Pododeljak"/>
		<xsl:apply-templates select="pro:Clan"/>
    </xsl:template>
    
    <xsl:template match="pro:Pododeljak">
    	<h5>
    		<xsl:value-of select="@Slovo"/><br/><xsl:value-of select="@Naziv"/>
    		<md-button type="button" class="md-raised md-primary">
				<xsl:attribute name="ng-click">
					elementEdit('<xsl:value-of select="@element_path" />')
				</xsl:attribute>
				<xsl:attribute name="ng-if">
					user.role &amp;&amp; user.role != 'Web Admin'
				</xsl:attribute>
				Izmeni
			</md-button>
    	</h5><br/><br/>
		<xsl:apply-templates select="pro:Clan"/>
    </xsl:template>
    
    <xsl:template match="pro:Clan">
    	<h6>
    		Član <xsl:value-of select="@Redni_broj"/><br/><xsl:value-of select="@Naziv"/>
    		<md-button type="button" class="md-raised md-primary">
				<xsl:attribute name="ng-click">
					elementEdit('<xsl:value-of select="@element_path" />')
				</xsl:attribute>
				<xsl:attribute name="ng-if">
					user.role &amp;&amp; user.role != 'Web Admin'
				</xsl:attribute>
				Izmeni
			</md-button>
    	</h6><br/><br/>
    	<xsl:apply-templates select="pro:Stav"/>
    </xsl:template>
    
    <xsl:template match="pro:Stav">
		<md-button type="button" class="md-raised md-primary">
			<xsl:attribute name="ng-click">
				elementEdit('<xsl:value-of select="@element_path" />')
			</xsl:attribute>
			<xsl:attribute name="ng-if">
				user.role &amp;&amp; user.role != 'Web Admin'
			</xsl:attribute>
			Izmeni
		</md-button><br/>
	    <xsl:apply-templates select="pro:Tekst"/>
	    <xsl:apply-templates select="pro:Tacka"/>
    </xsl:template>
    
    <xsl:template match="pro:Tacka">
    	<p>
    		<xsl:value-of select="@Redni_broj"/>
    		<md-button type="button" class="md-raised md-primary">
				<xsl:attribute name="ng-click">
					elementEdit('<xsl:value-of select="@element_path" />')
				</xsl:attribute>
				<xsl:attribute name="ng-if">
					user.role &amp;&amp; user.role != 'Web Admin'
				</xsl:attribute>
				Izmeni
			</md-button><br/>
    		<xsl:apply-templates select="pro:Tekst"/>
    		<xsl:apply-templates select="pro:Podtacka"/>
    	</p>
    </xsl:template>
    
    <xsl:template match="pro:Podtacka">
    	<p>
    		<xsl:value-of select="@Redni_broj"/>. 
    		<md-button type="button" class="md-raised md-primary">
				<xsl:attribute name="ng-click">
					elementEdit('<xsl:value-of select="@element_path" />')
				</xsl:attribute>
				<xsl:attribute name="ng-if">
					user.role &amp;&amp; user.role != 'Web Admin'
				</xsl:attribute>
				Izmeni
			</md-button><br/>
    		<xsl:apply-templates select="pro:Tekst"/>
    		<xsl:apply-templates select="pro:Alineja"/>
    	</p>
    </xsl:template>
    
    <xsl:template match="pro:Alineja">
    	<p>
    		<xsl:value-of select="."/>
    		<md-button type="button" class="md-raised md-primary">
				<xsl:attribute name="ng-click">
					elementEdit('<xsl:value-of select="@element_path" />')
				</xsl:attribute>
				<xsl:attribute name="ng-if">
					user.role &amp;&amp; user.role != 'Web Admin'
				</xsl:attribute>
				Izmeni
			</md-button><br/>
    	</p>
    </xsl:template>
    
    <xsl:template match="Tekst">
    	<p>
    		<xsl:value-of select="."/>
    		<md-button type="button" class="md-raised md-primary">
				<xsl:attribute name="ng-click">
					elementEdit('<xsl:value-of select="@element_path" />')
				</xsl:attribute>
				<xsl:attribute name="ng-if">
					user.role &amp;&amp; user.role != 'Web Admin'
				</xsl:attribute>
				Izmeni
			</md-button><br/>
    	</p>
    </xsl:template>
        
</xsl:stylesheet>