package security;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.CRLException;
import java.security.cert.X509CRL;

public class CRLUtils {
	
	public static void saveCRLfile(String path, X509CRL crl) {
		
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

}
