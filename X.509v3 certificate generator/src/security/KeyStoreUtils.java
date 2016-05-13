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
import java.security.cert.CertificateException;

public class KeyStoreUtils {
	
	/**
	 * Method for reading a {@link KeyStore} from a {@code .jks} file. If no file is specified, a new {@code KeyStore} will be created.
	 * 
	 * @param filepath - Location of the keystore file. If {@code null} a new keystore will be created.
	 * @param password - Password for the keystore.
	 * @return {@link KeyStore}
	 */
	public static KeyStore loadKeyStore(String filepath, String password) {
		
		KeyStore keystore = null;
		
		try {
			keystore = KeyStore.getInstance("JKS", "SUN");
			
			if(filepath != null) {
				BufferedInputStream in = new BufferedInputStream(new FileInputStream(filepath));
				keystore.load(in, password.toCharArray());
			}
			else {
				keystore.load(null, password.toCharArray());
			}
			
			return keystore;
			
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
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return keystore;
	}
	
	
	/**
	 * Method for saving {@link KeyStore} to a file.
	 * 
	 * @param keystore - The {@code KeyStore} object to be saved.
	 * @param filepath - The location of the {@code .jks} keystore file.
	 * @param password - Password for the {@code .jks} keystore file.
	 */
	public void saveKeyStore(KeyStore keystore, String filepath, String password) {
		try {
			keystore.store(new FileOutputStream(filepath), password.toCharArray());
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

}
