package security;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import data.IssuerData;
import data.SubjectData;

/**
 * Utility class for certificate managing.
 * It can generate key pairs and certificates and save them to file.
 *
 */
public class CertificateUtils {
	
	// Registracija providera
	static {
		Security.addProvider(new BouncyCastleProvider());
	}
	
	/**
	 * Saves the given {@link Certificate} into a PEM encoded file at the specified {@code path}.
	 * @param path {@link String}
	 * @param cert {@link Certificate}
	 */
	public static void savePEMfile(String path, Certificate cert) {
		try {
			FileWriter fw = new FileWriter(path);
			PEMWriter pw = new PEMWriter(fw);
			pw.writeObject(cert);
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Saves the given {@link Certificate} into a DER encoded file at the specified {@code path}.
	 * @param path {@link String}
	 * @param cert {@link Certificate}
	 */
	public static void saveDERfile(String path, Certificate cert) {
		File certFile = new File(path);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(certFile);
			fos.write(cert.getEncoded());
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (CertificateEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Generates a public-private key pair for asymmetric encryption.
	 * The algorithm used is RSA and key length is 1024 bit.
	 * 
	 * @return {@link KeyPair}
	 */
	public static KeyPair generateKeyPair() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(1024);
			
			KeyPair pair = keyGen.generateKeyPair();
			return pair;
			
        } catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Generates a X509v3 certificate.
	 * Keys are RSA (1024 bit).
	 * Certificate is signed with RSA (1024 bit) and SHA-256.
	 * 
	 * @param issuerData - {@link IssuerData} object containing information about the certificate issuer.
	 * @param subjectData - {@link SubjectData} object containing information about the certificate.
	 * @return {@link X509Certificate}
	 */
	public static X509Certificate generateCertificate(IssuerData issuerData, SubjectData subjectData) {
		
		try {
			
			// Certificate builder uses ContentSigner to sign the certificate with issuer's private key.
			// ContentSigner is created using a builder.
			JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
			builder.setProvider("BC");
			ContentSigner contentSigner = builder.build(issuerData.getPrivateKey());
			
			
			// Create the certificate.
			X509v3CertificateBuilder certificateBuilder = 
					new JcaX509v3CertificateBuilder(issuerData.getX500name(),
													new BigInteger(subjectData.getSerialNumber()),
													subjectData.getStartDate(),
													subjectData.getEndDate(),
													subjectData.getX500name(),
													subjectData.getPublicKey());
			X509CertificateHolder certificateHolder = certificateBuilder.build(contentSigner);
			
			// Convert the certificate from the holder object into an actual certificate.
			JcaX509CertificateConverter certificateConverter = new JcaX509CertificateConverter();
			certificateConverter = certificateConverter.setProvider("BC");
			return certificateConverter.getCertificate(certificateHolder);
			
			
		 } catch (CertificateEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return null;
		} catch (OperatorCreationException e) {
			e.printStackTrace();
			return null;
		} catch (CertificateException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
}
