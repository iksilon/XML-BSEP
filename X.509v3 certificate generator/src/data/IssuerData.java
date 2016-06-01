package data;

import java.security.PrivateKey;
import java.security.PublicKey;

import org.bouncycastle.asn1.x500.X500Name;

/**
 * Bean class that carries information about the certificate issuer.
 * The information is his {@link PrivateKey} and {@link X500Name}. 
 *
 */
public class IssuerData {
	
	private X500Name x500name;
	private PrivateKey privateKey;
	private PublicKey publicKey;
	
	public IssuerData() {
	}
	
	public IssuerData(PrivateKey privateKey, PublicKey publicKey, X500Name x500name) {
		this.privateKey = privateKey;
		this.publicKey = publicKey;
		this.x500name = x500name;
		
	}

	public X500Name getX500name() {
		return x500name;
	}

	public void setX500name(X500Name x500name) {
		this.x500name = x500name;
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}
	
	public PublicKey getPublicKey() { return publicKey; }
	public void setPublicKey(PublicKey publicKey) { this.publicKey = publicKey; }

	@Override
	public String toString() {
		return x500name.toString();
	}
	
	
	

}
