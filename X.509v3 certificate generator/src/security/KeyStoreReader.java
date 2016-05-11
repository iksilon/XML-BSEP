/**
 * 
 */
package security;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

/**
 * Class needed so that we can read te CA's stored private key
 * 
 * @author ILA
 *
 */
public class KeyStoreReader {

private static final String KEY_STORE_FILE = "./data/marija.jks";
	
	//private char[] password = "test10".toCharArray();
	//private char[] keyPass  = "marija1".toCharArray();
	
	public PrivateKey readKeyStorePrivateKey(char[] password, char[] keyPass){
		PrivateKey privKey1 = null;
		
		try {
			//kreiramo instancu KeyStore
			KeyStore ks = KeyStore.getInstance("JKS", "SUN");
			//ucitavamo podatke
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(KEY_STORE_FILE));
			ks.load(in, password);
			//citamo par sertifikat privatni kljuc
			System.out.println("Cita se Sertifikat i privatni kljuc...");
			
			if(ks.isKeyEntry("marija")) {
				System.out.println("Sertifikat:");
				Certificate cert = ks.getCertificate("marija");
				System.out.println(cert);
				PrivateKey privKey = (PrivateKey)ks.getKey("marija", keyPass);
				System.out.println("Privatni kljuc:");
				System.out.println(privKey);
				privKey1 = privKey;
			}
			else
				System.out.println("Nema para kljuceva za Mariju");
			

		
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return privKey1;
	}
}
