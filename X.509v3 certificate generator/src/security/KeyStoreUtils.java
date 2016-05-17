package security;

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
import java.util.Arrays;

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
	
	
	/**
	 * Method for saving {@link KeyStore} to a file.
	 * 
	 * @param keystore - The {@code KeyStore} object to be saved.
	 * @param filepath - The location of the {@code .jks} keystore file.
	 * @param password - Password for the {@code .jks} keystore file.
	 */
	public static void saveKeyStore(KeyStore keystore, String filepath, char[] password) {
		try {
			keystore.store(new FileOutputStream(filepath), password);
			Arrays.fill(password, '0');
		} catch (KeyStoreException e) {
			e.printStackTrace();
			Arrays.fill(password, '0');
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			Arrays.fill(password, '0');
		} catch (CertificateException e) {
			e.printStackTrace();
			Arrays.fill(password, '0');
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Arrays.fill(password, '0');
		} catch (IOException e) {
			e.printStackTrace();
			Arrays.fill(password, '0');
		}
	}
	
	/**
	 * Method for inserting a certificate into the keystore.
	 * 
	 * @param keystore
	 * @param alias
	 * @param privateKey
	 * @param password
	 * @param certificate
	 */
	public static void write(KeyStore keystore, String alias, PrivateKey privateKey, char[] password, Certificate certificate) {
		try {
			keystore.setKeyEntry(alias, privateKey, password, new Certificate[] {certificate});
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
	}
}
