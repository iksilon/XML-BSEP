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
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;

import javax.swing.JOptionPane;

import gui.MainWindow;

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
	 * Method for inserting a key into the keystore. 
	 * 
	 * @param keystore - The keystore into which the key will be inserted.
	 * @param alias - Alias of the key by which the key will be accessible.
	 * @param privateKey - The key to be inserted.
	 * @param password - Password used to secure the private key.
	 * @param certificate - Certificate for the corresponding public key.
	 */
	public static void insertKey(KeyStore keystore, String alias, PrivateKey privateKey, char[] password, Certificate certificate) {
		try {
			keystore.setKeyEntry(alias, privateKey, password, new Certificate[] {certificate});
			System.out.println("inserted key: " + keystore.isKeyEntry(alias));
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method for inserting a certificate into the keystore.
	 * 
	 * @param keystore - The keystore into which the certificate will be inserted.
	 * @param alias - Alias of the key by which the certificate will be accessible.
	 * @param certificate - The certificate to be inserted.
	 */
	public static void insertCertificate(KeyStore keystore, String alias, X509Certificate certificate) {
		try {
			keystore.setCertificateEntry(alias, certificate);
			System.out.println("inserted certificate: " + keystore.isCertificateEntry(alias));
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
	}
	
	public static String selectCA(KeyStore ks) {
		ArrayList<String> options = new ArrayList<>();
		
		Enumeration<String> aliases;
		try {
			aliases = ks.aliases();
			
			while(aliases.hasMoreElements()) {
				String a = aliases.nextElement();
				if(ks.isKeyEntry(a)) {
					options.add(a);
				}
			}
			
			String[] poss = new String[options.size()];
			for(int i = 0; i < options.size(); i++) {
				poss[i] = options.get(i);
			}
			String alias = (String)JOptionPane.showInputDialog(
	                MainWindow.getInstance(),
	                "Select the CA:",
	                "CA",
	                JOptionPane.PLAIN_MESSAGE,
	                null,
	                options.toArray(poss),
	                poss[0]);
			
			return alias;
		} catch (KeyStoreException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
}
