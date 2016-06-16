package utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

/**
 * Utility class for certificate managing.
 * It can generate key pairs and certificates and save them to file.
 *
 */
public class CertificateUtils {
	/*
	// Registracija providera
	static {
		Security.addProvider(new BouncyCastleProvider());
	}*/
	

	
	/**
	 * Reads a {@link Certificate} from a .pem or .cer/.crt file.
	 * Only one certificate per file is supported.
	 * 
	 * @param path {@link String}
	 * @return {@link Certificate}
	 */
	public static Certificate openPEMfile(String path) {
		try {
			FileInputStream fis = new FileInputStream(path);
			BufferedInputStream bis = new BufferedInputStream(fis);			
			CertificateFactory cf = CertificateFactory.getInstance("X.509"); 
			Certificate cert = cf.generateCertificate(bis);
			//System.out.println(cert.toString());
			bis.close();
			return cert;
		} catch (FileNotFoundException e) {
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
	 * Reads a {@link Certificate} from a .der or .cer/.crt file.
	 * Only one certificate per file is supported.
	 * 
	 * @param path
	 * @return {@link Certificate}
	 */
	public static Certificate openDERfile(String path) {
		try {
			FileInputStream fis = new FileInputStream(path);			
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			Certificate cert = cf.generateCertificate(fis);
			//System.out.println(cert);
			
			return cert;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (CertificateException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
