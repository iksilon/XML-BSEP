package security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;

public class CRLUtils {
	
	public static void saveCRLfile(String path, X509CRL crl) {
		System.out.println(crl);
		
		File crlFile = new File(path);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(crlFile);
			fos.write(crl.getEncoded());
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CRLException e) {
			e.printStackTrace();
		}
		
	}
	
	public static X509CRL openFromFile(String path) {
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

}
