package utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.SignedObject;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Contains static utility methods for securing the application.
 * Not part of the REST API.
 * All methods should only be accessible from within the application.
 * 
 * @author Isidora
 *
 */
public class SecurityUtils {
	
	public static Document signDocument(Document doc, PrivateKey privateKey, Certificate cert) {
        
        try {
			Element rootEl = doc.getDocumentElement();
			
			//kreira se signature objekat
			XMLSignature sig = new XMLSignature(doc, null, XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1);
			//kreiraju se transformacije nad dokumentom
			Transforms transforms = new Transforms(doc);
			    
			//iz potpisa uklanja Signature element
			//Ovo je potrebno za enveloped tip po specifikaciji
			transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
			//normalizacija
			transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);
			    
			//potpisuje se citav dokument (URI "")
			sig.addDocument("", transforms, Constants.ALGO_ID_DIGEST_SHA1);
			    
			//U KeyInfo se postavalja Javni kljuc samostalno i citav sertifikat
			sig.addKeyInfo(cert.getPublicKey());
			sig.addKeyInfo((X509Certificate) cert);
			    
			//poptis je child root elementa
			rootEl.appendChild(sig.getElement());
			    
			//potpisivanje
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
	 * Method for transforming given digest into a signature.
	 * The returned {@link SignedObject} contains both the original message and the signature.
	 * Right now is hardcoded to one private key.
	 * 
	 * @param data - {@link String} presumably representing the document hash.
	 * @return {@link SignedObject}
	 */
	public static SignedObject signDigest(String data) {
		try {
			// TODO: Signing: Get logged user info. Hardcoded keystore and private key for now. 
			
			String ksName = "odbornik1.jks";
			String ksPass = "odbornik1";
			
			KeyStore keystore = SecurityUtils.getKeyStore(ksName, ksPass.toCharArray());
			
			PrivateKey pk = (PrivateKey) keystore.getKey(ksPass, ksPass.toCharArray());
			
			Signature sig = Signature.getInstance("SHA1withRSA");
			SignedObject so = new SignedObject(data, pk, sig);
			
			return so;
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
			return null;
		} catch (KeyStoreException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			return null;
		} catch (SignatureException e) {
			e.printStackTrace();
			return null;
		}
	}
	

	/**
	 * Retrieves a {@link KeyStore} from the keystore folder of the application.
	 * Since this is a private folder, all keystores needed for this app should be there.
	 * 
	 * @param ksName - Name of keystore, including extension (e.g. "keystore.jks")
	 * @param ksPass - Password to access the keystore
	 * @return {@link KeyStore}
	 */
	public static KeyStore getKeyStore(String ksName, char[] ksPass) {
		String workingDir = System.getProperty("user.dir");
		workingDir = Paths.get(workingDir, "app", "keystores").toString();
		
		// TODO: Signing: Hardcoded keystore for now.
		String filepath = Paths.get(workingDir, ksName).toString();
		
		BufferedInputStream in;
		KeyStore keystore = null;
		try {
			in = new BufferedInputStream(new FileInputStream(filepath));
			keystore = KeyStore.getInstance("JKS", "SUN");
			keystore.load(in, ksPass);
			// Clean up.
			Arrays.fill(ksPass, '0');
		} catch (KeyStoreException | NoSuchProviderException | IllegalArgumentException
				| NoSuchAlgorithmException | CertificateException | IOException | SecurityException e) {
			e.printStackTrace();
		}
	
		return keystore;
	}

}
