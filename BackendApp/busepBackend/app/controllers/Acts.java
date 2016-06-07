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
		String workingDir = System.getProperty("user.dir");
		workingDir = Paths.get(workingDir, "public").toString();
		
		String sorcPath = Paths.get(workingDir, "placeholder.xml").toString();
		String destPath = Paths.get(workingDir, "signature.xml").toString();
		
		Document xmlDoc = XMLUtils.loadDocument(sorcPath);
		
		// TODO: User will be extracted.
		String kp = "odbornik1";
		KeyStore ks = SecurityUtils.getKeyStore("odbornik1.jsk", kp.toCharArray());
		
		try {
			PrivateKey pk = (PrivateKey) ks.getKey(kp, kp.toCharArray());
			Certificate cert = ks.getCertificate(kp);
			
			Document signedDoc = SecurityUtils.signDocument(xmlDoc, pk, cert);
			
			//TODO: XML will be inserted into the database.
			XMLUtils.saveDocument(signedDoc, destPath);
			
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
	}
}
