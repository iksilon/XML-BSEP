package utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;

import javax.swing.JOptionPane;

/**
 * Utility class for {@link KeyStore} managing.
 * {@code KeyStores} can be loaded from files, created and saved into files.
 * All methods are static.
 *
 */
public class KeyStoreUtils {
	
	/**
	 * Method for reading a {@link KeyStore} from a {@code .jks} file. If no file is specified, a new {@code KeyStore} will be created.
	 * 
	 * @param filepath - Location of the keystore file. If {@code null} a new keystore will be created.
	 * @param password - Password for the keystore.
	 * @return {@link KeyStore}
	 */
	public static KeyStore loadKeyStore(String filepath, char[] password) {
		
		KeyStore keystore = null;
		
		try {
			keystore = KeyStore.getInstance("JKS", "SUN");
			
			if(filepath != null) {
				BufferedInputStream in = new BufferedInputStream(new FileInputStream(filepath));
				keystore.load(in, password);
			}
			else {
				keystore.load(null, password);
			}
			
			Arrays.fill(password, '0');
			return keystore;
			
		} catch (KeyStoreException e) {
			e.printStackTrace();
			Arrays.fill(password, '0');
			return keystore;
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			Arrays.fill(password, '0');
			return keystore;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Arrays.fill(password, '0');
			return keystore;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			Arrays.fill(password, '0');
			return keystore;
		} catch (CertificateException e) {
			e.printStackTrace();
			Arrays.fill(password, '0');
			return keystore;
		} catch (IOException e) {
			e.printStackTrace();
			Arrays.fill(password, '0');
			return keystore;
		}
		
	}
	
}
