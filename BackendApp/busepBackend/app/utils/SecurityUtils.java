package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.SignedObject;
import java.security.UnrecoverableKeyException;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.keyresolver.implementations.RSAKeyValueResolver;
import org.apache.xml.security.keys.keyresolver.implementations.X509CertificateResolver;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Contains static utility methods for securing the application.
 * Not part of the REST API.
 * All methods should only be accessible from within the application.
 * 
 * @author Isidora
 *
 */
public class SecurityUtils {
	
	/**
	 * Signs the given XML document using given private and public key.
	 * Enveloped signature style. 
	 * 
	 * @param doc - {@link Document} that is to be signed 
	 * @param privateKey - {@link PrivateKey} which will be used to sign the document
	 * @param cert - {@link Certificate} with the corresponding public key
	 * @return - signed {@link Document}
	 */
	public static Document signDocument(Document doc, PrivateKey privateKey, Certificate cert) {
        
        try {
			Element rootEl = doc.getDocumentElement();
			
			// Create the signature element.
			XMLSignature sig = new XMLSignature(doc, null, XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1);
			
			// Create transformations and apply them.
			Transforms transforms = new Transforms(doc);
			transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
			transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);
			    
			// The whole document is signed.
			sig.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);
			
			sig.addKeyInfo(cert.getPublicKey());
			sig.addKeyInfo((X509Certificate) cert);
			
			// Enveloped signature.
			rootEl.appendChild(sig.getElement());
			    
			// Sign the document.
			sig.sign(privateKey);
			
			return doc;
			
		} catch (TransformationException e) {
			e.printStackTrace();
			return null;
		} catch (XMLSignatureException e) {
			e.printStackTrace();
			return null;
		} catch (DOMException e) {
			e.printStackTrace();
			return null;
		} catch (XMLSecurityException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Verifies if the XML document is signed by the key that the stored certificate corresponds to.
	 * If the public key does not correspond to the signature or there is no public key or certificate, verification will fail. 
	 * 
	 * @param doc - signed {@link Document} which is to be verified.
	 * @return boolean
	 */
	public static boolean verifySignature(Document doc) {
		
		try {
			// Find the first signature element.
			NodeList signatures = doc.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
			Element signatureEl = (Element) signatures.item(0);
			XMLSignature signature = new XMLSignature(signatureEl, null);
			
			KeyInfo keyInfo = signature.getKeyInfo();
			// Is there a key?
			if(keyInfo != null) {
				// Register public key and certificate resolvers.
				keyInfo.registerInternalKeyResolver(new RSAKeyValueResolver());
			    keyInfo.registerInternalKeyResolver(new X509CertificateResolver());
			    
			    //Is certificate contained?
			    if(keyInfo.containsX509Data() && keyInfo.itemX509Data(0).containsCertificate()) {
			        Certificate cert = keyInfo.itemX509Data(0).itemCertificate(0).getX509Certificate();
			        // Is certificate existing?
			        if(cert != null) {
			        	return signature.checkSignatureValue((X509Certificate) cert);
			        }
			        else {
			        	return false;
			        }
			    }
			    else {
			    	return false;
			    }
			}
			else {
				return false;
			}
		
		} catch (XMLSignatureException e) {
			e.printStackTrace();
			return false;
		} catch (XMLSecurityException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Opens the specified CRL file.
	 * 
	 * @param path - {@link String} representing the file path
	 * @return {@link X509Certificate}
	 */
	public static X509CRL openCRLFromFile(String path) {
		try {
			FileInputStream fis = new FileInputStream(path);			
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509CRL crl = (X509CRL) cf.generateCRL(fis);
			System.out.println(crl);
			
			return crl;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (CRLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Checks against the default CRL whether the given {@link Certificate} is revoked.
	 * 
	 * @param cert - {@link Certificate} to be checked.
	 * @return boolean - true if the certificate is revoked, false otherwise
	 */
	public static boolean isCertificateRevoked(Certificate cert) {
		String workingDir = System.getProperty("user.dir");
		workingDir = Paths.get(workingDir, "keystores", "rootca.crl").toString();
		
		X509CRL crl = openCRLFromFile(workingDir);
		
		return crl.isRevoked(cert);
	}

}
