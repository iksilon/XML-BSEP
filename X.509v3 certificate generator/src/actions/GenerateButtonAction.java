/**
 * 
 */
package actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.AbstractAction;

import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import application.IssuerData;
import application.SubjectData;
import gui.MainFrame;
import security.KeyStoreWriter;

/**
 * @author ILA
 *
 */
@SuppressWarnings("serial")
public class GenerateButtonAction extends AbstractAction {

	public GenerateButtonAction() {
		super();
		putValue(NAME, "Generate");
		putValue(SHORT_DESCRIPTION, "Generate the X.509v3 certificate.");
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
		try {
			MainFrame.getInstance().statusLabelSetVisible(true);
			MainFrame.getInstance().statusLabelSetText("Generating...");	//TODO Bug, ovo se ne pojavljuje...
			
			KeyPair keyPair = generateKeyPair();	//keypair value for the Subject
			
			//Datumi
			SimpleDateFormat iso8601Formater = new SimpleDateFormat("yyyy-MM-dd");
			Date startDate = iso8601Formater.parse("2007-12-31");
			Date endDate = iso8601Formater.parse("2017-12-31");	//TODO napraviti da datum bude od danas
						
			//Subject info
			//klasa X500NameBuilder pravi X500Name objekat koji nam treba
			X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
			builder.addRDN(BCStyle.CN, MainFrame.getInstance().getCN());
		    builder.addRDN(BCStyle.SURNAME, MainFrame.getInstance().getSurname());
		    builder.addRDN(BCStyle.GIVENNAME, MainFrame.getInstance().getGivenName());
		    builder.addRDN(BCStyle.O, MainFrame.getInstance().getO());
		    builder.addRDN(BCStyle.C, MainFrame.getInstance().getC());
		    builder.addRDN(BCStyle.E, MainFrame.getInstance().getE());
		    
		    
			//TODO odraditi serijski broj valjano
		    String sn = String.valueOf(ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE)); //Oh God why? verzija
		    
		    //kreiraju se podaci za issuer-a
		    //TODO ovo čitaš iz keystore-a (treba ti private key)
		    //TODO proveriti da li je potreban ceo issuerData ili je dovoljan samo private key
		    //DONE provereno. Potrebni su nam samo Issuer name i njegov private key. (fak, treba nam za ovo i certificate reader...)
		    //ovo je self signed sertifikat pa issuer ima iste podatke kao i subject
			IssuerData issuerData = new IssuerData(keyPair.getPrivate(), builder.build());
		    
			//kreiraju se podaci za vlasnika
			SubjectData subjectData = new SubjectData(keyPair.getPublic(), builder.build(), sn, startDate, endDate);
			
			//generise se sertifikat
			X509Certificate cert = generateCertificate(issuerData, subjectData);
			
			//sertifikat se čuva u .cer fajlu
			saveCertificateToFile(cert, MainFrame.getInstance().getGivenName());
			
			MainFrame.getInstance().statusLabelSetText("Done!");
			
			//TODO sačuvaj keystore subject-u
			//za ovo nam je potreban keystore writer
			KeyStoreWriter keyStoreWriter = new KeyStoreWriter();
			keyStoreWriter.loadKeyStore(null, "test".toCharArray());
			keyStoreWriter.write("test", keyPair.getPrivate(), "test10".toCharArray(), cert);
			keyStoreWriter.saveKeyStore("./certificates/" + MainFrame.getInstance().getGivenName() + ".jks", "test10".toCharArray());			
			
			
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
	}
	
	static {	//do this initially and only once
		Security.addProvider(new BouncyCastleProvider());
	}
	
	public X509Certificate generateCertificate(IssuerData issuerData, SubjectData subjectData) {
		 try {
			 
			 //posto klasa za generisanje sertifiakta ne moze da primi direktno privatni kljuc
			 //pravi se builder za objekat koji ce sadrzati privatni kljuc i koji 
			 //ce se koristitit za potpisivanje sertifikata
			 //parametar je koji algoritam se koristi za potpisivanje sertifiakta
			 JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
			 //koji provider se koristi
			 builder = builder.setProvider("BC");
			 
			 //objekat koji ce sadrzati privatni kljuc i koji ce se koristiti za potpisivanje sertifikata
			 ContentSigner contentSigner = builder.build(issuerData.getPrivateKey());
			 
			 //postavljaju se podaci za generisanje sertifiakta
			 X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerData.getX500name(),
					 															new BigInteger(subjectData.getSerialNumber()),
					 															subjectData.getStartDate(),
					 															subjectData.getEndDate(),
					 															subjectData.getX500name(),
					 															subjectData.getPublicKey());
			 //generise se sertifikat
			 X509CertificateHolder certHolder = certGen.build(contentSigner);
			 
			 //certGen generise sertifikat kao objekat klase X509CertificateHolder
			 //sad je potrebno certHolder konvertovati u sertifikat
			 //za to se koristi certConverter
			 JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
			 certConverter = certConverter.setProvider("BC");
			 
			 //konvertuje objekat u sertifikat i vraca ga
			 return certConverter.getCertificate(certHolder);
			 
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
	
	public KeyPair generateKeyPair() {
		try {
			//generator para kljuceva
			KeyPairGenerator   keyGen = KeyPairGenerator.getInstance("RSA");
			//inicijalizacija generatora, 1024 bitni kljuc
			keyGen.initialize(4096);	//Done promeniti na 4096 sa 1024
			
			//generise par kljuceva
			KeyPair pair = keyGen.generateKeyPair();
			
			return pair;
			
        } catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void saveCertificateToFile(X509Certificate cert, String subjectName) {
		String certificatePath = "./certificates/" + subjectName + ".cer";
		File certFile = new File(certificatePath);
		
		try {
			FileOutputStream fos = new FileOutputStream(certFile);
			fos.write(cert.getEncoded());
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
	}
}
