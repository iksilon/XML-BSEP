package utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

/**
 * <p>Util za rad sa keystore-om.</p>
 * <p>U njemu će se nalaziti sve metode potrebne u projektu za rad sa keystore-om i imajući to u vidu
 * u njemu se neće nalaziti apsolutno sve metode za rad sa keystore-om.</p>
 * <p>Ukoliko se nađe potreba za nekom metodom koja se ne nalazi u ovom paketu a tiče se rada sa keystore-om ili sertifikatom,
 * slobodno dodati tu metodu u ovu klasu.</p>
 *
 * Created by Nemanja on 12/6/2016.
 */
public class KeystoreUtils {

	static {
		//staticka inicijalizacija
		Security.addProvider(new BouncyCastleProvider());
		org.apache.xml.security.Init.init();
	}

	/**
	 * <p>Čita sertifikat iz kojeg će izvući ključ koji je potreban za enkripciju.</p>
	 * <p>Primer parametra KEY_STORE_FILE je: </p>
	 * <p><code>String KEY_STORE_FILE = "./data/primer.jks";</code></p>
	 *
	 *
	 * @param KEY_STORE_FILE putanja do keystore-a
	 * @return Vraća pročitani sertifikat iz KeyStore fajla
	 */
	public Certificate readCertificate(String KEY_STORE_FILE, String alias, String password) {    //putanja do keystore-a
		try {
			//kreiramo instancu KeyStore
			KeyStore ks = KeyStore.getInstance("JKS", "SUN");
			//ucitavamo podatke
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(KEY_STORE_FILE));
			//učitavamo keystore iz inputStream-a
			ks.load(in, password.toCharArray());    //drugi parametar je šifra keystore-a

			if (ks.isKeyEntry(alias)) {
				Certificate cert = ks.getCertificate(alias);
				return cert;

			} else
				return null;

		} catch (KeyStoreException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			return null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (CertificateException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Ucitava privatni kljuc is KS fajla
	 *
	 * @param KEY_STORE_FILE putanja do keystore-a
	 * @param alias Alias KeyStore-a
	 * @param password Lozinka KeyStore-a
	 */
	public PrivateKey readPrivateKey(String KEY_STORE_FILE, String alias, String password) {
		try {
			//kreiramo instancu KeyStore
			KeyStore ks = KeyStore.getInstance("JKS", "SUN");
			//ucitavamo podatke
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(KEY_STORE_FILE));
			ks.load(in, password.toCharArray());

			if(ks.isKeyEntry(alias)) {
				PrivateKey pk = (PrivateKey) ks.getKey(alias, password.toCharArray());
				return pk;
			}
			else
				return null;

		} catch (KeyStoreException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			return null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (CertificateException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
			return null;
		}
	}
}
