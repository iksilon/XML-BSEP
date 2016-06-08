package controllers;

import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;

import org.w3c.dom.Document;

import play.libs.XML;
import play.mvc.Controller;
import play.mvc.results.Ok;
import play.mvc.results.Result;
import utils.SecurityUtils;
import utils.XMLUtils;
public class Acts extends Controller {
	
	public static Result newAct() {
		return new Ok();
	}
	
	
	public static void submitXML() {
		// TODO: XML will be sent from the editor.
		org.apache.xml.security.Init.init();
		
		System.out.println("Beginning signature demo:\n\n");
		String workingDir = System.getProperty("user.dir");
		workingDir = Paths.get(workingDir, "public").toString();
		
		System.out.println("Files located at: " + workingDir);
		
		String sorcPath = Paths.get(workingDir, "placeholder1.xml").toString();
		String destPath = Paths.get(workingDir, "signature.xml").toString();
		
		Document xmlDoc = XMLUtils.loadDocument(sorcPath);
		
		System.out.println("Loaded placehoder1.xml");
		
		// TODO: User will be extracted.
		String kp = "odbornik1";
		KeyStore ks = SecurityUtils.getKeyStore("odbornik1.jks", kp.toCharArray());
		
		System.out.println("Loaded default user odbornik1");
		
		try {
			PrivateKey pk = (PrivateKey) ks.getKey(kp, kp.toCharArray());
			Certificate cert = ks.getCertificate(kp);
			
			Document signedDoc = SecurityUtils.signDocument(xmlDoc, pk, cert);
			
			System.out.println("Document signed.");
			
			//TODO: XML will be inserted into the database.
			XMLUtils.saveDocument(signedDoc, destPath);
			
			System.out.println("Document saved as signature.xml");
			
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
	}
}
